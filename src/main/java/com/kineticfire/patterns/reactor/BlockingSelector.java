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
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventGenerationState;

import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import java.util.concurrent.RejectedExecutionException;
import com.kineticfire.patterns.reactor.InvalidInterestOpsException;


/**
 * Provides a class to manage blocking tasks and readiness selection on those
 * blocking tasks. A single blocking task or a group of blocking tasks are
 * registered; when all tasks complete, a ready event is generated and the
 * associated handler is dispatched. When the handler completes, the handle is
 * removed; this action will also deregister the associated handler if the
 * handler is servicing only the blocking handle.
 * <p>
 * A thread pool is created to execute blocking tasks. By default, the thread
 * pool uses one worker thread and a three-second termination timeout for the
 * first and final shutdown subsequences. Settings for the thread pool may be
 * changed with configureBlockingThreadPool(..) method.
 * <p>
 * The BlockingSelector accepts java.lang.Runnable tasks.
 * <p>
 * It may first seem quite contradictory to have a mechanism for performing
 * blocking processing in a non-blocking architecture. However, there are cases
 * when performing blocking work--or a task that requires a substantial amount
 * of time with respect to other tasks or user expectations--is a must and is
 * outside the control of the JReactor user. An example of tasks that may block
 * or require an extended time to process are those delegated Runnable tasks
 * from the javax.net.ssl.SSLEngine retrieved via getDelegatedTask().
 * 
 * @author Kris Hall
 * @version 07-01-09
 */



public class BlockingSelector implements SpecificSelector {
    
    //*********************************************
    //~ Instance/static variables
    
    private CompositeSelector selector;
    private Map<Handle, BlockingTaskSet> registered;        // use Handle to access associated BlockingTaskSet
    private Map<Handle, EventGenerationState> handleState;    // status of event generation for Handle
    
    private ExecutorService workerThreadPool;                // executor for blocking worker threads
    private int workerThreadNum;                            // number of blocking worker threads
    private int terminationTimeoutFirst;                    // timeout value for first call of shutdown of workerThreadPool
    private TimeUnit terminationTimeoutUnitsFirst;            // timeout units for first call shutdown of workerThreadPool
    private int terminationTimeoutLast;                        // timeout value for first call of shutdown of workerThreadPool
    private TimeUnit terminationTimeoutUnitsLast;            // timeout units for first call shutdown of workerThreadPool
    
    private boolean done;


    
       //*********************************************
       //~ Constructors

    /**
     * Constructs a null BlockingSelector. The BlockingSelector has a null
     * referece to the Selector and no worker threads. A default three-second
     * termination timeout for the first and final shutdown subsequences is set.
     */
    public BlockingSelector( ) {
        selector = null;
        workerThreadNum = 0;
        workerThreadPool = null;
        registered = new HashMap<Handle,BlockingTaskSet>( );
        handleState = new HashMap<Handle, EventGenerationState>( );
        done = false;
        
        terminationTimeoutFirst = 3;
           terminationTimeoutUnitsFirst = TimeUnit.SECONDS;
           terminationTimeoutLast = 3;
           terminationTimeoutUnitsLast = TimeUnit.SECONDS;
    }
       
       /**
     * Constructs a BlockingSelector with a reference to the Selector and one
     * worker thread. A default three-second termination timeout for the first
     * and final shutdown subsequences is set. Settings for the thread pool may
     * be changed with configureBlockingThreadPool(..) method.
     * 
     * @param selector
     *            reference to the controlling selector
     */
       public BlockingSelector( CompositeSelector selector ) {
           this( selector, 1 );
       }
       
       
       /**
     * Constructs a BlockingSelector with a reference to the Selector and
     * blockingThreadNum number of worker threads. A default three-second
     * termination timeout for the first and final shutdown subsequences is set.
     * Settings for the thread pool may be changed with
     * configureBlockingThreadPool(..) method.
     * 
     * @param selector
     *            reference to the controlling selector
     * @param workerThreadNum
     *            number of worker threads to create in the blocking thread pool
     * 
     */
       public BlockingSelector( CompositeSelector selector, int workerThreadNum ) {
        this.selector = selector;
        this.workerThreadNum = workerThreadNum;
        
        registered = new HashMap<Handle,BlockingTaskSet>( );
        handleState = new HashMap<Handle, EventGenerationState>( );
        
        initWorkerThreadPool( workerThreadNum );
        
        done = false;
       }
       
       
       
    //*********************************************
       //~ METHODS
       
       /**
     * Creates a blocking worker thread pool, referenced by workerThreadPool, with
     * workerThreadNum number of threads. A default three-second termination
     * timeout for the first and final shutdown subsequences is set by changing
     * the values of 'terminationTimeoutFirst', 'terminateTimeoutUnitsFirst',
     * 'terminationTimeoutLast', and 'terminateTimeoutUnitsLast'.
     * 
     * @param workerThreadNum
     *            number of threads to be active in the blocking worker thread
     *            pool
     */
    private void initWorkerThreadPool( int workerThreadNum ) {
        workerThreadPool = Executors.newFixedThreadPool( workerThreadNum );
        terminationTimeoutFirst = 3;
           terminationTimeoutUnitsFirst = TimeUnit.SECONDS;
           terminationTimeoutLast = 3;
           terminationTimeoutUnitsLast = TimeUnit.SECONDS;
    }
       
       
       
       /**
     * Submits a blocking Runnable task with the given event handler and
     * interest operations. The task is run in the blocking thread pool.
     * Completion of the blocking runnable task results in a EventMask.BLOCKING
     * event and is serviced by the associated handler.
     * <p>
     * PRECONDITION: runnable and handler are not null; interestOps is
     * EventMask.{NOOP,BLOCKING}
     * 
     * @param runnable
     *            Runnable task to execute that may block or delay
     * @param handler
     *            event handler to associate with the completion of the Runnable
     *            task
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the blocking
     *         task event and handler
     */
       public synchronized Handle register( Runnable runnable, Handler handler, int interestOps ) {
           Set<Runnable> runnableSet = new HashSet<Runnable>( );
           runnableSet.add( runnable );
           Handle handle = registerGroup( runnableSet, handler, interestOps );
           return( handle );
       }
       
       
       
       /**
     * Submits a group of blocking Runnable tasks with the given event handler
     * and interest operations. The task is run in the blocking thread pool.
     * Completion of the group of blocking Runnable tasks results in a
     * EventMask.BLOCKING event and is serviced by the associated handler.
     * <p>
     * PRECONDITION: runnableSet and handler are not null; interestOps in
     * EventMask.{NOOP,BLOCKING}.
     * 
     * @param newRunnableSet
     *            Set of Runnable tasks to execute that may block or delay
     * @param handler
     *            event handler to associate with the completion of all Runnable
     *            tasks in the runnableSet
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the set of
     *         blocking tasks event and handler
     * @throws NullArgumentException
     *             if any runnable in runnableSet is null
     * @throws IllegalArgumentException
     *             if runnableSet is empty
     */
       public synchronized Handle registerGroup( Set<Runnable> newRunnableSet, Handler handler, int interestOps ) {
           
        Handle handle = null;        
        
        if ( !newRunnableSet.isEmpty( ) ) {
            Set<Runnable> runnableSet = new HashSet<Runnable>( );
            runnableSet.addAll( newRunnableSet );
            
            handle = new Handle( );
            
            Set<Runnable> runnableSet2 = new HashSet<Runnable>( );
            runnableSet2.addAll( newRunnableSet );
            BlockingTaskSet blockingTaskSet = new BlockingTaskSet( this, handle, runnableSet2 );
            selector.processRegister( handle, handler, this, interestOps );
            registered.put( handle, blockingTaskSet );
            handleState.put( handle, EventGenerationState.NONE );
            
            // submit each Runnable task to blockingWorkerThreads for execution            
            for ( Runnable runnable : runnableSet ) {
                if ( runnable != null ) {
                    BlockingTask blockingTask = new BlockingTask( blockingTaskSet, runnable );
                    try {
                        workerThreadPool.execute( blockingTask );                        
                    } catch ( RejectedExecutionException e ) {
                        
                    }
                } else {
                    throw ( new NullPointerException( ) );
                }
                    
            }
        } else {
            throw ( new IllegalArgumentException( ) );
        }
        
        return( handle );    
       }
       
       /**
     * Determines the registration status of the blockingHandle. Returns TRUE if
     * the blockingHandle is currently registered and is a handle for a blocking
     * event source and FALSE otherwise.
     * <p>
     * PRECONDITION: blockingHandle is not null
     * 
     * @param blockingHandle
     *            reference for which to request registration status
     * @return TRUE if the blockingHandle is currently registered and is a
     *         handle for a blocking event source and FALSE otherwise
     */
    public synchronized boolean isRegistered( Handle blockingHandle ) {
        return( registered.containsKey( blockingHandle ) );
    }
       
       
       /**
     * Sets the interest operations of handle to the given interestOps. 
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not EventMask.{NOOP,BLOCKING}
     */
    public synchronized void interestOps( Handle handle, int interestOps ) {
        if ( EventMask.xIsBlocking( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            if ( handleState.get( handle ) == EventGenerationState.HOLDING ) {
                if ( EventMask.xIsNoop( selector.interestOps( handle ) ) && EventMask.xIsBlocking( interestOps ) ) {
                    handleState.put( handle, EventGenerationState.FIRED );
                    selector.addReadyEvent( new Event( handle, EventMask.BLOCKING ) );
                }
            }
            selector.processInterestOps( handle, interestOps );
        } else {
            throw ( new InvalidInterestOpsException( interestOps ) );
        }
    }
       
       
    /**
     * Checks in a handle that failed to dispatch due to interest operations
     * having been to set to zero.
     * <p>
     * When this method completes, the handle will be contained in "checked in"
     * with the BlockingSelector and prepared to re-generate a ready event when
     * interest operations are set to EventMask.BLOCKING.
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            handle, referencing a registered blocking event, to be
     *            checked-in
     * @param event
     *            the event generated by the event source
     */
    public synchronized void checkin( Handle handle, Event event ) {
        if ( registered.containsKey( handle ) ) {
            handleState.put( handle, EventGenerationState.HOLDING );
        }
    }
    
    
    /**
     * Performs resume selection on the given handle.
     * <p>
     * If the event handler for the handle executed (e.g.
     * EventGenerationState.FIRED) then the handle is deregistered. If the
     * handle has a pending ready event (e.g. EventGenerationState.HOLDING) then
     * a new ready event is generated.
     * 
     * @param handle
     *            the handle on which to resume selection
     */
    public synchronized void resumeSelection( Handle handle ) {
        if ( handleState.get( handle ) == EventGenerationState.FIRED ) {
            deregister( handle );
        } else if ( ( handleState.get( handle ) == EventGenerationState.HOLDING ) && EventMask.xIsBlocking( selector.interestOps( handle ) ) ) {
            handleState.put( handle, EventGenerationState.FIRED );
            selector.addReadyEvent( new Event( handle, EventMask.BLOCKING ) );
        }        
    }
       
    
    /**
     * Called by a BlockingTaskSet when all managed BlockingTasks complete. This
     * method generates a blocking-ready event, EventMask.BLOCKING, and submits
     * the event to the Selector for dispatch.
     * 
     * @param handle
     *            the handle that generated the ready event
     */
    protected synchronized void triggerReadyEvent( Handle handle ) {
        
        if ( registered.containsKey( handle ) ) {
            handleState.put( handle, EventGenerationState.FIRED );
            selector.addReadyEvent( new Event( handle, EventMask.BLOCKING ) );        
        }
    }    
    
       
    /**
     * Deregisters the blocking event source referenced by handle. The blocking
     * event will no longer generate ready events
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle to deregister
     */
    public synchronized void deregister( Handle handle ) {
        registered.remove( handle );
        handleState.remove( handle );
        selector.processDeregister( handle );
    }
    
       
    /**
     * Configures the number of worker threads in the blocking worker thread
     * pool. The blocking worker thread pool is used to execute blocking
     * runnable tasks.
     * <p>
     * Once this message completes, the blocking worker thread pool will have
     * workerThreadNum number of worker threads.
     * <p>
     * This method should be called prior to submitting events to the blocking
     * selector.
     * 
     * @param workerThreadNum
     *            the number of worker threads in the blocking thread pool
     * @throws IllegalArgumentException
     *             if blockingWorkerThreadNum is less than one
     * 
     */
    public void configureBlockingThreadPool( int workerThreadNum ) {
        if ( workerThreadNum > 0 ) {
            workerThreadPool = Executors.newFixedThreadPool( workerThreadNum );
            this.workerThreadNum = workerThreadNum;
        } else {
            throw ( new IllegalArgumentException( ) );
        }
    }

    /**
     * Configures the termination timeout times of the blocking worker thread
     * pool. The blocking worker thread pool is used to execute blocking
     * runnable tasks.
     * <p>
     * Once this message completes, the blocking worker thread pool will have:
     * an initial termination timeout wait time of terminationTimeFirst with
     * units of terminationTimeoutUnitsFirst, and a final termination timeout
     * wait time of terminationTimeoutLast with units
     * terminationTimeoutUnitsLast.
     * <p>
     * The terminationTimeoutFirst is the time the shutdown process will wait
     * for running tasks to complete before attempting to directly terminate the
     * tasks. The terminationTimeoutLast is the time the shutdown process will
     * wait for running handlers to exit after an attempt was made to explicitly
     * terminate the tasks. See shutdown() for more information.
     * <p>
     * This method should be called prior to submitting events to the blocking
     * selector.
     * 
     * @param terminationTimeoutFirst
     *            the time the shutdown process will wait for running tasks to
     *            complete before attempting to directly terminate the tasks
     * @param terminationTimeoutUnitsFirst
     *            the units for terminationTimeoutFirst
     * @param terminationTimeoutLast
     *            the time the shutdown process will wait for running tasks to
     *            exit after an attempt was made to explicitly terminate the
     *            tasks
     * @param terminationTimeoutUnitsLast
     *            the units for terminationTimeoutLast
     * @throws IllegalArgumentException
     *             if terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     */
    public void configureBlockingThreadPool( int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitsFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitsLast ) {
        if ( terminationTimeoutFirst >= 0 && terminationTimeoutLast >= 0 ) {
            this.terminationTimeoutFirst = terminationTimeoutFirst;
               this.terminationTimeoutUnitsFirst = terminationTimeoutUnitsFirst;
               this.terminationTimeoutLast = terminationTimeoutLast;
               this.terminationTimeoutUnitsLast = terminationTimeoutUnitsLast;        
        } else {
            throw ( new IllegalArgumentException( ) );
        }
    }
    
    
    /**
     * Configures the number of worker threads and the termination timeout times
     * for the blocking worker thread pool. The blocking worker thread pool is
     * used to execute blocking runnable tasks.
     * <p>
     * Once this message completes, the blocking worker thread pool will have:
     * workerThreadNum number of worker threads, an initial termination timeout
     * wait time of terminationTimeFirst with units of
     * terminationTimeoutUnitsFirst, and a final termination timeout wait time
     * of terminationTimeoutLast with units terminationTimeoutUnitsLast.
     * <p>
     * The terminationTimeoutFirst is the time the shutdown process will wait
     * for running tasks to complete before attempting to directly terminate the
     * tasks. The terminationTimeoutLast is the time the shutdown process will
     * wait for running handlers to exit after an attempt was made to explicitly
     * terminate the tasks. See shutdown() for more information.
     * <p>
     * This method should be called prior to submitting events to the blocking
     * selector.
     * 
     * @param workerThreadNum
     *            the number of worker threads in the blocking thread pool
     * @param terminationTimeoutFirst
     *            the time the shutdown process will wait for running tasks to
     *            complete before attempting to directly terminate the tasks
     * @param terminationTimeoutUnitsFirst
     *            the units for terminationTimeoutFirst
     * @param terminationTimeoutLast
     *            the time the shutdown process will wait for running tasks to
     *            exit after an attempt was made to explicitly terminate the
     *            tasks
     * @param terminationTimeoutUnitsLast
     *            the units for terminationTimeoutLast
     * @throws IllegalArgumentException
     *             if workerThreadNum is less than unity or if
     *             terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     */
    public void configureBlockingThreadPool( int workerThreadNum, int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitsFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitsLast ) {
        
        if ( workerThreadNum > 0 && terminationTimeoutFirst >= 0 && terminationTimeoutLast >= 0 ) {
            workerThreadPool = Executors.newFixedThreadPool( workerThreadNum );
            this.workerThreadNum = workerThreadNum;
            
            this.terminationTimeoutFirst = terminationTimeoutFirst;
               this.terminationTimeoutUnitsFirst = terminationTimeoutUnitsFirst;
               this.terminationTimeoutLast = terminationTimeoutLast;
               this.terminationTimeoutUnitsLast = terminationTimeoutUnitsLast;
        } else {
            throw ( new IllegalArgumentException( ) );
        }
    }
    
    
    /**
     * Begins initial shutdown of BlockingSelector.
     * <p>
     * After this method is called, no future tasks can be sent to the
     * BlockingSelector's thread pool.
     * <p>
     * This method is thread-safe and will not block.
     * 
     */
    public void shutdownInit( ) {
        workerThreadPool.shutdown( );
    }

    
    /**
     * Completes shutdown of the BlockingSelector.
     * <p>
     * Prevents both the dispatching of new tasks and the addition of new tasks
     * into the blocking worker thread pool. The method may block if there are
     * blocking Runnable tasks that not yet completed. In that case, the thread
     * will block for an amount of time to wait for tasks to complete. If any
     * tasks are still running, then the tasks will be explicitly asked to
     * terminate. The thread will block for an amount of time to wait for all
     * tasks to exit. The wait times are configured by calling
     * configureBlockingThreadPool(..).
     * <p>
     * Calling shutdownInit() may or may not be called prior to calling this
     * method.
     * <p>
     * This method is thread-safe and may block.
     */
    public void shutdownFinal( ) {
        
        done = true;
        
        workerThreadPool.shutdown( );
        
        try {
            
            // blocks if any tasks are currently running, waiting up to terminationTimoutFirst or interrupted
            if ( !workerThreadPool.awaitTermination( terminationTimeoutFirst, terminationTimeoutUnitsFirst ) ) {
                
                // *** pool failed to terminate ***
                
                // cancel currently executing tasks
                workerThreadPool.shutdownNow( );
                
                // blocks if any tasks are currently running, waiting up to terminationTimoutLast or interrupted
                if ( !workerThreadPool.awaitTermination( terminationTimeoutLast, terminationTimeoutUnitsLast ) ) {
                    // *** pool failed to terminate, no more tries to stop or wait for tasks to complete ***
                }
            }
        } catch ( InterruptedException e ) { 
            // re-cancel if thread interrupted; cancel currently executing tasks
            workerThreadPool.shutdownNow( );
        }
        
    }
    
    
    /**
     * Prepares the BlockingSelector for garbage selection by gracefully
     * terminating the internal thread pool.
     */
    public void finalize( ) {
        if ( !done ) {
            shutdownFinal( );
        }
    }
    
}
