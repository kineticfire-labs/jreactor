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


import java.util.Collection;

import com.kineticfire.patterns.reactor.MessageBlock;



/**
  * Defines the MessageSink interface. The interface facilitates message passing among objects by establishing common methods and behavior for interaction between a source of messages and one or more consumers of messages.
  * <p>
  * A MessageSink receives data, as MessageBlock objects, from a MessageSource.  The MessageSink provides a method that returns a reference to its target input queue.
  * <p>
  * This interface typically applies to pipeline processing architecture (e.g. one-way data processing).
  * <p>
  * A sink can be MessageSource, MessageSink, or both.
  * <p>
  * It is recommended that the implementing class process immediately the methods defined by this interface if these can be used without concern for race conditions prior to enabling the sink.  When the sink is running, messages should be used to avoid race conditions.  Messages can be transferred out-of-band by another mechanism such as through the com.kineticfire.patterns.reactor.messaging.MessageComponent interface.
  * 
  * @author Kris Hall
  * @version 08-20-2016
  */
public interface MessageSink {


    /**
      * Sends a message to this sink.
      * <p>
      * The message is queued by the sink.  Throws a NullPointerException if the serving queue is unavailable (e.g. null) such as indicating that the sink has shutdown.
      * <p>
      * The sink's class documentation must describe messages handled by this sink.
      *
      * @param mb
      *    message to send
      * @return true if the message were added and false if the message were rejected
      */
    public boolean sinkMessage( MessageBlock mb );
    

    /**
      * Adds all elements in the collection 'c' to the sink.  Returns true if the items were successfully added or false otherwise.
      * 
      * @param c
      *    the collection to add
      * @return true if the collection were successfully added or false otherwise
      * @throws ClassCastException
      *    if the data is not a MessageBlock
      * @throws NullPointerException
      *    if the data is null
      * @throws ClassCastException
      *    if the class of the element prevents its addition into the sink 
      * @throws IllegalArgumentException
      *    if some property of this element prevents it from being added to the sink
      */
    public boolean sinkMessage( Collection<? extends MessageBlock> c );


    /**
      * Returns the ratio of the number of elements in the sink to the capacity of the sink on the range of [0.0,1.0].
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of elements in the sink to the capacity of the sink
      */
    public double sinkMessagePressure( );

}
