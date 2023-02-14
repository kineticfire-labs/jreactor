package com.kineticfire.patterns.reactor.messaging;

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

import com.kineticfire.patterns.reactor.MessageBlock;



/**
  * Defines the MessageSinkForwad interface. The interface facilitates bi-directional message passing among objects by establishing common methods and behavior for interaction between a source of messages and one or more consumers of messages.
  * <p>
  * A MessageSinkForward receives data as messages, defined as MessageBlocks, in the forward direction from one or more MessageSourceForward components to which it is attached.
  * <p>
  * This interface typically applies to bi-directional processing flows.  'Forward' and 'Reverse', as used in the interface and method names, refer arbitrarily to the direction of the dataflow.  The programmer defines the meaning of the names.  Typically, the core of the application would send in the forward direction and receive in the reverse direction.  An example of such an application is a network socket where the application core sends data to the remote server in the forward direction via the socket and recevies data from the socket in the reverse direction.
  * <p>
  * A component may implement one or more of MessageSourceForward, MessageSinkForward, MessageSourceReverse, or MessageSinkReverse.
  * <p>
  * Messages can be transferred out-of-band using 'oob' type messaging defined by this interface or by another mechanism such as through the com.kineticfire.patterns.reactor.messaging.MessageComponent interface.
  *
  * @author Kris Hall
  * @version 6-6-2017
  */
public interface MessageSinkForward {
    

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
    public boolean sinkMessageForward( MessageBlock mb );
    

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
    public boolean sinkMessageForward( Collection<? extends MessageBlock> c );


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the forward direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkMessageForwardPressure( );


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
    public boolean sinkOobMessageForward( MessageBlock mb );
    

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
    public boolean sinkOobMessageForward( Collection<? extends MessageBlock> c );


    /**
      * Returns the ratio of the number of messages in the component to the message capacity of the component on the range of [0.0,1.0] in the forward direction.
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of messages in the component to the message capacity of the component
      */
    public double sinkOobMessageForwardPressure( );

}
