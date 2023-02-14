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

import com.kineticfire.patterns.reactor.RunLockAuthority;
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.Handler;

/**
 * This class tracks running and locked Handlers and maps the Handlers to their
 * HandleAdapters. The 'running' and 'locked' state of a Handler may be queried
 * and used to determine what operations are permissable for the Handler. The
 * RunLockHandlerTracker can provide the HandlerAdapter for a given Handler in
 * order to queue messages for later processing.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class RunLockHandlerTracker implements RunLockAuthority {

    //*********************************************
    //~ Instance/static variables
    
    private enum ActiveState { RUNNING, LOCKED };
    
    private Map<Handler, ActiveState> handlerState; 
    private Map<Handler, HandlerAdapter> handlerAdapter;


       //*********************************************
       //~ Constructors


       /**
       * Constructs an empty RunLockHandlerAdapter.
       *
       */
       public RunLockHandlerTracker( ) {
           handlerState = new HashMap<Handler, ActiveState>( );
        handlerAdapter = new HashMap<Handler, HandlerAdapter>( );
       }



       //*********************************************
       //~ METHODS

       
    /**
     * Records the handler as 'running' and maps the handler to the adapter.
     * <p>
     * PRECONDITION: handler and adapter are not null
     * 
     * @param handler
     *            handler to mark as 'running'
     * @param adapter
     *            the adapter to associate with handler
     */
    public void submitRunning( Handler handler, HandlerAdapter adapter ) {
        handlerState.put( handler, ActiveState.RUNNING );
        handlerAdapter.put( handler, adapter );
    }

    
    /**
     * Returns true if the handler is recorded as 'running' and false otherwise.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            the handler to query if it is recorded as 'running'
     * @return true if a handler is recorded as 'running' and false otherwise.
     */
       public boolean isRunning( Handler handler ) {
           boolean isRunning = false;
           
           if ( handlerState.containsKey( handler ) ) {
               isRunning = handlerState.get( handler ) == ActiveState.RUNNING;
           }
           
        return( isRunning );
    }
       

    /**
     * Records the handler as 'locked' and maps the handler to the adapter.
     * <p>
     * PRECONDITION: handler and adapter are not null
     * 
     * @param handler
     *            handler to mark as 'locked'
     * @param adapter
     *            the adapter to associate with handler
     */
    public void submitLocked( Handler handler, HandlerAdapter adapter ) {
        handlerState.put( handler, ActiveState.LOCKED );
        handlerAdapter.put( handler, adapter );
    }

    
    /**
     * Returns true if the handler is recorded as 'locked' and false otherwise.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            the handler to query if it is recorded as 'locked'
     * @return true if a handler is recorded as 'running' and false otherwise.
     */
       public boolean isLocked( Handler handler ) {
           boolean isLocked = false;
           
           if ( handlerState.containsKey( handler ) ) {
               isLocked = handlerState.get( handler ) == ActiveState.LOCKED;
           }
           
        return( isLocked );
    }

    
       /**
     * Determines if the handle is active--e.g. either running or locked--or if
     * the handler is open--e.g. neither running nor locked. True is returned if
     * the handler is active and false otherwise.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            handler to query active status
     * @return true is returned if the handler is active and false otherwise
     */
    public boolean isActive( Handler handler ) {
        return( handlerState.containsKey( handler ) );
    }
    
    
    /**
     * Returns a reference to the HandlerAdapter that is mapped from a 'running'
     * or 'locked' handler. If the handler is not contained in the data
     * structure, then null is returned.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            'running' or 'locked' handler whose HandlerAdapter is desired
     * @return HandlerAdapter for handler or null if handler is not contained in
     *         the data structure
     */
    public HandlerAdapter getHandlerAdapter( Handler handler ) {
        return( handlerAdapter.get( handler ) );
    }
    
    
    /**
     * Removes a 'running' or 'locked' handler and returns its mapped
     * HandlerAdapter from the data structure. If the object does not contain
     * the handler, then no action is taken and 'null' is returned.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            the 'running' or 'locked' handler to remove from the data
     *            structure plus its mapped HandlerAdapter
     * @return the HandlerAdapter corresponding to handler or null if the
     *         handler was not contained in this data structure
     */
    public HandlerAdapter remove( Handler handler ) {
        handlerState.remove( handler );
        return( handlerAdapter.remove( handler ) );
    }
     
}
