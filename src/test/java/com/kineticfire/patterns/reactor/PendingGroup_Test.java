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

import java.util.List;
import java.lang.reflect.Field;

import com.kineticfire.patterns.reactor.PendingGroup;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.handler.Handler;

import com.kineticfire.patterns.reactor.GenericHandler_PendingGroup;

public class PendingGroup_Test {

	@Test
	public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		PendingGroup g = new PendingGroup( );

		Field handlerListF = PendingGroup.class.getDeclaredField( "handlerList" );
                handlerListF.setAccessible( true );
                List<Handle> handlerList = (List<Handle>)handlerListF.get( g );
		Assert.assertEquals( handlerList.size( ), 0 );

		Field handleListF = PendingGroup.class.getDeclaredField( "handleList" );
                handleListF.setAccessible( true );
                List<Handle> handleList = (List<Handle>)handleListF.get( g );
		Assert.assertEquals( handleList.size( ), 0 );
	}


        @Test
        public void testAdd( ) {
		PendingGroup g = new PendingGroup( );

		GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
		GenericHandler_PendingGroup handler2 = new GenericHandler_PendingGroup( );
		Handle handle1 = new Handle( );
		Handle handle2 = new Handle( );

		Assert.assertFalse( g.contains( handler1 ) );
		Assert.assertFalse( g.contains( handle1 ) );
		g.add( handler1, handle1 );
		Assert.assertTrue( g.contains( handler1 ) );
		Assert.assertTrue( g.contains( handle1 ) );

		Assert.assertFalse( g.contains( handler2 ) );
		Assert.assertFalse( g.contains( handle2 ) );
		g.add( handler2, handle2 );
		Assert.assertTrue( g.contains( handler2 ) );
		Assert.assertTrue( g.contains( handle2 ) );
		Assert.assertTrue( g.contains( handler1 ) );
		Assert.assertTrue( g.contains( handle1 ) );
        }


        @Test
        public void testRemoveHandlerHandle( ) {
                PendingGroup g = new PendingGroup( );

		GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
		Handle handle1 = new Handle( );
		Handle handle2 = new Handle( );

		// unsuccessful; no error
		g.remove( handler1, handle1 );

		// successful remove; one handler/handle
                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );
		g.remove( handler1, handle1 );
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );

		// successful remove; one handler/2handles
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );
                g.add( handler1, handle1 );
                g.add( handler1, handle2 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handle2 ) );
		g.remove( handler1, handle2 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle2 ) );
                Assert.assertTrue( g.contains( handle1 ) );
        }


        @Test
        public void testRemoveHandle( ) {
                PendingGroup g = new PendingGroup( );

                GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
                GenericHandler_PendingGroup handler2 = new GenericHandler_PendingGroup( );
                GenericHandler_PendingGroup handlerA = new GenericHandler_PendingGroup( );
                Handle handle1 = new Handle( );
                Handle handleA = new Handle( );

                // unsuccessful; no error
                g.remove( handle1 );

                // successful remove; one handler/handle
                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                g.remove( handle1 );
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );

                // successful remove; one handler/2handles
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );
                Assert.assertFalse( g.contains( handlerA ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handleA ) );
                g.add( handler1, handle1 );
                g.add( handler2, handle1 );
                g.add( handlerA, handleA );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handlerA ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handleA ) );
                g.remove( handle1 );
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handlerA ) );
                Assert.assertTrue( g.contains( handleA ) );
        }


        @Test
        public void testContainsHandler( ) {
                PendingGroup g = new PendingGroup( );

                GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
                GenericHandler_PendingGroup handler2 = new GenericHandler_PendingGroup( );
                Handle handle1 = new Handle( );
                Handle handle2 = new Handle( );

                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );

                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );

                g.add( handler2, handle2 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
        }


        @Test
        public void testContainsHandle( ) {
                PendingGroup g = new PendingGroup( );

                GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
                GenericHandler_PendingGroup handler2 = new GenericHandler_PendingGroup( );
                Handle handle1 = new Handle( );
                Handle handle2 = new Handle( );

                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );

                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );

                g.add( handler2, handle2 );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handle2 ) );
        }


        @Test
        public void testGetNextHandle( ) {
                PendingGroup g = new PendingGroup( );

                GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
                GenericHandler_PendingGroup handler2 = new GenericHandler_PendingGroup( );
                Handle handle1 = new Handle( );
                Handle handle2 = new Handle( );

                // unsuccessful; no error
                Assert.assertNull( g.getNextHandle( handler1 ) );

                // successful remove; one handler/handle
                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertSame( g.getNextHandle( handler1 ), handle1 );
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );

                // successful remove; one handler/2handles
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );
                g.add( handler1, handle1 );
                g.add( handler1, handle2 );
                g.add( handler2, handle2 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handle2 ) );
                Assert.assertSame( g.getNextHandle( handler1 ), handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handle2 ) );
                Assert.assertSame( g.getNextHandle( handler1 ), handle2 );
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handle2 ) );
        }


        @Test
        public void testPeekNextHandle( ) {
                PendingGroup g = new PendingGroup( );

                GenericHandler_PendingGroup handler1 = new GenericHandler_PendingGroup( );
                GenericHandler_PendingGroup handler2 = new GenericHandler_PendingGroup( );
                Handle handle1 = new Handle( );
                Handle handle2 = new Handle( );

                // unsuccessful; no error
                Assert.assertNull( g.peekNextHandle( handler1 ) );

                // successful; one handler/handle
                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertSame( g.peekNextHandle( handler1 ), handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );

		g.remove( handle1 );

                // successful; one handler/2handles
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );
                g.add( handler1, handle1 );
                g.add( handler1, handle2 );
                g.add( handler2, handle2 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handle2 ) );
                Assert.assertSame( g.peekNextHandle( handler1 ), handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handle2 ) );
        }
}
