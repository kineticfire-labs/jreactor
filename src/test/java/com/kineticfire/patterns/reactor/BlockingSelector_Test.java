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
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;

import com.kineticfire.patterns.reactor.BlockingSelector;
import com.kineticfire.patterns.reactor.BlockingTaskSet;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventGenerationState;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;

import com.kineticfire.patterns.reactor.MockCompositeSelector_BlockingSelector;
import com.kineticfire.patterns.reactor.Handler_BlockingSelector;



public class BlockingSelector_Test {


        @Test
        public void testConstructorNoArg( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                BlockingSelector blockingSelector = new BlockingSelector( );

                Field selectorF = BlockingSelector.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                Assert.assertNull( selectorF.get( blockingSelector ) );

                Field workerThreadNumF = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                int workerThreadNum = (Integer)workerThreadNumF.get( blockingSelector );
                Assert.assertEquals( workerThreadNum, 0 );

                Field workerThreadPoolF = BlockingSelector.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                Assert.assertNull( workerThreadPoolF.get( blockingSelector ) );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field doneF = BlockingSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( blockingSelector );
                Assert.assertFalse( done );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                int terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst, 3 );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                int terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast, 3 );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );
        }



        @Test
        public void testConstructorSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field selectorF = BlockingSelector.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                Assert.assertSame( selectorF.get( blockingSelector ), selector );

                Field workerThreadNumF = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                int workerThreadNum = (Integer)workerThreadNumF.get( blockingSelector );
                Assert.assertEquals( workerThreadNum, 1 );

                Field workerThreadPoolF = BlockingSelector.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                Assert.assertNotNull( workerThreadPoolF.get( blockingSelector ) );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field doneF = BlockingSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( blockingSelector );
                Assert.assertFalse( done );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                int terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst, 3 );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                int terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast, 3 );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );
        }


        @Test
        public void testConstructorSelectorInt( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector, 3 );

                Field selectorF = BlockingSelector.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                Assert.assertSame( selectorF.get( blockingSelector ), selector );

                Field workerThreadNumF = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                int workerThreadNum = (Integer)workerThreadNumF.get( blockingSelector );
                Assert.assertEquals( workerThreadNum, 3 );

                Field workerThreadPoolF = BlockingSelector.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                Assert.assertNotNull( workerThreadPoolF.get( blockingSelector ) );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field doneF = BlockingSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( blockingSelector );
                Assert.assertFalse( done );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                int terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst, 3 );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                int terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast, 3 );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );
        }



	@Test
	public void testRegister( ) {
		MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

		Assert.assertEquals( selector.getInterestOps( ), EventMask.NOOP );
		Runnable_BlockingSelector runnable = new Runnable_BlockingSelector( );
		Handler_BlockingSelector handler = new Handler_BlockingSelector( );
		Handle handle = blockingSelector.register( runnable, handler, EventMask.BLOCKING );
		Assert.assertNotNull( handle );
		Assert.assertTrue( blockingSelector.isRegistered( handle ) );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.BLOCKING );
	}


        @Test
        public void testRegisterGroup( ) {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Assert.assertEquals( selector.getInterestOps( ), EventMask.NOOP );
                Runnable_BlockingSelector runnable1 = new Runnable_BlockingSelector( );
                Runnable_BlockingSelector runnable2 = new Runnable_BlockingSelector( );
                Runnable_BlockingSelector runnable3 = new Runnable_BlockingSelector( );
		Set<Runnable> runnableSet = new HashSet<Runnable>( );
		runnableSet.add( runnable1 );
		runnableSet.add( runnable2 );
		runnableSet.add( runnable3 );
                Handler_BlockingSelector handler = new Handler_BlockingSelector( );
                Handle handle = blockingSelector.registerGroup( runnableSet, handler, EventMask.BLOCKING );
                Assert.assertNotNull( handle );
                Assert.assertTrue( blockingSelector.isRegistered( handle ) );
                Assert.assertEquals( selector.getInterestOps( ), EventMask.BLOCKING );
        }


        @Test
        public void testIsRegisteredHandle( ) {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Assert.assertFalse( blockingSelector.isRegistered( new Handle( ) ) );
		Runnable_BlockingSelector runnable = new Runnable_BlockingSelector( );
		Handler_BlockingSelector handler = new Handler_BlockingSelector( );
		Handle handle = blockingSelector.register( runnable, handler, EventMask.BLOCKING );
                Assert.assertTrue( blockingSelector.isRegistered( handle ) );
        }



        @Test
        public void testInterestOps( ) {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

		Runnable_BlockingSelector runnable = new Runnable_BlockingSelector( );
		Handler_BlockingSelector handler = new Handler_BlockingSelector( );
		Handle handle = blockingSelector.register( runnable, handler, EventMask.BLOCKING );

		// NOOP -> NOOP
		selector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.NOOP );
		blockingSelector.interestOps( handle, EventMask.NOOP );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.NOOP );

		// NOOP -> BLOCKING
		selector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.NOOP );
		blockingSelector.interestOps( handle, EventMask.BLOCKING );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.BLOCKING );

		// BLOCKING -> NOOP
		selector.setInterestOps( EventMask.BLOCKING );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.BLOCKING );
		blockingSelector.interestOps( handle, EventMask.NOOP );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.NOOP );

		// BLOCKING -> BLOCKING 
		selector.setInterestOps( EventMask.BLOCKING );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.BLOCKING );
		blockingSelector.interestOps( handle, EventMask.BLOCKING );
		Assert.assertEquals( selector.getInterestOps( ), EventMask.BLOCKING );
        }


        @Test
        public void testCheckin( ) throws NoSuchFieldException, IllegalAccessException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

		Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );
                Assert.assertEquals( handleState.size( ), 0 );


                blockingSelector.checkin( new Handle( ), new Event( ) );
                Assert.assertEquals( handleState.size( ), 0 );




		Runnable_BlockingSelector runnable = new Runnable_BlockingSelector( );
		Handler_BlockingSelector handler = new Handler_BlockingSelector( );
		Handle handle = blockingSelector.register( runnable, handler, EventMask.BLOCKING );

                Event event = new Event( );
                blockingSelector.checkin( handle, new Event( ) );
                Assert.assertEquals( handleState.size( ), 1 );
                Assert.assertTrue( handleState.containsKey( handle ) );
                Assert.assertEquals( handleState.get( handle ), EventGenerationState.HOLDING );

        }


        @Test
        public void testResumeSelectionFired( ) throws NoSuchFieldException, IllegalAccessException {
		// EventGenerationState = FIRED

                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );

		Handle handle = new Handle( );
		handleState.put( handle, EventGenerationState.FIRED );

		BlockingTaskSet blockingSet = new BlockingTaskSet( );	
		registered.put( handle, blockingSet );

                selector.setInterestOps( EventMask.BLOCKING );


                Assert.assertTrue( handleState.containsKey( handle ) );
                Assert.assertTrue( registered.containsKey( handle ) );
		Assert.assertNull( selector.getDeregister( ) );

                blockingSelector.resumeSelection( handle );

                Assert.assertFalse( handleState.containsKey( handle ) );
                Assert.assertFalse( registered.containsKey( handle ) );
		Assert.assertNotNull( selector.getDeregister( ) );
	}


        @Test
        public void testResumeSelectionHoldingBlocking( ) throws NoSuchFieldException, IllegalAccessException {
		// EventGenerationState = HOLDING and interestOps = BLOCKING

                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );

		Handle handle = new Handle( );
		handleState.put( handle, EventGenerationState.HOLDING );

		BlockingTaskSet blockingSet = new BlockingTaskSet( );	
		registered.put( handle, blockingSet );

                selector.setInterestOps( EventMask.BLOCKING );


                Assert.assertTrue( handleState.containsKey( handle ) );
		Assert.assertEquals( handleState.get( handle ), EventGenerationState.HOLDING );
                Assert.assertTrue( registered.containsKey( handle ) );
		Assert.assertNull( selector.getDeregister( ) );
		Assert.assertNull( selector.getReadyEvent( ) );

                blockingSelector.resumeSelection( handle );

                Assert.assertTrue( handleState.containsKey( handle ) );
		Assert.assertEquals( handleState.get( handle ), EventGenerationState.FIRED );
                Assert.assertTrue( registered.containsKey( handle ) );
		Assert.assertNull( selector.getDeregister( ) );
		Assert.assertNotNull( selector.getReadyEvent( ) );
	}


        @Test
        public void testResumeSelectionHoldingNoop( ) throws NoSuchFieldException, IllegalAccessException {
		// EventGenerationState = HOLDING and interestOps = NOOP

                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );

		Handle handle = new Handle( );
		handleState.put( handle, EventGenerationState.HOLDING );

		BlockingTaskSet blockingSet = new BlockingTaskSet( );	
		registered.put( handle, blockingSet );

                selector.setInterestOps( EventMask.NOOP );


                Assert.assertTrue( handleState.containsKey( handle ) );
		Assert.assertEquals( handleState.get( handle ), EventGenerationState.HOLDING );
                Assert.assertTrue( registered.containsKey( handle ) );
		Assert.assertNull( selector.getDeregister( ) );
		Assert.assertNull( selector.getReadyEvent( ) );

                blockingSelector.resumeSelection( handle );

                Assert.assertTrue( handleState.containsKey( handle ) );
		Assert.assertEquals( handleState.get( handle ), EventGenerationState.HOLDING );
                Assert.assertTrue( registered.containsKey( handle ) );
		Assert.assertNull( selector.getDeregister( ) );
		Assert.assertNull( selector.getReadyEvent( ) );
	}


        @Test
        public void testTriggerReadyEvent( ) throws NoSuchFieldException, IllegalAccessException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );
                Assert.assertEquals( handleState.size( ), 0 );


		// handle not registered
                selector.clearReadyEvent( );
		Assert.assertNull( selector.getReadyEvent( ) );
		Handle handle1 = new Handle( );
                blockingSelector.triggerReadyEvent( handle1 );
                Assert.assertFalse( handleState.containsKey( handle1 ) );
		Assert.assertNull( selector.getReadyEvent( ) );




		// handle registered
                Runnable_BlockingSelector runnable = new Runnable_BlockingSelector( );
                Handler_BlockingSelector handler = new Handler_BlockingSelector( );
                Handle handle2 = blockingSelector.register( runnable, handler, EventMask.BLOCKING );

                selector.clearReadyEvent( );
		Assert.assertNull( selector.getReadyEvent( ) );
                blockingSelector.triggerReadyEvent( handle2 );
                Assert.assertEquals( handleState.get( handle2 ), EventGenerationState.FIRED );
		Assert.assertNotNull( selector.getReadyEvent( ) );

        }


        @Test
        public void testDeregister( ) throws NoSuchFieldException, IllegalAccessException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field handleStateF = BlockingSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle, EventGenerationState> handleState = (Map<Handle, EventGenerationState>)handleStateF.get( blockingSelector );

                Field registeredF = BlockingSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle, BlockingTaskSet> registered = (Map<Handle, BlockingTaskSet>)registeredF.get( blockingSelector );


		// not registered
		Handle handle1 = new Handle( );
		selector.clearDeregister( );
		Assert.assertNull( selector.getDeregister( ) );
		blockingSelector.deregister( handle1 );
		Assert.assertNull( handleState.get( handle1 ) );
		Assert.assertNull( registered.get( handle1 ) );



		// registered
                Runnable_BlockingSelector runnable = new Runnable_BlockingSelector( );
                Handler_BlockingSelector handler = new Handler_BlockingSelector( );
                Handle handle2 = blockingSelector.register( runnable, handler, EventMask.BLOCKING );
		selector.clearDeregister( );
		Assert.assertNull( selector.getDeregister( ) );
		Assert.assertNotNull( handleState.get( handle2 ) );
		Assert.assertNotNull( registered.get( handle2 ) );
		blockingSelector.deregister( handle2 );
		Assert.assertNull( handleState.get( handle2 ) );
		Assert.assertNull( registered.get( handle2 ) );
		Assert.assertNotNull( selector.getDeregister( ) );
	}



        @Test
        public void testConfigureBlockingThreadPoolInt( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field workerThreadNumF = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                int workerThreadNum = (Integer)workerThreadNumF.get( blockingSelector );
                Assert.assertEquals( workerThreadNum, 1 );

		blockingSelector.configureBlockingThreadPool( 3 );

                Field workerThreadNumF2 = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF2.setAccessible( true );
                int workerThreadNum2 = (Integer)workerThreadNumF2.get( blockingSelector );
                Assert.assertEquals( workerThreadNum2, 3 );
	}


        @Test
        public void testConfigureBlockingThreadPool1Int( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                int terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst, 3 );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                int terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast, 3 );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );


		blockingSelector.configureBlockingThreadPool( 5, TimeUnit.MICROSECONDS, 5, TimeUnit.MICROSECONDS );

                Field terminationTimeoutFirstF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF2.setAccessible( true );
                int terminationTimeoutFirst2 = (Integer)terminationTimeoutFirstF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst2, 5 );

                Field terminationTimeoutUnitsFirstF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF2.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst2 = (TimeUnit)terminationTimeoutUnitsFirstF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst2, TimeUnit.MICROSECONDS );

                Field terminationTimeoutLastF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF2.setAccessible( true );
                int terminationTimeoutLast2 = (Integer)terminationTimeoutLastF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast2, 5 );

                Field terminationTimeoutUnitsLastF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF2.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast2 = (TimeUnit)terminationTimeoutUnitsLastF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast2, TimeUnit.MICROSECONDS );
	}


        @Test
        public void testConfigureBlockingThreadPool2Int( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                Field workerThreadNumF = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                int workerThreadNum = (Integer)workerThreadNumF.get( blockingSelector );
                Assert.assertEquals( workerThreadNum, 1 );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                int terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst, 3 );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                int terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast, 3 );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );


                blockingSelector.configureBlockingThreadPool( 3, 5, TimeUnit.MICROSECONDS, 5, TimeUnit.MICROSECONDS );

                Field workerThreadNumF2 = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF2.setAccessible( true );
                int workerThreadNum2 = (Integer)workerThreadNumF2.get( blockingSelector );
                Assert.assertEquals( workerThreadNum2, 3 );

                Field terminationTimeoutFirstF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF2.setAccessible( true );
                int terminationTimeoutFirst2 = (Integer)terminationTimeoutFirstF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutFirst2, 5 );

                Field terminationTimeoutUnitsFirstF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF2.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst2 = (TimeUnit)terminationTimeoutUnitsFirstF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsFirst2, TimeUnit.MICROSECONDS );

                Field terminationTimeoutLastF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF2.setAccessible( true );
                int terminationTimeoutLast2 = (Integer)terminationTimeoutLastF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutLast2, 5 );

                Field terminationTimeoutUnitsLastF2 = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF2.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast2 = (TimeUnit)terminationTimeoutUnitsLastF2.get( blockingSelector );
                Assert.assertEquals( terminationTimeoutUnitsLast2, TimeUnit.MICROSECONDS );
        }


	@Test
	public void testShutdownInit( ) throws NoSuchFieldException, IllegalAccessException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

		blockingSelector.shutdownInit( );

                Field workerThreadPoolF = BlockingSelector.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
		ExecutorService workerThreadPool = (ExecutorService)workerThreadPoolF.get( blockingSelector );
		Assert.assertTrue( workerThreadPool.isShutdown( ) );
	}


        @Test
        public void testShutdownFinal( ) throws NoSuchFieldException, IllegalAccessException {
                MockCompositeSelector_BlockingSelector selector = new MockCompositeSelector_BlockingSelector( );
                BlockingSelector blockingSelector = new BlockingSelector( selector );

                blockingSelector.shutdownInit( );
                blockingSelector.shutdownFinal( );

                Field workerThreadPoolF = BlockingSelector.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                ExecutorService workerThreadPool = (ExecutorService)workerThreadPoolF.get( blockingSelector );
                Assert.assertTrue( workerThreadPool.isShutdown( ) );
                Assert.assertTrue( workerThreadPool.isTerminated( ) );
        }

}
