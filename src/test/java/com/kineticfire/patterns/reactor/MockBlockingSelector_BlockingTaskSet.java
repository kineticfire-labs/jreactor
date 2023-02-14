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
import java.util.concurrent.TimeUnit;

import com.kineticfire.patterns.reactor.BlockingSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.Event;


public class MockBlockingSelector_BlockingTaskSet extends BlockingSelector {

	Handle triggerHandle;
	
	public MockBlockingSelector_BlockingTaskSet( ) {
		triggerHandle = null;
	}
   	
   	
   	public synchronized Handle register( Runnable runnable, Handler handler, int interestOps ) {
		return( new Handle( ) );
   	}
   	
   	
   	
   	public synchronized Handle registerGroup( Set<Runnable> newRunnableSet, Handler handler, int interestOps ) {
		return( new Handle( ) );
   	}
   	
	public synchronized boolean isRegistered( Handle blockingHandle ) {
		return( false );
	}
   	
   	
	public synchronized void interestOps( Handle handle, int interestOps ) {
	}
   	
   	
	public synchronized void checkin( Handle handle, Event event ) {
	}
	
	
	public synchronized void resumeSelection( Handle handle ) {
	}
   	
	
	protected synchronized void triggerReadyEvent( Handle handle ) {
		triggerHandle = handle;
	}	

	public void resetTriggerHandle( ) {
		triggerHandle = null;
	}

	public Handle getTriggerHandle( ) {
		return( triggerHandle );
	}
	
   	
	public synchronized void deregister( Handle handle ) {
	}
	
   	
	public void configureBlockingThreadPool( int workerThreadNum ) {
	}

	public void configureBlockingThreadPool( int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitLast ) {
	}
	
	
	public void configureBlockingThreadPool( int workerThreadNum, int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitLast ) {
	}
	
	
	public void shutdownInit( ) {
	}

	
	public void shutdownFinal( ) {
	}
	
	
	public void finalize( ) {
	}
    
}
