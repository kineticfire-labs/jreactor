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
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;

import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.LinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.PriorityLinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.EventMask;

import com.kineticfire.patterns.reactor.messaging.MessageSinkForward;
import com.kineticfire.patterns.reactor.messaging.MessageSourceReverse;
import com.kineticfire.patterns.reactor.messaging.MessageSinkReverse;
import com.kineticfire.patterns.reactor.messaging.MessageComponent;




/**
  * Provides an implementation of a TCP Connection, handling both Java NIO non-blocking channel and Reactor mechanics.
  * <p>
  * The TcpConnection is a stream-oriented connection that sends and receives primitive byte arrays.
  * <p>
  * Messages, as MessageBlocks, may be sent to the TcpConnection either as part of the data stream or as signaling added to the component outside of the data stream.  Messages added to the data stream via the MessageReverse interface--offering messages to queue returned from 'getForwardTargetQueue()'--are processed in the order in which they are received, which may include both signaling and data messages.  Thus a signaling message like to close the connection added to the data stream is processed only after all other preceeding messages.  Signaling messages added via offer to 'getSignalingQueue()' or 'messageComponent()' are processed in order of the queue which may occur before other previously queued messages in the data stream.
  * <p>
  * This class supports message priority for the signaling stream only and not for the data stream.
  * <p>
  * Messages in, which are valid in both the signaling and data streams:
  * <ol>
  *    <li>command -&gt; send -&gt; &lt;data&gt; === Sends the data 'data', which must be a primitive byte array.</li>
  *    <li>command -&gt; attach -&gt; &lt;AccessibleMessageQueue&gt; === Sending this message is equivalent to calling 'attachReverse(AccessibleMessageQueue)'.</li>
  *    <li>command -&gt; detach -&gt; &lt;AccessibleMessageQueue&gt; === Sending this message is equivalent to calling 'detachReverse(AccessibleMessageQueue)'.</li>
  *    <li>command -&gt; shutdownInput === Shutsdown input on the channel.</li>
  *    <li>command -&gt; shutdownOutput === Shutsdown output on the channel.</li>
  *    <li>command -&gt; close === Causes the TcpConnection to close the connection, which occurs asynchronously.  Once the channel is closed, the TcpConnection sends a message to the reverse target queue that it is shutdown.  Sending this message is equivalent to calling 'close()'; sending this message with a low priority is equivalent to calling 'closeNow()'.</li>
  * </ol>
  * <p>
  * Messages out, sent to the MessageSink:
  * <ol>
  *    <li>event -&gt; receive -&gt; &lt;data&gt; -&gt; TcpConnection === Data received from remote client.  Data is a primitive byte array.</li>
  *    <li>event -&gt; inputShutdown -&gt; TcpConnection === The input was shutdown.  </li>
  *    <li>event -&gt; outputShutdown -&gt; TcpConnection === The output was shutdown </li>
  *    <li>event -&gt; closed -&gt; TcpConnection === The connection was closed.</li>
  * </ol>
  *
  *
  * @author Kris Hall
  * @version 05-05-2017
  */
public class TcpConnection implements Handler, MessageSinkForward, MessageSourceReverse, MessageComponent {

    private Reactor reactor;


    private MessageQueue dataQueue;    
    private Handle dataQueueHandle;

    private MessageQueue eventQueue;    
    private Handle eventQueueHandle;

    private SocketChannel channel;
    private Handle channelHandle;

    private Handle errorHandle;


    private MessageSinkReverse outputMessageSinkReverse;
    private Queue<MessageBlock> outputDataQueue;
    private Queue<MessageBlock> outputEventQueue;

    private ByteBuffer dataOutBuffer;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;


    // for Mode.CLIENT only -- for future: remote host and port so could implement a "reconnect()" method or automatically reconnect
    private String host;
    private int port;


    private enum TcpConnectionState { READY, CONNECTING, CONNECTED, CLOSED, SHUTDOWN; }
    private TcpConnectionState state;

    private boolean closedInput;


    private enum TcpConnectionMode { CLIENT, SERVER; }
    private TcpConnectionMode mode;


    private String name;
    private String id;


    private static String sourceType = "com.kineticfire.patterns.reactor.net.TcpConnection"; // sets the 'sourceType' field for events and errors




    /**
      * Creates a client TcpConnection.  Does not initiate the connection.
      * <p>
      * Uses a direct-allocated buffer of capacity 4096 bytes for receiving and another for sending.
      * <p>
      * The TcpConnection is idle at instantiation.  Call 'connect(String,int)' to connect to a remote host.  The TcpConnection will process events such as 'attach' and 'detach' and will also queue data via 'send' prior to connecting.
      *
      * @param reactor
      *    reference to the controlling reactor
      */
    public TcpConnection( Reactor reactor, String host, int port ) {
        this( reactor, host, port, 4096, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null, null ); // default to page size (4096)
    }


    public TcpConnection( Reactor reactor, String host, int port, String name, String id ) {
        this( reactor, host, port, 4096, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, name, id ); // default to page size (4096)
    }


    public TcpConnection( Reactor reactor, String host, int port, int bufferCapacity ) {
        this( reactor, host, port, bufferCapacity, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null, null );
    }


    /**
      * Creates a client TcpConnection.  Does not initiate the connection.
      * <p>
      * Uses a direct-allocated buffer of capacity 'bufferCapacity' bytes for receiving and another for sending.
      * <p>
      * The TcpConnection is idle at instantiation.  Call 'connect(String,int)' to connect to a remote host.  The TcpConnection will process events such as 'attach' and 'detach' and will also queue data via 'send' prior to connecting.
      *
      * @param reactor
      *    reference to the controlling reactor
      * @param bufferCapacity
      *    capacity of send and receive buffers
      */
    public TcpConnection( Reactor reactor, String host, int port, int bufferCapacity, int forwardDataQueueCapacity, int forwardEventQueueCapacity, int reverseDataQueueCapacity, int reverseEventQueueCapacity, String name, String id ) {

        this.reactor = reactor;

        this.host = host;
        this.port = port;

        this.name = name;
        this.id = id;


        readBuffer = ByteBuffer.allocateDirect( bufferCapacity );
        writeBuffer = ByteBuffer.allocateDirect( bufferCapacity );

        this.dataQueue = new LinkedBlockingMessageQueue( forwardDataQueueCapacity );
        dataQueueHandle = reactor.registerQueue( dataQueue, this, EventMask.NOOP );

        this.eventQueue = new PriorityLinkedBlockingMessageQueue( forwardEventQueueCapacity );
        eventQueueHandle = reactor.registerQueue( eventQueue, this, EventMask.NOOP );

        channel = null;
        channelHandle = null;

        errorHandle = reactor.registerError( this, EventMask.NOOP );

        outputMessageSinkReverse = null;
        outputDataQueue = new LinkedBlockingQueue<MessageBlock>( reverseDataQueueCapacity );
        outputEventQueue = new LinkedBlockingQueue<MessageBlock>( reverseEventQueueCapacity );

        dataOutBuffer = ByteBuffer.allocate( 1 );
        dataOutBuffer.position( 1 );
        writeBuffer.position( writeBuffer.limit( ) );


        // since adding channel into constructor, ensure all components are built and reactor-managed resources registered before setting interest ops to activate them

        reactor.interestOps( errorHandle, EventMask.ERROR, errorHandle );

        reactor.interestOps( eventQueueHandle, EventMask.QREAD, errorHandle );


        state = TcpConnectionState.READY;
        mode = TcpConnectionMode.CLIENT;

        closedInput = false;

        sendReadyEvent( );
    }


    /**
      * Creates a server TcpConnection using the estblished channel 'channel'.
      * <p>
      * The 'finishConnect()' method will be called on the channel, if required.  If so, events such as 'attach' and 'detach' are still processed; data is also queued as well via calls to 'send'.
      *
      * @param reactor
      *    reference to the controlling reactor
      */
    public TcpConnection( Reactor reactor, SocketChannel channel ) {
        this( reactor, channel, 4096, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null, null ); // default to page size (4096)
    }


    public TcpConnection( Reactor reactor, SocketChannel channel, String name, String id ) {
        this( reactor, channel, 4096, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, name, id ); // default to page size (4096)
    }


    public TcpConnection( Reactor reactor, SocketChannel channel, int bufferCapacity ) {
        this( reactor, channel, bufferCapacity, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, null, null );
    }


    /**
      * Creates a server TcpConnection using the estblished channel 'channel'.
      * <p>
      * The 'finishConnect()' method will be called on the channel, if required.  If so, events such as 'attach' and 'detach' are still processed; data is also queued as well via calls to 'send'.
      *
      * @param reactor
      *    reference to the controlling reactor
      * @param bufferCapacity
      *    capacity of send and receive buffers
      */
    public TcpConnection( Reactor reactor, SocketChannel channel, int bufferCapacity, int forwardDataQueueCapacity, int forwardEventQueueCapacity, int reverseDataQueueCapacity, int reverseEventQueueCapacity, String name, String id ) {
        
        // since adding channel with constructor, must be sure not to allow events from channel that would create race conditions before the constructor has finished

        this.reactor = reactor;

        this.host = null;
        this.port = -1;

        this.name = name;
        this.id = id;


        readBuffer = ByteBuffer.allocateDirect( bufferCapacity );
        writeBuffer = ByteBuffer.allocateDirect( bufferCapacity );

        this.dataQueue = new LinkedBlockingMessageQueue( forwardDataQueueCapacity );
        this.eventQueue = new LinkedBlockingMessageQueue( forwardEventQueueCapacity );

        this.channel = channel;

        outputMessageSinkReverse = null;
        outputDataQueue = new LinkedBlockingQueue<MessageBlock>( reverseDataQueueCapacity );
        outputEventQueue = new LinkedBlockingQueue<MessageBlock>( reverseEventQueueCapacity );

        dataOutBuffer = ByteBuffer.allocate( 1 );
        dataOutBuffer.position( 1 );
        writeBuffer.position( writeBuffer.limit( ) );

        eventQueueHandle = reactor.registerQueue( eventQueue, this, EventMask.NOOP );

        dataQueueHandle = reactor.registerQueue( dataQueue, this, EventMask.NOOP );
        channelHandle = reactor.registerChannel( channel, this, EventMask.NOOP );

        errorHandle = reactor.registerError( this, EventMask.NOOP );

        closedInput = false;

        // since adding channel into constructor, ensure all components are built and reactor-managed resources registered before setting interest ops to activate them

        reactor.interestOps( errorHandle, EventMask.ERROR, errorHandle );


        reactor.interestOps( eventQueueHandle, EventMask.QREAD, errorHandle );


        state = TcpConnectionState.READY;
        mode = TcpConnectionMode.SERVER;

        sendReadyEvent( );


        if ( channel.isConnectionPending( ) ) {
            // don't read from dataQueue until channel connected; keep it at NOOP
            reactor.interestOps( channelHandle, EventMask.CONNECT, errorHandle );

            state = TcpConnectionState.CONNECTING;
            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connecting" );
            notifyOobTarget( mbR );

        } else {
            reactor.interestOps( dataQueueHandle, EventMask.QREAD, errorHandle );
            reactor.interestOps( channelHandle, EventMask.CREAD, errorHandle ); // data read from channel will be put into outputDataQueue

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connecting" );
            notifyOobTarget( mbR );

            state = TcpConnectionState.CONNECTED;
            MessageBlock mbR2 = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connected" );
            notifyOobTarget( mbR2 );
        }


    }


    private void sendReadyEvent( ) {
        MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_ready" );
        notifyOobTarget( mbR );
    }


    /**
      * Processes a ready event on the handle 'handle'.  To be called by the Reactor pattern only.
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
        } else if ( handle == dataQueueHandle ) {
            dataQueueHandleEvent( handle );
        } else if ( handle == eventQueueHandle ) {
            eventQueueHandleEvent( handle );
        } else if ( handle == errorHandle ) {
            errorHandleEvent( handle );
        }

    }


    private void channelHandleEvent( Handle handle, int readyOps ) {

        if ( readyOps == EventMask.CREAD || readyOps == EventMask.CWRITE ) {
            if ( readDataFromChannel( ) ) {
                // read from channel without error so try to write to channel if data available
                writeDataToChannel( );
            }
        } else if ( readyOps == EventMask.CONNECT ) {
            finishConnect( );
        }

    }


    private void dataQueueHandleEvent( Handle handle ) {

        if ( dataOutBuffer.hasRemaining( ) ) {
            reactor.interestOps( dataQueueHandle, EventMask.NOOP, errorHandle );
        } else {

            MessageBlock mb = dataQueue.poll( );

            if ( mb != null ) {
                processEvent( mb );
            }

        }
    }


    private void eventQueueHandleEvent( Handle handle ) {

        MessageBlock mb = eventQueue.poll( );

        if ( mb != null ) {
            processEvent( mb );
        }

    }


    private void errorHandleEvent( Handle handle ) {
        //todo how to handle this error
    }



    /*
      * Processes the event 'mb'.
      *
      * @param mb
      *    MessageBlock describing event to process
      */
    private void processEvent( MessageBlock mb ) {

        String type = mb.getType( );

        switch ( type ) {
            case "com.kineticfire.patterns.reactor.net.request_send":
                requestSend( mb );
                break;
            case "com.kineticfire.patterns.reactor.net.request_connect":
                requestConnect( mb );
                break;
            case "com.kineticfire.patterns.reactor.messaging.request_attach_message_sink_reverse":
                requestAttachMessageSinkReverse( mb );
                break;
            case "com.kineticfire.patterns.reactor.messaging.request_detach_message_sink_reverse":
                requestDetachMessageSinkReverse( mb );
                break;
            case "com.kineticfire.patterns.reactor.net.request_CloseInput":
                requestCloseInput( mb );
                break;
            case "com.kineticfire.patterns.reactor.net.request_closeOutput":
                requestCloseOutput( mb );
                break;
            case "com.kineticfire.patterns.reactor.net.request_close":
                requestClose( mb );
                break;
            case "com.kineticfire.patterns.reactor.request_shutdown":
                requestShutdown( mb );
                break;
        }

    }


    private void requestSend( MessageBlock mb ) {

        if ( state == TcpConnectionState.CONNECTED ) {
            if ( mb.hasVariantMessageNotNull( "data" ) ) {
                try {
                    writeDataToChannel( ( byte[] )mb.getVariantMessage( "data" ) );
                } catch ( ClassCastException e ) {
                }
            }
        }

    }


    private void requestConnect( MessageBlock mb ) {

        MessageBlock mbR;

        if ( mode == TcpConnectionMode.CLIENT ) {

            if ( state == TcpConnectionState.READY ) {

                String host = mb.getVariantMessageString( "host" );
                int ip = mb.getVariantMessageInteger( "port" );

                try {

                    boolean success = false;

                    channel = SocketChannel.open( );
                    channel.configureBlocking( false );
                    success = channel.connect( new InetSocketAddress( host, port ) );

                    if ( success ) {
                        channelHandle = reactor.registerChannel( channel, this, EventMask.CREAD );
                        mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connecting" );
                        notifyOobTarget( mb, mbR );

                        state = TcpConnectionState.CONNECTED;
                        mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connected" );
                        notifyOobTarget( mb, mbR );
                    } else {
                        state = TcpConnectionState.CONNECTING;
                        channelHandle = reactor.registerChannel( channel, this, EventMask.CONNECT );
                        mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connecting" );
                        notifyOobTarget( mb, mbR );
                    }

                } catch ( AlreadyConnectedException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "already_connected" );
                    notifyOobTarget( mb, mbR );
                } catch ( ConnectionPendingException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "connection_pending" );
                    notifyOobTarget( mb, mbR );
                } catch ( ClosedChannelException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "closed_channel" );
                    notifyOobTarget( mb, mbR );
                } catch ( UnresolvedAddressException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "unresolved_address" );
                    notifyOobTarget( mb, mbR );
                } catch ( UnsupportedAddressTypeException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "unsupported_address_type" );
                    notifyOobTarget( mb, mbR );
                } catch ( SecurityException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "security" );
                    notifyOobTarget( mb, mbR );
                } catch ( IOException e ) {
                    mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                    mbR.addVariantMessage( "reason", "io" );
                    notifyOobTarget( mb, mbR );
                }

            } else {
                mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
                mbR.addVariantMessage( "reason", "state" );
                notifyOobTarget( mb, mbR );
            }

        } else {
            mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_connect" );
            mbR.addVariantMessage( "reason", "mode" );
            notifyOobTarget( mb, mbR );
        }

    }


    private void requestAttachMessageSinkReverse( MessageBlock mb ) {

        if ( mb.hasVariantMessageNotNull( "target" ) ) {

            outputMessageSinkReverse = (MessageSinkReverse)mb.getVariantMessage( "target" );

            if ( !outputDataQueue.isEmpty( ) ) {
                try {
                    outputMessageSinkReverse.sinkMessageReverse( outputDataQueue );
                } catch ( NullPointerException e ) {
                }
                outputDataQueue.clear( );
            }

            if ( !outputEventQueue.isEmpty( ) ) {
                try {
                    outputMessageSinkReverse.sinkOobMessageReverse( outputEventQueue );
                } catch ( NullPointerException e ) {
                }
                outputEventQueue.clear( );
            }

            /* I don't think should stop reading from channel.
            int interestOps = reactor.interestOps( channelHandle );

            if ( !EventMask.isConnect( interestOps ) ) {
                reactor.interestOps( channelHandle, EventMask.CREAD, errorHandle ); // read from channel now that outputMessageSinkReverse attached
            }
            */

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.messaging.event_attached_message_sink_reverse" );
            mbR.addVariantMessage( "target", outputMessageSinkReverse );

            notifyOobTarget( mb, mbR );

        }

    }


    private void requestDetachMessageSinkReverse( MessageBlock mb ) {

        /* I don't think should stop reading from channel.
        int interestOps = reactor.interestOps( channelHandle );

        if ( !EventMask.isConnect( interestOps ) ) {
            interestOps = EventMask.and( ~EventMask.CREAD, interestOps );
            reactor.interestOps( channelHandle, interestOps, errorHandle ); // don't read from channel until outputMessageSinkReverse attached
        }
        */

        MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.messaging.event_detached_message_sink_reverse" );

        mbR.addVariantMessage( "target", outputMessageSinkReverse );

        notifyOobTarget( mb, mbR );

        outputMessageSinkReverse = null;
    }


    // shutdown reading only
    private void requestCloseInput( MessageBlock mb ) {
        if ( state == TcpConnectionState.CONNECTED ) {

            try {

                channel.shutdownInput( );

                MessageBlock mb1 = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_receive_end" );
                outputMessageSinkReverse.sinkMessageReverse( mb1 );
                boolean closedInput = true;

                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_input_closed" );
                notifyOobTarget( mb, mbR );

            } catch ( IOException e ) {
                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close_input" );
                mbR.addVariantMessage( "reason", "io_exception" );
                notifyOobTarget( mb, mbR );
            }

        } else {
            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close_input" );
            mbR.addVariantMessage( "reason", "state" );
            notifyOobTarget( mb, mbR );
        }
    }


    // shutdown writing only
    private void requestCloseOutput( MessageBlock mb ) {
        if ( state == TcpConnectionState.CONNECTED ) {

            try {

                channel.shutdownOutput( );

                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_output_closed" );
                notifyOobTarget( mb, mbR );

            } catch ( IOException e ) {
                MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close_output" );
                mbR.addVariantMessage( "reason", "io_exception" );
                notifyOobTarget( mb, mbR );
            }

        } else {
            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_error_close_output" );
            mbR.addVariantMessage( "reason", "state" );
            notifyOobTarget( mb, mbR );
        }
    }


    private void requestClose( MessageBlock mb ) {
        doClose( false, "local" );
    }


    private void requestShutdown( MessageBlock mb ) {
        doShutdown( );
    }



    /*
      * Attempts to write the data 'data' to the channel.
      * <p>
      * The data is written to the channel up to the amount of space in the 'writeBuffer'.  If all data fits into the write buffer and the write buffer has no data remaining (such that all of its data was successfully written to the channel), then the interest ops are not modified.  Else the channel handle is set to the interestOps of EventMask.CWRITE and, if dataOutBuffer has data to write then the dataQueueHandle is set to EventMask.NOOP to prevent subsequent reads.
      *
      * @param data
      *    data to write to the channel
      */
    private void writeDataToChannel( byte[] data ) {

        dataOutBuffer = ByteBuffer.wrap( data ); 

        if ( writeBuffer.hasRemaining( ) ) {

            if ( writeBuffer.position( ) > 0 ) {
                writeBuffer.compact( );
            }

        } else {
            writeBuffer.clear( );
        }

        int offset = dataOutBuffer.position( );
        int length = Math.min( dataOutBuffer.remaining( ), writeBuffer.remaining( ) );
        writeBuffer.put( dataOutBuffer.array( ), offset, length );
        dataOutBuffer.position( offset + length );

        writeBuffer.flip( );


        try {

            channel.write( writeBuffer );


            if ( writeBuffer.hasRemaining( ) || dataOutBuffer.hasRemaining( ) ) {

                reactor.interestOpsOr( channelHandle, EventMask.CWRITE, errorHandle );

                if ( dataOutBuffer.hasRemaining( ) ) {
                    reactor.interestOps( dataQueueHandle, EventMask.NOOP, errorHandle );
                }

            } else {
                // done writing data, turn off CWRITE
                reactor.interestOpsAnd( channelHandle, ~EventMask.CWRITE, errorHandle );
            }

        } catch ( ClosedChannelException e ) {
            // the channel is closed
        } catch ( IOException e ) {
            // an error occurred
            doClose( true, "error_write" );
        }

    }


    /*
      * Attempts to continue writing data from 'dataOutBuffer' and 'writeBuffer' to the channel.
      *
      */
    private void writeDataToChannel( ) {

        if ( dataOutBuffer.hasRemaining( ) ) {
            if ( writeBuffer.hasRemaining( ) ) {
                if ( writeBuffer.position( ) > 0 ) {
                    writeBuffer.compact( );
                }

            } else {
                writeBuffer.clear( );
            }

            int offset = dataOutBuffer.position( );
            int length = Math.min( dataOutBuffer.remaining( ), writeBuffer.remaining( ) );
            writeBuffer.put( dataOutBuffer.array( ), offset, length );
            dataOutBuffer.position( offset + length );


            writeBuffer.flip( );

            if ( !dataOutBuffer.hasRemaining( ) ) {
                reactor.interestOpsAnd( channelHandle, ~EventMask.CWRITE, errorHandle );
                reactor.interestOps( dataQueueHandle, EventMask.QREAD, errorHandle );
            }

        } else {
            reactor.interestOpsAnd( channelHandle, ~EventMask.CWRITE, errorHandle );
            reactor.interestOps( dataQueueHandle, EventMask.QREAD, errorHandle );
        }


        if ( writeBuffer.hasRemaining( ) ) {

            try {

                channel.write( writeBuffer );

                if ( !writeBuffer.hasRemaining( ) ) {
                    if ( !dataOutBuffer.hasRemaining( ) ) {
                        reactor.interestOpsAnd( channelHandle, ~EventMask.CWRITE, errorHandle );
                    }
                }

            } catch ( ClosedChannelException e ) {
                // the channel is closed
            } catch ( IOException e ) {
                // an error occurred
                doClose( true, "error_write" );
            }

        } else {

            if ( !dataOutBuffer.hasRemaining( ) ) {
                reactor.interestOpsAnd( channelHandle, ~EventMask.CWRITE, errorHandle );
            }
        }

    }


    /*
      * Reads data from the channel, and sends the data to the designated output (either outputMessageForward or outputDataQueue).
      * <p>
      * Data is read-in as a byte array and placed in a MessageBlock.  The MessageBlock is passed to the output (com.kineticfire.patterns.reactor.messaging.MessageForward interface) via a call to 'messageReverseDirection(MessageBlock)', which was set by calling 'attachMessageReverseDirection(MessageForward)' or by sending a the message 'request_attach_message_reverse_direction'. 
      *
      */
    private boolean readDataFromChannel( ) {

        boolean error = false;

        int numRead = 0;

        try {
            readBuffer.clear( );
            numRead = channel.read( readBuffer ); 
        } catch ( ClosedChannelException e ) {
            // the channel is closed
            error = true;
            numRead = -2;
        } catch ( IOException e ) {
            // remote forced connection closed or error; can close
            error = true;
            numRead = -2;
            doClose( true, "error_read" );
        }


        if ( numRead == -1 ) {

            // remote shutdown socket cleanly; can close
            error = true;
            doClose( false, "remote" );

        } else if ( numRead > 0 ) {

            readBuffer.flip( );
            byte[] wireIn = new byte[numRead];
            readBuffer.get( wireIn );

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_receive" );
            mbR.addVariantMessage( "data", wireIn );

            try {
                outputMessageSinkReverse.sinkMessageReverse( mbR );
            } catch ( Exception e ) {
                error = !outputDataQueue.offer( mbR );

                if ( error ) {
                    // the outputDataQueue is full
                    doClose( true, "error_read_output_queue_full" );
                }

            }
        }


        return( error );
    }


    /*
      * Completes the connection to a remote host by calling 'finishConnect()' on the channel.
      * <p>
      * Sets the interest operations on the channel to EventMask.CREAD.
      *
      */
    private void finishConnect( ) {

        try {
            channel.finishConnect( );

            reactor.interestOps( dataQueueHandle, EventMask.QREAD, errorHandle );

            state = TcpConnectionState.CONNECTED;
            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_connected" );
            notifyOobTarget( mbR );

            reactor.interestOps( channelHandle, EventMask.CREAD, errorHandle ); // data read from channel will be put into outputDataQueue if outputMessageSinkReverse not attached


            /* I think the above interestOps setting replaces all of this below.
            if ( outputMessageSinkReverse == null ) {
                //set channel interest ops to not CONNECT
                int interestOps = EventMask.and( ~EventMask.CONNECT, reactor.interestOps( channelHandle ) );
                reactor.interestOps( channelHandle, interestOps, errorHandle ); // don't read from channel until outputMessageSinkReverse attached
            } else {
                //set channel interest ops to not CONNECT and add CREAD
                int interestOps = EventMask.and( ~EventMask.CONNECT, reactor.interestOps( channelHandle ) );
                interestOps = EventMask.or( EventMask.CREAD, interestOps );
                reactor.interestOps( channelHandle, interestOps, errorHandle ); // read from channel since outputMessageSinkReverse attached
            }
            // set to original ops but not CONNECT
            */



        } catch ( IOException e ) {
            doClose( true, "finish_connect" );
        }
    }


    private void doClose( boolean isError, String reason ) {

        if ( state != TcpConnectionState.READY && state != TcpConnectionState.CLOSED ) {

            if ( channel != null ) {

                try {
                    channel.close( );
                } catch ( IOException e ) {
                }


                // if channel isn't null, then channelHandle isn't null
                reactor.deregister( channelHandle, errorHandle );

            }


            state = TcpConnectionState.CLOSED;


            if ( !closedInput ) {
                MessageBlock mb1 = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_receive_end" );
                sendMessageSinkReverse( mb1 );
            }

            MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.net.event_closed" );
            mbR.addVariantMessage( "isError", isError );
            mbR.addVariantMessage( "reason", reason );
            notifyOobTarget( mbR );

            checkCloseTransition( );
        }

    }


    // assumes that doClose() has been called and this is not a shutdown type event
    private void checkCloseTransition( ) {
        if ( mode == TcpConnectionMode.CLIENT ) {
            state = TcpConnectionState.READY;
            clearData( );
            sendReadyEvent( );
        }
    }



    /**
      * Shutsdown the connection, releasing all resources.
      *
      */
    private void doShutdown( ) {

        doClose( false, "shutdown" ); // deregisters the channel

        state = TcpConnectionState.SHUTDOWN;

        reactor.deregister( dataQueueHandle, errorHandle );
        reactor.deregister( eventQueueHandle, errorHandle );
        reactor.deregister( errorHandle, errorHandle );
        reactor = null;

        MessageBlock mbR = createMessageBlock( "com.kineticfire.patterns.reactor.event_shutdown" );
        notifyOobTarget( mbR );

        dataQueue = null;
        eventQueue = null;

        outputMessageSinkReverse = null;
        outputDataQueue = null;
        outputEventQueue = null;

    }


    // todo - also clears control messages.  Perhaps a closed client connection can remain closed until a 'reset' or 'reconnect' message is received.
    private void clearData( ) {
        dataQueue.clear( );
    }


    /*
      * Returns an InetSocketAddress object containing the IP and port of the local host or null if the TcpConnection was not or is not connected.
      *
      * @return an InetSocketAddress object containing the IP and port of the local host
      */
    /*
    public InetSocketAddress getLocalInetSocketAddress( ) {

        InetSocketAddress address = null;

        Socket socket = null;

        if ( channel != null ) {
            socket = channel.socket( );
        }

        if ( socket != null ) {
            InetAddress addr = socket.getLocalAddress( );
            address = new InetSocketAddress(addr, socket.getLocalPort( ) );
        }

        return( address );
    }
    */


    /*
      * Returns an InetSocketAddress object containing the IP and port of the remote host or null if the TcpConnection was not or is not connected.
      *
      * @return an InetSocketAddress object containing the IP and port of the remote host
      */
    /*
    public InetSocketAddress getRemoteInetSocketAddress( ) {
        InetSocketAddress address = null;

        Socket socket = null;
        if ( channel != null ) {
            socket = channel.socket( );
        }

        if ( socket != null ) {
            InetAddress addr = socket.getInetAddress( );
            address = new InetSocketAddress(addr, socket.getPort( ) );
        }

        return( address );
    }
    */


    /**
      * Causes the TcpConnection to attempt to establish a TCP connection with a remote host at the IP address 'ip' and port 'port'.
      * <p>
      * This method is not thread-safe.  This method should not be used at any time in server mode.  This method may be used in client mode when not connected.
      *
      * @throws java.nio.channels.AlreadyConnectedException
      *    if channel is already connected
      * @throws java.nio.channels.ConnectionPendingException
      *    if a non-blocking connection operation is relady in progress on this channel
      * @throws java.nio.channels.ClosedChannelException
      *    if the channel is closed
      * @throws java.nio.channels.UnresolvedAddressException
      *    remote address is not fully resolved
      * @throws java.nio.channels.UnsupportedAddressTypeException
      *    if the type of address is not supported
      * @throws java.lang.SecurityException
      *    if a security manager has been installed and it does not permit access to the given remote endpoint
      */
    public void connect( String host, int port ) throws IOException {

        MessageBlock mb = MessageBlock.createVariantBasedMessageBlock( "com.kineticfire.patterns.reactor.net.connect" );
        mb.addVariantMessage( "host", host );
        mb.addVariantMessage( "port", port );

        requestConnect( mb );
    }


    /**
      * Sends a message to this component.
      * <p>
      * The message is queued by the component.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the component has shutdown.
      * <p>
      * The component's class documentation must describe messages handled by this component.
      *
      * @param mb
      *    message to send
      * @return true if the message were added and false if the message were rejected
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
    

    /**
      * Sets or adds the reverse output MessageSinkReverse 'sink'.
      * <p>
      * Implementing classes should state if single or multiple reverse outputs are supported.  If only a single reverse output is supported, then 'sink' is set as the sole output target.  If multiple reverse outputs are supported, then 'sink' is added to the set of reverse outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe.
      * 
      * @param sink
      *    for a single reverse output, sets the reverse output to 'forward'; for multiple reverse outputs, adds 'sink' to the set of outputs
      */
    public void attachMessageSinkReverse( MessageSinkReverse sink  ) {
        MessageBlock mb = MessageBlock.createVariantBasedMessageBlock( "com.kineticfire.patterns.reactor.messaging.request_attach_message_sink_reverse" );
        mb.addVariantMessage( "target", sink );

        requestAttachMessageSinkReverse( mb );
    }


    /**
      * Removes the reverse output MessageSinkReverse 'sink'.
      * <p>
      * Implementing classes should state if single or multiple reverse outputs are supported.  If only a single reverse output is supported, then the reverse output is removed regardless of the value of 'sink' and may be null.  If multiple reverse outputs are supported, then 'sink' is removed from the set of reverse outputs.  No action is taken if 'sink' is not in the set of reverse outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe.
      * 
      * @param sink
      *    for a single reverse output, this value is ignored and may be null; for multiple reverse outputs, the reverse output to be removed from the set of output targets
      */
    public void detachMessageSinkReverse( MessageSinkReverse sink ) {
        MessageBlock mb = MessageBlock.createVariantBasedMessageBlock( "com.kineticfire.patterns.reactor.messaging.request_detach_message_sink_reverse" );
        mb.addVariantMessage( "target", sink );

        requestDetachMessageSinkReverse( mb );
    }



    /**
      * Sends in the forward direction a message to this component.
      * <p>
      * The message is queued by the component.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the component has shutdown.
      * <p>
      * The component's class documentation must describe messages handled by this component.
      *
      * @param mb
      *    message to send
      * @return true if the message were added and false if the message were rejected
      */
    public boolean sinkMessageForward( MessageBlock mb ) {
        return( dataQueue.offer( mb ) );
    }
    

    /**
      * Sends in the forward direction all messages in the collection 'c' to the component.  Returns true if the messages were successfully added or false otherwise.
      * 
      * @param c
      *    the messages as a collection to add
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
    public boolean sinkMessageForward( Collection<? extends MessageBlock> c ) {
        return( dataQueue.addAll( c ) );
    }


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the forward direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkMessageForwardPressure( ) {
        return( dataQueue.pressure( ) );
    }


    /**
      * Sends in the forward direction a message to this component.
      * <p>
      * The message is queued by the component.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the component has shutdown.
      * <p>
      * The component's class documentation must describe messages handled by this component.
      *
      * @param mb
      *    message to send
      * @return true if the message were added and false if the message were rejected
      */
    public boolean sinkOobMessageForward( MessageBlock mb ) {
        return( eventQueue.offer( mb ) );
    }
    

    /**
      * Sends in the forward direction all messages in the collection 'c' to the component.  Returns true if the messages were successfully added or false otherwise.
      * 
      * @param c
      *    the messages as a collection to add
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
    public boolean sinkOobMessageForward( Collection<? extends MessageBlock> c ) {
        return( eventQueue.addAll( c ) );
    }


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the forward direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkOobMessageForwardPressure( ) {
        return( eventQueue.pressure( ) );
    }


    public String getName( ) {
        return( name );
    }


    public String getId( ) {
        return( id );
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


    private void sendMessageSinkReverse( MessageBlock mb ) {
        try {
            outputMessageSinkReverse.sinkMessageReverse( mb );
        } catch ( NullPointerException e ) {
        }
    }

    private void notifyOobTarget( MessageBlock mbResponse ) {

        try {
            outputMessageSinkReverse.sinkOobMessageReverse( mbResponse );
        } catch ( NullPointerException e ) {
            outputEventQueue.offer( mbResponse );
        }

    }


    /*
      * Determines the appropriate target to receive event notifications based on 'mbRequest' and sends the 'mbResponse' to MessageBlock to that recipient.
      * <p>
      * If the MessageBlock has a 'notifyRef' set, then that component is the intended recipient unless a ClassCastException occurs in which case the recipient is the outputTarget unless that is not set (e.g. null) and then the message is given to the outputTemp.
      *
      * @param mbRequest
      *    MessageBlock to test for 'notifyRef' target
      * @param mbResponse
      *    MessageBlock to send to appropriate recipient
      */
    private void notifyOobTarget( MessageBlock mbRequest, MessageBlock mbResponse ) {

        if ( mbRequest.hasVariantMessageNotNull( "notifyRef" ) ) {
            try {
                MessageComponent notifyRef = (MessageComponent)mbRequest.getVariantMessage( "notifyRef" );
                notifyRef.messageComponent( mbResponse );
            } catch ( NullPointerException e ) {
            }
        } else {
            try {
                outputMessageSinkReverse.sinkOobMessageReverse( mbResponse );
            } catch ( NullPointerException e ) {
                outputDataQueue.offer( mbResponse );
            }
        }

    }

}
