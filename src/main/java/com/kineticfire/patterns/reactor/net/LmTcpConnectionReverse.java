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



import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.LinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.EventMask;

import com.kineticfire.patterns.reactor.messaging.MessageSinkReverse;
import com.kineticfire.patterns.reactor.messaging.MessageSourceReverse;
import com.kineticfire.patterns.reactor.messaging.MessageComponent;


import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.UnsupportedEncodingException;





/**
  * Defines an LmTcpConnectionReverse that removes framing around data received from a remote endpoint, suitable for implementing a message-oriented TCP connection.
  * <p>
  * Data received from the remote host is done so through the messaging passing interface, where data is sent as a MessageBlock to the attached MessageSink.  The MessageSink is set with a call to 'attach(MessageSink)'.  See the method as well as an event description in this class documentation for more information.  The received data is a primitive byte array.
  * <p>
  * Framing is removed from the recevied data.  Messages may be from 1 to 65,537 bytes in length inclusive (excluding the framing), due to the framing mechanism.
  * <p>
  * The messages exchanged are of the form: &lt;length&gt;&lt;message&gt;.  The 'length' is a two-byte, big-endian unsigned integer giving the number of bytes of the message minus one; that is, a 'length' of 0 is 1 byte, 1 is 2 bytes, and so on.  Message is a primitive byte array and sent and received as an atomic unit.
  * <p>
  * The messages, as MessageBlocks, defined for LmTcpConnectionReverse consist of the following.  LmTcpConnectionReverse does not act on other messages and allows those messages to pass transparently.
  * <p>
  * Messages in:
  * <ol>
  * </ol>
  *
  * @author Kris Hall
  * @version 05-29-2017
  */
public class LmTcpConnectionReverse implements Handler, MessageSinkReverse, MessageSourceReverse {

    private Reactor reactor;

    private MessageComponent forward; // to notify of shutdown event

    private MessageQueue dataQueue;    
    private Handle dataQueueHandle;

    private MessageQueue eventQueue;    
    private Handle eventQueueHandle;

    private MessageSinkReverse outputMessageSinkReverse;
    private Queue<MessageBlock> outputDataQueue;
    private Queue<MessageBlock> outputEventQueue;

    private Handle errorHandle;

    private byte[] dataByteArray;
    private int needNumBytes;


    private String name;
    private String id;


    private static String sourceType = "com.kineticfire.patterns.reactor.net.LmTcpConnection"; // sets the 'sourceType' field for events and errors





    public LmTcpConnectionReverse( Reactor reactor, MessageComponent forward, TcpConnection tcpConnection ) {
        this( reactor, forward, tcpConnection, Integer.MAX_VALUE, Integer.MAX_VALUE );
    }


    /**
      * Creates a TcpConnection prepared to make establish a new connection.
      * <p>
      * The TcpConnection is idle at instantiation.  Call 'connect(String,int)' to connect to a remote host.  The TcpConnection will process events such as 'attach' and 'detach' and will also queue data via 'send' prior to connecting.
      *
      * @param reactor
      *    reference to the controlling reactor
      * @param lmTcpConnection
      *    reference to the controlling lmTcpConnection
      */
    public LmTcpConnectionReverse( Reactor reactor, MessageComponent forward, TcpConnection tcpConnection, int reverseDataQueueCapacity, int reverseEventQueueCapacity ) {

        this.reactor = reactor;

        this.forward = forward;

        this.dataQueue = new LinkedBlockingMessageQueue( );
        dataQueueHandle = reactor.registerQueue( dataQueue, this, EventMask.NOOP );

        this.eventQueue = new LinkedBlockingMessageQueue( );
        eventQueueHandle = reactor.registerQueue( eventQueue, this, EventMask.NOOP );

        outputDataQueue = new LinkedBlockingQueue<MessageBlock>( reverseDataQueueCapacity );
        outputEventQueue = new LinkedBlockingQueue<MessageBlock>( reverseEventQueueCapacity );

        errorHandle = reactor.registerError( this, EventMask.NOOP );

        outputMessageSinkReverse = null;

        dataByteArray = null;
        needNumBytes = -1;


        tcpConnection.attachMessageSinkReverse( this ); // creates a message so is asynch safe

        name = tcpConnection.getName( );
        id = tcpConnection.getId( );


        // since adding channel into constructor, ensure all components are built and reactor-managed resources registered before setting interest ops to activate them

        reactor.interestOps( errorHandle, EventMask.ERROR, errorHandle );

        reactor.interestOps( eventQueueHandle, EventMask.QREAD, errorHandle );

        reactor.interestOps( dataQueueHandle, EventMask.QREAD, errorHandle );

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
        if ( handle == dataQueueHandle ) {
            dataQueueHandleEvent( handle );
        } else if ( handle == eventQueueHandle ) {
            eventQueueHandleEvent( handle );
        }
    }


    private void dataQueueHandleEvent( Handle handle ) {

        MessageBlock mb = dataQueue.poll( );

        if ( mb != null ) {

            String type = mb.getType( );

            switch ( type ) {
                case "com.kineticfire.patterns.reactor.net.event_receive":
                    processReceivedData( mb );
                    break;
                default:
                    sendOutputMessageSinkReverse( mb );
                    break;
            }

        }
    }


    private void eventQueueHandleEvent( Handle handle ) {

        MessageBlock mb = eventQueue.poll( );

        if ( mb != null ) {

            String type = mb.getType( );

            switch ( type ) {
                case "com.kineticfire.patterns.reactor.event_shutdown":
                    processEventShutdown( mb );
                    break;
                case "com.kineticfire.patterns.reactor.messaging.request_attach_message_sink_reverse":
                    requestAttachMessageSinkReverse( mb );
                    break;
                case "com.kineticfire.patterns.reactor.messaging.request_detach_message_sink_reverse":
                    requestDetachMessageSinkReverse( mb );
                    break;
                default:
                    sendOutputOobMessageSinkReverse( mb );
                    break;
            }
        }

    }


    /*
      * Removes framing and sends to the output queue.
      * <p>
      * The format of received data must be <length><message>.  'Length' is the byte length of all data, minus one such that a length of zero is one bytes, one is two bytes, and so on.  The 'length' does not include the length of the 'length' field itself.  The 'length' field is a 2-byte unsigned big-endian integer.  Message is the data to send, passed as the data argument(s).  The length of the message, not including the length of the 'length' field, must be between 1 and 65,737 bytes.
      * @param mb
      *    MessageBlock describing event to process
      */
    private void processReceivedData( MessageBlock mb ) {

        byte[] currentByteArray = (byte[])mb.getVariantMessage( "data" );

        int posCurrentByteArray = 0;

        int min;


        while ( posCurrentByteArray < currentByteArray.length ) {

            if ( needNumBytes == -1 ) {

                // have not received full length for frame; either have none or have half (1 byte)

                if ( dataByteArray == null ) {

                    // have not received any of length of frame

                    dataByteArray = new byte[2]; // the length in the frame is always two bytes
                    dataByteArray[0] = currentByteArray[0]; // must be at least one byte in recevied byte array
                    posCurrentByteArray = 1;

                    if ( currentByteArray.length > 1 ) {
                        dataByteArray[1] = currentByteArray[1];
                        needNumBytes = ( ( ( dataByteArray[0] << 8 ) & 0x0000ff00) | ( dataByteArray[1] & 0x000000ff ) ) + 1;
                        posCurrentByteArray = 2;
                        dataByteArray = null;
                    }

                } else {

                    // must have previously received exactly one byte of the frame, which is the first byte of the length

                    dataByteArray[1] = currentByteArray[0];
                    needNumBytes = ( ( ( dataByteArray[0] << 8 ) & 0x0000ff00) | ( dataByteArray[1] & 0x000000ff ) ) + 1;
                    posCurrentByteArray = 1;
                    dataByteArray = null;

                }

            }


            if ( posCurrentByteArray < currentByteArray.length ) {

                if ( dataByteArray == null && ( needNumBytes <= currentByteArray.length - posCurrentByteArray ) ) {

                    // All of the bytes needed for this atomic message are currently contained in the current byte array, possibly plus the length indicator.  That is, there are no bytes from earlier messages in the dataByteArray and the currentByteArray contains at least needNumBytes number of bytes.  As many (most?) messages will be contained in a single packet, this check provides efficiency by avoiding array copy, as processed by the other 'if' blocks below when a message is split across multiple packets.

                    //todo is is worth having this now, since no longer translating directly to string and instead returning byte[] ?

                    dataByteArray = new byte[needNumBytes];

                    System.arraycopy( currentByteArray, posCurrentByteArray, dataByteArray, 0, needNumBytes );

                    mb.addVariantMessage( "data", dataByteArray );
                    sendOutputMessageSinkReverse( mb );

                    posCurrentByteArray += needNumBytes;
                    needNumBytes = -1;
                    dataByteArray = null;


                } else {

                    // data to read in current byte array... dataByteArray null and/or insufficient bytes in current byte array for single message

                    if ( dataByteArray == null ) {
                        dataByteArray = new byte[needNumBytes];
                    }

                    min = Math.min( needNumBytes, currentByteArray.length - posCurrentByteArray );

                    int posDataByteArray = dataByteArray.length - needNumBytes;

                    System.arraycopy( currentByteArray, posCurrentByteArray, dataByteArray, posDataByteArray, min );

                    posCurrentByteArray += needNumBytes;
                    needNumBytes -= min;

                    if ( needNumBytes == 0 ) {

                        // got enough bytes for the message, so convert to a String and send event

                        mb.addVariantMessage( "data", dataByteArray );
                        sendOutputMessageSinkReverse( mb );

                        needNumBytes = -1;
                        dataByteArray = null;

                    }

                }

            }

        }

    }


    private void processEventShutdown( MessageBlock mb ) {

        forward.messageComponent( mb ); // notify LmTcpConnectionForward of shutdown event

        forward = null;

        reactor.deregister( dataQueueHandle, errorHandle );
        reactor.deregister( eventQueueHandle, errorHandle );
        reactor.deregister( errorHandle, errorHandle );
        reactor = null;

        outputMessageSinkReverse = null;
        outputDataQueue = null;
        outputEventQueue = null;

        dataByteArray = null;
    }



    private void sendOutputMessageSinkReverse( MessageBlock mb ) {
        try {
            outputMessageSinkReverse.sinkMessageReverse( mb );
        } catch ( NullPointerException e ) {
            outputDataQueue.offer( mb );
        }
    }


    private void sendOutputOobMessageSinkReverse( MessageBlock mb ) {
        try {
            outputMessageSinkReverse.sinkOobMessageReverse( mb );
        } catch ( NullPointerException e ) {
            outputEventQueue.offer( mb );
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

            MessageBlock mbR = createEventMessageBlock( "com.kineticfire.patterns.reactor.messaging.event_attached_message_sink_reverse" );
            mbR.addVariantMessage( "target", outputMessageSinkReverse );

            notifyTarget( mb, mbR );

        }

    }


    private void requestDetachMessageSinkReverse( MessageBlock mb ) {

        MessageBlock mbR = createEventMessageBlock( "com.kineticfire.patterns.reactor.messaging.event_detached_message_sink_reverse" );

        mbR.addVariantMessage( "target", outputMessageSinkReverse );

        notifyTarget( mb, mbR );

        outputMessageSinkReverse = null;
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
      * Sends in the reverse direction a message to this component.
      * <p>
      * The message is queued by the component.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the component has shutdown.
      * <p>
      * The component's class documentation must describe messages handled by this component.
      *
      * @param mb
      *    message to send
      * @return true if the message were added and false if the message were rejected
      */
    public boolean sinkMessageReverse( MessageBlock mb ) {
        return( dataQueue.offer( mb ) );
    }
    

    /**
      * Sends in the reverse direction all messages in the collection 'c' to the component.  Returns true if the messages were successfully added or false otherwise.
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
    public boolean sinkMessageReverse( Collection<? extends MessageBlock> c ) {
        return( dataQueue.addAll( c ) );
    }


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the reverse direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkMessageReversePressure( ) {
        return( dataQueue.pressure( ) );
    }
    

    /**
      * Sends in the reverse direction an out-of-band message to this component.
      * <p>
      * The message is queued by the component.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the component has shutdown.
      * <p>
      * The component's class documentation must describe messages handled by this component.
      *
      * @param mb
      *    out-of-band message to send
      * @return true if the message were added and false if the message were rejected
      */
    public boolean sinkOobMessageReverse( MessageBlock mb ) {
        return( eventQueue.offer( mb ) );
    }
    

    /**
      * Sends in the reverse direction all out-of-band messages in the collection 'c' to the component.  Returns true if the messages were successfully added or false otherwise.
      * 
      * @param c
      *    the out-of-band messages as a collection to add
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
    public boolean sinkOobMessageReverse( Collection<? extends MessageBlock> c ) {
        return( eventQueue.addAll( c ) );
    }


    /**
      * Returns the ratio of the number of out-of-band messages in the component to the message capacity of the component on the range of [0.0,1.0] in the reverse direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkOobMessageReversePressure( ) {
        return( eventQueue.pressure( ) );
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
            sendOutputOobMessageSinkReverse( mbResponse );
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
    private void notifyTarget( MessageBlock mbRequest, MessageBlock mbResponse ) {

        if ( mbRequest.hasVariantMessageNotNull( "notifyRef" ) ) {
            try {
                MessageComponent notifyRef = (MessageComponent)mbRequest.getVariantMessage( "notifyRef" );
                notifyRef.messageComponent( mbResponse );
            } catch ( NullPointerException e ) {
            }
        } else {
            sendOutputMessageSinkReverse( mbResponse );
        }

    }


    /*
      * Creates and returns a new variant-based MessageBlock sufficent for an emitted event with the type 'type'.
      * <p>
      * The new MessageBlock is created and returned with type set to 'type' plus the the following variant-based data messages:
      * <ol>
      *   <li>sourceType: com.kineticfire.patterns.reactor.net.TcpListener</li>
      *   <li>sourceName: $lt;the name of the TcpListener; not populated if name is null or not set&gt;</li>
      *   <li>sourceId: &lt;the id of the TcpListener; not populated if id is null or not set&gt;</li>
      * </ol>
      *
      * @param type
      *    the type to set for the new MessageBlock
      * @return the newly created MessageBlock
      */
    private MessageBlock createEventMessageBlock( String type ) {

        MessageBlock mb = MessageBlock.createVariantBasedMessageBlock( type );

        mb.addVariantMessage( "sourceType", sourceType );

        mb.addVariantMessage( "sourceRef", this );


        if ( name != null ) {
            mb.addVariantMessage( "sourceName", name );
        }

        if ( id != null ) {
            mb.addVariantMessage( "sourceId", id );
        }

        return( mb );
    }


}
