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
import java.util.concurrent.ExecutorService;

import com.kineticfire.patterns.reactor.GenericHandler;
import com.kineticfire.patterns.reactor.GenericRunnable;
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


public class JReactor_Test {


        @Test
        public void testJReactorNoArg( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		JReactor jreactor = new JReactor( );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
		Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
		Assert.assertNull( dispatchThread );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
		CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
		Assert.assertNull( selector );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
		RunLockHandlerTracker runLockHandlerTracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );
		Assert.assertNull( runLockHandlerTracker );

                Field workerThreadPoolF = JReactor.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
		ExecutorService workerThreadPool = (ExecutorService)workerThreadPoolF.get( jreactor );
		Assert.assertNull( workerThreadPool );

                Field workerThreadNumF = JReactor.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
		Integer workerThreadNum = (Integer)workerThreadNumF.get( jreactor );
		Assert.assertEquals( workerThreadNum.intValue( ), 0 );

                Field terminationTimeoutFirstF = JReactor.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
		Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( jreactor );
		Assert.assertEquals( terminationTimeoutFirst.intValue( ), 0 );

                Field terminationTimeoutUnitsFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
		TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( jreactor );
		Assert.assertNull( terminationTimeoutUnitsFirst );

                Field terminationTimeoutLastF = JReactor.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
		Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( jreactor );
		Assert.assertEquals( terminationTimeoutLast.intValue( ), 0 );

                Field terminationTimeoutUnitsLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
		TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( jreactor );
		Assert.assertNull( terminationTimeoutUnitsLast );

                Field stopWorkerF = JReactor.class.getDeclaredField( "stopWorker" );
                stopWorkerF.setAccessible( true );
		Boolean stopWorker = (Boolean)stopWorkerF.get( jreactor );
		Assert.assertFalse( stopWorker );

                Field guardWorkerF = JReactor.class.getDeclaredField( "guardWorker" );
                guardWorkerF.setAccessible( true );
		Byte guardWorker = (Byte)guardWorkerF.get( jreactor );
		Assert.assertNull( guardWorker );



                Field criticalThreadPoolF = JReactor.class.getDeclaredField( "criticalThreadPool" );
                criticalThreadPoolF.setAccessible( true );
		ExecutorService criticalThreadPool = (ExecutorService)criticalThreadPoolF.get( jreactor );
		Assert.assertNull( criticalThreadPool );

                Field criticalErrorHandlerF = JReactor.class.getDeclaredField( "criticalErrorHandler" );
                criticalErrorHandlerF.setAccessible( true );
		Handler criticalErrorHandler = (Handler)criticalErrorHandlerF.get( jreactor );
		Assert.assertNull( criticalErrorHandler );

                Field criticalErrorHandleF = JReactor.class.getDeclaredField( "criticalErrorHandle" );
                criticalErrorHandleF.setAccessible( true );
		Handler criticalErrorHandle = (Handler)criticalErrorHandleF.get( jreactor );
		Assert.assertNull( criticalErrorHandle );

                Field terminationTimeoutCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalFirst" );
                terminationTimeoutCriticalFirstF.setAccessible( true );
                Integer terminationTimeoutCriticalFirst = (Integer)terminationTimeoutCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalFirst.intValue( ), 0 );

                Field terminationTimeoutUnitsCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalFirst" );
                terminationTimeoutUnitsCriticalFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalFirst = (TimeUnit)terminationTimeoutUnitsCriticalFirstF.get( jreactor );
                Assert.assertNull( terminationTimeoutUnitsCriticalFirst );

                Field terminationTimeoutCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalLast" );
                terminationTimeoutCriticalLastF.setAccessible( true );
                Integer terminationTimeoutCriticalLast = (Integer)terminationTimeoutCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalLast.intValue( ), 0 );

                Field terminationTimeoutUnitsCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalLast" );
                terminationTimeoutUnitsCriticalLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalLast = (TimeUnit)terminationTimeoutUnitsCriticalLastF.get( jreactor );
                Assert.assertNull( terminationTimeoutUnitsCriticalLast );

                Field stopCriticalF = JReactor.class.getDeclaredField( "stopCritical" );
                stopCriticalF.setAccessible( true );
                Boolean stopCritical = (Boolean)stopCriticalF.get( jreactor );
                Assert.assertFalse( stopCritical );

                Field guardCriticalF = JReactor.class.getDeclaredField( "guardCritical" );
                guardCriticalF.setAccessible( true );
                Byte guardCritical = (Byte)guardCriticalF.get( jreactor );
                Assert.assertNull( guardCritical );

        }


        @Test
        public void testJReactorHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( handler );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
                Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
                Assert.assertNull( dispatchThread );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Assert.assertNotNull( selector );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker runLockHandlerTracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );
                Assert.assertNotNull( runLockHandlerTracker );

                Field workerThreadPoolF = JReactor.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                ExecutorService workerThreadPool = (ExecutorService)workerThreadPoolF.get( jreactor );
                Assert.assertNotNull( workerThreadPool );

                Field workerThreadNumF = JReactor.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                Integer workerThreadNum = (Integer)workerThreadNumF.get( jreactor );
                Assert.assertEquals( workerThreadNum.intValue( ), 1 );

                Field terminationTimeoutFirstF = JReactor.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutFirst.intValue( ), 3 );

                Field terminationTimeoutUnitsFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = JReactor.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutLast.intValue( ), 3 );

                Field terminationTimeoutUnitsLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );

                Field stopWorkerF = JReactor.class.getDeclaredField( "stopWorker" );
                stopWorkerF.setAccessible( true );
                Boolean stopWorker = (Boolean)stopWorkerF.get( jreactor );
                Assert.assertFalse( stopWorker );

                Field guardWorkerF = JReactor.class.getDeclaredField( "guardWorker" );
                guardWorkerF.setAccessible( true );
                Byte guardWorker = (Byte)guardWorkerF.get( jreactor );
                Assert.assertNotNull( guardWorker );

                Field criticalThreadPoolF = JReactor.class.getDeclaredField( "criticalThreadPool" );
                criticalThreadPoolF.setAccessible( true );
                ExecutorService criticalThreadPool = (ExecutorService)criticalThreadPoolF.get( jreactor );
                Assert.assertNull( criticalThreadPool );

                Field criticalErrorHandlerF = JReactor.class.getDeclaredField( "criticalErrorHandler" );
                criticalErrorHandlerF.setAccessible( true );
                Handler criticalErrorHandler = (Handler)criticalErrorHandlerF.get( jreactor );
                Assert.assertSame( criticalErrorHandler, handler );

                Field criticalErrorHandleF = JReactor.class.getDeclaredField( "criticalErrorHandle" );
                criticalErrorHandleF.setAccessible( true );
                Handle criticalErrorHandle = (Handle)criticalErrorHandleF.get( jreactor );
                Assert.assertNotNull( criticalErrorHandle );

                Field terminationTimeoutCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalFirst" );
                terminationTimeoutCriticalFirstF.setAccessible( true );
                Integer terminationTimeoutCriticalFirst = (Integer)terminationTimeoutCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalFirst.intValue( ), 3 );

                Field terminationTimeoutUnitsCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalFirst" );
                terminationTimeoutUnitsCriticalFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalFirst = (TimeUnit)terminationTimeoutUnitsCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalFirst, TimeUnit.SECONDS );

                Field terminationTimeoutCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalLast" );
                terminationTimeoutCriticalLastF.setAccessible( true );
                Integer terminationTimeoutCriticalLast = (Integer)terminationTimeoutCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalLast.intValue( ), 3 );

                Field terminationTimeoutUnitsCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalLast" );
                terminationTimeoutUnitsCriticalLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalLast = (TimeUnit)terminationTimeoutUnitsCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalLast, TimeUnit.SECONDS );

                Field stopCriticalF = JReactor.class.getDeclaredField( "stopCritical" );
                stopCriticalF.setAccessible( true );
                Boolean stopCritical = (Boolean)stopCriticalF.get( jreactor );
                Assert.assertFalse( stopCritical );

                Field guardCriticalF = JReactor.class.getDeclaredField( "guardCritical" );
                guardCriticalF.setAccessible( true );
                Byte guardCritical = (Byte)guardCriticalF.get( jreactor );
                Assert.assertNotNull( guardCritical );
        }



        @Test
        public void testJReactorIntegerHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
                Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
                Assert.assertNull( dispatchThread );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Assert.assertNotNull( selector );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker runLockHandlerTracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );
                Assert.assertNotNull( runLockHandlerTracker );

                Field workerThreadPoolF = JReactor.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                ExecutorService workerThreadPool = (ExecutorService)workerThreadPoolF.get( jreactor );
                Assert.assertNotNull( workerThreadPool );

                Field workerThreadNumF = JReactor.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                Integer workerThreadNum = (Integer)workerThreadNumF.get( jreactor );
                Assert.assertEquals( workerThreadNum.intValue( ), 3 );

                Field terminationTimeoutFirstF = JReactor.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutFirst.intValue( ), 3 );

                Field terminationTimeoutUnitsFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = JReactor.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutLast.intValue( ), 3 );

                Field terminationTimeoutUnitsLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );

                Field stopWorkerF = JReactor.class.getDeclaredField( "stopWorker" );
                stopWorkerF.setAccessible( true );
                Boolean stopWorker = (Boolean)stopWorkerF.get( jreactor );
                Assert.assertFalse( stopWorker );

                Field guardWorkerF = JReactor.class.getDeclaredField( "guardWorker" );
                guardWorkerF.setAccessible( true );
                Byte guardWorker = (Byte)guardWorkerF.get( jreactor );
                Assert.assertNotNull( guardWorker );

                Field criticalThreadPoolF = JReactor.class.getDeclaredField( "criticalThreadPool" );
                criticalThreadPoolF.setAccessible( true );
                ExecutorService criticalThreadPool = (ExecutorService)criticalThreadPoolF.get( jreactor );
                Assert.assertNull( criticalThreadPool );

                Field criticalErrorHandlerF = JReactor.class.getDeclaredField( "criticalErrorHandler" );
                criticalErrorHandlerF.setAccessible( true );
                Handler criticalErrorHandler = (Handler)criticalErrorHandlerF.get( jreactor );
                Assert.assertSame( criticalErrorHandler, handler );

                Field criticalErrorHandleF = JReactor.class.getDeclaredField( "criticalErrorHandle" );
                criticalErrorHandleF.setAccessible( true );
                Handle criticalErrorHandle = (Handle)criticalErrorHandleF.get( jreactor );
                Assert.assertNotNull( criticalErrorHandle );

                Field terminationTimeoutCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalFirst" );
                terminationTimeoutCriticalFirstF.setAccessible( true );
                Integer terminationTimeoutCriticalFirst = (Integer)terminationTimeoutCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalFirst.intValue( ), 3 );

                Field terminationTimeoutUnitsCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalFirst" );
                terminationTimeoutUnitsCriticalFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalFirst = (TimeUnit)terminationTimeoutUnitsCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalFirst, TimeUnit.SECONDS );

                Field terminationTimeoutCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalLast" );
                terminationTimeoutCriticalLastF.setAccessible( true );
                Integer terminationTimeoutCriticalLast = (Integer)terminationTimeoutCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalLast.intValue( ), 3 );

                Field terminationTimeoutUnitsCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalLast" );
                terminationTimeoutUnitsCriticalLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalLast = (TimeUnit)terminationTimeoutUnitsCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalLast, TimeUnit.SECONDS );

                Field stopCriticalF = JReactor.class.getDeclaredField( "stopCritical" );
                stopCriticalF.setAccessible( true );
                Boolean stopCritical = (Boolean)stopCriticalF.get( jreactor );
                Assert.assertFalse( stopCritical );

                Field guardCriticalF = JReactor.class.getDeclaredField( "guardCritical" );
                guardCriticalF.setAccessible( true );
                Byte guardCritical = (Byte)guardCriticalF.get( jreactor );
                Assert.assertNotNull( guardCritical );
	}

        @Test
        public void testJReactorIntegerHandlerBoolean( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler, false );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
                Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
                Assert.assertNull( dispatchThread );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Assert.assertNotNull( selector );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker runLockHandlerTracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );
                Assert.assertNotNull( runLockHandlerTracker );

                Field workerThreadPoolF = JReactor.class.getDeclaredField( "workerThreadPool" );
                workerThreadPoolF.setAccessible( true );
                ExecutorService workerThreadPool = (ExecutorService)workerThreadPoolF.get( jreactor );
                Assert.assertNotNull( workerThreadPool );

                Field workerThreadNumF = JReactor.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                Integer workerThreadNum = (Integer)workerThreadNumF.get( jreactor );
                Assert.assertEquals( workerThreadNum.intValue( ), 3 );

                Field terminationTimeoutFirstF = JReactor.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutFirst.intValue( ), 3 );

                Field terminationTimeoutUnitsFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.SECONDS );

                Field terminationTimeoutLastF = JReactor.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutLast.intValue( ), 3 );

                Field terminationTimeoutUnitsLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.SECONDS );

                Field stopWorkerF = JReactor.class.getDeclaredField( "stopWorker" );
                stopWorkerF.setAccessible( true );
                Boolean stopWorker = (Boolean)stopWorkerF.get( jreactor );
                Assert.assertFalse( stopWorker );

                Field guardWorkerF = JReactor.class.getDeclaredField( "guardWorker" );
                guardWorkerF.setAccessible( true );
                Byte guardWorker = (Byte)guardWorkerF.get( jreactor );
                Assert.assertNotNull( guardWorker );

                Field criticalThreadPoolF = JReactor.class.getDeclaredField( "criticalThreadPool" );
                criticalThreadPoolF.setAccessible( true );
                ExecutorService criticalThreadPool = (ExecutorService)criticalThreadPoolF.get( jreactor );
                Assert.assertNull( criticalThreadPool );

                Field criticalErrorHandlerF = JReactor.class.getDeclaredField( "criticalErrorHandler" );
                criticalErrorHandlerF.setAccessible( true );
                Handler criticalErrorHandler = (Handler)criticalErrorHandlerF.get( jreactor );
                Assert.assertSame( criticalErrorHandler, handler );

                Field criticalErrorHandleF = JReactor.class.getDeclaredField( "criticalErrorHandle" );
                criticalErrorHandleF.setAccessible( true );
                Handle criticalErrorHandle = (Handle)criticalErrorHandleF.get( jreactor );
                Assert.assertNotNull( criticalErrorHandle );

                Field terminationTimeoutCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalFirst" );
                terminationTimeoutCriticalFirstF.setAccessible( true );
                Integer terminationTimeoutCriticalFirst = (Integer)terminationTimeoutCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalFirst.intValue( ), 3 );

                Field terminationTimeoutUnitsCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalFirst" );
                terminationTimeoutUnitsCriticalFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalFirst = (TimeUnit)terminationTimeoutUnitsCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalFirst, TimeUnit.SECONDS );

                Field terminationTimeoutCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalLast" );
                terminationTimeoutCriticalLastF.setAccessible( true );
                Integer terminationTimeoutCriticalLast = (Integer)terminationTimeoutCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalLast.intValue( ), 3 );

                Field terminationTimeoutUnitsCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalLast" );
                terminationTimeoutUnitsCriticalLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalLast = (TimeUnit)terminationTimeoutUnitsCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalLast, TimeUnit.SECONDS );

                Field stopCriticalF = JReactor.class.getDeclaredField( "stopCritical" );
                stopCriticalF.setAccessible( true );
                Boolean stopCritical = (Boolean)stopCriticalF.get( jreactor );
                Assert.assertFalse( stopCritical );

                Field guardCriticalF = JReactor.class.getDeclaredField( "guardCritical" );
                guardCriticalF.setAccessible( true );
                Byte guardCritical = (Byte)guardCriticalF.get( jreactor );
                Assert.assertNotNull( guardCritical );
	}


        @Test
        public void testDispatchSuccess( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

		jreactor.dispatch( );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
                Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
                Assert.assertNotNull( dispatchThread );
	}


        @Test
        public void testDispatchMulti( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

                jreactor.dispatch( );
                jreactor.dispatch( );
                jreactor.dispatch( );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
                Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
                Assert.assertNotNull( dispatchThread );
        }


        @Test
        public void testDispatchShutdown( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

		jreactor.shutdown( );
		jreactor.dispatch( );

                Field dispatchThreadF = JReactor.class.getDeclaredField( "dispatchThread" );
                dispatchThreadF.setAccessible( true );
                Thread dispatchThread = (Thread)dispatchThreadF.get( jreactor );
                Assert.assertNull( dispatchThread );
        }


        @Test
        public void testShutdown( ) {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

		Assert.assertFalse( jreactor.isShutdown( ) );

		jreactor.dispatch( );
                jreactor.shutdown( );

		Assert.assertTrue( jreactor.isShutdown( ) );
        }


        @Test
        public void testIsDispatching( ) {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

		Assert.assertFalse( jreactor.isDispatching( ) );

                jreactor.dispatch( );

		Assert.assertTrue( jreactor.isDispatching( ) );

                jreactor.shutdown( );

		Assert.assertFalse( jreactor.isDispatching( ) );
        }


        @Test
        public void testIsShutdown( ) {
                GenericHandler handler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, handler );

                Assert.assertFalse( jreactor.isShutdown( ) );

                jreactor.dispatch( );
                jreactor.shutdown( );

                Assert.assertTrue( jreactor.isShutdown( ) );
        }


        @Test
        public void testIsRegisteredHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

		GenericHandler handler = new GenericHandler( );
		Handle handle1 = jreactor.registerLock( handler, EventMask.LOCK );
		Assert.assertTrue( jreactor.isRegistered( handle1 ) );

		Handle handle2 = new Handle( );
		Assert.assertFalse( jreactor.isRegistered( handle2 ) );
	}


        @Test
        public void testInterestOpsGet( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                GenericHandler handler = new GenericHandler( );
                Handle handle1 = jreactor.registerLock( handler, EventMask.LOCK );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.LOCK );
 
                Handle handle2 = new Handle( );
                Assert.assertEquals( jreactor.interestOps( handle2 ), -1 );
        }


        @Test
        public void testInterestOpsSetSuccess( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
		Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

		// NOOP -> NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );
		jreactor.interestOps( handle0, EventMask.NOOP, errorHandle );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

		// NOOP -> QREAD
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.NOOP );
                jreactor.interestOps( handle1, EventMask.QREAD, errorHandle );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.QREAD );

		// QREAD -> NOOP 
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.QREAD );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );
                jreactor.interestOps( handle2, EventMask.NOOP, errorHandle );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.NOOP );

		// QREAD -> QREAD 
                GenericHandler handler3 = new GenericHandler( );
                Handle handle3 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.QREAD );
                Assert.assertEquals( jreactor.interestOps( handle3 ), EventMask.QREAD );
                jreactor.interestOps( handle3, EventMask.QREAD, errorHandle );
                Assert.assertEquals( jreactor.interestOps( handle3 ), EventMask.QREAD );
        }


        @Test
        public void testInterestOpsSetRunning( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
		tracker.submitRunning( handler1, new HandlerAdapter( ) );

                // NOOP -> QREAD
                jreactor.interestOps( handle1, EventMask.QREAD, errorHandle );
		HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
		Assert.assertEquals( commands.size( ), 1 );
		MessageBlock mb = commands.get( 0 );
		Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
		mb = mb.getNext( );
		Assert.assertEquals( mb.getMessageString( ).compareTo( "set" ), 0 );
		mb = mb.getNext( );
		Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
		mb = mb.getNext( );
		Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
		mb = mb.getNext( );
		Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
		mb = mb.getNext( );
		Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.QREAD );
		mb = mb.getNext( );
		Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
		mb = mb.getNext( );
		Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testInterestOpsSetLocked( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
		jreactor.addMember( handleA, handler1, errorHandle );
		jreactor.lock( handleA, errorHandle );


                // NOOP -> QREAD
                jreactor.interestOps( handle1, EventMask.QREAD, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 2 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "nokey" ), 0 );
                mb = commands.get( 1 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "set" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.QREAD );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testInterestOpsSetLockedHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );


                // NOOP -> QREAD
                jreactor.interestOps( handleA, handle1, EventMask.QREAD, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "set" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lockHandle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handleA );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.QREAD );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
	}


        @Test
        public void testInterestOpsOrSetSuccess( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD | CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );

                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD );
                jreactor.interestOpsOr( handle1, EventMask.CWRITE, errorHandle );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD | EventMask.CWRITE );
        }


        @Test
        public void testInterestOpsOrRunning( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD | CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );
                tracker.submitRunning( handler1, new HandlerAdapter( ) );

                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD );
                jreactor.interestOpsOr( handle1, EventMask.CWRITE, errorHandle );
		HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );


                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "or" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.CWRITE );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testInterestOpsOrLocked( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD | CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );


                // CREAD -> CREAD | CWRITE
                jreactor.interestOpsOr( handle1, EventMask.CWRITE, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 2 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "nokey" ), 0 );
                mb = commands.get( 1 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "or" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.CWRITE );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }

        @Test
        public void testInterestOpsOrLockedHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD | CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );

                jreactor.interestOpsOr( handleA, handle1, EventMask.CWRITE, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "or" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lockHandle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handleA );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.CWRITE );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
	}


        @Test
        public void testInterestOpsAndSetSuccess( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD & CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );

                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD );
                jreactor.interestOpsAnd( handle1, EventMask.CWRITE, errorHandle );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.NOOP );
        }


        @Test
        public void testInterestOpsAndRunning( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD & CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );
                tracker.submitRunning( handler1, new HandlerAdapter( ) );

                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD );
                jreactor.interestOpsAnd( handle1, EventMask.CWRITE, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "and" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.CWRITE );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testInterestOpsAndLocked( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD | CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );

                // CREAD -> CREAD & CWRITE
                jreactor.interestOpsAnd( handle1, EventMask.CWRITE, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 2 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "nokey" ), 0 );
                mb = commands.get( 1 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "and" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.CWRITE );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
	}


        @Test
        public void testInterestOpsAndLockedHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // CREAD -> CREAD & CWRITE
                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );

                jreactor.interestOpsAnd( handleA, handle1, EventMask.CWRITE, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );

                Assert.assertEquals( mb.getMessageString( ).compareTo( "interestOps" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "and" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lockHandle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handleA );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "ops" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageInteger( ).intValue( ), EventMask.CWRITE );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
	}


        @Test
        public void testDeregisterHandle1Success( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // 1 handle
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
		jreactor.deregister( handle0, errorHandle );
		Assert.assertFalse( jreactor.isRegistered( handle0 ) );
        }


        @Test
        public void testDeregisterHandle2Success( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // 2 handle
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                jreactor.deregister( handle0, errorHandle );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
                Assert.assertTrue( jreactor.isRegistered( handle1 ) );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
        }


        @Test
        public void testDeregisterHandleRunning( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                tracker.submitRunning( handler1, new HandlerAdapter( ) );

                // NOOP -> QREAD
                jreactor.deregister( handle1, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "deregister" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void tesDeregisterHandleLocked( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );


                jreactor.deregister( handle1, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 2 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "nokey" ), 0 );
                mb = commands.get( 1 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "deregister" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testDeregisterHandleLockedHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );

                jreactor.deregister( handleA, handle1, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "deregister" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lockHandle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handleA );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
	}


        @Test
        public void testHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

		Assert.assertSame( jreactor.handler( handle1 ), handler1 );


		// handle not registered
		Assert.assertNull( jreactor.handler( new Handle( ) ) );	
	}


        @Test
        public void testIsRegisteredHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

                Assert.assertTrue( jreactor.isRegistered( handler1 ) );


                // handler not registered
                Assert.assertFalse( jreactor.isRegistered( new GenericHandler( ) ) );
        }


        @Test
        public void testHandles( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


		// 0 handles
		Assert.assertEquals( jreactor.handles( new GenericHandler( ) ).size( ), 0 );


		// 1 handle
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
		Assert.assertEquals( jreactor.handles( handler1 ).size( ), 1 );

		// 2 handles
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                Handle handle3 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
		Assert.assertEquals( jreactor.handles( handler2 ).size( ), 2 );
        }


        @Test
        public void testDeregisterHandler1Success( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // 1 handle
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                jreactor.deregister( handler0, errorHandle );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
                Assert.assertFalse( jreactor.isRegistered( handler0 ) );
        }


        @Test
        public void testDeregisterHandler2Success( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // 2 handles
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler0, EventMask.NOOP );
                jreactor.deregister( handler0, errorHandle );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
                Assert.assertFalse( jreactor.isRegistered( handle1 ) );
                Assert.assertFalse( jreactor.isRegistered( handler0 ) );
        }


        @Test
        public void testDeregisterHandlerRunning( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                tracker.submitRunning( handler1, new HandlerAdapter( ) );

                jreactor.deregister( handler1, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "deregister" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handler" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handler)mb.getMessage( ), handler1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }



        @Test
        public void testDeregisterHandlerLocked( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field trackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                trackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)trackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
                jreactor.addMember( handleA, handler1, errorHandle );
                jreactor.lock( handleA, errorHandle );


                jreactor.deregister( handler1, errorHandle );
                HandlerAdapter adapter = tracker.getHandlerAdapter( handler1 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 2 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "nokey" ), 0 );
                mb = commands.get( 1 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "deregister" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "handler" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handler)mb.getMessage( ), handler1 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testRegisterQueue( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );


                GenericHandler handler1 = new GenericHandler( );
		MessageQueue queue = new LinkedBlockingMessageQueue( ); 
                Handle handle1 = jreactor.registerQueue( queue, handler1, EventMask.NOOP );
		Assert.assertTrue( jreactor.isRegistered( handler1 ) );
		Assert.assertTrue( jreactor.isRegisteredQueue( handle1 ) );
		Assert.assertTrue( jreactor.isRegisteredQueue( queue ) );
		Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.NOOP );

                GenericHandler handler2 = new GenericHandler( );
                MessageQueue queue2 = new LinkedBlockingMessageQueue( ); 
                Handle handle2 = jreactor.registerQueue( queue2, handler2, EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegistered( handler2 ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( handle2 ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( queue2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );

                MessageQueue queue3 = new LinkedBlockingMessageQueue( ); 
                Handle handle3 = jreactor.registerQueue( queue3, handler2, EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegistered( handler2 ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( handle2 ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( queue2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegisteredQueue( handle3 ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( queue3 ) );
                Assert.assertEquals( jreactor.interestOps( handle3 ), EventMask.QREAD );
	}


        @Test
        public void testIsRegisteredQueueQueue( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );


                GenericHandler handler1 = new GenericHandler( );
                MessageQueue queue = new LinkedBlockingMessageQueue( ); 
                Handle handle1 = jreactor.registerQueue( queue, handler1, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredQueue( queue ) );

		Assert.assertFalse( jreactor.isRegisteredQueue( new LinkedBlockingMessageQueue( ) ) );
	}


        @Test
        public void testIsRegisteredQueueHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );


                GenericHandler handler1 = new GenericHandler( );
                MessageQueue queue = new LinkedBlockingMessageQueue( );
                Handle handle1 = jreactor.registerQueue( queue, handler1, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredQueue( handle1 ) );

                Assert.assertFalse( jreactor.isRegisteredQueue( errorHandle ) );

                Assert.assertFalse( jreactor.isRegisteredQueue( new Handle( ) ) );
        }


        @Test
        public void testRegisterChannel( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler handler0 = new GenericHandler( );
                SocketChannel channel0 = null;
                try {
                        channel0 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle0 = jreactor.registerChannel( channel0, handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle0 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( channel0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );


                GenericHandler handler8 = new GenericHandler( );
                SocketChannel channel8 = null;
                try {
                        channel8 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle8 = jreactor.registerChannel( channel8, handler8, EventMask.CWRITE );
                Assert.assertTrue( jreactor.isRegistered( handler8 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle8 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( channel8 ) );
                Assert.assertEquals( jreactor.interestOps( handle8 ), EventMask.CWRITE );

                GenericHandler handler9 = new GenericHandler( );
                SocketChannel channel9 = null;
                try {
                        channel9 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle9 = jreactor.registerChannel( channel9, handler9, EventMask.CWRITE | EventMask.CREAD );
                Assert.assertTrue( jreactor.isRegistered( handler9 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle9 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( channel9 ) );
                Assert.assertEquals( jreactor.interestOps( handle9 ), EventMask.CWRITE | EventMask.CREAD );


                GenericHandler handler1 = new GenericHandler( );
                SocketChannel channel = null;
                try {
                        channel = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle1 = jreactor.registerChannel( channel, handler1, EventMask.CREAD );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle1 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( channel ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD );


                SocketChannel channel2 = null;
                try {
                        channel2 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle2 = jreactor.registerChannel( channel2, handler1, EventMask.CREAD );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle1 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle2 ) );
                Assert.assertTrue( jreactor.isRegisteredChannel( channel2 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.CREAD );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.CREAD );

        }


        @Test
        public void testIsRegisteredChannelChannel( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler handler0 = new GenericHandler( );
                SocketChannel channel0 = null;
                try {
                        channel0 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle0 = jreactor.registerChannel( channel0, handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredChannel( channel0 ) );

                SocketChannel channel1 = null;
                try {
                        channel1 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Assert.assertFalse( jreactor.isRegisteredChannel( channel1 ) );
        }


        @Test
        public void testIsRegisteredChannelHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                SocketChannel channel0 = null;
                try {
                        channel0 = SocketChannel.open( );
                } catch ( IOException e ) { }
                Handle handle0 = jreactor.registerChannel( channel0, handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredChannel( handle0 ) );

                Assert.assertFalse( jreactor.isRegisteredChannel( errorHandle ) );

                Assert.assertFalse( jreactor.isRegisteredChannel( new Handle( ) ) );
        }


        @Test
        public void testRegisterTimerOnceDate( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );


                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = jreactor.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                Date firstTime1 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle1 = jreactor.registerTimerOnce( firstTime1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );

                // 2nd event source and handle
                Date firstTime2 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle2 = jreactor.registerTimerOnce( firstTime2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.TIMER );
        }


        @Test
        public void testRegisterTimerOnceDelay( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                long delay0 = 1000;
                Handle handle0 = jreactor.registerTimerOnce( delay0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                long delay1 = 1000;
                Handle handle1 = jreactor.registerTimerOnce( delay1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );

                // 2nd event source and handle
                long delay2 = 1000;
                Handle handle2 = jreactor.registerTimerOnce( delay2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.TIMER );
        }


        @Test
        public void testRegisterTimerFixedDelayDate( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period0 = 1000;
                Handle handle0 = jreactor.registerTimerFixedDelay( firstTime0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                Date firstTime1 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period1 = 1000;
                Handle handle1 = jreactor.registerTimerFixedDelay( firstTime1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );

                // 2nd event source and handle
                Date firstTime2 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period2 = 1000;
                Handle handle2 = jreactor.registerTimerFixedDelay( firstTime2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.TIMER );
	}


        @Test
        public void testRegisterTimerFixedDelayLong( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );


                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                long delay0 = 1000;
                long period0 = 1000;
                Handle handle0 = jreactor.registerTimerFixedDelay( delay0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                long delay1 = 1000;
                long period1 = 1000;
                Handle handle1 = jreactor.registerTimerFixedDelay( delay1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );

                // 2nd event source and handle
                long delay2 = 1000;
                long period2 = 1000;
                Handle handle2 = jreactor.registerTimerFixedDelay( delay2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.TIMER );
	}


        @Test
        public void testRegisterTimerFixedRateDate( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period0 = 1000;
                Handle handle0 = jreactor.registerTimerFixedRate( firstTime0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                Date firstTime1 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period1 = 1000;
                Handle handle1 = jreactor.registerTimerFixedRate( firstTime1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );

                // EventMask = TIMER
                Date firstTime2 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                long period2 = 1000;
                Handle handle2 = jreactor.registerTimerFixedRate( firstTime2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.TIMER );
	}


        @Test
        public void testRegisterFixedRateLong( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                long delay0 = 1000;
                long period0 = 1000;
                Handle handle0 = jreactor.registerTimerFixedRate( delay0, period0, handler0, EventMask.NOOP );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = TIMER
                GenericHandler handler1 = new GenericHandler( );
                long delay1 = 1000;
                long period1 = 1000;
                Handle handle1 = jreactor.registerTimerFixedRate( delay1, period1, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );

                // EventMask = TIMER
                long delay2 = 1000;
                long period2 = 1000;
                Handle handle2 = jreactor.registerTimerFixedRate( delay2, period2, handler1, EventMask.TIMER );
                // assuming these will be evaluated before system time plus 10 seconds from creation of Date object
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.TIMER );
                Assert.assertTrue( jreactor.isRegisteredTimer( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.TIMER );
	}


        @Test
        public void testIsRegisteredTimer( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = jreactor.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );

                Assert.assertTrue( jreactor.isRegisteredTimer( handle0 ) );

                Assert.assertFalse( jreactor.isRegisteredTimer( errorHandle ) );

                Assert.assertFalse( jreactor.isRegisteredTimer( new Handle( ) ) );
        }


        @Test
        public void testCancelTimerSingleNotRegistered( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Handle handle = new Handle( );
                Assert.assertFalse( jreactor.cancelTimer( handle ) );
                Assert.assertFalse( jreactor.cancelTimer( handle ) );
        }


        @Test
        public void testCancelTimerSingleDeregister( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = jreactor.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );

                Assert.assertTrue( jreactor.isRegistered( handle0 ) );
                Assert.assertTrue( jreactor.cancelTimer( handle0 ) );
                Assert.assertFalse( jreactor.cancelTimer( handle0 ) );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
        }


        @Test
        public void testCancelTimerSingleDeregisterLocked( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Date firstTime0 = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle0 = jreactor.registerTimerOnce( firstTime0, handler0, EventMask.NOOP );

                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
		jreactor.addMember( handleA, handler0, errorHandle );
		jreactor.lock( handleA, errorHandle );

                Assert.assertTrue( jreactor.isLocked( handleA ) );
                Assert.assertTrue( jreactor.isRegistered( handle0 ) );
                Assert.assertTrue( jreactor.cancelTimer( handleA, handle0 ) );
                Assert.assertFalse( jreactor.cancelTimer( handleA, handle0 ) );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
        }



        @Test
        public void testRegisterLock( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredLock( handle0 ) );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = LOCK
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerLock( handler1, EventMask.LOCK );
                Assert.assertTrue( jreactor.isRegisteredLock( handle1 ) );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.LOCK );

                // 2nd event source and handle
		MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = jreactor.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegisteredLock( handle1 ) );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.LOCK );
                Assert.assertTrue( jreactor.isRegistered( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );
        }


        @Test
        public void testAddMember( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
		jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                GenericHandler handler2 = new GenericHandler( );
		jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
		jreactor.addMember( handle0, handler1, errorHandle );
		Assert.assertTrue( jreactor.isMember( handle0, handler1 ) );
		jreactor.addMember( handle0, handler2, errorHandle );
		Assert.assertTrue( jreactor.isMember( handle0, handler1 ) );
		Assert.assertTrue( jreactor.isMember( handle0, handler2 ) );
	}


        @Test
        public void testRemoveMember( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
		jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                GenericHandler handler2 = new GenericHandler( );
		jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                jreactor.removeMember( handle0, handler1, errorHandle );
                Assert.assertFalse( jreactor.isMember( handle0, handler1 ) );
                jreactor.addMember( handle0, handler1, errorHandle );
                jreactor.addMember( handle0, handler2, errorHandle );
                jreactor.removeMember( handle0, handler1, errorHandle );
                jreactor.removeMember( handle0, handler2, errorHandle );
                Assert.assertFalse( jreactor.isMember( handle0, handler1 ) );
                Assert.assertFalse( jreactor.isMember( handle0, handler2 ) );
        }


        @Test
        public void testIsRegisteredLock( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
		Assert.assertTrue( jreactor.isRegisteredLock( handle0 ) );

		Handle handle1 = new Handle( );
		Assert.assertFalse( jreactor.isRegisteredLock( handle1 ) );
	}


        @Test
        public void testIsMember( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );

                GenericHandler handler1 = new GenericHandler( );
		jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                Assert.assertTrue( jreactor.isMember( handle0, handler1 ) );
                GenericHandler handler2 = new GenericHandler( );
		jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );
                Assert.assertTrue( jreactor.isMember( handle0, handler1 ) );
                Assert.assertTrue( jreactor.isMember( handle0, handler2 ) );

		Assert.assertFalse( jreactor.isMember( handle0, new GenericHandler( ) ) );
        }


        @Test
        public void testIsLockedMemberOpen( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
		RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                // open
                Assert.assertFalse( jreactor.isLockedMember( handle0, handler1 ) );
                Assert.assertFalse( jreactor.isLockedMember( handle0, handler2 ) );
        }


        @Test
        public void testIsLockedMemberPending( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                jreactor.lock( handle0, errorHandle );

                // pending
                Assert.assertFalse( jreactor.isLockedMember( handle0, handler1 ) );
                Assert.assertFalse( jreactor.isLockedMember( handle0, handler2 ) );
        }


        @Test
        public void testIsLockedMemberLocked( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                jreactor.lock( handle0, errorHandle );

                // locked
                Assert.assertTrue( jreactor.isLockedMember( handle0, handler1 ) );
                Assert.assertTrue( jreactor.isLockedMember( handle0, handler2 ) );
        }



        @Test
        public void testIsLockedMemberNotRegistered( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                // test when handle is not a member
                GenericHandler handlerA = new GenericHandler( );
                Assert.assertFalse( jreactor.isLockedMember( handle0, handlerA ) );
        }


        @Test
        public void testLockToLock( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                jreactor.lock( handle0, errorHandle );

                // locked
                Assert.assertTrue( jreactor.isLocked( handle0 ) );
                Assert.assertTrue( jreactor.isLocked( handler1 ) );
                Assert.assertTrue( jreactor.isLocked( handler2 ) );
        }


        @Test
        public void testLockToPending( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                jreactor.lock( handle0, errorHandle );

                // pending
                Assert.assertTrue( jreactor.isPending( handle0 ) );
                Assert.assertTrue( jreactor.isPending( handler1 ) );
                Assert.assertTrue( jreactor.isLocked( handler2 ) );
        }


        @Test
        public void testLockToQueued( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                tracker.submitRunning( handler0, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                jreactor.lock( handle0, errorHandle );

                HandlerAdapter adapter = tracker.getHandlerAdapter( handler0 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lock" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lockHandle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testUnlockSuccess( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                jreactor.lock( handle0, handle0 );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
		HandlerAdapter adapter = selector.getLockedHandlerAdapter( handle0 );
                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );

		jreactor.resumeSelection( handler0, handle0, commands );

                jreactor.unlock( handle0, handle0 );

                Assert.assertFalse( jreactor.isLocked( handle0 ) );
                Assert.assertFalse( jreactor.isLocked( handler1 ) );
                Assert.assertFalse( jreactor.isLocked( handler2 ) );
                Assert.assertTrue( jreactor.isOpen( handler1 ) );
                Assert.assertTrue( jreactor.isOpen( handler2 ) );
        }



        @Test
        public void testUnlockToQueued( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                jreactor.lock( handle0, errorHandle );
                tracker.submitRunning( handler0, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                jreactor.unlock( handle0, errorHandle );

                HandlerAdapter adapter = tracker.getHandlerAdapter( handler0 );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List<MessageBlock> commands = (LinkedList<MessageBlock>)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 1 );
                MessageBlock mb = commands.get( 0 );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "unlock" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "lockHandle" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), handle0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "errorHandle" ), 0  );
                mb = mb.getNext( );
                Assert.assertSame( (Handle)mb.getMessage( ), errorHandle );
        }


        @Test
        public void testIsOpenHandler( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                Assert.assertTrue( jreactor.isOpen( handler1 ) );
                Assert.assertTrue( jreactor.isOpen( handler2 ) );
	}


        @Test
        public void testIsOpenHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                Assert.assertTrue( jreactor.isOpen( handle0 ) );
        }


        @Test
        public void testIsPendingHandler( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                jreactor.lock( handle0, errorHandle );

                // pending
                Assert.assertTrue( jreactor.isPending( handler1 ) );
                Assert.assertTrue( jreactor.isLocked( handler2 ) );
        }


        @Test
        public void testIsPendingHandle( )  throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                Field runLockHandlerTrackerF = JReactor.class.getDeclaredField( "runLockHandlerTracker" );
                runLockHandlerTrackerF.setAccessible( true );
                RunLockHandlerTracker tracker = (RunLockHandlerTracker)runLockHandlerTrackerF.get( jreactor );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                tracker.submitRunning( handler1, new HandlerAdapter( jreactor, handler1, handle1, EventMask.QREAD ) );
                jreactor.lock( handle0, errorHandle );

                // pending
                Assert.assertTrue( jreactor.isPending( handle0 ) );
        }


        @Test
        public void testIsLockedHandler( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                jreactor.lock( handle0, errorHandle );

                // locked
                Assert.assertTrue( jreactor.isLocked( handler1 ) );
                Assert.assertTrue( jreactor.isLocked( handler2 ) );
        }


        @Test
        public void testIsLockedHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerLock( handler0, EventMask.NOOP );
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler1, EventMask.NOOP );
                jreactor.addMember( handle0, handler1, errorHandle );
                GenericHandler handler2 = new GenericHandler( );
                Handle handle2 = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), handler2, EventMask.NOOP );
                jreactor.addMember( handle0, handler2, errorHandle );

                jreactor.lock( handle0, errorHandle );

                // locked
                Assert.assertTrue( jreactor.isLocked( handle0 ) );
        }


        @Test
        public void testRegisterSignal( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerSignal( "shutdownhook", handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredSignal( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredSignal( "shutdownhook" ) );
		jreactor.deregister( handle0, handle0 );

                // EventMask = SIGNAL
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerSignal( "shutdownhook", handler1, EventMask.SIGNAL );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredSignal( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.SIGNAL );
                Assert.assertTrue( jreactor.isRegisteredSignal( "shutdownhook" ) );

                // 2nd event source and handle
		MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = jreactor.registerQueue( queue2, handler1, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredSignal( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.SIGNAL );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredSignal( "shutdownhook" ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( handle2 ) );
                Assert.assertTrue( jreactor.isRegisteredQueue( queue2 ) );

		jreactor.shutdown( );
        }


        @Test
        public void testIsRegisteredSignalString( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerSignal( "shutdownhook", handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredSignal( "shutdownhook" ) );

                jreactor.shutdown( );
        }


        @Test
        public void testIsRegisteredSignalHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerSignal( "shutdownhook", handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredSignal( handle0 ) );

                jreactor.shutdown( );
        }



        @Test
        public void testRegisterError( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerError( handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegisteredError( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = ERROR
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerError( handler1, EventMask.ERROR );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredError( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.ERROR );

                // 2nd event source and handle
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = jreactor.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegisteredError( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.ERROR );
                Assert.assertTrue( jreactor.isRegisteredQueue( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );
        }


        @Test
        public void testNewErrorHandleHandler( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerError( handler0, EventMask.NOOP );

		Handle handle2 = jreactor.newErrorHandle( handler0 );

                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
                Assert.assertTrue( jreactor.isRegisteredError( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.NOOP );
	}


        @Test
        public void testNewErrorHandleHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerError( handler0, EventMask.NOOP );

                Handle handle2 = jreactor.newErrorHandle( handle0 );

                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertFalse( jreactor.isRegistered( handle0 ) );
                Assert.assertTrue( jreactor.isRegisteredError( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.NOOP );
        }


        @Test
        public void testIsRegisteredErrorHandler( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerError( handler0, EventMask.NOOP );

                Assert.assertTrue( jreactor.isRegisteredError( handler0 ) );
        }


        @Test
        public void testIsRegisteredErrorHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerError( handler0, EventMask.NOOP );

                Assert.assertTrue( jreactor.isRegisteredError( handle0 ) );
        }



        @Test
        public void testRegisterBlockingSingle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerBlocking( new GenericRunnable( ), handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegistered( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = BLOCKING
                GenericHandler handler1 = new GenericHandler( );
                Handle handle1 = jreactor.registerBlocking( new GenericRunnable( ), handler1, EventMask.BLOCKING );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegistered( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.BLOCKING );

                // 2nd event source and handle
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = jreactor.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegistered( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.BLOCKING );
                Assert.assertTrue( jreactor.isRegistered( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );
        }


        @Test
        public void testRegisterBlockingGroup( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
		Set<Runnable> runSet1 = new HashSet( );
		runSet1.add( new GenericRunnable( ) );
		runSet1.add( new GenericRunnable( ) );
                Handle handle0 = jreactor.registerBlockingGroup( runSet1, handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegistered( handler0 ) );
                Assert.assertTrue( jreactor.isRegistered( handle0 ) );
                Assert.assertEquals( jreactor.interestOps( handle0 ), EventMask.NOOP );

                // EventMask = BLOCKING
                GenericHandler handler1 = new GenericHandler( );
		Set<Runnable> runSet2 = new HashSet( );
		runSet2.add( new GenericRunnable( ) );
		runSet2.add( new GenericRunnable( ) );
                Handle handle1 = jreactor.registerBlockingGroup( runSet2, handler1, EventMask.BLOCKING );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegistered( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.BLOCKING );

                // 2nd event source and handle
                MessageQueue queue2 = new LinkedBlockingMessageQueue( );
                Handle handle2 = jreactor.registerQueue( queue2, handler1, EventMask.QREAD );
                Assert.assertTrue( jreactor.isRegistered( handler1 ) );
                Assert.assertTrue( jreactor.isRegistered( handle1 ) );
                Assert.assertEquals( jreactor.interestOps( handle1 ), EventMask.BLOCKING );
                Assert.assertTrue( jreactor.isRegistered( handle2 ) );
                Assert.assertEquals( jreactor.interestOps( handle2 ), EventMask.QREAD );
        }


        @Test
        public void testIsRegisteredBlockingHandle( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                // EventMask = NOOP
                GenericHandler handler0 = new GenericHandler( );
                Handle handle0 = jreactor.registerBlocking( new GenericRunnable( ), handler0, EventMask.NOOP );
                Assert.assertTrue( jreactor.isRegisteredBlocking( handle0 ) );
	}


        @Test
        public void testConfigureWorkerThreadPoolThread( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                jreactor.configureWorkerThreadPool( 2 );

                Field workerThreadNumF = JReactor.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                Integer workerThreadNum = (Integer)workerThreadNumF.get( jreactor );
                Assert.assertEquals( workerThreadNum.intValue( ), 2 );
        }


        @Test
        public void testConfigureWorkerThreadPoolTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

		jreactor.configureWorkerThreadPool( 2, 4, TimeUnit.NANOSECONDS, 5, TimeUnit.MICROSECONDS );

                Field workerThreadNumF = JReactor.class.getDeclaredField( "workerThreadNum" );
                workerThreadNumF.setAccessible( true );
                Integer workerThreadNum = (Integer)workerThreadNumF.get( jreactor );
                Assert.assertEquals( workerThreadNum.intValue( ), 2 );

                Field terminationTimeoutFirstF = JReactor.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutFirst.intValue( ), 4 );

                Field terminationTimeoutUnitsFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.NANOSECONDS );

                Field terminationTimeoutLastF = JReactor.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutLast.intValue( ), 5 );

                Field terminationTimeoutUnitsLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.MICROSECONDS );
	}


        @Test
        public void testConfigureWorkerThreadPoolThreadTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                jreactor.configureWorkerThreadPool( 4, TimeUnit.NANOSECONDS, 5, TimeUnit.MICROSECONDS );

                Field terminationTimeoutFirstF = JReactor.class.getDeclaredField( "terminationTimeoutFirst" );
                terminationTimeoutFirstF.setAccessible( true );
                Integer terminationTimeoutFirst = (Integer)terminationTimeoutFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutFirst.intValue( ), 4 );

                Field terminationTimeoutUnitsFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsFirst" );
                terminationTimeoutUnitsFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsFirst = (TimeUnit)terminationTimeoutUnitsFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsFirst, TimeUnit.NANOSECONDS );

                Field terminationTimeoutLastF = JReactor.class.getDeclaredField( "terminationTimeoutLast" );
                terminationTimeoutLastF.setAccessible( true );
                Integer terminationTimeoutLast = (Integer)terminationTimeoutLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutLast.intValue( ), 5 );

                Field terminationTimeoutUnitsLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsLast" );
                terminationTimeoutUnitsLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsLast = (TimeUnit)terminationTimeoutUnitsLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsLast, TimeUnit.MICROSECONDS );
        }


        @Test
        public void testConfigureCriticalThreadPoolTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                jreactor.configureCriticalThreadPool( 4, TimeUnit.NANOSECONDS, 5, TimeUnit.MICROSECONDS );

                Field terminationTimeoutCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalFirst" );
                terminationTimeoutCriticalFirstF.setAccessible( true );
                Integer terminationTimeoutCriticalFirst = (Integer)terminationTimeoutCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalFirst.intValue( ), 4 );

                Field terminationTimeoutUnitsCriticalFirstF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalFirst" );
                terminationTimeoutUnitsCriticalFirstF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalFirst = (TimeUnit)terminationTimeoutUnitsCriticalFirstF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalFirst, TimeUnit.NANOSECONDS );

                Field terminationTimeoutCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutCriticalLast" );
                terminationTimeoutCriticalLastF.setAccessible( true );
                Integer terminationTimeoutCriticalLast = (Integer)terminationTimeoutCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutCriticalLast.intValue( ), 5 );

                Field terminationTimeoutUnitsCriticalLastF = JReactor.class.getDeclaredField( "terminationTimeoutUnitsCriticalLast" );
                terminationTimeoutUnitsCriticalLastF.setAccessible( true );
                TimeUnit terminationTimeoutUnitsCriticalLast = (TimeUnit)terminationTimeoutUnitsCriticalLastF.get( jreactor );
                Assert.assertEquals( terminationTimeoutUnitsCriticalLast, TimeUnit.MICROSECONDS );
        }


        @Test
        public void testConfigureBlockingThreadPoolThread( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

		jreactor.configureBlockingThreadPool( 2 );
	}


        @Test
        public void testConfigureBlockingThreadPoolTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

		jreactor.configureBlockingThreadPool( 7, TimeUnit.MILLISECONDS, 8, TimeUnit.NANOSECONDS );
	}


        @Test
        public void testConfigureBlockingThreadPoolThreadTimeouts( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

		jreactor.configureBlockingThreadPool( 2, 7, TimeUnit.MILLISECONDS, 8, TimeUnit.NANOSECONDS );
	}


        @Test
        public void testEnableQueueSelector( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );
	
		jreactor.enableQueueSelector( );	
		Handle handle = jreactor.registerQueue( new LinkedBlockingMessageQueue( ), new GenericHandler( ), EventMask.NOOP );
		Assert.assertNotNull( handle );
	}


        @Test
        public void testEnableChannelSelector( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

                jreactor.enableChannelSelector( );
                Handle handle = null;
		try {
			handle = jreactor.registerChannel( SocketChannel.open( ), new GenericHandler( ), EventMask.NOOP );
		} catch ( IOException e ) { }
                Assert.assertNotNull( handle );
        }


        @Test
        public void testEnableTimerSelector( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

                jreactor.enableTimerSelector( );
                Date firstTime = new Date( new Date( ).getTime( ) + 10000 );  // create a Date object which is set to current system time plus 10 seconds
                Handle handle = jreactor.registerTimerOnce( firstTime, new GenericHandler( ), EventMask.NOOP );
                Assert.assertNotNull( handle );
        }


        @Test
        public void testEnableBlockingSelector( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

                jreactor.enableBlockingSelector( );
                Handle handle = jreactor.registerBlocking( new GenericRunnable( ), new GenericHandler( ), EventMask.NOOP );
                Assert.assertNotNull( handle );
        }


        @Test
        public void testEnableSignalSelector( ) {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

                jreactor.enableSignalSelector( );
                Handle handle = jreactor.registerSignal( "shutdownhook", new GenericHandler( ), EventMask.NOOP );
                Assert.assertNotNull( handle );

		jreactor.shutdown( );
        }


        @Test
        public void testReportErrorHandleException( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

		Handle handle = new Handle( );
		IOException e = new IOException( );
		jreactor.reportError( handle, e );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<Event> eventQueue = (BlockingDeque<Event>)eventQueueF.get( selector );

		Event eRes = null;
		try {
			eRes = eventQueue.takeFirst( );
		} catch ( InterruptedException ie ) { }

                MessageBlock mb = eRes.getInfo( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "error" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "level" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "standard" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "exception" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (IOException)mb.getMessage( ), e );

	}


        @Test
        public void testReportErrorHandleExceptionCommand( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

                Handle handle = new Handle( );
                IOException e = new IOException( );
		MessageBlock mbC = new MessageBlock( "the command is" );
                jreactor.reportError( handle, e, mbC );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<Event> eventQueue = (BlockingDeque<Event>)eventQueueF.get( selector );

                Event eRes = null;
                try {
                        eRes = eventQueue.takeFirst( );
                } catch ( InterruptedException ie ) { }

                MessageBlock mb = eRes.getInfo( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "error" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "level" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "standard" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "exception" ), 0 );
                mb = mb.getNext( );
                Assert.assertSame( (IOException)mb.getMessage( ), e );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "info" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "command" ), 0 );
                mb = mb.getNext( );
                Assert.assertEquals( mb.getMessageString( ).compareTo( "the command is" ), 0 );
        }


        @Test
        public void testReportCriticalError( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler, false );

                IOException e = new IOException( );
                jreactor.reportCriticalError( e );

                Field criticalThreadPoolF = JReactor.class.getDeclaredField( "criticalThreadPool" );
                criticalThreadPoolF.setAccessible( true );
		ExecutorService criticalThreadPool = (ExecutorService)criticalThreadPoolF.get( jreactor );
		Assert.assertNotNull( criticalThreadPool );
        }


        @Test
        public void testResumeSelectionTransferEvents( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Field eventStoreF = CompositeSelector.class.getDeclaredField( "eventStore" );
                eventStoreF.setAccessible( true );
                EventStore eventStore = (EventStore)eventStoreF.get( selector );
                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

		GenericHandler handler0 = new GenericHandler( );
		MessageQueue queue0 = new LinkedBlockingMessageQueue( );
		Handle handle0 = jreactor.registerQueue( queue0, handler0, EventMask.QREAD );
		eventStore.store( handler0, new Event( ) );
		eventStore.store( handler0, new Event( ) );

		jreactor.resumeSelection( handler0, handle0, new LinkedList<MessageBlock>( ) );

		Assert.assertEquals( eventQueue.size( ), 2 );
	}


        @Test
        public void testresumeSelectionHandleNotNull( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

		GenericHandler handler0 = new GenericHandler( );
		MessageQueue queue0 = new LinkedBlockingMessageQueue( );
		Handle handle0 = jreactor.registerQueue( queue0, handler0, EventMask.QREAD );
                GenericHandler handlerA = new GenericHandler( );
                Handle handleA = jreactor.registerLock( handlerA, EventMask.NOOP );
		jreactor.addMember( handleA, handler0, errorHandle );

		jreactor.lock( handleA, errorHandle );

		jreactor.resumeSelection( handlerA, handleA, new LinkedList<MessageBlock>( ) );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Field lockSelectorF = CompositeSelector.class.getDeclaredField( "lockSelector" );
                lockSelectorF.setAccessible( true );
                LockSelector lockSelector = (LockSelector)lockSelectorF.get( selector );
                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                LockHandlerGroup group = handleGroupMap.get( handleA );

		Assert.assertTrue( group.getEventDone( ) );
	}


        @Test
        public void testresumeSelectionHandleNull( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

                GenericHandler errorHandler = new GenericHandler( );
                Handle errorHandle = jreactor.registerError( errorHandler, EventMask.ERROR );

                GenericHandler handler = new GenericHandler( );
                MessageQueue queue0 = new LinkedBlockingMessageQueue( );
                Handle handle0 = jreactor.registerQueue( queue0, handler, EventMask.QREAD );
                MessageQueue queue1 = new LinkedBlockingMessageQueue( );
                Handle handle1 = jreactor.registerQueue( queue1, handler, EventMask.QREAD );

                queue0.offer( new MessageBlock( ) );
                queue1.offer( new MessageBlock( ) );

                jreactor.resumeSelection( handler, null, new LinkedList<MessageBlock>( ) );

                Field selectorF = JReactor.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( jreactor );
                Field eventQueueF = CompositeSelector.class.getDeclaredField( "eventQueue" );
                eventQueueF.setAccessible( true );
                BlockingDeque<MessageBlock> eventQueue = (BlockingDeque<MessageBlock>)eventQueueF.get( selector );

                Assert.assertEquals( eventQueue.size( ), 4 );
        }


        @Test
        public void testFinalize( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                GenericHandler criticalHandler = new GenericHandler( );
                JReactor jreactor = new JReactor( 3, criticalHandler );

		jreactor.finalize( );

                Field stopWorkerF = JReactor.class.getDeclaredField( "stopWorker" );
                stopWorkerF.setAccessible( true );
		Boolean stopWorker = (Boolean)stopWorkerF.get( jreactor );
		Assert.assertTrue( stopWorker );
	}
}
