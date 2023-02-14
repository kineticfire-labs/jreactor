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



import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.messaging.MessageSinkReverse;



/**
  * Defines the MessageSourceReverse interface. The interface facilitates bi-directional message passing among objects by establishing common methods and behavior for interaction between a source of messages and one or more consumers of messages.
  * <p>
  * A MessageSourceReverse sends data as messages, defined as MessageBlocks, in the reverse direction to one or more attached MessageSinkReverse components.
  * <p>
  * This interface typically applies to bi-directional processing flows.  'Forward' and 'Reverse', as used in the interface and method names, refer arbitrarily to the direction of the dataflow.  The programmer defines the meaning of the names.  Typically, the core of the application would send in the forward direction and receive in the reverse direction.  An example of such an application is a network socket where the application core sends data to the remote server in the forward direction via the socket and recevies data from the socket in the reverse direction.
  * <p>
  * A component may implement one or more of MessageSourceForward, MessageSinkForward, MessageSourceReverse, or MessageSinkReverse.
  * <p>
  * A class that implements this inteface must:
  * <ol>
  *   <li>specify if the methods attachMessageSinkReverse(MessageSinkReverse) and detachMessageSinkReverse(MessageSinkReverse) are thread-safe and if the change is immediate or queued</li>
  *   <li>specify if the class supports single or multiple output MessageSinkReverse objects, as added by the method attachMessageSinkReverse(MessageSinkReverse) or similar message</li>
  *   <li>specify if the behavior if no output MessageSinkReverse output is attached; for example, sourced messages could be dropped or queued</li>
  * </ol>
  * <p>
  * It is recommended that the implementing class process immediately the methods defined by this interface if these can be used without concern for race conditions prior to enabling the component.  When the component is running, messages should be used to avoid race conditions.  Messages can be transferred out-of-band using 'oob' type messaging defined by MessageSinkReverse or by another mechanism such as through the com.kineticfire.patterns.reactor.messaging.MessageComponent interface.
  *
  * @author Kris Hall
  * @version 6-6-2017
  */
public interface MessageSourceReverse {
    

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
    public void attachMessageSinkReverse( MessageSinkReverse sink  );

    
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
    public void detachMessageSinkReverse( MessageSinkReverse sink );

}
