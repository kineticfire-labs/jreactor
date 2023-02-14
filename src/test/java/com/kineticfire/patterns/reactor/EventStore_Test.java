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

import com.kineticfire.patterns.reactor.EventStore;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.handler.Handler;

import com.kineticfire.patterns.reactor.GenericHandler;

public class EventStore_Test {

	@Test
	public void testConstructor( ) {
		EventStore store = new EventStore( );
	}


	@Test
	public void testStoreOne( ) {
		EventStore s = new EventStore( );
		GenericHandler h1 = new GenericHandler( );
		Event e1 = new Event( );
		s.store( h1, e1 );

		Assert.assertTrue( s.contains( h1 ) );
		Event eA = s.retrieve( h1 );
		Assert.assertSame( eA, e1 );
	}


        @Test
        public void testStoreMultiUnique( ) {
                EventStore s = new EventStore( );
                GenericHandler h1 = new GenericHandler( );
                GenericHandler h2 = new GenericHandler( );
                GenericHandler h3 = new GenericHandler( );
                Event e1 = new Event( );
                Event e2 = new Event( );
                Event e3 = new Event( );
                s.store( h1, e1 );
                s.store( h2, e2 );
                s.store( h3, e3 );

                Assert.assertTrue( s.contains( h1 ) );
                Assert.assertTrue( s.contains( h2 ) );
                Assert.assertTrue( s.contains( h3 ) );
                Event eA = s.retrieve( h1 );
                Event eB = s.retrieve( h2 );
                Event eC = s.retrieve( h3 );
		Assert.assertSame( eA, e1 );
		Assert.assertSame( eB, e2 );
		Assert.assertSame( eC, e3 );
        }


        @Test
        public void testStoreMultiNonunique( ) {
                EventStore s = new EventStore( );
                GenericHandler h1 = new GenericHandler( );
                GenericHandler h2 = new GenericHandler( );
                GenericHandler h3 = new GenericHandler( );
                Event e11 = new Event( );
                Event e12 = new Event( );
                Event e13 = new Event( );
                Event e21 = new Event( );
                Event e22 = new Event( );
                Event e23 = new Event( );
                Event e31 = new Event( );
                Event e32 = new Event( );
                Event e33 = new Event( );
                s.store( h1, e11 ); 
                s.store( h1, e12 ); 
                s.store( h1, e13 ); 
                s.store( h2, e21 ); 
                s.store( h2, e22 ); 
                s.store( h2, e23 ); 
                s.store( h3, e31 );
                s.store( h3, e32 );
                s.store( h3, e33 );

                Assert.assertTrue( s.contains( h1 ) );
                Assert.assertTrue( s.contains( h2 ) );
                Assert.assertTrue( s.contains( h3 ) );
                Event ee11 = s.retrieve( h1 );
                Event ee12 = s.retrieve( h1 );
                Event ee13 = s.retrieve( h1 );
                Event ee21 = s.retrieve( h2 );
                Event ee22 = s.retrieve( h2 );
                Event ee23 = s.retrieve( h2 );
                Event ee31 = s.retrieve( h3 );
                Event ee32 = s.retrieve( h3 );
                Event ee33 = s.retrieve( h3 );
                Assert.assertSame( ee11, e11 );
                Assert.assertSame( ee12, e12 );
                Assert.assertSame( ee13, e13 );
                Assert.assertSame( ee21, e21 );
                Assert.assertSame( ee22, e22 );
                Assert.assertSame( ee23, e23 );
                Assert.assertSame( ee31, e31 );
                Assert.assertSame( ee32, e32 );
                Assert.assertSame( ee33, e33 );
        }


        @Test
        public void testRetrieve( ) {
                EventStore s = new EventStore( );
                GenericHandler h1 = new GenericHandler( );
                GenericHandler h2 = new GenericHandler( );
                GenericHandler h3 = new GenericHandler( );
                Event e11 = new Event( );
                Event e12 = new Event( );
                Event e13 = new Event( );
                Event e21 = new Event( );
                Event e22 = new Event( );
                Event e23 = new Event( );
                Event e31 = new Event( );
                Event e32 = new Event( );
                Event e33 = new Event( );
                s.store( h1, e11 );
                s.store( h1, e12 );
                s.store( h1, e13 );
                s.store( h2, e21 );
                s.store( h2, e22 );
                s.store( h2, e23 );
                s.store( h3, e31 );
                s.store( h3, e32 );
                s.store( h3, e33 );

                Assert.assertTrue( s.contains( h1 ) );
                Assert.assertTrue( s.contains( h2 ) );
                Assert.assertTrue( s.contains( h3 ) );
                Event ee11 = s.retrieve( h1 );
                Event ee12 = s.retrieve( h1 );
                Event ee13 = s.retrieve( h1 );
                Event ee21 = s.retrieve( h2 );
                Event ee22 = s.retrieve( h2 );
                Event ee23 = s.retrieve( h2 );
                Event ee31 = s.retrieve( h3 );
                Event ee32 = s.retrieve( h3 );
                Event ee33 = s.retrieve( h3 );
                Assert.assertSame( ee11, e11 );
                Assert.assertSame( ee12, e12 );
                Assert.assertSame( ee13, e13 );
                Assert.assertSame( ee21, e21 );
                Assert.assertSame( ee22, e22 );
                Assert.assertSame( ee23, e23 );
                Assert.assertSame( ee31, e31 );
                Assert.assertSame( ee32, e32 );
                Assert.assertSame( ee33, e33 );
        }


        @Test
        public void testContains( ) {
                EventStore s = new EventStore( );
                GenericHandler h1 = new GenericHandler( );
                GenericHandler h2 = new GenericHandler( );
                GenericHandler h3 = new GenericHandler( );
                Event e11 = new Event( );
                Event e12 = new Event( );
                Event e13 = new Event( );
                Event e2 = new Event( );
                s.store( h1, e11 );
                s.store( h1, e12 );
                s.store( h1, e13 );
                s.store( h2, e2 );

                Assert.assertTrue( s.contains( h1 ) );
                Assert.assertTrue( s.contains( h2 ) );
                Assert.assertFalse( s.contains( h3 ) );
        }


        @Test
        public void testRemove( ) {
                EventStore s = new EventStore( );
                GenericHandler h1 = new GenericHandler( );
                GenericHandler h2 = new GenericHandler( );
                GenericHandler h3 = new GenericHandler( );
                Event e11 = new Event( );
                Event e12 = new Event( );
                Event e13 = new Event( );
                Event e2 = new Event( );
                s.store( h1, e11 );
                s.store( h1, e12 );
                s.store( h1, e13 );
                s.store( h2, e2 );

                Assert.assertTrue( s.contains( h1 ) );
                Assert.assertTrue( s.contains( h2 ) );
                Assert.assertFalse( s.contains( h3 ) );

		List<Event> l1 = s.remove( h1 );
		List<Event> l2 = s.remove( h2 );
		List<Event> l3 = s.remove( h3 );

                Assert.assertFalse( s.contains( h1 ) );
                Assert.assertFalse( s.contains( h2 ) );
                Assert.assertFalse( s.contains( h3 ) );

		Assert.assertEquals( l1.size( ), 3 );
		Assert.assertEquals( l2.size( ), 1 );
		Assert.assertNull( l3 );

		Assert.assertSame( l1.get( 0 ), e11 );
		Assert.assertSame( l1.get( 1 ), e12 );
		Assert.assertSame( l1.get( 2 ), e13 );
		Assert.assertSame( l2.get( 0 ), e2 );
        }

}
