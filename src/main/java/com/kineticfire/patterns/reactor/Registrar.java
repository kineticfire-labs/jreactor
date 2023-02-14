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
import java.util.List;
import java.util.LinkedList;

import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.util.MultiValueMap;
import com.kineticfire.util.HashLinkMultiValueMap;

/**
 * The Registrar manages the registration information for an event source,
 * independent of the type of event source. The Registrar can be queried for a
 * handle's associated selector, event handler, and interest operations; a set
 * of all handles for a given handler can be retrieved as well.
 * <p>
 * The purpose of this class is to centrally manage registration information
 * that is common across all event sources. This approach simplifies each
 * selector and reduces the needed amount of code. Furthermore, common queries
 * such as registration status and interest operations are more efficiently made
 * to the Registrar instead of to internally synchronized and/or threaded
 * selectors.
 * <p>
 * The Registrar centers on the 'handle' which represents the relationship
 * between a registered event source with an event handler and interest
 * operations. A handle refers to exactly one handler, selector, and value of
 * interest operations. A handler, however, may service many event sources and
 * thus have multiple handles.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class Registrar {

    //*********************************************
    //~ Instance/static variables
    
    private Map<Handle, SpecificSelector> handleSelectorMap;    // handle belongs to a selector
    private Map<Handle, Handler> handleHandlerMap;                // handle references an event handler
    private Map<Handle, Integer> handleOpsMap;                    // handle has interest operations
    private MultiValueMap<Handler, Handle> handlerHandlesMap;        // a handler has one or more handles


    
    
       //*********************************************
       //~ Constructors


       /**
       * Constructs an empty Registrar.
       *
       */
       public Registrar(  ) {
        handleSelectorMap = new HashMap<Handle, SpecificSelector>( );
        handleHandlerMap = new HashMap<Handle, Handler>( );
        handleOpsMap = new HashMap<Handle, Integer>( );
        handlerHandlesMap = new HashLinkMultiValueMap<Handler, Handle>( );
       }
       
       
       
       
       //*********************************************
       //~ METHODS


    /**
     * Adds the handle-handler relationship.
     * <p>
     * PRECONDITION: handle, handler, and selector are not null
     * 
     * @param handle
     *            reference for association of an event source to a handler
     * @param handler
     *            event handler to associate with handle
     * @param selector
     *            selector that owns the handle
     * @param ops
     *            interest operations to assign the handle
     */
       public void add( Handle handle, Handler handler, SpecificSelector selector, Integer ops ) {
           handleSelectorMap.put( handle, selector );
           handleHandlerMap.put( handle, handler );
           handleOpsMap.put( handle, ops );
           handlerHandlesMap.put( handler, handle );
       }
    
       /**
     * Removes the handle and it's associated selector and interest operations.
     * If the handler was associated with only this one handle, then the handler
     * is removed as well. Returns true if the handler was removed with the last
     * reference handle or the handle was not contained in the data structure,
     * otherwise false is returned.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference to remove
     * @return true if the handler referenced by handle is no longer contained
     *         in the data structure and false otherwise
     */
       public boolean remove( Handle handle ) {
           
           Handler handler = handleHandlerMap.remove( handle );
           handleSelectorMap.remove( handle );
           handleOpsMap.remove( handle );
           handlerHandlesMap.remove( handler, handle );
           
           return( !handlerHandlesMap.containsKey( handler ) );  // HashMap permits null arg here
       }
       
       
       /**
     * Completely removes the handler. All associated handles are consequently
     * removed along with associated selector and interest operations.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            event handler to remove
     */
       public void remove( Handler handler ) {
           List<Handle> handles = new LinkedList<Handle>( );
           handles.addAll( handlerHandlesMap.getAll( handler ) );    
           
           for ( Handle handle : handles ) {
               remove( handle );
           }
       }
       
       
       /**
     * Checks the registration status of a handle.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference for which to query the registration status
     * @return true if the handle is registered and false otherwise
     */
       public boolean contains( Handle handle ) {
           return( handleSelectorMap.containsKey( handle ) );
       }
       
       
       /**
     * Checks the registration status of a handler.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            event handler for which to query the registration status
     * @return true if the handler is registered and false otherwise
     */
       public boolean contains( Handler handler ) {
           return( handlerHandlesMap.containsKey( handler ) );
       }
       
       
       /**
     * Returns the selector that owns the given handle.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference that owns the selector
     * @return the selector object that owns the handle, or null if the handle
     *         is not registered
     */
       public SpecificSelector getSelector( Handle handle ) {
           return( handleSelectorMap.get( handle ) );
       }
       
       
       /**
     * Returns a list of handles that are associated to the given handler. If
     * the handler is not registered, then an empty list is returned.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handler
     *            event handler for which to return the set of associated
     *            handles
     * @return a list of associated handles for the handler; if the handler is
     *         not registered, an empty list is returned
     */
       public List<Handle> getHandles( Handler handler ) {
           List<Handle> handles = handlerHandlesMap.getAll( handler );
           if ( handles == null ) {
               handles = new LinkedList<Handle>( );
           }
           return( handles );
       }
       
       
       /**
     * Returns the handler associated with the handle.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference to an event handler
     * @return handler associated with the handle; returns null if handle is not
     *         registered
     */
       public Handler getHandler( Handle handle ) {
           return( handleHandlerMap.get( handle ) );
       }
       
       
       
       /**
     * Returns the number of handles associated with handler.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            event handler to query
     * @return the number of handles associated with handler or 0 if the
     *         handler is not registered
     */
       public int numHandles( Handler handler ) {
           int num = 0;
           
           if ( handlerHandlesMap.containsKey( handler ) ) {
               List<Handle> handles = handlerHandlesMap.getAll( handler );
               num = handles.size( );
           }
           
           return( num );
       }
              
       
       /**
     * Returns the interest operations associated with the handle.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            reference to the event handler
     * @return interest operations for the handle, or -1 if the handle is not
     *         registered
     */
       public int getInterestOps( Handle handle ) {
           int ops = -1;
           if ( handleOpsMap.containsKey( handle ) ) {
               ops = handleOpsMap.get( handle );
           }
           return( ops );
       }
       
       
       /**
     * Sets the interest operations for a handle.
     * <p>
     * PRECONDITION: handle is not null; handle is registered
     * 
     * @param handle
     *            reference to an event handler
     * @param interestOps
     *            interest operations for this handle
     */
       public void setInterestOps( Handle handle, int interestOps ) {
           handleOpsMap.put( handle, interestOps );
       }

}
