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


import org.testng.annotations.Test;
import org.testng.Assert;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.kineticfire.patterns.reactor.SignalEventHandlerInternal;
import com.kineticfire.patterns.reactor.MockSignalSelector_SignalEventHandlerInternal;


public class SignalEventHandlerInternal_Test {

	@Test
	public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockSignalSelector_SignalEventHandlerInternal selector = new MockSignalSelector_SignalEventHandlerInternal( );
		Handle handle = new Handle( );
		SignalEventHandlerInternal internal = new SignalEventHandlerInternal( selector, handle );

                Field signalSelectorF = SignalEventHandlerInternal.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( internal );
                Assert.assertSame( selector, signalSelector );

                Field handleF = SignalEventHandlerInternal.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Handle handleG = (Handle)handleF.get( internal );
                Assert.assertSame( handle, handleG );

                Field queueF = SignalEventHandlerInternal.class.getDeclaredField( "queue" );
                queueF.setAccessible( true );
                LinkedBlockingQueue<Byte> queue = (LinkedBlockingQueue<Byte>)queueF.get( internal );
		Assert.assertEquals( queue.size( ), 0 );
	}


        @Test
        public void testRunCancel( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockSignalSelector_SignalEventHandlerInternal selector = new MockSignalSelector_SignalEventHandlerInternal( );
                Handle handle = new Handle( );
                SignalEventHandlerInternal internal = new SignalEventHandlerInternal( selector, handle );

		selector.setSignalFired( true );
		
		internal.cancel( );
		internal.run( );
        }

}
