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

import com.kineticfire.patterns.reactor.Handle;


/**
 * OpenGroup tracks OPEN Handles. Although Handles map to LockHandlerGroups, the
 * Handle referencing the HandlerGroup is the only entity that is tracked.
 * OpenGroup is effectively a Set which manages Handles.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */

public class OpenGroup {

    
    //*********************************************
    //~ Instance/static variables
    
    private Set<Handle> values;
    
    
       //*********************************************
       //~ Constructors
    
    /**
     * Creates a new, empty IdleGroup.
     */
    public OpenGroup( ) {        
        values = new HashSet<Handle>( );        
    }

    
       //*********************************************
       //~ METHODS
    
    /**
     * Marks this handle as being open. If successful in recording this handle
     * as open, the handle is added to the data structure. Otherwise, the handle
     * is already present in the data structure (e.g. the handle has previously
     * been marked as open) and no further action is taken.
     * <p>
     * PRECONDITION: lockHandle is not null
     * 
     * @param lockHandle
     *            attemp to mark this handle as open
     */
    public void add( Handle lockHandle ) {
        values.add( lockHandle );
    }
    
    
    /**
     * Unmarks this handle as being open. If successful in recording this handle
     * as no longer open, the handle is removed from the data structure.
     * Otherwise, the handle was not already present in the data structure (e.g.
     * the handle was not previously been marked as open) and no further action
     * is taken.
     * <p>
     * PRECONDITION: lockHandle is not null
     * 
     * @param lockHandle
     *            attemp to unmark this handle as open
     */
    public void remove( Handle lockHandle ) {
        values.remove( lockHandle );
    }
    
    
    /**
     * Queries the open status of a handle. Returns true if the handle is open
     * and false otherwise.
     * <p>
     * PRECONDITION: lockHandle is not null
     * 
     * @param lockHandle
     *            handle to query the open status
     * @return true if the handle is open and false otherwise.
     */
    public boolean contains( Handle lockHandle ) {
        return( values.contains( lockHandle ) );
    }
}