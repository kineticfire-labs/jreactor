package com.kineticfire.patterns.reactor;


/**
 * Copyright 2010 KineticFire.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventMask;

import com.kineticfire.patterns.reactor.DuplicateRegistrationException;
import com.kineticfire.patterns.reactor.InvalidInterestOpsException;
import com.kineticfire.patterns.reactor.SelectorFailureException;
import com.kineticfire.patterns.reactor.IllegalResourceStateException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.IllegalBlockingModeException;



/**
 * This class implements a ChannelSelector. The selector manages
 * SelectableChannel resources. The selector controls registration,
 * deregistration, setting interest operations, and coordinating with the
 * Selector to fire ready events for SelectableChannels.
 * <p>
 * A SelectabeChannel can be closed outside of the JReactor architecture,
 * resulting in an IllegalResourceStateException. The channel can be closed via
 * the SelectableChannel.close() method or canceling the associated SelectionKey
 * via the SelectionKey.cancel() method. The channel may also be improperly
 * closed by the remote host or a network error may result, thus closing the
 * channel. An IllegalResourceStateException indicates that an unrecoverable
 * error occured on the channel and the channel is no longer usable. The channel
 * should be deregistered; deregister(handler/handle) will remove the channel
 * and will not produce an IllegalResourceStateException.
 * <p>
 * The java.nio.channels.Selector may fail, resulting in a critical
 * SelectorFailureException to be reported. It is not possible to recover from a
 * critical error.
 * <p>
 * This class is thread-safe.
 * <p>
 * USAGE NOTES on the SelectableChannel:
 * <p>
 * (1) It is not possible to determine that a channel has been closed without
 * performing an operation on a channel. For a channel closed normally, a call
 * to read() will return -1. A channel closed abnormally will return an
 * IOException.
 * <p>
 * (2) There are two approaches to writing data to SelectableChannels: the first
 * is to attempt to WRITE data and then, if that fails, to set the interest
 * operations to WRITE and wait for the selector's notification that the channel
 * is ready for writing. The second method is to always set the channel's
 * interest operations to WRITE, wait for the selector to detect the write event
 * on the channel, and then write data to the channel. The JReactor is written
 * to facilitate the first approach.
 * <p>
 * (3) There are are three approaches to reading the 'correct' amount of data a
 * network connection. (a) Send the number of bytes. (b) Use a delimiter which
 * cannot appear in your data. (c) Close the remote connection.
 * <p>
 * (4) Developer code may use SSL to encrypt data sent over a SelectableChannel
 * by implementing javax.net.ssl.SSLEngine. Encryption is done outside of the
 * JReactor and is transparent to the JReactor.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */


public class ChannelSelector implements SpecificSelector {

    //*********************************************
    //~ Instance/static variables
    
    /*
     * Used to add events to and coordinate with the Selector
     */
    private CompositeSelector selector;
    
    /*
     * Provides non-blocking selection of SelectableChannels 
     */
    private Selector nioSelector;
    
    /*
     * Tracks registered channels by providing a handle->channel map
     */
    private Map<Handle, SelectableChannel> handleChannelMap;
    
    /*
     * Tracks registered channels by providing a channel->handle map
     */
    private Map<SelectableChannel, Handle> channelHandleMap;
    
    /*
     * Record channels that have fired ready events, but have not yet completed
     * either through checkin() or resumeSelection(). Channels that have fired
     * ready events yet have not completed have their interest operations
     * temporarily set to zero so that they will not fire additional events.
     */
    private Set<Handle> fired;
    
    /*
     * Internal thread that will perform selection on non-blocking
     * SelectableChannel, using the java.nio.channels.Selector in the
     * doSelection() method
     */
    private Thread selectThread;
    
    /*
     * Provides a synchronization point to coordinate the internal
     * java.nio.channels.Selector thread with external threads (e.g. Reactor
     * threads)
     * <p>
     * All operations in the ChannelSelector must synchronize on this object.
     */
    private static Byte guard;
    
    /*
     * Used to in the closing process of ChannelSelector, especially in
     * coordination of shutting down the java.nio.channels.Selector and internal
     * thread. Ensures the closing process is executed at most once, gaurding
     * against multiple calls resulting from close() and/or finalize().
     */ 
    private volatile boolean done;
    
    
       //*********************************************
       //~ Constructors


    /**
     * Constructs a ChannelSelector with a reference to the Selector. Starts a
     * dedicated thread to perform readiness selection on registered channels.
     * <p>
     * Reports a critical error to the Selector if a java.nio.channels.Selector
     * exception is encountered.
     * 
     * @param selector
     *            reference to the controlling selector
     */
       public ChannelSelector( CompositeSelector selector ) {
        this.selector = selector;
        guard = Byte.parseByte( "0" );
        done = false;
        
        handleChannelMap = new HashMap<Handle, SelectableChannel>( );        
        channelHandleMap = new HashMap<SelectableChannel, Handle>( );
        fired = new HashSet<Handle>( );
        
        synchronized( guard ) {
            if ( !done ) {

                try {
                    nioSelector = Selector.open( );
                } catch ( IOException e ) { 
                    done = true;
                    this.selector.reportCriticalError( new SelectorFailureException( ) );
                }

                if ( !done ) {
                    selectThread = new Thread( new Runnable( ) { public void run( ) { doSelection( ); } } );
                    selectThread.start( );
                }
            }
        }
       }

    

    //*********************************************
    //~ Methods



    
    /**
     * Performs readiness selection on SelectableChannels using the
     * java.nio.channels.Selector. Opens a new selector. SelectableChannels that
     * are found ready, based on their interest operation set, generate a ready
     * event which is added to the controlling selector; the channel
     * subsequently have their interest operations set to zero to suppress
     * further ready events. The channel's interest operations are restored when
     * the channel is serviced, indicated by checkin() or resumeSelection().
     * <p>
     * This method makes a blocking call to Selector.select(). Other threads
     * wishing to modify the Selector should gain a lock on the 'guard' object
     * and call the Selector's wakup() method.
     * <p>
     * Readiness selection continues until the close() method is called; the
     * selector's wakeup() method should be called to nudge the selector. When
     * the selection process is stopped, the selection thread performing this
     * method will call the shutdown() command.
     * <p>
     * Reports a critical error to the Selector if a java.nio.channels.Selector
     * exception is encountered.
     */
    private void doSelection( ) {

        /*
         * Notes:
         * <p>
         * (1) Synchronization to the guard object provides coordination between
         * the internal selection process and the Selector and external threads.
         * Open literature indicated that the Selector behaved erratically when
         * it was modified during a selection operation. The guard object serves
         * as a handshaking mechanism between the internal Selector thread and
         * external threads. A thread wanting to modify information in the
         * Selector should gain access to the guard object and call the
         * Selector's wakeup() method.
         * <p>
         * (2) The close() method will cause this doSelection() method to exit
         * the main loop and call shutdown(), thus terminating the internal
         * Selector thread and permanently closing the Selector. Calling wakup()
         * on the Selector will either (1) cause a thread currently blocked in a
         * call to select() to return immediately or (2) if no selection
         * operation is currently underway, the next invocation of select() will
         * return immediately.
         */
        
        Set<SelectionKey> selectedKeys;
        
        
        while ( !done ) {            
            
            synchronized( guard ) { }  // empty
            
            try {
                nioSelector.select( );   // blocking call to the selector
            } catch ( IOException e ) { 
                selector.reportCriticalError( new SelectorFailureException( ) );
            } catch ( ClosedSelectorException e ) { 
                selector.reportCriticalError( new SelectorFailureException( ) );
            }

            
            synchronized( guard ) {
                
                if ( !done ) {
                    try {
                        selectedKeys = nioSelector.selectedKeys( );
                    } catch ( ClosedSelectorException e ) {
                        done = true;
                        selectedKeys = new HashSet<SelectionKey>( ); // create an empty set
                        selector.reportCriticalError( new SelectorFailureException( ) );
                    }

                    for ( SelectionKey key : selectedKeys ) {
                        if ( key.isValid( ) ) {
                            SelectableChannel channel = key.channel( );
                            Handle handle = channelHandleMap.get( channel );
                                                
                            try {
                                int readyOps = key.readyOps( );
                                // set keys interestOps to 0 to suppress further ready events until current event completes
                                key.interestOps( 0 );                                    
                                // add this key to fired set to note that its interestOps have been temporarily set to 0
                                fired.add( handle );                                    
                                // add event to Selector
                                selector.addReadyEvent( new Event( handle, readyOps ) );
                            } catch ( CancelledKeyException e ) {
                                // ignore
                            }                                            
                        }
                    }

                    // remove key from selectedKey set
                    selectedKeys.clear( );
                    
                }
            }

        }
        
        close( );

    }


    /**
     * Registers a channel event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event source and handler.
     * <p>
     * Configures the channel for non-blocking mode.
     * <p>
     * PRECONDITION: channel and handler not null; interestOps is
     * EventMask.{NOOP | CREAD | CWRITE | ACCEPT | CONNECT}
     * 
     * @param channel
     *            event source to register
     * @param handler
     *            event handler to associate with the channel
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws DuplicateRegistrationException
     *             if the channel is already registered
     * @throws IllegalResourceStateException
     *             if an error occurred while registering the channel to the
     *             java.nio.channels.Selector
     */
    public Handle register( SelectableChannel channel, Handler handler, int interestOps ) {

        synchronized( guard ) {
            
            Handle handle = null;
            
            if ( !handleChannelMap.containsValue( channel ) ) {
                nioSelectorRegister( channel, interestOps );
                handle = new Handle( );
                channelHandleMap.put( channel, handle );
                handleChannelMap.put( handle, channel );
                selector.processRegister( handle, handler, this, interestOps );
            } else {
                throw( new DuplicateRegistrationException( ) );
            }
            
            return( handle );
        }
    }
    
    
    /**
     * Completes the registration of a channel by registering the channel with
     * the java.nio.channels.Selector. The interest operations of the channel
     * within the selector are set to interestOps.
     * <p>
     * The channel is configured for non-blocking mode.
     * <p>
     * Requires external synchronization on the 'guard' object.
     * <p>
     * PRECONDITION: channel is not null, interestOps is valid for the channel
     * 
     * @param channel
     *            the channel to register and set interest operations; or, if
     *            already registered, the channel to change interest operations
     * @param interestOps
     *            the interest operations to set for the channel
     */
    private void nioSelectorRegister( SelectableChannel channel, int interestOps ) {
    
        try {
            channel.configureBlocking( false );
        } catch ( ClosedChannelException e ) { 
            throw( new IllegalResourceStateException( ) );
        } catch ( IllegalBlockingModeException e ) {
            throw( new IllegalResourceStateException( ) );
        } catch ( IOException e ) {
            throw( new IllegalResourceStateException( ) );
        }
        
        nioSelector.wakeup( );
        
        try {
            channel.register( nioSelector, interestOps );
        } catch ( ClosedChannelException e ) { 
            throw( new IllegalResourceStateException( ) );
        } catch ( ClosedSelectorException e ) { 
            throw( new IllegalResourceStateException( ) );
        } catch ( IllegalSelectorException e ) {
            throw( new IllegalResourceStateException( ) );
        } catch ( CancelledKeyException e ) {
            throw( new IllegalResourceStateException( ) );
        } catch ( IllegalBlockingModeException e ) {
            throw( new IllegalResourceStateException( ) );
        } catch ( NullPointerException e ) {
            /*
             * Silelently discard this exception. Occurs when the
             * ChannelSelector is shutting down or has been shutdown yet a
             * request to register a new channel or change interest ops on a
             * registered channel is received. Since the ChannelSelector is
             * closing or is now closed, subseqent changes to registration are
             * irrelevant and the exception may be ignored.
             */
        }
    }
    
    
    
    
    /**
     * Sets the interest operations of handle to the given interestOps.
     * <p>
     * Reports a critical error to the Selector if a java.nio.channels.Selector
     * exception is encountered.
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not
     *             EventMask.{NOOP,CREAD,CWRITE,ACCEPT,CONNECT}
     */
    public void interestOps( Handle handle, int interestOps ) {
        
        synchronized( guard ) {
            if ( EventMask.xIsChannel( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
                nioSelector.wakeup( );
                 SelectableChannel channel = handleChannelMap.get( handle );
                SelectionKey key = channel.keyFor( nioSelector );
                if ( key != null ) {
                    if ( key.isValid( ) ) {
                        
                        boolean error = false;
                        
                        if ( !fired.contains( handle ) ) {
                            try {
                                key.interestOps( interestOps );
                            } catch ( CancelledKeyException e ) { 
                                error = true;
                            }
                        }
                        
                        if ( !error ) {
                            selector.processInterestOps( handle, interestOps );
                        }
                        
                    }
                }
            } else {
                throw( new InvalidInterestOpsException( interestOps ) );
            }
        }
    }
    
            
    /**
     * Determines the registration status of the channel. Returns true if the
     * channel is currently registered and false otherwise.
     * <p>
     * PRECONDITION: channel is not null
     * 
     * @param channel
     *            event source for which to request registration status
     * @return true if the channel is currently registered and false otherwise
     */
    public boolean isRegistered( SelectableChannel channel ) {
        synchronized( guard ) {
            return( channelHandleMap.containsKey( channel ) );
        }
    }

    /**
     * Determines the registration status of the channelHandle. Returns true if
     * the channelHandle is currently registered and is a handle for a channel
     * event source and false otherwise.
     * <p>
     * PRECONDITION: channel is not null
     * 
     * @param channelHandle
     *            reference for which to request registration status
     * @return true if the channelHandle is currently registered and is a handle
     *         for a channel event source and false otherwise
     */
    public boolean isRegistered( Handle channelHandle ) { 
        synchronized( guard ) {
            return( channelHandleMap.containsValue( channelHandle ) );
        }
    }

    /**
     * Deregisters the channel event source referenced by handle. The channel
     * will no longer generate ready events.
     * <p>
     * When this method completes, the handle will be deregistered from the
     * SpecificSelector.
     * <p>
     * This method will proceed even if the SelectableChannel associated to
     * handle is 'closed' and the SelectionKey associated to handle is
     * 'invalid'.
     * <p>
     * PRECONDITION: handle is not null, handle references a registered channel
     * 
     * @param handle
     *            the handle to deregister
     */
    public void deregister( Handle handle ) {
        

        synchronized( guard ) {
             nioSelector.wakeup( );
             SelectableChannel channel = handleChannelMap.get( handle );
             SelectionKey key = channel.keyFor( nioSelector );
             if ( key != null ) {
                 
                // not checking if key is valid
                
                key.cancel( );
                
                channelHandleMap.remove( channel );
                handleChannelMap.remove( handle );
                fired.remove( handle );
                
                selector.processDeregister( handle );
             }
        }

    }
    
            
    /**
     * Provides notification that a handle with a ready event was not
     * dispatched.
     * <p>
     * Should only be called when an event fails to dispatch due to interestOps.
     * <p>
     * This method performs resumeSelection on the handle. When this method
     * completes, the current interestOps in the Selector will be transfered to
     * the channel's selector and the handle will be removed from the 'fired'
     * group.
     * <p>
     * PRECONDITION: handle is not null, handle references registered channel
     * 
     * @param handle
     *            handle, referencing a registered channel, to be checked-in
     * @param event
     *            the event generated by the event source
     */
    public void checkin( Handle handle, Event event ) {
        resumeSelection( handle );
    }
        

    /**
     * Resumes readiness selection on the channel event source referenced by
     * handle. That is, the channel referenced by handle will now fire ready
     * events that will be serviced by the assigned event handler.
     * <p>
     * PRECONDITION: handle is not null, handle references registered channel
     * 
     * @param handle
     *            handle that references channel event source for which to
     *            re-enable to fire ready events
     */
    public void resumeSelection( Handle handle ) {

        synchronized( guard ) {            
            nioSelector.wakeup( );
            SelectableChannel channel = handleChannelMap.get( handle );
            SelectionKey key = channel.keyFor( nioSelector );
            if ( key != null ) {
                if ( key.isValid( ) ) {                        
                    boolean error = false;
                    
                    int interestOps = selector.interestOps( handle );
                    
                    try {
                        key.interestOps( interestOps );
                    } catch ( CancelledKeyException e ) { 
                        error = true;
                    }
                    
                    if ( !error ) {                        
                        fired.remove( handle );
                    }
                    
                }
            }
        }
    }

    
    /**
     * Permanently terminates readiness selection and causes the internal thread
     * performing readiness selection, via the doSelection() loop to gracefully
     * exit. Closes the java.nio.channels.Selector object. Calling this method
     * multiple times has no affect.
     * 
     */
    public void shutdown( ) {        
        synchronized( guard ) {
            if ( !done ) {
                done = true;
                if ( nioSelector != null ) {
                    nioSelector.wakeup();
                }
            }
        }
    }

    
    /**
     * Closes the java.nio.channels.Selector object. Called by the internal
     * thread performing readiness selection in the doSelection() loop when it
     * gracefully exits.
     * <p>
     * Reports a critical error to the Selector if a java.nio.channels.Selector
     * exception is encountered.
     */
    private void close( ) {
        
        synchronized( guard ) {
                        
            try {
                nioSelector.close( );
            } catch ( IOException e ) { 
                selector.reportCriticalError( new SelectorFailureException( ) );
            }
            
        }
    }
    
    
    /**
     * Prepares the ChannelSelector for garbage selection by gracefully
     * terminating the internal readiness selection thread and closing the
     * java.nio.channels.Selector object.
     */
    public void finalize( ) {
        shutdown( );
    }


}
