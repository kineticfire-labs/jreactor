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


import java.util.Map;
import java.util.HashMap;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;

import com.kineticfire.patterns.reactor.DuplicateRegistrationException;
import com.kineticfire.patterns.reactor.InvalidInterestOpsException;


/**
 * This class implements a QueueSelector. The QueueSelector manages MessageQueue
 * resources, specifically registration, deregistration, setting interest
 * operations, and coordinating the between the CompositeSelector and the queue
 * to fire ready events.
 * <p>
 * A queue event source may only be registered once.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */


public class QueueSelector implements SpecificSelector {

    //*********************************************
    //~ Instance/static variables
    
    private CompositeSelector selector;
    private Map<Handle, MessageQueue> handleQueueMap;




       //*********************************************
       //~ Constructors

    
    /**
     * Constructs a QueueSelector with a null reference to the Selector.
     * 
     */
    public QueueSelector( ) {
        selector = null;
        handleQueueMap = new HashMap<Handle, MessageQueue>( );
    }
    
    
    /**
     * Constructs a QueueSelector with a reference to the Selector.
     * 
     * @param selector
     *            reference to the controlling Selector
     */
    public QueueSelector( CompositeSelector selector ) {
        this.selector = selector;
        handleQueueMap = new HashMap<Handle, MessageQueue>( );
    }




    //*********************************************
    //~ METHODS

    
    /**
     * Registers a queue event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event source and handler.
     * <p>
     * PRECONDITION: queue and handler not null; interestOps is
     * EventMask.{NOOP,QREAD}
     * 
     * @param queue
     *            event source to register
     * @param handler
     *            event handler to associate with the queue
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws DuplicateRegistrationException
     *             if the queue is already registered
     */
    public Handle register( MessageQueue queue, Handler handler, int interestOps ) {
        
        Handle handle = null;
        
        if ( !handleQueueMap.containsValue( queue ) ) {
            handle = new Handle( );
            queue.configure( this, handle );
            handleQueueMap.put( handle, queue );
            selector.processRegister( handle, handler, this, interestOps );
            checkNewReadEvent( queue, interestOps );
        } else {
            throw( new DuplicateRegistrationException( ) );
        }
        
        return( handle );
    }


    /**
     * Determines the registration status of the queue. Returns true if the
     * queue is currently registered and false otherwise.
     * <p>
     * PRECONDITION: queue is not null
     * 
     * @param queue
     *            event source for which to request registration status
     * @return true if the queue is currently registered and false otherwise
     */
    public boolean isRegistered( MessageQueue queue ) {
        return( handleQueueMap.containsValue( queue ) );
    }
    
    
    /**
     * Determines if the queueHandle references a currently registered queue
     * event source. Returns true if the queueHandle is currently registered and
     * is a handle for a queue event source and false otherwise.
     * <p>
     * PRECONDITION: queueHandle is not null
     * 
     * @param queueHandle
     *            reference for which to request registration status
     * @return true if the queueHandle is currently registered and is a handle
     *         for a queue event source and false otherwise
     */
    public boolean isRegistered( Handle queueHandle ) {
        return( handleQueueMap.containsKey( queueHandle ) );
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps.
     * <p>
     * PRECONDITION: handle is not null, handle is registered as queueHandle
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not EventMask.{NOOP,QREAD}
     */
    public void interestOps( Handle handle, int interestOps ) {
        if ( EventMask.xIsNoop( interestOps ) || EventMask.xIsQRead( interestOps ) ) {
            checkReadEvent( handle, interestOps ); // looks for change 0 --> 1
            selector.processInterestOps( handle, interestOps );
        } else {
            throw ( new InvalidInterestOpsException( interestOps ) );
        }        
    }
    
    
    /**
     * Deregisters the queue event source referenced by handle. The queue will
     * no longer generate ready events
     * <p>
     * When this method completes, the MessageQueue referenced by handle will
     * have null values for its QueueSelector and Handle references and the
     * handle will be removed from the QueueSelector and Selector.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle to deregister
     */
    public void deregister( Handle handle ) {

         if ( handleQueueMap.containsKey( handle ) ) {
             MessageQueue queue = handleQueueMap.remove( handle );
            queue.configure( null, null );
            selector.processDeregister( handle );
         }
    }    
    
    
    /**
     * Provides notification that a handle with a ready event was not
     * dispatched.
     * <p>
     * This method takes no action and is here for compatibility with the
     * com.kineticfire.patterns.reactor.CompositeSelector interface.
     * 
     * @param handle
     *            handle, referencing a registered queue, with a ready event to
     *            be checked-in
     * @param event
     *            the event generated by the event source
     */
    public void checkin( Handle handle, Event event ) {
        // do nothing
    }
        
    
    /**
     * Resumes readiness selection on the queue event source referenced by
     * handle. That is, the queue referenced by handle will now fire ready
     * events that will be serviced by the assigned event handler.
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            handle that references queue event source for which to
     *            re-enable to fire ready events
     */
    public void resumeSelection( Handle handle ) {
        MessageQueue queue = handleQueueMap.get( handle );
        if ( EventMask.xIsQRead( selector.interestOps( handle ) ) ) {
            queue.checkEvent( );
        }    
    }
    
    

    /**
     * Coordinates with a newly registered queue to fire a ready event,
     * EventMask.QREAD, if the interest operations indicate interest in the
     * queue--e.g. EventMask.QREAD--and queue contains data.
     * <p>
     * This method should only be used for a queue that is in the process of
     * being registered.
     * <p>
     * PRECONDITION: queue is not null, interestOps is in the set
     * EventMask.{NOOP,QREAD}.
     * 
     * @param queue
     *            newly registered queue to check for interest operations and
     *            data ready to fire a ready event
     * @param interestOps
     *            interest operations that are set for this queue; the queue is
     *            checked for data only if the interest operations are
     *            EventMask.QREAD
     */
    private void checkNewReadEvent( MessageQueue queue, int interestOps ) {
        // queue in process of being registered
        if ( EventMask.xIsQRead( interestOps ) ) {
            queue.checkEvent( );
        }
    }
    
     
    /**
     * Coordinates with a queue to fire a ready event, EventMask.QREAD. A ready
     * event is fired for the queue if the interest operations are changing from
     * EventMask.NOOP to EventMask.QREAD and the queue has data ready.
     * <p>
     * This method is called when the interest operations are being changed for
     * a queue. The method must be called before the new interest operations are
     * committed.
     * <p>
     * PRECONDITION: handle is not null, handle references a registered queue,
     * interestOps is in the set EventMask.{NOOP,QREAD}.
     * 
     * @param queue
     *            newly registered queue to check for interest operations and
     *            data ready to fire a ready event
     * @param interestOps
     *            interest operations that are set for this queue; the queue is
     *            checked for data only if the interest operations are changing
     *            from EventMask.NOOP to EventMask.QREAD
     */
    private void checkReadEvent( Handle handle, int interestOps ) {
        // queue already registered, interestOps changing
        if ( EventMask.xIsNoop( selector.interestOps( handle ) ) && EventMask.xIsQRead( interestOps ) ) {
            MessageQueue queue = handleQueueMap.get( handle );
            queue.checkEvent( );
        }
    }
    
    
    /**
     * Adds an event to the QueueSelector for processing.
     * <p>
     * PRECONDITION: e is not null
     * 
     * @param e
     *            event to add to reactor for processing.
     */
    protected void addReadyEvent( Event e ) {
           selector.addReadyEvent( e );
       }
        
}
