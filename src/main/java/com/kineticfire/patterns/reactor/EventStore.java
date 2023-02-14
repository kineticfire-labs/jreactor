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


import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.util.MultiValueMap;
import com.kineticfire.util.HashLinkMultiValueMap;

import java.util.List;

/**
 * EventStore provides a storage container for Events and associates them to a
 * Handler. The EventStore records one or more Events for each Handler and can
 * be queried to return the queued Events for a Handler in FIFO order.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class EventStore {
    
    //*********************************************
    //~ Instance/static variables
    
    private MultiValueMap<Handler, Event> handlerEventsMap;
    
    
    //*********************************************
       //~ Constructors
    
    /**
     * Creates an empty EventStore.
     * 
     */
    public EventStore( ) {
        handlerEventsMap = new HashLinkMultiValueMap<Handler, Event>( );
    }
    
    
       //*********************************************
       //~ METHODS
    
    /**
     * Stores the event, associating it to handler.
     * <p>
     * PRECONDITIONS: handler and event are not null
     * 
     * @param handler
     *            handler for which to associate to event
     * @param event
     *            event to store 
     */
    public void store( Handler handler, Event event ) {
        handlerEventsMap.put( handler, event );        
    }
    
    
    /**
     * Returns the next event, in FIFO order, associated to handler or null if
     * the handler is not a member of this data structure.
     * <p>
     * PRECONDITIONS: handler is not null
     * 
     * @param handler
     *            handler for which to retrieve next event
     * @return next event, in FIFO order, associated to handler or null if the
     *         handler is not a member of this data structure
     */
    public Event retrieve( Handler handler ) {
        return( handlerEventsMap.remove( handler ) );
    }
    
    
    /**
     * Returns true if the handler has mappings to stored events and false
     * otherwise.
     * <p>
     * PRECONDITIONS: handler is not null
     * 
     * @param handler
     *            handler for which to query for event mappings
     * @return true if the handler has mappings to stored events and false
     *         otherwise
     */
    public boolean contains( Handler handler ) {
        return( handlerEventsMap.containsKey( handler ) );
    }
    
    
    /**
     * Removes handler and its mapped events from this data structure. Returns a
     * List of those events mapped to handler or 'null' if handler is not
     * contained as key.
     * <p>
     * PRECONDITIONS: handler is not null
     * 
     * @param handler
     *            handler and associated events to remove from data structure
     * @return a List containing the events corresponding to handler or null if
     *         the handler was not contained as a key
     */
    public List<Event> remove( Handler handler ) {
        return( handlerEventsMap.removeAll( handler ) );
    }

}
