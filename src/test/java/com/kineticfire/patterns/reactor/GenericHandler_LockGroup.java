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


import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

public class GenericHandler_LockGroup implements Handler {

	private Handle handle;
	private int readyOps;
	private MessageBlock info;


	public GenericHandler_LockGroup( ) {
		// does nothing
		handle = null;
		readyOps = 0;
		info = null;	
	}


	public void handleEvent( Handle handle, int readyOps, MessageBlock info ) {
		// stores args 
		this.handle = handle;
		this.readyOps = readyOps;
		this.info = info;
	}
}
