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
import java.util.List;
import java.util.Iterator;

import com.kineticfire.patterns.reactor.JReactor;
import com.kineticfire.patterns.reactor.RunLockAuthority;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;

import com.kineticfire.patterns.reactor.LockHandlerGroup;
import com.kineticfire.patterns.reactor.OpenGroup;
import com.kineticfire.patterns.reactor.PendingGroup;
import com.kineticfire.patterns.reactor.LockGroup;

import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;

import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.ObjectBasedMessageBlockBuilder;

import com.kineticfire.util.MultiValueMap;
import com.kineticfire.util.HashLinkMultiValueMap;
import com.kineticfire.patterns.reactor.InvalidResourceException;
import com.kineticfire.patterns.reactor.InvalidInterestOpsException;
import com.kineticfire.patterns.reactor.LockFailedException;
import com.kineticfire.patterns.reactor.UnlockFailedException;


/**
 * The LockSelector provides the capability to acquire a lock on an event
 * handler--that is, a guarentee that the handler is not running and will not
 * run--and generate a ready event at such time for readiness selection.
 * Obtaining a lock on event handlers is useful for recovering from errors.
 * <p>
 * A LockHandlerGroup is created to manage member Handlers. Handlers may be
 * freely added and removed to a LockHandlerGroup, and a Handler may be a member
 * of more than one LockHandlerGroup. A LockHandlerGroup is referenced by a
 * unique Handle. The request to lock the group, referenced by Handle, attempts
 * to immediately lock each Handler in the LockHandlerGroup. If the Handler is
 * inactive (e.g. not running, pending, or locked) then the Handler is locked
 * immediately. If the Handler is active (e.g. running, pending, or locked) the
 * Handler is placed in pending status and the new lock request on that Handler
 * is queued. A pending Handler will be locked when the Handler is released.
 * Once all Handlers in the group are locked, the associated event handler is
 * dispatched to service the event EventMask.LOCK.
 * <p>
 * A succesful lock on a LockHandlerGroup is a guarantee that the members
 * Handlers are not running and will not run and the Handle owns the only lock
 * on those member Handlers. Locked Handlers are not and will not be executed or
 * modified by the JReactor.
 * <p>
 * If a lock request is made but fails immediately, an exception is returned. If
 * a queued lock request fails for any reason, then an EventMask.ERROR event is
 * generated. The error handler associated with the generated event will run.
 * <p>
 * This class is not thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */





public class LockSelector implements SpecificSelector {

    //*********************************************
    //~ Instance/static variables
    
    
    /*
     * (lock) Handle --> LockHandlerGroup
     * <p>
     * Added when a new LockHandlerGroup is created with register(). Used for
     * developer to reference LockHandlerGroup.
     * <p>
     * Removed when lockHandle deregistered and group removed.
     */
    private Map<Handle, LockHandlerGroup> handleGroupMap;
    
    /*
     * (managed) Handler --> Set<(lock) Handle>
     * <p>
     * Added when a new managed Handler is added into a LockHandlerGroup through
     * addMember(). Used to remove managedHandler from LockSelector if
     * managedHandler is deregistered (via JReactor) without first removing the
     * managedHandler from the handler group.
     * <p>
     * Removal of the entire lockHandle mapping occurs when the lockHandle is
     * deregistered. Removal the managedHandler from the entire data structure
     * occurs via deregister(handler).
     */
    private MultiValueMap<Handler, Handle> handlerHandlesMap;
    
    /*
     * (lock) Handle --> (lock) HandlerAdapter
     * <p>
     * Added when a lock is achieved for lockHandle. Used to store changes to
     * lockHandler or the LockHandlerGroup referenced by LockHandle when the
     * lockHandle is locked.
     * <p>
     * Removed when lockHandle is unlocked via a call to unlock() which calls
     * resumeSelection() on the adapter or lockHandle deregistered.
     */
    private Map<Handle, HandlerAdapter> handleAdapterMap;
    
    /*
     * (lock) Handle --> (error) Handle
     * <p>
     * Added for each queued lock request on a handler group. If the lock
     * command fails, then the event handler servicing the errorHandle will be
     * dispatched with the error event.
     * <p>
     * Removed when command fails and error event generated, lockHandle
     * deregistered, or after event fired and resumeSelection() called on the
     * lockHandle.
     */
    private Map<Handle, Handle> lockHandleErrorHandleMap;
    
    
    private OpenGroup openGroup;
    private PendingGroup pendingGroup;
    private LockGroup lockGroup;
    
    private JReactor jreactor;
    private CompositeSelector selector;
    private RunLockAuthority runLockAuthority;


    // etc
    private ObjectBasedMessageBlockBuilder messageBlockBuilder;                  // builds MessageBlocks
    
 


       //*********************************************
       //~ Constructors


    /**
     * Constructs an empty LockedSelector with a reference to the JReactor,
     * Selector, and RunLockAuthority.
     * <p>
     * PRECONDITION: jreactor, selector, and runLockAuthority not null
     * 
     * @param jreactor
     *            reference to the controlling JReactor
     * @param selector
     *            reference to the controlling Selector
     * @param runLockAuthority
     *            reference to the entity that allows query of a Handler's
     *            'running' and 'lock' states and to manipulate a Handler's lock
     *            state
     */
    public LockSelector( JReactor jreactor, CompositeSelector selector, RunLockAuthority runLockAuthority ) {
        this.jreactor = jreactor;
        this.selector = selector;
           this.runLockAuthority = runLockAuthority;
           
           handleGroupMap = new HashMap<Handle, LockHandlerGroup>( );
           handlerHandlesMap = new HashLinkMultiValueMap<Handler, Handle>( );
           handleAdapterMap = new HashMap<Handle, HandlerAdapter>( );
           lockHandleErrorHandleMap = new HashMap<Handle, Handle>( );
           
           openGroup    = new OpenGroup( );           // Set<Handle>
           pendingGroup = new PendingGroup( );        // List<Handler> --> List<Handle>
           lockGroup    = new LockGroup( );           // Map<Handler,Handle>

           messageBlockBuilder = new ObjectBasedMessageBlockBuilder( );
           
    }



       //*********************************************
       //~ METHODS

           
    /**
     * Creates a new handler group and registers the lockHandler to service
     * ready events from that group, based on the provided interest operations.
     * Returns a handle representing the relationship between the event source
     * and handler.
     * <p>
     * The handler group is initially empty and open. Handle is used to
     * reference the handler group.
     * <p>
     * PRECONDITION: lockHandler not null; interestOps is EventMask.{NOOP,LOCK}
     * 
     * @param lockHandler
     *            event handler to associate with the newly created handler
     *            group
     * @param interestOps
     *            interest operations to assign the handle
     * @return a handle that represents the relationship between the event
     *         source and handler
     */
    public Handle register( Handler lockHandler, int interestOps ) {
        Handle lockHandle = new Handle( );        
        LockHandlerGroup group = new LockHandlerGroup( );        
        handleGroupMap.put( lockHandle, group );
        openGroup.add( lockHandle );        
        selector.processRegister( lockHandle, lockHandler, this, interestOps );
        
        return( lockHandle );
    }
    
    
    /**
     * Adds the event handler to the handler group referenced by lockHandle.
     * <p>
     * PRECONDITION: lockHandle and managedHandler are not null and are
     * registered; lockHandle open and lockHandler not running
     * 
     * @param lockHandle
     *            handle that references the handler group to which to add the
     *            managedHandler
     * @param managedHandler
     *            handler to add to the handler group
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public void addMember( Handle lockHandle, Handler managedHandler ) {
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group != null ) {
            group.addMember( managedHandler );                
            handlerHandlesMap.put( managedHandler, lockHandle );                
        } else {
            throw( new InvalidResourceException( ) );
        }
    }
    

    /**
     * Removes the event handler from the handler group referenced by lockHandle.
     * <p>
     * PRECONDITION: lockHandle and managedHandler are not null and are
     * registered; lockHandle open and lockHandler not running
     * 
     * @param lockHandle
     *            handle that references the handler group from which to remove
     *            the managedHandler
     * @param managedHandler
     *            handler to remove from the handler group
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public void removeMember( Handle lockHandle, Handler managedHandler ) {
                
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group != null ) {
            group.removeMember( managedHandler );                    
            handlerHandlesMap.remove( managedHandler, lockHandle );                
        } else {
            throw( new InvalidResourceException( ) );
        }
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
     */
    public boolean isRegistered( Handle lockHandle ) {
        return( handleGroupMap.containsKey( lockHandle ) );
    }
        
    
    /**
     * Determines if the handler is a managed handler and thus contained in a
     * LockHandlerGroup. Returns true if the handler is contained in a
     * LockHandlerGroup and false otherwise.
     * <p>
     * PRECONDITION: handler is not null
     * 
     * @param handler
     *            handler to query
     * @return true if the handler is contained in a LockHandlerGroup and false
     *         otherwise
     */
    public boolean isRegistered( Handler handler ) {
        return( handlerHandlesMap.containsKey( handler ) );
    }
    
    
    /**
     * Determines if the managedHandler is a member of the handler group
     * referenced by lockHandle. Returns true if the managedHandler is a member
     * of the handle group referenced by lockHandle.
     * <p>
     * PRECONDITION: lockHandle and managedHandler are not null
     * 
     * @param lockHandle
     *            reference to the handler group
     * @param managedHandler
     *            handler to query if it is a member of the group referenced by
     *            lockHandle
     * @return true if the managedHandler is a member of the handler group
     *         referenced by lockHandle
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isMember( Handle lockHandle, Handler managedHandler ) {
        boolean isMember = false;
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group != null ) {
            isMember = group.isMember( managedHandler );
        } else {
            throw( new InvalidResourceException( ) );
        }
        
        return( isMember );
    }
    
    
    /**
     * Sets the interest operations of handle to the given interestOps.
     * <p>
     * PRECONDITION: handle is not null, handle is registered, lockHandle
     * registered to a handler group
     * 
     * @param lockHandle
     *            the handle to set interest operations
     * @param interestOps
     *            the interest operations to set for handle
     * @throws InvalidInterestOpsException
     *             if the interest operations are not EventMask.{NOOP,LOCK}
     */
    public void interestOps( Handle lockHandle, int interestOps ) {
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( EventMask.xIsLock( interestOps ) || EventMask.xIsNoop( interestOps ) ) {
            if ( group.getEventHold( ) ) {
                if ( EventMask.xIsNoop( selector.interestOps( lockHandle ) ) && EventMask.xIsLock( interestOps ) ) {
                    triggerEvent( lockHandle, group );
                }
            }
            selector.processInterestOps( lockHandle, interestOps );
        } else {
            throw ( new InvalidInterestOpsException( interestOps ) );
        }        
    }
    
    
    /**
     * Deregisters the lockHandle, removing the associated handler group. Used
     * when lockHandle is deregistered by the JReactor. This method will always
     * succeed immediately.
     * <p>
     * The lockHandle can reference a LockHandlerGroup that may be open,
     * pending, or locked. If the group is locked, any queued messages on the
     * member managed handlers will be processed. Queued messages are discarded
     * if the group is pending.
     * <p>
     * PRECONDITION: lockHandle not null; lockHandle references a valid handler
     * group; lock event handler not running
     * 
     * @param lockHandle
     *            the lockHandle to deregister
     */
    public void deregister( Handle lockHandle ) {
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group.isPending( ) ){
                
            // is 'pending'
            
            pendingGroup.remove( lockHandle );            
            
            for ( Handler managedHandler : group.getPendingHandlers( ) ) {    
                group.removeMember( managedHandler );
                handlerHandlesMap.remove( managedHandler, lockHandle );
            }
            
            checkEvent( lockHandle, group );
        }
        
        
        if ( group.isLocked( ) ) {
            
            // is 'locked'            
            processUnlock( lockHandle, group );
            
        }
        

        // is 'open' or group is empty
        if ( handleGroupMap.containsKey( lockHandle ) ) {   // be sure lockHandle still registered after potentially processing resumeSelection from a locked group
            
            handleGroupMap.remove( lockHandle );
            
            openGroup.remove( lockHandle );
            
            Set<Handler> managedHandlers = group.getHandlers( );
            removeAllHandlerHandlesMap( managedHandlers, lockHandle );
        
            handleAdapterMap.remove( lockHandle );
            
            lockHandleErrorHandleMap.remove( lockHandle );
            
            selector.processDeregister( lockHandle );
        }
        
    }

    
    /**
     * Removes a managedHandler from the LockedSelector. The managedHandler may
     * be open or pending. No action is taken if the managedHandler is not a
     * member of any group within LockedSelector.
     * <p>
     * When the method completes, the managedHandler will not be a member of any
     * group in the LockedSelector.
     * <p>
     * Used when managedHandler is deregistered by JReactor. This method will
     * always succeed immediately.
     * <p>
     * PRECONDITION: managedHandler not null and not locked
     * 
     * @param managedHandler
     *            managed event handler to remove from LockSelector
     */
    public void remove( Handler managedHandler ) {
        
        
        List<Handle> lockHandles = handlerHandlesMap.removeAll( managedHandler );
        
        if ( lockHandles != null ) {
            for ( Handle lockHandle : lockHandles ) { // effectively:  for each group (referenced by lockHandle) for which managedHandler is a member
                
                LockHandlerGroup group = handleGroupMap.get( lockHandle );
                group.removeMember( managedHandler );
                handlerHandlesMap.remove( managedHandler, lockHandle );
                
                /*
                 * Must check if removed handler was in pending group and if the
                 * removal of the handler resulted in the pending group now
                 * becoming locked.
                 */
                if ( pendingGroup.contains( managedHandler ) ) {
                    
                    pendingGroup.remove( managedHandler, lockHandle );                    
                    
                    if ( !group.isEmpty( ) ) {
                        checkEvent( lockHandle, group );    
                    } else {
                        Handle errorHandle = lockHandleErrorHandleMap.get( lockHandle );

                        messageBlockBuilder.reset( );
                        
                        messageBlockBuilder.createMessageBlock( "lock" );
                        messageBlockBuilder.createMessageBlock( "lockHandle" );
                        messageBlockBuilder.createMessageBlock( lockHandle );
                        messageBlockBuilder.createMessageBlock( "errorHandle" );
                        messageBlockBuilder.createMessageBlock( errorHandle );

                        LockFailedException e = new LockFailedException( );
                        
                        selector.reportError( errorHandle, e, messageBlockBuilder.getMessageBlock( ) );
                    }
                    
                }
            
            }
            
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
     * <p>
     * PRECONDITION: lockHandle and errorHandle are not null and are registered;
     * lockHandle is open
     * 
     * @param lockHandle
     *            the lockHandle to lock
     * @param errorHandle
     *            the handle that receives the error event if the lock command
     *            is queued and fails
     * @throws InvalidResourceException
     *             if lockHandle does not reference a valid lock group
     * @throws LockFailedException
     *             if the handler group referenced by lockHandle is empty
     */
    public void lock( Handle lockHandle, Handle errorHandle ) {
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
            
        if ( group != null ) {
            
            if ( !group.isEmpty( ) ) {
                    
                openGroup.remove( lockHandle );
                
                for ( Handler managedHandler : group.getHandlers( ) ) {
                    if ( runLockAuthority.isActive( managedHandler ) ) {
                        // managedHandler running (may also be pending)
                        HandlerAdapter adapter = runLockAuthority.getHandlerAdapter( managedHandler );

                        messageBlockBuilder.reset( );
                        
                        messageBlockBuilder.createMessageBlock( "pending" );
                        messageBlockBuilder.createMessageBlock( "lock" );
                        messageBlockBuilder.createMessageBlock( "lockHandle" );
                        messageBlockBuilder.createMessageBlock( lockHandle );                
                        adapter.command( messageBlockBuilder.getMessageBlock( ) );

                        pendingGroup.add( managedHandler, lockHandle );
                        group.setPending( managedHandler );
                    } else {
                        // managedHandler idle and open:  not running, not pending, not locked
                        HandlerAdapter adapter = new HandlerAdapter( jreactor, managedHandler );
                        runLockAuthority.submitLocked( managedHandler, adapter );
                        lockGroup.add( managedHandler, lockHandle );
                        group.setLocked( managedHandler );
                    }                    
                }
                
                lockHandleErrorHandleMap.put( lockHandle, errorHandle );
                
                checkEvent( lockHandle, group );
                
            } else {
                throw( new LockFailedException( ) );
            }
        } else {
            throw ( new InvalidResourceException( ) );
        }
    }

    
    /**
     * Unlocks a locked handler group referenced by lockHandle. Event sources
     * serviced by event handlers that are members of the handler group will
     * resume generating ready events; those events will be subsequently
     * processed.
     * <p>
     * PRECONDITION: lockHandle not null; lockHandler not running; lockHandle is
     * locked
     * 
     * @param lockHandle
     *            the lockHandle referencing a group of event handlers to unlock
     * @throws InvalidHandleException
     *             if lockHandle does not reference a valid handler group
     * @throws UnlockFailedException
     *             if the handler group referenced by lockHandle is not locked
     *             or the the event handler has not completed
     */
    public void unlock( Handle lockHandle ) {
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );        
        
        if ( group != null ) {            
            if ( group.isLocked( ) ) {            
                
                // locked and handler not running                
                if ( group.getEventDone( ) ) {
                    // event handler completed
                    processUnlock( lockHandle, group );                    
                } else {
                    // handler has not yet run and is not running
                    throw( new UnlockFailedException( ) );
                }                
            } else {
                throw( new UnlockFailedException( ) );
            }
                                
        } else {
            throw( new InvalidResourceException( ) );
        }
    }
    
    
    /**
     * Unlocks the lockedHandlerGroup referenced by lockHandle. Event sources
     * serviced by event handlers that are members of the handler group will
     * resume generating ready events; those events will be subsequently
     * processed.
     * <p>
     * PRECONDITION: lockHandle and lockHandlerGroup not null; lockHandler not
     * running; lockHandle is locked
     * 
     * @param lockHandle
     *            the lockHandle, referencing a LockHandlerGroup, to unlock
     * @param lockHandlerGroup
     *            the group to unlock, referenced by lockHandle
     */            
    private void processUnlock( Handle lockHandle, LockHandlerGroup group ) {
        openGroup.add( lockHandle );
        for ( Handler managedHandler : group.getHandlers( ) ) {
            lockGroup.remove( managedHandler );
            group.setOpen( managedHandler );
            HandlerAdapter adapter = runLockAuthority.remove( managedHandler );
            if ( pendingGroup.contains( managedHandler ) ) {

                messageBlockBuilder.reset( );
                        
                messageBlockBuilder.createMessageBlock( "pending" );
                messageBlockBuilder.createMessageBlock( "unlock" );
                messageBlockBuilder.createMessageBlock( "next" );
                adapter.command( messageBlockBuilder.getMessageBlock( ) );
            }
            adapter.resumeSelection( );            
        }
        group.setEventNone( );
        HandlerAdapter adapter = handleAdapterMap.remove( lockHandle );
        MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( "enableAll" );
        adapter.command( mb1 );
        adapter.resumeSelection( );        
    }
    
    
    /**
     * Informs this selector an 'unlock' request on a lockHandle for which the
     * handler is currently running. The LockSelector will queue a message in
     * each handler that is a member of the group referenced by lockHandle.
     * <p>
     * PRECONDITION: lockHandle not null and registered; lockHandle is valid
     * lock handle; lockHandle locked; lockHandler running
     * 
     * @param lockHandle
     *            the handle referencing the group for which an unlock request
     *            was received
     * @throws UnlockFailedException
     *             if the handler group referenced by lockHandle is not locked
     *             or the the event handler has not completed
     */
    public void unlockRunning( Handle lockHandle ) {
        LockHandlerGroup group = handleGroupMap.get( lockHandle );                                                    
        
        Set<Handler> handlers = group.getHandlers( );
        
        for ( Handler managedHandler : handlers ) {                                                

            messageBlockBuilder.reset( );
                        
            messageBlockBuilder.createMessageBlock( "pending" );
            messageBlockBuilder.createMessageBlock( "unlock" );
            
            HandlerAdapter adapter = runLockAuthority.getHandlerAdapter( managedHandler );
            adapter.command( messageBlockBuilder.getMessageBlock( ) );
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
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isLockedMember( Handle lockHandle, Handler managedHandler ) {        
        boolean isLockedMember = false;
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group != null ) {
            isLockedMember = group.isLocked( ) && isMember( lockHandle, managedHandler );
        } else {
            throw( new InvalidResourceException( ) );
        }
        
        return( isLockedMember );
    }
    
    
    

    /**
     * Determines if the managed handler is open.
     * <p>
     * PRECONDITION: managedHandler not null
     * 
     * @param managedHandler
     *            handler on which to query open status
     * @return true if the managed handler is open and false otherwise; true is
     *          returned if managedHandler is not contained in any group
     */
    public boolean isOpen( Handler managedHandler ) {
        return( !( ( lockGroup.contains( managedHandler ) || pendingGroup.contains( managedHandler ) ) ) );
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
        boolean isOpen = false;
        if ( handleGroupMap.containsKey( lockHandle ) ) {
            isOpen = openGroup.contains( lockHandle );
        } else {
            throw( new InvalidResourceException( ) );
        }
        return( isOpen );
    }
    
    
    /**
     * Determines if the managed handler is pending.
     * <p>
     * PRECONDITION: managedHandler not null
     * 
     * @param managedHandler
     *            handler on which to query pending status
     * @return true if the managed handler is pending and false otherwise; false is
     *          returned if managedHandler is not contained in any group
     */
    public boolean isPending( Handler managedHandler ) {
        return( pendingGroup.contains( managedHandler ) );
    }
    
    
    /**
     * Determines if the handler group referenced by lockHandle is pending.
     * <p>
     * PRECONDITION: lockHandle not null
     * 
     * @param lockHandle
     *            handle on which to query pending status
     * @return true if the handler group referenced by lockHandle is pending and
     *          false otherwise
     * @throws InvalidResourceException
     *             if lockHandle does not references a valid handler group
     */
    public boolean isPending( Handle lockHandle ) {        
        boolean isPending = false;        
        if ( handleGroupMap.containsKey( lockHandle ) ) {
            isPending = pendingGroup.contains( lockHandle );
        } else {
            throw( new InvalidResourceException( ) );
        }
        return( isPending );
    }
    
    
    /**
     * Determines if the managed handler is locked.
     * <p>
     * PRECONDITION: managedHandler not null
     * 
     * @param managedHandler
     *            handler on which to query lock status
     * @return true if the managed handler is locked and false otherwise; false
     *         is returned if managedHandler is not contained in any group
     */
    public boolean isLocked( Handler managedHandler ) {
        return( lockGroup.contains( managedHandler ) );
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
        boolean isLocked = false;
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group != null ) {
            isLocked = lockGroup.contains( lockHandle ) & !pendingGroup.contains( lockHandle );
        } else {
            throw ( new InvalidResourceException( ) );
        }
        
        return( isLocked );
        
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
     * @param managedHandler
     *            handler to assume control over
     * @return true if managedHandler is now controlled by LockSelector and
     *         false otherwise
     */
    public boolean takePending( Handle lockHandle, Handler managedHandler ) {
        boolean tookPending = false;
        
        if ( pendingGroup.contains( managedHandler ) ) {
            
            Handle nextLockHandle = pendingGroup.peekNextHandle( managedHandler ); // looks at next lockHandle in FIFO order
            
            if ( lockHandle == nextLockHandle ) {
                tookPending = true;
            
                pendingGroup.getNextHandle( managedHandler ); // removes next lockHandle in FIFO order
                
                HandlerAdapter adapter =  new HandlerAdapter( jreactor, managedHandler );
                runLockAuthority.submitLocked( managedHandler, adapter );
                            
                lockGroup.add( managedHandler, nextLockHandle );
                
                LockHandlerGroup group = handleGroupMap.get( nextLockHandle );
                group.setLocked( managedHandler );            
            
                checkEvent( nextLockHandle, group );

            }
        }
        
        return( tookPending );

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
     * @param managedHandler
     *            handler to assume control over
     * @return true if managedHandler is now controlled by LockSelector and
     *         false otherwise
     */
    public boolean takePending( Handler managedHandler ) {
        boolean tookPending = false;
        
        if ( pendingGroup.contains( managedHandler ) ) {            
            
            Handle nextLockHandle = pendingGroup.getNextHandle( managedHandler ); // removes next lockHandle in FIFO order
            
            tookPending = true;            
            
            HandlerAdapter adapter =  new HandlerAdapter( jreactor, managedHandler );
            runLockAuthority.submitLocked( managedHandler, adapter );
                        
            lockGroup.add( managedHandler, nextLockHandle );
            
            LockHandlerGroup group = handleGroupMap.get( nextLockHandle );
            group.setLocked( managedHandler );            
        
            checkEvent( nextLockHandle, group );

        }
        
        return( tookPending );        
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
        return( handleAdapterMap.get( lockHandle ) );
    }
    
    /**
     * Queues the command referenced by mb to the HandlerAdapter of lockHandle.
     * <p>
     * Typically, this is used by the resumeSelection(..) method of the
     * JReactor. For example, the handler for a lockHandle was running and a
     * command like addMember(..) or interestOps(..)--e.g. commands affecting
     * the group membership or handler disposition--are queued. Since the
     * commands cannot be processed until the lockHandler is unlocked, the
     * JReactor's resumeSelection(..) method transfers the queued commands to
     * the adapter within LockSelector for processing when the lockHandle is
     * unlocked.
     * <p>
     * PRECONDITION: lockHandle and mb are not null; lockHandle references a
     * locked handler group
     * 
     * @param lockHandle
     *            lockHandle to queue this command
     * @param mb
     *            command to queue
     */
    public void addLockHandleCommand( Handle lockHandle, MessageBlock mb ) {        
        HandlerAdapter adapter = handleAdapterMap.get( lockHandle );
        adapter.command( mb );        
    }
    

    /**
     * Provides notification that a handle with a ready event was not
     * dispatched.
     * <p>
     * The group referenced to this lockHandle is set as having fired an event
     * that was not dispatched.
     * <p>
     * PRECONDITION: lockHandle is not null; lockHandle references a valid
     * handler group
     * 
     * @param lockHandle
     *            the handle that generated the event being checked-in
     * @param event
     *            the event being checked-in
     */
    public void checkin( Handle lockHandle, Event event ) {
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        group.setEventHold( );
    }

    
    /**
     * Accepts notice that the event handler for the lockHandle completed. The
     * group is set to no longer fire events until it is unlocked.
     * <p>
     * PRECONDITION: lockHandle is not null; lockHandle references a valid
     * handler group
     * 
     * @param lockHandle
     *            lockHandle whose even handler completed execution
     */
    public void resumeSelection( Handle lockHandle ) {
        
        LockHandlerGroup group = handleGroupMap.get( lockHandle );
        
        if ( group.getEventFired( ) ) {
            group.setEventDone( );
            lockHandleErrorHandleMap.remove( lockHandle );
        } else if ( group.getEventHold( ) ) {
            triggerEvent( lockHandle, group );
        }
    }
    

    /**
     * Checks if the handler group, referenced by lockHandle, is locked and, if
     * so, generates a ready event and submits the event to the Selector for
     * dispatch. If an event is generated, then the handleAdapterMap is updated
     * with a lockHandle mapped to a new and empty HandlerAdapter.
     * <p>
     * PRECONDITION: lockHandle and group not null; lockHandle references a
     * valid handler group; group referenced by lockHandle is not empty
     * 
     * @param lockHandle
     *            handle to check event
     * @param group
     *            handler group referenced by lockHandle
     */
    private void checkEvent( Handle lockHandle, LockHandlerGroup group ) {
        if ( group.isLocked( ) ) {
            
            Handler handler = selector.handler( lockHandle );
            HandlerAdapter adapter = new HandlerAdapter( jreactor, handler, lockHandle );
            handleAdapterMap.put( lockHandle, adapter );
            
            triggerEvent( lockHandle, group );
        }
    }
    
    
    /**
     * Generates a ready event, EventMask.LOCK, for the lockHandle and submits
     * the event to the Selector for dispatch. Sets the event generation status
     * of the group referenced by lockHandle to having fired a ready event that
     * is not yet processed.
     * <p>
     * PRECONDITION: lockHandle and group not null; lockHandle references a
     * valid handler group; group is a valid handler group; lockHandle
     * references group
     * 
     * @param lockHandle
     *            handle that generates the ready event
     * @param group
     *            LockHandlerGroup referenced by lockHandle
     */
    private void triggerEvent( Handle lockHandle, LockHandlerGroup group ) {
        selector.addReadyEvent( new Event( lockHandle, EventMask.LOCK ) );
        group.setEventFired( );
    }
    
    
    /**
     * Updates the handlerHandlesMap when deregistering a handler group
     * referenced by lockHandle.
     * <p>
     * PRECONDITION: lockHandle and managedHandlers not null
     * 
     * @param managedHandlers
     *            set of handlers to remove from the handler group referenced by
     *            lockHandle
     * @param lockHandle
     *            handle that references a handler group
     */
    private void removeAllHandlerHandlesMap( Set<Handler> managedHandlers, Handle lockHandle ) {
        
        for ( Handler managedHandler : managedHandlers ) {
            handlerHandlesMap.remove( managedHandler, lockHandle );
        }
    }

}
