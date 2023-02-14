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

import com.kineticfire.patterns.reactor.RunLockHandlerTracker;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.HandlerAdapter;

import com.kineticfire.patterns.reactor.GenericHandler_RunLockHandlerTracker;


public class RunLockHandlerTracker_Test {


        @Test
        public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
        }



	@Test
	public void testSubmitRunning( ) {
		RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

		GenericHandler_RunLockHandlerTracker handler = new GenericHandler_RunLockHandlerTracker( );
		HandlerAdapter adapter = new HandlerAdapter( );
		tracker.submitRunning( handler, adapter );

		Assert.assertTrue( tracker.isRunning( handler ) );
		Assert.assertTrue( tracker.isActive( handler ) );
		Assert.assertFalse( tracker.isLocked( handler ) );
	}


        @Test
        public void testIsRunning( ) {
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

                GenericHandler_RunLockHandlerTracker handler = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler2 = new GenericHandler_RunLockHandlerTracker( );
                HandlerAdapter adapter = new HandlerAdapter( );
                tracker.submitRunning( handler, adapter );

                Assert.assertTrue( tracker.isRunning( handler ) );
                Assert.assertFalse( tracker.isRunning( handler2 ) );
        }


        @Test
        public void testSubmitLocked( ) {
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

                GenericHandler_RunLockHandlerTracker handler = new GenericHandler_RunLockHandlerTracker( );
                HandlerAdapter adapter = new HandlerAdapter( );
                tracker.submitLocked( handler, adapter );

                Assert.assertFalse( tracker.isRunning( handler ) );
                Assert.assertTrue( tracker.isActive( handler ) );
                Assert.assertTrue( tracker.isLocked( handler ) );
        }


        @Test
        public void testIsLocked( ) {
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

                GenericHandler_RunLockHandlerTracker handler = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler2 = new GenericHandler_RunLockHandlerTracker( );
                HandlerAdapter adapter = new HandlerAdapter( );
                tracker.submitLocked( handler, adapter );

                Assert.assertTrue( tracker.isLocked( handler ) );
                Assert.assertFalse( tracker.isLocked( handler2 ) );
        }


        @Test
        public void testIsActive( ) {
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

                GenericHandler_RunLockHandlerTracker handler = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler1 = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler2 = new GenericHandler_RunLockHandlerTracker( );
                HandlerAdapter adapter = new HandlerAdapter( );
                tracker.submitLocked( handler1, adapter );
                tracker.submitLocked( handler2, adapter );

                Assert.assertTrue( tracker.isLocked( handler1 ) );
                Assert.assertTrue( tracker.isLocked( handler2 ) );
                Assert.assertFalse( tracker.isLocked( handler ) );
        }


        @Test
        public void testGetHandlerAdapter( ) {
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

                GenericHandler_RunLockHandlerTracker handler = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler2 = new GenericHandler_RunLockHandlerTracker( );
                HandlerAdapter adapter = new HandlerAdapter( );
                tracker.submitLocked( handler, adapter );

                Assert.assertSame( tracker.getHandlerAdapter( handler ), adapter );
                Assert.assertNull( tracker.getHandlerAdapter( handler2 ) );
        }


        @Test
        public void testRemove( ) {
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );

                GenericHandler_RunLockHandlerTracker handler1 = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler2 = new GenericHandler_RunLockHandlerTracker( );
                GenericHandler_RunLockHandlerTracker handler3 = new GenericHandler_RunLockHandlerTracker( );
                HandlerAdapter adapter1 = new HandlerAdapter( );
                HandlerAdapter adapter2 = new HandlerAdapter( );
                tracker.submitRunning( handler1, adapter1 );
                tracker.submitLocked( handler2, adapter2 );

                Assert.assertTrue( tracker.isRunning( handler1 ) );
		Assert.assertSame( tracker.remove( handler1 ), adapter1 );
                Assert.assertFalse( tracker.isRunning( handler1 ) );

                Assert.assertTrue( tracker.isLocked( handler2 ) );
		Assert.assertSame( tracker.remove( handler2 ), adapter2 );
                Assert.assertFalse( tracker.isLocked( handler2 ) );

		Assert.assertNull( tracker.remove( handler3 ) );
		
        }

}
