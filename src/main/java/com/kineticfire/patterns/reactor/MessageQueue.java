package com.kineticfire.patterns.reactor;

/**
 * Copyright 2010 KineticFire.
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

import com.kineticfire.patterns.reactor.ReactorQueue;
import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

import java.util.Collection;
import java.lang.NullPointerException;
import java.lang.ClassCastException;
import java.lang.IllegalArgumentException;


/**
 * Defines the interface and behavior for a MessageQueue. A MessageQueue
 * supports readiness selection for a queue data structure. The MessageQueue
 * natively operates on MessageBlock elements. The MessageQueue provides typical
 * Queue methods for adding and removing data from the Queue.
 * <p>
 * Implementing classes should be thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */
public interface MessageQueue extends ReactorQueue {


    /**
     * Retrieves and removes the head of this queue or null if the queue is
     * empty.
     * 
     * @return the head of this queue or null if the queue is empty
     */
    public MessageBlock poll();
    
    
    /**
     * Retrieves but does not remove the head of this queue or null if the queue
     * is empty.
     * 
     * @return the head of this queue or null if the queue is empty
     */
    public MessageBlock peek( );
    
    
    /**
     * Removes all the elements from the queue. The queue is empty when the
     * method returns.
     * 
     */
    public void clear( );


    /**
      * Adds the MessageBlock to the queue. Returns true if the item was
      * successfully added or false otherwise.
      * 
      * @param mb
      *            MessageBlock data to add to the queue
      * @return true if the item was successfully added or false otherwise
      * @throws ClassCastException
      *             if the data is not a MessageBlock
      * @throws NullPointerException
      *             if the data is null
      * @throws ClassCastException
      *            if the class of the element prevents its addition into the
      *            queue
      * @throws IllegalArgumentException
      *             if some property of this element prevents it from being added
      *             to the queue
      */
    public boolean offer( MessageBlock mb );
    

    /**
      * Adds all elements in the collection 'c' to the queue.  Returns true if the items were successfully added or false otherwise.
      * 
      * @param c
      *    the collection to add
      * @return true if the collection were successfully added or false otherwise
      * @throws ClassCastException
      *             if the data is not a MessageBlock
      * @throws NullPointerException
      *             if the data is null
      * @throws ClassCastException
      *            if the class of the element prevents its addition into the
      *            queue
      * @throws IllegalArgumentException
      *             if some property of this element prevents it from being added
      *             to the queue
      */
    public boolean addAll( Collection<? extends MessageBlock> c );

    
    /**
     * Determines if the queue is empty. Returns true if the queue is empty and
     * false if the queue contains data.
     * <p>
     * This method is equivalent to !hasData().
     * 
     * @return true if the queue is empty and false otherwise
     */
    public boolean isEmpty( );
    
    
    /**
     * Determines if the queue contains data. Returns true if the queue contains
     * data and false otherwise.
     * <p>
     * This method is equivalent to !isEmpty().
     * 
     * @return true if the queue contains data and false otherwise
     */
    public boolean hasData( );
    
    
    /**
     * Returns the size, as a count of the number of elements, contained in the
     * queue.
     * 
     * @return a count of the number of elements in the queue
     */
    public int size( );

    
    /**
     * Returns the number of additional elements the queue can hold before
     * reaching capacity.
     * 
     * @return the number of additional elements the queue can hold before
     *         reaching capacity
     */
    public int remainingCapacity( );

    
    /**
     * Returns the ratio of the number of elements in the queue to the capacity
     * of the queue. The ratio will be in the range of [0.0,1.0].
     * 
     * @return the ratio, in the range of [0.0,1.0], of the number of elements in
     *         the queue to the capacity of the queue
     */
    public double pressure( );    

}
