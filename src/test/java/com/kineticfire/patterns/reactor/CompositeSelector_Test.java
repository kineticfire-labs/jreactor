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
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

import com.kineticfire.patterns.reactor.GenericHandler;
import com.kineticfire.patterns.reactor.GenericRunnable;
import com.kineticfire.patterns.reactor.JReactor_CompositeSelector;
import com.kineticfire.patterns.reactor.CompositeSelector;
import com.kineticfire.patterns.reactor.LinkedBlockingMessageQueue;
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.LockHandlerGroup;

// Core Selector
import com.kineticfire.patterns.reactor.RunLockHandlerTracker;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import com.kineticfire.patterns.reactor.Registrar;
import com.kineticfire.patterns.reactor.EventStore;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

// Specific Selectors
import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.ChannelSelector;
import com.kineticfire.patterns.reactor.LockSelector;
import com.kineticfire.patterns.reactor.TimerSelector;
import com.kineticfire.patterns.reactor.SignalSelector;
import com.kineticfire.patterns.reactor.BlockingSelector;
import com.kineticfire.patterns.reactor.ErrorSelector;

// Handler & Adapter
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.handler.Handler;

// Event Sources & Event
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.MessageQueue;
import java.nio.channels.SelectableChannel;

// Util
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.Date;


public class CompositeSelector_Test {


        @Test
        public void testConstructorNoArg( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		CompositeSelector selector = new CompositeSelector( );

                Field jreactorF = CompositeSelector.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
		JReactor jreactor = (JReactor)jreactorF.get( selector );
		Assert.assertNull( jreactor );

                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
		BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );
		Assert.assertNull( eventQueue );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
		Registrar registrar = (Registrar)registrarF.get( selector );
		Assert.assertNull( registrar );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
		EventStore eventStore = (EventStore)eventStoreF.get( selector );
		Assert.assertNull( eventStore );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
		QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );
		Assert.assertNull( queueSelector );

                Field channelSelectorF = CompositeSelector.class.getDeclaredField( "channelSelector" );
                channelSelectorF.setAccessible( true );
		ChannelSelector channelSelector = (ChannelSelector)channelSelectorF.get( selector );
		Assert.assertNull( channelSelector );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
		LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
		Assert.assertNull( lockSelector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
		TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );
		Assert.assertNull( timerSelector );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
		SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );
		Assert.assertNull( signalSelector );

                Field errorSelectorF = CompositeSelector.class.getDeclaredField( "errorSelector" );
                errorSelectorF.setAccessible( true );
		ErrorSelector errorSelector = (ErrorSelector)errorSelectorF.get( selector );
		Assert.assertNull( errorSelector );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
		BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );
		Assert.assertNull( blockingSelector );
        }


        @Test
        public void testConstructorArgTrue( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
		RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field jreactorF = CompositeSelector.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                JReactor jreactorA = (JReactor)jreactorF.get( selector );
                Assert.assertSame( jreactor, jreactorA );

                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );
                Assert.assertNotNull( eventQueue );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertNotNull( registrar );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                Assert.assertNotNull( eventStore );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );
                Assert.assertNotNull( queueSelector );

                Field channelSelectorF = CompositeSelector.class.getDeclaredField( "channelSelector" );
                channelSelectorF.setAccessible( true );
                ChannelSelector channelSelector = (ChannelSelector)channelSelectorF.get( selector );
                Assert.assertNotNull( channelSelector );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Assert.assertNotNull( lockSelector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );
                Assert.assertNotNull( timerSelector );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );
                Assert.assertNotNull( signalSelector );

                Field errorSelectorF = CompositeSelector.class.getDeclaredField( "errorSelector" );
                errorSelectorF.setAccessible( true );
                ErrorSelector errorSelector = (ErrorSelector)errorSelectorF.get( selector );
                Assert.assertNotNull( errorSelector );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );
                Assert.assertNotNull( blockingSelector );
        }


        @Test
        public void testConstructorArgFalse( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Field jreactorF = CompositeSelector.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                JReactor jreactorA = (JReactor)jreactorF.get( selector );
                Assert.assertSame( jreactor, jreactorA );

                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );
                Assert.assertNotNull( eventQueue );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertNotNull( registrar );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                Assert.assertNotNull( eventStore );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );
                Assert.assertNull( queueSelector );

                Field channelSelectorF = CompositeSelector.class.getDeclaredField( "channelSelector" );
                channelSelectorF.setAccessible( true );
                ChannelSelector channelSelector = (ChannelSelector)channelSelectorF.get( selector );
                Assert.assertNull( channelSelector );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Assert.assertNotNull( lockSelector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );
                Assert.assertNull( timerSelector );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );
                Assert.assertNull( signalSelector );

                Field errorSelectorF = CompositeSelector.class.getDeclaredField( "errorSelector" );
                errorSelectorF.setAccessible( true );
                ErrorSelector errorSelector = (ErrorSelector)errorSelectorF.get( selector );
                Assert.assertNotNull( errorSelector );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );
                Assert.assertNull( blockingSelector );
        }


        @Test
        public void testIsRegisteredHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

		GenericHandler handler = new GenericHandler( );
		Handle handle1 = selector.registerLock( handler, EventMask.LOCK );
		Assert.assertTrue( selector.isRegistered( handle1 ) );

		Handle handle2 = new Handle( );
		Assert.assertFalse( selector.isRegistered( handle2 ) );
	}


        @Test
        public void testInterestOpsGet( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                Assert.assertEquals( selector.interestOps( handle0 ), EventMask.NOOP );

                GenericHandler handler = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler, EventMask.LOCK );
                Assert.assertEquals( selector.interestOps( handle1 ), EventMask.LOCK );
 
                Handle handle2 = new Handle( );
                Assert.assertEquals( selector.interestOps( handle2 ), -1 );
        }


        @Test
        public void testInterestOpsSet( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                Assert.assertEquals( selector.interestOps( handle0 ), EventMask.NOOP );
		selector.interestOps( handle0, EventMask.NOOP, handle0 );
                Assert.assertEquals( selector.interestOps( handle0 ), EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
                Assert.assertEquals( selector.interestOps( handle1 ), EventMask.NOOP );
                selector.interestOps( handle1, EventMask.LOCK, handle1 );
                Assert.assertEquals( selector.interestOps( handle1 ), EventMask.LOCK );

                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerLock( handler2, EventMask.LOCK );
                Assert.assertEquals( selector.interestOps( handle2 ), EventMask.LOCK );
                selector.interestOps( handle2, EventMask.NOOP, handle2 );
                Assert.assertEquals( selector.interestOps( handle2 ), EventMask.NOOP );

                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = selector.registerLock( handler3, EventMask.LOCK );
                Assert.assertEquals( selector.interestOps( handle3 ), EventMask.LOCK );
                selector.interestOps( handle3, EventMask.LOCK, handle3 );
                Assert.assertEquals( selector.interestOps( handle3 ), EventMask.LOCK );
        }


        @Test
        public void testInterestOpsOrSet( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                GenericHandler handler2 = new GenericHandler( );
		SocketChannel channel = null;
		try {
			channel = SocketChannel.open( );
			channel.configureBlocking( false );
		} catch ( IOException e ) { }
                Handle handle2 = selector.registerChannel( channel, handler2, EventMask.CWRITE );
                selector.interestOpsOr( handle2, EventMask.CREAD, handle2 );
                selector.interestOpsOr( handle2, EventMask.CWRITE, handle2 );
                Assert.assertTrue( EventMask.isCRead( selector.interestOps( handle2 ) ) );
                Assert.assertTrue( EventMask.isCWrite( selector.interestOps( handle2 ) ) );

                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = selector.registerLock( handler3, EventMask.LOCK );
                Assert.assertEquals( selector.interestOps( handle3 ), EventMask.LOCK );
                selector.interestOpsOr( handle3, EventMask.LOCK, handle3 );
                Assert.assertEquals( selector.interestOps( handle3 ), EventMask.LOCK );

                GenericHandler handler4 = new GenericHandler( );
                Handle handle4 = selector.registerLock( handler4, EventMask.NOOP );
                Assert.assertEquals( selector.interestOps( handle4 ), EventMask.NOOP );
                selector.interestOpsOr( handle4, EventMask.NOOP, handle4 );
                Assert.assertEquals( selector.interestOps( handle4 ), EventMask.NOOP );
        }


        @Test
        public void testInterestOpsAndSet( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                GenericHandler handler2 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                        channel.configureBlocking( false );
                } catch ( IOException e ) { }
                Handle handle2 = selector.registerChannel( channel, handler2, EventMask.CWRITE );
                Assert.assertTrue( EventMask.isCWrite( selector.interestOps( handle2 ) ) );
                selector.interestOpsAnd( handle2, EventMask.CREAD, handle2 );
                Assert.assertTrue( EventMask.xIsNoop( selector.interestOps( handle2 ) ) );

                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = selector.registerLock( handler3, EventMask.LOCK );
                Assert.assertEquals( selector.interestOps( handle3 ), EventMask.LOCK );
                selector.interestOpsAnd( handle3, EventMask.LOCK, handle3 );
                Assert.assertEquals( selector.interestOps( handle3 ), EventMask.LOCK );
         
                GenericHandler handler4 = new GenericHandler( );
                Handle handle4 = selector.registerLock( handler4, EventMask.NOOP );
                Assert.assertEquals( selector.interestOps( handle4 ), EventMask.NOOP );
                selector.interestOpsAnd( handle4, EventMask.NOOP, handle4 );
                Assert.assertEquals( selector.interestOps( handle4 ), EventMask.NOOP );

                GenericHandler handler5 = new GenericHandler( );
                Handle handle5 = selector.registerLock( handler5, EventMask.LOCK );
                Assert.assertEquals( selector.interestOps( handle5 ), EventMask.LOCK );
                selector.interestOpsAnd( handle5, EventMask.NOOP, handle5 );
                Assert.assertEquals( selector.interestOps( handle5 ), EventMask.NOOP );
        }


        @Test
        public void testDeregisterHandleSingle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		// deregister handle -- handler has only one handle, in lock group, has event in EventStore

                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
		selector.addMember( handle1, handler0 );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
		eventStore.store( handler0, new Event( ) );

		selector.deregister( handle0, handle0 );

		Assert.assertFalse( selector.isRegistered( handle0 ) );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
		Assert.assertFalse( registrar.contains( handle0 ) );
		Assert.assertFalse( registrar.contains( handler0 ) );
		Assert.assertNull( registrar.getSelector( handle0 ) );
		Assert.assertEquals( registrar.getInterestOps( handle0 ), -1 );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
		Assert.assertFalse( lockSelector.isRegistered( handler0 ) );

		Assert.assertFalse( eventStore.contains( handler0 ) );
	}


        @Test
        public void testDeregisterHandleMulti( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                // deregister handle -- handler has two handles, in lock group, has event in EventStore

                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                Handle handle0a = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
                selector.addMember( handle1, handler0 );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                eventStore.store( handler0, new Event( ) );

                selector.deregister( handle0, handle0 );

                Assert.assertFalse( selector.isRegistered( handle0 ) );
                Assert.assertTrue( selector.isRegistered( handle0a ) );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertFalse( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertNull( registrar.getSelector( handle0 ) );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), -1 );
                Assert.assertTrue( registrar.contains( handle0a ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertNotNull( registrar.getSelector( handle0a ) );
                Assert.assertEquals( registrar.getInterestOps( handle0a ), 0 );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
		Assert.assertTrue( lockSelector.isRegistered( handler0 ) );

                Assert.assertTrue( eventStore.contains( handler0 ) );
        }


        @Test
        public void testDeregisterHandleLockHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                // deregister handle -- lockHandle -- lockHandle manages one handler and has event in EventStore

                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
                selector.addMember( handle1, handler0 );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                eventStore.store( handler1, new Event( ) );

                selector.deregister( handle1, handle1 );

		Assert.assertTrue( selector.isRegistered( handler0 ) );

                Assert.assertFalse( selector.isRegistered( handle1 ) );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertFalse( registrar.contains( handle1 ) );
                Assert.assertFalse( registrar.contains( handler1 ) );
                Assert.assertNull( registrar.getSelector( handle1 ) );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), -1 );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Assert.assertFalse( lockSelector.isRegistered( handle1 ) );

                Assert.assertFalse( eventStore.contains( handler1 ) );
        }


        @Test
        public void testHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0a = selector.registerLock( handler0, EventMask.NOOP );
                Handle handle0b = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
		Assert.assertSame( selector.handler( handle0a ), handler0 );
		Assert.assertSame( selector.handler( handle0b ), handler0 );

		Handle handle = new Handle( );
		Assert.assertNull( selector.handler( handle ) );
	}


        @Test
        public void testIsRegisteredHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler, EventMask.LOCK );
                Assert.assertTrue( selector.isRegistered( handler ) );

                GenericHandler handler2 = new GenericHandler( );
                Assert.assertFalse( selector.isRegistered( handler2 ) );
        }


        @Test
        public void testHandles( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                Assert.assertEquals( selector.handles( handler0 ).size( ), 1 );

                GenericHandler handler1 = new GenericHandler( );
                Handle handle1a = selector.registerLock( handler1, EventMask.NOOP );
                Handle handle1b = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );

                GenericHandler handler2 = new GenericHandler( );
                Assert.assertEquals( selector.handles( handler2 ).size( ), 0 );
        }


        @Test
        public void testDeregisterHandlerSingle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                // deregister handler -- handler has only one handle, in lock group, has event in EventStore

                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
                selector.addMember( handle1, handler0 );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                eventStore.store( handler0, new Event( ) );

                selector.deregister( handler0, handle0 );

                Assert.assertFalse( selector.isRegistered( handler0 ) );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertFalse( registrar.contains( handle0 ) );
                Assert.assertFalse( registrar.contains( handler0 ) );
                Assert.assertNull( registrar.getSelector( handle0 ) );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), -1 );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Assert.assertFalse( lockSelector.isRegistered( handler0 ) );

                Assert.assertFalse( eventStore.contains( handler0 ) );
        }


        @Test
        public void testDeregisterHandlerMulti( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                // deregister handler -- handler has two handles, in lock group, has event in EventStore

                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                Handle handle0a = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
                selector.addMember( handle1, handler0 );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                eventStore.store( handler0, new Event( ) );

                selector.deregister( handler0, handle0 );

                Assert.assertFalse( selector.isRegistered( handler0 ) );
                Assert.assertTrue( selector.isRegistered( handler1 ) );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertFalse( registrar.contains( handle0 ) );
                Assert.assertFalse( registrar.contains( handler0 ) );
                Assert.assertNull( registrar.getSelector( handle0 ) );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), -1 );
                Assert.assertFalse( registrar.contains( handle0a ) );
                Assert.assertFalse( registrar.contains( handler0 ) );
                Assert.assertNull( registrar.getSelector( handle0a ) );
                Assert.assertEquals( registrar.getInterestOps( handle0a ), -1 );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Assert.assertFalse( lockSelector.isRegistered( handler0 ) );

                Assert.assertFalse( eventStore.contains( handler0 ) );
        }


        @Test
        public void testDeregisterHandlerLockHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                // deregister handle -- lockHandle -- lockHandle manages one handler and has event in EventStore

                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.NOOP );
                selector.addMember( handle1, handler0 );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                eventStore.store( handler1, new Event( ) );

                selector.deregister( handler1, handle1 );

                Assert.assertTrue( selector.isRegistered( handler0 ) );

                Assert.assertFalse( selector.isRegistered( handle1 ) );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertFalse( registrar.contains( handle1 ) );
                Assert.assertFalse( registrar.contains( handler1 ) );
                Assert.assertNull( registrar.getSelector( handle1 ) );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), -1 );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Assert.assertFalse( lockSelector.isRegistered( handle1 ) );

                Assert.assertFalse( eventStore.contains( handler1 ) );
        }


        @Test
        public void testRegisterQueue( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

		// EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
		MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = selector.registerQueue( queue0, handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( queueSelector.isRegistered( handle0 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue0 ) );

		// EventMask = QREAD 
                GenericHandler handler1 = new GenericHandler( );
                MessageQueue queue1 = new LinkedBlockingMessageQueue( );
                Handle handle1 = selector.registerQueue( queue1, handler1, EventMask.QREAD );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.QREAD );
                Assert.assertTrue( queueSelector.isRegistered( handle1 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue1 ) );

		// 2nd event source and handle 
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = selector.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.QREAD );
                Assert.assertTrue( queueSelector.isRegistered( handle2 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
	}


        @Test
        public void testIsRegisteredQueueMessageQueue( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                GenericHandler handler0 = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = selector.registerQueue( queue0, handler0, EventMask.NOOP );
                Assert.assertTrue( selector.isRegisteredQueue( queue0 ) );

                MessageQueue queue1 = new LinkedBlockingMessageQueue( );
                Assert.assertFalse( selector.isRegisteredQueue( queue1 ) );
        }


        @Test
        public void testIsRegisteredQueueHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                GenericHandler handler0 = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = selector.registerQueue( queue0, handler0, EventMask.NOOP );
                Assert.assertTrue( selector.isRegisteredQueue( handle0 ) );

                Handle handle1 = new Handle( );
                Assert.assertFalse( selector.isRegisteredQueue( handle1 ) );
        }


        @Test
        public void testRegisterChannel( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field channelSelectorF = CompositeSelector.class.getDeclaredField( "channelSelector" );
                channelSelectorF.setAccessible( true );
                ChannelSelector channelSelector = (ChannelSelector)channelSelectorF.get( selector );

		// EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                SocketChannel channel0 = null;
                try {
                        channel0 = SocketChannel.open( );
                        channel0.configureBlocking( false );
                } catch ( IOException e ) { }
                Handle handle0 = selector.registerChannel( channel0, handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), channelSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( channelSelector.isRegistered( handle0 ) );
                Assert.assertTrue( channelSelector.isRegistered( channel0 ) );


		// EventMask = CREAD
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel1 = null;
                try {
                        channel1 = SocketChannel.open( );
                        channel1.configureBlocking( false );
                } catch ( IOException e ) { }
                Handle handle1 = selector.registerChannel( channel1, handler1, EventMask.CREAD );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), channelSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.CREAD );
                Assert.assertTrue( channelSelector.isRegistered( handle1 ) );
                Assert.assertTrue( channelSelector.isRegistered( channel1 ) );


		// 2nd event source and handle 
                SocketChannel channel2 = null;
                try {
                        channel2 = SocketChannel.open( );
                        channel2.configureBlocking( false );
                } catch ( IOException e ) { }
                Handle handle2 = selector.registerChannel( channel2, handler1, EventMask.CREAD );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), channelSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.CREAD );
                Assert.assertTrue( channelSelector.isRegistered( handle2 ) );
                Assert.assertTrue( channelSelector.isRegistered( channel2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testIsRegisteredChannelChannel( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                GenericHandler handler0 = new GenericHandler( );
                SocketChannel channel0 = null;
                try {
                        channel0 = SocketChannel.open( );
                        channel0.configureBlocking( false );
                } catch ( IOException e ) { }
                Handle handle0 = selector.registerChannel( channel0, handler0, EventMask.NOOP );
                Assert.assertTrue( selector.isRegisteredChannel( channel0 ) );


                SocketChannel channel1 = null;
                try {
                        channel1 = SocketChannel.open( );
                        channel1.configureBlocking( false );
                } catch ( IOException e ) { }

                Assert.assertFalse( selector.isRegisteredChannel( channel1 ) );
        }


        @Test
        public void testIsRegisteredChannelHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                GenericHandler handler0 = new GenericHandler( );
                SocketChannel channel0 = null;
                try {
                        channel0 = SocketChannel.open( );
                        channel0.configureBlocking( false );
                } catch ( IOException e ) { }
                Handle handle0 = selector.registerChannel( channel0, handler0, EventMask.NOOP );
                Assert.assertTrue( selector.isRegisteredChannel( handle0 ) );


		Handle handle1 = new Handle( );
                Assert.assertFalse( selector.isRegisteredChannel( handle1 ) );
        }


        @Test
        public void testRegisterTimerOnceDate( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

		// EventMask = NOOP
		GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = selector.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle0 ) );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                Date firstTime1 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle1 = selector.registerTimerOnce( firstTime1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle1 ) );

		// 2nd event source and handle 
                Date firstTime2 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle2 = selector.registerTimerOnce( firstTime2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testRegisterTimerOnceDelay( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                long delay0 = 1000;
                Handle handle0 = selector.registerTimerOnce( delay0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle0 ) );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                long delay1 = 1000;
                Handle handle1 = selector.registerTimerOnce( delay1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
                long delay2 = 1000;
                Handle handle2 = selector.registerTimerOnce( delay2, handler1, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }

        @Test
        public void testRegisterTimerFixedDelayDate( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period0 = 1000;
                Handle handle0 = selector.registerTimerFixedDelay( firstTime0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle0 ) );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                Date firstTime1 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period1 = 1000;
                Handle handle1 = selector.registerTimerFixedDelay( firstTime1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
                Date firstTime2 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period2 = 1000;
                Handle handle2 = selector.registerTimerFixedDelay( firstTime2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testRegisterTimerFixedDelayLong( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                long delay0 = 1000;
                long period0 = 1000;
                Handle handle0 = selector.registerTimerFixedDelay( delay0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle0 ) );

                // EventMask = TIMER 
                GenericHandler handler1 = new GenericHandler( );
                long delay1 = 1000;
                long period1 = 1000;
                Handle handle1 = selector.registerTimerFixedDelay( delay1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
                long delay2 = 1000;
                long period2 = 1000;
                Handle handle2 = selector.registerTimerFixedDelay( delay2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }

        @Test
        public void testRegisterTimerFixedRateDate( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period0 = 1000;
                Handle handle0 = selector.registerTimerFixedRate( firstTime0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle0 ) );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                Date firstTime1 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period1 = 1000;
                Handle handle1 = selector.registerTimerFixedRate( firstTime1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle1 ) );

                // EventMask = TIMER
                Date firstTime2 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period2 = 1000;
                Handle handle2 = selector.registerTimerFixedRate( firstTime2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testRegisterFixedRateLong( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                long delay0 = 1000;
                long period0 = 1000;
                Handle handle0 = selector.registerTimerFixedRate( delay0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( timerSelector.isRegistered( handle0 ) );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                long delay1 = 1000;
                long period1 = 1000;
                Handle handle1 = selector.registerTimerFixedRate( delay1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle1 ) );

                // EventMask = TIMER
                long delay2 = 1000;
                long period2 = 1000;
                Handle handle2 = selector.registerTimerFixedRate( delay2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), timerSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.TIMER );
                Assert.assertTrue( timerSelector.isRegistered( handle2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testIsRegisteredTimer( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = selector.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );

		Assert.assertTrue( selector.isRegistered( handle0 ) );
	}


        @Test
        public void testCancelTimerSingleNotRegistered( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

		Handle handle = new Handle( );
		Assert.assertFalse( selector.cancelTimer( handle ) );
		Assert.assertFalse( selector.cancelTimer( handle ) );
	}


        @Test
        public void testCancelTimerSingleDeregister( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = selector.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );

		Assert.assertTrue( selector.cancelTimer( handle0 ) );
		Assert.assertFalse( selector.cancelTimer( handle0 ) );
                Assert.assertFalse( selector.isRegistered( handle0 ) );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );
                Assert.assertFalse( registrar.contains( handle0 ) );
                Assert.assertFalse( registrar.contains( handler0 ) );
                Assert.assertNull( registrar.getSelector( handle0 ) );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), -1 );
        }


        @Test
        public void testCancelTimerSinglePending( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = selector.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );
		timerSelector.checkin( handle0, new Event( ) );

                Assert.assertTrue( selector.cancelTimer( handle0 ) );
                Assert.assertFalse( selector.cancelTimer( handle0 ) );
                Assert.assertTrue( selector.isRegistered( handle0 ) );
	}


        @Test
        public void testRegisterLock( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), lockSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( lockSelector.isRegistered( handle0 ) );

                // EventMask = LOCK
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerLock( handler1, EventMask.LOCK );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), lockSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.LOCK );
                Assert.assertTrue( lockSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
		MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = selector.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.QREAD );
                Assert.assertTrue( queueSelector.isRegistered( handle2 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testAddMember( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                GenericHandler handler2 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
		selector.addMember( handle0, handler1 );
		Assert.assertTrue( selector.isMember( handle0, handler1 ) );
		selector.addMember( handle0, handler2 );
		Assert.assertTrue( selector.isMember( handle0, handler1 ) );
		Assert.assertTrue( selector.isMember( handle0, handler2 ) );
	}


        @Test
        public void testRemoveMember( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                GenericHandler handler2 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                selector.removeMember( handle0, handler1 );
                Assert.assertFalse( selector.isMember( handle0, handler1 ) );
                selector.addMember( handle0, handler1 );
                selector.addMember( handle0, handler2 );
                selector.removeMember( handle0, handler1 );
                selector.removeMember( handle0, handler2 );
                Assert.assertFalse( selector.isMember( handle0, handler1 ) );
                Assert.assertFalse( selector.isMember( handle0, handler2 ) );
        }

        @Test
        public void testIsRegisteredLock( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
		Assert.assertTrue( selector.isRegisteredLock( handle0 ) );

		Handle handle1 = new Handle( );
		Assert.assertFalse( selector.isRegisteredLock( handle1 ) );
	}


        @Test
        public void testIsMember( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                Assert.assertTrue( selector.isMember( handle0, handler1 ) );
                GenericHandler handler2 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );
                Assert.assertTrue( selector.isMember( handle0, handler1 ) );
                Assert.assertTrue( selector.isMember( handle0, handler2 ) );
        }


        @Test
        public void testLockToLock( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

		selector.lock( handle0, handle0 );

		Assert.assertTrue( selector.isLocked( handle0 ) );
		Assert.assertTrue( tracker.isLocked( handler1 ) );
		Assert.assertTrue( tracker.isLocked( handler2 ) );
        }


        @Test
        public void testLockToPending( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

		tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                Assert.assertTrue( selector.isPending( handle0 ) );
                Assert.assertTrue( tracker.isRunning( handler1 ) );
                Assert.assertFalse( tracker.isLocked( handler1 ) );
                Assert.assertTrue( tracker.isLocked( handler2 ) );
        }


        @Test
        public void testUnlock( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                selector.lock( handle0, handle0 );

                selector.resumeSelection( handler0, handle0 );
                selector.unlock( handle0, handle0 );

                Assert.assertFalse( selector.isLocked( handle0 ) );
                Assert.assertFalse( tracker.isLocked( handler1 ) );
                Assert.assertFalse( tracker.isLocked( handler2 ) );
        }


        @Test
        public void testUnlockRunning( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                selector.lock( handle0, handle0 );
                selector.unlockRunning( handle0, handle0 );
                selector.resumeSelection( handler0, handle0 );
                selector.unlock( handle0, handle0 );

                Assert.assertFalse( selector.isLocked( handle0 ) );
                Assert.assertFalse( tracker.isLocked( handler1 ) );
                Assert.assertFalse( tracker.isLocked( handler2 ) );
        }


        @Test
        public void testIsLockedMember( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

		// open
                Assert.assertFalse( selector.isLockedMember( handle0, handler1 ) );
                Assert.assertFalse( selector.isLockedMember( handle0, handler2 ) );

		tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

		// pending
                Assert.assertFalse( selector.isLockedMember( handle0, handler1 ) );
                Assert.assertFalse( selector.isLockedMember( handle0, handler2 ) );

		tracker.remove( handler1 );
		selector.takePending( handle0, handler1 );

		// locked
                Assert.assertTrue( selector.isLockedMember( handle0, handler1 ) );
                Assert.assertTrue( selector.isLockedMember( handle0, handler2 ) );


		// test when handle is not a member
		GenericHandler handlerA = new GenericHandler( );
                Assert.assertFalse( selector.isLockedMember( handle0, handlerA ) );
        }


        @Test
        public void testIsOpenHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                // open
                Assert.assertTrue( selector.isOpen( handler1 ) );
                Assert.assertTrue( selector.isOpen( handler2 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                // pending
                Assert.assertFalse( selector.isOpen( handler1 ) );
                Assert.assertFalse( selector.isOpen( handler2 ) );

                tracker.remove( handler1 );
                selector.takePending( handle0, handler1 );

                // locked
                Assert.assertFalse( selector.isOpen( handler1 ) );
                Assert.assertFalse( selector.isOpen( handler2 ) );


                // test when handler is not a managed handler
                GenericHandler handlerA = new GenericHandler( );
                Assert.assertTrue( selector.isOpen( handlerA ) );
        }


        @Test
        public void testIsOpenHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                // open
                Assert.assertTrue( selector.isOpen( handle0 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                // pending
                Assert.assertFalse( selector.isOpen( handle0 ) );

                tracker.remove( handler1 );
                selector.takePending( handle0, handler1 );

                // locked
                Assert.assertFalse( selector.isOpen( handle0 ) );
        }


        @Test
        public void testIsPendingHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                // open
                Assert.assertFalse( selector.isPending( handler1 ) );
                Assert.assertFalse( selector.isPending( handler2 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                // pending
                Assert.assertTrue( selector.isPending( handler1 ) );
                Assert.assertFalse( selector.isPending( handler2 ) );

                tracker.remove( handler1 );
                selector.takePending( handle0, handler1 );

                // locked
                Assert.assertFalse( selector.isPending( handler1 ) );
                Assert.assertFalse( selector.isPending( handler2 ) );


                // test when handler is not a managed handler
                GenericHandler handlerA = new GenericHandler( );
                Assert.assertFalse( selector.isPending( handlerA ) );
        }


        @Test
        public void testIsPendingHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                // open
                Assert.assertFalse( selector.isPending( handle0 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                // pending
                Assert.assertTrue( selector.isPending( handle0 ) );

                tracker.remove( handler1 );
                selector.takePending( handle0, handler1 );

                // locked
                Assert.assertFalse( selector.isPending( handle0 ) );
        }


        @Test
        public void testIsLockedHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                // open
                Assert.assertFalse( selector.isLocked( handler1 ) );
                Assert.assertFalse( selector.isLocked( handler2 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                // pending
                Assert.assertFalse( selector.isLocked( handler1 ) );
                Assert.assertTrue( selector.isLocked( handler2 ) );

                tracker.remove( handler1 );
                selector.takePending( handle0, handler1 );

                // locked
                Assert.assertTrue( selector.isLocked( handler1 ) );
                Assert.assertTrue( selector.isLocked( handler2 ) );


                // test when handler is not a managed handler
                GenericHandler handlerA = new GenericHandler( );
                Assert.assertFalse( selector.isLocked( handlerA ) );
        }


        @Test
        public void testIsLockedHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                // open
                Assert.assertFalse( selector.isLocked( handle0 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                // pending
                Assert.assertFalse( selector.isLocked( handle0 ) );

                tracker.remove( handler1 );
                selector.takePending( handle0, handler1 );

                // locked
                Assert.assertTrue( selector.isLocked( handle0 ) );
        }

        @Test
        public void testRegisterSignal( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerSignal( "shutdownhook", handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), signalSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( signalSelector.isRegistered( "shutdownhook" ) );
		selector.deregister( handle0, handle0 );

                // EventMask = SIGNAL
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerSignal( "shutdownhook", handler1, EventMask.SIGNAL );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), signalSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.SIGNAL );
                Assert.assertTrue( signalSelector.isRegistered( handle1 ) );
                Assert.assertTrue( signalSelector.isRegistered( "shutdownhook" ) );

                // 2nd event source and handle
                Handle handle2 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.NOOP );
                Assert.assertTrue( queueSelector.isRegistered( handle2 ) );
                Assert.assertTrue( signalSelector.isRegistered( "shutdownhook" ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
		selector.deregister( handle1, handle1 );

		signalSelector.shutdown( );
        }


        @Test
        public void testIsRegisteredSignalHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerSignal( "shutdownhook", handler0, EventMask.NOOP );
                Assert.assertTrue( selector.isRegisteredSignal( handle0 ) );

		Handle handle1 = new Handle( );
                Assert.assertFalse( selector.isRegisteredSignal( handle1 ) );

		signalSelector.shutdown( );
        }


        @Test
        public void testIsRegisteredSignalString( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );

                Assert.assertFalse( selector.isRegisteredSignal( "shutdownhook" ) );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerSignal( "shutdownhook", handler0, EventMask.NOOP );
                Assert.assertTrue( selector.isRegisteredSignal( "shutdownhook" ) );

                signalSelector.shutdown( );
        }


        @Test
        public void testRegisterError( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                Field errorSelectorF = CompositeSelector.class.getDeclaredField( "errorSelector" );
                errorSelectorF.setAccessible( true );
                ErrorSelector errorSelector = (ErrorSelector)errorSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerError( handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), errorSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( errorSelector.isRegistered( handle0 ) );

                // EventMask = ERROR
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerError( handler1, EventMask.ERROR );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), errorSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = selector.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.QREAD );
                Assert.assertTrue( queueSelector.isRegistered( handle2 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testNewErrorHandleHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field errorSelectorF = CompositeSelector.class.getDeclaredField( "errorSelector" );
                errorSelectorF.setAccessible( true );
                ErrorSelector errorSelector = (ErrorSelector)errorSelectorF.get( selector );

                // EventMask = ERROR
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerError( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		Handle handle2 = selector.newErrorHandle( handler1 );
                Assert.assertFalse( errorSelector.isRegistered( handle1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle2 ) );
        }


        @Test
        public void testNewErrorHandleHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field errorSelectorF = CompositeSelector.class.getDeclaredField( "errorSelector" );
                errorSelectorF.setAccessible( true );
                ErrorSelector errorSelector = (ErrorSelector)errorSelectorF.get( selector );

                // EventMask = ERROR
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerError( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
                Handle handle2 = selector.newErrorHandle( handle1 );
                Assert.assertFalse( errorSelector.isRegistered( handle1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle2 ) );
        }


        @Test
        public void testGetErrorHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerError( handler1, EventMask.ERROR );
                Assert.assertSame( selector.getErrorHandle( handler1 ), handle1 );
        }


        @Test
        public void testIsRegisteredErrorHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerError( handler1, EventMask.ERROR );
                Assert.assertTrue( selector.isRegisteredError( handler1 ) );

                GenericHandler handler2 = new GenericHandler( );
                Assert.assertFalse( selector.isRegisteredError( handler2 ) );
        }


        @Test
        public void testIsRegisteredErrorHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerError( handler1, EventMask.ERROR );
                Assert.assertTrue( selector.isRegisteredError( handle1 ) );

                Handle handle2 = new Handle( );
                Assert.assertFalse( selector.isRegisteredError( handle2 ) );
        }


        @Test
        public void testRegisterBlockingSingle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerBlocking( new GenericRunnable( ), handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), blockingSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( blockingSelector.isRegistered( handle0 ) );

                // EventMask = BLOCKING
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerBlocking( new GenericRunnable( ), handler1, EventMask.BLOCKING );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), blockingSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.BLOCKING );
                Assert.assertTrue( blockingSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = selector.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.QREAD );
                Assert.assertTrue( queueSelector.isRegistered( handle2 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testRegisterBlockingGroup( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
		Set<Runnable> runSet1 = new HashSet( );
		runSet1.add( new GenericRunnable( ) );
		runSet1.add( new GenericRunnable( ) );
                Handle handle0 = selector.registerBlockingGroup( runSet1, handler0, EventMask.NOOP );
                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), blockingSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( blockingSelector.isRegistered( handle0 ) );

                // EventMask = BLOCKING
                GenericHandler handler1 = new GenericHandler( );
		Set<Runnable> runSet2 = new HashSet( );
		runSet2.add( new GenericRunnable( ) );
		runSet2.add( new GenericRunnable( ) );
                Handle handle1 = selector.registerBlockingGroup( runSet2, handler1, EventMask.BLOCKING );
                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), blockingSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.BLOCKING );
                Assert.assertTrue( blockingSelector.isRegistered( handle1 ) );

                // 2nd event source and handle
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = selector.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.QREAD );
                Assert.assertTrue( queueSelector.isRegistered( handle2 ) );
                Assert.assertTrue( queueSelector.isRegistered( queue2 ) );
                Assert.assertEquals( selector.handles( handler1 ).size( ), 2 );
        }


        @Test
        public void testIsRegisteredBlockingHandle( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerBlocking( new GenericRunnable( ), handler1, EventMask.BLOCKING );
                Assert.assertTrue( selector.isRegisteredBlocking( handle1 ) );

                Handle handle2 = new Handle( );
                Assert.assertFalse( selector.isRegisteredBlocking( handle2 ) );
        }


        @Test
        public void testConfigureBlockingThreadPoolThread( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );

		selector.configureBlockingThreadPool( 3 );

                Field workerThreadNumF2 = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF2.setAccessible( true );
                Integer workerThreadNum2 = (Integer)workerThreadNumF2.get( blockingSelector );
		int b = workerThreadNum2;
		Assert.assertEquals( b, 3 );

	}


        @Test
        public void testConfigureBlockingThreadPoolTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

		selector.configureBlockingThreadPool( 7, TimeUnit.MILLISECONDS, 8, TimeUnit.NANOSECONDS );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );

		int a = terminationTimeoutFirst;
		int b = terminationTimeoutLast;
		Assert.assertEquals( a, 7 );
		Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.MILLISECONDS );
		Assert.assertEquals( b, 8 );
		Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.NANOSECONDS );
        }


        @Test
        public void testConfigureBlockingThreadPoolThreadTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                selector.configureBlockingThreadPool( 3, 7, TimeUnit.MILLISECONDS, 8, TimeUnit.NANOSECONDS );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );

                Field workerThreadNumF2 = BlockingSelector.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF2.setAccessible( true );
                Integer workerThreadNum2 = (Integer)workerThreadNumF2.get( blockingSelector );

                Field terminationTimeoutFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( blockingSelector );

                Field terminationTimeoutUnitsFirstF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( blockingSelector );

                Field terminationTimeoutLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( blockingSelector );

                Field terminationTimeoutUnitsLastF = BlockingSelector.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( blockingSelector );

                int z = workerThreadNum2;
                int a = terminationTimeoutFirst;
                int b = terminationTimeoutLast;
                Assert.assertEquals( z, 3 );
                Assert.assertEquals( a, 7 );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.MILLISECONDS );
                Assert.assertEquals( b, 8 );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.NANOSECONDS );
        }


        @Test
        public void testEnableQueueSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );
                Assert.assertNull( queueSelector );

		selector.enableQueueSelector( );

                Field queueSelectorF2 = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF2.setAccessible( true );
                QueueSelector queueSelector2 = (QueueSelector)queueSelectorF2.get( selector );
                Assert.assertNotNull( queueSelector2 );
	}


        @Test
        public void testEnableChannelSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Field channelSelectorF = CompositeSelector.class.getDeclaredField( "channelSelector" );
                channelSelectorF.setAccessible( true );
                ChannelSelector channelSelector = (ChannelSelector)channelSelectorF.get( selector );
                Assert.assertNull( channelSelector );

                selector.enableChannelSelector( );

                Field channelSelectorF2 = CompositeSelector.class.getDeclaredField( "channelSelector" );
                channelSelectorF2.setAccessible( true );
                ChannelSelector channelSelector2 = (ChannelSelector)channelSelectorF2.get( selector );
                Assert.assertNotNull( channelSelector2 );
        }


        @Test
        public void testEnableTimerSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );
                Assert.assertNull( timerSelector );

                selector.enableTimerSelector( );

                Field timerSelectorF2 = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF2.setAccessible( true );
                TimerSelector timerSelector2 = (TimerSelector)timerSelectorF2.get( selector );
                Assert.assertNotNull( timerSelector2 );
        }


        @Test
        public void testEnableBlockingSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Field blockingSelectorF = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                BlockingSelector blockingSelector = (BlockingSelector)blockingSelectorF.get( selector );
                Assert.assertNull( blockingSelector );

                selector.enableBlockingSelector( );

                Field blockingSelectorF2 = CompositeSelector.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF2.setAccessible( true );
                BlockingSelector blockingSelector2 = (BlockingSelector)blockingSelectorF2.get( selector );
                Assert.assertNotNull( blockingSelector2 );
        }


        @Test
        public void testEnableSignalSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Field signalSelectorF = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF.setAccessible( true );
                SignalSelector signalSelector = (SignalSelector)signalSelectorF.get( selector );
                Assert.assertNull( signalSelector );

                selector.enableSignalSelector( );

                Field signalSelectorF2 = CompositeSelector.class.getDeclaredField( "signalSelector" );
                signalSelectorF2.setAccessible( true );
                SignalSelector signalSelector2 = (SignalSelector)signalSelectorF2.get( selector );
                Assert.assertNotNull( signalSelector2 );
        }


        @Test
        public void testReportErrorHandleException( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

		Handle handle = new Handle( );
		IOException e = new IOException( );
		selector.reportError( handle, e );
		Assert.assertSame( jreactor.getReportHandle( ), handle );
		Assert.assertSame( jreactor.getReportE( ), e );
	}


        @Test
        public void testReportErrorHandleExceptionCommand( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                Handle handle = new Handle( );
                IOException e = new IOException( );
		MessageBlock mb = new MessageBlock( );
                selector.reportError( handle, e, mb );
                Assert.assertSame( jreactor.getReportHandle( ), handle );
                Assert.assertSame( jreactor.getReportE( ), e );
                Assert.assertSame( jreactor.getReportCommand( ), mb );
        }


        @Test
        public void testReportCriticalError( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, false );

                IOException e = new IOException( );
                selector.reportCriticalError( e );
                Assert.assertSame( jreactor.getReportE( ), e );
        }


        @Test
        public void testresumeSelectionTransferEvents( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

		GenericHandler handler0 = new GenericHandler( );
		MessageQueue queue0 = new LinkedBlockingMessageQueue( );
		Handle handle0 = selector.registerQueue( queue0, handler0, EventMask.QREAD );
		eventStore.store( handler0, new Event( ) );
		eventStore.store( handler0, new Event( ) );

		selector.resumeSelection( handler0, handle0 );

		Assert.assertEquals( eventQueue.size( ), 2 );
	}


        @Test
        public void testresumeSelectionHandleNotNull( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                GenericHandler handler0 = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = selector.registerQueue( queue0, handler0, EventMask.QREAD );
		queue0.offer( new MessageBlock( ) );

                selector.resumeSelection( handler0, handle0 );

                Assert.assertEquals( eventQueue.size( ), 2 );
        }


        @Test
        public void testresumeSelectionHandleNull( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                GenericHandler handler = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = selector.registerQueue( queue0, handler, EventMask.QREAD );
                MessageQueue queue1 = new LinkedBlockingMessageQueue( );
                Handle handle1 = selector.registerQueue( queue1, handler, EventMask.QREAD );

                queue0.offer( new MessageBlock( ) );
                queue1.offer( new MessageBlock( ) );

                selector.resumeSelection( handler, null );

                Assert.assertEquals( eventQueue.size( ), 4 );
        }


        @Test
        public void testTakePendingHandleHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                Assert.assertFalse( selector.takePending( handle0, handler1 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

		tracker.remove( handler1 );

                Assert.assertTrue( selector.takePending( handle0, handler1 ) );
        }


        @Test
        public void testTakePendingHandler( ) {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                Assert.assertFalse( selector.takePending( handler1 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

                tracker.remove( handler1 );

                Assert.assertTrue( selector.takePending( handler1 ) );
        }


        @Test
        public void testProcessRegister( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

		// handler -> handle
                GenericHandler handler0 = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
		Handle handle0 = new Handle( );
		selector.processRegister( handle0, handler0, queueSelector, EventMask.NOOP );

		// handler -> 2 handles
                GenericHandler handler1 = new GenericHandler( );
                MessageQueue queue1 = new LinkedBlockingMessageQueue( );
		Handle handle1 = new Handle( );
		selector.processRegister( handle1, handler1, queueSelector, EventMask.NOOP );
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
		Handle handle2 = new Handle( );
		selector.processRegister( handle2, handler1, queueSelector, EventMask.NOOP );


                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.NOOP );
                Assert.assertEquals( registrar.getHandles( handler0 ).size( ), 1 );

                Assert.assertTrue( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertEquals( registrar.getSelector( handle1 ), queueSelector );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), EventMask.NOOP );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.NOOP );
                Assert.assertEquals( registrar.getHandles( handler1 ).size( ), 2 );
	}


        @Test
        public void testProcessInterestOps( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );
                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                // handler -> handle
                GenericHandler handler0 = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = new Handle( );
                selector.processRegister( handle0, handler0, queueSelector, EventMask.NOOP );
                selector.processInterestOps( handle0, EventMask.QREAD );


                Assert.assertTrue( registrar.contains( handle0 ) );
                Assert.assertTrue( registrar.contains( handler0 ) );
                Assert.assertEquals( registrar.getSelector( handle0 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), EventMask.QREAD );
                Assert.assertEquals( registrar.getHandles( handler0 ).size( ), 1 );
        }


        @Test
        public void testProcessDeregisterHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field queueSelectorF = CompositeSelector.class.getDeclaredField( "queueSelector" );
                queueSelectorF.setAccessible( true );
                QueueSelector queueSelector = (QueueSelector)queueSelectorF.get( selector );

                // handler -> handle
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = new Handle( );
                selector.processRegister( handle0, handler0, queueSelector, EventMask.NOOP );
		selector.processDeregister( handle0 );

                // handler -> 2 handles
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = new Handle( );
                selector.processRegister( handle1, handler1, queueSelector, EventMask.NOOP );
                Handle handle2 = new Handle( );
                selector.processRegister( handle2, handler1, queueSelector, EventMask.NOOP );
		selector.processDeregister( handle1 );


                Assert.assertFalse( registrar.contains( handle0 ) );
                Assert.assertFalse( registrar.contains( handler0 ) );
                Assert.assertNull( registrar.getSelector( handle0 ) );
                Assert.assertEquals( registrar.getInterestOps( handle0 ), -1 );
                Assert.assertEquals( registrar.getHandles( handler0 ).size( ), 0 );

                Assert.assertFalse( registrar.contains( handle1 ) );
                Assert.assertTrue( registrar.contains( handle2 ) );
                Assert.assertTrue( registrar.contains( handler1 ) );
                Assert.assertNull( registrar.getSelector( handle1 ) );
                Assert.assertEquals( registrar.getSelector( handle2 ), queueSelector );
                Assert.assertEquals( registrar.getInterestOps( handle1 ), -1 );
                Assert.assertEquals( registrar.getInterestOps( handle2 ), EventMask.NOOP );
                Assert.assertEquals( registrar.getHandles( handler1 ).size( ), 1 );
        }


        @Test
        public void testProcessDeregisterHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field registrarF = CompositeSelector.class.getDeclaredField( "registrar" );
                registrarF.setAccessible( true );
                Registrar registrar = (Registrar)registrarF.get( selector );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );

                GenericHandler handler0 = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                selector.registerQueue( queue0, handler0, EventMask.NOOP );

                eventStore.store( handler0, new Event( ) );

		GenericHandler handler1 = new GenericHandler( );
		Handle handle1 = selector.registerLock( handler1, EventMask.LOCK );
		selector.addMember( handle1, handler0 );

                selector.processDeregister( handler0 );

		Assert.assertFalse( eventStore.contains( handler0 ) );
		Assert.assertFalse( lockSelector.isRegistered( handler1 ) );
        }


        @Test
        public void testGetLockedHandlerAdapter( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                selector.addMember( handle0, handler1 );
                GenericHandler handler2 = new GenericHandler( );
                selector.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                selector.addMember( handle0, handler2 );

                Assert.assertFalse( selector.takePending( handler1 ) );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                selector.lock( handle0, handle0 );

		HandlerAdapter adapter = selector.getLockedHandlerAdapter( handle0 );

		Assert.assertSame( adapter, lockSelector.getLockedHandlerAdapter( handle0 ) );

		// null if lockHandle is not registered or doesn't register a lockHandle group
		Assert.assertNull( lockSelector.getLockedHandlerAdapter( handle1 ) );
		Assert.assertNull( lockSelector.getLockedHandlerAdapter( new Handle( ) ) );
	}


        @Test
        public void testAddReadyEvent( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

		Event ev0 = new Event( );
		Event ev1 = new Event( );
                selector.addReadyEvent( ev0 );
                selector.addReadyEvent( ev1 );

                Assert.assertEquals( eventQueue.size( ), 2 );
        }


        @Test
        public void testGetReadyEvent( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );


                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

                Event ev0 = new Event( );
                Event ev1 = new Event( );
                selector.addReadyEvent( ev0 );
                selector.addReadyEvent( ev1 );

		Event evResult = null;
		try {
			evResult = selector.getReadyEvent( );
		} catch ( InterruptedException e ) { }
		Assert.assertSame( ev0, evResult );
                Assert.assertEquals( eventQueue.size( ), 1 );
        }


        @Test
        public void testProcessDispatchFailOps( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = selector.registerLock( handler0, EventMask.LOCK );
                GenericHandler handler1 = new GenericHandler( );
		selector.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.QREAD );
		selector.addMember( handle0, handler1 );
		selector.lock( handle0, handle0 );

		selector.processDispatchFailOps( handle0, new Event( ) );

		LockHandlerGroup group = handleGroupMap.get( handle0 );
		Assert.assertTrue( group.getEventHold( ) );
	}


        @Test
        public void testProcessDispatchFailRunning( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );

		GenericHandler handler = new GenericHandler( );
                selector.processDispatchFailRunning( handler, new Event( ) );

                Assert.assertTrue( eventStore.contains( handler ) );
        }


        @Test
        public void testProcessDispatchFailLocked( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );


		// queue and channel -- these do nothing
                selector.processDispatchFailLocked( new GenericHandler( ), new Handle( ), EventMask.QREAD, new Event( ) );
                selector.processDispatchFailLocked( new GenericHandler( ), new Handle( ), EventMask.CREAD, new Event( ) );


		// timer
                Field timerSelectorF = CompositeSelector.class.getDeclaredField( "timerSelector" );
                timerSelectorF.setAccessible( true );
                TimerSelector timerSelector = (TimerSelector)timerSelectorF.get( selector );
                Field expiredTimersF = TimerSelector.class.getDeclaredField( "expiredTimers" );
                expiredTimersF.setAccessible( true );
                Map<Handle,Integer> expiredTimers = (HashMap<Handle,Integer>)expiredTimersF.get( timerSelector );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = selector.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );
		timerSelector.timerExpired( handle0 );
                selector.processDispatchFailLocked( handler0, handle0, EventMask.TIMER, new Event( ) );
		int t = expiredTimers.get( handle0 );	
		Assert.assertEquals( t, 0 );	


		// lock, signal, error, blocking
                GenericHandler handler2 = new GenericHandler( );
                GenericHandler handler3 = new GenericHandler( );
                GenericHandler handler4 = new GenericHandler( );
                GenericHandler handler5 = new GenericHandler( );

                selector.processDispatchFailLocked( handler2, new Handle( ), EventMask.LOCK, new Event( ) );
                selector.processDispatchFailLocked( handler3, new Handle( ), EventMask.SIGNAL, new Event( ) );
                selector.processDispatchFailLocked( handler4, new Handle( ), EventMask.ERROR, new Event( ) );
                selector.processDispatchFailLocked( handler5, new Handle( ), EventMask.BLOCKING, new Event( ) );

		Assert.assertTrue( eventStore.contains( handler2 ) );
		Assert.assertTrue( eventStore.contains( handler3 ) );
		Assert.assertTrue( eventStore.contains( handler4 ) );
		Assert.assertTrue( eventStore.contains( handler5 ) );
        }


        @Test
        public void testShutdownInit( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

		selector.shutdownInit( );
	}


        @Test
        public void testShutdownFinal( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                JReactor_CompositeSelector jreactor = new JReactor_CompositeSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                CompositeSelector selector = new CompositeSelector( jreactor, tracker, true );

                selector.shutdownInit( );
                selector.shutdownFinal( );
        }

}
