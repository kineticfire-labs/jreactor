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


import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;

import com.kineticfire.patterns.reactor.InvalidInterestOpsException;
import com.kineticfire.patterns.reactor.InvalidResourceException;



/**
 * The ErrorSelector manages handles registered for error events. The selector
 * provides methods to register a handler to receive error events, change a
 * handle's interest operations for error events, and deregister a handle.
 * <p>
 * The ErrorSelector provides the capability to store error events. Error events
 * typically cannot be discarded since the event contains unique data such as
 * the command that caused the error and the resulting exception. Circumstances
 * that would cause an error event not to be dispatch include a handler that is
 * running or locked as well as incompatible interest operations.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

 


public class ErrorSelector implements SpecificSelector {

    //*********************************************
    //~ Instance/static variables
    private CompositeSelector selector;
    private Map<Handler, Handle> handlerHandleMap;
    private Map<Handle, List<Event>> handleEventMap;




       //*********************************************
       //~ Constructors


    /**
     * Constructs an empty ErrorSelector with a reference to the controlling
     * Selector.
     * 
     * @param selector
     *            reference to the Selector
     */
    public ErrorSelector( CompositeSelector selector ) {
        this.selector = selector;
        handlerHandleMap = new HashMap<Handler, Handle>( );
        handleEventMap = new HashMap<Handle, List<Event>>( );
    }




    //*********************************************
    //~ METHODS

    /**
     * Registers the handler to receive error events with the provided interest
     * operations and returns a handle representing relationship of handler, and
     * the interest operations. If the handler already has an associated handle
     * to receive error events, then that handle is returned and the interest
     * operations is updated.
     * <p>
     * PRECONDITION: handler is not null; interestOps is EventMask.{NOOP,ERROR}
     * 
     * @param handler
     *            handler to register to receive error events
     * @param interestOps
     *            interest operations to assign to this handler
     * @return a handle that represents the registration of this handler and the
     *         interest operations
     */
    public Handle register( Handler handler, int interestOps ) {
        
        Handle handle = null;
        
        if ( !handlerHandleMap.containsKey( handler ) ) {
    
            handle = new Handle( );
            handlerHandleMap.put( handler, handle );
            selector.processRegister( handle, handler, this, interestOps );
        } else {
            handle = handlerHandleMap.get( handler );
            interestOps( handle, interestOps );
        }
        
        return( handle );
    }

    
    /**
     * Creates a new error handle for a previously registered handler. Any
     * events stored for the handler are discarded. If the handler did not have
     * an error handle (or otherwise was not registered), then a new error
     * handle is created and returned with its interest operations set to
     * EventMask.ERROR. The latter operation has the same result as calling
     * register(Handler, EventMask.ERROR).
     * <p>
     * PRECONDITION: handler not null and registered
     * 
     * @param handler
     *            event handler to generate a new error handle
     * @return a new handle representing the registration of the handler and its
     *         interest operations
     */
    public Handle newErrorHandle( Handler handler ) {
        Handle handle = null;
        int ops;
        
        Handle oldHandle = handlerHandleMap.get( handler );
        if ( oldHandle != null ) {
            ops = selector.interestOps( oldHandle );
            deregister( oldHandle );
        } else {
            ops = EventMask.ERROR;
        }
        
        handle = register( handler, ops );
        
        return( handle );
    }
    
    
    /**
     * Creates a new handle for a previously registered handle. Any events
     * stored for the handle are discarded.
     * <p>
     * PRECONDITION: handle not null
     * 
     * @param handle
     *            handle to generate new handle
     * @return handle a new handle, old one discarded, representing registration
     *         of handler, interest operations, and implicit error event source
     * @throws InvalidResourceException
     *             if handle is not registered as an error handle
     */
    public Handle newErrorHandle( Handle handle ) {
        Handle newHandle = null;
        
        if ( handlerHandleMap.containsValue( handle ) ) {

            Handler handler = selector.handler( handle );
            int ops = selector.interestOps( handle );
            
            deregister( handle );
            
            newHandle = register( handler, ops );
        } else {
            throw( new InvalidResourceException( ) );
        }
        
        return( newHandle );
    }
    
    
    /**
     * Returns the error handle for the handler or null if the handler is not
     * registered or does not have an associated error handle.
     * <p>
     * PRECONDITION: handler not null
     *  
     * @param handler
     *            handler for which to retrieve error handle
     * @return error handle for the handler or null if the handler is not
     *         registered or does not have an associated error handle
     */
    public Handle getErrorHandle( Handler handler ) {
        //return( handlerHandleMap.get( handler ) );
        Handle handle = null;
        if ( handlerHandleMap.containsKey( handler ) ) {
            handle = handlerHandleMap.get( handler );
        }
        
        return( handle );
    }
    
    
    /**
     * Determines if the handler is currently registered to service ERROR
     * events.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            reference for which to request registration status
     * @return true if the handler is currently registered to service ERROR
     *         events and false otherwise
     */
    public boolean isRegistered( Handler handler ) {
        return( handlerHandleMap.containsKey( handler ) );
    }
    
    
    /**
     * Determines if the handle references a currently registered error event
     * handle.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference for which to request registration status
     * @return true if the handle is currently registered and is a handle for an
     *         error event source and false otherwise
     */
    public boolean isRegistered( Handle handle ) {
        return( handlerHandleMap.containsValue( handle ) );
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps.
     * <p>
     * PRECONDITION: handle is not null, handle is registered as an errorHandle
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not EventMask.{NOOP,ERROR}
     */
    public void interestOps( Handle handle, int interestOps ) {
        if ( EventMask.xIsError( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            
            if ( EventMask.xIsNoop( selector.interestOps( handle ) ) && EventMask.xIsError( interestOps ) ) {
                List<Event> errorEvents = handleEventMap.get( handle );
                if ( errorEvents != null ) {
                    for ( Event errorEvent : errorEvents ) {
                        selector.addReadyEvent( errorEvent );
                    }
                }
            }
            
            selector.processInterestOps( handle, interestOps );;
            
        } else {
            throw ( new InvalidInterestOpsException( interestOps ) );
        }
    }
    
    /**
     * Resume selection for the error handle. If there are stored ready error
     * events, then those events are added to the selector for dispatch.
     * 
     * @param handle
     *            the handle to resume selection and generate further ready
     *            events
     */
    public void resumeSelection( Handle handle ) {
        List<Event> errorEvents = handleEventMap.get( handle );
        if ( errorEvents != null ) {
            for ( Event errorEvent : errorEvents ) {
                selector.addReadyEvent( errorEvent );
            }
        }
    }
        
    
    /**
     * Provides notification that a handle with a ready event, e, was not
     * dispatched.
     * <p>
     * PRECONDITION: handle and e are not null; handle is registered as an
     * errorHandle
     * 
     * @param handle
     *            the handle referencing an event source whose ready event was
     *            not dispatched
     * @param e
     *            the event that was generated but not processed, associated to
     *            the handle
     */
    public void checkin( Handle handle, Event e ) {
        List<Event> events = handleEventMap.get( handle );
        
        if ( events == null ) {
            events = new LinkedList<Event>( );
            handleEventMap.put( handle, events );
        }
        
        events.add( e );        
    }
    
    
    /**
     * Deregisters the handle.
     * <p>
     * PRECONDITION: handle is not null; handle is registered as errorHandle
     * 
     * @param handle
     *            the handle to deregister
     */
    public void deregister( Handle handle ) {
        
        if ( handlerHandleMap.containsValue( handle ) ) {
            Handler handler = selector.handler( handle );
            
            handleEventMap.remove( handle );
            handlerHandleMap.remove( handler );
            selector.processDeregister( handle );
        }
    }
    
}
