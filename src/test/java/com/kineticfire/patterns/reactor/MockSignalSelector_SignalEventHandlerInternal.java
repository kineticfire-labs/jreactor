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
import java.util.HashSet;
import java.util.Iterator;

import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.SignalEventHandlerInternal;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.EventGenerationState;

import com.kineticfire.patterns.reactor.DuplicateRegistrationException;
import com.kineticfire.patterns.reactor.InvalidInterestOpsException;
import com.kineticfire.patterns.reactor.InvalidResourceException;

import com.kineticfire.patterns.reactor.SignalSelector;


/**
 * This class provides for the creation, generation, and management of signal
 * events.
 * <p>
 * A JVM signal event is registered by providing its name (as a string) and
 * assigning a handler with interest operations. When a JVM signal fires, the
 * SignalSelector sends an event to the Selector for dispatch. If the JVM signal
 * thread causes the JVM terminate when the internal handler exits, the
 * SignalSelector causes the JVM signal thread to block. The thread will remain
 * blocked until the handle/handler for the JVM is deregistered or the
 * SignalSelector is closed.
 * <p>
 * Currently supported signals are: "shutdownhook".
 * <p>
 * This class is thread-safe.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */


public class MockSignalSelector_SignalEventHandlerInternal extends SignalSelector {

	boolean fired;
	
   	//*********************************************
   	//~ Constructors

    	public MockSignalSelector_SignalEventHandlerInternal( ) {	
		fired = false;
    	}




    	//*********************************************
    	//~ METHODS

	public synchronized Handle register( String signalName, Handler handler, int interestOps ) {
		return( new Handle( ) );
	}

	

	public synchronized boolean isRegistered( String signalName ) {
		return( false );
	}

	
	public synchronized boolean isRegistered( Handle handle ) {
		return( false );
	}
	
	
	public synchronized void interestOps( Handle handle, int interestOps ) {
	}
	
	
	public synchronized void deregister( Handle handle ) {
	}


	public synchronized void checkin( Handle handle, Event event ) {
	}	
	
	
	public synchronized void resumeSelection( Handle handle ) {
	} 
	

	protected synchronized boolean signalFired( Handle handle ) {
		return( fired );
	}

	// added for testing
	protected synchronized void setSignalFired( boolean fired ) {
		this.fired = fired;
	}
	
	private String validate( String signalName ) {
		return( new String( ) );
	}	
	

	private void installHandler( String validSignalName, SignalEventHandlerInternal internalHandler ) {
	}
	
	private void uninstallHandler( String validSignalName, SignalEventHandlerInternal internalHandler ) {
	}

	private void triggerEvent( Handle handle ) {
	}
	
	
	public synchronized void shutdown( ) {
	}
	
	
    	public void finalize( ) {
    	}
    
    
}
