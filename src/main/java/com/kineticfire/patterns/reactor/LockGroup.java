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

import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;


/**
 * LockGroup tracks LOCKED managedHandlers as members of a LockHandlerGroup. The
 * tuple of (managedHandler,lockHandle) is tracked.
 * <p>
 * This data structure is conceptually a Map of managedHandler that references a
 * lockHandle.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */


public class LockGroup {

    //*********************************************
    //~ Instance/static variables
    
    private Map<Handler, Handle> values;
    

       //*********************************************
       //~ Constructors
    
    /**
     * Creates a new, empty LockedGroup.
     */
    public LockGroup( ) {        
        values = new HashMap<Handler, Handle>( );        
    }

    
       //*********************************************
       //~ METHODS
    
    /**
     * Locks a managedHandler and associates that handler to a lockHandler. No
     * action is taken if the managedHandler is already locked.
     * <p>
     * PRECONDITION: managedHandler lockHandle are not null
     * 
     * @param managedHandler
     *            event handler to lock
     * @param lockHandle
     *            lockHandle to associate with the event handler
     */
    public void add( Handler managedHandler, Handle lockHandle ) {
        values.put( managedHandler, lockHandle );        
    }
        
    
    /**
     * Unlock the managedHandler (e.g. event handler). No action is taken if the
     * managedHandler is not already locked.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            event handler to unlock
     */
    public void remove( Handler managedHandler ) {        
        values.remove( managedHandler );
    }
    
    
    /**
     * Queries if the lockHandle has at least one associated locked event
     * handler (e.g. managedHandler). The presence of the lockHandle in the
     * LockedGroup does not necessarily mean that the entire HandlerGroup
     * referenced by the lockHandle is locked.
     * <p>
     * PRECONDITION: lockHandle is not null
     * 
     * @param lockHandle
     *            lockHandle to check if it has at least one associated locked
     *            event handler
     * @return true if the lockHandle has at least one associated locked event
     *         handler and false otherwise
     */
    public boolean contains( Handle lockHandle ) {
        return( values.containsValue( lockHandle ) );
    }
    
    
    /**
     * Queries if the managedHandler (e.g. event handler) is locked.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            event handler for which to request lock status
     * @return true if the event handler is locked and false otherwise
     */
    public boolean contains( Handler managedHandler ) {
        return( values.containsKey( managedHandler ) );
    }
    
    
    /**
     * Returns the lockHandle associated with the managedHandler. If the
     * managedHandler is not locked, then null is returned.
     * <p>
     * PRECONDITION: managedHandler is not null
     * 
     * @param managedHandler
     *            event handler for which to request its associated lockHandle
     * @return lockHandle associated with locked event handler or null if the
     *         handler is not locked
     */
    public Handle getHandle( Handler managedHandler ) {
        return( values.get( managedHandler ) );
    }
        
}
