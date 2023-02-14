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


import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.kineticfire.patterns.reactor.EventGenerationState;
import com.kineticfire.patterns.reactor.Handler;

/**
 * A LockHandlerGroup manages a set of event handlers and tracks the lock
 * state--open, pending, or locked--of both the individual handlers and the
 * composite group. The LockHandlerGroup also tracks the state of event generation
 * resulting from the successful locking of the group.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */


public class LockHandlerGroup {

    //*********************************************
    //~ Instance/static variables
    
    private enum LockState { OPEN, PENDING, LOCKED };
    
    private Map<Handler, LockState> handlerState;    
    private EventGenerationState eventGenerationState;
    
    
       //*********************************************
       //~ Constructors
    
    
    /**
     * Creates a new LockHandlerGroup. The group contains no members. The initial
     * group state is 'open' and set as not having fired a ready event.
     */
    public LockHandlerGroup( ) {
        handlerState = new HashMap<Handler,LockState>( );
        eventGenerationState = EventGenerationState.NONE;
    }
    
    
       //*********************************************
       //~ METHODS
    
    /**
     * Adds the managedHandler into the group. The handler is set as 'open'. No
     * action is taken if the managedHandler is already a member of the group.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            the handler to add to the group
     */
    public void addMember( Handler managedHandler ) {
        handlerState.put( managedHandler, LockState.OPEN );
    }
    
    

    /**
     * Removes the managedHandler from the group. No action is taken if the
     * managedHandler is not a member of the group.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            the handler to remove from the group
     */ 
    public void removeMember( Handler managedHandler ) {
        handlerState.remove( managedHandler );
    }
    
    
    /**
     * Returns a Set of all the pending handlers in the group. If the group is
     * empty, then an empty Set is returned.
     * 
     * @return a Set of all pending handlers in the group; an empty Set is
     *         returned if the group is empty
     */ 
    public Set<Handler> getPendingHandlers( ) {
        Set<Handler> pendingHandlers = new HashSet<Handler>( );
        Set<Handler> handlers= handlerState.keySet( );
        
        for( Handler handler : handlers ) {
            if ( handlerState.get( handler ) == LockState.PENDING ){
                pendingHandlers.add( handler );
            }
        }
        
        return( pendingHandlers );
    }
    
    
        
    /**
     * Returns a set of all handlers that are members of this group.
     * 
     * @return a set of all handlers that are members of this group; an empty
     *         group is returned if this data structure is empty
     */    
    public Set<Handler> getHandlers( ) {
        Set<Handler> managedHandlers = new HashSet<Handler>( );
        managedHandlers.addAll( handlerState.keySet( ) );
        
        return( managedHandlers );
    }
    

    /**
     * Queries the membership status of the managedHandler with this group.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            the handler to query membership status
     * @return true if the handler is a meber of the group and false otherwise
     */
    public boolean isMember( Handler managedHandler ) {
        return( handlerState.containsKey( managedHandler ) );
    }
    
    
    /**
     * Queries if the group contains member handlers.
     * 
     * @return true if the group does not contain member handlers and false otherwise
     */
    public boolean isEmpty( ) {
        return( handlerState.isEmpty( ) );
    }
    
    
    /**
     * Determines if the group is in a 'lock' status. A group is locked when the
     * group contains at least one managed handler that is locked and does not
     * contain any 'open' or 'pending' handlers.
     * 
     * @return true if the group is locked and false otherwise; false is
     *         returned if the group is empty
     */
    public boolean isLocked( ) { 
        return( !handlerState.containsValue( LockState.OPEN ) && !handlerState.containsValue( LockState.PENDING ) && handlerState.containsValue( LockState.LOCKED ) );
    }
    
    
    /**
     * Determines if the group is in a 'pending' status. A group is pending when
     * the group contains at least one managed handler that is pending.
     * 
     * @return true if the group is pending and false otherwise; false is
     *         returned if the group is empty
     */
    public boolean isPending( ) {
        return( handlerState.containsValue( LockState.PENDING ) && !handlerState.isEmpty( ) );
    }
    
    
    /**
     * Determines if the group is in an 'open' status. A group is open when the
     * group contains at least one managed handler that is open and does not
     * contain any 'pending' or 'locked' handlers.
     * 
     * @return true if the group is open and false otherwise; false is returned
     *         if the group is empty
     */
    public boolean isOpen( ) {
        return( handlerState.containsValue( LockState.OPEN ) && !handlerState.containsValue( LockState.PENDING ) && !handlerState.containsValue( LockState.LOCKED ) );
    }

    
    /**
     * Sets this managedHandler as 'locked' status.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            handler to set as 'locked' status
     */    
    public void setLocked( Handler managedHandler ) {
        handlerState.put( managedHandler, LockState.LOCKED );
    }
    
    
    /**
     * Sets this managedHandler as 'pending' status.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            handler to set as 'pending' status
     */    
    public void setPending( Handler managedHandler ) {
        handlerState.put( managedHandler, LockState.PENDING );
    }
    
    
    /**
     * Sets this managedHandler as 'open' status.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            handler to set as 'open' status
     */    
    public void setOpen( Handler managedHandler ) {
        handlerState.put( managedHandler, LockState.OPEN );
    }    
    
    
    /**
     * Queries if the event generation state of this group is set as not having
     * fired an event.
     */    
    public boolean getEventNone( ) {
        return( eventGenerationState == EventGenerationState.NONE );
    }
    
    
    /**
     * Sets the event generation state of this group as not having fired an
     * event.
     */
    public void setEventNone( ) {
        eventGenerationState = EventGenerationState.NONE;
    }
    
    
    /**
     * Queries if the event generation state of this group is set as having
     * fired an event that has not been processed.
     */    
    public boolean getEventFired( ) {
        return( eventGenerationState == EventGenerationState.FIRED );
    }
    
    
    /**
     * Sets the event generation state of this group as having fired an event
     * that has not been processed.
     */
    public void setEventFired( ) {
        eventGenerationState = EventGenerationState.FIRED;
    }

    
    /**
     * Queries if the event generation state of this group is set as having
     * fired an event that was checked-in and thus not processed.
     * 
     * @return true if this group has fired an event that was checked-in and not
     *         processed and false otherwise
     */
    public boolean getEventHold( ) {
        return( eventGenerationState == EventGenerationState.HOLDING );
    }
    
    
    /**
     * Sets the event generation state of this group as having fired an event
     * that was checked-in and thus not processed.
     * 
     */
    public void setEventHold( ) {
        eventGenerationState = EventGenerationState.HOLDING;
    }
    
    
    /**
     * Queries if the event generation state of this group is set as having
     * fired an event that was processed.
     * 
     * @return true if this group fired an event that was processed and false
     *         otherwise
     */
    public boolean getEventDone( ) {
        return( eventGenerationState == EventGenerationState.DONE );
    }
    
    
    /**
     * Sets the event generation state of this group as having fired an event
     * that was processed.
     */
    public void setEventDone( ) {
        eventGenerationState = EventGenerationState.DONE;
    }
    
}
