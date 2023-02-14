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

import com.kineticfire.patterns.reactor.QueueSelector;



public class MockQueueSelector_LinkedBlockingMessageQueue extends QueueSelector {

	//*********************************************
	//~ Instance/static variables

	Event readyEvent;	


   	//*********************************************
   	//~ Constructors

    

    	//*********************************************
    	//~ METHODS

    
	public void addReadyEvent( Event e ) {
		readyEvent = e;
   	}


	public Event getReadyEvent( ) {
		return( readyEvent );
	}
		
	public void clearReadyEvent( ) {
		readyEvent = null;
	}
}
