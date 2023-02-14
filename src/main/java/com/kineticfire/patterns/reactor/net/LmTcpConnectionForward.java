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
import com.kineticfire.patterns.reactor.PriorityLinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.EventMask;

import com.kineticfire.patterns.reactor.messaging.MessageSinkForward;
import com.kineticfire.patterns.reactor.messaging.MessageSourceForward;
import com.kineticfire.patterns.reactor.messaging.MessageComponent;


import java.util.Collection;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;



/**
  * Defines an LmTcpConnectionForward that adds framing around data to send to a remote endpoint, suitable for implementing a message-oriented TCP connection.
  * <p>
  * Data to send to the remote host is done so through the 'send(MessageBlock)' method.  See the method as well as an event description in this class documentation for more information.  The data to send must be a primitive byte array.
  * <p>
  * Framing is applied to the data when sending.  Messages may be from 1 to 65,537 bytes in length inclusive (excluding the framing), due to the framing mechanism.
  * <p>
  * The messages exchanged are of the form: &lt;length&gt;&lt;message&gt;.  The 'length' is a two-byte, big-endian unsigned integer giving the number of bytes of the message minus one; that is, a 'length' of 0 is 1 byte, 1 is 2 bytes, and so on.  Message is a primitive byte array and sent and received as an atomic unit.
  * <p>
  * The messages, as MessageBlocks, defined for LmTcpConnectionForward consist of the following.  LmTcpConnectionForward does not act on other messages and allows those messages to pass transparently.
  * <p>
  * Messages in:
  * <ol>
  * </ol>
  * <p>
  * There are no messages out.
  *
  * @author Kris Hall
  * @version 05-29-2017
  */
public class LmTcpConnectionForward implements Handler, MessageSinkForward, MessageSourceForward, MessageComponent {

    private Reactor reactor;

    private MessageQueue dataQueue;    
    private Handle dataQueueHandle;

    private MessageQueue eventQueue;    
    private Handle eventQueueHandle;

    private Handle errorHandle;

    private MessageSinkForward outputMessageSinkForward;


    private String name;
    private String id;


    private static String sourceType = "com.kineticfire.patterns.reactor.net.LmTcpConnection"; // sets the 'sourceType' field for events and errors



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
    public LmTcpConnectionForward( Reactor reactor, TcpConnection tcpConnection ) {

        this.reactor = reactor;

        outputMessageSinkForward = tcpConnection;

        this.dataQueue = new PriorityLinkedBlockingMessageQueue( );
        dataQueueHandle = reactor.registerQueue( dataQueue, this, EventMask.NOOP );

        this.eventQueue = new PriorityLinkedBlockingMessageQueue( );
        eventQueueHandle = reactor.registerQueue( eventQueue, this, EventMask.NOOP );

        errorHandle = reactor.registerError( this, EventMask.NOOP );


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
                case "com.kineticfire.patterns.reactor.net.request_send":
                    processData( mb );
                    break;
                default:
                    sendOutputMessageSinkForward( mb );
                    break;
            }

        }
    }


    private void eventQueueHandleEvent( Handle handle ) {

        MessageBlock mb = eventQueue.poll( );

        if ( mb != null ) {

            String type = mb.getType( );

            switch ( type ) {
                case "com.kineticfire.patterns.reactor.event_shutdown": // from LmTcpConnectionMessageReverse
                    processEventShutdown( mb );
                    break;
                default:
                    sendOutputOobMessageSinkForward( mb );
                    break;
            }
        }

    }


    /*
      * Process data 'mb'.
      * <p>
      * Sends to processEvent(MessageBlock)' if the data is control messaging.
      * <p>
      * The format of sent data is <length><message>.  'Length' is the byte length of all data, minus one such that a length of zero is one bytes, one is two bytes, and so on.  The 'length' does not include the length of the 'length' field itself.  The 'length' field is a 2-byte unsigned big-endian integer.  Message is the data to send, passed as the data argument(s).  The length of the message, not including the length of the 'length' field, must be between 1 and 65,737 bytes.
      *
      * @param mb
      *    data to process
      */
    private void processData( MessageBlock mb ) {

        // all data at this point is data to frame and send to remote client

        if ( mb.hasVariantMessageNotNull( "data" ) ) {

            byte[] data = ( byte[] )mb.getVariantMessage( "data" );


            int dataLength = data.length;
            int encodedDataLength = data.length - 1; // encoded length is = length - 1, and excludes 2-byte length indicator
            int totalLength = data.length + 2; // actual length is 2 byte header plus length of data

            ByteBuffer bb = ByteBuffer.allocate( 4 ); // default order is big-endian
            bb.putInt( encodedDataLength );
            byte[] l = bb.array( );


            byte[] b = new byte[totalLength];

            b[0] = l[2];
            b[1] = l[3];

            System.arraycopy( data, 0, b, 2, data.length );


            //todo:  why is this here?  i think was for testing for display purposes but has no impact on the data sent??
            for (byte ba : b) {
                 String st = String.format("%02X", ba);
            }

            mb.addVariantMessage( "data", b );

            sendOutputMessageSinkForward( mb );

        }

    }


    private void processEventShutdown( MessageBlock mb ) {
        // Message recevied from LmTcpConnectionReverse.  Message is sent by refernence so do not modify.

        reactor.deregister( dataQueueHandle, errorHandle );
        reactor.deregister( eventQueueHandle, errorHandle );
        reactor.deregister( errorHandle, errorHandle );
        reactor = null;

        dataQueue = null;
        eventQueue = null;

        outputMessageSinkForward = null;

    }


    private void sendOutputMessageSinkForward( MessageBlock mb ) {
        try {
            outputMessageSinkForward.sinkMessageForward( mb );
        } catch ( NullPointerException e ) {
        }
    }


    private void sendOutputOobMessageSinkForward( MessageBlock mb ) {
        try {
            outputMessageSinkForward.sinkOobMessageForward( mb );
        } catch ( NullPointerException e ) {
        }
    }


    private void requestAttachMessageSinkForward( MessageBlock mb ) {

        if ( mb.hasVariantMessageNotNull( "target" ) ) {

            outputMessageSinkForward = (MessageSinkForward)mb.getVariantMessage( "target" );

            MessageBlock mbR = createEventMessageBlock( "com.kineticfire.patterns.reactor.messaging.event_attached_message_sink_forward" );
            mbR.addVariantMessage( "target", outputMessageSinkForward );


            sendOutputOobMessageSinkForward( mbR );
        }

    }
    

    /**
      * Sets or adds the reverse output MessageSinkForward 'sink'.
      * <p>
      * Implementing classes should state if single or multiple reverse outputs are supported.  If only a single reverse output is supported, then 'sink' is set as the sole output target.  If multiple reverse outputs are supported, then 'sink' is added to the set of reverse outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe.
      * 
      * @param sink
      *    for a single reverse output, sets the reverse output to 'forward'; for multiple reverse outputs, adds 'sink' to the set of outputs
      */
    public void attachMessageSinkForward( MessageSinkForward sink  ) {
        MessageBlock mb = MessageBlock.createVariantBasedMessageBlock( "com.kineticfire.patterns.reactor.messaging.request_attach_message_sink_forward" );
        mb.addVariantMessage( "target", sink );

        requestAttachMessageSinkForward( mb );
    }


    /**
      * Removes the reverse output MessageSinkForward 'sink'.
      * <p>
      * Implementing classes should state if single or multiple reverse outputs are supported.  If only a single reverse output is supported, then the reverse output is removed regardless of the value of 'sink' and may be null.  If multiple reverse outputs are supported, then 'sink' is removed from the set of reverse outputs.  No action is taken if 'sink' is not in the set of reverse outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe.
      * 
      * @param sink
      *    for a single reverse output, this value is ignored and may be null; for multiple reverse outputs, the reverse output to be removed from the set of output targets
      */
    public void detachMessageSinkForward( MessageSinkForward sink ) {
        // does not support detach
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
