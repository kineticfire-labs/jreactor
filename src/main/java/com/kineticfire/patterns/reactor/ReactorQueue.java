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


import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.Handle;

/**
 * Defines the interface and behavior for a ReactorQueue. A ReactorQueue
 * specifies methods used by the Reactor architecture to coordinate with queue
 * structures.
 * <p>
 * Implementing classes should be thread-safe.
 * 
 * @author Kris Hall
 * @version 06-18-10
 * 
 */


public interface ReactorQueue {

    
    /**
     * Fires a ready EventMask.QREAD event if the queue contains data.
     * <p>
     * This method should only be used by the QueueSelector.
     * 
     */
    public void checkEvent( );

    
    /**
     * Sets the QueueSelector and Handle for the queue. The queue requires
     * the QueueSelector reference in order to submit ready events for
     * demultiplexing and dispatch; the reference to the handle is needed
     * to for the Reactor to demultiplex ready events.
     * <p>
     * This method should only be used by the QueueSelector.
     * 
     * @param queueSelector
     *            reference to the QueueSelector for submitting ready events
     * @param handle
     *            reference to the queue event source
     */
    public void configure( QueueSelector queueSelector, Handle handle );
    
}
