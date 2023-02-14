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

import java.util.Date;
import java.util.Timer;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import com.kineticfire.patterns.reactor.TimerSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.Event;

import com.kineticfire.patterns.reactor.MockCompositeSelector;
import com.kineticfire.patterns.reactor.GenericHandler;

public class TimerSelector_Test {


        @Test
        public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                TimerSelector selector = new TimerSelector( );

                Field selectorF = TimerSelector.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector1 = (CompositeSelector)selectorF.get( selector );
                Assert.assertNull( selector1 );

                Field timerF = TimerSelector.class.getDeclaredField( "timer" );
                timerF.setAccessible( true );
                Timer timer = (Timer)timerF.get( selector );
                Assert.assertNotNull( timer );

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertEquals( 0, handleTaskMap.size( ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertEquals( 0, isRecurringEvent.size( ) );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );
                Assert.assertEquals( 0, expiredTimers.size( ) );

                Field pendingCancelF = TimerSelector.class.getDeclaredField( "pendingCancel" );
                pendingCancelF.setAccessible( true );
                Set<Handle> pendingCancel = (HashSet<Handle>)pendingCancelF.get( selector );
                Assert.assertEquals( 0, pendingCancel.size( ) );

                Field doneF = TimerSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( selector );
                Assert.assertFalse( done );

                Field guardF = TimerSelector.class.getDeclaredField( "guard" );
                guardF.setAccessible( true );
                Byte guard = (Byte)guardF.get( selector );
                Assert.assertNotNull( guard );
        }


        @Test
        public void testConstructorSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

                Field selectorF = TimerSelector.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector1 = (CompositeSelector)selectorF.get( selector );
                Assert.assertNotNull( selector1 );

                Field timerF = TimerSelector.class.getDeclaredField( "timer" );
                timerF.setAccessible( true );
                Timer timer = (Timer)timerF.get( selector );
                Assert.assertNotNull( timer );

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertEquals( 0, handleTaskMap.size( ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertEquals( 0, isRecurringEvent.size( ) );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );
                Assert.assertEquals( 0, expiredTimers.size( ) );

                Field pendingCancelF = TimerSelector.class.getDeclaredField( "pendingCancel" );
                pendingCancelF.setAccessible( true );
                Set<Handle> pendingCancel = (HashSet<Handle>)pendingCancelF.get( selector );
                Assert.assertEquals( 0, pendingCancel.size( ) );

                Field doneF = TimerSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( selector );
                Assert.assertFalse( done );

                Field guardF = TimerSelector.class.getDeclaredField( "guard" );
                guardF.setAccessible( true );
                Byte guard = (Byte)guardF.get( selector );
                Assert.assertNotNull( guard );
        }


	@Test
	public void testRegisterOnceDate( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

		Date firstTime = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
		GenericHandler handler = new GenericHandler( );
		Handle handle = selector.registerOnce( firstTime, handler, EventMask.TIMER );

		// assuming these will be evaluated before system time plus 10 seconds from creation of Date object

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertNotNull( handleTaskMap.get( handle ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertFalse( isRecurringEvent.get( handle ) );

		selector.shutdown( );
	}


	@Test
	public void testRegisterOnceDelay( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

		long delay = 1000;
		GenericHandler handler = new GenericHandler( );
		Handle handle = selector.registerOnce( delay, handler, EventMask.TIMER );

                // assuming these will be evaluated before system time plus 1 seconds from creation of Date object

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertNotNull( handleTaskMap.get( handle ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertFalse( isRecurringEvent.get( handle ) );

                selector.shutdown( );
	}


        @Test
        public void testRegisterFixedDelayDate( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

		Date firstTime = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period = 1000;
                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.registerFixedDelay( firstTime, period, handler, EventMask.TIMER );


                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertNotNull( handleTaskMap.get( handle ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertTrue( isRecurringEvent.get( handle ) );
                
                selector.shutdown( );
        }


        @Test
        public void testRegisterFixedDelayLong( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

                long delay = 1000;
                long period = 1000;
                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.registerFixedDelay( delay, period, handler, EventMask.TIMER );


                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertNotNull( handleTaskMap.get( handle ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertTrue( isRecurringEvent.get( handle ) );

                selector.shutdown( );
        }


        @Test
        public void testRegisterFixedRateDate( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

                Date firstTime = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period = 1000;
                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.registerFixedRate( firstTime, period, handler, EventMask.TIMER );


                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertNotNull( handleTaskMap.get( handle ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertTrue( isRecurringEvent.get( handle ) );

                selector.shutdown( );
        }


        @Test
        public void testRegisterFixedRateLong( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

                long delay = 1000;
                long period = 1000;
                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.registerFixedRate( delay, period, handler, EventMask.TIMER );


                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );
                Assert.assertNotNull( handleTaskMap.get( handle ) );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );
                Assert.assertTrue( isRecurringEvent.get( handle ) );

                selector.shutdown( );
        }



	@Test
	public void testIsRegistered( ) {
		MockCompositeSelector composite = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( composite );

		Handle handle1 = new Handle( );
		Assert.assertFalse( selector.isRegistered( handle1 ) );

                long delay = 10;
                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.registerOnce( delay, handler, EventMask.TIMER );

                Assert.assertTrue( selector.isRegistered( handle ) ); // assuming this will be evaluated before 10 seconds from register method

		selector.shutdown( );
	}


	@Test
	public void testInterestOps( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector cselector = new MockCompositeSelector( );
		TimerSelector selector = new TimerSelector( cselector );

                long delay = 1000;
                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.registerOnce( delay, handler, EventMask.TIMER );

		// assuming these will be evaluated before 1 second from register method

                // NOOP -> NOOP
                cselector.setInterestOps( EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );
                selector.interestOps( handle, EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );

                // NOOP -> TIMER
                cselector.setInterestOps( EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );
                selector.interestOps( handle, EventMask.TIMER );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.TIMER );

                // TIMER -> NOOP
                cselector.setInterestOps( EventMask.TIMER );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.TIMER );
                selector.interestOps( handle, EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );

                // TIMER -> TIMER 
                cselector.setInterestOps( EventMask.TIMER );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.TIMER );
                selector.interestOps( handle, EventMask.TIMER );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.TIMER );

                selector.shutdown( );

	}

        @Test
        public void testTimerExpired( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );


		cselector.clearReadyEvent( );
		Handle handle1 = new Handle( );
		selector.timerExpired( handle1 );
		Assert.assertFalse( cselector.hasReadyEvent( ) );

		cselector.clearReadyEvent( );
                long delay2 = 1000;
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerOnce( delay2, handler2, EventMask.TIMER );
		selector.cancel( handle2 );
		selector.timerExpired( handle2 );
		Assert.assertFalse( cselector.hasReadyEvent( ) );

		cselector.clearReadyEvent( );
                long delay3 = 1000;
                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = selector.registerOnce( delay3, handler3, EventMask.TIMER );
		selector.timerExpired( handle3 );
		Assert.assertTrue( cselector.hasReadyEvent( ) );
		int num3A = expiredTimers.get( handle3 );
		Assert.assertEquals( num3A, 1 );
		cselector.clearReadyEvent( );
		selector.timerExpired( handle3 );
		Assert.assertTrue( cselector.hasReadyEvent( ) );
		int num3B = expiredTimers.get( handle3 );
		Assert.assertEquals( num3B, 2 );

	}


	@Test
	public void testCancel( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );

                Field pendingCancelF = TimerSelector.class.getDeclaredField( "pendingCancel" );
                pendingCancelF.setAccessible( true );
                Set<Handle> pendingCancel = (HashSet<Handle>)pendingCancelF.get( selector );



		// not registered
		Handle handle1 = new Handle( );
		cselector.clearProcessDeregisterHandle( );
		Assert.assertFalse( selector.cancel( handle1 ) );
		Assert.assertNull( cselector.getProcessDeregisterHandle( ) );

		// canceled w/ no expired timers = deregistered
                long delay2 = 1000;
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerOnce( delay2, handler2, EventMask.TIMER );
		cselector.clearProcessDeregisterHandle( );
		Assert.assertTrue( selector.cancel( handle2 ) );
		Assert.assertNull( handleTaskMap.get( handle2 ) );
		Assert.assertNull( isRecurringEvent.get( handle2 ) );
		Assert.assertNull( expiredTimers.get( handle2 ) );
		Assert.assertFalse( pendingCancel.contains( handle2 ) );
		Assert.assertSame( handle2, cselector.getProcessDeregisterHandle( ) );

		// expired timers and put into pending = pending
                long delay3 = 1000;
                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = selector.registerOnce( delay3, handler3, EventMask.TIMER );
		cselector.clearProcessDeregisterHandle( );
		selector.timerExpired( handle3 );
		Assert.assertTrue( selector.cancel( handle3 ) );
		Assert.assertTrue( pendingCancel.contains( handle3 ) );
		Assert.assertTrue( selector.isRegistered( handle3 ) );
		Assert.assertNull( cselector.getProcessDeregisterHandle( ) );

		// canceled w/ no expired timers -- canceled multiple times = deregistered
                long delay4 = 1000;
                GenericHandler handler4 = new GenericHandler( );
                Handle handle4 = selector.registerOnce( delay4, handler4, EventMask.TIMER );
		cselector.clearProcessDeregisterHandle( );
		Assert.assertTrue( selector.cancel( handle4 ) );
		Assert.assertFalse( selector.cancel( handle4 ) );
		Assert.assertNull( handleTaskMap.get( handle4 ) );
		Assert.assertNull( isRecurringEvent.get( handle4 ) );
		Assert.assertNull( expiredTimers.get( handle4 ) );
		Assert.assertFalse( pendingCancel.contains( handle4 ) );
		Assert.assertSame( handle4, cselector.getProcessDeregisterHandle( ) );

                // expired timers and put into pending -- canceled multiple times = pending
                long delay5 = 1000;
                GenericHandler handler5 = new GenericHandler( );
                Handle handle5 = selector.registerOnce( delay5, handler5, EventMask.TIMER );
		cselector.clearProcessDeregisterHandle( );
                selector.timerExpired( handle5 );
                Assert.assertTrue( selector.cancel( handle5 ) );
                Assert.assertTrue( pendingCancel.contains( handle5 ) );
                Assert.assertFalse( selector.cancel( handle5 ) );
		Assert.assertTrue( selector.isRegistered( handle5 ) );
		Assert.assertNull( cselector.getProcessDeregisterHandle( ) );
	}


        @Test
        public void testDeregister( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );

                Field pendingCancelF = TimerSelector.class.getDeclaredField( "pendingCancel" );
                pendingCancelF.setAccessible( true );
                Set<Handle> pendingCancel = (HashSet<Handle>)pendingCancelF.get( selector );

                // not registered
                Handle handle1 = new Handle( );
                cselector.clearProcessDeregisterHandle( );
		selector.deregister( handle1 );
                Assert.assertNull( cselector.getProcessDeregisterHandle( ) );

                // registered
                long delay4 = 1000;
                GenericHandler handler4 = new GenericHandler( );
                Handle handle4 = selector.registerOnce( delay4, handler4, EventMask.TIMER );
                cselector.clearProcessDeregisterHandle( );
		selector.deregister( handle4 );
                Assert.assertNull( handleTaskMap.get( handle4 ) );
                Assert.assertNull( isRecurringEvent.get( handle4 ) );
                Assert.assertNull( expiredTimers.get( handle4 ) );
                Assert.assertFalse( pendingCancel.contains( handle4 ) );
                Assert.assertSame( handle4, cselector.getProcessDeregisterHandle( ) );
	}


        @Test
        public void testCheckin( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );

                long delay4 = 1000;
                GenericHandler handler4 = new GenericHandler( );
                Handle handle4 = selector.registerOnce( delay4, handler4, EventMask.TIMER );
                selector.timerExpired( handle4 );
		int act1 = expiredTimers.get( handle4 );
		Assert.assertEquals( act1, 1 );
		selector.checkin( handle4, new Event( ) );
		int act2 = expiredTimers.get( handle4 );
		Assert.assertEquals( act2, 0 );
        }


        @Test
        public void testResumeSelection( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

                Field handleTaskMapF = TimerSelector.class.getDeclaredField( "handleTaskMap" );
                handleTaskMapF.setAccessible( true );
                Map<Handle,StandardTimebaseTask> handleTaskMap = (HashMap<Handle,StandardTimebaseTask>)handleTaskMapF.get( selector );

                Field isRecurringEventF = TimerSelector.class.getDeclaredField( "isRecurringEvent" );
                isRecurringEventF.setAccessible( true );
                Map<Handle,Boolean> isRecurringEvent = (HashMap<Handle,Boolean>)isRecurringEventF.get( selector );

                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( selector );

                Field pendingCancelF = TimerSelector.class.getDeclaredField( "pendingCancel" );
                pendingCancelF.setAccessible( true );
                Set<Handle> pendingCancel = (HashSet<Handle>)pendingCancelF.get( selector );


                // not a recurring event = deregistered
                long delay2 = 1000;
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerOnce( delay2, handler2, EventMask.TIMER );
                cselector.clearProcessDeregisterHandle( );
		selector.resumeSelection( handle2 );
                Assert.assertNull( handleTaskMap.get( handle2 ) );
                Assert.assertNull( isRecurringEvent.get( handle2 ) );
                Assert.assertNull( expiredTimers.get( handle2 ) );
                Assert.assertFalse( pendingCancel.contains( handle2 ) );
                Assert.assertSame( handle2, cselector.getProcessDeregisterHandle( ) );

                // recurring and not pending = registered
                long delay3 = 1000;
                long period3 = 1000;
                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = selector.registerFixedDelay( delay3, period3, handler3, EventMask.TIMER );
                cselector.clearProcessDeregisterHandle( );
		selector.timerExpired( handle3 );
		selector.resumeSelection( handle3 );
		int act2 = expiredTimers.get( handle3 );
		Assert.assertEquals( act2, 0 );
                Assert.assertTrue( selector.isRegistered( handle3 ) );
                Assert.assertNull( cselector.getProcessDeregisterHandle( ) );

                // recurring and pending but expired timers > 0 = registered
                long delay4 = 1000;
                long period4 = 1000;
                GenericHandler handler4 = new GenericHandler( );
                Handle handle4 = selector.registerFixedDelay( delay4, period4, handler4, EventMask.TIMER );
                cselector.clearProcessDeregisterHandle( );
                selector.timerExpired( handle4 );
                selector.timerExpired( handle4 );
		selector.cancel( handle4 );
                selector.resumeSelection( handle4 );
                int act4 = expiredTimers.get( handle4 );
                Assert.assertEquals( act4, 1 );
                Assert.assertTrue( selector.isRegistered( handle4 ) );
                Assert.assertTrue( pendingCancel.contains( handle4 ) );
                Assert.assertNull( cselector.getProcessDeregisterHandle( ) );

                // recurring and pending but expired timers = 0 = deregistered
                long delay5 = 1000;
                long period5 = 1000;
                GenericHandler handler5 = new GenericHandler( );
                Handle handle5 = selector.registerFixedDelay( delay5, period5, handler5, EventMask.TIMER );
                cselector.clearProcessDeregisterHandle( );
                selector.timerExpired( handle5 );
                selector.timerExpired( handle5 );
		selector.cancel( handle5 );
                selector.resumeSelection( handle5 );
                selector.resumeSelection( handle5 );
                Assert.assertNull( handleTaskMap.get( handle5 ) );
                Assert.assertNull( isRecurringEvent.get( handle5 ) );
                Assert.assertNull( expiredTimers.get( handle5 ) );
                Assert.assertFalse( pendingCancel.contains( handle5 ) );
                Assert.assertSame( handle5, cselector.getProcessDeregisterHandle( ) );
	}


        @Test
        public void testShutdown( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

		selector.shutdown( );

                Field timerF = TimerSelector.class.getDeclaredField( "timer" );
                timerF.setAccessible( true );
                Timer timer = (Timer)timerF.get( selector );
		Assert.assertNotNull( timer );

                Field doneF = TimerSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( selector );
                Assert.assertTrue( done );
	}


        @Test
        public void testFinalize( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector cselector = new MockCompositeSelector( );
                TimerSelector selector = new TimerSelector( cselector );

                selector.finalize( );

                Field timerF = TimerSelector.class.getDeclaredField( "timer" );
                timerF.setAccessible( true );
                Timer timer = (Timer)timerF.get( selector );
                Assert.assertNotNull( timer );

                Field doneF = TimerSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                boolean done = (Boolean)doneF.get( selector );
                Assert.assertTrue( done );
        }
}
