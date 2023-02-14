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


import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.handler.Handler;



public class MockCompositeSelector extends CompositeSelector {

	private int ops;
	private boolean event;
	private Handle processDeregisterHandle;

	public MockCompositeSelector( ) {
		ops = 0;
		event = false;
		processDeregisterHandle = null;
	}


	public void processRegister( Handle handle, Handler handler, SpecificSelector specificSelector, int interestOps ) {
		// do nothing
	}


        public void processInterestOps( Handle handle, int interestOps ) {
		this.ops = interestOps;
	}


	public void processDeregister( Handle handle ) {
		processDeregisterHandle = handle;
	}

	// added for testing
	public Handle getProcessDeregisterHandle( ) {
		return( processDeregisterHandle );
	}

	// added for testing
	public void clearProcessDeregisterHandle( ) {
		processDeregisterHandle = null;
	}

	public void processDeregister( Handler handler ) { 
		// do nothing
	}


	public int interestOps( Handle handle ) {
		return( 0 );
	}

	// added for testing
	public void setInterestOps( int ops ) {
		this.ops = ops;
	}

	// added for testing
	public int getInterestOps( ) {
		return( ops );
	}

	public void addReadyEvent( Event e ) {
		event = true;
	}

	// added for testing
	public void clearReadyEvent( ) {
		event = false;
	}

	//added for testing
	public boolean hasReadyEvent( ) {
		return( event );
	}


	public Handler handler( Handle handle ) {
		return( new GenericHandler( ) );
	}

}
