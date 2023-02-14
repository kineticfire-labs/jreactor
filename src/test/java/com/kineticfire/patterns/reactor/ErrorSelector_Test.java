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

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.kineticfire.patterns.reactor.ErrorSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.handler.Handler;

import com.kineticfire.patterns.reactor.MockCompositeSelector_ErrorSelector;
import com.kineticfire.patterns.reactor.Handler_ErrorSelector;



public class ErrorSelector_Test {

	@Test
	public void testConstructor( ) throws NoSuchFieldException, IllegalAccessException {
		MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
		ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Field selectorF = ErrorSelector.class.getDeclaredField( "selector" );
                selectorF.setAccessible( true );
                CompositeSelector selector = (CompositeSelector)selectorF.get( errorSelector );
		Assert.assertNotNull( selector );

		Field handlerHandleMapF = ErrorSelector.class.getDeclaredField( "handlerHandleMap" );
                handlerHandleMapF.setAccessible( true );
                Map<Handler,Handle> handlerHandleMap = (HashMap<Handler,Handle>)handlerHandleMapF.get( errorSelector );
                Assert.assertEquals( handlerHandleMap.size( ), 0 );

		Field handleEventMapF = ErrorSelector.class.getDeclaredField( "handleEventMap" );
                handleEventMapF.setAccessible( true );
                Map<Handle,List<Event>> handleEventMap = (HashMap<Handle,List<Event>>)handleEventMapF.get( errorSelector );
                Assert.assertEquals( handleEventMap.size( ), 0 );
	}


	@Test
	public void testRegisterNew( ) {
		MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
		ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

		Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );

		compositeSelector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
		Assert.assertFalse( errorSelector.isRegistered( handler1 ) );
		Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
		Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
		Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.ERROR );
	}


        @Test
        public void testRegisterOld( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );

		compositeSelector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
                Assert.assertFalse( errorSelector.isRegistered( handler1 ) );
                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.ERROR );

		compositeSelector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
                Handle handle2 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle2 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		Assert.assertSame( handle1, handle2 );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.ERROR );
        }




        @Test
        public void testNewErrorHandleHandler( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );

                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );

		compositeSelector.setHandler( handler1 );
		compositeSelector.setHandle( handle1 );

                Handle handle2 = errorSelector.newErrorHandle( handler1 );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle2 ) );
                Assert.assertFalse( errorSelector.isRegistered( handle1 ) );
        }


        @Test
        public void testNewErrorHandleHandle( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );

                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		compositeSelector.setHandler( handler1 );

                Handle handle2 = errorSelector.newErrorHandle( handle1 );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle2 ) );
                Assert.assertFalse( errorSelector.isRegistered( handle1 ) );
        }



        @Test
        public void testGetErrorHandle( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );

                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		Assert.assertSame( errorSelector.getErrorHandle( handler1 ), handle1 );


                Handler_ErrorSelector handler2 = new Handler_ErrorSelector( );
		Assert.assertNull( errorSelector.getErrorHandle( handler2 ) );
        }



        @Test
        public void testIsRegisteredHandler( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );
                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handler1 ) );


                Handler_ErrorSelector handler2 = new Handler_ErrorSelector( );
                Assert.assertFalse( errorSelector.isRegistered( handler2 ) );
        }


        @Test
        public void testIsRegisteredHandle( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );
                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );
                Assert.assertTrue( errorSelector.isRegistered( handle1 ) );


		Handle handle2 = new Handle( );
                Assert.assertFalse( errorSelector.isRegistered( handle2 ) );
        }


        @Test
        public void testInterestOpsNoEvent( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );
                Handle handle1 = errorSelector.register( handler1, EventMask.ERROR );

		Event event1 = new Event( );
		Event event2 = new Event( );
		errorSelector.checkin( handle1, event1 );
		errorSelector.checkin( handle1, event2 );

		Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.ERROR );
		errorSelector.interestOps( handle1, EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
        }


        @Test
        public void testInterestOpsEvent( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );
                Handle handle1 = errorSelector.register( handler1, EventMask.NOOP );

		Event event1 = new Event( );
		Event event2 = new Event( );
		errorSelector.checkin( handle1, event1 );
		errorSelector.checkin( handle1, event2 );

                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
                errorSelector.interestOps( handle1, EventMask.ERROR );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.ERROR );
		Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 2 );
        }


        @Test
        public void testCheckin( ) throws IllegalAccessException, NoSuchFieldException {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );
                Handle handle1 = errorSelector.register( handler1, EventMask.NOOP );

                Field handleEventMapF = ErrorSelector.class.getDeclaredField( "handleEventMap" );
                handleEventMapF.setAccessible( true );
                Map<Handle,List<Event>> handleEventMap = (HashMap<Handle,List<Event>>)handleEventMapF.get( errorSelector );
                Assert.assertEquals( handleEventMap.size( ), 0 );

                Event event1 = new Event( );
                Event event2 = new Event( );
                errorSelector.checkin( handle1, event1 );
                errorSelector.checkin( handle1, event2 );

                Assert.assertEquals( handleEventMap.size( ), 1 );
		List<Event> events = handleEventMap.get( handle1 );
                Assert.assertEquals( events.size( ), 2 );
        }



        @Test
        public void testDeregister( ) {
                MockCompositeSelector_ErrorSelector compositeSelector = new MockCompositeSelector_ErrorSelector( );
                ErrorSelector errorSelector = new ErrorSelector( compositeSelector );

                Handler_ErrorSelector handler1 = new Handler_ErrorSelector( );
                Handle handle1 = errorSelector.register( handler1, EventMask.NOOP );
		compositeSelector.setHandler( handler1 );

		Assert.assertTrue( errorSelector.isRegistered( handle1 ) );
		errorSelector.deregister( handle1 );
		Assert.assertFalse( errorSelector.isRegistered( handle1 ) );
        }

}
