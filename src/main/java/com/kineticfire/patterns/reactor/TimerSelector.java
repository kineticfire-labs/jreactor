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


import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.util.Timer;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.StandardTimebaseTask;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;

import java.lang.IllegalStateException;
import com.kineticfire.patterns.reactor.SelectorFailureException;


/**
 * This class provides for the creation, generation, and management of timer
 * events.
 * <p>
 * REGISTER -- Registration creates a timer event that will expire in the
 * future. The timer event may be scheduled to expire once or to fire more than
 * once with a certain period. Successful registration returns a handle which
 * can be used to reference the registered timer event.
 * <p>
 * CANCEL -- The cancel() method attempts to cancel a timer event. Cancel
 * returns true if the event was successfully canceled and false otherwise. A
 * timer event is successfully canceled by this method when the
 * event--referenced by a timerHandle--is currently registered, has not had the
 * cancel() method called, and, if a run-once handler, the event has not yet
 * fired. Successfully canceling a run-once timer event means that the timer
 * event handler will not run; else the handler was, is, or will run.
 * Successfully canceling a recurring timer event means that the event source
 * will not continue to fire events; the handler may or may not have run in the
 * past, may be running currently, and events that already fired will be
 * serviced by their handlers.
 * <p>
 * DEREGISTER -- Deregistration removes a timer event. The timer event will no
 * longer generate ready events and the handle referencing the timer event is
 * removed. Any timer events that previously fired yet are not dispatched will
 * be discarded; a handler already dispatched to service a timer event will run
 * to completion.
 * <p>
 * EVENT BEHAVIOR DURING INTERESTOPS NOOP -- Timer events that are being
 * processed from the event queue when their interest operations are set to NOOP
 * are not dispatched and are checked-in with the TimerSelector. The selector
 * uses the checked-in events to track the number of outstanding timer.
 * <p>
 * EVENT BEHAVIOR DURING LOCK -- Timer events that are being processed from the
 * event queue when their associated handlers are locked are not dispatched and
 * are checked-in with the TimerSelector. The selector uses the checked-in
 * events to track the number of outstanding timer.
 * <p>
 * By default, a java.util.Timer object is used to manage timer events.
 * <p>
 * This class is thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */



public class TimerSelector implements SpecificSelector {

    // *********************************************
    // ~ Instance/static variables
    
    /* Used to add timer expired events to Selector */
    private CompositeSelector selector;    
    
    
    /*
     * Manages timer events, represented as StandardTimebaseTask; creates events
     * and cancels events; fires events at the appropriate time
     * <p>
     * The Timer object is thread-safe.
     */
       private Timer timer;
       
       
       /*
     * Maps handler to a scheduled event, represented as a StandardTimebaseTask;
     * tracks registered handlers; the reference to the task is necessary to
     * cancel the timer event or check if the timer event has previously been
     * canceled
     */
       private Map<Handle, StandardTimebaseTask> handleTaskMap;
       
       
       /* Tracks run-once vs recurring scheduled timer events */
       private Map<Handle, Boolean> isRecurringEvent;
       
       
       /*
     * Counts the number of timer events fired for which the associated handlers
     * have not yet resumed selection (e.g. checked-in). Ideally, the number of
     * expired timers for a handler is, at most, unity meaning that one event
     * fired and one handler is or will soon run. The number of expired timers
     * will be greater than unity when JReactor dispatch time and/or handler
     * execution time is large wrt the time difference in expired events. The
     * number of outstanding expired timer events is necessary to track so that
     * the TimerSelector will know when it is appropriate to process the
     * cancel() command.
     */ 
       private Map<Handle, Integer> expiredTimers;
       
       
       /*
     * Tracks handlers that are awaiting deregistration resulting from the
     * cancel() method having been called on the handle/task.
     * <p>
     * Pending status is due to a cancel() request, not the nature of a run-once
     * event
     */
       private Set<Handle> pendingCancel;
       
       
       /*
     * Used in the closing process of TimerSelector, especially in coordination
     * of shutting down the Timebase and managing the Timebase, TimebaseTask
     * (runnable objects under control of the Timebase), and external threads (e.g.
     * Reactor threads). Ensures the closing process is executed at most once,
     * gaurding against multiple calls resulting from shutdown() and/or finalize().
     */
    private boolean done;
    
    
    /*
     * Provides a synchronization point to coordinate the Timer,
     * StandardTimebaseTask and external threads (e.g. JReactor threads). All
     * operations in the TimerSelector must synchronize on this object.
     */
    private static Byte guard;


       // *********************************************
       // ~ Constructors


       /**
     * Constructs the TimerSelector without a reference to the selector.
     * <p>
     * The Timebase is initially implemented with a StandardTimebase object. The
     * latter is wrapper for java.util.Timer; it is initialized with its
     * intrinsic thread as a daemon. The Timebase may be changed with a call to
     * the configureTimebase(Timebase) method.
     */
       public TimerSelector( ) {
        selector = null;
        
        /* put Timer into a daemon thread */
          timer = new Timer( "Timer", true );

        handleTaskMap = new HashMap<Handle, StandardTimebaseTask>( );
        isRecurringEvent = new HashMap<Handle, Boolean>( );
        expiredTimers = new HashMap<Handle, Integer>( );
        pendingCancel = new HashSet<Handle>( );
        guard = Byte.parseByte( "0" );
        done = false;
       }
       
       
       /**
     * Constructs the TimerSelector with a reference to the selector.
     * <p>
     * The Timebase is initially implemented with a StandardTimebase object. The
     * latter is wrapper for java.util.Timer; it is initialized with its
     * intrinsic thread as a daemon. The Timebase may be changed with a call to
     * the configureTimebase(Timebase) method.
     * 
     * @param selector
     *            reference to the controlling Selector
     */
       public TimerSelector( CompositeSelector selector ) {
        this.selector = selector;
        
        /* put Timer into a daemon thread */
          timer = new Timer( "Timer", true );

        handleTaskMap = new HashMap<Handle, StandardTimebaseTask>( );
        isRecurringEvent = new HashMap<Handle, Boolean>( );
        expiredTimers = new HashMap<Handle, Integer>( );
        pendingCancel = new HashSet<Handle>( );
        guard = Byte.parseByte( "0" );
        done = false;
       }



    // *********************************************
    // ~ METHODS

       
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
     * PRECONDITION: handler not null, interestOps is EventMask.{NOOP,TIMER}
     * 
     * @param time
     *            time at which to expire the timer event and invoke the handler
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
       public Handle registerOnce( Date time, Handler handler, int interestOps ) {
           Handle handle = null;
           
           synchronized( guard ) {
            handle = new Handle( );            
            StandardTimebaseTask task = registerInternal( handler, handle, interestOps, false );    
            
            try {
                timer.schedule( task, time );
            } catch ( IllegalStateException e ) {
                throw( new SelectorFailureException( ) );
            }
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
     * PRECONDITION: handler not null, interestOps is EventMask.{NOOP,TIMER}
     * 
     * @param delay
     *            delay in milliseconds before the event is fired
     * @param handler
     *            event handler to associate with the timer event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timer event
     *         and handler
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
    public Handle registerOnce( long delay, Handler handler, int interestOps ) {
        Handle handle = null;
           
           synchronized( guard ) {
            handle = new Handle( );            
            StandardTimebaseTask task = registerInternal( handler, handle, interestOps, false );
            
            try {
                timer.schedule( task, delay );
            } catch ( IllegalStateException e ) {
                throw( new SelectorFailureException( ) );
            }
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
     * PRECONDITION: handler not null, interestOps is EventMask.{NOOP,TIMER}
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
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
    public Handle registerFixedDelay( Date firstTime, long period, Handler handler, int interestOps ) {
        Handle handle = null;
        
        synchronized( guard ) {
            handle = new Handle( );            
            StandardTimebaseTask task = registerInternal( handler, handle, interestOps, true );
            
            try {
                timer.schedule( task, firstTime, period );
            } catch ( IllegalStateException e ) {
                throw( new SelectorFailureException( ) );
            }
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
     * <p>
     * PRECONDITION: handler not null, interestOps is EventMask.{NOOP,TIMER}
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
     * @throws IllegalArgumentException
     *             if delay is negative, or the sume delay and
     *             System.currentTimeMillis() is negative
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
       public Handle registerFixedDelay( long delay, long period, Handler handler, int interestOps ) {
           Handle handle = null;
           
           synchronized( guard ) {
            handle = new Handle( );            
            StandardTimebaseTask task = registerInternal( handler, handle, interestOps, true );
            
            try {
                timer.schedule( task, delay, period );
            } catch ( IllegalStateException e ) {
                throw( new SelectorFailureException( ) );
            }
           }
           
           return( handle );

       }


       
       /**
     * Registers a timer event to fire at a fixed-rate beginning at firstTime.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the timer event and handler.
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
     * See java.util.Timer:scheduleAtFixedRate(TimerTask,Date,long).
     * <p>
     * PRECONDITION: handler not null, interestOps is EventMask.{NOOP,TIMER}
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
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
    public Handle registerFixedRate( Date firstTime, long period, Handler handler, int interestOps ) {
        Handle handle = null;
           
           synchronized( guard ) {
            handle = new Handle( );            
            StandardTimebaseTask task = registerInternal( handler, handle, interestOps, true );
            
            try {
                timer.scheduleAtFixedRate( task, firstTime, period );
            } catch ( IllegalStateException e ) {
                throw( new SelectorFailureException( ) );
            }
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
     * PRECONDITION: handler not null, interestOps is EventMask.{NOOP,TIMER}
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
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the Timer thread terminated or the timer was cancelled
     */
    public Handle registerFixedRate( long delay, long period, Handler handler, int interestOps ) {
        Handle handle = null;
           
           synchronized( guard ) {
            handle = new Handle( );            
            StandardTimebaseTask task = registerInternal( handler, handle, interestOps, true );
            
            try {
                timer.scheduleAtFixedRate( task, delay, period );
            } catch ( IllegalStateException e ) {
                throw( new SelectorFailureException( ) );
            }
           }
           
           return( handle );

    }


    /**
     * Completes registration for all timer events. Updates the handlTask map,
     * isRecurringEvent map, and expiredTimers map. Sends registration
     * information to the selector. Returns a StandardTimebaseTask that can be
     * added to java.util.Timer; the task coordinates expiration of the timer
     * event with the TimerSelector.
     * <p>
     * Requires external sychronization.
     * <p>
     * PRECONDITION: handler and handle are not null; interestOps is in
     * EventMask.{NOOP,TIMER}
     * 
     * @param handler
     *            event handler to associate with the timer event
     * @param handle
     *            a handle that represents the relationship between the timer
     *            event and handler
     * @param interestOps
     *            interest operations to assign the handle
     * @param isRecurring
     *            true if this is a recurring event and false if this is a run-
     *            once event
     * @return a StandardTimebaseTask that can be registered with a Timebase;
     *         the task coordinates the expiration of the timer event with the
     *         TimerSelector
     */
    private StandardTimebaseTask registerInternal( Handler handler, Handle handle, int interestOps, boolean isRecurring ) {
           StandardTimebaseTask task = new StandardTimebaseTask( this, handle );
        handleTaskMap.put( handle, task );
        isRecurringEvent.put( handle, isRecurring );
        expiredTimers.put( handle, 0 );
        selector.processRegister( handle, handler, this, interestOps );
        
        return( task );
       }

    
    /**
     * Determines the registration status of the timerHandle. Returns TRUE if
     * the timerHandle is currently registered and is a handle for a timer event
     * source and FALSE otherwise.
     * <p>
     * PRECONDITION: timerHandle is not null
     * 
     * @param timerHandle
     *            reference for which to request registration status
     * @return TRUE if the timerHandle is currently registered and is a handle
     *         for a timer event source and FALSE otherwise
     */
    public boolean isRegistered( Handle timerHandle ) {
        synchronized( guard ) {
            return( handleTaskMap.containsKey( timerHandle ) );
        }
    }

    
    
    /**
     * Sets the interest operations of handle to the given interestOps. 
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not EventMask.{NOOP,TIMER}
     */
    public void interestOps( Handle handle, int interestOps ) {
        if ( EventMask.xIsTimer( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            selector.processInterestOps( handle, interestOps );
        } else {
            throw( new InvalidInterestOpsException( ) );
        }
    }
    
    

    /**
     * Coordinates the expiration of a timer event. A StandardTimebaseTask calls
     * this method when its timer event expires. If the handle managed by the
     * task is registered and is not pending cancellation, an event is added to
     * the selector and the number of expired events for this handle is
     * incremented. If the handle managed by the task is not registered or is
     * pending cancellation, then no action is taken and the call is ignored.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle for which the timer event expired
     */
    protected void timerExpired( Handle handle ) {

        synchronized( guard ) {
            if ( handleTaskMap.containsKey( handle ) ) {
                if ( !pendingCancel.contains( handle ) ) {
                    selector.addReadyEvent( new Event( handle, EventMask.TIMER  ) );
                    int current = expiredTimers.get( handle );
                    expiredTimers.put( handle, ++current );
                }
            }
        }
    }


    /**
     * Attempts to cancel and then deregister a timer event referenced by
     * handle, returning true if the timer event was successfully canceled and
     * false otherwise.
     * <p>
     * A timer event is successfully canceled by this method when the
     * event--referenced by handle--is currently registered, has not had the
     * cancel() method called, and, if a run-once handler, the event has not yet
     * fired. Successfully canceling a run-once timer event means that the timer
     * event handler will not run. Successfully canceling a recurring timer
     * event means that the event source will not continue to fire events; the
     * handler may or may not have run in the past, may be running currently,
     * and events that already fired will run.
     * <p>
     * If the cancelled handle has no outstanding timer events, then the handle
     * will be deregistered. If the cancelled handle does have outstanding timer
     * events, then the handle will be deregistered when all timer events have
     * been accounted.
     * <p>
     * Calling this event multiple times has no affect.
     * <p>
     * PRECONDITION: handle not null
     * 
     * @param handle
     *            reference to timer event to attemp cancellation
     * @return true if the timer event referenced by handle was successfully
     *         cancelled and false otherwise
     */
    public boolean cancel( Handle handle ) {
        boolean canceled = false;

        synchronized( guard ) {
            StandardTimebaseTask task = handleTaskMap.get( handle );

            if ( task != null ) {                
                /*
                 * Implicit check of registration status: if task == null, then
                 * is deregistered, in process of deregistering, or was never
                 * registered
                 */
                
                /*
                 * Returns TRUE if prevented one or more firings of this
                 * StandardTimebaseTask
                 * <p>
                 * Returns FALSE if handle never registered, deregistered, or in
                 * process of deregistering
                 */
                canceled = task.cancel( );
                
                if ( canceled ) {
                    // equivalent to:  if ( !pendingCancel )
                    
                    int current = expiredTimers.get( handle );
                    
                    if ( current == 0 ) {
                        // all timer events for this handler are accounted
                        deregister( handle );
                    } else {
                        // timer events outstanding for this handler
                        pendingCancel.add( handle );
                    }
                }
            }
        }

        return ( canceled );        
    }
    
    /**
     * Deregisters the timer event referenced by handle. The timer event
     * will no longer generate ready events.
     * <p>
     * The timer event is immediately removed and all outstanding events
     * are discarded.
     * <p>
     * PRECONDITION: handle is not null
     * 
     * @param handle
     *            the handle to deregister
     */
    public void deregister( Handle handle ) {

        synchronized( guard ) {
            
            if ( handleTaskMap.containsKey( handle ) ) {
                StandardTimebaseTask task = handleTaskMap.remove( handle );
                task.cancel( );
                
                isRecurringEvent.remove( handle );
                expiredTimers.remove( handle );
                pendingCancel.remove( handle );
                
                selector.processDeregister( handle );
            }
        }
    }
    
    
    
    /**
     * Checks-in a handle from a timer event that was not processed. Updates the
     * count of outstanding fired events in 'expiredTimers'.
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            handle that references a timer event that was not dispatched
     */
    public void checkin( Handle handle, Event event ) {
        synchronized( guard ) {
            int current = expiredTimers.get( handle ) - 1;
            expiredTimers.put( handle, current );
        }
    }
    

    
    /**
     * Resumes readiness selection on the timer event referenced by handle.
     * <p>
     * If this handle refers to a run-once timer event or any canceled timer
     * event, the handle undergoes implicit deregistration. In that case, the
     * handle is removed from the TimerSelector and deregistered with the
     * selector.
     * <p>
     * PRECONDITION: handle is not null, handle is registered
     * 
     * @param handle
     *            handle that references timer event for which to resume
     *            selection for subsequent ready events
     */
    public void resumeSelection( Handle handle ) {
        
        synchronized( guard ) {

            
            if ( !isRecurringEvent.get( handle ) ) {
                // this is a run-once event
                
                /*
                 * deregister a completed non-recurring handle
                 */
                deregister( handle );
                    
                
            } else {
                // this is a recurring event
                
                int current = expiredTimers.get( handle ) - 1;
                expiredTimers.put( handle, current );
                

                // check deregistration request status of handler
                if ( pendingCancel.contains( handle ) ) {

                    // remove handler if all fired events checked in
                    if ( current <= 0 ) {
                        deregister( handle );
                    }
                }
            }
        }        
    }    
    
    
    /**
     * Permanently terminates timer event readiness selection and causes the
     * internal timer object to terminate and cancel all StandardTimebaseTask
     * objects. Calling this method multiple times has no affect.
     */
    public void shutdown( ) {
        synchronized( guard ) {
            if ( !done ) {
                done = true;
                timer.cancel( );
            }
        }
    }
    
    
    /**
     * Prepares the TimerSelector for garbage selection by gracefully
     * terminating the internal timer event readiness selection and causes the
     * internal timer object to terminate and cancel all StandardTimebaseTask
     * objects.
     * 
     */
    public void finalize( ) {
        shutdown( );
    }

}
