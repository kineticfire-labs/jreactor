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

import java.util.Collection;
import java.util.concurrent.PriorityBlockingQueue;

import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.MessageBlockPriorityComparator;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;

import java.lang.NullPointerException;
import java.lang.ClassCastException;
import java.lang.IllegalArgumentException;

/**
 * Implements an unbounded priority queue data structure that supports readiness
 * selection and adheres to the MessageQueue interface. The
 * PriorityBlockingMessageQueue natively operates on MessageBlock elements. The
 * PriorityBlockingMessageQueue provides typical Queue methods for adding and
 * removing data from the Queue.
 * <p>
 * Messageblocks in the queue will be ordered such that message blocks with the
 * lowest priority are at the head of the queue. Ordering between message blocks
 * with the same priority is not guaranteed.
 * <p>
 * Th PriorityLinkedBlockingMessageQueue is always unbounded, despite specifying
 * a capacity. The notion of capacity is used so that the value from method
 * remainingCapacity() and the 'pressure' value returned from methods
 * offerPressure(MessageBlock) and pressure() are accurate and consistant with
 * the MessageQueue interface.
 * <p>
 * This class is thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */


public class PriorityLinkedBlockingMessageQueue implements MessageQueue {

    //    *********************************************
    //~ Instance/static variables
    
    private volatile QueueSelector queueSelector;
    private Handle handle;
    private PriorityBlockingQueue<MessageBlock> q;
    private int capacity;


    
    //    *********************************************
       //~ Constructors
    
    
    /**
     * Creates a new priority queue with an initial capacity of
     * 11. Message blocks in the queue will be ordered such that
     * message blocks with the lowest priority are at the head of the queue.
     * Ordering between message blocks with the same priority is not guaranteed.
     * 
     */
    public PriorityLinkedBlockingMessageQueue( ) {
        queueSelector = null;
        handle = null;
        capacity = 11;
        q = new PriorityBlockingQueue<MessageBlock>( 11, new MessageBlockPriorityComparator( ) );
    }

    /**
     * Creates a new queue with an initial capacity of 'capacity.' Message
     * blocks in the queue will be ordered such that message blocks with the
     * lowest priority are at the head of the queue. Ordering between message
     * blocks with the same priority is not guaranteed.
     * 
     * @param initialCapacity
     *            the initialCapacity to set for the priority queue
     */
    public PriorityLinkedBlockingMessageQueue( int initialCapacity ) {
        queueSelector = null;
        handle = null;
        q = new PriorityBlockingQueue<MessageBlock>( initialCapacity, new MessageBlockPriorityComparator( ) );
        capacity = initialCapacity;
    }

    
    //    *********************************************
    //~ Methods

    
        
    /**
     * Adds the MessageBlock to the queue according to the priority ordering
     * scheme. Returns true if the item was successfully added or false
     * otherwise.
     * 
     * @param mb
     *            MessageBlock data to add to the queue, placed in the queue
     *            according to the priority ordering scheme
     * @return true if the item was successfully added or false otherwise
     * @throws ClassCastException
     *             if the data is not a MessageBlock
     * @throws NullPointerException
     *             if the data is null
     * @throws IllegalArgumentException
     *             if some property of this element prevents it from being added
     *             to the queue
     */
    public boolean offer( MessageBlock mb  ) {
        boolean success = q.offer( mb );        
        
        if ( success ) {
            triggerEvent( );
        }
        
        return( success );
    }
    

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
    public boolean addAll( Collection<? extends MessageBlock> c ) {
        boolean success = true;

        if ( !c.isEmpty( ) ) {
            try {
                q.addAll( c );
            } catch ( IllegalStateException e ) {
                success = false;
            }

            triggerEvent( );
        }
        
        return( success );
    }
    
    
    /**
     * Retrieves and removes the head of this queue or null if the queue is
     * empty.
     * 
     * @return the head of this queue or null if the queue is empty
     */
    public MessageBlock poll( ) {
        return( q.poll( ) );    
    }
    

    /**
     * Retrieves but does not remove the head of this queue or null if the queue
     * is empty.
     * 
     * @return the head of this queue or null if the queue is empty
     */
    public MessageBlock peek( ) {
        return( q.peek( ) );
    }
    
    
    /**
     * Removes all the elements from the queue. The queue is empty when the
     * method returns.
     * 
     */
    public void clear( ) {
        q.clear( );
    }

    
    /**
     * Determines if the queue is empty. Returns true if the queue is empty and
     * false if the queue contains data.
     * 
     * @return true if the queue is empty and false otherwise
     */
    public boolean isEmpty( ) {
        return( q.isEmpty( ) );
    }

    
    /**
     * Determines if the queue contains data. Returns true if the queue contains
     * data and false otherwise.
     * 
     * @return true if the queue contains data and false otherwise
     */
    public boolean hasData( ) {
        return( !q.isEmpty( ) );
    }
    
    
    /**
     * Returns the size, as a count of the number of elements, contained in the
     * queue.
     * 
     * @return a count of the number of elements in the queue
     */
    public int size( ) {
        return( q.size( ) );
    }

    
    /**
     * Returns the number of additional elements the queue can hold before
     * reaching capacity. Since this data structure is unbounded,
     * Integer.MAX_VALUE elements can be held despite the value of capacity.
     * Thus, the returned value will not be [0,capacity] but rather
     * [(capacity-Integer.MAX_VALUE),capacity].
     * 
     * @return the number of additional elements the queue can hold before
     *         reaching capacity
     */
    public int remainingCapacity( ) {
        return( capacity - q.size( ) );
    }
    

    /**
     * Returns the ratio of the number of elements in the queue to the capacity
     * of the queue. The ratio will be in the range of [0.0,1.0].
     * 
     * @return the ratio, in the range of [0.0,1.0], of the number of elements
     *         in the queue to the capacity of the queue
     */
    public double pressure( ) {
        double ans = Math.min( ( (double)q.size( )/(double)capacity ), 1.0 );
        return( ans );
    }
    
    
    /**
     * Generates a ready event and submits the event to the QueueSelector. If the
     * QueueSelector reference is null, then the event is discarded.
     * <p>
     * This method should only be used by the Reactor.
     * <p>
     * This method is not thread-safe.
     * 
     */
    private void triggerEvent( ) {        
        try {
            queueSelector.addReadyEvent( new Event( handle, EventMask.QREAD ) );
        } catch ( NullPointerException e ) { }
    }
    
    
    /**
     * Fires a ready event, specifically EventMask.QREAD, if the queue contains
     * data.
     * <p>
     * This method should only be used by the QueueSelector.
     */
    public void checkEvent( ) {
        if ( hasData( ) ) {
            triggerEvent( );
        }
    }
    
        
    /**
     * Sets the QueueSelector and handle for the queue. The queue requires the
     * QueueSelector reference in order to submit ready events for
     * demultiplexing and dispatch; the reference to the handle is needed to
     * create ready events.
     * <p>
     * This method should only be used by the QueueSelector.
     * 
     * @param queueSelector
     *            reference to the JReactor for submitting ready events
     * @param handle
     *            reference to handle in order to create ready events
     */
    public void configure( QueueSelector queueSelector, Handle handle ) {
        this.queueSelector = queueSelector;
        this.handle = handle;
    }
    
}
