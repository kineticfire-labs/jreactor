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



// Core Reactor

import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.RunLockHandlerTracker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.ObjectBasedMessageBlockBuilder;

// Handler & Adapter
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.Handler;

// Event & Event Sources
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.MessageQueue;
import java.nio.channels.SelectableChannel;

// Util
import java.util.Set;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.Date;

// Exceptions
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import java.util.concurrent.RejectedExecutionException;
import com.kineticfire.patterns.reactor.InvalidResourceException;
import com.kineticfire.patterns.reactor.EventDispatchFailedException;


/**
 * Provides an implementation of the Reactor interface along with configuration
 * methods.
 * <p>
 * The Reactor is an architectural pattern in software design that promotes
 * scalable, re-usable, and robust software. It lends itself to event-driven
 * applications such as data-driven processing, simulation, gaming, user
 * interfaces, networking, and real-time communications signaling.
 * <p>
 * The Reactor pattern, originally described in [Sch94], performs readiness
 * selection--non-blocking and non-polling--on registered event sources. The
 * Reactor dispatches event handlers to service ready events on sources. An
 * event source, such as a queue or a network channel, is registered with the
 * Reactor together with an event handler and a set of interest operations. When
 * an event source's ready operations match the registered interest operations,
 * the Reactor dispatches the event source's associated event handler to service
 * the ready event. An event handler is developer-code that implements the
 * business-logic of an application.
 * <p>
 * <b>Implementation</b>
 * <p>
 * The JReactor provides an implementation of the Reactor interface that allows
 * optimization of the Reactor architecture and promotes strong error control.
 * <p>
 * <i>Initialization</i>
 * <p>
 * The JReactor is initialized with any one of its three constructors. The
 * constructors provide for the ability to set the number of worker threads
 * available to execute event handlers and to disable selection for certain
 * event sources, discussed later. All constructors set three-second timeouts
 * for the first and second shutdown sequences in both the worker thread pool
 * and blocking thread pool, if the blocking thread pool is enabled.
 * <p>
 * The JReactor is operational after initialization. For example, event sources
 * may be registered and generate ready events. However, the JReactor will not
 * dispatch event handlers to service ready events until the dispatch() method
 * is called.
 * <p>
 * <i>Dispatch</i>
 * <p>
 * Calling the dispatch() method causes the JReactor to dispatch event handlers
 * to service ready events on registered event sources. The JReactor continues
 * to dispatch until stopped by a call to shutdown(). The JReactor ensures that
 * only one instance of a given event handler will execute at the same time.
 * <p>
 * <i>Shutdown</i>
 * <p>
 * When shutdown, the JReactor no longer dispatches event handlers and cannot be
 * re-started. The JReactor is shutdown by a call to the shutdown() method. The
 * method immediately terminates event handler dispatch such that no further
 * ready events are processed. The method then gracefully closes the worker
 * thread pool, the critical thread pool if created, and the blocking thead pool
 * if enabled.
 * <p>
 * The closure of each thread pool procedes as follows. If a thread pool is not
 * currently executing a task, then the pool is closed immediately. Otherwise,
 * the pool waits for a configurable amount of time (first termination timeout)
 * before attempting to cancel each executing task. If a task is still
 * executing, the pool will wait for another configurable amount of time (second
 * termination timeout) before continuing.
 * <p>
 * <i>Error Control</i>
 * <p>
 * The JReactor supports two types of errors: standard and critical. It is
 * possible to recover from a standard error and not possible to recover from a
 * critical error. A standard error typically results from a problem in the
 * program logic such as a null reference, use of an event source that is not
 * registered, or a resource that is in an illegal state. A critical error
 * results from an internal JReactor error such as the failure of the event
 * dispatch thread.
 * <p>
 * The JReactor uses a dedicated execution path for servicing critical errors to
 * provide added reliability. Queueing, dispatch, and execution in response to
 * critical ready events are wholly separated from processing standard-level
 * ready events to include standard error events.
 * <p>
 * A critical error handler must be supplied with any JReactor constructor. A
 * critical-level error is reported to the user as the following MessageBlock
 * chain.
 * <p>
 * error -> level -> critical -> exception -> |exception|
 * <p>
 * Any developer command that may be queued requires an error handler,
 * referenced by the handler's handle, to service any error when the queued
 * command is processed. A standard-level error is reported to the user as the
 * following MessageBlock chain. Note that the portion of the chain beginning
 * with and including 'info' is only present if the error resulted from a failed
 * command that had been queued.
 * <p>
 * error -> level -> standard -> exception -> |exception| -> info -> command ->
 * |command|
 * <p>
 * <b>Configuration</b>
 * <p>
 * Thread pools and selectors for certain event sources may be adjusted to tune
 * JReactor performance, improve user experience, and conserve system resources.
 * <p>
 * <i>Adjusting the Worker Thread Pool</i>
 * <p>
 * The worker thread pool executes event handlers that service ready events on
 * registered event sources.
 * <p>
 * The number of worker threads in the worker thread pool is initially set with
 * the constructor and may be later adjusted using one of the
 * configureWorkerThreadPool(...) methods.
 * <p>
 * The first and second termination timeout values default to three seconds. The
 * values may be adjusted using one of the configureWorkerThreadPool(...)
 * methods.
 * <p>
 * Ideally, the number of the threads in the worker thread pool should be set to
 * the total number of cores available less threads used by the JReactor and any
 * threads used by the implementing application and JVM.  The number of cores can
 * be obtained via the command 'Runtime.getRuntime( ).availableProcessors( )'.
 * <p>
 * JReactor uses the following threads not including the worker thread pool:
 * <ol>
 *   <li>Event dispatch thread = 1... always runs</li>
 *   <li>Blocking thread pool = defaults to one, but can be configured by configureBlockingThreadPool() or disabled</li>
 *   <li>Critical thread pool = 1... always runs</li>
 *   <li>Channel selector = 1... but can be disabled</li>
 *   <li>Timer selector = 1... via java.util.Timer background thread or disabled</li>
 *   <li>Note that the signal selector uses one thread only when the SIGTERM signal fires, but this is the termination of the application.  Or it can be disabled.</li>
 *   <li>Plus default JVM threads like garbage collection, GUI (if using), etc.</li>
 * </ol>
 * <p>
 * <i>Adjusting the Blocking Thread Pool</i>
 * <p>
 * The blocking thread pool executes registered blocking tasks.
 * <p>
 * The number of threads in the blocking thread pool defaults to one and may be
 * adjusted using one of the configureBlockingThreadPool(...) methods.
 * <p>
 * The first and second termination timeout values default to three seconds. The
 * values may be adjusted using one of the configureBlockingThreadPool(...)
 * methods.
 * <p>
 * <i>Adjusting the Critical Thread Pool</i>
 * <p>
 * The critical thread pool executes critical-level error events.
 * <p>
 * The number of threads is set to one and cannot be changed.
 * <p>
 * The first and second termination timeout values default to three seconds. The
 * values may be adjusted using one of the configureCriticalThreadPool(...)
 * methods.
 * <p>
 * <i>Disabling and Enabling Individual Selectors</i>
 * <p>
 * Disabling selection on event sources that are not used may conserve system
 * resources. Selection on certain event sources may be disabled only by using
 * the JReactor(int, Handler, boolean) constructor and setting the boolean to
 * false. Event selection is then disabled for event types of: queue, channel,
 * timer, blocking, and signal. Note that lock and error event types cannot be
 * disabled.
 * <p>
 * Event selection can be enabled individually using one of the
 * enableXXXSelector() where XXX is the event type. Once enabled, event
 * selection cannot be disabled.
 * <p>
 * <b>References</b>
 * <p>
 * [Sch94] Douglas Schmidt, "Reactor: An Object Behavioral Pattern for
 * Concurrent Event Demultiplexing and Event Handler Dispatching, " Proceedings
 * of the First Pattern Languages of Programs conference in Monticello,
 * Illinois, Aug. 1994.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class JReactor implements Reactor {

    //*********************************************
    //~ Instance/static variables
    
    private Thread dispatchThread;                            // thread for dispatch of ready events; also handles termination of JReactor       

    private CompositeSelector selector;                        // composite non-blocking readiness selector for event sources
    
    private RunLockHandlerTracker runLockHandlerTracker;    // tracks running/locked handlers and maps to their adapters
    
    
    // event dispatch path
    private ExecutorService workerThreadPool;                // executor for worker threads
    private int workerThreadNum;                            // number of worker threads
    private int terminationTimeoutFirst;                    // timeout value for first call of shutdown of workerThreadPool
    private TimeUnit terminationTimeoutUnitsFirst;            // timeout units for first call shutdown of workerThreadPool
    private int terminationTimeoutLast;                        // timeout value for last call of shutdown of workerThreadPool
    private TimeUnit terminationTimeoutUnitsLast;            // timeout units for last call shutdown of workerThreadPool
    private volatile boolean stopWorker;                    // stop dispatching event handlers    
    private static Byte guardWorker;                                // sycnhronization point for event dispatch path
        
    
    // critical event dispatch path
    private ExecutorService criticalThreadPool;                // executor for critical error events    
    private Handler criticalErrorHandler;                    // error handler for critical error events
    private Handle criticalErrorHandle;                        // error handle for critical error events
    private int terminationTimeoutCriticalFirst;            // timeout value for first call of shutdown of criticalThreadPool
    private TimeUnit terminationTimeoutUnitsCriticalFirst;    // timeout units for first call shutdown of criticalThreadPool
    private int terminationTimeoutCriticalLast;                // timeout value for last call of shutdown of criticalThreadPool
    private TimeUnit terminationTimeoutUnitsCriticalLast;    // timeout units for last call shutdown of criticalThreadPool
    private volatile boolean stopCritical;                    // stop dispatching critical error event handlers        
    private static Byte guardCritical;                                // synchronization point for critical event dispatch path


    // etc
    private ObjectBasedMessageBlockBuilder messageBlockBuilder;                  // builds MessageBlocks

    
    

       //*********************************************
       //~ Constructors

    
       // *******************************************************************
       // ****************** INITIALIZATION (Constructors) ****************** 
       // *******************************************************************


    /**
     * Constructs an uninitialized JReactor. All internal components are set to
     * null.
     * 
     */
    public JReactor( ) {
        dispatchThread = null;
        selector = null;
        runLockHandlerTracker = null;
        workerThreadPool = null;
        workerThreadNum = 0;
        terminationTimeoutFirst = 0;
        terminationTimeoutUnitsFirst = null;
        terminationTimeoutLast = 0;
        terminationTimeoutUnitsLast = null;
        stopWorker = false;
        guardWorker = null;
        criticalThreadPool = null;
        criticalErrorHandler = null;
        criticalErrorHandle = null;
        terminationTimeoutCriticalFirst = 0;
        terminationTimeoutUnitsCriticalFirst = null;
        terminationTimeoutCriticalLast = 0;
        terminationTimeoutUnitsCriticalLast = null;
        stopCritical = false;
        guardCritical = null;
        messageBlockBuilder = null;
    }
    
    
    /**
     * Constructs the JReactor. The number of worker threads is set to one, and
     * the critical error handler is set to criticalErrorHandler. All event-type
     * selectors are enabled.
     * <p>
     * The JReactor will accept and queue ready events after constructor
     * initialization. The dispatch() method must be called for the JReactor to
     * serviced queued ready events, if any, as well as subsequent ready events.
     * 
     * @param criticalErrorHandler
     *            event handler to service critical errors
     * @throws NullPointerException
     *             if criticalErrorHandler is null
     */
       public JReactor( Handler criticalErrorHandler ) {
          this( 1, criticalErrorHandler, true );
       }
       
       
       /**
     * Constructs the JReactor. The number of worker threads is set to
     * workerThreadNum, and the critical error handler is set to
     * criticalErrorHandler. All event-type selectors are enabled.
     * <p>
     * The JReactor will accept and queue ready events after constructor
     * initialization. The dispatch() method must be called for the JReactor to
     * serviced queued ready events, if any, as well as subsequent ready events.
     * 
     * @param workerThreadNum
     *            number of threads to permanantly create and dedicate to the
     *            worker thread pool to service ready events
     * @param criticalErrorHandler
     *            event handler to service critical errors
     * @throws IllegalArgumentException
     *             if workerThreadNum is less than unity
     * @throws NullPointerException
     *             if criticalErrorHandler is null            
     */
       public JReactor( int workerThreadNum, Handler criticalErrorHandler ) {
          this( workerThreadNum, criticalErrorHandler, true );
       }
       
       
       /**
     * Constructs the JReactor. The number of worker threads is set to
     * workerThreadNum, and the critical error handler is set to
     * criticalErrorHandler. All event-type selectors are started if
     * enableAllSelectors is true, else only lock and error event-type selectors
     * are enabled while queue, channel, timer, blocking, and signal event-types
     * are disabled.
     * <p>
     * The JReactor will accept and queue ready events after constructor
     * initialization. The dispatch() method must be called for the JReactor to
     * serviced queued ready events, if any, as well as subsequent ready events.
     * 
     * @param workerThreadNum
     *            number of threads to permanantly create and dedicate to the
     *            worker thread pool to service ready events
     * @param criticalErrorHandler
     *            event handler to service critical errors
     * @param enableAllSelectors
     *            enables readiness selection for all events sources else only
     *            enables lock and error events and disables queue, channel,
     *            timer, blocking, and signal events
     * @throws IllegalArgumentException
     *             if workerThreadNum is less than unity
     * @throws NullPointerException
     *             if criticalErrorHandler is null
     */
       public JReactor( int workerThreadNum, Handler criticalErrorHandler, boolean enableAllSelectors ) {

           if ( workerThreadNum > 0 ) { 
               if ( isNotNull( criticalErrorHandler ) ) {
                   
                   // *** JREACTOR GENERAL                                                 
                  
                runLockHandlerTracker = new RunLockHandlerTracker( );
                
                selector = new CompositeSelector( this, runLockHandlerTracker, enableAllSelectors );
        
                
                // *** EVENT DISPATCH PATH
                
                // creates worker thread pool and inits shutdown parameters
                initWorkerThreadPool( workerThreadNum );
                
                dispatchThread = null;
                
                stopWorker = false;
                
                guardWorker = Byte.parseByte( "0" );
                
                
                
                // *** CRITICAL EVENT DISPATCH PATH                    
                
                criticalThreadPool = null;
        
                // registers error handler for critical events
                   criticalErrorHandle = registerError( criticalErrorHandler, EventMask.ERROR );
                   this.criticalErrorHandler = criticalErrorHandler;
                   
                   // default timeouts for critical thread pool
                   terminationTimeoutCriticalFirst = 3;
                   terminationTimeoutUnitsCriticalFirst = TimeUnit.SECONDS;
                   terminationTimeoutCriticalLast = 3;
                   terminationTimeoutUnitsCriticalLast = TimeUnit.SECONDS;
                   
                stopCritical = false;                
                guardCritical = Byte.parseByte( "0" );

                messageBlockBuilder = new ObjectBasedMessageBlockBuilder( );
                
               } else {
                   throw( new NullPointerException( ) );
               }
           } else {
               throw( new IllegalArgumentException( ) );
           }
       }
       
       
       

       //*********************************************
       //~ METHODS
       
       
       // *****************************************************************************
       // ****************** INITIALIZATION con't (for constructors) ****************** 
       // *****************************************************************************
         

    /**
     * Creates a worker thread pool, referenced by workerThreadPool, with
     * workerThreadNum number of threads. A default three-second termination
     * timeout for the first and final shutdown subsequences is set by changing
     * the values of 'terminationTimeoutFirst', 'terminateTimeoutUnitsFirst',
     * 'terminationTimeoutLast', and 'terminateTimeoutUnitsLast'.
     * 
     * @param workerThreadNum
     *            number of threads to be active in the worker thread pool
     */
    private void initWorkerThreadPool( int workerThreadNum ) {
        this.workerThreadNum = workerThreadNum;
        workerThreadPool = Executors.newFixedThreadPool( workerThreadNum );
            // fixed number of threads, shared and unbounded queue, restarts failed threads if not shutdown
        
        terminationTimeoutFirst = 3;
           terminationTimeoutUnitsFirst = TimeUnit.SECONDS;
           terminationTimeoutLast = 3;
           terminationTimeoutUnitsLast = TimeUnit.SECONDS;
    }
    
    
    
       // *************************************************************************
       // ****************** CONTROL ****************** 
       // *************************************************************************
       

    /**
     * Starts the JReactor. Triggers the JReactor to begin demultiplexing events
     * and dispatching event handlers to service event sources.
     * <p>
     * The JReactor is idle prior to calling dispatch() and all ready events are
     * queued. Upon calling dispatch(), the JReactor services queued ready
     * events and continues to service subsequent ready events until the
     * JReactor is closed via the shutdown() method.
     * <p>
     * This method creates a new thread, if not already started, dedicated to
     * dispatching ready events added to the event queue.
     * <p>
     * Calling dispatch() multiple times has no affect. Calls to dispatch after
     * calling shutdown() are ignored.
     * <p>
     * This method does not block.
     */
    public void dispatch( ) {
        synchronized( guardWorker ) {
            if ( dispatchThread == null && !stopWorker ) {
                dispatchThread = new Thread( new Runnable( ) { public void run( ) { dispatchLoop( ); } } );
                dispatchThread.start( );
            }
        }
    }
    
 

    /**
     * Terminates the JReactor. The JReactor no longer demultiplexes ready
     * events and exits when all running event handlers, if any, terminate or
     * after the timeout value is reached. Both the event dispatch path and the
     * critical event dispatch path are shutdown, in that order.
     * <p>
     * Ideally, all event sources are deregistered before calling shutdown() or
     * at least no event handlers are running. Shutdown will proceed without,
     * fail, however if there are still registered but idle or running event
     * handlers.
     * <p>
     * Calling shutdown() multiple times has no affect.
     * <p>
     * This method creates a new thread, if not already started, to complete the
     * shutdown process.
     * <p>
     * This method does not block.
     */
    public void shutdown( ) {

        synchronized( guardWorker ) {
            
            if ( !stopWorker ) {
                stopWorker = true; // causes dispatchLoop() to exit
                selector.addReadyEvent( new Event( EventMask.NOOP ) ); // adds element into event queue incase dispatchLoop() blocked in a call to take() 
                
                // creates a new thread to execute the close method and shutdown JReactor;
                //      new thread incase the dispatchLoop() thread died
                Thread closeThread = new Thread( new Runnable( ) { public void run( ) { close( ); } } );
                closeThread.start( );                            
            }            
        }    
    }

    
    
       // *************************************************************************
       // ****************** STATUS ****************** 
       // *************************************************************************

    
    /**
     * Queries if the JReactor is dispatching ready events.
     * <p>
     * A return value of true indicates that the JReactor is processing ready
     * events and dispatching handlers to service those events. A return value
     * of false indicates that the JReactor was either not started, was started
     * but is now shutdown, or is in the process of shutting down. If the
     * JReactor is shutting down, handlers that were running prior to JReactor
     * shutdown may continue to run to completion unless acted upon by another
     * method.
     * 
     * @return true if the JReactor is dispatching ready events and false
     *         otherwise
     */
    public boolean isDispatching( ) {
        boolean isDispatching;
        
        synchronized( guardWorker ) {
            if ( dispatchThread == null ) {
                isDispatching = false;
            } else {
                isDispatching = !stopWorker;
            }
        }
        
        return( isDispatching );
    }
    
    
    /**
     * Queries if the JReactor has been shutdown.
     * <p>
     * A return value of true indicates that the JReactor is no longer
     * processing new ready events and dispatching handlers for those events;
     * handlers that were running prior to JReactor shutdown may continue to run
     * to completion unless acted upon by another method.
     * 
     * @return true if the JReactor is shutdown or false otherwise
     */
    public boolean isShutdown( ) {
        boolean isShutdown;
        
        synchronized( guardWorker ) {
            isShutdown = stopWorker;
        }
        
        return( isShutdown );
    }

    
    
    
    
       // *************************************************************************
       // ****************** COMMANDS ****************** 
       // *************************************************************************

       
    
       // ------------------ HANDLE ------------------
    
    
    /**
     * Determines if this handle is currently associated to any registered
     * handler.
     * 
     * @param handle
     *            the handle to query registration status
     * @return true if the handle is currently associated to any registered
     *         handler and false otherwise
     * @throws NullPointerException
     *             if handle is null 
     */
    public boolean isRegistered( Handle handle ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;
            if ( handle != null ) {
                isRegistered = selector.isRegistered( handle );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isRegistered );
        }
    }
    
    
    
    /**
     * Returns the interest operations for the handle or -1 if the handle is not
     * registered.
     * 
     * @param handle
     *            the handle to request interest operations
     * @return the interest operations set for this handle or -1 if the handle
     *         is not registered
     * @throws NullPointerException
     *             if handle is null
     */
    public int interestOps( Handle handle ) {
        synchronized( guardWorker ) {
            int ops = -1;
            if ( handle != null ) {
                ops = selector.interestOps( handle ); 
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( ops );
        }
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps.
     * <p>
     * The command is executed immediately, if possible. If the handler
     * associated to this handle is running, then the command is queued. If the
     * handler associated to this handle is pending or locked, then the command
     * is silently ignored.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is queued and results in an error, then the
     * handler associated with the errorHandle will be dispatched at a later
     * time.
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws NullPointerException
     *             if handle or errorHandle is null
     * @throws InvalidResourceException
     *             if handle or errorHandle is not registered
     * @throws InvalidLockStatusException
     *             if handle is not open
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOps( Handle handle, int interestOps, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( handle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    
                    if ( runLockHandlerTracker.isRunning( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        interestOpsInternal( handle, interestOps, adapter, errorHandle );
                    } else if ( runLockHandlerTracker.isLocked( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( new String( "nokey" ) );
                        adapter.command( mb1 );
                        interestOpsInternal( handle, interestOps, adapter, errorHandle );
                    } else if ( selector.isOpen( handler ) ) {
                        selector.interestOps( handle, interestOps, errorHandle );
                    } else {
                        throw( new InvalidLockStatusException( ) );
                    }
                    
                } else {
                    throw ( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
           }
    }
    
    
    /**
     * Queues the interestOps command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching the handler 
     * associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: handle, adapter, and errorHandle are not null
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @param adapter
     *            adapter to receieve the queued command
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void interestOpsInternal( Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "interestOps" );
        messageBlockBuilder.createMessageBlock( "set" );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "ops" );
        messageBlockBuilder.createMessageBlock( interestOps );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );


        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Sets the interest operations of a locked handle to the given interestOps.
     * The command is queued until the handler associated with the handle is
     * unlocked.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is accepted, then it is queued. If the queued
     * command results in an error, then the handler associated with the
     * errorHandle will be dispatched at a later time.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle, that is locked, to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockHandle, handle, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, handle, or errorHandle is not registered; if
     *             lockHandle does not references a valid handler group
     * @throws NotGroupMemberException
     *             if the handler referenced by handle is not a member of the
     *             group referenced by lockHandle
     * @throws InvalidLockStatusException
     *             if lockHandle is not locked
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOps( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( handle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    
                    if ( selector.isLockedMember( lockHandle, handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        interestOpsInternal( lockHandle, handle, interestOps, adapter, errorHandle );
                    } else {
                        if ( !selector.isMember( lockHandle, handler ) ) {
                            throw( new NotGroupMemberException( ) );
                        } else {
                              throw( new InvalidLockStatusException( ) );
                        }
                    }
                    
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }
    
    /**
     * Queues the interestOps command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching the handler 
     * associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: lockHandle, handle, adapter, and errorHandle are not null
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @param adapter
     *            adapter to receieve queued commands or commands are sent to
     *            selector if adapter is null
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void interestOpsInternal( Handle lockHandle, Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "interestOps" );
        messageBlockBuilder.createMessageBlock( "set" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "ops" );
        messageBlockBuilder.createMessageBlock( interestOps );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps OR'd with
     * the current interest operations.
     * <p>
     * The command is executed immediately, if possible. If the handler
     * associated to this handle is running, then the command is queued. If the
     * handler associated to this handle is pending or locked, then the command
     * is silently ignored.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is queued and results in an error, then the
     * handler associated with the errorHandle will be dispatched at a later
     * time.
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle, OR'd with the
     *            current intesest operations
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws NullPointerException
     *             if handle or errorHandle is null
     * @throws InvalidResourceException
     *             if handle or errorHandle is not registered
     * @throws InvalidLockStatusException
     *             if handle is not open
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOpsOr( Handle handle, int interestOps, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( handle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    
                    if ( runLockHandlerTracker.isRunning( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        interestOpsOrInternal( handle, interestOps, adapter, errorHandle );
                    } else if ( runLockHandlerTracker.isLocked( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( "nokey" );
                        adapter.command( mb1 );
                        interestOpsOrInternal( handle, interestOps, adapter, errorHandle );
                    } else if ( selector.isOpen( handler ) ) {
                        selector.interestOpsOr( handle, interestOps, errorHandle );
                    } else {
                        throw( new InvalidLockStatusException( ) );
                    }
                    
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
           }
    }
    
    
    /**
     * Queues the interestOps command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching
     * the handler associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: handle, adapter, and errorHandle are not null
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle OR'd with the
     *            current interest operations
     * @param adapter
     *            adapter to receieve the queued command
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void interestOpsOrInternal( Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "interestOps" );
        messageBlockBuilder.createMessageBlock( "or" );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "ops" );
        messageBlockBuilder.createMessageBlock( interestOps );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Sets the interest operations of a locked handle to the given interestOps
     * OR'd with the current interest operations. The command is queued until
     * the handler associated with the handle is unlocked.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is accepted, then it is queued. If the queued
     * command results in an error, then the handler associated with the
     * errorHandle will be dispatched at a later time.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle, that is locked, to set interest operations
     * @param interestOps
     *            the interest operations to set for handle AND'd with the
     *            current interest operations
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockHandle, handle, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, handle, or errorHandle is not registered; if
     *             lockHandle does not references a valid handler group
     * @throws NotGroupMemberException
     *             if the handler referenced by handle is not a member of the
     *             group referenced by lockHandle
     * @throws InvalidLockStatusException
     *             if lockHandle is not locked
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOpsOr( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( handle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    if ( selector.isLockedMember( lockHandle, handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        interestOpsOrInternal( lockHandle, handle, interestOps, adapter, errorHandle );
                    } else {
                        if ( !selector.isMember( lockHandle, handler ) ) {
                            throw( new NotGroupMemberException( ) );
                        } else {
                              throw( new InvalidLockStatusException( ) );
                        }
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }
    
    
    /**
     * Queues the interestOpsOr command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching the handler 
     * associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: lockHandle, handle, adapter, and errorHandle are not null
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle OR'd with the
     *            current interest operations
     * @param adapter
     *            adapter to receieve queued commands or commands are sent to
     *            selector if adapter is null
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void interestOpsOrInternal( Handle lockHandle, Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "interestOps" );
        messageBlockBuilder.createMessageBlock( "or" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "ops" );
        messageBlockBuilder.createMessageBlock( interestOps );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps AND'd
     * with the current interest operations.
     * <p>
     * The command is executed immediately, if possible. If the handler
     * associated to this handle is running, then the command is queued. If the
     * handler associated to this handle is pending or locked, then the command
     * is silently ignored.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is queued and results in an error, then the
     * handler associated with the errorHandle will be dispatched at a later
     * time.
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle, AND'd with the
     *            current intesest operations
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws NullPointerException
     *             if handle or errorHandle is null
     * @throws InvalidResourceException
     *             if handle or errorHandle is not registered
     * @throws InvalidLockStatusException
     *             if handle is not open
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     * @throws EventSourceModifiedException
     *             if the state of the event source referenced by handle was
     *             modified outside of the JReactor
     */
    public void interestOpsAnd( Handle handle, int interestOps, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( handle != null && errorHandle != null ) {
                if ( selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    
                    if ( runLockHandlerTracker.isRunning( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        interestOpsAndInternal( handle, interestOps, adapter, errorHandle );
                    } else if ( runLockHandlerTracker.isLocked( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( "nokey" );
                        adapter.command( mb1 );
                        interestOpsAndInternal( handle, interestOps, adapter, errorHandle );
                    } else if ( selector.isOpen( handler ) ) {
                        selector.interestOpsAnd( handle, interestOps, errorHandle );
                    } else {
                        throw( new InvalidLockStatusException( ) );
                    }
                    
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
           }
    }
    
    
    /**
     * Queues the interestOpsAnd command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching
     * the handler associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: handle, adapter, and errorHandle are not null
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle AND'd with the
     *            current interest operations
     * @param adapter
     *            adapter to receieve the queued command
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void interestOpsAndInternal( Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "interestOps" );
        messageBlockBuilder.createMessageBlock( "and" );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "ops" );
        messageBlockBuilder.createMessageBlock( interestOps );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Sets the interest operations of a locked handle to the given interestOps
     * AND'd with the current interest operations. The command is queued until
     * the handler associated with the handle is unlocked.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is accepted, then it is queued. If the queued
     * command results in an error, then the handler associated with the
     * errorHandle will be dispatched at a later time.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle, that is locked, to set interest operations
     * @param interestOps
     *            the interest operations to set for handle AND'd with the
     *            current interest operations
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockHandle, handle, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, handle, or errorHandle is not registered; if
     *             lockHandle does not references a valid handler group
     * @throws NotGroupMemberException
     *             if the handler referenced by handle is not a member of the
     *             group referenced by lockHandle
     * @throws InvalidLockStatusException
     *             if lockHandle is not locked
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOpsAnd( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( handle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    if ( selector.isLockedMember( lockHandle, handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        interestOpsAndInternal( lockHandle, handle, interestOps, adapter, errorHandle );
                    } else {
                        if ( !selector.isMember( lockHandle, handler ) ) {
                            throw( new NotGroupMemberException( ) );
                        } else {
                              throw( new InvalidLockStatusException( ) );
                        }
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }
    

    /**
     * Queues the interestOpsAnd command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching the handler 
     * associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: lockHandle, handle, adapter, and errorHandle are not null
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle AND'd with the
     *            current interest operations
     * @param adapter
     *            adapter to receieve queued commands or commands are sent to
     *            selector if adapter is null
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void interestOpsAndInternal( Handle lockHandle, Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "interestOps" );
        messageBlockBuilder.createMessageBlock( "and" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "ops" );
        messageBlockBuilder.createMessageBlock( interestOps );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    

    
    /**
     * Deregisters the event source referenced by handle such that the event
     * source will no longer generate ready events and the handle is no longer
     * registered. If this is the last handle registered to an an event handler,
     * then the handler is deregistered as well.
     * <p>
     * The command is executed immediately, if possible. If the handler
     * associated to this handle is running, then the command is queued. If the
     * handler associated to this handle is pending or locked, then the command
     * is silently ignored.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is queued and results in an error, then the
     * handler associated with the errorHandle will be dispatched at a later
     * time.
     * <p>
     * An error handle may be deregistered where its reference is used for both
     * handle arguments.  If successful, deregistration proceeds normally as
     * described above.  If unsuccessful, then a ready event is triggered on the
     * error handle, and the errorHandle is not deregistered.
     * 
     * @param handle
     *            the handle to deregister
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws NullPointerException
     *             if handle or errorHandle is null
     * @throws InvalidResourceException
     *             if handle or errorHandle is not registered
     * @throws InvalidLockStatusException
     *             if handle is not open
     */
    public void deregister( Handle handle, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( handle != null && errorHandle != null ) {
                if ( selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    
                    if ( runLockHandlerTracker.isRunning( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        deregisterInternal( handle, adapter, errorHandle );
                    } else if ( runLockHandlerTracker.isLocked( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( "nokey" );
                        adapter.command( mb1 );
                        deregisterInternal( handle, adapter, errorHandle );
                    } else if ( selector.isOpen( handler ) ) {
                        selector.deregister( handle, errorHandle );
                    } else {
                        throw( new InvalidLockStatusException( ) );
                    }
                    
                } else {
                    throw ( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
           }
    }
    
    
    /**
     * Queues the deregister command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching
     * the handler associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: handle, adapter, and errorHandle are not null
     * 
     * @param handle
     *            the handle to set interest operations
     * @param adapter
     *            adapter to receieve the queued command
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void deregisterInternal( Handle handle, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "deregister" );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }

    
    /**
     * Deregisters the event source referenced by handle such that the event
     * source will no longer generate ready events and the handle is no longer
     * registered. If this is the last handle registered to an an event handler,
     * then the handler is deregistered as well.
     * <p>
     * The command is queued until the handler associated with the handle is
     * unlocked.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is accepted, then it is queued. If the queued
     * command results in an error, then the handler associated with the
     * errorHandle will be dispatched at a later time.
     * <p>
     * An error handle may be deregistered where its reference is used for both
     * handle and error arguments.  If successful, deregistration proceeds
     * normally as described above.  If unsuccessful, then a ready event is
     * triggered on the error handle, and the errorHandle is not deregistered.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle, that is locked, to deregister
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockHandle, handle, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, handle, or errorHandle is not registered; if
     *             lockHandle does not references a valid handler group
     * @throws NotGroupMemberException
     *             if the handler referenced by handle is not a member of the
     *             group referenced by lockHandle
     * @throws InvalidLockStatusException
     *             if lockHandle is not locked
     */
    public void deregister( Handle lockHandle, Handle handle, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( handle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( handle ) && selector.isRegistered( errorHandle ) ) {
                    Handler handler = selector.handler( handle );
                    if ( selector.isLockedMember( lockHandle, handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        deregisterInternal( lockHandle, handle, adapter, errorHandle );
                    } else {
                        if ( !selector.isMember( lockHandle, handler ) ) {
                            throw( new NotGroupMemberException( ) );
                        } else {
                              throw( new InvalidLockStatusException( ) );
                        }
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }

    
    /**
     * Queues the deregister-handle command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching the handler 
     * associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: lockHandle, handle, adapter, and errorHandle are not null
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler associated with the
     *            handle is a member
     * @param handle
     *            the handle to set interest operations
     * @param adapter
     *            adapter to receieve queued commands or commands are sent to
     *            selector if adapter is null
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void deregisterInternal( Handle lockHandle, Handle handle, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "deregister" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handle" );
        messageBlockBuilder.createMessageBlock( handle );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Retrieves the handler currently associated to the given handle. Returns
     * null if the handle is not associated to a currently registered handler.
     * 
     * @param handle
     *            handle used to retrieve the associated handler
     * @return the handler associated with the handle or null if the handle is
     *         not associated to a registered handler
     * @throws NullPointerException
     *             if handle is null
     */
    public Handler handler( Handle handle ) {
        synchronized( guardWorker ) {
            Handler handler = null;
            if ( handle != null ) {
                handler = selector.handler( handle );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( handler );
        }
    }
    
    
    
    //     ------------------ HANDLER ------------------
    
    
    /**
     * Determines if this handler is currently registered.
     * 
     * @param handler
     *            the handler to query registration status
     * @return true if the handler is currently registered and false otherwise
     * @throws NullPointerException
     *             if handler is null
     */
    public boolean isRegistered( Handler handler ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;
            if ( handler != null ) {
                isRegistered = selector.isRegistered( handler );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isRegistered );
        }
    }
    
    
    /**
     * Returns a set of handles that are associated to the given handler. If the
     * handler is not registered, then an empty set is returned.
     * 
     * @param handler
     *            event handler for which to return the set of associated
     *            handles
     * @return a set of associated handles for the handler; if the handler is
     *         not registered, an empty set is returned
     * @throws NullPointerException
     *             if handler is null
     */
    public Set<Handle> handles( Handler handler ) {
        synchronized( guardWorker ) {
            Set<Handle> handles = null;
            if ( handler != null ) {
                handles = selector.handles( handler );
            } else {
                throw( new NullPointerException( ) );
            }
            return( handles );
        }
    }
    
    
    
    /**
     * Deregisters an event handler. Removes the event handler and also
     * deregisters the event source(s) and associated handle(s) such that ready
     * events are neither generated nor serviced.
     * <p>
     * The command is executed immediately, if possible. If the handler
     * associated to this handle is running, then the command is queued. If the
     * handler associated to this handle is pending or locked, then the command
     * is silently ignored.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is queued and results in an error, then the
     * handler associated with the errorHandle will be dispatched at a later
     * time.
     * 
     * @param handler
     *            the handler to deregister
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws NullPointerException
     *             if handler or errorHandle is null
     * @throws InvalidResourceException
     *             if handler or errorHandle is not registered
     * @throws InvalidLockStatusException
     *             if handle is not open            
     */
    public void deregister( Handler handler, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( handler != null && errorHandle != null ) {
                if ( selector.isRegistered( handler ) && selector.isRegistered( errorHandle ) ) {
                    
                    if ( runLockHandlerTracker.isRunning( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        deregisterInternal( handler, adapter, errorHandle );
                    } else if ( runLockHandlerTracker.isLocked( handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( "nokey" );
                        adapter.command( mb1 );
                        deregisterInternal( handler, adapter, errorHandle );
                    } else if ( selector.isOpen( handler ) ) {
                        selector.deregister( handler, errorHandle );
                    } else {
                        throw( new InvalidLockStatusException( ) );
                    }
                    
                } else {
                    throw ( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
           }
    }

    
    /**
     * Queues the deregister command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching
     * the handler associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: handle, adapter, and errorHandle are not null
     * 
     * @param handle
     *            the handle to set interest operations
     * @param adapter
     *            adapter to receieve the queued command
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void deregisterInternal( Handler handler, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "deregister" );
        messageBlockBuilder.createMessageBlock( "handler" );
        messageBlockBuilder.createMessageBlock( handler );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    
    /**
     * Deregisters a locked handler. Removes the event handler and also
     * deregisters the event source(s) and associated handle(s) such that ready
     * events are neither generated nor serviced.
     * <p>
     * The command is queued until the handler is unlocked.
     * <p>
     * Exceptions are thrown on this method call if the command fails
     * immediately. If the command is queued and results in an error, then the
     * handler associated with the errorHandle will be dispatched at a later
     * time.
     * <p>
     * An error handle may be deregistered where its reference is used for both
     * arguments.  If successful, deregistration proceeds normally as described
     * above.  If unsuccessful, then a ready event is triggered on the error
     * handle, and the errorHandle is not deregistered.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler is a member
     * @param handler
     *            the handler to deregister
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws NullPointerException
     *             if lockhandle, handler, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, handle, or errorHandle is not registered; if
     *             lockHandle does not references a valid handler group
     * @throws NotGroupMemberException
     *             if the handler referenced by handle is not a member of the
     *             group referenced by lockHandle
     * @throws InvalidLockStatusException
     *             if lockHandle is not locked
     */
    public void deregister( Handle lockHandle, Handler handler, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( handler ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( handler ) && selector.isRegistered( errorHandle ) ) {
                    if ( selector.isLockedMember( lockHandle, handler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( handler );
                        deregisterInternal( lockHandle, handler, adapter, errorHandle );
                    } else {
                        if ( !selector.isMember( lockHandle, handler ) ) {
                            throw( new NotGroupMemberException( ) );
                        } else {
                              throw( new InvalidLockStatusException( ) );
                        }
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }

    
    /**
     * Queues the deregister-handler command to the given adapter.
     * <p>
     * Exceptions are thrown if the queued and results in an error, dispatching the handler 
     * associated with the errorHandle.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: lockHandle, handle, adapter, and errorHandle are not null
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler is a member
     * @param handler
     *            the handler to deregister
     * @param adapter
     *            adapter to receieve queued commands or commands are sent to
     *            selector if adapter is null
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     */
    private void deregisterInternal( Handle lockHandle, Handler handler, HandlerAdapter adapter, Handle errorHandle ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "deregister" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handler" );
        messageBlockBuilder.createMessageBlock( handler );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    // ------------------ QUEUE ------------------

    /**
     * Registers a queue event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event source and handler.
     * 
     * @param queue
     *            event source to register
     * @param handler
     *            event handler to associate with the queue
     * @param interestOps
     *            interest operations to assign the queue
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if either queue or handler are null; or if the queue selector
     *             is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't either EventMask.{NOOP, QREAD}
     * @throws DuplicateRegistrationException
     *             if the queue is already registered
     */
       public Handle registerQueue( MessageQueue queue, Handler handler, int interestOps ) {
           synchronized( guardWorker ) {               
               Handle handle = null;
               
               if ( isNotNull( queue ) && isNotNull( handler ) ) {                                  
                handle = selector.registerQueue( queue, handler, interestOps );
               } else {
                   throw( new NullPointerException( ) );
               }
                                  
               return( handle );
           }
    }


       /**
     * Determines the registration status of the queue. Returns true if the
     * queue is currently registered and false otherwise.
     * 
     * @param queue
     *            event source for which to request registration status
     * @return true if the queue is currently registered and false otherwise
     * @throws NullPointerException
     *             if queue is null; or if the queue selector is disabled
     */
    public boolean isRegisteredQueue( MessageQueue queue ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;

            if ( isNotNull( queue ) ) {
                isRegistered = selector.isRegisteredQueue( queue );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isRegistered );
        }
    }
    
    /**
     * Determines if the queueHandle references a currently registered queue
     * event source. Returns true if the queueHandle is currently registered and
     * is a handle for a queue event source and false otherwise.
     * 
     * @param queueHandle
     *            reference for which to request registration status
     * @return true if the queueHandle is currently registered and is a handle
     *         for a queue event source and false otherwise
     * @throws NullPointerException
     *             if queueHandle is null; or if the queue selector is disabled
     */
    public boolean isRegisteredQueue( Handle queueHandle ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;

            if ( isNotNull( queueHandle ) ) {
                isRegistered = selector.isRegisteredQueue( queueHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isRegistered );
        }
    }

    
    
    // ------------------ CHANNEL ------------------
        
    /**
     * Registers a channel event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event source and handler.
     * <p>
     * Configures the channel for non-blocking mode.
     * 
     * @param channel
     *            event source to register
     * @param handler
     *            event handler to associate with the channel
     * @param interestOps
     *            interest operations to assign the channel
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if either channel or handler are null; or if the channel
     *             selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't either EventMask.{NOOP, CREAD, CWRITE,
     *             ACCEPT, CONNECT}
     * @throws DuplicateRegistrationException
     *             if the channel is already registered
     * @throws IllegalResourceStateException
     *             if an error occurred while registering the channel to the
     *             java.nio.channels.Selector
     */
    public Handle registerChannel( SelectableChannel channel, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( channel ) && isNotNull( handler ) ) {
                handle = selector.registerChannel( channel, handler, interestOps );
            } else {
                throw ( new NullPointerException( ) );
            }
            return( handle );
        }
    }
        

    /**
     * Determines the registration status of the channel. Returns true if the
     * channel is currently registered and false otherwise.
     * 
     * @param channel
     *            event source for which to request registration status
     * @return true if the channel is currently registered and false otherwise
     * @throws NullPointerException
     *             if channel is null; or if the channel selector is disabled
     */
    public boolean isRegisteredChannel( SelectableChannel channel ) {
       synchronized( guardWorker ) {
           boolean isRegistered = false;
           if ( isNotNull( channel ) ) {
               isRegistered = selector.isRegisteredChannel( channel );
           } else {
               throw( new NullPointerException( ) );
           }
           return( isRegistered );
        }
    }
        

    /**
     * Determines the registration status of the channelHandle. Returns true if
     * the channelHandle is currently registered and is a handle for a channel
     * event source and false otherwise.
     * 
     * @param channelHandle
     *            reference for which to request registration status
     * @return true if the channelHandle is currently registered and is a handle
     *         for a channel event source and false otherwise
     * @throws NullPointerException
     *             if channelHandle is null; ; or if the channel selector is
     *             disabled
     */
    public boolean isRegisteredChannel( Handle channelHandle ) {
        synchronized( guardWorker ) {
           boolean isRegistered = false;
           if ( isNotNull( channelHandle ) ) {
               isRegistered = selector.isRegisteredChannel( channelHandle );
           } else {
               throw( new NullPointerException( ) );
           }
           return( isRegistered );
        }
    }
    
        
        
    // ------------------ TIMER ------------------

    
    /**
     * Registers a timer event to fire once at an absolute time. The timer event
     * is associated with an event handler and interest operations. Returns a
     * handle representing the relationship between the timer event and handler.
     * <p>
     * The handler will run exactly once unless it is canceled before the event
     * expires or it is deregistered.
     * <p>
     * See java.util.Timer:schedule(TimerTask,Date).
     * 
     * @param time
     *            time at which to expire the timer event and invoke the handler
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerOnce( Date time, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( handler ) ) {
                handle = selector.registerTimerOnce( time, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            return( handle );
        }
    }

    /**
     * Registers a timer event to fire once after a set delay. The timer event
     * is associated with an event handler and interest operations. Returns a
     * handle representing the relationship between the timer event and handler.
     * <p>
     * The handler will run exactly once unless it is canceled before the event
     * expires or it is deregistered.
     * <p>
     * See java.util.Timer:schedule(TimerTask,long).
     * 
     * @param delay
     *            delay in milliseconds before the event is fired
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerOnce( long delay, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( handler ) ) {
                handle = selector.registerTimerOnce( delay, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            return( handle ); 
        }
    }

    /**
     * Registers a timer event to fire at a fixed-delay beginning at firstTime.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the timer event and handler.
     * <p>
     * The handler will be dispatched for each expired timer event until
     * deregistered. Timer events will continue to expire until canceled or
     * deregistered.
     * <p>
     * For fixed-delay execution, the execution time is scheduled relative to
     * the actual execution time of the previous execution. This type of
     * execution is appropriate for tasks that require smoothness over a short
     * time window, like animation, rather than a long time window.
     * <p>
     * See java.util.Timer:schedule(TimerTask,Date,long).
     * 
     * @param firstTime
     *            the first time the timer event will expire
     * @param period
     *            time in milliseconds between timer event expirations
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedDelay( Date firstTime, long period, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( handler ) ) {
                handle = selector.registerTimerFixedDelay( firstTime, period, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            return( handle );
        }
    }

    /**
     * Registers a timer event to fire at a fixed-delay after an initial delay.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the event source and handler.
     * <p>
     * The handler will be dispatched for each timer expired event until
     * explicitly canceled or deregistered.
     * <p>
     * For fixed-delay execution, the execution time is scheduled relative to
     * the actual execution time of the previous execution. This type of
     * execution is appropriate for tasks that require smoothness over a short
     * time window, like animation, rather than a long time window.
     * <p>
     * See java.util.Timer schedule(TimerTask,delay,period).
     * 
     * @param delay
     *            the initial delay in milliseconds before the timer event first
     *            expires
     * @param period
     *            time in milliseconds between timer event expirations
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if delay is negative, or the sume delay and
     *             System.currentTimeMillis() is negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedDelay( long delay, long period, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( handler ) ) {
                handle = selector.registerTimerFixedDelay( delay, period, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            return( handle );
        }
    }
    
    /**
     * Registers a timer event to fire at a fixed-delay after an initial delay.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the timer event and handler.
     * <p>
     * The handler will be dispatched for each expired timer event until
     * deregistered. Timer events will continue to expire until canceled or
     * deregistered.
     * <p>
     * For fixed-delay execution, the execution time is scheduled relative to
     * the actual execution time of the previous execution. This type of
     * execution is appropriate for tasks that require smoothness over a short
     * time window, like animation, rather than a long time window.
     * <p>
     * See java.util.Timer:schedule(TimerTask,delay,period).
     * 
     * @param firstTime
     *            the absolute time at which the timer event first expires
     * @param period
     *            time in milliseconds between timer event expirations
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedRate( Date firstTime, long period, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( handler ) ) {
                handle = selector.registerTimerFixedRate( firstTime, period, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            return( handle );
        }
    }

    /**
     * Registers a timer event to fire at a fixed-rate beginning after an
     * initial delay. Following executions will occur at approximately regular
     * intervals, as given by period. Returns a handle representing the
     * relationship between the timer event and handler.
     * <p>
     * The handler will be dispatched for each expired timer event until
     * deregistered. Timer events will continue to expire until canceled or
     * deregistered.
     * <p>
     * For fixed-rate execution, the execution time is scheduled relative the
     * initial execution time. This type of execution is appropriate for tasks
     * that are sensitive to absolute time, like sounding alarm at a recurring
     * time, or where the number of executions is critical to the task, such as
     * a count-down alarm.
     * <p>
     * See java.util.Timer:scheduleAtFixedRate(TimerTask,delay,long).
     * 
     * @param delay
     *            the initial delay in milliseconds before the timer event first
     *            expires
     * @param period
     *            time in milliseconds between timer event expirations
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedRate( long delay, long period, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( handler ) ) {
                handle = selector.registerTimerFixedRate( delay, period, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }        
            return( handle );
        }
    }
    
    
    /**
     * Determines the registration status of the timerHandle. Returns TRUE if
     * the timerHandle is currently registered and is a handle for a timer event
     * source and FALSE otherwise.
     * 
     * @param timerHandle
     *            reference for which to request registration status
     * @return TRUE if the timerHandle is currently registered and is a handle
     *         for a timer event source and FALSE otherwise
     * @throws NullPointerException
     *             if timerHandle is null; or if the timer selector is disabled
     */
    public boolean isRegisteredTimer( Handle timerHandle ) {
        synchronized( guardWorker ) {
            boolean isRegisteredTimer = false;
            
            if ( isNotNull( timerHandle ) ) {
                isRegisteredTimer = selector.isRegisteredTimer( timerHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isRegisteredTimer );
        }
    }

    

    /**
     * Attempts to cancel and then deregister a timer event referenced by
     * timerHandle, returning true if the timer event was successfully canceled
     * and false otherwise.
     * <p>
     * A timer event is successfully canceled by this method when the
     * event--referenced by timerHandle--is currently registered, has not had
     * the cancel() method called, and, if a run-once handler, the event has not
     * yet fired. Successfully canceling a run-once timer event means that the
     * timer event handler will not run. Successfully canceling a recurring
     * timer event means that the event source will not continue to fire events;
     * the handler may or may not have run in the past, may be running
     * currently, and events that already fired will run.
     * <p>
     * If the cancelled timerHandle has no outstanding timer events, then the
     * timerHandle will be deregistered. If the cancelled timerHandle does have
     * outstanding timer events, then the timerHandle will be deregistered when
     * all timer events have been accounted.
     * <p>
     * Calling this event multiple times has no affect.
     * <p>
     * PRECONDITION: handle not null
     * 
     * @param timerHandle
     *            reference to timer event to attemp cancellation
     * @return true if the timer event referenced by timerHandle was
     *         successfully cancelled and false otherwise
     * @throws NullPointerException
     *             if timerHandle is null; or if the timer selector is disabled
     * @throws InvalidLockStatusException
     *             if handle is not open
     */
    public boolean cancelTimer( Handle timerHandle ) {
        synchronized( guardWorker ) {
            boolean canceled = false;
            if ( isNotNull( timerHandle ) ) {
                if ( selector.isRegisteredTimer( timerHandle ) ) {
                    Handler handler = selector.handler( timerHandle );
                    if ( selector.isOpen( handler ) ) {
                        canceled = selector.cancelTimer( timerHandle );
                    } else {
                        throw( new InvalidLockStatusException( ) );
                    }
                }
            } else {
                throw( new NullPointerException( ) );
            }
            return( canceled );
        }
    }
        
    /**
     * Attempts to cancel and then deregister a timer event referenced by
     * timerHandle and locked by lockHandle, returning true if the timer event
     * was successfully canceled and false otherwise.
     * <p>
     * A timer event is successfully canceled by this method when the
     * event--referenced by timerHandle--is currently registered, has not had
     * the cancel() method called, is locked by lockHandle, and, if a run-once
     * handler, the event has not yet fired. Successfully canceling a run-once
     * timer event means that the timer event handler will not run. Successfully
     * canceling a recurring timer event means that the event source will not
     * continue to fire events; the handler may or may not have run in the past,
     * may be running currently, and events that already fired will run.
     * <p>
     * If the cancelled timerHandle has no outstanding timer events, then the
     * timerHandle will be deregistered once the lockHandle is unlocked. If the
     * cancelled timerHandle does have outstanding timer events, then the
     * timerHandle will be deregistered when all timer events have been
     * accounted and once the lockHandle is unlocked.
     * <p>
     * Calling this event multiple times has no affect.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler is a member
     * @param timerHandle
     *            reference to timer event to attemp cancellation
     * @return true if the timer event referenced by timerHandle was
     *         successfully cancelled and false otherwise
     * @throws NullPointerException
     *             if lockHandle or timerHandle is null; or if the timer
     *             selector is disabled
     * @throws InvalidResourceException
     *             if lockHandle is not registered or does not references a
     *             valid handler group
     * @throws NotGroupMemberException
     *             if the handler referenced by handle is not a member of the
     *             group referenced by lockHandle
     * @throws InvalidLockStatusException
     *             if lockHandle is not locked
     */
    public boolean cancelTimer( Handle lockHandle, Handle timerHandle ) {
        synchronized( guardWorker ) {
            boolean canceled = false;
            if ( isNotNull( lockHandle ) && isNotNull( timerHandle ) ) {
                if ( selector.isRegistered( timerHandle ) && selector.isRegistered( lockHandle ) ) {
                    Handler handler = selector.handler( timerHandle );
                    if ( selector.isLockedMember( lockHandle, handler ) ) {
                        canceled = selector.cancelTimer( timerHandle );
                    } else {                
                        if ( !selector.isMember( lockHandle, handler ) ) {
                            throw( new NotGroupMemberException( ) );
                        } else {
                              throw( new InvalidLockStatusException( ) );
                        }
                    }                    
                } else {
                    // purposely does not throw an error if timerHandle is not registered
                    if ( !selector.isRegistered( lockHandle ) ) {
                        throw( new InvalidResourceException( ) );
                    }
                }
            } else {
                throw( new NullPointerException( ) );
            }
            return( canceled );
        }
    }
    
    
    // ------------------ LOCK ------------------
        
    /**
     * Creates a new handler group and registers the lockHandler to service
     * ready events from that group, based on the provided interest operations.
     * Returns a handle representing the relationship between the event source
     * and handler.
     * <p>
     * The handler group is initially empty and open. Handle is used to
     * reference the handler group.
     * 
     * @param lockHandler
     *            event handler to associate with the newly created handler
     *            group
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if lockHandler is null
     * @throws InvalidInterestOpsException
     *             if interestOps aren't in EventMask.{NOOP,LOCK}
     */
    public Handle registerLock( Handler lockHandler, int interestOps ) {        
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( lockHandler ) ) {
                handle = selector.registerLock( lockHandler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( handle );
        }
    }

        
    /**
     * Adds the event handler to the handler group referenced by lockHandle. If
     * the group is locked, the command will be queued and processed when the
     * group is unlocked. If the handler associated with the group is running,
     * the command is queued and processed when the handler completes. If the
     * command is queued and results in an error, then the errorHandler is
     * dispatched.
     * 
     * @param lockHandle
     *            handle that references the handler group to which to add the
     *            managedHandler
     * @param managedHandler
     *            handle to add to the handler group
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockhandle, managedHandler, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, managedHandler, or errorHandle is not
     *             registered
     * @throws ResourcePendingException
     *             if lockHandle is pending
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public void addMember( Handle lockHandle, Handler managedHandler, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( managedHandler ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( managedHandler ) && selector.isRegistered( errorHandle ) ) {
                    Handler lockHandler = selector.handler( lockHandle );
                    
                    if ( runLockHandlerTracker.isRunning( lockHandler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( lockHandler );
                        addMemberInternal( lockHandle, managedHandler, errorHandle, adapter );
                    } else {
                        if ( selector.isOpen( lockHandle ) ) {
                            selector.addMember( lockHandle, managedHandler );
                        } else if ( selector.isLocked( lockHandle ) ) {
                            HandlerAdapter adapter = selector.getLockedHandlerAdapter( lockHandle );
                            addMemberInternal( lockHandle, managedHandler, errorHandle, adapter );    
                        } else {
                            throw ( new InvalidLockStatusException( ) );
                        }
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }   
    }
    
    /**
     * Queues the addMember(...) command to a HandlerAdapter. The adapter may be
     * either locked or running or both.
     * <p>
     * PRECONDITION: lockHandle, handler, errorHandle, and adapter are not null
     * 
     * @param lockHandle
     *            handle that references the handler group to which to add the
     *            managedHandler
     * @param managedHandler
     *            handler to add to the handler group
     * @param errorHandler
     *            error handler that is invoked if the command is queued and
     *            results in an error
     * @param adapter
     *            the adapter to store the command
     */
    private void addMemberInternal( Handle lockHandle, Handler managedHandler, Handle errorHandle, HandlerAdapter adapter ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "member" );
        messageBlockBuilder.createMessageBlock( "add" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handler" );
        messageBlockBuilder.createMessageBlock( managedHandler );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }

    /**
     * Removes the event handler from the handler group referenced by lockHandle.
     * If the group is locked, the command will be queued and processed when the
     * group is unlocked. If the handler associated with the group is running,
     * the command is queued and processed when the handler completes. If the
     * command is queued and results in an error, then the errorHandler is
     * dispatched.
     * 
     * @param lockHandle
     *            handle that references the handler group from which to remove
     *            the managedHandler
     * @param managedHandler
     *            handler to remove from the handler group
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockhandle, managedHandler, or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle, managedHandler, or errorHandle is not
     *             registered
     * @throws ResourcePendingException
     *             if lockHandle is pending
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public void removeMember( Handle lockHandle, Handler managedHandler, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( managedHandler  ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( managedHandler ) && selector.isRegistered( errorHandle ) ) {
                    Handler lockHandler = selector.handler( lockHandle );                    
                    if ( runLockHandlerTracker.isRunning( lockHandler ) ) {
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( lockHandler );
                        removeMemberInternal( lockHandle, managedHandler, errorHandle, adapter );
                    } else {
                        if ( selector.isOpen( lockHandle ) ) {
                            selector.removeMember( lockHandle, managedHandler );
                        } else if ( selector.isLocked( lockHandle ) ) {
                            HandlerAdapter adapter = selector.getLockedHandlerAdapter( lockHandle );
                            removeMemberInternal( lockHandle, managedHandler, errorHandle, adapter );    
                        } else {
                            throw ( new InvalidLockStatusException( ) );
                        }
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }

    
    /**
     * Queues the removeMember(...) command to a HandlerAdapter. The adapter may
     * be either locked or running or both.
     * <p>
     * PRECONDITION: lockHandle, handler, errorHandle, and adapter are not null
     * 
     * @param lockHandle
     *            handle that references the handler group from which to remove
     *            the managedHandler
     * @param managedHandler
     *            handler to remove from the handler group
     * @param errorHandler
     *            error handler that is invoked if the command is queued and
     *            results in an error
     * @param adapter
     *            the adapter to store the command
     */
    private void removeMemberInternal( Handle lockHandle, Handler handler, Handle errorHandle, HandlerAdapter adapter ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "member" );
        messageBlockBuilder.createMessageBlock( "add" );
        messageBlockBuilder.createMessageBlock( "lockHandle" );
        messageBlockBuilder.createMessageBlock( lockHandle );
        messageBlockBuilder.createMessageBlock( "handler" );
        messageBlockBuilder.createMessageBlock( handler );
        messageBlockBuilder.createMessageBlock( "errorHandle" );
        messageBlockBuilder.createMessageBlock( errorHandle );
        
        adapter.command( messageBlockBuilder.getMessageBlock( ) );
    }
    
    
    /**
     * Determines if the lockHandle references a currently registered lock event
     * source. Returns true if the lockHandle is currently registered and is a
     * handle for a lock event source and false otherwise.
     * 
     * @param lockHandle
     *            reference for which to request registration status
     * @return true if the lockHandle is currently registered and is a handle
     *         for a lock event source and false otherwise
     * @throws NullPointerException
     *             if lockHandle is null
     */
    public boolean isRegisteredLock( Handle lockHandle ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;
            if ( isNotNull( lockHandle ) ) {
                isRegistered = selector.isRegisteredLock( lockHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isRegistered );
        }    
    }
    
    
    /**
     * Determines if the managedHandler is a member of the handler group
     * referenced by lockHandle. Returns true if the managedHandler is a member
     * of the handle group referenced by lockHandle.
     * 
     * @param lockHandle
     *            reference for which to request registration status
     * @return true if the managedHandler is a member of the handle group
     *         referenced by lockHandle
     * @throws NullPointerException
     *             if lockHandle or managedHandler is null
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isMember( Handle lockHandle, Handler managedHandler ) {
        synchronized( guardWorker ) {
            boolean isMember = false;
            if ( isNotNull( lockHandle ) && isNotNull( managedHandler ) ) {
                isMember = selector.isMember( lockHandle, managedHandler );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isMember );
        }
    }
    

    /**
     * Determines if the managedHandler is a member of the handler group
     * referenced by lockHandle the group is locked. Returns true if the
     * managedHandler is a member of the handle group referenced by lockHandle
     * and the group is locked and false otherwise.
     * <p>
     * PRECONDITION: lockHandle and managedHandler are not null
     * 
     * @param lockHandle
     *            reference for which to request registration status
     * @return true if the managedHandler is a member of the handle group
     *         referenced by lockHandle and the group is locked and false
     *         otherwise
     * @throws NullPointerException
     *             if lockhandle or managedHandler is null
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isLockedMember( Handle lockHandle, Handler handler ) {
        synchronized( guardWorker ) {
            boolean isLockedMember = false;
            
            if ( isNotNull( lockHandle ) && isNotNull( handler ) ) {
                isLockedMember = selector.isLockedMember( lockHandle, handler );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( isLockedMember );
        }
    }
    
    
    
    /**
     * Establishes a lock on a handler group referenced by lockHandle and
     * triggers the dispatch of a handler to service the event. A lock, once
     * achieved, guarantees that the handlers in the handler group are not
     * running and will not run until the lock is explicitly released via a call
     * to unlock.
     * <p>
     * The lock may not be achieved immediately. Handler members that are not
     * running, locked, or pending are immediately locked. Handler members that
     * could not be immediatley locked are said to be pending. Modifications to
     * an event source, handle, or handler while the handler is pending result
     * in a thrown error. Once all members of a group are locked, an event is
     * triggered and the associated handler is dispatched to service the event.
     * <p>
     * Modifications to an an event source, handle, or handler while the handler
     * is locked that do not use a method involving the lockHandle result in a
     * thrown error. Modifications that use the correct lockHandle are queued
     * and then processed when the lock is released via a call to unlock().
     * <p>
     * The event handler will be dispatched, at most, once for a lock event on a
     * lockHandle. If the lock command is queued--thus in a pending status--and
     * then the lock command fails, the supplied error event handler will be
     * dispatched. The lock request would fail, for example, if it were
     * requested and queued for a handler group that was either deregistered or
     * made empty.
     * 
     * @param lockHandle
     *            the lockHandle to lock
     * @param errorHandle
     *            the handle that receives the error event if the lock command
     *            is queued and fails
     * @throws NullPointerException
     *             if lockHandle or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle or errorHandle is not registered
     * @throws InvalidResourceException
     *             if lockHandle does not reference a valid handler group
     * @throws LockFailedException
     *             if the handler group referenced by lockHandle is empty or the
     *             handler group is not open
     */
    public void lock( Handle lockHandle, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( errorHandle ) ) {
                    Handler lockHandler = selector.handler( lockHandle );
                    if ( !runLockHandlerTracker.isRunning( lockHandler ) ) {
                        if ( selector.isOpen( lockHandle ) ) {
                            selector.lock( lockHandle, errorHandle );
                        } else {
                            throw( new LockFailedException( ) );
                        }
                    } else {

                        messageBlockBuilder.reset( );

                        messageBlockBuilder.createMessageBlock( "lock" );
                        messageBlockBuilder.createMessageBlock( "lockHandle" );
                        messageBlockBuilder.createMessageBlock( lockHandle );
                        messageBlockBuilder.createMessageBlock( "errorHandle" );
                        messageBlockBuilder.createMessageBlock( errorHandle );
                        
                        HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( lockHandler );
                        adapter.command( messageBlockBuilder.getMessageBlock( ) );
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
        }
    }
    
    
    /**
     * Unlocks a locked handler group referenced by lockHandle. Event sources
     * serviced by event handlers that are members of the handler group will
     * continue to generate ready events and have those events processed.
     * 
     * @param lockHandle
     *            the lockHandle referencing a group of event handlers to unlock
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws NullPointerException
     *             if lockHandle or errorHandle is null
     * @throws InvalidResourceException
     *             if lockHandle or errorHandle is not registered
     * @throws InvalidResourceException
     *             if lockHandle does not reference a valid handler group
     * @throws UnlockFailedException
     *             if the handler group referenced by lockHandle is not locked
     *             or the the event handler has not completed
     */
    public void unlock( Handle lockHandle, Handle errorHandle ) {
        synchronized( guardWorker ) {
            if ( isNotNull( lockHandle ) && isNotNull( errorHandle ) ) {
                if ( selector.isRegistered( lockHandle ) && selector.isRegistered( errorHandle ) ) {
                    if ( selector.isRegistered( lockHandle ) ) {
                        Handler lockHandler = selector.handler( lockHandle );
                        if ( !runLockHandlerTracker.isRunning( lockHandler ) ) {
                            if ( selector.isLocked( lockHandle ) ) {
                                selector.unlock( lockHandle, errorHandle );
                            } else {
                                throw ( new UnlockFailedException( ) );
                            }
                        } else {

                            messageBlockBuilder.reset( );

                            messageBlockBuilder.createMessageBlock( "unlock" );
                            messageBlockBuilder.createMessageBlock( "lockHandle" );
                            messageBlockBuilder.createMessageBlock( lockHandle );
                            messageBlockBuilder.createMessageBlock( "errorHandle" );
                            messageBlockBuilder.createMessageBlock( errorHandle );
                            
                            HandlerAdapter adapter = runLockHandlerTracker.getHandlerAdapter( lockHandler );
                            adapter.command( messageBlockBuilder.getMessageBlock( ) );
                            
                            selector.unlockRunning( lockHandle, errorHandle );
                        }
                    } else {
                        throw( new InvalidResourceException( ) );
                    }
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                 throw( new NullPointerException( ) );
             }
        }
    }
    
    
    
    /**
     * Determines if the handler is open.
     * 
     * @param managedHandler
     *            handler on which to query open status
     * @return true if the handler is open and false otherwise; true is
     *          returned if managedHandler is not contained in any group
     * @throws NullPointerException
     *             if managedHandler is null
     */
    public boolean isOpen( Handler managedHandler ) {
        synchronized( guardWorker ) {
            boolean isOpen = false;
            if ( isNotNull( managedHandler ) ) {
                isOpen = selector.isOpen( managedHandler );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isOpen );
        }
    }
    
    
    /**
     * Determines if the handler group referenced by lockHandle is open.
     * 
     * @param lockHandle
     *            handle on which to query open status
     * @return true if the handler group referenced by lockHandle is open and
     *          false otherwise
     * @throws NullPointerException
     *             if lockHandle is null
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isOpen( Handle lockHandle ) {
        synchronized( guardWorker ) {
            boolean isOpen = false;
            if ( isNotNull( lockHandle ) ) {
                isOpen = selector.isOpen( lockHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isOpen );
        }
    }
    
    /**
     * Determines if the handler is pending.
     * 
     * @param managedHandler
     *            handler on which to query pending status
     * @return true if the handler is pending and false otherwise
     * @throws NullPointerException
     *             if managedHandler is null
     */
    public boolean isPending( Handler managedHandler ) {
        synchronized( guardWorker ) {
            boolean isPending = false;
            if ( isNotNull( managedHandler ) ) {
                isPending = selector.isPending( managedHandler );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isPending );
        }
    }
    
    /**
     * Determines if the handler group referenced by lockHandle is pending.
     * 
     * @param lockHandle
     *            handle on which to query pending status
     * @return true if the handler group referenced by lockHandle is pending
     *          and false otherwise
     * @throws NullPointerException
     *             if lockHandle is null
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isPending( Handle lockHandle ) {
        synchronized( guardWorker ) {
            boolean isPending = false;
            if ( isNotNull( lockHandle ) ) {
                isPending = selector.isPending( lockHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isPending );
        }
    }
    
    
    /**
     * Determines if the handler is locked.
     * 
     * @param managedHandler
     *            handler on which to query lock status
     * @return true if the handler is locked and false otherwise
     * @throws NullPointerException
     *             if managedHandler is null
     */
    public boolean isLocked( Handler managedHandler ) {
        synchronized( guardWorker ) {
            boolean isLocked = false;
            if ( isNotNull( managedHandler ) ) {
                isLocked = selector.isLocked( managedHandler );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isLocked );
        }
    }
    
    /**
     * Determines if the handler group referenced by lockHandle is locked.
     * 
     * @param lockHandle
     *            handle on which to query lock status
     * @return true if the handler group referenced by lockHandle is locked and
     *          false otherwise
     * @throws NullPointerException
     *             if lockHandle is null
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isLocked( Handle lockHandle ) {
        synchronized( guardWorker ) {
            boolean isLocked = false;
            if ( isNotNull( lockHandle ) ) {
                isLocked = selector.isLocked( lockHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isLocked );
        }
    }
    
    
    

    // ------------------ SIGNAL ------------------    
    
    
    
    /**
     * Registers a signal event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event handler and interest operations.
     * <p>
     * SUPPORTED SIGNALS:
     * <p>
     * (1) shutdownhook -- Signal fired when the JVM terminates due to (a) the
     * program exiting normally such as the last non-daemon thread exits or when
     * Runtime.exit() is invoked or (b) a response from a user interrupt such a
     * CTRL-C or a system-wide event like a user logoff or system shutdown to
     * inlucde SIGUP (Unix only), SIGINT, or SIGTERM. See Chris White,
     * "Revelations on Java signal handling and termination," IBM Technical
     * Report, 1 Jan. 2002.
     * <p>
     * SIGNAL BEHAVIOR:
     * <p>
     * (1) shutdownhook -- The firing of a shutdownhook event triggers the
     * execution of the associated event handler exactly once. No event handler
     * is run if shutdownhook lacks an associated event handler. Once the
     * shutdownhook event fires, modification requests to the shutdownhook
     * handler (changing the handler and deregistration) are ignored. The
     * handler will remain registered and the JVM will not exit until the
     * Reactor exits either due to an explicit call to the shutdown() method or
     * from a call to its finalize() method during garbage collection. A program
     * should call the Reactor's shutdown() method in order to allow the program
     * and the JVM to exit. This behavior allows the Reactor-controlled program
     * time to gracefully shutdown after a shutdownhook event is detected. A
     * well-behaved program should exit as soon as possible in response to a
     * shutdownhook event.
     * 
     * @param signalName
     *            name of the signal to register
     * @param handler
     *            event handler to associate with the signal
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if signalName or handler are null
     * @throws InvalidInterestOpsException
     *             if the interestOps are not EventMask.{NOOP,SIGNAL}
     * @throws InvalidResourceException
     *             if the event source is not valid or supported
     * @throws DuplicateRegistrationException
     *             if the signal is already registered
     */
    public Handle registerSignal( String signalName, Handler handler, int interestOps ) {
        synchronized( guardWorker ) {
            Handle handle = null;
            if ( isNotNull( signalName) && isNotNull( handler ) ) {                                  
                handle = selector.registerSignal( signalName, handler, interestOps );
               } else {
                   throw( new NullPointerException( ) );
               }
            return( handle );
        }
    }
    
    
    /**
     * Determines if the signalName event source is currently registered.
     * 
     * @param signalName
     *            event source to query registration status
     * @return true if the signalName is registered and false otherwise
     * @throws NullPointerException
     *             if signalName is null
     * @throws InvalidResourceException
     *             if the signal name is not valid or the signal is not
     *             supported            
     */
    public boolean isRegisteredSignal( String signalName ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;
            if ( isNotNull( signalName ) ) {
                isRegistered = selector.isRegisteredSignal( signalName );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isRegistered );
        }
    }

    
    /**
     * Determines if the signalHandle references a currently registered signal
     * event source. Returns true if the signalHandle is currently registered
     * and is a handle for a signal event source and false otherwise.
     * 
     * @param signalHandle
     *            reference for which to request registration status
     * @return true if the signalHandle is currently registered and is a handle
     *         for a signal event source and false otherwise
     * @throws NullPointerException
     *             if signalHandle is null
     */
    public boolean isRegisteredSignal( Handle signalHandle ) {
        synchronized( guardWorker ) {
            boolean isRegistered = false;
            if ( isNotNull( signalHandle ) ) {
                isRegistered = selector.isRegisteredSignal( signalHandle );
            } else {
                throw( new NullPointerException( ) );
            }
            return( isRegistered );
        }
    }
    
    // ------------------ ERROR ------------------
    
    
    /**
     * Registers the handler to receive error events with the provided interest
     * operations and returns a handle representing relationship of handler, and
     * the interest operations. If the handler already has an associated handle
     * to receive error events, then that handle is returned and the interest
     * operations is updated.
     * 
     * @param handler
     *            handler to register to receive error events
     * @param interestOps
     *            interest operations to assign to this handler
     * @return a handle that represents the registration of this handler and the
     *         interest operations
     * @throws NullPointerException
     *             if handler is null
     * @throws InvalidInterestOpsException
     *             if interest operations is not EventMask.{NOOP,ERROR}
     */
    public Handle registerError( Handler handler, int interestOps ) {

        synchronized( guardWorker ) {               
               Handle handle = null;
               
            if ( isNotNull( handler ) ) {
                    handle = selector.registerError( handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
                                  
               return( handle );
           }
    }
    
    
    /**
     * Creates a new error handle for a previously registered handler. Any
     * events stored for the handler are discarded. If the handler did not have
     * an error handle (or otherwise was not registered), then a new error
     * handle is created and returned with its interest operations set to
     * EventMask.ERROR. The latter operation has the same result as calling
     * registerError(Handler, EventMask.ERROR).
     * 
     * @param handler
     *            event handler to generate new handle
     * @return handle a new handle, old one discarded, representing registration
     *         of handler, interest operations, and implicit error event source
     * @throws NullPointerException
     *             if handler is null
     * @throws InvalidResourceException
     *             if the handler is not registered
     */
    public Handle newErrorHandle( Handler handler ) {
        synchronized( guardWorker ) {               
               Handle handle = null;
               
            if ( isNotNull( handler ) ) {
                if ( selector.isRegisteredError( handler ) ) {
                    handle = selector.newErrorHandle( handler );
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
                                  
               return( handle );
           }
    }
    
    
    /**
     * Creates a new handle for a previously registered handle. Any events
     * stored for the handle are discarded.
     * 
     * @param errorHandle
     *            handle to generate new handle
     * @return handle a new handle, old one discarded, representing registration
     *         of handler, interest operations, and implicit error event source
     * @throws NullPointerException
     *             if errorHandle is null
     * @throws InvalidResourceException
     *             if handle is not registered as an error handle
     */
    public Handle newErrorHandle( Handle errorHandle ) {

        synchronized( guardWorker ) {               
               Handle handle = null;
               
            if ( isNotNull( errorHandle ) ) {
                if ( selector.isRegisteredError( errorHandle ) ) {
                    handle = selector.newErrorHandle( errorHandle );
                } else {
                    throw( new InvalidResourceException( ) );
                }
            } else {
                throw( new NullPointerException( ) );
            }
                                  
               return( handle );
           }
    }    
    
 
    /**
     * Returns the error handle for the handler or null if the handler is not
     * registered or does not have an associated error handle.
     * 
     * @param handler
     *            handler for which to retrieve error handle
     * @return error handle for the handler or null if the handler is not
     *         registered or does not have an associated error handle
     * @throws NullPointerException
     *             if handler is null            
     */
    public Handle getErrorHandle( Handler handler ) {
        synchronized( guardWorker ) {
            Handle errorHandle = null;
            if ( isNotNull( handler ) ) {
                errorHandle = selector.getErrorHandle( handler );
            } else {
                throw( new NullPointerException( ) );
            }
            return( errorHandle );
        }
    }
    
    
    /**
     * Determines if the handler is currently registered to service ERROR
     * events.
     * 
     * @param handler
     *            reference for which to request registration status
     * @return true if the handler is currently registered to service ERROR
     *         events and false otherwise
     * @throws NullPointerException
     *             if handler is null
     */
    public boolean isRegisteredError( Handler handler ) {
        synchronized( guardWorker ) {               
               boolean registered = false;
               
            if ( isNotNull( handler ) ) {
                registered = selector.isRegisteredError( handler );
            } else {
                throw( new NullPointerException( ) );
            }
                                  
               return( registered );
           }
    }
    
    
    /**
     * Determines if the handle references a currently registered error event
     * handle
     * 
     * @param errorHandle
     *            reference for which to request registration status
     * @return true if the handle is currently registered and is a handle for an
     *         error event source and false otherwise
     * @throws NullPointerException
     *             if errorHandle is null
     */
    public boolean isRegisteredError( Handle errorHandle ) {
        synchronized( guardWorker ) {               
               boolean registered = false;
               
            if ( isNotNull( errorHandle ) ) {
                registered = selector.isRegisteredError( errorHandle );
            } else {
                throw( new NullPointerException( ) );
            }
                                  
               return( registered );
           }
    }
    
 

    
    
    // ------------------ BLOCKING TASK ------------------
    
    
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
     * @throws NullPointerException
     *             if runnable or handler is null; or if the blocking selector
     *             is disabled
     * @throws InvalidInterestOpsException
     *             if the interestOps are not EventMask.{NOOP,BLOCKING}
     */
    public Handle registerBlocking( Runnable runnable, Handler handler, int interestOps ) {
        synchronized ( guardWorker ) {
            Handle handle = null;
            
            if ( isNotNull( runnable ) && isNotNull( handler ) ) {
                handle = selector.registerBlocking( runnable, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( handle );
        }
    }
    
    
    /**
     * Submits a group of blocking Runnable tasks with the given event handler
     * and interest operations. The task is run in the blocking thread pool.
     * Completion of the group of blocking Runnable tasks results in a
     * EventMask.BLOCKING event and is serviced by the associated handler.
     * 
     * @param runnableSet
     *            Set of Runnable tasks to execute that may block or delay
     * @param handler
     *            event handler to associate with the completion of all Runnable
     *            tasks in the runnableSet
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the set of
     *         blocking tasks event and handler
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | BLOCKING}
     * @throws NullArgumentException
     *             if runnableSet is null; if any runnable in runnableSet is
     *             null; or if the blocking selector is disabled
     */
    public Handle registerBlockingGroup( Set<Runnable> runnableSet, Handler handler, int interestOps ) {
        synchronized ( guardWorker ) {
            Handle handle = null;            
            if ( isNotNull( runnableSet ) && isNotNull( handler ) ) {
                handle = selector.registerBlockingGroup( runnableSet, handler, interestOps );
            } else {
                throw( new NullPointerException( ) );
            }
            
            return( handle );
        }
    }
    
    
    /**
     * Determines the registration status of the blockingHandle. Returns TRUE if
     * the blockingHandle is currently registered and is a handle for a blocking
     * event source and FALSE otherwise.
     * 
     * @param blockingHandle
     *            reference for which to request registration status
     * @return TRUE if the blockingHandle is currently registered and is a
     *         handle for a blocking event source and FALSE otherwise
     * @throws NullPointerException
     *             if blockingHandle is null
     */
    public boolean isRegisteredBlocking( Handle blockingHandle ) {
        synchronized( guardWorker ) {               
               boolean registered = false;
               
            if ( isNotNull( blockingHandle ) ) {
                registered = selector.isRegisteredBlocking( blockingHandle );
            } else {
                throw( new NullPointerException( ) );
            }
                                  
               return( registered );
           }
    }
    
    
    
       
       // *************************************************************************
       // ****************** JREACTOR SPECIFIC ****************** 
       // *************************************************************************   

           

    /**
     * Configures the number of worker threads in the worker thread pool. The
     * worker thread pool is used by the JReactor to execute developer-code that
     * implements the Handler interface.
     * <p>
     * Once this message completes, the worker thread pool will have
     * workerThreadNum number of worker threads.
     * <p>
     * This method should be called prior to calling dispatch().
     * <p>
     * This call is ignored if the JReactor is shutting down or is shutdown.
     * 
     * @param workerThreadNum
     *            the number of worker threads in the thread pool
     * @throws IllegalArgumentException
     *             if workerThreadNum is less than unity
     */
    public void configureWorkerThreadPool( int workerThreadNum ) {
        synchronized( guardWorker ) {
            if ( workerThreadNum > 0 ) {
                if ( !stopWorker ) {
                                    
                    workerThreadPool = Executors.newFixedThreadPool( workerThreadNum );
                    this.workerThreadNum = workerThreadNum;
                       
                }
            } else {
                throw ( new IllegalArgumentException( ) );
            }
        }
    }
    
    
    /**
     * Configures the termination timeouts for the worker thread pool. The
     * worker thread pool is used by the JReactor to execute developer-code that
     * implements the Handler interface.
     * <p>
     * Once this message completes, the worker thread pool will have: an initial
     * termination timeout wait time of terminationTimeoutFirst with units of
     * terminationTimeoutUnitsFirst, and a final termination timeout wait time
     * of terminationTimeoutLast with units terminationTimeoutUnitsLast.
     * <p>
     * The terminationTimeoutFirst is the time the shutdown process will wait
     * for running handlers to complete before attempting to directly terminate
     * the tasks. The terminationTimeoutLast is the time the shutdown process
     * will wait for running handlers to exit after an attempt was made to
     * explicitly terminate the tasks. See shutdown() for more information.
     * <p>
     * This method should be called prior to calling dispatch().
     * <p>
     * This call is ignored if the JReactor is shutting down or is shutdown.
     * 
     * @param terminationTimeoutFirst
     *            the time the shutdown process will wait for running handlers
     *            to complete before attempting to directly terminate the tasks
     * @param terminationTimeoutUnitsFirst
     *            the units for terminationTimeoutFirst
     * @param terminationTimeoutLast
     *            the time the shutdown process will wait for running handlers
     *            to exit after an attempt was made to explicitly terminate the
     *            tasks
     * @param terminationTimeoutUnitsLast
     *            the units for terminationTimeoutLast
     * @throws IllegalArgumentException
     *             if terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     */
    public void configureWorkerThreadPool( int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitsFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitsLast ) {
        synchronized( guardWorker ) {
            if ( terminationTimeoutFirst >= 0 && terminationTimeoutLast >= 0 ) {
                if ( !stopWorker ) {
                    
                    this.terminationTimeoutFirst = terminationTimeoutFirst;
                       this.terminationTimeoutUnitsFirst = terminationTimeoutUnitsFirst;
                       this.terminationTimeoutLast = terminationTimeoutLast;
                       this.terminationTimeoutUnitsLast = terminationTimeoutUnitsLast;
                       
                }
            } else {
                throw ( new IllegalArgumentException( ) );
            }
        }
    }
    
    
    /**
     * Configures the number of worker threads and termination timeouts for the
     * worker thread pool. The worker thread pool is used by the JReactor to
     * execute developer-code that implements the Handler interface.
     * <p>
     * Once this message completes, the worker thread pool will have:
     * workerThreadNum number of worker threads, an initial termination timeout
     * wait time of terminationTimeoutFirst with units of
     * terminationTimeoutUnitsFirst, and a final termination timeout wait time
     * of terminationTimeoutLast with units terminationTimeoutUnitsLast.
     * <p>
     * The terminationTimeoutFirst is the time the shutdown process will wait
     * for running handlers to complete before attempting to directly terminate
     * the tasks. The terminationTimeoutLast is the time the shutdown process
     * will wait for running handlers to exit after an attempt was made to
     * explicitly terminate the tasks. See shutdown() for more information.
     * <p>
     * This method should be called prior to calling dispatch().
     * <p>
     * This call is ignored if the JReactor is shutting down or is shutdown.
     * 
     * @param workerThreadNum
     *            the number of worker threads in the thread pool
     * @param terminationTimeoutFirst
     *            the time the shutdown process will wait for running handlers
     *            to complete before attempting to directly terminate the tasks
     * @param terminationTimeoutUnitsFirst
     *            the units for terminationTimeoutFirst
     * @param terminationTimeoutLast
     *            the time the shutdown process will wait for running handlers
     *            to exit after an attempt was made to explicitly terminate the
     *            tasks
     * @param terminationTimeoutUnitsLast
     *            the units for terminationTimeoutLast
     * @throws IllegalArgumentException
     *             if workerThreadNum is less than unity or if
     *             terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     */
    public void configureWorkerThreadPool( int workerThreadNum, int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitsFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitsLast ) {
        synchronized( guardWorker ) {
            if ( workerThreadNum > 0 && terminationTimeoutFirst >= 0 && terminationTimeoutLast >= 0 ) {
                if ( !stopWorker ) {
                                    
                    workerThreadPool = Executors.newFixedThreadPool( workerThreadNum );
                    this.workerThreadNum = workerThreadNum;
                    
                    this.terminationTimeoutFirst = terminationTimeoutFirst;
                       this.terminationTimeoutUnitsFirst = terminationTimeoutUnitsFirst;
                       this.terminationTimeoutLast = terminationTimeoutLast;
                       this.terminationTimeoutUnitsLast = terminationTimeoutUnitsLast;
                       
                }
            } else {
                throw ( new IllegalArgumentException( ) );
            }
        }
    }

    
    /**
     * Configures the termination timeouts for the critical thread pool. The
     * critical thread pool is used by the JReactor to execute developer-code,
     * implementing the Handler interface, to service critical error events.
     * <p>
     * Once this message completes, the critical thread pool will: have an
     * initial termination timeout wait time of terminationTimeoutCriticalFirst
     * with units of terminationTimeoutUnitsCriticalFirst, and a final
     * termination timeout wait time of terminationTimeoutCriticalLast with
     * units terminationTimeoutUnitsCriticalLast.
     * <p>
     * The terminationTimeoutCriticalFirst is the time the shutdown process will
     * wait for running handlers to complete before attempting to directly
     * terminate the tasks. The terminationTimeoutCriticalLast is the time the
     * shutdown process will wait for running handlers to exit after an attempt
     * was made to explicitly terminate the tasks. See criticalShutdown() for
     * more information.
     * <p>
     * This method should be called prior to beginning dispatch().
     * <p>
     * This call is ignored if the JReactor is shutting down or is shutdown.
     * 
     * @param terminationTimeoutCriticalFirst
     *            the time the shutdown process will wait for running critical
     *            handlers to complete before attempting to directly terminate
     *            the tasks
     * @param terminationTimeoutUnitsCriticalFirst
     *            the units for terminationTimeoutCriticalFirst
     * @param terminationTimeoutCriticalLast
     *            the time the shutdown process will wait for running critical
     *            handlers to exit after an attempt was made to explicitly
     *            terminate the tasks
     * @param terminationTimeoutUnitsCriticalLast
     *            the units for terminationTimeoutCriticalLast
     * @throws IllegalArgumentException
     *             if terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     */
    public void configureCriticalThreadPool( int terminationTimeoutCriticalFirst, TimeUnit terminationTimeoutUnitsCriticalFirst, int terminationTimeoutCriticalLast, TimeUnit terminationTimeoutUnitsCriticalLast ) {
        synchronized( guardWorker ) {
            if ( terminationTimeoutFirst >= 0 && terminationTimeoutLast >= 0 ) {
                if ( !stopWorker ) {
                    
                    this.terminationTimeoutCriticalFirst = terminationTimeoutCriticalFirst;
                       this.terminationTimeoutUnitsCriticalFirst = terminationTimeoutUnitsCriticalFirst;
                       this.terminationTimeoutCriticalLast = terminationTimeoutCriticalLast;
                       this.terminationTimeoutUnitsCriticalLast = terminationTimeoutUnitsCriticalLast;
                       
                }
            } else {
                throw( new IllegalArgumentException( ) );
            }
        }
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
     * @param blockingThreadNum
     *            the number of worker threads in the blocking thread pool
     * @throws IllegalArgumentException
     *             if blockingWorkerThreadNum is less than one
     * @throws NullPointerException
     *             if the blocking selector is disabled
     */
    public void configureBlockingThreadPool( int blockingThreadNum ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.configureBlockingThreadPool( blockingThreadNum );
            }                
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
     * @param terminationTimeoutUnitFirst
     *            the units for terminationTimeoutFirst
     * @param terminationTimeoutLast
     *            the time the shutdown process will wait for running tasks to
     *            exit after an attempt was made to explicitly terminate the
     *            tasks
     * @param terminationTimeoutUnitLast
     *            the units for terminationTimeoutLast
     * @throws IllegalArgumentException
     *             if terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     * @throws NullPointerException
     *             if the blocking selector is disabled
     */
    public void configureBlockingThreadPool( int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitLast ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.configureBlockingThreadPool( terminationTimeoutFirst, terminationTimeoutUnitFirst, terminationTimeoutLast, terminationTimeoutUnitLast );
            }
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
     * @param blockingThreadNum
     *            the number of worker threads in the blocking thread pool
     * @param terminationTimeoutFirst
     *            the time the shutdown process will wait for running tasks to
     *            complete before attempting to directly terminate the tasks
     * @param terminationTimeoutUnitFirst
     *            the units for terminationTimeoutFirst
     * @param terminationTimeoutLast
     *            the time the shutdown process will wait for running tasks to
     *            exit after an attempt was made to explicitly terminate the
     *            tasks
     * @param terminationTimeoutUnitLast
     *            the units for terminationTimeoutLast
     * @throws IllegalArgumentException
     *             if blockingThreadNum is less than unity or if
     *             terminationTimeOutFirst or terminationTimeoutLast are less
     *             than zero
     * @throws NullPointerException
     *             if the blocking selector is disabled
     */
    public void configureBlockingThreadPool( int blockingThreadNum, int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitLast ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.configureBlockingThreadPool( blockingThreadNum, terminationTimeoutFirst, terminationTimeoutUnitFirst, terminationTimeoutLast, terminationTimeoutUnitLast );
            }
        }
    }
    

    /**
     * Enables the QueueSelector. The QueueSelector allows for readiness
     * selection of events pertaining to MessageQueue objects.
     * <p>
     * This method should be called prior to registering any queue event
     * sources.
     * <p>
     * This call is ignored if the QueueSelector is already enabled or if the
     * JReactor is shutting down or is shutdown.
     * 
     */
    public void enableQueueSelector( ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.enableQueueSelector( );
            }
        }
    }
    
    
    
    /**
     * Enables the ChannelSelector. The ChannelSelector allows for readiness
     * selection of events pertaining to java.nio.channels.SelectableChannel
     * objects.
     * <p>
     * This method should be called prior to registering any channel event
     * sources.
     * <p>
     * This call is ignored if the ChannelSelector is already enabled or if the
     * JReactor is shutting down or is shutdown.
     * 
     */
    public void enableChannelSelector( ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.enableChannelSelector( );
            }
        }
    }
    
    
    /**
     * Enables the TimerSelector. The TimerSelector allows for readiness
     * selection of timer events.
     * <p>
     * This method should be called prior to scheduling any timer events or
     * before changing the TimeBase via a call to installTimeBase(..).
     * <p>
     * This call is ignored if the TimerSelector is already enabled or if the
     * JReactor is shutting down or is shutdown.
     * 
     */
    public void enableTimerSelector( ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.enableTimerSelector( );
            }
        }
    }

    
    /**
     * Enables the BlockingSelector. The BlockingSelector allows for readiness
     * selection of completed blocking events.
     * <p>
     * This method should be called prior to scheduling any blocking events.
     * <p>
     * This call is ignored if the BlockingSelector is already enabled or if the
     * JReactor is shutting down or is shutdown.
     * 
     */
    public void enableBlockingSelector( ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.enableBlockingSelector( );
            }
        }
    }
    

    
    /**
     * Enables the SignalSelector. The SignalSelector allows for readiness
     * selection of activated signal events.
     * <p>
     * This method should be called prior to scheduling any signal events.
     * <p>
     * This call is ignored if the SignalSelector is already enabled or if the
     * JReactor is shutting down or is shutdown.
     * 
     */
    public void enableSignalSelector( ) {
        synchronized( guardWorker ) {
            if ( !stopWorker ) {
                selector.enableSignalSelector( );
            }
        }        
    }
    
    
    
       // *************************************************************************
       // ****************** ERROR REPORTING ****************** 
       // *************************************************************************    

    /**
     * Used to report a standard-level error. It is reasonable to recover from a
     * standard error. The errorHandle maps to the Handler servicing the error,
     * and the Exception e is the exception thrown.
     * <p>
     * This method is thread-safe.
     * 
     * @param errorHandle
     *            errorHandle that maps to a Handler to service the error event
     * @param e
     *            the exception thrown
     */
    protected void reportError( Handle errorHandle, Exception e ) {
        reportError( errorHandle, e, null );
    }    
    
    
    /**
     * Used to report a standard-level error. It is reasonable to recover from a
     * standard error. The errorHandle maps to the Handler servicing the error,
     * the command is the API instruction that resulted in an error, and the
     * Exception e is the exception thrown.
     * <p>
     * This method is thread-safe.
     * 
     * @param errorHandle
     *            errorHandle that maps to a Handler to service the error event
     * @param e
     *            the exception thrown
     * @param command
     *            the API instruction that resulted in an error
     */
    protected void reportError( Handle errorHandle, Exception e, MessageBlock command ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "error" );
        messageBlockBuilder.createMessageBlock( "level" );
        messageBlockBuilder.createMessageBlock( "standard" );
        messageBlockBuilder.createMessageBlock( "exception" );
        messageBlockBuilder.createMessageBlock( e );
        
        if ( command != null ) {
            messageBlockBuilder.createMessageBlock( "info" );
            messageBlockBuilder.createMessageBlock( "command" );
            messageBlockBuilder.insertNext( command );
        }
        
        Event event = new Event( errorHandle, EventMask.ERROR, messageBlockBuilder.getMessageBlock( ) );
        selector.addReadyEvent( event );
    }
    
    
    /**
     * Used to report a critical error. It is not possible to recover from a
     * critical error. The critical event handler is dispatched to service the
     * critical error event if the JReactor has not been shutdown.
     * <p>
     * This method initializes the criticalWorkerThreadPool, if not already
     * started, unless the JReactor is shutdown.
     * <p>
     * This method is thread-safe.
     * 
     * @param e
     *            the exception thrown
     */
    protected void reportCriticalError( Exception e ) {

        messageBlockBuilder.reset( );

        messageBlockBuilder.createMessageBlock( "error" );
        messageBlockBuilder.createMessageBlock( "level" );
        messageBlockBuilder.createMessageBlock( "critical" );
        messageBlockBuilder.createMessageBlock( "exception" );
        messageBlockBuilder.createMessageBlock( e );
        
        
        synchronized( guardCritical ) {
            if ( !stopCritical ) {        
                
                if ( criticalThreadPool == null) {
                    initCriticalWorkerThreadPool( );                    
                }

                // execute the criticalErrorHandler in the criticalThreadPool
                try {
                    criticalThreadPool.execute( new Runnable( ) { public void run( ) { criticalErrorHandler.handleEvent( criticalErrorHandle, EventMask.ERROR, messageBlockBuilder.getMessageBlock( ) ); } } );
                } catch ( RejectedExecutionException ree ) {
                    // ignore
                }
            }
        }        
    }

    
    /**
     * Creates a critical worker thread pool, referenced by criticalThreadPool,
     * with one worker thread.
     */
    private void initCriticalWorkerThreadPool( ) {
        criticalThreadPool = Executors.newFixedThreadPool( 1 );
    }


       // *************************************************************************
       // ****************** ADAPTER COORDINATION ****************** 
       // *************************************************************************

    /**
     * Called by a HandlerAdapter to inform the JReactor that the Handler,
     * managed by the HandlerAdapter, has completed. The commands queued while
     * the Handler was running or locked, if any, are processed. A Handler that
     * was running or was a member of a locked group has a non-null Handle; the
     * event source, referenced by Handle, is re-enabled to generate subsequent
     * ready events. A Handler for a locked event has a null Handle; at least
     * one command, 'enableAll', is queued and all Handles are re-enabled.
     * <p>
     * PRECONDITION: handler and commands not null
     * 
     * @param handler
     *            the developer-code whose execution terminated and is
     *            coordinating its termination with the JReactor
     * @param handle
     *            the handle, serviced by handler, that references the event
     *            that generated the ready event
     * @param commands
     *            a list of commands that were queued while the handler was
     *            running
     * 
     */
    protected void resumeSelection( Handler handler, Handle handle, List<MessageBlock> commands ) {
        synchronized( guardWorker ) {
            
            runLockHandlerTracker.remove( handler );            
                        
            if ( !commands.isEmpty( ) ) {                
                processCommands( handler, handle, commands );                                                                        
            }
            
            selector.resumeSelection( handler, handle );
        }
        
    }
    
    
    /**
     * Process the queued commands for the event handler. Commands are queued
     * when an event handler is running or locked; for an event handler
     * servicing a lock group, group membership commands are queued when the
     * lockHandle is locked. The handle is a reference to the event source that
     * generating the event, having been serviced by the event handler, now
     * terminating. The handle, however, is null for those event handlers that
     * were locked.
     * <p>
     * This method requires external synchronization.
     * <p>
     * PRECONDITION: handler, handle, and commands not null; handler and handle
     * are registered
     * 
     * @param handler
     *            the event handler for which the commands were queued
     * @param handle
     *            the reference to the event source that generated the ready
     *            event; handle is null for those handlers that were locked
     * @param commands
     *            the list of queued commands for the handler
     */
    private void processCommands( Handler completedHandler, Handle completedHandle, List<MessageBlock> commands ) {
        
        boolean cont = true;
        boolean unlockDetected = false;
        
        ListIterator<MessageBlock> it = commands.listIterator( );
        String label;
        MessageBlock head;
        MessageBlock temp;
        
        while ( it.hasNext( ) && cont ) {
            head = it.next( );
            
            label = head.getMessageString( );
            if ( label.compareTo( "interestOps" ) == 0 ) {
                temp = head.getNext( );
                
                String action = temp.getMessageString( );
                temp = temp.getNext( );
                
                Handle lockHandle = null;
                Handle handle = null;
                int interestOps = 0;
                Handle errorHandle = null;
            
                label = temp.getMessageString( );
                if ( label.compareTo( "lockHandle" ) == 0 ) {
                    temp = temp.getNext( );
                    lockHandle = (Handle)temp.getMessage( );
                    temp = temp.getNext( );
                }
                
                temp = temp.getNext( );
                handle = (Handle)temp.getMessage( );

                temp = temp.getNext( 2 );
                interestOps = temp.getMessageInteger( );

                temp = temp.getNext( 2 );
                errorHandle = (Handle)temp.getMessage( );
                
                try {
                    if ( lockHandle == null ) {
                        if ( action.compareTo( "set" ) == 0 ) { 
                            interestOps( handle, interestOps, errorHandle );
                        } else if ( action.compareTo( "or" ) == 0 ) {
                            interestOpsOr( handle, interestOps, errorHandle );
                        } else {
                            interestOpsAnd( handle, interestOps, errorHandle );
                        }
                    } else {
                        if ( action.compareTo( "set" ) == 0 ) {
                            interestOps( lockHandle, handle, interestOps, errorHandle );
                        } else if ( action.compareTo( "or" ) == 0 ) {
                            interestOpsOr( lockHandle, handle, interestOps, errorHandle );
                        } else {
                            interestOpsAnd( lockHandle, handle, interestOps, errorHandle );
                        }
                    }
                } catch ( RuntimeException e ) {
                    reportError( errorHandle, e, head );
                }
                                
            } else if ( label.compareTo( "deregister" ) == 0 ) {
                temp = head.getNext( );
                
                Handle lockHandle = null;
                Handle errorHandle = null;
                
                label = temp.getMessageString( );
                if ( label.compareTo( "lockHandle" ) == 0 ) {
                    temp = temp.getNext( );
                    lockHandle = (Handle)temp.getMessage( );
                    temp = temp.getNext( );            
                }
                
                // look ahead to get errorHandle, but don't advance reference to temp pointer
                errorHandle = (Handle)temp.getNext(3).getMessage( );

                label = temp.getMessageString( );
                if ( label.compareTo( "handle" ) == 0 ) {
                    temp = temp.getNext( );
                    Handle handle = (Handle)temp.getMessage( );
                    
                    try {
                        if ( lockHandle == null ) {
                            deregister( handle, errorHandle );
                        } else {
                            deregister( lockHandle, handle, errorHandle );
                        }
                    } catch ( RuntimeException e ) {
                        reportError( errorHandle, e, head );
                    }                    
                    
                } else { // label is handler
                    temp = temp.getNext( );
                    Handler handler = (Handler)temp.getMessage( );
                    
                    try {
                        if ( lockHandle == null ) {
                            deregister( handler, errorHandle );
                        } else {
                            deregister( lockHandle, handler, errorHandle );
                        }
                    } catch ( RuntimeException e ) {
                        reportError( errorHandle, e, head );
                    }
                    
                }
                
                cont = selector.isRegistered( completedHandler );
                            
            } else if ( label.compareTo( "member" ) == 0 ) {
                Handle lockHandle = null;                
                Handler handler = null;
                Handle errorHandle = null;

                temp = head.getNext( );
                String action = temp.getMessageString( );
                
                temp = temp.getNext( 2 );
                lockHandle = (Handle)temp.getMessage( );
                
                temp = temp.getNext( 2 );
                handler = (Handler)temp.getMessage( );

                temp = temp.getNext( 2 );
                errorHandle = (Handle)temp.getMessage( );
                
                try {
                    if ( action.compareTo( "add" ) == 0 ) { 
                        addMember( lockHandle, handler, errorHandle );
                    } else {
                        removeMember( lockHandle, handler, errorHandle );
                    }
                } catch ( RuntimeException e ) {
                    reportError( errorHandle, e, head );
                }            
                
            } else if ( label.compareTo( "lock" ) == 0 ) {
                Handle lockHandle = null;                
                Handle errorHandle = null;

                temp = head.getNext( 2 );
                lockHandle = (Handle)temp.getMessage( );                

                temp = temp.getNext( 2 );
                errorHandle = (Handle)temp.getMessage( );
                
                cont = false;
                
                try {
                    lock( lockHandle, errorHandle );
                } catch ( RuntimeException e ) {
                    reportError( errorHandle, e, head );
                    cont = true;
                }
                
            } else if ( label.compareTo( "unlock" ) == 0 ) {
                Handle lockHandle = null;                
                Handle errorHandle = null;

                temp = head.getNext( 2 );
                lockHandle = (Handle)temp.getMessage( );                

                temp = temp.getNext( 2 );
                errorHandle = (Handle)temp.getMessage( );                
                
                try {
                    unlock( lockHandle, errorHandle );
                } catch ( RuntimeException e ) {
                    reportError( errorHandle, e, head );
                }
                
            } else if ( label.compareTo( "pending" ) == 0 ) {
                temp = head.getNext( );
                
                label = temp.getMessageString( );
                if ( label.compareTo( "lock" ) == 0 ) {
                    temp = temp.getNext( );
                    
                    label = temp.getMessageString( );
                    if ( label.compareTo( "lockHandle" ) == 0 ) {

                        temp = temp.getNext( );
                        Handle lockHandle = (Handle)temp.getMessage( );                    
                        
                        cont = selector.takePending( lockHandle, completedHandler );
                        
                    } else {
                        // label is 'next'
                        cont = selector.takePending( completedHandler );
                    }                    
                } else { // unlock
                    unlockDetected = true;
                }                        
            } else if ( label.compareTo( "nokey" ) == 0 ) {
                if ( !unlockDetected ) {
                    it.next( );
                }    
            }

            //    label 'enableAll' is ignored
            
        }

    }
       
    

       // *************************************************************************
       // ****************** DISPATCH ****************** 
       // *************************************************************************


    /**
     * Performs dispatch until stopped due to a call to shutdown().
     * <p>
     * This method makes a blocking call to the eventQueue, waiting for an Event
     * to become available or an interruption from another thread. An Event
     * arriving in the queue is checked for a valid handle, ready operations
     * compared to interest operations, and running and locked status before the
     * handle's associated handler is dispatched to service the Event.
     * <p>
     * A Handler will be dispatched when the Handle referencing the ready-event
     * source is (1) not null, (2) registered, (3) has ready operations in the
     * interest set operations, (3) is associated to a Handler that is not
     * running, and (4) is associated to a Handler that is open (e.g. not locked
     * or pending).
     * <p>
     * A dispatched Handler is wrapped in a HandlerAdapter and submitted to the
     * ExecutorService workerthreads for execution. The HandlerAdapter
     * coordinates the execution of the Handler--e.g. developer coder--and later
     * termination of the Handler with the JReactor via a call to
     * resumeSelection(...).
     * <p>
     * A Handler that is not dispatched is dealt with according to one of the
     * processDispatchFailxxx(...) methods.
     * <p>
     * Dispatch continues until stopped via a call to shutdown(). An Event--a
     * NOOP Event is suggested--must be added to the eventQueue after calling
     * shutdown to ensure the blocked read on the queue proceeds to the
     * shutdown() code. See the shutdown() method.
     * <p>
     * This method should only be called by the dispatch() method.
     */
    private void dispatchLoop( ) {        
    
        Event event = null;
        
        while ( !stopWorker ) {
            
            try {
                
                // blocking call to selector, waiting for: data available or InterruptedException
                event = selector.getReadyEvent( );
                
            } catch ( InterruptedException ie ) {
                
                // Blocking read interrupted
                
                
                // report critical error
                EventDispatchFailedException e = new EventDispatchFailedException( );
                reportCriticalError( e );
                
                // generate NOOP event so processDispatch will not generate an error
                event = new Event( EventMask.NOOP );
            }
                    
            processDispatch( event );                
        }
        
    }
    
    
    /**
     * Demultiplexes and dispatches a Handler to service the ready event.
     * 
     * The Event is checked for a valid handle, ready operations compared to
     * interest operations, and running and locked status before the handle's
     * associated handler is dispatched to service the Event.
     * <p>
     * A dispatched Handler is wrapped in a HandlerAdapter and submitted to the
     * ExecutorService workerthreads for execution. The HandlerAdapter
     * coordinates the termination of the Handler with the JReactor via a call
     * to resumeSelection().
     * <p>
     * This method is thread-safe.
     * <p>
     * PRECONDITION: event not null; handle in Event not null; readyOps > 0
     * 
     * @param event
     *            the event to dispatch
     */
    private void processDispatch( Event event ) {        
        
        synchronized( guardWorker ) {
            Handle handle = event.getHandle( );
            if ( selector.isRegistered( handle ) ) {
                // handle is registered
                int readyOps = event.getReadyOps( );
                int interestOps = selector.interestOps( handle );
                if ( ( readyOps & interestOps ) == readyOps ) {  // optimized: is really EventMask.contains( readyOps, interestOps ) )
                    // handle is ready for operations contained in interest operations
                    Handler handler = selector.handler( handle );
                    if ( runLockHandlerTracker.isActive( handler ) ) { // optimized: isActive is more efficient than isRunning/isLocked                                                
                        // handler is active:  could be running or locked                        
                        if ( selector.isLocked( handler ) ) { // optimized:  faster is ask selector.isLocked(handler) vs querying runLockHandlerTracker{isRunning/isLocked}
                            // FAIL: handler locked
                            processDispatchFailLocked( handler, handle, readyOps, event );
                        } else {
                            // FAIL: handler running
                            processDispatchFailRunning( handler, handle, readyOps, event );
                        }
                    } else {                        
                        // handler is not active:  idle and open (e.g. not running, not locked)
                        processDispatchSuccess( handler, handle, readyOps, event );                                            
                    }
                } else {
                    // FAIL: handle not ready for operations contained in interest operations
                    processDispatchFailOps( handle, event );
                }                
            } // FAIL: handle not registered (ignored)            
        }        
    }


    /**
     * Process an event that failed to dispatch due to ready operations on the
     * handle that are not contained in the interest operations for this handle.
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handle and event not null; readyOps valid
     * 
     * @param handle
     *            the handle with a ready operation that failed to dispatch due
     *            to ready operations not contained in the interest operations
     * @param readyOps
     *            the ready operations for the handle
     * @param event
     *            the ready event genereated by the event source
     */
    private void processDispatchFailOps( Handle handle, Event event ) {
        selector.processDispatchFailOps( handle, event ); 
    }
    
    
    /**
     * Process an event that failed to dispatch due to its handler is currently
     * running.
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handler and event not null
     * 
     * @param handler
     *            event handler, servicing the handle, that is currently running
     * @param handle
     *            the handle with a ready operation that failed to dispatch
     * @param readyOps
     *            the ready operations for the handle
     * @param event
     *            the ready event generated by the event source
     * 
     */
    private void processDispatchFailRunning( Handler handler, Handle handle, int readyOps, Event event ) {
        if ( EventMask.isQRead( readyOps ) ) {
            if ( !runLockHandlerTracker.getHandlerAdapter( handler ).eventHandleNoted( handle ) ) {
                selector.processDispatchFailRunning( handler, event );    
            }
        } else {
            selector.processDispatchFailRunning( handler, event );
        }
    }
    
    
    /**
     * Process an event that failed to dispatch due to its handler is currently
     * pending or locked.
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handler, handle, event not null; readyOps valid 
     * 
     * @param handler
     *            the event handler, servicing the handle, that is currently
     *            pending or locked
     * @param handle
     *            the handle with a ready operation that failed to dispatch due
     *            to ready operations not contained in the interest operations
     * @param readyOps
     *            the ready operations for the handle
     * @param event
     *            the ready event genereated by the event source
     */
    private void processDispatchFailLocked( Handler handler, Handle handle, int readyOps, Event event ) {
        selector.processDispatchFailLocked( handler, handle, readyOps, event );            
    }     
       
    
    /**
     * Dispatch the handler to service the ready event with ready operations on
     * the event source referenced by handle.
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handler, handle, event not null; readyOps valid
     * 
     * @param handler
     *            the event handler to be dispatched
     * @param handle
     *            the handle with a ready operation
     * @param readyOps
     *            the ready operations for the handle
     * @param event
     *            the ready event genereated by the event source
     */
    private void processDispatchSuccess( Handler handler, Handle handle, int readyOps, Event event ) {
        HandlerAdapter adapter = new HandlerAdapter( this, handler, handle, readyOps, event.getInfo( ) );                                
        runLockHandlerTracker.submitRunning( handler, adapter );
        
        try {
            workerThreadPool.execute( adapter );
        } catch ( RejectedExecutionException e ) {
            runLockHandlerTracker.remove( handler );
        }
    }     
       // *************************************************************************
       // ****************** SHUTDOWN ****************** 
       // *************************************************************************
    
    /**
     * Completes the shutdown of the JReactor. New critical error events are
     * immediately ignored then both the event dispatch path and the critical
     * event dispatch path are closed, in that order. Timer events are canceled
     * and SelectableChannels are closed, if any.
     * <p>
     * This method may block while the CompositeSelector (it's
     * blockingThreadPool), workerThreadPool, and the criticalThreadPool are
     * shutdown. A delay may result from canceling timer tasks and closing
     * SelectableChannels.
     * <p>
     * This method should only be called by the thread created from shutdown().
     */
    private void close( ) {

        // no more critical error events accepted
        synchronized ( guardCritical ) {            
            stopCritical = true;
        }
        
        
        closeWorker( );
        
        closeCritical( );
        
    }
    
    
    /**
     * Completes the shutdown of the worker thread pool and the event dispatch
     * path.
     * <p>
     * This method may block while the CompositeSelector (it's
     * blockingThreadPool) and the workerThreadPool are shutdown. A delay may
     * result from canceling timer tasks and closing SelectableChannels.
     * <p>
     * This method should only be called from close().
     */
    private void closeWorker( ) {
        
        // stop accepting new tasks; allow previously submitted tasks to execute
        workerThreadPool.shutdown( );

        // tells BlockingSelector thread pool to stop accepting new tasks
        selector.shutdownInit( ); // thread-safe call
        
        
        // this section trys to wait and gracefully shutdown threads stilling running in the thread pool; all selectors are still active to perform resumeSelection of tasks 
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
        
        
        
        /*
         * At this point:
         * <p>
         * No new tasks accepted into worker and blocking thread pools. Worker
         * thread pool has no running tasks or has one or more tasks that won't
         * terminate.
         * <p>
         * Will assume that most tasks in the worker thread pool are
         * well-behaved and will have completed by the awaitTermination timeout.
         * Tasks that are still running or additional user commands may result
         * in exceptions. The former will generate events that are added to the
         * event queue but never processed. The latter will receive appropriate
         * exceptions.
         * 
         */

        // may block while BlockingSelector thread pool is shutdown
        // may delay while ChannelSelector and TimerSelector are shutdown
        // may cause immediate exit if SignalSelector has certain JVM signals active
        selector.shutdownFinal( ); // this call is NOT thread-safe and purposely NOT sychronizing to guardWorker
        
    }
    
    
    /**
     * Completes the shutdown of the critical thread pool and the critical event
     * dispatch path.
     * <p>
     * This method may block while the criticalThreadPool is shutdown.
     * <p>
     * This method should only be called from close().
     */
    private void closeCritical( ) {
            
        boolean doShutdown = false;
        
        synchronized ( guardCritical ) {
            if ( criticalThreadPool != null ) {
                doShutdown = true;
            }
        }
        
        if ( doShutdown ) {
            
            // stop accepting new tasks; allow previously submitted tasks to execute
            criticalThreadPool.shutdown( );        

            
            // this section trys to wait and gracefully shutdown the single thread still running in the critical thread pool
            try {
                
                // blocks if any tasks are currently running, waiting up to terminationTimoutFirst or interrupted
                if ( !criticalThreadPool.awaitTermination( terminationTimeoutCriticalFirst, terminationTimeoutUnitsCriticalFirst ) ) {
                    
                    // *** pool failed to terminate ***
                    
                    // cancel currently executing tasks
                    criticalThreadPool.shutdownNow( );
                    
                    // blocks if any tasks are currently running, waiting up to terminationTimoutLast or interrupted
                    if ( !criticalThreadPool.awaitTermination( terminationTimeoutCriticalLast, terminationTimeoutUnitsCriticalLast ) ) {
                        // *** pool failed to terminate, no more tries to stop or wait for tasks to complete ***
                    }
                }
            } catch ( InterruptedException e ) { 
                // re-cancel if thread interrupted; cancel currently executing tasks
                criticalThreadPool.shutdownNow( );
            }
        }            
        
    }

    
    
    /**
     * Finalize garbage collection.
     * <p>
     * Initiates shutdown of the JReactor if not already shutdown or in the
     * process of shutting down.
     * <p>
     * This method will not block.
     * 
     */
    public void finalize( ) {
        shutdown( );
    }

    
       // *************************************************************************
       // ****************** CONVENIENCE METHODS ****************** 
       // *************************************************************************
    
    
    /**
     * Returns true if the provided object is not null and false otherwise.
     * 
     * @param o
     *            object to test for a null reference
     * @return true if the provided object is not null and false otherwise
     */
    private boolean isNotNull( Object o ) {
        boolean isNotNull = true;
        
        if ( o == null ) {
            isNotNull = false;
        }
        
        return( isNotNull );
    }
}
