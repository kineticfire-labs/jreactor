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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.LinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.PriorityLinkedBlockingMessageQueue;

import com.kineticfire.patterns.reactor.MockCompositeSelector_QueueSelector;
import com.kineticfire.patterns.reactor.GenericHandler_QueueSelector;

public class QueueSelector_Test {


        @Test
        public void testConstructorNoArg( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                QueueSelector qselector = new QueueSelector( );

                Field cselectorF = QueueSelector.class.getDeclaredField( "selector" );
                cselectorF.setAccessible( true );
                Assert.assertNull( cselectorF.get( qselector ) );

                Field handleQueueMapF = QueueSelector.class.getDeclaredField( "handleQueueMap" );
                handleQueueMapF.setAccessible( true );
                Map<Handle, MessageQueue> result = (Map<Handle, MessageQueue>)handleQueueMapF.get( qselector );
                Assert.assertEquals( result.size( ), 0 );
        }


	@Test
	public void testConstructorComposite( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );

                Field cselectorF = QueueSelector.class.getDeclaredField( "selector" );
                cselectorF.setAccessible( true );
                Assert.assertSame( cselectorF.get( qselector ), cselector );

                Field handleQueueMapF = QueueSelector.class.getDeclaredField( "handleQueueMap" );
                handleQueueMapF.setAccessible( true );
                Map<Handle, MessageQueue> result = (Map<Handle, MessageQueue>)handleQueueMapF.get( qselector );
                Assert.assertEquals( result.size( ), 0 );
	}



	@Test
	public void testRegister( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );

		LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
		GenericHandler_QueueSelector handler = new GenericHandler_QueueSelector( );
		Handle handle = qselector.register( q, handler, 0 );

		Assert.assertNotNull( handle );
		Assert.assertTrue( qselector.isRegistered( handle ) );
		Assert.assertTrue( qselector.isRegistered( q ) );
	}


        @Test
        public void testIsRegisteredQueue( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );

		LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
		GenericHandler_QueueSelector handler = new GenericHandler_QueueSelector( );

                Assert.assertFalse( qselector.isRegistered( q ) );
                Handle handle = qselector.register( q, handler, 0 );
                Assert.assertTrue( qselector.isRegistered( q ) );
        }


        @Test
        public void testIsRegisteredHandle( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );


		LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
		GenericHandler_QueueSelector handler = new GenericHandler_QueueSelector( );

                Assert.assertFalse( qselector.isRegistered( new Handle( ) ) );
                Handle handle = qselector.register( q, handler, 0 );
                Assert.assertTrue( qselector.isRegistered( handle ) );
        }



        @Test
        public void testInterestOps( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );

		LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
		GenericHandler_QueueSelector handler = new GenericHandler_QueueSelector( );
                Handle handle = qselector.register( q, handler, 0 );

		// NOOP -> NOOP
		cselector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );
		qselector.interestOps( handle, EventMask.NOOP );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );

		// NOOP -> QREAD
		cselector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );
		qselector.interestOps( handle, EventMask.QREAD );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.QREAD );

		// QREAD -> NOOP
		cselector.setInterestOps( EventMask.QREAD );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.QREAD );
		qselector.interestOps( handle, EventMask.NOOP );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );

		// QREAD -> QREAD 
		cselector.setInterestOps( EventMask.QREAD );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.QREAD );
		qselector.interestOps( handle, EventMask.QREAD );
		Assert.assertEquals( cselector.getInterestOps( ), EventMask.QREAD );
        }


        @Test
        public void testDeregister( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );

		qselector.deregister( new Handle( ) );

		LinkedBlockingMessageQueue q = new LinkedBlockingMessageQueue( 10 );
		GenericHandler_QueueSelector handler = new GenericHandler_QueueSelector( );
                Handle handle = qselector.register( q, handler, 0 );
                Assert.assertTrue( qselector.isRegistered( handle ) );
		qselector.deregister( handle );
                Assert.assertFalse( qselector.isRegistered( handle ) );
        }


        @Test
        public void testCheckin( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );

		Handle handle = new Handle( );
		Event event = new Event( );

		qselector.checkin( handle, event );
	}


        @Test
        public void testResumeSelection( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );
		LinkedBlockingMessageQueue q1 = new LinkedBlockingMessageQueue( 10 );
		LinkedBlockingMessageQueue q2 = new LinkedBlockingMessageQueue( 10 );
		GenericHandler_QueueSelector handler1 = new GenericHandler_QueueSelector( );
		GenericHandler_QueueSelector handler2 = new GenericHandler_QueueSelector( );
                Handle handle1 = qselector.register( q1, handler1, EventMask.NOOP );
                Handle handle2 = qselector.register( q2, handler2, EventMask.QREAD );

		// ops = NOOP; no data
		cselector.setInterestOps( EventMask.NOOP );
		Assert.assertNull( cselector.getReadyEvent( ) );
		qselector.resumeSelection( handle1 );
		Assert.assertNull( cselector.getReadyEvent( ) );

		// ops = NOOP; data
		q1.offer( new MessageBlock( ) );
		cselector.clearReadyEvent( );
		cselector.setInterestOps( EventMask.NOOP );
		Assert.assertNull( cselector.getReadyEvent( ) );
		qselector.resumeSelection( handle1 );
		Assert.assertNull( cselector.getReadyEvent( ) );

		// ops = QREAD; no data 
		cselector.setInterestOps( EventMask.QREAD );
		Assert.assertNull( cselector.getReadyEvent( ) );
		qselector.resumeSelection( handle2 );
		Assert.assertNull( cselector.getReadyEvent( ) );

		// ops = QREAD; data 
		q2.offer( new MessageBlock( ) );
		cselector.clearReadyEvent( );
		cselector.setInterestOps( EventMask.QREAD );
		Assert.assertNull( cselector.getReadyEvent( ) );
		qselector.resumeSelection( handle2 );
		Assert.assertNotNull( cselector.getReadyEvent( ) );
	}


        @Test
        public void testAddReadyEvent( ) {
		MockCompositeSelector_QueueSelector cselector = new MockCompositeSelector_QueueSelector( );
		QueueSelector qselector = new QueueSelector( cselector );
	
		Event event = new Event( );	
		cselector.clearReadyEvent( );
		Assert.assertNull( cselector.getReadyEvent( ) );
		qselector.addReadyEvent( event );
		Assert.assertSame( cselector.getReadyEvent( ), event );
	}
}
