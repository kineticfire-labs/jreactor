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
  * Defines the MessageComponent interface for a component that can be messaged with a MessageBlock.
  * 
  * @author Kris Hall
  * @version 07-15-2017
  */
public interface MessageComponent {


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
    public boolean messageComponent( MessageBlock mb );
    

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
    public boolean messageComponent( Collection<? extends MessageBlock> c );


    /**
      * Returns the ratio of the number of elements in the component to the capacity of the component on the range of [0.0,1.0].
      * 
      * @return the ratio, in the range of [0.0,1.0], of the number of elements in the component to the capacity of the component
      */
    public double messageComponentPressure( );

}
