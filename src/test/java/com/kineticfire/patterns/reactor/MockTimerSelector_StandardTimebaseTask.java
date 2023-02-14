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


import com.kineticfire.patterns.reactor.TimerSelector;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.StandardTimebaseTask;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;



public class MockTimerSelector_StandardTimebaseTask extends TimerSelector {


	Handle expiredHandle;


   	// *********************************************
   	// ~ Constructors


   	public MockTimerSelector_StandardTimebaseTask( ) {
		expiredHandle = null;
   	}



    	// *********************************************
    	// ~ METHODS

   	
   	public Handle registerOnce( Date time, Handler handler, int interestOps ) {
		return( new Handle( ) );
   	}

   	
	public Handle registerOnce( long delay, Handler handler, int interestOps ) {
		return( new Handle( ) );
	}
	

	public Handle registerFixedDelay( Date firstTime, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
	}



   	public Handle registerFixedDelay( long delay, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
   	}


   	
	public Handle registerFixedRate( Date firstTime, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
	}

	public Handle registerFixedRate( long delay, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
	}


	private StandardTimebaseTask registerInternal( Handler handler, Handle handle, int interestOps, boolean isRecurring ) {
		return( new StandardTimebaseTask( ) );
   	}

	
	public boolean isRegistered( Handle timerHandle ) {
		return( false );
	}

	
	
	public void interestOps( Handle handle, int interestOps ) {
	}
	
	

	protected void timerExpired( Handle handle ) {
		expiredHandle = handle;
	}

	// added for testing
	protected Handle getTimerExpiredHandle( ) {
		return( expiredHandle );
	}


	// added for testing
	protected void clearTimerExpiredHandle( ) {
		expiredHandle = null;
	}


	public boolean cancel( Handle handle ) {
		return( false );
	}

	
	public void deregister( Handle handle ) {
	}
	
	
	public void checkin( Handle handle, Event event ) {
	}

	
	public void resumeSelection( Handle handle ) {
	}    
	
	
    	public void shutdown( ) {
    	}
    
    
    	public void finalize( ) {
    	}

}
