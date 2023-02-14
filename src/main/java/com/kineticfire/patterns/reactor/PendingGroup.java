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
import java.util.ListIterator;

import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;


/**
 * PendingGroup tracks PENDING managedHandlers as members of a LockHandlerGroup. The
 * tuple of (managedHandler,lockHandle) is added to the PendingGroup. The method
 * getNextHandle(Handler) retrieves, in FIFO order, the next lockHandle
 * associated with the Handler.
 * <p>
 * This data structure is conceptually a List of managedHandlers mapped to a
 * corresponding List of lockHandles. The approach allows search and removal on
 * what, in a Map, would be considered the 'key' and 'value' pairs, in this case
 * the managedHandler and the lockHandle.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */


public class PendingGroup {

    //*********************************************
    //~ Instance/static variables
    
    private List<Handler> handlerList;
    private List<Handle> handleList;

    
       //*********************************************
       //~ Constructors
    
    /**
     * Creates a new, empty PendingGroup.
     * 
     */
    public PendingGroup( ) {        
        handlerList = new LinkedList<Handler>( );
        handleList = new LinkedList<Handle>( );
    }

    
       //*********************************************
       //~ METHODS
    
    /**
     * Submits the tuple (managedHandler,lockHandle) to the pending group. The
     * tuple is added in FIFO order.
     * 
     * @param managedHandler
     *            the managed event handler to add to the pending group
     * @param lockHandle
     *            the lock handle to the pending group
     */
    public void add( Handler managedHandler, Handle lockHandle ) {
        handlerList.add( managedHandler );
        handleList.add( lockHandle );
    }

    
    /**
     * Removes all occurances of the (managedHandler,lockHandle) tuple from the
     * data structure. No action is taken if the tuple is not contained in the
     * data structure.
     * 
     * @param managedHandler
     *            the managed event handler, mapped to the lockHandle, to remove
     * @param lockHandle
     *            the lock handle, mapped to the managedHandler, to remove
     */    
    public void remove( Handler managedHandler, Handle lockHandle ) {
        
        ListIterator<Handler> itHandler = handlerList.listIterator( );
        ListIterator<Handle> itHandle = handleList.listIterator( );
        
        Handler curHandler;
        Handle curHandle;
        
        while ( itHandler.hasNext( ) ) {
            curHandler = itHandler.next( );
            curHandle = itHandle.next( );
            if ( curHandler == managedHandler ) {
                if ( curHandle == lockHandle ) {
                    itHandler.remove( );
                    itHandle.remove( );
                }
            }
        }
    }
    
    
    /**
     * Removes all occurances of (managedHandler,lockHandle) from the data
     * structure where the value is lockHandle; no action is taken if
     * lockHandle is not already contained in the data structure.
     * 
     * @param lockHandle
     *            the lock handle, mapped to the managedHandler, to remove
     */    
    public void remove( Handle lockHandle ) {
        
        int position = handleList.indexOf( lockHandle );
        while( position > -1 ) {
            handleList.remove( position );
            handlerList.remove( position );            
            
            position = handleList.indexOf( lockHandle );
        }
        
        
    }
    
    
    /**
     * Determines if the lockHandle is contained in this data structure. A
     * return value of 'true' does not necessarily indicate that the
     * lockHandle is pending; the lockHandle may also be simultaneously
     * locked.
     * 
     * @param lockHandle
     *            lockHandle on which to query
     * @return true if the managedHandler is contained in the data structure
     */
    public boolean contains( Handle lockHandle ) {
        return( handleList.contains( lockHandle ) );
    }
    
    
    /**
     * Determines if the managedHandler is contained in this data structure. A
     * return value of 'true' does not necessarily indicate that the
     * managedHandler is pending; the managedHandler may also be simultaneously
     * locked.
     * 
     * @param managedHandler
     *            the managed event handler on which to query
     * @return true if the managedHandler is contained in the data structure
     */
    public boolean contains( Handler managedHandler ) {
        return( handlerList.contains( managedHandler ) );
    }



    /**
     * Removes and returns the next lockHandle associated to the managedHandler
     * in FIFO order or null if the managedHandler is not present.
     * 
     * @param managedHandler
     *            the managed event handler used to look-up the next lock handle
     * @return the next lock handle associated to the managedHandler in FIFO
     *         order or null if the managedHandler is not present
     */
    public Handle getNextHandle( Handler managedHandler ) {
        Handle lockHandle = null;
        int index = -1;
        
        /*
         * returns first occurance (e.g. lowest index) of 'managedHandler' or -1
         * if managedHandler is not found
         */
        index = handlerList.indexOf( managedHandler );
        
        if ( index > -1 ) {
            handlerList.remove( index );
            lockHandle = handleList.remove( index );
        }
        
        return ( lockHandle );
    }
    
    
    /**
     * Returns but does not remove the next lockHandle associated to the
     * managedHandler in FIFO order.
     * 
     * @param managedHandler
     *            the managed event handler used to look-up the next lock handle
     * @return the next lock handle associated to the managedHandler in FIFO
     *         order or null if the managedHandler is not present
     */
    public Handle peekNextHandle( Handler managedHandler ) {
        Handle lockHandle = null;
        int index = -1;
        
        /*
         * returns first occurance (e.g. lowest index) of 'managedHandler' or -1
         * if managedHandler is not found
         */
        index = handlerList.indexOf( managedHandler );
        
        if ( index > -1 ) {
            lockHandle = handleList.get( index );
        }
        
        return ( lockHandle );
    }    
    
        
}
