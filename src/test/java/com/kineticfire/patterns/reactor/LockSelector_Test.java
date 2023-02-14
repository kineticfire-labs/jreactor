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
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import com.kineticfire.patterns.reactor.LockSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.LockHandlerGroup;
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.MessageBlock;

import com.kineticfire.patterns.reactor.JReactor_LockSelector;
import com.kineticfire.patterns.reactor.MockCompositeSelector_ErrorSelector;
import com.kineticfire.patterns.reactor.Handler_ErrorSelector;

import com.kineticfire.util.MultiValueMap;
import com.kineticfire.util.HashLinkMultiValueMap;

public class LockSelector_Test {

	@Test
	public void testConstructor( ) throws NoSuchFieldException, IllegalAccessException {
		JReactor_LockSelector jreactor = new JReactor_LockSelector( );
		MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
		RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
		LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Field jreactorF = LockSelector.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( lockSelector ), jreactor );

                Field compositeSelectorF = LockSelector.class.getDeclaredField( "selector" );
                compositeSelectorF.setAccessible( true );
                Assert.assertSame( compositeSelectorF.get( lockSelector ), compositeSelector );

                Field trackerF = LockSelector.class.getDeclaredField( "runLockAuthority" );
                trackerF.setAccessible( true );
                Assert.assertSame( trackerF.get( lockSelector ), tracker );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                Assert.assertNotNull( openGroupF.get( lockSelector ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                Assert.assertNotNull( pendingGroupF.get( lockSelector ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                Assert.assertNotNull( lockGroupF.get( lockSelector ) );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertEquals( handleGroupMap.size( ), 0 );

                Field handlerHandlesMapF = LockSelector.class.getDeclaredField( "handlerHandlesMap" );
                handlerHandlesMapF.setAccessible( true );
                MultiValueMap<Handler,Handle> handlerHandlesMap = (HashLinkMultiValueMap<Handler,Handle>)handlerHandlesMapF.get( lockSelector );
                Assert.assertEquals( handlerHandlesMap.size( ), 0 );

                Field handleAdapterMapF = LockSelector.class.getDeclaredField( "handleAdapterMap" );
                handleAdapterMapF.setAccessible( true );
                Map<Handle,HandlerAdapter> handleAdapterMap = (HashMap<Handle,HandlerAdapter>)handleAdapterMapF.get( lockSelector );
                Assert.assertEquals( handleAdapterMap.size( ), 0 );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle,Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertEquals( lockHandleErrorHandleMap.size( ), 0 );
	}


	@Test
	public void testRegister( ) throws IllegalAccessException, NoSuchFieldException {
		JReactor_LockSelector jreactor = new JReactor_LockSelector( );
		MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
		RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
		LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

		Handler_LockSelector handler1 = new Handler_LockSelector( );

		compositeSelector.setInterestOps( EventMask.NOOP );
		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );

		Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );

		Assert.assertTrue( lockSelector.isRegistered( handle1 ) );
                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
		OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
		Assert.assertTrue( openGroup.contains( handle1 ) );

		Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.LOCK );
	}


        @Test
        public void testAddMember( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );

		lockSelector.addMember( handle1, handler2 );
		lockSelector.addMember( handle1, handler3 );
		lockSelector.addMember( handle1, handler4 );


                Assert.assertTrue( lockSelector.isRegistered( handle1 ) );
                Assert.assertTrue( lockSelector.isRegistered( handler2 ) );
                Assert.assertTrue( lockSelector.isRegistered( handler3 ) );
                Assert.assertTrue( lockSelector.isRegistered( handler4 ) );

        }


        @Test
        public void testRemoveMember( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );

                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertTrue( lockSelector.isRegistered( handle1 ) );
                Assert.assertTrue( lockSelector.isRegistered( handler2 ) );
                Assert.assertTrue( lockSelector.isRegistered( handler3 ) );
                Assert.assertTrue( lockSelector.isRegistered( handler4 ) );

                lockSelector.removeMember( handle1, handler2 );
                lockSelector.removeMember( handle1, handler3 );
                lockSelector.removeMember( handle1, handler4 );

                Assert.assertFalse( lockSelector.isRegistered( handler2 ) );
                Assert.assertFalse( lockSelector.isRegistered( handler3 ) );
                Assert.assertFalse( lockSelector.isRegistered( handler4 ) );
        }


       @Test
        public void testIsRegisteredHandle( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );

                Assert.assertTrue( lockSelector.isRegistered( handle1 ) );
        }


        @Test
        public void testIsRegisteredHandler( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );
                Handler_LockSelector handler2 = new Handler_LockSelector( );

		Assert.assertFalse( lockSelector.isRegistered( handler2 ) );

                Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );

                lockSelector.addMember( handle1, handler2 );

		Assert.assertTrue( lockSelector.isRegistered( handler2 ) );
        }


        @Test
        public void testIsMember( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                Handler_LockSelector handlerA = new Handler_LockSelector( );

                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

		Assert.assertTrue( lockSelector.isMember( handle1, handler2 ) );
		Assert.assertTrue( lockSelector.isMember( handle1, handler3 ) );
		Assert.assertTrue( lockSelector.isMember( handle1, handler4 ) );
		Assert.assertFalse( lockSelector.isMember( handle1, handlerA ) );
        }


        @Test
        public void testInterestOpsNoEvent( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.LOCK );


                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.LOCK );
                lockSelector.interestOps( handle1, EventMask.NOOP );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
        }


        @Test
        public void testInterestOpsEvent( ) {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Event event1 = new Event( );
                lockSelector.checkin( handle1, event1 );


                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
                lockSelector.interestOps( handle1, EventMask.LOCK );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.LOCK );
                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 1 );
        }


        @Test
        public void testDeregisterGroupEmpty( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );
		lockSelector.deregister( handle1 );


                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
		OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
		PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
		LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertFalse( lockGroup.contains( handle1 ) );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertFalse( handleGroupMap.containsKey( handle1 ) );

                Field handlerHandlesMapF = LockSelector.class.getDeclaredField( "handlerHandlesMap" );
                handlerHandlesMapF.setAccessible( true );
                MultiValueMap<Handler,Handle> handlerHandlesMap = (HashLinkMultiValueMap<Handler,Handle>)handlerHandlesMapF.get( lockSelector );
                Assert.assertFalse( handlerHandlesMap.containsValue( handle1 ) );

                Field handleAdapterMapF = LockSelector.class.getDeclaredField( "handleAdapterMap" );
                handleAdapterMapF.setAccessible( true );
                Map<Handle,HandlerAdapter> handleAdapterMap = (HashMap<Handle,HandlerAdapter>)handleAdapterMapF.get( lockSelector );
                Assert.assertFalse( handleAdapterMap.containsValue( handle1 ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle,Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertFalse( lockHandleErrorHandleMap.containsKey( handle1 ) );

	}


        @Test
        public void testDeregisterGroupOpen( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );

                lockSelector.deregister( handle1 );


                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertFalse( lockGroup.contains( handle1 ) );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertFalse( handleGroupMap.containsKey( handle1 ) );

                Field handlerHandlesMapF = LockSelector.class.getDeclaredField( "handlerHandlesMap" );
                handlerHandlesMapF.setAccessible( true );
                MultiValueMap<Handler,Handle> handlerHandlesMap = (HashLinkMultiValueMap<Handler,Handle>)handlerHandlesMapF.get( lockSelector );
                Assert.assertFalse( handlerHandlesMap.containsValue( handle1 ) );

                Field handleAdapterMapF = LockSelector.class.getDeclaredField( "handleAdapterMap" );
                handleAdapterMapF.setAccessible( true );
                Map<Handle,HandlerAdapter> handleAdapterMap = (HashMap<Handle,HandlerAdapter>)handleAdapterMapF.get( lockSelector );
                Assert.assertFalse( handleAdapterMap.containsValue( handle1 ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle,Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertFalse( lockHandleErrorHandleMap.containsKey( handle1 ) );
	}


        @Test
        public void testDeregisterGroupLocked( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );

		Handle errorHandle = new Handle( );
		lockSelector.lock( handle1, errorHandle );
		Assert.assertTrue( lockSelector.isLocked( handle1 ) );	

                lockSelector.deregister( handle1 );


                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertFalse( lockGroup.contains( handle1 ) );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertFalse( handleGroupMap.containsKey( handle1 ) );

                Field handlerHandlesMapF = LockSelector.class.getDeclaredField( "handlerHandlesMap" );
                handlerHandlesMapF.setAccessible( true );
                MultiValueMap<Handler,Handle> handlerHandlesMap = (HashLinkMultiValueMap<Handler,Handle>)handlerHandlesMapF.get( lockSelector );
                Assert.assertFalse( handlerHandlesMap.containsValue( handle1 ) );

                Field handleAdapterMapF = LockSelector.class.getDeclaredField( "handleAdapterMap" );
                handleAdapterMapF.setAccessible( true );
                Map<Handle,HandlerAdapter> handleAdapterMap = (HashMap<Handle,HandlerAdapter>)handleAdapterMapF.get( lockSelector );
                Assert.assertFalse( handleAdapterMap.containsValue( handle1 ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle,Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertFalse( lockHandleErrorHandleMap.containsKey( handle1 ) );
	}


        @Test
        public void testDeregisterGroupPending( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );

                Handle errorHandle = new Handle( );
		HandlerAdapter adapter = new HandlerAdapter( jreactor, handler2, new Handle( ), EventMask.QREAD );
		tracker.submitRunning( handler2, adapter );
                lockSelector.lock( handle1, errorHandle );
                Assert.assertTrue( lockSelector.isPending( handle1 ) );

                lockSelector.deregister( handle1 );


                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertFalse( lockGroup.contains( handle1 ) );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle,LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertFalse( handleGroupMap.containsKey( handle1 ) );

                Field handlerHandlesMapF = LockSelector.class.getDeclaredField( "handlerHandlesMap" );
                handlerHandlesMapF.setAccessible( true );
                MultiValueMap<Handler,Handle> handlerHandlesMap = (HashLinkMultiValueMap<Handler,Handle>)handlerHandlesMapF.get( lockSelector );
                Assert.assertFalse( handlerHandlesMap.containsValue( handle1 ) );

                Field handleAdapterMapF = LockSelector.class.getDeclaredField( "handleAdapterMap" );
                handleAdapterMapF.setAccessible( true );
                Map<Handle,HandlerAdapter> handleAdapterMap = (HashMap<Handle,HandlerAdapter>)handleAdapterMapF.get( lockSelector );
                Assert.assertFalse( handleAdapterMap.containsValue( handle1 ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle,Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertFalse( lockHandleErrorHandleMap.containsKey( handle1 ) );
	}


	@Test
        public void testRemoveGroupOpen( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );

		Assert.assertTrue( lockSelector.isMember( handle1, handler2 ) );

                lockSelector.remove( handler2 );

		Assert.assertFalse( lockSelector.isMember( handle1, handler2 ) );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertTrue( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertFalse( lockGroup.contains( handle1 ) );
        }


        @Test
        public void testRemoveGroupPendingToLock( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );

                Handle errorHandle = new Handle( );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler2, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler2, adapter );
                lockSelector.lock( handle1, errorHandle );
                Assert.assertTrue( lockSelector.isPending( handle1 ) );

                Assert.assertTrue( lockSelector.isMember( handle1, handler2 ) );

                lockSelector.remove( handler2 );

                Assert.assertFalse( lockSelector.isMember( handle1, handler2 ) );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true ); 
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertTrue( lockGroup.contains( handle1 ) );
        }


        @Test
        public void testRemoveGroupPendingToPending( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Handle errorHandle = new Handle( );
                HandlerAdapter adapter2 = new HandlerAdapter( jreactor, handler2, new Handle( ), EventMask.QREAD );
                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler2, adapter2 );
                tracker.submitRunning( handler4, adapter4 );
                lockSelector.lock( handle1, errorHandle );
                Assert.assertTrue( lockSelector.isPending( handle1 ) );

                Assert.assertTrue( lockSelector.isMember( handle1, handler2 ) );

                lockSelector.remove( handler2 );

                Assert.assertFalse( lockSelector.isMember( handle1, handler2 ) );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertTrue( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertTrue( lockGroup.contains( handle1 ) );
        }


        @Test
        public void testLockToLock( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );

                Assert.assertTrue( lockSelector.isOpen( handler2 ) );
                Assert.assertTrue( lockSelector.isOpen( handler3 ) );
                Assert.assertFalse( tracker.isLocked( handler2 ) );
                Assert.assertFalse( tracker.isLocked( handler3) );

		Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );

		Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertTrue( lockSelector.isLocked( handler2 ) );
                Assert.assertTrue( lockSelector.isLocked( handler3 ) );
                Assert.assertTrue( tracker.isLocked( handler2 ) );
                Assert.assertTrue( tracker.isLocked( handler3) );


                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertTrue( lockGroup.contains( handle1 ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle, Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertSame( lockHandleErrorHandleMap.get( handle1 ), errorHandle );

		Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 1 );
        }


        @Test
        public void testLockToPending( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );

                Assert.assertTrue( lockSelector.isOpen( handler2 ) );
                Assert.assertTrue( lockSelector.isOpen( handler3 ) );
                Assert.assertTrue( lockSelector.isOpen( handler4 ) );
                Assert.assertTrue( tracker.isRunning( handler4) );

                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertTrue( lockSelector.isLocked( handler2 ) );
                Assert.assertTrue( lockSelector.isLocked( handler3 ) );
                Assert.assertTrue( lockSelector.isPending( handler4 ) );
                Assert.assertTrue( tracker.isLocked( handler2 ) );
                Assert.assertTrue( tracker.isLocked( handler3) );
                Assert.assertTrue( tracker.isRunning( handler4) );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertTrue( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertTrue( lockGroup.contains( handle1 ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle, Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertSame( lockHandleErrorHandleMap.get( handle1 ), errorHandle );

                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );
        }


        @Test
        public void testUnlock( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );
		lockSelector.resumeSelection( handle1 );
		lockSelector.unlock( handle1 );

		Assert.assertFalse( tracker.isLocked( handler2 ) );
		Assert.assertFalse( tracker.isLocked( handler3 ) );
		Assert.assertFalse( tracker.isLocked( handler4 ) );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertTrue( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertFalse( lockGroup.contains( handle1 ) );
        }


        @Test
        public void testUnlockRunning( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );

                HandlerAdapter adapter2 = new HandlerAdapter( jreactor, handler2, new Handle( ), EventMask.QREAD );
                HandlerAdapter adapter3 = new HandlerAdapter( jreactor, handler3, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler2, adapter2 );
                tracker.submitRunning( handler3, adapter3 );

                lockSelector.unlockRunning( handle1 );

                Field commandsF2 = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF2.setAccessible( true );
                List<MessageBlock> commands2 = (LinkedList<MessageBlock>)commandsF2.get( adapter2 );
                Assert.assertEquals( commands2.size( ), 1 );

                Field commandsF3 = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF3.setAccessible( true );
                List<MessageBlock> commands3 = (LinkedList<MessageBlock>)commandsF3.get( adapter3 );
                Assert.assertEquals( commands3.size( ), 1 );

	}


        @Test
        public void testIsLockedMember( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );

                Assert.assertFalse( lockSelector.isLockedMember( handle1, handler2 ) );
                Assert.assertFalse( lockSelector.isLockedMember( handle1, handler3 ) );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertTrue( lockSelector.isLockedMember( handle1, handler2 ) );
                Assert.assertFalse( lockSelector.isLockedMember( handle1, handler3 ) );
	}


        @Test
        public void testIsOpenHandler( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertTrue( lockSelector.isOpen( handler4 ) );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertFalse( lockSelector.isOpen( handler4 ) );

                lockSelector.takePending( handler4 );

                Assert.assertFalse( lockSelector.isOpen( handler4 ) );
        }


        @Test
        public void testIsOpenHandle( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

		Assert.assertTrue( lockSelector.isOpen( handle1 ) );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

		Assert.assertFalse( lockSelector.isOpen( handle1 ) );

                lockSelector.takePending( handler4 );

		Assert.assertFalse( lockSelector.isOpen( handle1 ) );
	}


        @Test
        public void testIsPendingHandler( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertFalse( lockSelector.isPending( handler4 ) );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertTrue( lockSelector.isPending( handler4 ) );

                lockSelector.takePending( handler4 );

                Assert.assertFalse( lockSelector.isPending( handler4 ) );
        }


        @Test
        public void testIsPendingHandle( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertFalse( lockSelector.isPending( handle1 ) );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertTrue( lockSelector.isPending( handle1 ) );

                lockSelector.takePending( handler4 );

                Assert.assertFalse( lockSelector.isPending( handle1 ) );
        }



        @Test
        public void testIsLockedHandler( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertFalse( lockSelector.isLocked( handler4 ) );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertFalse( lockSelector.isLocked( handler4 ) );

                lockSelector.takePending( handler4 );

                Assert.assertTrue( lockSelector.isLocked( handler4 ) );
        }


        @Test
        public void testIsLockedHandle( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertFalse( lockSelector.isLocked( handle1 ) );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

                Assert.assertFalse( lockSelector.isLocked( handle1 ) );

                lockSelector.takePending( handler4 );

                Assert.assertTrue( lockSelector.isLocked( handle1 ) );
        }


        @Test
        public void testTakePendingHandleHandlerFalse( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );
                Handler_LockSelector handlerA = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );
                Handle handleA = lockSelector.register( handlerA, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );
                lockSelector.addMember( handleA, handler4 );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );
                lockSelector.lock( handleA, errorHandle );

		Assert.assertFalse( lockSelector.takePending( handleA, handler4 ) );
        }


        @Test
        public void testTakePendingHandleHandlerTrue( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );
                Handler_LockSelector handlerA = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );
                Handle handleA = lockSelector.register( handlerA, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );
                lockSelector.addMember( handleA, handler4 );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );
                lockSelector.lock( handleA, errorHandle );

                Assert.assertTrue( lockSelector.takePending( handle1, handler4 ) );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertTrue( lockGroup.contains( handle1 ) );

        }


        @Test
        public void testTakePendingFalse( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );
                Handler_LockSelector handlerA = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );
                Handle handleA = lockSelector.register( handlerA, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );
                lockSelector.addMember( handleA, handler4 );

                Assert.assertFalse( lockSelector.takePending( handle1, handler4 ) );
        }


        @Test
        public void testTakePendingTrue( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );
                Handler_LockSelector handlerA = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );
                Handle handleA = lockSelector.register( handlerA, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );
                lockSelector.addMember( handleA, handler4 );

                HandlerAdapter adapter4 = new HandlerAdapter( jreactor, handler4, new Handle( ), EventMask.QREAD );
                tracker.submitRunning( handler4, adapter4 );
                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );
                lockSelector.lock( handleA, errorHandle );

                Assert.assertTrue( lockSelector.takePending( handler4 ) );

		Assert.assertTrue( tracker.isLocked( handler4 ) );

                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 1 );

                Field openGroupF = LockSelector.class.getDeclaredField( "openGroup" );
                openGroupF.setAccessible( true );
                OpenGroup openGroup = (OpenGroup)openGroupF.get( lockSelector );
                Assert.assertFalse( openGroup.contains( handle1 ) );

                Field pendingGroupF = LockSelector.class.getDeclaredField( "pendingGroup" );
                pendingGroupF.setAccessible( true );
                PendingGroup pendingGroup = (PendingGroup)pendingGroupF.get( lockSelector );
                Assert.assertFalse( pendingGroup.contains( handle1 ) );

                Field lockGroupF = LockSelector.class.getDeclaredField( "lockGroup" );
                lockGroupF.setAccessible( true );
                LockGroup lockGroup = (LockGroup)lockGroupF.get( lockSelector );
                Assert.assertTrue( lockGroup.contains( handle1 ) );

        }


        @Test
        public void testGetLockedHandlerAdapter( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

		HandlerAdapter lockedAdapter = lockSelector.getLockedHandlerAdapter( handle1 );

                Field handleAdapterMapF = LockSelector.class.getDeclaredField( "handleAdapterMap" );
                handleAdapterMapF.setAccessible( true );
                Map<Handle,HandlerAdapter> handleAdapterMap = (HashMap<Handle,HandlerAdapter>)handleAdapterMapF.get( lockSelector );
                Assert.assertSame( handleAdapterMap.get( handle1 ), lockedAdapter );
	}


        @Test
        public void testAddLockHandleCommand( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

		lockSelector.addLockHandleCommand( handle1, new MessageBlock( ) );

                HandlerAdapter lockedAdapter = lockSelector.getLockedHandlerAdapter( handle1 );
                Field commandsF3 = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF3.setAccessible( true );
                List<MessageBlock> commands3 = (LinkedList<MessageBlock>)commandsF3.get( lockedAdapter );
                Assert.assertEquals( commands3.size( ), 1 );
        }


        @Test
        public void testCheckin( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                lockSelector.checkin( handle1, new Event( ) );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle, LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertTrue( handleGroupMap.get( handle1 ).getEventHold( ) );
        }


        @Test
        public void testResumeSelectionFired( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

		lockSelector.resumeSelection( handle1 );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle, LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertTrue( handleGroupMap.get( handle1 ).getEventDone( ) );

                Field lockHandleErrorHandleMapF = LockSelector.class.getDeclaredField( "lockHandleErrorHandleMap" );
                lockHandleErrorHandleMapF.setAccessible( true );
                Map<Handle, Handle> lockHandleErrorHandleMap = (HashMap<Handle,Handle>)lockHandleErrorHandleMapF.get( lockSelector );
                Assert.assertFalse( lockHandleErrorHandleMap.containsKey( handle1 ) );
        }


        @Test
        public void testResumeSelectionHold( ) throws IllegalAccessException, NoSuchFieldException {
                JReactor_LockSelector jreactor = new JReactor_LockSelector( );
                MockCompositeSelector_LockSelector compositeSelector = new MockCompositeSelector_LockSelector( );
                RunLockHandlerTracker tracker = new RunLockHandlerTracker( );
                LockSelector lockSelector = new LockSelector( jreactor, compositeSelector, tracker );

                Handler_LockSelector handler1 = new Handler_LockSelector( );

                Handle handle1 = lockSelector.register( handler1, EventMask.NOOP );

                Handler_LockSelector handler2 = new Handler_LockSelector( );
                Handler_LockSelector handler3 = new Handler_LockSelector( );
                Handler_LockSelector handler4 = new Handler_LockSelector( );
                lockSelector.addMember( handle1, handler2 );
                lockSelector.addMember( handle1, handler3 );
                lockSelector.addMember( handle1, handler4 );

                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 0 );

                Handle errorHandle = new Handle( );
                lockSelector.lock( handle1, errorHandle );

		lockSelector.checkin( handle1, new Event( ) );

                lockSelector.resumeSelection( handle1 );

                Field handleGroupMapF = LockSelector.class.getDeclaredField( "handleGroupMap" );
                handleGroupMapF.setAccessible( true );
                Map<Handle, LockHandlerGroup> handleGroupMap = (HashMap<Handle,LockHandlerGroup>)handleGroupMapF.get( lockSelector );
                Assert.assertTrue( handleGroupMap.get( handle1 ).getEventFired( ) );

                Assert.assertEquals( compositeSelector.getReadyEvents( ).size( ), 2 );
        }



}
