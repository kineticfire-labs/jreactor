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


// Core Selector
import com.kineticfire.patterns.reactor.JReactor;
import com.kineticfire.patterns.reactor.RunLockAuthority;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import com.kineticfire.patterns.reactor.Registrar;
import com.kineticfire.patterns.reactor.EventStore;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

// Specific Selectors
import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.ChannelSelector;
import com.kineticfire.patterns.reactor.LockSelector;
import com.kineticfire.patterns.reactor.TimerSelector;
import com.kineticfire.patterns.reactor.SignalSelector;
import com.kineticfire.patterns.reactor.BlockingSelector;
import com.kineticfire.patterns.reactor.ErrorSelector;

// Handler & Adapter
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.Handler;

// Event Sources & Event
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.MessageQueue;
import java.nio.channels.SelectableChannel;

// Util
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.Date;

// Execptions
import java.lang.NullPointerException;
import java.lang.InterruptedException;
import com.kineticfire.patterns.reactor.EventDispatchFailedException;



/**
 * Implements a composite selector that performs readiness selection over all
 * supported event types: queue, channel, lock, timer, signal, error, and
 * blocking. The Selector governs the relationship between handle, handler,
 * event source, and interest operations. The Selector also manages events that
 * are fired yet not processed and thus stored for later access.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 * 
 */


public class CompositeSelector {
    
    //*********************************************
    //~ Instance/static variables
    private JReactor jreactor;                                // reference to JReactor passed to SpecificSelectors
    
    private BlockingDeque<Event> eventQueue;                // event queue
    private Registrar registrar;                            // manages registered relationship of handle, handler, selector, ops
    private EventStore eventStore;                            // holds events when handler running
    
    private QueueSelector    queueSelector;                    // queue
       private ChannelSelector  channelSelector;                // channel
       private LockSelector        lockSelector;                     // lock
       private TimerSelector    timerSelector;                   // timer
    private SignalSelector   signalSelector;                // signal
    private ErrorSelector    errorSelector;                    // error
    private BlockingSelector blockingSelector;                // blocking
    
    
       //*********************************************
       //~ Constructors
    
    
       // *******************************************************************
       // ****************** INITIALIZATION (Constructors) ****************** 
       // *******************************************************************

    
    public CompositeSelector( ) {
        jreactor = null;
        eventQueue = null;
        registrar = null;
        eventStore = null;
        queueSelector = null;
        channelSelector = null;
        lockSelector = null;
        timerSelector = null;
        signalSelector = null;
        errorSelector = null;
        blockingSelector = null;        
    }
            
    
    /**
     * Creates a CompositeSelector with a reference to the controlling JReactor and a
     * RunLockAuthority.
     * <p>
     * A blocking worker thread pool is created to execute blocking code. The
     * blocking worker thread pool uses a single worker thread. The blocking
     * worker thread pool uses default three second termination timeouts for
     * first and final shutdown subsequences. See shutdownInit() and
     * shutdownFinal() for further information. The blocking thread pool
     * characteristics may be adjusted using the
     * configureBlockingWorkerThreadPool(..) method.
     * <p>
     * The CompositeSelector initially creates three administrative threads. One thread
     * each is used for: (1) blocking worker thread pool monitor, (2) channel
     * event selection, and (3) timer event selection.
     * 
     * @param jreactor
     *            reference to jreactor
     * @param runLockAuthority
     *            reference to the entity that allows access to top-level
     *            mechanisms that enforce locks on Handlers and queries on the
     *            running and lock status of Handlers
     * @param enableAllSelectors
     *            enables readiness selection for all events sources else only
     *            enables lock and error events and disables queue, channel,
     *            timer, blocking, and signal events
     * @throws NullPointerException
     *             if jreactor or runLockAuthority is null
     */
    public CompositeSelector( JReactor jreactor, RunLockAuthority runLockAuthority, boolean enableAllSelectors ) {
        
        if ( isNotNull( jreactor) && isNotNull( runLockAuthority ) ) {
            this.jreactor = jreactor;
            
            // queue to hold ready events
              eventQueue = new LinkedBlockingDeque<Event>( );
              
            registrar = new Registrar( );
            eventStore = new EventStore( );        
            
            errorSelector = new ErrorSelector( this );
            lockSelector = new LockSelector( jreactor, this, runLockAuthority );
            
            if ( enableAllSelectors ) {
                queueSelector = new QueueSelector( this );        
                channelSelector = new ChannelSelector( this );        
                timerSelector = new TimerSelector( this );
                  signalSelector = new SignalSelector( this );
                  blockingSelector = new BlockingSelector( this );
            }
              
        } else {
            throw( new NullPointerException( ) );
        }
        
    }
    
    
    //*********************************************
       //~ METHODS
    
    
    
       // **************************************************************************
       // ****************** COMMANDS ****************** 
    // **************************************************************************
    
    
       // ------------------ HANDLE ------------------
    
    
    /**
     * Determines if this handle is currently associated to any registered
     * handler.
     * <p>
     * PRECONDITION: handle not null
     * 
     * @param handle
     *            the handle to query registration status
     * @return true if the handle is currently associated to any registered
     *         handler and false otherwise 
     */
    public boolean isRegistered( Handle handle ) {
        return( registrar.contains( handle ) );
    }
    
    
    /**
     * Returns the interest operations for the handle or -1 if the handle is not
     * registered.
     * <p>
     * PRECONDITION: handle not null
     * 
     * @param handle
     *            the handle to request interest operations
     * @return the interest operations set for this handle or -1 if the handle
     *         is not registered
     */
    public int interestOps( Handle handle ) {
        return( registrar.getInterestOps( handle ) ); 
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
     * <p>
     * PRECONDITION: handle and errorHandle not null and are registered; handle
     * is open
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOps( Handle handle, int interestOps, Handle errorHandle ) {
        registrar.getSelector( handle ).interestOps( handle, interestOps );
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
     * <p>
     * PRECONDITION: handle and errorHandle not null and are registered; handle
     * is open
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle, OR'd with the
     *            current intesest operations
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */
    public void interestOpsOr( Handle handle, int interestOps, Handle errorHandle ) {
        int currentOps = interestOps( handle );
        registrar.getSelector( handle ).interestOps( handle, currentOps | interestOps );
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
     * <p>
     * PRECONDITION: handle and errorHandle not null and are registered; handle
     * is open
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle, AND'd with the
     *            current intesest operations
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     * @throws InvalidInterestOpsException
     *             if the interest operations are not valid for this handle
     */    
    public void interestOpsAnd( Handle handle, int interestOps, Handle errorHandle ) {
        int currentOps = interestOps( handle );
        registrar.getSelector( handle ).interestOps( handle, currentOps & interestOps );
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
     * PRECONDITION: handle and errorHandle not null and are registered; handle
     * is open
     * 
     * @param handle
     *            the handle to deregister
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     */
    public void deregister( Handle handle, Handle errorHandle ) {
        registrar.getSelector( handle ).deregister( handle );
    }

    
    /**
     * Retrieves the handler currently associated to the given handle. Returns
     * null if the handle is not associated to a currently registered handler.
     * <p>
     * PRECONDITION: handle not null
     *  
     * @param handle
     *            handle used to retrieve the associated handler
     * @return the handler associated with the handle or null if the handle is
     *         not associated to a registered handler
     */
    public Handler handler( Handle handle ) {
        return( registrar.getHandler( handle ) );
    }
    
    
    //     ------------------ HANDLER ------------------
    
    
    /**
     * Determines if this handler is currently registered.
     * <p>
     * PRECONDITION: handler is not null 
     * 
     * @param handler
     *            the handler to query registration status
     * @return true if the handler is currently registered and false otherwise
     */
    public boolean isRegistered( Handler handler ) {
        return( registrar.contains( handler ) );
    }
    
    
    /**
     * Returns a set of handles that are associated to the given handler. If the
     * handler is not registered, then an empty set is returned.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            event handler for which to return the set of associated
     *            handles
     * @return a set of associated handles for the handler; if the handler is
     *         not registered, an empty set is returned
     */
    public Set<Handle> handles( Handler handler ) {
        Set<Handle> handles = new HashSet<Handle>( );
        handles.addAll( registrar.getHandles( handler ) );
        return( handles );
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
     * <p>
     * PRECONDITION: handler and errorHandle not null and are registered;
     * handler is open
     * 
     * @param handler
     *            the handler to deregister
     * @param errorHandle
     *            the handle that receives the error event if this command is
     *            queued and fails
     */
    public void deregister( Handler handler, Handle errorHandle ) {
        
        // a Handler is deregistered by individually deregistering its associated Handles
        
        List<Handle> handles = new LinkedList<Handle>( );
        handles.addAll( registrar.getHandles( handler ) );                    
        
        for ( Handle currentHandle : handles ) {
            registrar.getSelector( currentHandle ).deregister( currentHandle );
        }
        
    }        
    
    
    
    // ------------------ QUEUE ------------------

    /**
     * Registers a queue event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event source and handler.
     * <p>
     * PRECONDITION: queue and handler are not null
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
     *             if the queue selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't either EventMask.{NOOP |
     *             EventMask.QREAD}
     * @throws DuplicateRegistrationException
     *             if the queue is already registered
     */
       public Handle registerQueue( MessageQueue queue, Handler handler, int interestOps ) {   
        Handle handle = null;    
        if ( EventMask.xIsQRead( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = queueSelector.register( queue, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
                        
        return( handle );
    }
       
    
       /**
     * Determines the registration status of the queue. Returns true if the
     * queue is currently registered and false otherwise.
     * <p>
     * PRECONDITION: queue is not null
     * 
     * @param queue
     *            event source for which to request registration status
     * @return true if the queue is currently registered and false otherwise
     * @throws NullPointerException
     *             if the queue selector is disabled
     */
    public boolean isRegisteredQueue( MessageQueue queue ) {
        return( queueSelector.isRegistered( queue ) );
    }
    
    
    /**
     * Determines if the queueHandle references a currently registered queue
     * event source. Returns true if the queueHandle is currently registered and
     * is a handle for a queue event source and false otherwise.
     * <p>
     * PRECONDITION: queueHandle is not null
     * 
     * @param queueHandle
     *            reference for which to request registration status
     * @return true if the queueHandle is currently registered and is a handle
     *         for a queue event source and false otherwise
     * @throws NullPointerException
     *             if the queue selector is disabled
     */
    public boolean isRegisteredQueue( Handle queueHandle ) {
        return( queueSelector.isRegistered( queueHandle ) );            
    }
    
    
    // ------------------ CHANNEL ------------------
    
    /**
     * Registers a channel event source with an event handler and interest
     * operations. Returns a handle representing the relationship between the
     * event source and handler.
     * <p>
     * Configures the channel for non-blocking mode.
     * <p>
     * PRECONDITION: channel and handler not null
     * 
     * @param channel
     *            event source to register
     * @param handler
     *            event handler to associate with the channel
     * @param interestOps
     *            interest operations to assign the channel
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws InvalidInterestOpsException
     *             if interestOps aren't either EventMask.{NOOP | CREAD | CWRITE |
     *             ACCEPT | CONNECT}
     * @throws NullPointerException
     *             if the channel selector is disabled
     * @throws DuplicateRegistrationException
     *             if the channel is already registered
     * @throws IllegalResourceStateException
     *             if an error occurred while registering the channel to the
     *             java.nio.channels.Selector
     */
    public Handle registerChannel( SelectableChannel channel, Handler handler, int interestOps ) {
        Handle handle = null;
        
        if ( EventMask.xIsChannel( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = channelSelector.register( channel, handler, interestOps );
        } else {
            throw ( new InvalidInterestOpsException( interestOps ) );
        }
        
        return( handle );
    }
    
    
    /**
     * Determines the registration status of the channel. Returns true if the
     * channel is currently registered and false otherwise.
     * 
     * @param channel
     *            event source for which to request registration status
     * @return true if the channel is currently registered and false otherwise
     * @throws NullPointerException
     *             if the channel selector is disabled
     */
    public boolean isRegisteredChannel( SelectableChannel channel ) {
           return( channelSelector.isRegistered( channel ) );
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
     *             if the channel selector is disabled
     */
    public boolean isRegisteredChannel( Handle channelHandle ) {
           return( channelSelector.isRegistered( channelHandle ) );
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
     * <p>
     * PRECONDITION: handler not null
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
     *             if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerOnce( Date time, Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = timerSelector.registerOnce( time, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle );
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
     * <p>
     * PRECONDITION: handler not null
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
     *             if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerOnce( long delay, Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = timerSelector.registerOnce( delay, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle ); 
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
     * <p>
     * PRECONDITION: handler not null
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
     *             if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedDelay( Date firstTime, long period, Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = timerSelector.registerFixedDelay( firstTime, period, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle );
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
     *             if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if delay is negative, or the sume delay and
     *             System.currentTimeMillis() is negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedDelay( long delay, long period, Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = timerSelector.registerFixedDelay( delay, period, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle );
    }
    
    /**
     * Registers a timer event to fire at a fixed-rate beginning at firstTime.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the event source and handler.
     * <p>
     * The handler will be dispatched for each timer expired event until
     * explicitly canceled or deregistered.
     * <p>
     * For fixed-rate execution, the execution time is scheduled relative the
     * initial execution time. This type of execution is appropriate for tasks
     * that are sensitive to absolute time, like sounding alarm at a recurring
     * time, or where the number of executions is critical to the task, such as
     * a count-down alarm.
     * <p>
     * See java.util.Timer scheduleAtFixedRate(TimerTask,Date,long).
     * <p>
     * PRECONDITION: handler not null
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
     *             if the timer selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timer selector encountered a fatal error
     */
    public Handle registerTimerFixedRate( Date firstTime, long period, Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = timerSelector.registerFixedRate( firstTime, period, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle );
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
     * <p>
     * PRECONDITION: handler not null
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
     *             if the timer selector is disabled
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
    public Handle registerTimerFixedRate( long delay, long period, Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = timerSelector.registerFixedRate( delay, period, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle );
    }
    
    
    /**
     * Determines the registration status of the timerHandle. Returns TRUE if
     * the timerHandle is currently registered and is a handle for a timer event
     * source and FALSE otherwise.
     * <p>
     * PRECONDITION: handler not null
     * 
     * @param timerHandle
     *            reference for which to request registration status
     * @return TRUE if the timerHandle is currently registered and is a handle
     *         for a timer event source and FALSE otherwise
     * @throws NullPointerException
     *             if the timer selector is disabled
     */
    public boolean isRegisteredTimer( Handle timerHandle ) {        
        return( timerSelector.isRegistered( timerHandle ) );
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
     *             if the timer selector is disabled
     */
    public boolean cancelTimer( Handle timerHandle ) {
        return( timerSelector.cancel( timerHandle ) );
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
     * <p>
     * PRECONDITION: lockHandler not null
     * 
     * @param lockHandler
     *            event handler to associate with the newly created handler
     *            group
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler          
     * @throws InvalidInterestOpsException
     *             if interestOps aren't in EventMask.{NOOP,LOCK}
     */
    public Handle registerLock( Handler lockHandler, int interestOps ) {        
        Handle handle = null;
        if ( EventMask.xIsLock( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = lockSelector.register( lockHandler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        
        return( handle );
    }

        
    /**
     * Adds an event handler to the handler group referenced by lockHandle. If
     * the group is locked, the command will be queued and processed when the
     * group is unlocked. If the handler associated with the group is running,
     * the command is queued and processed when the handler completes. If the
     * command is queued and results in an error, then the errorHandler is
     * dispatched.
     * <p>
     * PRECONDITION: lockHandle, managedHandler, and errorHandle not null and
     * all registered; lockHandler not running; lockHandle is open
     * 
     * @param lockHandle
     *            handle that references the handler group to which to add the
     *            managedHandler
     * @param managedHandler
     *            handle to add to the handler group           
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public void addMember( Handle lockHandle, Handler managedHandler ) {            
        lockSelector.addMember( lockHandle, managedHandler );
    }
    

    /**
     * Removes an event handler from the handler group referenced by lockHandle.
     * If the group is locked, the command will be queued and processed when the
     * group is unlocked. If the handler associated with the group is running,
     * the command is queued and processed when the handler completes. If the
     * command is queued and results in an error, then the errorHandler is
     * dispatched.
     * <p>
     * PRECONDITION: lockHandle, managedHandler, and errorHandle not null and
     * all registered; lockHandler not running; lockHandle is open
     *  
     * @param lockHandle
     *            handle that references the handler group from which to remove
     *            the managedHandler
     * @param managedHandler
     *            handler to remove from the handler group
     * @throws NullPointerException
     *             if lockhandle, managedHandler, or errorHandle is null
     * @throws NotRegisteredException
     *             if lockHandle, managedHandler, or errorHandle is not
     *             registered
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public void removeMember( Handle lockHandle, Handler managedHandler ) {
        lockSelector.removeMember( lockHandle, managedHandler );
    }
    
    
    /**
     * Determines if the lockHandle references a currently registered lock event
     * source. Returns true if the lockHandle is currently registered and is a
     * handle for a lock event source and false otherwise.
     * <p>
     * PRECONDITION: lockHandle is not null
     * 
     * @param lockHandle
     *            reference for which to request registration status
     * @return true if the lockHandle is currently registered and is a handle
     *         for a lock event source and false otherwise
     * @throws NullPointerException
     *             if lockHandle is null
     */
    public boolean isRegisteredLock( Handle lockHandle ) {
        return( lockSelector.isRegistered( lockHandle ) );            
    }
    
    
    /**
     * Determines if the managedHandler is a member of the handler group
     * referenced by lockHandle. Returns true if the managedHandler is a member
     * of the handle group referenced by lockHandle.
     * <p>
     * PRECONDITION: lockHandle and managedHandler are not null
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
        return( lockSelector.isMember( lockHandle, managedHandler ) );            
    }
    
    /**
     * Establishes a lock on a handler group referenced by lockHandle. A lock,
     * once achieved, guarantees that the handlers in the handler group are not
     * running and will not run until the lock is explicitly released via a call
     * to unlock. The handler associated with the handler group will (usually)
     * run exactly once.
     * <p>
     * The lock may not be achieved immediately.
     * <p>
     * Handler members that could not be immediatley locked are said to be
     * pending. Modifications to an event source, handle, or handler while the
     * handler is pending are ignored.
     * <p>
     * Modifications to an an event source, handle, or handler while the handler
     * is locked that do not use a method involving the lockHandle are ignored.
     * Modifications that use the correct lockHandle are queued and then
     * processed when the lock is released.
     * <p>
     * The event handler will be dispatched exactly once for a lock event on a
     * lockHandle. If the lock command is queued--thus in a pending status--and
     * then the lock command fails, the supplied error event handler will be
     * dispatched. The lock request would fail, for example, if it were
     * requested and queued for a handler group that was either deregistered or
     * made empty.
     * <p>
     * PRECONDITION: lockHandle and errorHandle not null and registered;
     * lockHandle open; lockHandler not running
     * 
     * @param lockHandle
     *            the lockHandle to lock
     * @param errorHandle
     *            the handle that receives the error event if the lock command
     *            is queued and fails
     * @throws InvalidResourceException
     *             if lockHandle does not reference a valid handler group
     * @throws LockFailedException
     *             if the handler group referenced by lockHandle is empty or the
     *             handler group is not open
     */
    public void lock( Handle lockHandle, Handle errorHandle ) {
        lockSelector.lock( lockHandle, errorHandle );                    
    }    
    
    
    /**
     * Unlocks a locked handler group referenced by lockHandle. Event sources
     * serviced by event handlers that are members of the handler group will
     * continue to generate ready events and have those events processed.
     * <p>
     * PRECONDITION: lockHandle and errorHandle not null and registered;
     * lockHandle locked; lockHandler not running
     * 
     * @param lockHandle
     *            the lockHandle referencing a group of event handlers to unlock
     * @param errorHandle
     *            the handle that receives the error event if the queued command
     *            fails
     * @throws InvalidResourceException
     *             if lockHandle does not reference a valid handler group
     * @throws UnlockFailedException
     *             if the handler group referenced by lockHandle is not locked
     *             or the the event handler has not completed
     */
    public void unlock( Handle lockHandle, Handle errorHandle ) {
        lockSelector.unlock( lockHandle );
    }
    
    
    /**
     * Informs the LockSelector of an 'unlock' request on a lockHandle for
     * which the handler is currently running. The LockSelector will queue a
     * message in each handler that is a member of the group referenced by
     * lockHandle.
     * <p>
     * PRECONDITION: lockHandle and errorHandle not null and registered;
     * lockHandle locked; lockHandler running
     * 
     * @throws InvalidResourceException
     *             if lockHandle does not reference a valid handler group
     * @throws UnlockFailedException
     *             if the handler group referenced by lockHandle is not locked
     *             or the the event handler has not completed
     */
    public void unlockRunning( Handle lockHandle, Handle errorHandle ) {
        lockSelector.unlockRunning( lockHandle );
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
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isLockedMember( Handle lockHandle, Handler handler ) {
        return( lockSelector.isLockedMember( lockHandle, handler ) );            
    }
    
            
    /**
     * Determines if the handler is open.
     * <p>
     * PRECONDITION: managedHandler not null
     * 
     * @param managedHandler
     *            handler on which to query open status
     * @return true if the handler is open and false otherwise; true is
     *          returned if managedHandler is not contained in any group
     */
    public boolean isOpen( Handler managedHandler ) {
        return( lockSelector.isOpen( managedHandler ) );
    }
    
    
    /**
     * Determines if the handler group referenced by lockHandle is open.
     * <p>
     * PRECONDITION: lockHandle not null 
     * 
     * @param lockHandle
     *            handle on which to query open status
     * @return true if the handler group referenced by lockHandle is open and
     *          false otherwise
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isOpen( Handle lockHandle ) {
        return( lockSelector.isOpen( lockHandle ) );
    }
    
    /**
     * Determines if the handler is pending.
     * <p>
     * PRECONDITION: managedHandler not null
     * 
     * @param managedHandler
     *            handler on which to query pending status
     * @return true if the handler is pending and false otherwise
     */
    public boolean isPending( Handler managedHandler ) {
        return( lockSelector.isPending( managedHandler ) );
    }
    
    /**
     * Determines if the handler group referenced by lockHandle is pending.
     * <p>
     * PRECONDITION: lockHandle not null 
     * 
     * @param lockHandle
     *            handle on which to query pending status
     * @return true if the handler group referenced by lockHandle is pending
     *          and false otherwise
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isPending( Handle lockHandle ) {
        return( lockSelector.isPending( lockHandle ) );
    }
    
    
    /**
     * Determines if the handler is locked.
     * <p>
     * PRECONDITION: managedHandler not null
     * 
     * @param managedHandler
     *            handler on which to query lock status
     * @return true if the handler is locked and false otherwise
     */
    public boolean isLocked( Handler managedHandler ) {
        return( lockSelector.isLocked( managedHandler ) );
    }
    
    /**
     * Determines if the handler group referenced by lockHandle is locked.
     * <p>
     * PRECONDITION: lockHandle not null 
     * 
     * @param lockHandle
     *            handle on which to query lock status
     * @return true if the handler group referenced by lockHandle is locked and
     *          false otherwise
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isLocked( Handle lockHandle ) {
        return( lockSelector.isLocked( lockHandle ) );
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
     * <p>
     * PRECONDITION: signalName and handler are not null
     * 
     * @param signalName
     *            name of the signal to register
     * @param handler
     *            event handler to associate with the signal
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws InvalidInterestOpsException
     *             if the interestOps are not EventMask.{NOOP,SIGNAL}
     * @throws InvalidResourceException
     *             if the event source is not valid or supported
     * @throws DuplicateRegistrationException
     *             if the signal is already registered
     */
    public Handle registerSignal( String signalName, Handler handler, int interestOps ) {
        Handle handle = null;                           
        if ( EventMask.xIsSignal( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = signalSelector.register( signalName, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
        return( handle );
    }
    
    
    /**
     * Determines if the signalName event source is currently registered.
     * <p>
     * PRECONDITION: signalName is not null
     * 
     * @param signalName
     *            event source to query registration status
     * @return true if the signalName is registered and false otherwise
     * @throws InvalidResourceException
     *             if the signal name is not valid or the signal is not
     *             supported            
     */
    public boolean isRegisteredSignal( String signalName ) {
        return( signalSelector.isRegistered( signalName ) );
    }

    
    /**
     * Determines if the signalHandle references a currently registered signal
     * event source. Returns true if the signalHandle is currently registered
     * and is a handle for a signal event source and false otherwise.
     * <p>
     * PRECONDITION: signalHandle is not null
     * 
     * @param signalHandle
     *            reference for which to request registration status
     * @return true if the signalHandle is currently registered and is a handle
     *         for a signal event source and false otherwise
     */
    public boolean isRegisteredSignal( Handle signalHandle ) {
        return( signalSelector.isRegistered( signalHandle ) );
    }
    
    
    // ------------------ ERROR ------------------
    
    
    /**
     * Registers the handler to receive error events with the provided interest
     * operations and returns a handle representing relationship of handler, and
     * the interest operations. If the handler already has an associated handle
     * to receive error events, then that handle is returned and the interest
     * operations are updated.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            handler to register to receive error events
     * @param interestOps
     *            interest operations to assign to this handler
     * @return a handle that represents the registration of this handler and the
     *         interest operations
     * @throws InvalidInterestOpsException
     *             if interest operations is not EventMask.{NOOP,ERROR}
     */
    public Handle registerError( Handler handler, int interestOps ) {
        Handle handle = null;

        if ( EventMask.xIsError( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = errorSelector.register( handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( interestOps ) );
        }
                           
        return( handle );
    }
    
    
    /**
     * Creates a new error handle for a previously registered handler. Any
     * events stored for the handler are discarded. If the handler did not have
     * an error handle (or otherwise was not registered), then a new error
     * handle is created and returned with its interest operations set to
     * EventMask.ERROR. The latter operation has the same result as calling
     * registerError(Handler, EventMask.ERROR).
     * <p>
     * PRECONDITION: handler not null
     * 
     * @param handler
     *            event handler to generate new handle
     * @return handle a new handle, old one discarded, representing registration
     *         of handler, interest operations, and implicit error event source
     */
    public Handle newErrorHandle( Handler handler ) {
        return( errorSelector.newErrorHandle( handler ) );
    }
    
    
    /**
     * Creates a new handle for a previously registered handle. Any events
     * stored for the handle are discarded.
     * <p>
     * PRECONDITION: errorHandle not null
     * 
     * @param errorHandle
     *            handle to generate new handle
     * @return handle a new handle, old one discarded, representing registration
     *         of handler, interest operations, and implicit error event source
     * @throws InvalidResourceException
     *             if handle is not registered as an error handle
     */
    public Handle newErrorHandle( Handle errorHandle ) {
        return( errorSelector.newErrorHandle( errorHandle ) );
    }
    
    
    /**
     * Returns the error handle for the handler or null if the handler is not
     * registered or does not have an associated error handle.
     * <p>
     * PRECONDITION: handler not null
     * 
     * @param handler
     *            handler for which to retrieve error handle
     * @return error handle for the handler or null if the handler is not
     *         registered or does not have an associated error handle            
     */
    public Handle getErrorHandle( Handler handler ) {
        return( errorSelector.getErrorHandle( handler ) ); 
    }
    
    
    /**
     * Determines if the handler is currently registered to service ERROR
     * events.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            reference for which to request registration status
     * @return true if the handler is currently registered to service ERROR
     *         events and false otherwise
     */
    public boolean isRegisteredError( Handler handler ) {
        return( errorSelector.isRegistered( handler ) );
    }
    
    
    /**
     * Determines if the handle references a currently registered error event
     * handle
     * <p>
     * PRECONDITION: errorHandle not null
     * 
     * @param errorHandle
     *            reference for which to request registration status
     * @return true if the handle is currently registered and is a handle for an
     *         error event source and false otherwise
     */
    public boolean isRegisteredError( Handle errorHandle ) {
        return( errorSelector.isRegistered( errorHandle ) );
    }

    
    
    // ------------------ BLOCKING TASK ------------------
    
    
    /**
     * Submits a blocking Runnable task with the given event handler and
     * interest operations. The task is run in the blocking thread pool.
     * Completion of the blocking runnable task results in a EventMask.BLOCKING
     * event and is serviced by the associated handler.
     * <p>
     * PRECONDITION: runnable and handler are not null
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
     * @throws InvalidInterestOpsException
     *             if the interestOps are not EventMask.{NOOP,BLOCKING}
     */
    public Handle registerBlocking( Runnable runnable, Handler handler, int interestOps ) {
        Handle handle = null;
        
        if ( EventMask.xIsBlocking( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = blockingSelector.register( runnable, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( ) );
        }
                
        return( handle );
    }
    
    
    /**
     * Submits a group of blocking Runnable tasks with the given event handler
     * and interest operations. The task is run in the blocking thread pool.
     * Completion of the group of blocking Runnable tasks results in a
     * EventMask.BLOCKING event and is serviced by the associated handler.
     * <p>
     * PRECONDITION: runnableSet and handler are not null
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
     *             if any Runnable in the runnableSet is null; if the blocking
     *             selector is disabled
     * @throws IllegalArgumentException
     *             if runnableSet is empty
     */
    public Handle registerBlockingGroup( Set<Runnable> runnableSet, Handler handler, int interestOps ) {
        Handle handle = null;
        
        if ( EventMask.xIsBlocking( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            handle = blockingSelector.registerGroup( runnableSet, handler, interestOps );
        } else {
            throw( new InvalidInterestOpsException( ) );
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
    public boolean isRegisteredBlocking( Handle blockingHandle ) {               
        return( blockingSelector.isRegistered( blockingHandle ) );
    }

    
       // *************************************************************************
       // ****************** JREACTOR SPECIFIC ****************** 
       // *************************************************************************
            

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
        blockingSelector.configureBlockingThreadPool( blockingThreadNum );
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
        blockingSelector.configureBlockingThreadPool( terminationTimeoutFirst, terminationTimeoutUnitFirst, terminationTimeoutLast, terminationTimeoutUnitLast );
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
        blockingSelector.configureBlockingThreadPool( blockingThreadNum, terminationTimeoutFirst, terminationTimeoutUnitFirst, terminationTimeoutLast, terminationTimeoutUnitLast );
    }
    

    /**
     * Enables the QueueSelector. The QueueSelector allows for readiness
     * selection of events pertaining to MessageQueue objects.
     * <p>
     * This method should be called prior to registering any queue event
     * sources.
     * <p>
     * This call is ignored if the QueueSelector is already enabled.
     * 
     */
    public void enableQueueSelector( ) {
        if ( queueSelector == null ) {
            queueSelector = new QueueSelector( this );
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
     * This call is ignored if the ChannelSelector is already enabled.
     * 
     */
    public void enableChannelSelector( ) {
        if ( channelSelector == null ) {
            channelSelector = new ChannelSelector( this );
        }
    }
    
    
    /**
     * Enables the TimerSelector. The TimerSelector allows for readiness
     * selection of timer events.
     * <p>
     * This method should be called prior to registering any timer events.
     * <p>
     * This call is ignored if the TimerSelector is already enabled.
     * 
     */
    public void enableTimerSelector( ) {
        if ( timerSelector == null ) {
            timerSelector = new TimerSelector( this );
        }
    }

    
    /**
     * Enables the BlockingSelector. The BlockingSelector allows for readiness
     * selection of completed blocking events.
     * <p>
     * This method should be called prior to registering any blocking events.
     * <p>
     * This call is ignored if the BlockingSelector is already enabled.
     * 
     */
    public void enableBlockingSelector( ) {
        if ( blockingSelector == null ) {
            blockingSelector = new BlockingSelector( this );
        }
    }
    

    
    /**
     * Enables the SignalSelector. The SignalSelector allows for readiness
     * selection of activated signal events.
     * <p>
     * This method should be called prior to registering any signal events.
     * <p>
     * This call is ignored if the SignalSelector is already enabled.
     * 
     */
    public void enableSignalSelector( ) {
        if ( signalSelector == null ) {
            signalSelector = new SignalSelector( this );
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
        jreactor.reportError( errorHandle, e );
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
        jreactor.reportError( errorHandle, e, command );
    }
    
    
    /**
     * Used to report a critical error. It is not possible to recover from a
     * critical error.
     * <p>
     * This method is thread-safe.
     * 
     * @param e
     *            the exception thrown
     */
    protected void reportCriticalError( Exception e ) {                
        jreactor.reportCriticalError( e );
    }
    
       // *************************************************************************
       // ****************** ADAPTER COORDINATION ****************** 
       // *************************************************************************
    
    /**
     * Performs the process of resume selection for the handler and handle.
     * <p>
     * Transfers all ready events, if any, to the event queue for dispatch. If
     * handle is non-null, the handle's selector is notified to request the
     * handle generate further ready events as appropriate. If the handle is
     * null, then the selectors for all handles serviced by the handler are
     * notified to request that handles generate further ready events as
     * appropriate.
     * 
     * @param handler
     *            the event handler to resume selection
     * @param handle
     *            the handle to resume selection if non-null; else, resume
     *            selection if performed on all handles serviced by the event
     *            handler
     */
    public void resumeSelection( Handler handler, Handle handle ) {
        
        transferReadyEvents( handler );
        
        if ( handle != null ) {
            try {
                registrar.getSelector( handle ).resumeSelection( handle );
            } catch ( NullPointerException e ) {
                // ignore:  handle was deregistered
            }
        } else {            
            List<Handle> handles = registrar.getHandles( handler );   // returns an empty List if handler not contained (e.g. deregistered)            
            for ( Handle localHandle : handles ) {
                registrar.getSelector( localHandle ).resumeSelection( localHandle );
            }
        }
        
    }
    
    
    /**
     * Transfer control of the managedHandler to the LockedSelector. Typically,
     * the managedHandler was running with a queued message indicating that the
     * managedHandler should be locked; the managedHandler is passed to the
     * LockedSelector by the JReactor upon resumeSelection() of the
     * managedHandler.
     * <p>
     * The managedHandler will be taken by LockSelector (with a boolean return
     * value of true) if the lockHandle provided is next in FIFO ordering for a
     * lock request on the managedHandler. Else, false is returned and the
     * managedHandler is not controlled by LockSelector.
     * <p>
     * PRECONDITION: managedHandler and lockHandle not null and are registered
     * 
     * @param lockHandle
     *            the lockHandle which manages the handler
     * @param handler
     *            handler to assume control over
     * @return true if managedHandler is now controlled by LockSelector and
     *         false otherwise
     */
    public boolean takePending( Handle lockHandle, Handler handler ) {
        return( lockSelector.takePending( lockHandle, handler ) );
    }
    
    
    /**
     * Transfer control of the managedHandler to the LockedSelector. Typically,
     * the managedHandler was running with a queued message indicating that the
     * managedHandler should be locked; the managedHandler is passed to the
     * LockedSelector by the JReactor upon resumeSelection() of the
     * managedHandler.
     * <p>
     * The managedHandler will be taken by LockSelector (with a boolean return
     * value of true) if the any lockHandle has a pending request on the
     * managedHandler. Else, false is returned and the managedHandler is not
     * controlled by LockSelector.
     * <p>
     * PRECONDITION: managedHandler not null and is registered
     * 
     * @param handler
     *            handler to assume control over
     * @return true if managedHandler is now controlled by LockSelector and
     *         false otherwise
     */
    public boolean takePending( Handler handler ) {
        return( lockSelector.takePending( handler ) );
    }
    
    
    
    
       // ******************************************************************************************
       // ****************** SPECIFIC-SELECTOR COORDINATION ****************** 
       // ******************************************************************************************
        
       /**
     * Registers the handler and associates it the handle with the given
     * interest operations. The given SpecificSelector is assigned to manage the
     * handle. This information is committed to the Registrar.
     * <p>
     * PRECONDITIONS: handle, handler, and selector are not null; interestOps
     * are valid for this handle
     * 
     * @param handle
     *            the handle to register
     * @param handler
     *            the handler to register
     * @param specificSelector
     *            the selector that owns the handle
     * @param interestOps
     *            the interest operations to set for this handle
     * 
     */       
       protected void processRegister( Handle handle, Handler handler, SpecificSelector specificSelector, int interestOps ) {
           registrar.add( handle, handler, specificSelector, interestOps );
       }
       
       
       /**
     * Sets the interest operations for this handle to the Registrar.
     * <p>
     * PRECONDITIONS: handle is not null; handle is registered; interestOps are
     * valid for this handle
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for this handle
     * 
     */
       protected void processInterestOps( Handle handle, int interestOps ) {
           registrar.setInterestOps( handle, interestOps );
       }
       
       
       /**
     * Deregisters this handle from the Registrar.
     * <p>
     * If this method removed the last handle serviced by a handler, then that
     * handler is also deregistered. All references to the handler are removed
     * from the Registrar, the handler is removed from all groups in the
     * LockSelector, and all ready events for the handler are removed from the
     * EventStore.
     * <p>
     * PRECONDITIONS: handle is not null; handle is deregistered
     * 
     * @param handle
     *            the handle to deregister
     * 
     */
       protected void processDeregister( Handle handle ) {
           Handler handler = registrar.getHandler( handle );
           boolean handlerRemoved = registrar.remove( handle );
           if ( handlerRemoved ) {
               processDeregister( handler );
           }           
       }
       
       
       /**
     * Completes deregistration of handler by coordinating with EventStore and
     * LockSelector. All events associated with the handler are discarded, and
     * the handler is removed from all groups in the LockSelector.
     * <p>
     * Method is called to remove Handler when all associated Handles have been
     * deregistered.
     * <p>
     * PRECONDITIONS: handler is not null
     * 
     * @param handler
     *            the handler to deregister
     * 
     */
       protected void processDeregister( Handler handler ) {
           eventStore.remove( handler );
           lockSelector.remove( handler );
       }

       
       /**
     * Returns the HandlerAdapter of a locked lockHandle or null if the
     * lockHandle is not locked or does not reference a HandlerGroup.
     * <p>
     * PRECONDITION: lockHandle not null
     * 
     * @param lockHandle
     *            the lockHandle, referencing a HandlerGroup, for which to
     *            retrieve the HandlerAdapter
     * @return the HandlerAdapter of locked lockHandle or null if the lockHandle
     *         is not locked or does not reference a HandlerGroup
     */
    public HandlerAdapter getLockedHandlerAdapter( Handle lockHandle ) {
        return( lockSelector.getLockedHandlerAdapter( lockHandle ) );
    }
    
    // ******************************************************************************
       // ****************** EVENT DISPATCH ****************** 
       // ******************************************************************************
       
       
       /**
     * Adds an event to the JReactor for processing.
     * <p>
     * Methods that add and remove elements to/from the eventQueue are
     * thread-safe.
     * <p>
     * PRECONDITION: event is a valid event
     * 
     * @param event
     *            the event to add to JReactor for processing.
     */
       public void addReadyEvent( Event event ) {    
        try {
            eventQueue.addLast( event );
        } catch ( IllegalStateException ise ) { 
            // this is a fatal error
            // the eventQueue is full, at a capacity of Integer.MAX_VALUE, and an event was missed by the JReactor and will not be recovered; thus, this event will not be serviced and the event source generating the event may never again be serviced
            
            // report critical error
            EventDispatchFailedException e = new EventDispatchFailedException( );
            jreactor.reportCriticalError( e );        
        }
       }
       
       
       /**
     * Removes and returns the next ready Event, blocking if necessary until an
     * Event becomes available.
     * <p>
     * Methods that add and remove elements to/from the eventQueue are
     * thread-safe.
     * 
     * @return the next ready Event
     * @throws InterruptedException
     *             if the blocking call to read from the event queue is
     *             interrupted
     */
       public Event getReadyEvent( ) throws InterruptedException {
 
           Event event = eventQueue.takeFirst( );
           
           return( event );
       }
              
    
    /**
     * Process an event that failed to dispatch due to ready operations on the
     * handle that are not contained in the interest operations for this handle.
     * <p>
     * All event types are checked-in, although Queue and Channel event types
     * are ignored.
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handle and event not null; handle is registered; readyOps valid
     * 
     * @param handle
     *            the handle with a ready operation that failed to dispatch due
     *            to ready operations not contained in the interest operations
     * @param event
     *            the ready event genereated by the event source
     */
    public void processDispatchFailOps( Handle handle, Event event ) {
        checkin( handle, event ); 
    }
    
    
    /**
     * Process an event that failed to dispatch due to its handler is currently
     * running.
     * <p>
     * All event types are stored.
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handler and event not null; handler is registered
     * 
     * @param handler
     *            event handler, servicing the handle, that is currently running
     * @param event
     *            the ready event generated by the event source
     * 
     */
    public void processDispatchFailRunning( Handler handler, Event event ) {
        // all event types
        store( handler, event );
    }
    
    
    /**
     * Process an event that failed to dispatch due to its handler is currently
     * pending or locked.
     * <p>
     * Queue, Channel -- ignore
     * <p>
     * Timer -- checkin
     * <p>
     * Lock, Signal, Error, Blocking -- store
     * <p>
     * This method requires external sychronization.
     * <p>
     * PRECONDITION: handler, handle, event not null; handler and handle are
     * registered; readyOps valid
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
    public void processDispatchFailLocked( Handler handler, Handle handle, int readyOps, Event event ) {
        if ( EventMask.isQRead( readyOps ) || EventMask.isChannel( readyOps ) ) {
            // Queue, Channel
            // ignore!
        } else if ( EventMask.isTimer( readyOps ) ) { 
            // Timer
            checkin( handle, event );
        } else {
            // Lock, Signal, Error, Blocking
            store( handler, event );            
        }
    }

    
            
    /**
     * 
     * Notifies the selector that an event with the given handle had fired but
     * was not dispatched. An example of a non-dispatch event requiring selector
     * notification are the interest ops of EventMask.NOOP. Individual selectors
     * should manage these notifications as appropriate.
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            reference to the event source and handler relationship for
     *            which to notify that a fired event was not dispatched
     * @param event
     *            the event generated by the event source
     */
    private void checkin( Handle handle, Event event ) {
        registrar.getSelector( handle ).checkin( handle, event );
    }
    
    
    /**
     * Stores the event, associating it to handler.
     * <p>
     * PRECONDITIONS: handler and event are not null; handler is registered
     * 
     * @param handler
     *            handler for which to associate to event
     * @param event
     *            event to store
     */
    private void store( Handler handler, Event event ) {
        eventStore.store( handler, event );
    }
    
    
    /**
     * Transfers all stored ready events, if any, for the given handler to the
     * event queue.
     * <p>
     * This method does not block.
     * 
     * @param handler
     *            the event handler for which to move all ready events, if any,
     *            to the event queue
     */
    private void transferReadyEvents( Handler handler ) {
        if ( eventStore.contains( handler ) ) {
            eventQueue.addAll( eventStore.remove( handler ) );
        }
    }
    
     
       // *************************************************************************
       // ****************** SHUTDOWN ****************** 
       // *************************************************************************
    
    /**
     * Begins the process of shutting down the Selector.
     * <p>
     * Informs the BlockingSelector to stop accepting new tasks into its thread
     * pool.
     * <p>
     * Calling this method multiple times has no affect.
     * <p>
     * This method is thread-safe.
     */
    public void shutdownInit( ) {
        if ( blockingSelector != null ) {
            blockingSelector.shutdownInit( );
        }
    }
    
    
    /**
     * Completes the process of shutting down the Selector.
     * <p>
     * Shutsdown: ChannelSelector, TimerSelector, BlockingSelector, and
     * SignalSelector.
     * <p>
     * May delay while ChannelSelector and TimerSelector are shutdown. May block
     * while the BlockingSelector is shutdown. May cause the JReactor to exit
     * immediately if the SignalSelector held certain JVM signals active.
     * <p>
     * Calling this method multiple times has no affect.
     * <p>
     * This method is not thread-safe.
     * <p>
     * PRECONDITIONS: shutdownInit() must have previously been called
     */
    public void shutdownFinal( ) {
        if ( channelSelector != null ) {
            channelSelector.shutdown( );
        }
        
        if ( timerSelector != null ) {
            timerSelector.shutdown( );
        }
        
        if ( blockingSelector != null ) {
            blockingSelector.shutdownFinal( );
        }
        
        if ( signalSelector != null ) {
            signalSelector.shutdown( ); // may cause immediate exit if certain JVM signals active
        }
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
