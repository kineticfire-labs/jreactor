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

import com.kineticfire.patterns.reactor.BlockingSelector;
import com.kineticfire.patterns.reactor.Handle;


/**
 * Creates a BlockingTaskSet that tracks when a set of java.lang.Runnable tasks completes and,
 * after all Runnable tasks finish, submits an EventMaks.BLOCKING event to the
 * BlockingSelector.
 * <p>
 * This class is thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class BlockingTaskSet {
    
    //*********************************************
    //~ Instance/static variables
    
    private BlockingSelector blockingSelector;
    private Handle handle;
    private Set<Runnable> runnableSet;
    
    
    
       //*********************************************
       //~ Constructors
    
    
    /**
     * Creates a new BlockingTaskSet with null references to the
     * blockingSelector, managed handle, and set of Runnable tasks.
     */
    public BlockingTaskSet( ) {
        blockingSelector = null;
        handle = null;
        runnableSet = null;
    }
        

    /**
     * Creates a new BlockingTaskSet with references to the blockingSelector,
     * managed handle, and the set of Runnable tasks.
     * <p>
     * PRECONDITION: blockingSelector, handle, and runnableSet are not null
     * 
     * @param blockingSelector
     *            reference to blockingSelector in order to submit ready event when
     *            group of Runnable tasks complete
     * @param handle
     *            managed handle that references registration of this event
     * @param runnableSet
     *            the set of Runnable tasks to track
     */
    public BlockingTaskSet( BlockingSelector blockingSelector, Handle handle, Set<Runnable> runnableSet ) {
        this.blockingSelector = blockingSelector;
        this.handle = handle;
        this.runnableSet = runnableSet;
    }
    
    
    //*********************************************
       //~ METHODS
    
    
    /**
     * Used by a BlockingTask to notify the BlockingTaskSet that the task has
     * completed execution.
     * <p>
     * PRECONDITION: runnable is not null
     * 
     * @param runnable
     *            the Runnable task that completed execution
     */
    protected synchronized void completed( Runnable runnable ) {
        runnableSet.remove( runnable );
        if ( runnableSet.isEmpty( ) ) {
            blockingSelector.triggerReadyEvent( handle );
        }
    }        
    
}
