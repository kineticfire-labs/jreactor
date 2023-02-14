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


import com.kineticfire.patterns.reactor.BlockingTaskSet;

/**
 * Wraps a java.lang.Runnable task and notifies a BlockingTaskSet when this task
 * completes.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */


public class BlockingTask implements Runnable {
    
    
    //*********************************************
    //~ Instance/static variables
    
    
    private BlockingTaskSet blockingTaskSet;
    private Runnable runnable;

    
       //*********************************************
       //~ Constructors
    
    /**
     * Creates a new BlockingTask with null references to the blockingTaskSet
     * and runnable.
     */
    public BlockingTask( ) {
        blockingTaskSet = null;
        runnable = null;
    }
        

    /**
     * Creates a new BlockingTask with references to blockingTaskSet and
     * runnable.
     * <p>
     * PRECONDITION: blockingTaskSet and runnable are not null
     * 
     * @param blockingTaskSet
     *            the BlockingTaskSet that is managing this task
     * @param runnable
     *            the Runnable task to wrap
     * 
     */
    public BlockingTask( BlockingTaskSet blockingTaskSet, Runnable runnable ) {
        this.blockingTaskSet = blockingTaskSet;
        this.runnable = runnable;
    }
    
    
    
    //*********************************************
       //~ METHODS
    
    /**
     * Executes run() method of java.lang.Runnable task and notifies that
     * managing blockingTaskSet when execution completes.
     * <p>
     * This run() method satisfies java.lang.Runnable interface.
     * 
     */
    public void run( ) {
        runnable.run( );
        blockingTaskSet.completed( runnable );
    }
}