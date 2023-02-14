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


import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

import com.kineticfire.patterns.reactor.JReactor;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

/**
 * This class implements a HandlerAdapter. The HandlerAdapter provides
 * coordination between the JReactor and developer code--implementing the
 * Handler interface--and between the JReactor and the locking process within
 * the Selector (specifically,the LockSelector).
 * <p>
 * The role of the adapter is to:
 * <p>
 * (1) trigger the execution of developer code which implements the Handler
 * interface. The HandlerAdapter implements the Runnable interface and may be
 * directly added to and executed by an Executor.
 * <p>
 * (2) store queued commands for the Handler belonging to the HandlerAdapter
 * while the Handler is running. The commands will be processed when the Handler
 * completes.
 * <p>
 * (3) notify the JReactor when the Handler completes, calling
 * resumeSelection(..) on the Handle and passing the queued commands.
 * 
 * @author Kris Hall
 * @version 1.20.07
 */


public class HandlerAdapter implements Runnable {

    //*********************************************
    //~ Instance/static variables
    private JReactor jreactor;
    private Handler handler;
    private Handle handle;
    private int readyOps;
    private MessageBlock info;
    private List<MessageBlock> commands;
    private Set<Handle> eventHandleNoted;


       //*********************************************
       //~ Constructors


    /**
     * Constructs the HandlerAdapter. The references to jreactor, handler,
     * handle, and info are null; the ready operations are set to zero; and the
     * queued command List is empty.
     * 
     */
    public HandlerAdapter( ) {
        jreactor = null;
        handler = null;
        handle = null;
        readyOps = 0;
        info = null;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
    }
        
    
       /**
     * Creates a new HandlerAdapter with the given reference to the JReactor.
     * The references to handler, handle, and info are null; the ready
     * operations are set to zero; and the queued command List is empty.
     * <p>
     * PRECONDITION: jreactor is not null
     * 
     * @param jreactor
     *            reference to the conrolling JReactor
     */        
       public HandlerAdapter( JReactor jreactor ) {
        this.jreactor = jreactor;
        handler = null;
        handle = null;
        readyOps = 0;
        info = null;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
       }
       
       
       /**
     * Creates a new HandlerAdapter with the given references to the JReactor
     * and handler. The references to handle and info are null; the ready
     * operations are set to zero; and the queued command List is empty.
     * <p>
     * PRECONDITION: jreactor not null
     * 
     * @param jreactor
     *            reference to the conrolling JReactor
     * @param handler
     *            the handler to execute
     */
       public HandlerAdapter( JReactor jreactor, Handler handler ) {
        this.jreactor = jreactor;
        this.handler = handler;
        handle = null;
        readyOps = 0;
        info = null;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
       }
       
       
       /**
     * Creates a new HandlerAdapter with the given references to the JReactor
     * and handle. The references to handler and info are null; the ready
     * operations are set to zero; and the queued command List is empty.
     * <p>
     * Records the handle as referencing an event for the handler serviced by
     * this adapter.
     * <p>
     * PRECONDITION: jreactor not null
     * 
     * @param jreactor
     *            reference to the conrolling JReactor
     * @param handle
     *            the handle referencing the event source that generated the
     *            ready event
     */
       public HandlerAdapter( JReactor jreactor, Handle handle ) {
        this.jreactor = jreactor;
        handler = null;
        this.handle = handle;
        readyOps = 0;
        info = null;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
        
        eventHandleNoted.add( handle );
       }
       
       
       /**
     * Creates a new HandlerAdapter with the given references to the JReactor,
     * handler, and handle. The reference to info is null, the ready operations
     * are set to zero, and the queued command List is empty.
     * <p>
     * Records the handle as referencing an event for the handler serviced by
     * this adapter.
     * <p>
     * PRECONDITION: jreactor not null
     * 
     * @param jreactor
     *            reference to the conrolling JReactor
     * @param handler
     *            the handler to execute
     * @param handle
     *            the handle referencing the event source that generated the
     *            ready event
     */
       public HandlerAdapter( JReactor jreactor, Handler handler, Handle handle ) {
        this.jreactor = jreactor;
        this.handler = handler;
        this.handle = handle;
        readyOps = 0;
        info = null;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
        
        eventHandleNoted.add( handle );
       }
       
       
       /**
     * Creates a new HandlerAdapter with the given references to JReactor,
     * handler, and handle. The reference to info is null; the ready operations
     * are set to readyOps; and the queued command List is empty.
     * <p>
     * Records the handle as referencing an event for the handler serviced by
     * this adapter.
     * <p>
     * PRECONDITION: jreactor and handle, are not null; readyOps consists of
     * valid values within EventMask
     * 
     * @param jreactor
     *            reference to the conrolling JReactor
     * @param handler
     *            the handler to execute
     * @param handle
     *            the handle referencing the event source that generated the
     *            ready event
     * @param readyOps
     *            the interest operations for which this handle may be ready
     */
       public HandlerAdapter( JReactor jreactor, Handler handler, Handle handle, int readyOps ) {
        this.jreactor = jreactor;
        this.handler = handler;
        this.handle = handle;
        this.readyOps = readyOps;
        info = null;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
        
        eventHandleNoted.add( handle );
       }
       
 
       /**
     * Creates a new HandlerAdapter with the given references to JReactor,
     * handler, handle, and info. The ready operations are set to readyops and
     * the queued command List is empty.
     * <p>
     * Records the handle as referencing an event for the handler serviced by
     * this adapter.
     * <p>
     * PRECONDITION: jreactor and handle, are not null; readyOps consists of
     * valid values within EventMask
     * 
     * @param jreactor
     *            reference to the conrolling JReactor
     * @param handler
     *            the handler to execute
     * @param handle
     *            the handle referencing the event source that generated the
     *            ready event
     * @param readyOps
     *            the interest operations for which this handle may be ready
     * @param info
     *            additional information for this event
     */
       public HandlerAdapter( JReactor jreactor, Handler handler, Handle handle, int readyOps, MessageBlock info ) {
        this.jreactor = jreactor;
        this.handler = handler;
        this.handle = handle;
        this.readyOps = readyOps;
        this.info = info;
        commands = new LinkedList<MessageBlock>( );
        eventHandleNoted = new HashSet<Handle>( );
        
        eventHandleNoted.add( handle );
       }

       
       /**
     * Triggers the execution of the handler and notifies the JReactor when the
     * handler completes.
     * <p>
     * The handleEvent( Handle, int, MessageBlock ) method of handler,
     * implementing the Handler interface, is called to execute the handler.
     * When the handler exits, the the JReactor is notified through a call to
     * resumeSelection() and passes any queued commands.
     * <p>
     * PRECONDITION: handler, jreactor, and handle are not null
     * 
     */
       public void run( ) {
           handler.handleEvent( handle, readyOps, info );
           resumeSelection( );
       }
       
       
       /**
     * Notify JReactor that the handler has completed. Process queued commands
     * and, if appropriate, re-enable the handle referencing the triggered event
     * to generate further events. If handle is null, then all handles serviced
     * by the handler are re-enabled for ready events, as appropriate.
     * <p>
     * PRECONDITION: jreactor and commands are not null
     */
       public void resumeSelection( ) {
           jreactor.resumeSelection( handler, handle, commands );
       }
       
       
       /**
     * Queues the command, mb, to be processed later.
     * <p>
     * PRECONDITION: mb not null
     * 
     * @param mb
     *            the command to queue to process when the handler exits
     */
    public void command( MessageBlock mb ) {
        commands.add( mb );
    }    
    
    
    /**
     * Query if the handle was referenced by an event for the handler serviced
     * by this adapter. Records the handle as referencing an event for the
     * handler serviced by this adapter if not already noted. Returns true if
     * the handle was already recorded and false otherwise.
     * 
     * @param handle
     *            the handle to query and record
     * @return true if the handle was already recorded as referencing an event
     *         for the handler serviced by this adapter and false otherwise
     */
    public boolean eventHandleNoted( Handle handle ) {
        boolean contained = false;
        if ( eventHandleNoted.contains( handle ) ) {
            contained = true;
        } else {
            // contained = false
            eventHandleNoted.add( handle );
        }
        return( contained );
    }
    
}
