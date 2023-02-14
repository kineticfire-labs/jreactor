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


import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.SignalEventHandlerInternal;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.EventGenerationState;

import com.kineticfire.patterns.reactor.DuplicateRegistrationException;
import com.kineticfire.patterns.reactor.InvalidInterestOpsException;
import com.kineticfire.patterns.reactor.InvalidResourceException;


/**
 * This class provides for the creation, generation, and management of signal
 * events.
 * <p>
 * A JVM signal event is registered by providing its name (as a string) and
 * assigning a handler with interest operations. When a JVM signal fires, the
 * SignalSelector sends an event to the Selector for dispatch. If the JVM signal
 * thread causes the JVM terminate when the internal handler exits, the
 * SignalSelector causes the JVM signal thread to block. The thread will remain
 * blocked until the handle/handler for the JVM is deregistered or the
 * SignalSelector is closed.
 * <p>
 * Currently supported signals are: "shutdownhook".
 * <p>
 * This class is thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */


public class SignalSelector implements SpecificSelector {

    //*********************************************
    //~ Instance/static variables
    private CompositeSelector selector;
    private Map<Handle, String> registered;                       // handle --> signal name
    private Map<Handle, EventGenerationState> handleState;        // handle --> event state
    private Map<Handle, SignalEventHandlerInternal> keepalive;    // handle --> SignalEventHandlerInternal  ;  used to track internal handlers that keep the JVM signal thread from exiting immediately
    private boolean done;


       //*********************************************
       //~ Constructors

    /**
     * Constructs an empty SignalSelector and no reference to the Selector.
     */
    public SignalSelector( ) {
        selector = null;
        registered = new HashMap<Handle, String>( );
        handleState = new HashMap<Handle, EventGenerationState>( );
        keepalive = new HashMap<Handle, SignalEventHandlerInternal>( );
        done = false;       
    }
    
    
    /**
     * Constructs an empty SignalSelector with a reference to the Selector.
     * 
     * @param selector
     *            a reference to the controlling Selector
     */
    public SignalSelector( CompositeSelector selector ) {
        this.selector = selector;
        registered = new HashMap<Handle, String>( );
        handleState = new HashMap<Handle, EventGenerationState>( );
        keepalive = new HashMap<Handle, SignalEventHandlerInternal>( );
        done = false;       
    }




    //*********************************************
    //~ METHODS

    /**
     * Registers a signal event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event handler and interest operations.
     * <p>
     * PRECONDITION: signalName and handler not null; interestOps is
     * EventMask.{NOOP | SIGNAL}
     * <p>
     * SUPPORTED SIGNALS:
     * <p>
     * (1) shutdownhook -- Signal fired when the JVM terminates due to (a) the
     * program exiting normally such as the last non-daemon thread exits or when
     * Runtime.exit() is invoked or (b) a response from a user interrupt such a
     * CTRL-C or a system-wide event like a user logoff or system shutdown to
     * inlucde SIGUP (Unix only), SIGINT, or SIGTERM. See Chris White,
     * "Revelations on Java signal handling and termination," IBM Technical
     * Report, 1 Jan. 2002.
     * <p>
     * SIGNAL BEHAVIOR:
     * <p>
     * (1) shutdownhook -- The firing of a shutdownhook event triggers the
     * execution of the associated event handler exactly once. No event handler
     * is run if shutdownhook lacks an associated event handler. Once the
     * shutdownhook event fires, the JVM will not exit until the (a) the
     * JReactor is shudtown, (b) the handler assigned to shutdownhook is
     * deregistered, or (c) the handle assigned to shutdownhook is deregistered.
     * A program should call the Reactor's shutdown() method in order to allow
     * the program and the JVM to exit. This behavior allows the
     * Reactor-controlled program time to gracefully shutdown after a
     * shutdownhook event is detected. A well-behaved program should exit as
     * soon as possible in response to a shutdownhook event.
     * 
     * @param signalName
     *            name of the signal to register
     * @param handler
     *            event handler to associate with the signal
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws InvalidResourceException
     *             if the event source is not valid or supported
     * @throws DuplicateRegistrationException
     *             if the signal is already registered
     */
    public synchronized Handle register( String signalName, Handler handler, int interestOps ) {
        Handle handle = null;        
        String validSignalName = validate( signalName );
            
        if ( validSignalName.compareTo( "invalid" ) != 0 ) {
            if ( !registered.containsValue( validSignalName ) ) {
                handle = new Handle( );
                
                registered.put( handle, validSignalName );
                handleState.put( handle, EventGenerationState.NONE );

                // won't check if "keep alive" needed because only supported signal is "shutdown" hook which must have keep alive 
                SignalEventHandlerInternal internalHandler = new SignalEventHandlerInternal( this, handle );
                keepalive.put( handle, internalHandler );                    
                installHandler( validSignalName, internalHandler );
                
                selector.processRegister( handle, handler, this, interestOps );
                
            }  else {
                throw( new DuplicateRegistrationException( ) );
            }
        } else {
            throw( new InvalidResourceException( ) );
        }
        
        return( handle );
    }

    

    /**
     * Determines if the signalName event source is currently registered.
     * <p>
     * PRECONDITION: signalName is not null
     * 
     * @param signalName
     *            event source to query registration status
     * @return true if the signalName is registered and false otherwise
     * @throws InvalidEventSourceException
     *             if the signal name is not valid or the signal is not
     *             supported
     */
    public synchronized boolean isRegistered( String signalName ) {
        boolean isRegistered = false;
        
        String validSignalName = validate( signalName );
        if ( validSignalName.compareTo( "invalid" ) != 0 ) {
            if ( registered.containsValue( validSignalName ) ) {
                isRegistered = true;
            }
        }  else {
            throw( new InvalidResourceException( ) );
        }
            
        return( isRegistered );
    }

    
    /**
     * Determines if the signalHandle references a currently registered signal
     * event source. Returns true if the signalHandle is currently registered
     * and is a handle for a signal event source and false otherwise.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference for which to request registration status
     * @return true if the signalHandle is currently registered and is a handle
     *         for a signal event source and false otherwise
     */
    public synchronized boolean isRegistered( Handle handle ) {
        return( registered.containsKey( handle ) );
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps. 
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not EventMask.{NOOP,SIGNAL}
     */
    public synchronized void interestOps( Handle handle, int interestOps ) {
        if ( EventMask.xIsSignal( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            if ( handleState.get( handle ) == EventGenerationState.HOLDING ) {
                if ( EventMask.xIsNoop( selector.interestOps( handle ) ) && EventMask.xIsSignal( interestOps ) ) {
                    triggerEvent( handle );
                }
            }
            selector.processInterestOps( handle, interestOps );
        } else {
            throw ( new InvalidInterestOpsException( interestOps ) );
        }
    }
    
    

    /**
     * Deregisters the signal event source referenced by handle. The signal will
     * no longer generate ready events
     * <p>
     * Deregistering signals like "shutdownhook" cause the JVM to close
     * immediately.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle to deregister
     */
    public synchronized void deregister( Handle handle ) {
         if ( registered.containsKey( handle ) ) {

             String validSignalName = registered.remove( handle );
             
             handleState.remove( handle );

              SignalEventHandlerInternal internalHandler = keepalive.remove( handle );
              
             /*
             * Calling the internalHandler's cancel() method on
             * signal 'shutdownhook' will immediately cause the JVM
             * to exit
             */
             internalHandler.cancel( );
             uninstallHandler( validSignalName, internalHandler );
         }
    }


        /**
         * Provides notification that a handle with a ready event was not
         * dispatched.
         * <p>
         * This method takes no action and is here for compatibility with the
         * com.kineticfire.patterns.reactor.SpecificSelector interface.
         *
         * @param handle
         *            handle, referencing a registered signal event source, with a ready event to
         *            be checked-in
         * @param event
         *            the event generated by the event source
         */

    public synchronized void checkin( Handle handle, Event event ) {
            if ( registered.containsKey( handle ) ) {
                handleState.put( handle, EventGenerationState.HOLDING );
            }
    }    
    
    
    /**
     * Resumes readiness selection on the signal event source referenced by
     * handle. That is, the signal referenced by handle will now fire ready
     * events that will be serviced by the assigned event handler.
     * <p>
     * PRECONDITION: handle is not null; handle is a signalHandle
     * 
     * @param handle
     *            handle that references signal event source for which to
     *            re-enable to fire ready events
     */
    public synchronized void resumeSelection( Handle handle ) {
        
        if ( handleState.get( handle ) == EventGenerationState.FIRED ) {
            handleState.put( handle, EventGenerationState.DONE );
        } else if ( ( handleState.get( handle ) == EventGenerationState.HOLDING ) && EventMask.xIsSignal( selector.interestOps( handle ) ) ) {
            triggerEvent( handle );
        }
    } 
    

    /**
     * Generates a signal-ready event, EventMask.SIGNAL, and submits the event
     * to the Selector for dispatch.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle that generated the ready event
     */
    protected synchronized boolean signalFired( Handle handle ) {
        boolean prepared = false;
        
        if ( registered.containsKey( handle ) ) {
            prepared = true;
            triggerEvent( handle );    
        }
        
        return( prepared );
    }
    
    /**
     * Validates the signal name and returns a known String name for the signal.
     * If valid, the returned string name is all lowercase. If invalid, then the
     * string "invalid" is returned.
     * <p>
     * PRECONDITION: signalName is not null
     * 
     * @param signalName
     *            the name of the signal to validate
     * @return the validated signal name in all lowercase or "invalid" if the
     *         supplied signalName is not valid
     */
    private String validate( String signalName ) {
        String validSignalName;
        String tempSignalName = signalName.toLowerCase( );
        
        if ( tempSignalName.compareTo( "shutdownhook" ) == 0 ) {
            validSignalName = "shutdownhook";
        } else {
            validSignalName = "invalid";
        }
        
        return( validSignalName );
    }    
    

    /**
     * Registers an InternalSignalEventHandler to service a JVM signal.
     * <p>
     * Currently supported signals are: "shutdownhook".
     * <p>
     * PRECONDITION: validSignalName and internalHandler are not null;
     * validSignalName refers to a valid signal name
     * 
     * @param validSignalName
     *            validated signal name to register with internal handler
     * @param internalHandler
     *            internal handler to register with signal name
     */
    private void installHandler( String validSignalName, SignalEventHandlerInternal internalHandler ) {
        if ( validSignalName.compareTo( "shutdownhook" ) == 0 ) {
            Runtime.getRuntime( ).addShutdownHook( internalHandler );
        }
    }
    
    /**
     * Deregisters an InternalSignalEventHandler from servicing a JVM signal.
     * <p>
     * Currently supported signals are: "shutdownhook".
     * <p>
     * PRECONDITION: validSignalName and internalHandler are not null
     * 
     * @param validSignalName
     *            validated signal name to deregister from internal handler
     * @param internalHandler
     *            internal handler to deregister from signal name
     */
    private void uninstallHandler( String validSignalName, SignalEventHandlerInternal internalHandler ) {

        if ( validSignalName.compareTo( "shutdownhook" ) == 0 ) {

            try {
                Runtime.getRuntime( ).removeShutdownHook( internalHandler );
            } catch ( IllegalStateException e ) {
                // thrown if the VM is already in the process of shutting down
            } catch ( SecurityException e ) {
                // thrown if a security manager is present and it denies access
            }
        }

    }

    /**
     * Records a signal event source referenced by handle as having fired an
     * event and sends the event to the controlling Selector for processing.
     * <p>
     * This method requires external sychronization.
     * 
     * @param handle
     *            the handle refernencing the event source that generated a
     *            ready event
     */
    private void triggerEvent( Handle handle ) {
        handleState.put( handle, EventGenerationState.FIRED );
        selector.addReadyEvent( new Event( handle, EventMask.SIGNAL ) );
    }
    
    
    /**
     * Permanently terminates readiness selection and causes all internal signal
     * threads currently blocked to exit. Calling this method multiple times has
     * no affect.
     * <p>
     * Closing blocked JVM signal threads on signals like "shutdownhook" will
     * cause the JVM to exit immediately.
     */
    public synchronized void shutdown( ) {
        if ( !done ) {
            Set<Handle> handles = keepalive.keySet( );
            for ( Iterator<Handle> it = handles.iterator( ); it.hasNext( ); ) {
                Handle handle = it.next( );

                SignalEventHandlerInternal internalHandler = keepalive.get( handle );
                 internalHandler.cancel( );
                 String signalName = registered.get( handle ); 
                 uninstallHandler( signalName, internalHandler );                 
            }
            registered.clear( );
            keepalive.clear( );
            handleState.clear( );
            
            done = true;
        }
    }
    
    
    /**
     * Prepares the SignalSelector for garbage selection by gracefully
     * terminating the internal readiness selection thread and closing all
     * internal JVM signal threads currently blocked. Calling this method
     * multiple times has no affect.
     * <p>
     * Closing blocked JVM signal threads on signals like "shutdownhook" will
     * cause the JVM to exit immediately.
     * 
     */
    public void finalize( ) {
        if ( !done ) {
            shutdown( );
        }
    }
    
    
}
