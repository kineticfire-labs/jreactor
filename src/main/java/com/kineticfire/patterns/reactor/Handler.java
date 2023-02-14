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

import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.Handle;


/**
 * Defines the interface for event handlers. An event handler is developer-code
 * that implements the business-logic for an application. The event handler is
 * executed by the JReactor in response to a ready event on an event source
 * associated with the event handler. The dispatching of a handler is only an
 * indication, not a guarantee, that the event source is ready for a data
 * operation.
 * <p>
 * Implementing classes should specify if they are thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */



public interface Handler {

    
    /**
     * Called by the JReactor in response to a ready event on a registered event
     * source associated to the event handler. The dispatching of a handler is
     * only an indication, not a guarantee, that the event source is ready for a
     * data operation.
     * <p>
     * Handle will not be null. The readyOps value will consist of a composition
     * of valid values in EventMask. The info parameter is null when not used.
     * 
     * @param handle
     *            the handle associated with the event source that generated the
     *            ready event
     * @param readyOps
     *            the ready operations, a subset of the interest operations, for
     *            which this event source is ready
     * @param info
     *            additional information related to this specific event
     */
    public void handleEvent( Handle handle, int readyOps, MessageBlock info );
    
}
