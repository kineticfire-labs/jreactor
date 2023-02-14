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

import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.MessageQueue;
import java.nio.channels.SelectableChannel;
import com.kineticfire.patterns.reactor.MessageBlock;

import java.util.Set;
import java.util.Date;


/**
 * Defines the interface and behavior for a Reactor. The Reactor emphasizes
 * concurrency and error management along with simplified data exchange among
 * objects through a message-passing paradigm.
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
 * The Reactor internally manages concurrency among event handlers and event
 * sources. An implementing class may support multiple execution threads, thus
 * executing event handlers in parallel.
 * <p>
 * The Reactor typically results in slower speed performance than other software
 * designs. The Reactor, however, trades speed in favor of robustness and
 * scalability.
 * <p>
 * <b>References</b>
 * <p>
 * [Sch94] Douglas Schmidt, "Reactor: An Object Behavioral Pattern for
 * Concurrent Event Demultiplexing and Event Handler Dispatching, " Proceedings
 * of the First Pattern Languages of Programs conference in Monticello,
 * Illinois, Aug. 1994.
 * 
 * @author Kris Hall
 * @version 11-22-09
 */


public interface Reactor {
    
       // *************************************************************************
       // ****************** CONTROL ****************** 
       // *************************************************************************
    
    /**
     * Starts the Reactor. Triggers the Reactor to begin demultiplexing events
     * and dispatching event handlers to service event sources.
     * <p>
     * All ready events are queued but not processed prior to calling
     * dispatch(). Upon calling dispatch(), the Reactor services queued ready
     * events and continues to service subsequent ready events until the Reactor
     * is closed via the shutdown() method.
     */
    public void dispatch( );
        
    
    /**
     * Shutsdown the Reactor. The Reactor no longer demultiplexes ready events
     * and exits when all running event handlers, if any terminate or after the
     * timeout value is reached.
     */
    public void shutdown( );

    
    
       // *************************************************************************
       // ****************** STATUS ****************** 
       // *************************************************************************
        
    
    /**
     * Queries if the Reactor is dispatching ready events.
     * <p>
     * A return value of true indicates that the Reactor is processing ready
     * events and dispatching handlers to service those events. A return value
     * of false indicates that the Reactor was either not started, was started
     * but is now shutdown, or is in the process of shutting down. If the
     * Reactor is shutting down, handlers that were running prior to Reactor
     * shutdown may continue to run to completion unless acted upon by another
     * method.
     * 
     * @return true if the Reactor is dispatching ready events and false
     *         otherwise
     */
    public boolean isDispatching( );    
    
    
    /**
     * Queries if the Reactor has been shutdown.
     * <p>
     * A return value of true indicates that the Reactor is no longer
     * processing new ready events and dispatching handlers for those events;
     * handlers that were running prior to Reactor shutdown may continue to run
     * to completion unless acted upon by another method.
     * 
     * @return true if the Reactor is shutdown or false otherwise
     */
    public boolean isShutdown( );
    
    
    
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
    public boolean isRegistered( Handle handle );
    
    

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
    public int interestOps( Handle handle );
    
    
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
    public void interestOps( Handle handle, int interestOps, Handle errorHandle );
    
    
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
    public void interestOps( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle );
    
    
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
    public void interestOpsOr( Handle handle, int interestOps, Handle errorHandle );
    
    
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
    public void interestOpsOr( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle );
    
    
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
     */
    public void interestOpsAnd( Handle handle, int interestOps, Handle errorHandle );
    
    
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
    public void interestOpsAnd( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle );
    
        
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
    public void deregister( Handle handle, Handle errorHandle );
    
    
    
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
    public void deregister( Handle lockHandle, Handle handle, Handle errorHandle );
    
    
    /**
     * Retrieves the handler currently associated to the given handle. Returns
     * null if the handle is not associated to a currently registered handler.
     * 
     * @param handle
     *            handle used to retrieve the associated handler
     * @return the handler associated with the handle or false if the handle is
     *         not associated to a registered handler
     * @throws NullPointerException
     *             if handle is null
     */
    public Handler handler( Handle handle );
    
    
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
    public boolean isRegistered( Handler handler );
    
    
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
    public Set<Handle> handles( Handler handler );
    
        
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
     *             if handler is not open            
     */
    public void deregister( Handler handler, Handle errorHandle );    
    
    
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
    public void deregister( Handle lockHandle, Handler handler, Handle errorHandle );
    
    
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
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if either queue or handler are null; or if the queue selector
     *             is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps isn't EventMask.{NOOP, QREAD}
     * @throws DuplicateRegistrationException
     *             if the queue is already registered
     */
    public Handle registerQueue( MessageQueue queue, Handler handler, int interestOps );    


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
    public boolean isRegisteredQueue( MessageQueue queue );
    
    
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
    public boolean isRegisteredQueue( Handle queueHandle );

    
    
    
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
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if either channel or handler are null; or if the channel
     *             selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps isn't EventMask.{NOOP, CREAD, CWRITE, ACCEPT,
     *             CONNECT}
     * @throws DuplicateRegistrationException
     *             if the channel is already registered
     * @throws IllegalResourceStateException
     *             if an error occurred while registering the channel to the
     *             java.nio.channels.Selector
     */
    public Handle registerChannel( SelectableChannel channel, Handler handler, int interestOps );    
        
    
    /**
     * Queries the registration status of a SelectableChannel. Determines the
     * registration status of the channel. Returns true if the channel is
     * currently registered and false otherwise.
     * 
     * @param channel
     *            event source for which to request registration status
     * @return true if the channel is currently registered and false otherwise
     * @throws NullPointerException
     *             if channel is null; or if the channel selector is disabled
     */
    public boolean isRegisteredChannel( SelectableChannel channel );
    
    
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
     *             if channelHandle is null; or if the channel selector is disabled
     */
    public boolean isRegisteredChannel( Handle channelHandle );
    

    
    // ------------------ TIMED ------------------
    
    /**
     * Registers a timed event to fire once at an absolute time. The timed event
     * is associated with an event handler and interest operations. Returns a
     * handle representing the relationship between the timed event and handler.
     * <p>
     * The handler will run exactly once unless it is canceled before the event
     * expires or it is deregistered.
     * <p>
     * See java.util.Timer:schedule(TimerTask,Date).
     * 
     * @param time
     *            time at which to expire the timed event and invoke the handler
     * @param handler
     *            event handler to associate with the timed event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timed event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timed selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timed selector encountered a fatal error
     */
    public Handle registerTimerOnce( Date time, Handler handler, int interestOps );
    
    
    /**
     * Registers a timed event to fire once after a set delay. The timed event
     * is associated with an event handler and interest operations. Returns a
     * handle representing the relationship between the timed event and handler.
     * <p>
     * The handler will run exactly once unless it is canceled before the event
     * expires or it is deregistered.
     * <p>
     * See java.util.Timer:schedule(TimerTask,long).
     * 
     * @param delay
     *            delay in milliseconds before the event is fired
     * @param handler
     *            event handler to associate with the timed event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timed event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timed selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timed selector encountered a fatal error
     */
    public Handle registerTimerOnce( long delay, Handler handler, int interestOps );

    
    /**
     * Registers a timed event to fire at a fixed-delay beginning at firstTime.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the timed event and handler.
     * <p>
     * The handler will be dispatched for each expired timed event until
     * deregistered. Timed events will continue to expire until canceled or
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
     *            the first time the timed event will expire
     * @param period
     *            time in milliseconds between timed event expirations
     * @param handler
     *            event handler to associate with the timed event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timed event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timed selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timed selector encountered a fatal error
     */
    public Handle registerTimerFixedDelay( Date firstTime, long period, Handler handler, int interestOps );
    
    
    
    /**
     * Registers a timed event to fire at a fixed-delay after an initial delay.
     * Following executions will occur at approximately regular intervals, as
     * given by period. Returns a handle representing the relationship between
     * the timed event and handler.
     * <p>
     * The handler will be dispatched for each expired timed event until
     * deregistered. Timed events will continue to expire until canceled or
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
     *            the initial delay in milliseconds before the timed event first
     *            expires
     * @param period
     *            time in milliseconds between timed event expirations
     * @param handler
     *            event handler to associate with the timed event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timed event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timed selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if delay is negative, or the sume delay and
     *             System.currentTimeMillis() is negative
     * @throws SelectorFailureException
     *             if the timed selector encountered a fatal error
     */
    public Handle registerTimerFixedDelay( long delay, long period, Handler handler, int interestOps );
    
    
    
    /**
     * Registers a timed event to fire at a fixed-rate beginning at firstTime.
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
     * 
     * @param firstTime
     *            the first time the timer event will expire
     * @param period
     *            time in milliseconds between timer event expirations
     * @param handler
     *            event handler to associate with the timed event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timed event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timed selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timed selector encountered a fatal error
     */
    public Handle registerTimerFixedRate( Date firstTime, long period, Handler handler, int interestOps );
    
    
    
    /**
     * Registers a timed event to fire at a fixed-rate beginning after an
     * initial delay. Following executions will occur at approximately regular
     * intervals, as given by period. Returns a handle representing the
     * relationship between the event source and handler.
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
     * See java.util.Timer scheduleAtFixedRate(TimerTask,delay,long).
     * 
     * @param delay
     *            the initial delay in milliseconds before the timer event first
     *            expires
     * @param period
     *            time in milliseconds between timer event expirations
     * @param handler
     *            event handler to associate with the timed event
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the timed event
     *         and handler
     * @throws NullPointerException
     *             if handler is null; or if the timed selector is disabled
     * @throws InvalidInterestOpsException
     *             if interestOps aren't EventMask.{NOOP | TIMER}
     * @throws IllegalArgumentException
     *             if time.getTime() returns negative
     * @throws SelectorFailureException
     *             if the timed selector encountered a fatal error
     */
    public Handle registerTimerFixedRate( long delay, long period, Handler handler, int interestOps );
    
        
    /**
     * Determines the registration status of the timerHandle. Returns TRUE if
     * the timerHandle is currently registered and is a handle for a timed event
     * source and FALSE otherwise.
     * 
     * @param timerHandle
     *            reference for which to request registration status
     * @return TRUE if the timerHandle is currently registered and is a handle
     *         for a timed event source and FALSE otherwise
     * @throws NullPointerException
     *             if timerHandle is null; or if the timed selector is disabled           
     */
    public boolean isRegisteredTimer( Handle timerHandle );
    
    
    /**
     * Attempts to cancel and then deregister a timed event referenced by
     * timerHandle, returning true if the timed event was successfully canceled
     * and false otherwise.
     * <p>
     * A timed event is successfully canceled by this method when the
     * event--referenced by timerHandle--is currently registered, has not had
     * the cancel() method called, and, if a run-once handler, the event has not
     * yet fired. Successfully canceling a run-once timed event means that the
     * timed event handler will not run. Successfully canceling a recurring
     * timed event means that the event source will not continue to fire events;
     * the handler may or may not have run in the past, may be running
     * currently, and events that already fired will run.
     * <p>
     * If the cancelled timerHandle has no outstanding timed events, then the
     * timerHandle will be deregistered. If the cancelled timerHandle does have
     * outstanding timed events, then the timerHandle will be deregistered when
     * all timed events have been accounted.
     * <p>
     * Calling this event multiple times has no affect.
     * <p>
     * PRECONDITION: handle not null
     * 
     * @param timerHandle
     *            reference to timed event to attemp cancellation
     * @return true if the timed event referenced by timerHandle was
     *         successfully cancelled and false otherwise
     * @throws NullPointerException
     *             if timerHandle is null; or if the timed selector
     *             is disabled
     * @throws InvalidLockStatusException
     *             if timerHandle is not open
     */
    public boolean cancelTimer( Handle timerHandle );
    
    
    /**
     * Attempts to cancel and then deregister a timed event referenced by
     * timerHandle and locked by lockHandle, returning true if the timed event
     * was successfully canceled and false otherwise.
     * <p>
     * A timed event is successfully canceled by this method when the
     * event--referenced by timerHandle--is currently registered, has not had
     * the cancel() method called, is locked by lockHandle, and, if a run-once
     * handler, the event has not yet fired. Successfully canceling a run-once
     * timed event means that the timed event handler will not run. Successfully
     * canceling a recurring timed event means that the event source will not
     * continue to fire events; the handler may or may not have run in the past,
     * may be running currently, and events that already fired will run.
     * <p>
     * If the cancelled timerHandle has no outstanding timed events, then the
     * timerHandle will be deregistered once the lockHandle is unlocked. If the
     * cancelled timerHandle does have outstanding timed events, then the
     * timerHandle will be deregistered when all timed events have been
     * accounted and once the lockHandle is unlocked.
     * <p>
     * Calling this event multiple times has no affect.
     * 
     * @param lockHandle
     *            the locked lockHandle of which the handler is a member
     * @param timerHandle
     *            reference to timed event to attemp cancellation
     * @return true if the timed event referenced by timerHandle was
     *         successfully cancelled and false otherwise
     * @throws NullPointerException
     *             if lockHandle or timerHandle is null; or if the timed
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
    public boolean cancelTimer( Handle lockHandle, Handle timerHandle );
        
    
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
    public Handle registerLock( Handler lockHandler, int interestOps );

    
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
    public void addMember( Handle lockHandle, Handler managedHandler, Handle errorHandle );
       
    
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
    public void removeMember( Handle lockHandle, Handler managedHandler, Handle errorHandle );
    
    
    
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
    public boolean isRegisteredLock( Handle lockHandle );
    
    
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
    public boolean isMember( Handle lockHandle, Handler managedHandler );
    
    
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
    public boolean isLockedMember( Handle lockHandle, Handler managedHandler );
    
        
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
    public void lock( Handle lockHandle, Handle errorHandle );
    
    
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
    public void unlock( Handle lockHandle, Handle errorHandle );
            
    
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
    public boolean isOpen( Handler managedHandler );
    
    
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
    public boolean isOpen( Handle lockHandle );
    
    
    /**
     * Determines if the handler is pending.
     * 
     * @param managedHandler
     *            handler on which to query pending status
     * @return true if the handler is pending and false otherwise
     * @throws NullPointerException
     *             if managedHandler is null
     */
    public boolean isPending( Handler managedHandler );    
    
    
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
    public boolean isPending( Handle lockHandle );
    
    
    /**
     * Determines if the handler is locked.
     * 
     * @param managedHandler
     *            handler on which to query lock status
     * @return true if the handler is locked and false otherwise
     * @throws NullPointerException
     *             if managedHandler is null
     */
    public boolean isLocked( Handler managedHandler );
    
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
    public boolean isLocked( Handle lockHandle );
    
    
    //  ------------------ SIGNAL ------------------
    
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
     *             if the event source, signalName, is not valid or supported
     * @throws DuplicateRegistrationException
     *             if the signal is already registered
     */
    public Handle registerSignal( String signalName, Handler handler, int interestOps );
    
        
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
    public boolean isRegisteredSignal( String signalName );
    
        
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
     *             
     */
    public boolean isRegisteredSignal( Handle signalHandle );
    
        
    
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
    public Handle registerError( Handler handler, int interestOps );
    
        
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
    public Handle newErrorHandle( Handler handler );
    
    
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
    public Handle newErrorHandle( Handle errorHandle );
    
    
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
    public Handle getErrorHandle( Handler handler );    
    
    
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
    public boolean isRegisteredError( Handler handler );
    
    
    /**
     * Determines if the handle references a currently registered error event
     * handle.
     * 
     * @param errorHandle
     *            reference for which to request registration status
     * @return true if the handle is currently registered and is a handle for an
     *         error event source and false otherwise
     * @throws NullPointerException
     *             if errorHandle is null
     */
    public boolean isRegisteredError( Handle errorHandle );

    
    
    // ------------------ BLOCKING TASK ------------------
    
    /**
     * Submits a blocking task to the Reactor with given event handler,
     * interest operations, and error handler. The task is run in a special
     * thread pool, separate from the normal worker thread pool, thus blocking
     * tasks do not significantly interfere with common tasks. Completion of the
     * blocking runnable task results in a EventMask.BLOCKING event and is
     * serviced by the associated handler; errors cause the dispatch of the
     * handler associated with the errorHandle.
     * 
     * @param runnable
     *            blocking Runnable task to execute
     * @param handler
     *            event handler to associate with the completion of the runnable
     *            task
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if runnable or handler is null
     * @throws InvalidResourceException
     *             if the handler is not regisered
     * @throws InvalidInterestOpsException
     *             if the interestOps are not EventMask.{NOOP,BLOCKING}
     */
    public Handle registerBlocking( Runnable runnable, Handler handler, int interestOps );
    
    
    /**
     * Submits a group of blocking tasks to the Reactor with given event
     * handler, interest operations, and error handler. The group of tasks is
     * run in a special thread pool, separate from the normal worker thread
     * pool, thus blocking tasks do not significantly interfere with common
     * tasks. Completion of the group of blocking runnable tasks results in a
     * EventMask.BLOCKING event and is serviced by the associated handler;
     * errors cause the dispatch of the handler associated with the errorHandle.
     * <p>
     * If successfully registered, the handler must have a handle associated
     * with error events and be immediately be capable of receiving error events
     * e.g. EventMask.ERROR.
     * 
     * @param runnableSet
     *            group of blocking Runnable tasks to execute
     * @param handler
     *            event handler to associate with the completion of the runnable
     *            task
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     * @throws NullPointerException
     *             if handler, runnableSet, or any runnable in runnableSet is
     *             null
     * @throws IllegalArgumentException
     *             if runnableSet is empty
     * @throws InvalidResourceException
     *             if the handler is not regisered
     * @throws InvalidInterestOpsException
     *             if the interestOps are not EventMask.{NOOP,BLOCKING}
     */
    public Handle registerBlockingGroup( Set<Runnable> runnableSet, Handler handler, int interestOps );
    
    
    /**
     * Determines if the blockingHandle references a currently registered
     * blocking event source. Returns true if the blockingHandle is currently
     * registered and is a handle for a blocking event source and false
     * otherwise.
     * 
     * @param blockingHandle
     *            reference for which to request registration status
     * @return true if the blockingHandle is currently registered and is a
     *         handle for a blocking event source and false otherwise
     * @throws NullPointerException
     *             if blockingHandle is null
     */
    public boolean isRegisteredBlocking( Handle blockingHandle );

}
