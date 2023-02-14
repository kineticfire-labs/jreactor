package com.kineticfire.patterns.reactor.messaging;

/**
 * Copyright 2016 KineticFire.
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


import com.kineticfire.patterns.reactor.messaging.MessageSink;


/**
  * Defines the MessageSource interface. The interface facilitates one-way message passing among objects by establishing common methods and behavior for interaction between a source of messages and one or more consumers of messages.
  * <p>
  * A MessageSource sends data as messages, defined as a MessageBlock, to one or more attached MessageSink components.
  * <p>
  * This interface typically applies to pipeline processing architectures (e.g. one-way data processing).
  * <p>
  * A component can be MessageSource, MessageSink, or both.
  * <p>
  * A class that implements this inteface must:
  * <ol>
  *   <li>specify if the methods attachMessageSink(MessageSink) and detachMessageSink(MessageSink) are thread-safe and if the change is immediate or queued</li>
  *   <li>specify if the class supports single or multiple output MessageSink components, as added by the method attachMessageSink(MessageSink)</li>
  *   <li>specify if the behavior if no output AccessibleMessageQueue is attached; for example, messages at the source could be dropped or queued</li>
  * </ol>
  * <p>
  * It is recommended that the implementing class process immediately the methods defined by this interface if these can be used without concern for race conditions prior to enabling the component.  When the component is running, messages should be used to avoid race conditions.  Messages can be transferred out-of-band by another mechanism such as through the com.kineticfire.patterns.reactor.messaging.MessageComponent interface.
  * 
  * @author Kris Hall
  * @version 08-20-16
  */
public interface MessageSource {

    /**
      * Sets or adds the output MessageSink 'sink'.
      * <p>
      * Implementing classes should state if single or multiple output MessageSink components are supported.  If only a single MessageSink is supported, then 'sink' is set as the sole output target.  If multiple outputs are supported, then 'sink' is added to the set of outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe and if the change is immediate or queued.
      * 
      * @param sink
      *    for a single output, sets the output to 'sink'; for multiple outputs, adds 'sink' to the set of outputs
      */
    public void attachMessageSink( MessageSink sink  );

    
    /**
      * Removes the output MessageSink 'sink'.
      * <p>
      * Implementing classes should state if single or multiple output MessageSink components are supported.  If only a single MessageSink is supported, then the output is removed regardless of the value of 'sink'; the value may be null.  If multiple outputs are supported, then 'sink' is removed from the set of outputs.  No action is taken if 'sink' is not in the set of outputs.
      * <p>
      * Implementing classes should state if this method is thread-safe and if the change is immediate or queued.
      * 
      * @param sink
      *    for a single output, this value is ignored and may be null; for multiple outputs, the sink is removed from the set of outputs
      */
    public void detachMessageSink( MessageSink sink );

}
