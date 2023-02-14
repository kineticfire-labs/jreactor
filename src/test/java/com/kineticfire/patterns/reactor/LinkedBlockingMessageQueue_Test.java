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

import com.kineticfire.patterns.reactor.LinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Event;

import com.kineticfire.patterns.reactor.MockQueueSelector_LinkedBlockingMessageQueue;

/**
 * Not tested:
 *      - triggerEvent( )
 *           - but is tested by other methods
 */


public class LinkedBlockingMessageQueue_Test {

	@Test
	public void testConstructorNoArg( ) {
		LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( );
		Assert.assertTrue( q.isEmpty( ) );
		Assert.assertEquals( q.remainingCapacity( ), Integer.MAX_VALUE );
	}


        @Test
        public void testConstructorCapacity( ) {
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                Assert.assertTrue( q.isEmpty( ) );
		Assert.assertEquals( q.remainingCapacity( ), 10 );
        }


        @Test
        public void testOffer( ) {
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( );
		MessageBlock mb3 = new MessageBlock( );

                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );

                Assert.assertEquals( q.size( ), 0 );
		q.offer( mb1 );
                Assert.assertEquals( q.size( ), 1 );
		q.offer( mb2 );
                Assert.assertEquals( q.size( ), 2 );
		q.offer( mb3 );
                Assert.assertEquals( q.size( ), 3 );
        }


        @Test
        public void testOfferEventThrown( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );

                MockQueueSelector_LinkedBlockingMessageQueue s = new MockQueueSelector_LinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                q.configure( s, handle );

		Assert.assertNull( s.getReadyEvent( ) );
                Assert.assertEquals( q.size( ), 0 );
		q.offer( mb1 );
                Assert.assertEquals( q.size( ), 1 );
		Assert.assertNotNull( s.getReadyEvent( ) );
		s.clearReadyEvent( );
		Assert.assertNull( s.getReadyEvent( ) );
		q.offer( mb2 );
		Assert.assertNotNull( s.getReadyEvent( ) );
	}


        @Test
        public void testOfferEventSource( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );

                MockQueueSelector_LinkedBlockingMessageQueue s = new MockQueueSelector_LinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 2 );
                q.configure( s, handle );

                q.offer( mb1 );
                q.offer( mb2 );
                q.configure( s, handle );
                Assert.assertEquals( q.size( ), 2 );
		s.clearReadyEvent( );
		Assert.assertNull( s.getReadyEvent( ) );

                q.offer( mb3 );
		Assert.assertNull( s.getReadyEvent( ) );
        }


        @Test
        public void testOfferPressure( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
		Double d1;
		Double d2;
		Double d3;
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                Assert.assertEquals( q.size( ), 0 );
                d1 = q.offerPressure( mb1 );
                Assert.assertEquals( q.size( ), 1 );
                Assert.assertEquals( d1, (double)0.1 );
                d2 = q.offerPressure( mb2 );
                Assert.assertEquals( q.size( ), 2 );
                Assert.assertEquals( d2, (double)0.2 );
                d3 = q.offerPressure( mb3 );
                Assert.assertEquals( q.size( ), 3 );
                Assert.assertEquals( d3, (double)3/(double)10 );
        }


        @Test
        public void testOfferPressureEventThrown( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );

                MockQueueSelector_LinkedBlockingMessageQueue s = new MockQueueSelector_LinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                q.configure( s, handle );

                Assert.assertNull( s.getReadyEvent( ) );
                Assert.assertEquals( q.size( ), 0 );
                q.offerPressure( mb1 );
                Assert.assertEquals( q.size( ), 1 );
                Assert.assertNotNull( s.getReadyEvent( ) );
                s.clearReadyEvent( );
                Assert.assertNull( s.getReadyEvent( ) );
                q.offerPressure( mb2 );
                Assert.assertNotNull( s.getReadyEvent( ) );
        }


        @Test
        public void testOfferPressureEventSource( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );

                MockQueueSelector_LinkedBlockingMessageQueue s = new MockQueueSelector_LinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 2 );
                q.configure( s, handle );

                q.offerPressure( mb1 );
                q.offerPressure( mb2 );
                q.configure( s, handle );
                Assert.assertEquals( q.size( ), 2 );
                s.clearReadyEvent( );
                Assert.assertNull( s.getReadyEvent( ) );

                q.offerPressure( mb3 );
                Assert.assertNull( s.getReadyEvent( ) );
        }


        @Test
        public void testPoll( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                Assert.assertEquals( q.size( ), 0 );


                Assert.assertNull( q.poll( ) );

                q.offer( mb1 );
		MessageBlock mbA = q.poll( );
		Assert.assertEquals( q.size( ), 0 );
		Assert.assertSame( mbA, mb1 );


                q.offer( mb1 );
                q.offer( mb2 );
		MessageBlock mbB = q.poll( );
		MessageBlock mbC = q.poll( );
		Assert.assertEquals( q.size( ), 0 );
		Assert.assertSame( mbB, mb1 );
		Assert.assertSame( mbC, mb2 );
        }


        @Test
        public void testPeek( ) {
                MessageBlock mb1 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                Assert.assertEquals( q.size( ), 0 );


                Assert.assertNull( q.peek( ) );

                q.offer( mb1 );
                MessageBlock mbA = q.peek( );
                Assert.assertEquals( q.size( ), 1 );    
                Assert.assertSame( mbA, mb1 );
                MessageBlock mbB = q.poll( );
		Assert.assertSame( mbB, mb1 );
        }


        @Test
        public void testClear( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                Assert.assertEquals( q.size( ), 0 );

		q.clear( );
                Assert.assertEquals( q.size( ), 0 );

		q.offer( mb1 );
		q.offer( mb2 );
		q.clear( );
                Assert.assertEquals( q.size( ), 0 );
        }


        @Test
        public void testIsEmpty( ) {
                MessageBlock mb1 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );

                Assert.assertTrue( q.isEmpty( ) );

                q.offer( mb1 );
                Assert.assertFalse( q.isEmpty( ) );
        }


        @Test
        public void testHasData( ) {
                MessageBlock mb1 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );

                Assert.assertFalse( q.hasData( ) );

                q.offer( mb1 );
                Assert.assertTrue( q.hasData( ) );
        }


        @Test
        public void testSize( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );

                Assert.assertEquals( q.size( ), 0 );

                q.offer( mb1 );
                Assert.assertEquals( q.size( ), 1 );

                q.offer( mb2 );
                Assert.assertEquals( q.size( ), 2 );
        }


        @Test
        public void testRemainingCapacity( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );

                Assert.assertEquals( q.remainingCapacity( ), 10 );

                q.offer( mb1 );
                Assert.assertEquals( q.remainingCapacity( ), 9 );

                q.offer( mb2 );
                Assert.assertEquals( q.remainingCapacity( ), 8 );
        }


        @Test
        public void testPressure( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                MessageBlock mb5 = new MessageBlock( );
                MessageBlock mb6 = new MessageBlock( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 5 );

                Assert.assertEquals( q.pressure( ), (double)0.0 );

                q.offer( mb1 );
                Assert.assertEquals( q.pressure( ), (double)0.2 );

                q.offer( mb2 );
                Assert.assertEquals( q.pressure( ), (double)0.4 );

                q.offer( mb3 );
                Assert.assertEquals( q.pressure( ), (double)0.6 );

                q.offer( mb4 );
                Assert.assertEquals( q.pressure( ), (double)0.8 );

                q.offer( mb5 );
                Assert.assertEquals( q.pressure( ), (double)1.0 );
        }


        @Test
        public void testCheckEvent( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MessageBlock mb1 = new MessageBlock( );

                MockQueueSelector_LinkedBlockingMessageQueue s = new MockQueueSelector_LinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                q.configure( s, handle );

                Assert.assertNull( s.getReadyEvent( ) );
                Assert.assertEquals( q.size( ), 0 );

		q.checkEvent( );

                Assert.assertNull( s.getReadyEvent( ) );
                Assert.assertEquals( q.size( ), 0 );

		q.offer( mb1 );
		s.clearReadyEvent( );
		q.checkEvent( );
                Assert.assertNotNull( s.getReadyEvent( ) );
                Assert.assertEquals( q.size( ), 1 );

        }


        @Test
        public void testConfigure( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MessageBlock mb1 = new MessageBlock( );

                MockQueueSelector_LinkedBlockingMessageQueue s = new MockQueueSelector_LinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
                q.configure( s, handle );

                Field qsF = LinkedBlockingMessageQueue.class.getDeclaredField( "queueSelector" );
                qsF.setAccessible( true );
                MockQueueSelector_LinkedBlockingMessageQueue qs = (MockQueueSelector_LinkedBlockingMessageQueue)qsF.get( q );
		Assert.assertSame( qs, s );

                Field handleF = LinkedBlockingMessageQueue.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Handle handleR = (Handle)handleF.get( q );
		Assert.assertSame( handleR, handle );

		Assert.assertSame( qs, s );
	}
}
