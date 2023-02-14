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



import com.kineticfire.patterns.reactor.Reactor;

import com.kineticfire.patterns.reactor.net.TcpConnection;

import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.messaging.MessageSinkForward;
import com.kineticfire.patterns.reactor.messaging.MessageSourceReverse;
import com.kineticfire.patterns.reactor.messaging.MessageSinkReverse;




/**
  * Defines an LmTcpConnection, suitable for a message-oriented TCP connection.
  * <p>
  * LmTcpConnection provides a message-oriented, TCP communication mechanism. Data is sent as a primitive byte array; received data is fired as an event containing the received data as a primitive byte array.  Data received from the remote side is provided only when an entire message is available.  Framing is applied to the data when sending; receive processing removes the framing applied by the remote endpoint.  Messages may be from 1 to 65,537 bytes in length inclusive (excluding the framing), due to the framing mechanism.
  * <p>
  * The messages exchanged are of the form: &lt;length&gt;&lt;message&gt;.  The 'length' is a two-byte, big-endian unsigned integer giving the number of bytes of the message minus one; that is, a 'length' of 0 is 1 byte, 1 is 2 bytes, and so on.  Message is a primitive byte array and sent and received as an atomic unit.
  * <p>
  * At initialization, the LmTcpConnection is prepared to send and receive data.  Note that received data will be queued until the recipient of recevied data is defined.  By implementing the MessageTRX interface, separate MessageSource and MessageSink objects may be retrieved.  The MessageSink can be used to supply data to send to the remote endpoint.  The MessageSource would be needed to set the target MessageSink.
  *
  * @author Kris Hall
  * @version 05-29-2017
  */
public class LmTcpConnection implements MessageSinkForward, MessageSourceReverse {

    private LmTcpConnectionForward forward;
    private LmTcpConnectionReverse reverse;




    /**
      * Creates a LmTcpConnection, based on an established TcpConnection.
      *
      * @param reactor
      *    reference to the controlling Reactor
      * @param tcpConnection
      *    reference to an establish TcpConnection
      */
    public LmTcpConnection( Reactor reactor, TcpConnection tcpConnection ) {
        this( reactor, tcpConnection, null, null );
    }


    /**
      * Creates a LmTcpConnection, based on an established TcpConnection.
      *
      * @param reactor
      *    reference to the controlling Reactor
      * @param tcpConnection
      *    reference to an establish TcpConnection
      */
    public LmTcpConnection( Reactor reactor, TcpConnection tcpConnection, String name, String id ) {

        forward = new LmTcpConnectionForward( reactor, tcpConnection );

        reverse = new LmTcpConnectionReverse( reactor, forward, tcpConnection );

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
        return( forward.sinkMessageForward( mb ) );
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
        return( forward.sinkMessageForward( c ) );
    }


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the forward direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkMessageForwardPressure( ) {
        return( forward.sinkMessageForwardPressure( ) );
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
        return( forward.sinkOobMessageForward( mb ) );
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
        return( forward.sinkOobMessageForward( c ) );
    }


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the forward direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkOobMessageForwardPressure( ) {
        return( forward.sinkOobMessageForwardPressure( ) );
    }
    

    /**
      * Sets or adds the reverse output MessageForward 'sink'.
      * <p>
      * Implementing classes should state if single or multiple reverse outputs are supported.  If only a single reverse output is supported, then 'sink' is set as the sole output target.  If multiple reverse outputs are supported, then 'sink' is added to the set of reverse outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe.
      * 
      * @param sink
      *    for a single reverse output, sets the reverse output to 'forward'; for multiple reverse outputs, adds 'sink' to the set of outputs
      */
    public void attachMessageSinkReverse( MessageSinkReverse sink  ) {
        reverse.attachMessageSinkReverse( sink );
    }

    
    /**
      * Removes the reverse output MessageForward 'sink'.
      * <p>
      * Implementing classes should state if single or multiple reverse outputs are supported.  If only a single reverse output is supported, then the reverse output is removed regardless of the value of 'sink' and may be null.  If multiple reverse outputs are supported, then 'sink' is removed from the set of reverse outputs.  No action is taken if 'sink' is not in the set of reverse outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe.
      * 
      * @param sink
      *    for a single reverse output, this value is ignored and may be null; for multiple reverse outputs, the reverse output to be removed from the set of output targets
      */
    public void detachMessageSinkReverse( MessageSinkReverse sink ) {
        reverse.detachMessageSinkReverse( sink );
    }

}
