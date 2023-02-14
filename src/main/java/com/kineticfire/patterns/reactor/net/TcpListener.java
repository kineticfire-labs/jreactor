package com.kineticfire.patterns.reactor.net;

/**
 * Copyright 2017 KineticFire.
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



import java.util.Collection;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.PriorityLinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.MessageBlock;

import com.kineticfire.patterns.reactor.messaging.MessageComponent;




/**
  * Implements a TcpListener that listens and accepts TCP connections, using the Reactor pattern.
  * <p>
  * At construction, the listener must be provided with a reference to the controlling Reactor, a reference to the component accepting client connections, and a port on which to listen.  Note that the listener is not started until calling the 'listen()' method or until a 'request_listen' request message is received.  The buffer capacity defaults to 4,096 bytes (a page size), and may be changed by calling the appropriate constructor.  The TcpListener may also be assigned a name and an id via a constructor and also changed with the requests messages 'request_set_name' and 'request_set_id'.  Note that the port cannot be changed once set with the constructor.  The TcpListener is registered with the Reactor and processes incoming request messages but is not listening for client connections until explicitly triggered to do so.
  * <p>
  * The TcpListener is triggered to listen for client connections by calling the 'listen()' method or by sending a 'request_listen' request message.  Once listening for client connections and upon receiving a client connection request, the TcpListener accepts the client channel, wraps the channel in a com.kineticfire.patterns.reactor.net.TcpConnection object, and sends the TcpConnection object in a 'event_accept' event message to the component set to receive new client connections.
  * <p>
  * The TcpListener emits a variety of event messages (including error notifications) as needed and also sends response messages to request messages as necessary.  These messages are detailed later.
  * <p>
  * The TcpListener follows the Variant-based data MessageBlock scheme, as described in com.kineticfire.patterns.reactor.MessageBlock.  Events and responses emitted by the TcpListener include a variant message with key 'com.kineticfire.patterns.reactor.typeDomain' and value 'com.kineticfire.patterns.reactor.net', which defines the namespace in which the message type is defined.  The port is conveyed with events and responses as a mechanism for controlling components to differentiate message from multiple TcpListeners; the name and id are populated if set and not null.
  * <p>
  * This class supports prioritized messages.  Creating a MessageBlock and setting its priority lower than others will cause that message to be processed ahead of others.  It may be beneficial to prioritize messages ahead of others, such as shutdown request messages.
  * <p>
  * 
  * @author Kris Hall
  * @version 05-05-2017
  */




public class TcpListener implements Handler, MessageComponent {

    private Reactor reactor;

    private MessageQueue eventQueue;    
    private Handle eventQueueHandle;

    private ServerSocketChannel channel;
    private Handle channelHandle;

    private Handle errorHandle;

    private MessageComponent acceptor;

    private int bufferCapacity;

    private enum TcpListenerState { READY, LISTENING, SHUTDOWN; }
    private TcpListenerState state;


    private int port;
    private String name;
    private String id;



    private static String sourceType = "com.kineticfire.patterns.reactor.net.TcpListener"; // sets the 'sourceType' field for events and errors



    /**
      * Constructs a new TcpListener that is idle (not listening).
      * <p>
      * The reference to the controlling Reactor is set as 'reactor', the reference to the component accepting client client connections is set as 'acceptor', and the listening port is set to 'port'.  The buffer capacity for client connections defaults to 4096 bytes.  The TcpListener neither has a name nor ID set.
      *
      * @param reactor
      *    reference to the controlling Reactor
      * @param acceptor
      *    reference to the MessageComponent to receive newly connected clients
      * @param port
      *    port to listen on
      */
    public TcpListener( Reactor reactor, MessageComponent acceptor, int port ) {

        this.reactor = reactor;

        this.acceptor = acceptor;

        this.port = port;

        this.eventQueue = new PriorityLinkedBlockingMessageQueue( );
        eventQueueHandle = reactor.registerQueue( eventQueue, this, EventMask.QREAD );

        channel = null;
        channelHandle = null;

        errorHandle = reactor.registerError( this, EventMask.ERROR );

        bufferCapacity = 4096; // for client connection; 4096 is a page size

        name = null;
        id = null;

        state = TcpListenerState.READY;

        MessageBlock mb = createMessageBlock( "com.kineticfire.patterns.reactor.event_ready" );
        notifyTarget( mb );
    }


    /**
      * Constructs a new TcpListener that is idle (not listening).
      * <p>
      * The reference to the controlling Reactor is set as 'reactor', the reference to the component accepting client client connections is set as 'acceptor', the listening port is set to 'port', and the buffer capacity for client connections is set as 'bufferCapacity'.  The TcpListener neither has a name nor ID set.
      *
      * @param reactor
      *    reference to the controlling Reactor
      * @param acceptor
      *    reference to the MessageComponent to receive newly connected clients
      * @param port
      *    port to listen on
      * @param bufferCapacity
      *    buffer capacity for send and receive buffers for an accepted TcpConnection
      */
    public TcpListener( Reactor reactor, MessageComponent acceptor, int port, int bufferCapacity ) {
        this( reactor, acceptor, port );
        this.bufferCapacity = bufferCapacity;
    }


    /**
      * Constructs a new TcpListener that is idle (not listening).
      * <p>
      * The reference to the controlling Reactor is set as 'reactor', the reference to the component accepting client client connections is set as 'acceptor', the listening port is set to 'port, the name is set to 'name', and id set to 'id'.  The buffer capacity for client connections defaults to 4096 bytes.
      *
      * @param reactor
      *    reference to the controlling Reactor
      * @param acceptor
      *    reference to the MessageComponent to receive newly connected clients
      * @param port
      *    port for this TcpListener
      * @param name
      *    port to listen on
      * @param id
      *    id for this TcpListener
      */
    public TcpListener( Reactor reactor, MessageComponent acceptor, int port, String name, String id ) {
        this( reactor, acceptor, port );
        this.name = name;
        this.id = id;
    }



    /**
      * Constructs a new TcpListener that is idle (not listening).
      * <p>
      * The reference to the controlling Reactor is set as 'reactor', the reference to the component accepting client client connections is set as 'acceptor', the listening port is set to 'port', the name is set to 'name', id set to 'id', and buffer capacity for client connections is set to 'bufferCapacity'.
      *
      * @param reactor
      *    reference to the controlling Reactor
      * @param acceptor
      *    reference to the MessageComponent to receive newly connected clients
      * @param port
      *    port to listen on
      * @param name
      *    name for this TcpListener
      * @param id
      *    id for this TcpListener
      * @param bufferCapacity
      *    buffer capacity for send and receive buffers for an accepted TcpConnection
      */
    public TcpListener( Reactor reactor, MessageComponent acceptor, int port, String name, String id, int bufferCapacity ) {
        this( reactor, acceptor, port, bufferCapacity );
        this.name = name;
        this.id = id;
    }


    /**
      * Sends a message to this component.
      * <p>
      * The message is queued by the component.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the component has shutdown.
      *
      * @param mb
      *    message to process
      */
    public boolean messageComponent( MessageBlock mb ) {
        return( eventQueue.offer( mb ) );
    }


    /**
      * Adds all elements in the collection 'c' to the component.  Returns true if the items were successfully added or false otherwise.
      * 
      * @param c
      *    the collection to add
      * @return true if the collection were successfully added or false otherwise
      * @throws ClassCastException
      *    if the data is not a MessageBlock
      * @throws NullPointerException
      *    if the data is null
      * @throws ClassCastException
      *    if the class of the element prevents its addition into the component
      * @throws IllegalArgumentException
      *    if some property of this element prevents it from being added to the component
      */
    public boolean messageComponent( Collection<? extends MessageBlock> c ) {
        return( eventQueue.addAll( c ) );
    }


    /**
      * Returns the ratio of the number of elements in the component to the capacity of the component on the range of [0.0,1.0].
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of elements in the component to the capacity of the component
      */
    public double messageComponentPressure( ) {
        return( eventQueue.pressure( ) );
    }


    /*
      * Processes a ready event on the handle 'handle'.  To be called by the Reactor pattern only.
      * <p>
      * See methods channelHandleEvent(Handle, int), queueHandleEvent(Handle), and errorHandleEvent(Handle) for further information.
      *
      * @param handle
      *    a reference to the event source that generated the ready event
      * @param readyOps
      *    the operations for which the event source is ready
      * @param info
      *    additional information on the ready event
      */
    public void handleEvent( Handle handle, int readyOps, MessageBlock info ) {
        if ( handle == channelHandle ) {
            channelHandleEvent( handle, readyOps );
        } else if ( handle == eventQueueHandle ) {
            queueHandleEvent( handle );
        } else if ( handle == errorHandle ) {
            errorHandleEvent( handle );
        }
    }


    /*
      * Handles ready events on channel type handles.
      * <p>
      * If the 'readyOps' is EventMask.ACCEPT, then the method will attempt to accept the connection.  If successful, the accepted connection (as a SocketChannel) is passed in a 'com.kineticfire.patterns.reactor.net.event_accept' event to the acceptor.  If an error occurs, then the event 'com.kineticfire.patterns.reactor.net.event_error_accept' is emitted to the acceptor.
      *
      * @param handle
      *    a reference to the event source that generated the ready event
      * @param readyOps
      *    the operations for which the event source is ready
      */
    private void channelHandleEvent( Handle handle, int readyOps ) {

        if ( readyOps == EventMask.ACCEPT ) {

            SocketChannel clientChannel = null;

            try {
                clientChannel = channel.accept( );
            } catch ( IOException e ) {

                clientChannel = null;

                MessageBlock mb = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_accept" );

                notifyTarget( mb );
            }

            if ( clientChannel != null ) {
                TcpConnection client = new TcpConnection( reactor, clientChannel, bufferCapacity );

                MessageBlock mb = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_accept" );
                mb.addVariantMessage( "client", client );

                notifyTarget( mb );
            }
        }
    }


    /*
      * Handles ready events on queue type handles.
      *
      * @param handle
      *    a reference to the event source that generated the ready event
      */
    private void queueHandleEvent( Handle handle ) {

        MessageBlock mb = eventQueue.poll( );

        if ( mb != null ) { 

            String type = mb.getType( );

            switch ( type ) {
                case "com.kineticfire.patterns.reactor.net.request_listen":
                    queueHandleEventRequestListen( mb );
                    break;
                case "com.kineticfire.patterns.reactor.net.request_close":
                    queueHandleEventClose( mb );
                    break;
                case "com.kineticfire.patterns.reactor.request_shutdown":
                    queueHandleEventShutdown( mb );
                    break;
            }
        }
    }


    /**
      *
      */
    private void queueHandleEventRequestListen( MessageBlock mb ) {

        if ( state == TcpListenerState.READY ) {
            listen( mb );
        } else {
            // must be in TcpListenerState.LISTENING state

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_listen" );
            mbR.addVariantMessage( "reason", "state" );

            notifyTarget( mb, mbR );
        }

    }


    /*
      * Causes the TcpListener to listen for incoming client TCP connection requests on the port set via the constructor.
      * <p>
      * If the listener starts successfully, then the an event type 'com.kineticfire.patterns.reactor.net.event_listening' is sent to the acceptor.  If the listener errors, then an event type 'com.kineticfire.patterns.reactor.net.event_error_listen' is sent to the acceptor.
      *
      */
    private void listen( MessageBlock mb ) {

        try {
            channel = ServerSocketChannel.open( );
        } catch ( IOException e ) {
            channel = null;    

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_listen" );
            mbR.addVariantMessage( "reason", "open" );

            notifyTarget( mb, mbR );
        }


        if ( channel != null ) {

            ServerSocket socket = null;

            try {

                socket = channel.socket( );
                socket.bind( new InetSocketAddress( port ) );
                channel.configureBlocking( false );
                
                channelHandle = reactor.registerChannel( channel, this, EventMask.ACCEPT );

                state = TcpListenerState.LISTENING;

                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_listening" );

                notifyTarget( mb, mbR );

            } catch ( ClosedChannelException e ) {
                // thrown by configureBlocking()
                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_listen" );
                mbR.addVariantMessage( "reason", "closed_channel" );
                notifyTarget( mb, mbR );
            } catch ( SecurityException e ) {
                // thrown by bind() if a SecurityManager is present and its checkListen method doesn't allow the operation
                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_listen" );
                mbR.addVariantMessage( "reason", "security" );
                notifyTarget( mb, mbR );
            } catch ( IOException e ) {
                // thrown by configureBlocking() if channel closed
                // thrown by bind() if operation fails or socket is already bound
                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_listen" );
                mbR.addVariantMessage( "reason", "socket" );
                notifyTarget( mb, mbR );
            }
        }
    }


    /**
      *
      */
    private void queueHandleEventClose( MessageBlock mb ) {
        if ( state == TcpListenerState.LISTENING ) {
            close( mb, false );
        } else {
            // must be in TcpListenerState.READY

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close" );
            mbR.addVariantMessage( "reason", "state" );

            notifyTarget( mb, mbR );
        }
    }


    /*
      * Closes the channel.
      */
    private void close( MessageBlock mb, boolean shuttingDown ) {

        boolean closedSuccess = true;

        try {

            channel.close( );

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_closed" );
            notifyTarget( mb, mbR );

        } catch ( NullPointerException e ) {
            // the channel was never initialized... should never occur
            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close" );
            mbR.addVariantMessage( "reason", "not_initialized" );
            notifyTarget( mb, mbR );
        } catch ( IOException e ) {
            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close" );
            mbR.addVariantMessage( "reason", "io_exception" );
            notifyTarget( mb, mbR );
        } finally {

            if ( channelHandle != null && errorHandle != null ) {
                reactor.deregister( channelHandle, errorHandle );
            }

            channel = null;
            channelHandle = null;

            state = TcpListenerState.READY;

            if ( !shuttingDown ) {
                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.event_ready" );
                notifyTarget( mb, mbR );
            }

        }

    }


    /**
      *
      */
    private void queueHandleEventShutdown( MessageBlock mb ) {
        // all states can shutdown; shutdown state is not processing messages
        shutdown( mb );
    }


    /*
      *
      */
    private void shutdown( MessageBlock mb ) {

        if ( state == TcpListenerState.LISTENING ) {
            close( mb, true );
        }

        // channelHandle is deregistered via close(MessageBlock) method

        MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.event_shutdown" );
        notifyTarget( mb, mbR );


        if ( eventQueueHandle != null && errorHandle != null ) {
            reactor.deregister( eventQueueHandle, errorHandle );
        }

        if ( errorHandle != null ) {
            reactor.deregister( errorHandle, errorHandle );
        }


        reactor = null;

        eventQueue = null;
        eventQueueHandle = null;
        channel = null;
        channelHandle = null;
        errorHandle = null;
        acceptor = null;


        state = TcpListenerState.SHUTDOWN;
    }



    /**
      *
      */
    private void errorHandleEvent( Handle handle ) {
        MessageBlock mb = createMessageBlock( "com.kineticfire.patterns.reactor.event_error_handle" );
        notifyTarget( mb );
    }


    /*
      * Creates and returns a new variant-based MessageBlock sufficent for an emitted event with the type 'type'.
      * <p>
      * The new MessageBlock is created and returned with type set to 'type' plus the the following variant-based data messages:
      * <ol>
      *   <li>sourceType: com.kineticfire.patterns.reactor.net.TcpListener</li>
      *   <li>sourcePort: &lt;the port set for the TcpListener&gt;</li>
      *   <li>sourceName: $lt;the name of the TcpListener; not populated if name is null or not set&gt;</li>
      *   <li>sourceId: &lt;the id of the TcpListener; not populated if id is null or not set&gt;</li>
      * </ol>
      *
      * @param type
      *    the type to set for the new MessageBlock
      * @return the newly created MessageBlock
      */
    private MessageBlock createMessageBlock( String type ) {

        MessageBlock mb = MessageBlock.createVariantBasedMessageBlock( type );

        mb.addVariantMessage( "sourceType", sourceType );

        mb.addVariantMessage( "sourceRef", this );

        mb.addVariantMessage( "sourcePort", port );


        if ( name != null ) {
            mb.addVariantMessage( "sourceName", name );
        }

        if ( id != null ) {
            mb.addVariantMessage( "sourceId", id );
        }

        return( mb );
    }


    /*
      * Sends the message to the target for notifications.  The target is always the 'acceptor'.
      *
      * @param mbResponse
      *    MessageBlock to send to appropriate recipient
      */
    private void notifyTarget( MessageBlock mbResponse ) {
        acceptor.messageComponent( mbResponse );
    }


    /*
      * Determines the appropriate target to receive event notifications based on 'mbRequest' and sends the 'mbResponse' to MessageBlock to that recipient.
      * <p>
      * If the MessageBlock has a 'notifyRef' set, then that component is the intended recipient unless a ClassCastException occurs in which case the recipient is the acceptor.
      *
      * @param mbRequest
      *    MessageBlock to test for 'notifyRef' target
      * @param mbResponse
      *    MessageBlock to send to appropriate recipient
      */
    private void notifyTarget( MessageBlock mbRequest, MessageBlock mbResponse ) {

        if ( mbRequest.hasVariantMessageNotNull( "notifyRef" ) ) {
            try {
                MessageComponent notifyRef = (MessageComponent)mbRequest.getVariantMessage( "notifyRef" );
                notifyRef.messageComponent( mbResponse );
            } catch ( ClassCastException e ) {
            }
        } else {
            acceptor.messageComponent( mbResponse );
        }

    }

}
