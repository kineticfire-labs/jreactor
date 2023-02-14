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

import java.lang.reflect.Field;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.kineticfire.patterns.reactor.SignalSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.Event;

import com.kineticfire.patterns.reactor.MockCompositeSelector_SignalSelector;
import com.kineticfire.patterns.reactor.SignalSelector;
import com.kineticfire.patterns.reactor.GenericHandler;

import java.util.Map;
import java.util.HashMap;


public class SignalSelector_Test {



	@Test
	public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		SignalSelector selector = new SignalSelector( );

                Field compositeSelectorF = SignalSelector.class.getDeclaredField( "selector" );
                compositeSelectorF.setAccessible( true );
                CompositeSelector compositeSelector = (CompositeSelector)compositeSelectorF.get( selector );
		Assert.assertNull( compositeSelector );

                Field registeredF = SignalSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle,String> registered = (HashMap<Handle,String>)registeredF.get( selector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field keepaliveF = SignalSelector.class.getDeclaredField( "keepalive" );
                keepaliveF.setAccessible( true );
                Map<Handle,SignalEventHandlerInternal> keepalive = (HashMap<Handle,SignalEventHandlerInternal>)keepaliveF.get( selector );
                Assert.assertEquals( keepalive.size( ), 0 );

                Field doneF = SignalSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                Boolean done = (Boolean)doneF.get( selector );
                Assert.assertFalse( done );
	}


        @Test
        public void testConstructorCompositeSelector( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockCompositeSelector_SignalSelector compositeSelector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( compositeSelector );

                Field compositeSelectorF = SignalSelector.class.getDeclaredField( "selector" );
                compositeSelectorF.setAccessible( true );
                CompositeSelector aCompositeSelector = (CompositeSelector)compositeSelectorF.get( selector );
                Assert.assertSame( compositeSelector, aCompositeSelector );

                Field registeredF = SignalSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle,String> registered = (HashMap<Handle,String>)registeredF.get( selector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field keepaliveF = SignalSelector.class.getDeclaredField( "keepalive" );
                keepaliveF.setAccessible( true );
                Map<Handle,SignalEventHandlerInternal> keepalive = (HashMap<Handle,SignalEventHandlerInternal>)keepaliveF.get( selector );
                Assert.assertEquals( keepalive.size( ), 0 );

                Field doneF = SignalSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                Boolean done = (Boolean)doneF.get( selector );
                Assert.assertFalse( done );
        }


        @Test
        public void testRegister( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector compositeSelector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( compositeSelector );

		GenericHandler handler = new GenericHandler( );
		Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );

		Assert.assertNotNull( handle );

                Field registeredF = SignalSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle,String> registered = (HashMap<Handle,String>)registeredF.get( selector );
                Assert.assertSame( registered.get( handle ), "shutdownhook" );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertSame( handleState.get( handle ), EventGenerationState.NONE );

                Field keepaliveF = SignalSelector.class.getDeclaredField( "keepalive" );
                keepaliveF.setAccessible( true );
                Map<Handle,SignalEventHandlerInternal> keepalive = (HashMap<Handle,SignalEventHandlerInternal>)keepaliveF.get( selector );
                Assert.assertTrue( keepalive.containsKey( handle ) );

		SignalEventHandlerInternal internalHandler = keepalive.get( handle );
		Assert.assertTrue( Runtime.getRuntime( ).removeShutdownHook( internalHandler ) );

		selector.shutdown( );
        }


        @Test
        public void testIsRegisteredString( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector compositeSelector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( compositeSelector );

                GenericHandler handler = new GenericHandler( );

		Assert.assertFalse( selector.isRegistered( "shutdownhook" ) );

                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );

		Assert.assertTrue( selector.isRegistered( "shutdownhook" ) );

		selector.shutdown( );
	}


        @Test
        public void testIsRegisteredHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector compositeSelector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( compositeSelector );

                GenericHandler handler = new GenericHandler( );

		Handle handle1 = new Handle( );
                Assert.assertFalse( selector.isRegistered( handle1 ) );

                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );
                Assert.assertTrue( selector.isRegistered( handle ) );

                selector.shutdown( );
        }


        @Test
        public void testInterestOps( ) {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );


                // NOOP -> NOOP
                cselector.setInterestOps( EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );
                selector.interestOps( handle, EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );

                // NOOP -> SIGNAL
                cselector.setInterestOps( EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );
                selector.interestOps( handle, EventMask.SIGNAL );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.SIGNAL );

                // SIGNAL -> NOOP
                cselector.setInterestOps( EventMask.SIGNAL );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.SIGNAL );
                selector.interestOps( handle, EventMask.NOOP );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.NOOP );

                // SIGNAL -> SIGNAL 
                cselector.setInterestOps( EventMask.SIGNAL );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.SIGNAL );
                selector.interestOps( handle, EventMask.SIGNAL );
                Assert.assertEquals( cselector.getInterestOps( ), EventMask.SIGNAL );

		selector.shutdown( );
        }

        @Test
        public void testDeregister( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );
		selector.deregister( handle );	

                Field registeredF = SignalSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle,String> registered = (HashMap<Handle,String>)registeredF.get( selector );
                Assert.assertFalse( registered.containsKey( handle ) );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertFalse( handleState.containsKey( handle ) );

                Field keepaliveF = SignalSelector.class.getDeclaredField( "keepalive" );
                keepaliveF.setAccessible( true );
                Map<Handle,SignalEventHandlerInternal> keepalive = (HashMap<Handle,SignalEventHandlerInternal>)keepaliveF.get( selector );
                Assert.assertFalse( keepalive.containsKey( handle ) );

		selector.shutdown( );
	}


        @Test
        public void testCheckin( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );

		selector.checkin( handle, new Event( ) );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.get( handle ), EventGenerationState.HOLDING );

                selector.shutdown( );
        }


        @Test
        public void testResumeSelectionFiredToDone( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );

		selector.signalFired( handle );
                selector.resumeSelection( handle );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.get( handle ), EventGenerationState.DONE );

                selector.shutdown( );
        }


        @Test
        public void testResumeSelectionHoldToFired( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );
		cselector.setInterestOps( EventMask.SIGNAL );

                selector.signalFired( handle );
		selector.checkin( handle, new Event( ) );
                selector.resumeSelection( handle );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.get( handle ), EventGenerationState.FIRED );

		Assert.assertEquals( cselector.getReadyEvents( ).size( ), 2 );

                selector.shutdown( );
        }


        @Test
        public void testSignalFired( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );
                cselector.setInterestOps( EventMask.SIGNAL );

                selector.signalFired( handle );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.get( handle ), EventGenerationState.FIRED );

                Assert.assertEquals( cselector.getReadyEvents( ).size( ), 1 );

                selector.shutdown( );
        }


        @Test
        public void testShutdown( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );

		selector.shutdown( );

                Field registeredF = SignalSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle,String> registered = (HashMap<Handle,String>)registeredF.get( selector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field keepaliveF = SignalSelector.class.getDeclaredField( "keepalive" );
                keepaliveF.setAccessible( true );
                Map<Handle,SignalEventHandlerInternal> keepalive = (HashMap<Handle,SignalEventHandlerInternal>)keepaliveF.get( selector );
                Assert.assertEquals( keepalive.size( ), 0 );
        }


        @Test
        public void testFinalize( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockCompositeSelector_SignalSelector cselector = new MockCompositeSelector_SignalSelector( );
                SignalSelector selector = new SignalSelector( cselector );

                GenericHandler handler = new GenericHandler( );
                Handle handle = selector.register( "shutdownhook", handler, EventMask.SIGNAL );

                selector.shutdown( );

                Field registeredF = SignalSelector.class.getDeclaredField( "registered" );
                registeredF.setAccessible( true );
                Map<Handle,String> registered = (HashMap<Handle,String>)registeredF.get( selector );
                Assert.assertEquals( registered.size( ), 0 );

                Field handleStateF = SignalSelector.class.getDeclaredField( "handleState" );
                handleStateF.setAccessible( true );
                Map<Handle,EventGenerationState> handleState = (HashMap<Handle,EventGenerationState>)handleStateF.get( selector );
                Assert.assertEquals( handleState.size( ), 0 );

                Field keepaliveF = SignalSelector.class.getDeclaredField( "keepalive" );
                keepaliveF.setAccessible( true );
                Map<Handle,SignalEventHandlerInternal> keepalive = (HashMap<Handle,SignalEventHandlerInternal>)keepaliveF.get( selector );
                Assert.assertEquals( keepalive.size( ), 0 );
        }


}
