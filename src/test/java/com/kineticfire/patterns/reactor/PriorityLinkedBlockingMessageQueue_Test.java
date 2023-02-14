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

import com.kineticfire.patterns.reactor.PriorityLinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.Event;


/**
 * Not tested:
 *      - triggerEvent( )
 *           - but is tested by other methods
 *
 */


public class PriorityLinkedBlockingMessageQueue_Test {

	@Test
	public void testConstructorNoArg( ) {
		PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( );
		Assert.assertTrue( q.isEmpty( ) );
		Assert.assertEquals( q.remainingCapacity( ), 11 );
		Assert.assertEquals( q.size( ), 0 );
	}


        @Test
        public void testConstructorCapacity( ) {
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 20 );
                Assert.assertTrue( q.isEmpty( ) );
		Assert.assertEquals( q.remainingCapacity( ), 20 );
		Assert.assertEquals( q.size( ), 0 );
        }


        @Test
        public void testOffer( ) {
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( );
		MessageBlock mb3 = new MessageBlock( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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

                MockQueueSelector_PriorityLinkedBlockingMessageQueue s = new MockQueueSelector_PriorityLinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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

                MockQueueSelector_PriorityLinkedBlockingMessageQueue s = new MockQueueSelector_PriorityLinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 2 );
                q.configure( s, handle );

                q.offer( mb1 );
                q.offer( mb2 );
                q.configure( s, handle );
                Assert.assertEquals( q.size( ), 2 );
        }


        @Test
        public void testOfferPressure( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
		Double d1;
		Double d2;
		Double d3;
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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

                MockQueueSelector_PriorityLinkedBlockingMessageQueue s = new MockQueueSelector_PriorityLinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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

                MockQueueSelector_PriorityLinkedBlockingMessageQueue s = new MockQueueSelector_PriorityLinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 2 );
                q.configure( s, handle );

                q.offerPressure( mb1 );
                q.offerPressure( mb2 );
                q.configure( s, handle );
                Assert.assertEquals( q.size( ), 2 );
        }


        @Test
        public void testPollOneElement( ) {
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
                Assert.assertEquals( q.size( ), 0 );

                Assert.assertNull( q.poll( ) );

                q.offer( mb1 );
		MessageBlock mbA = q.poll( );
		Assert.assertEquals( q.size( ), 0 );
		Assert.assertSame( mbA, mb1 );
	}


        @Test
        public void testPollTwoEqualElements( ) {
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                MessageBlock mb2 = new MessageBlock( new Integer( 1 ), 300 );
                MessageBlock mb3 = new MessageBlock( new Integer( 1 ), 300 );
                MessageBlock mb4 = new MessageBlock( new Integer( 1 ), 5000 );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

                q.offer( mb4 );
                q.offer( mb3 );
                q.offer( mb1 );
                q.offer( mb2 );
		MessageBlock mbB = q.poll( );
		MessageBlock mbC = q.poll( );
		MessageBlock mbD = q.poll( );
		MessageBlock mbE = q.poll( );
		Assert.assertEquals( q.size( ), 0 );
		Assert.assertSame( mbB, mb1 );
		Assert.assertSame( mbC, mb2 );
		Assert.assertSame( mbD, mb3 );
		Assert.assertSame( mbE, mb4 );
        }


        @Test
        public void testPollInOrderElements( ) {
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                MessageBlock mb2 = new MessageBlock( new Integer( 2 ), 200 );
                MessageBlock mb3 = new MessageBlock( new Integer( 3 ), 5000 );
                MessageBlock mb4 = new MessageBlock( new Integer( 4 ) );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

                q.offer( mb1 );
                q.offer( mb2 );
                q.offer( mb3 );
                q.offer( mb4 );
		MessageBlock mbB = q.poll( );
		MessageBlock mbC = q.poll( );
		MessageBlock mbD = q.poll( );
		MessageBlock mbE = q.poll( );
		Assert.assertEquals( q.size( ), 0 );
		Assert.assertSame( mbB, mb1 );
		Assert.assertSame( mbC, mb2 );
		Assert.assertSame( mbD, mb3 );
		Assert.assertSame( mbE, mb4 );
        }


        @Test
        public void testPollReverseOrderElements( ) {
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                MessageBlock mb2 = new MessageBlock( new Integer( 2 ), 200 );
                MessageBlock mb3 = new MessageBlock( new Integer( 3 ), 5000 );
                MessageBlock mb4 = new MessageBlock( new Integer( 4 ) );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

                q.offer( mb4 );
                q.offer( mb3 );
                q.offer( mb2 );
                q.offer( mb1 );
                MessageBlock mbB = q.poll( );
                MessageBlock mbC = q.poll( );
                MessageBlock mbD = q.poll( );
                MessageBlock mbE = q.poll( );
                Assert.assertEquals( q.size( ), 0 );
                Assert.assertSame( mbB, mb1 );
                Assert.assertSame( mbC, mb2 );
                Assert.assertSame( mbD, mb3 );
                Assert.assertSame( mbE, mb4 );
        }


        @Test
        public void testPollAltOrderElements( ) {
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                MessageBlock mb2 = new MessageBlock( new Integer( 2 ), 200 );
                MessageBlock mb3 = new MessageBlock( new Integer( 3 ), 5000 );
                MessageBlock mb4 = new MessageBlock( new Integer( 4 ) );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

                q.offer( mb3 );
                q.offer( mb4 );
                q.offer( mb1 );
                q.offer( mb2 );
                MessageBlock mbB = q.poll( );
                MessageBlock mbC = q.poll( );
                MessageBlock mbD = q.poll( );
                MessageBlock mbE = q.poll( );
                Assert.assertEquals( q.size( ), 0 );
                Assert.assertSame( mbB, mb1 );
                Assert.assertSame( mbC, mb2 );
                Assert.assertSame( mbD, mb3 );
                Assert.assertSame( mbE, mb4 );
        }


        @Test
        public void testPeek( ) {
                MessageBlock mb1 = new MessageBlock( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

                Assert.assertTrue( q.isEmpty( ) );

                q.offer( mb1 );
                Assert.assertFalse( q.isEmpty( ) );
        }


        @Test
        public void testHasData( ) {
                MessageBlock mb1 = new MessageBlock( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

                Assert.assertFalse( q.hasData( ) );

                q.offer( mb1 );
                Assert.assertTrue( q.hasData( ) );
        }


        @Test
        public void testSize( ) {
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

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
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );

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

                q.offer( mb6 );
                Assert.assertEquals( q.pressure( ), (double)1.0 );
        }

        @Test
        public void testCheckEvent( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MessageBlock mb1 = new MessageBlock( );

                MockQueueSelector_PriorityLinkedBlockingMessageQueue s = new MockQueueSelector_PriorityLinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
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

                MockQueueSelector_PriorityLinkedBlockingMessageQueue s = new MockQueueSelector_PriorityLinkedBlockingMessageQueue( );
                Handle handle = new Handle( );
                PriorityLinkedBlockingMessageQueue q = new PriorityLinkedBlockingMessageQueue( 10 );
                q.configure( s, handle );

                Field qsF = PriorityLinkedBlockingMessageQueue.class.getDeclaredField( "queueSelector" );
                qsF.setAccessible( true );
                MockQueueSelector_PriorityLinkedBlockingMessageQueue qs = (MockQueueSelector_PriorityLinkedBlockingMessageQueue)qsF.get( q );
                Assert.assertSame( qs, s );

                Field handleF = PriorityLinkedBlockingMessageQueue.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Handle handleR = (Handle)handleF.get( q );
                Assert.assertSame( handleR, handle );

                Assert.assertSame( qs, s );
        }


}
