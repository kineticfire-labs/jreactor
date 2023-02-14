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
import org.testng.annotations.BeforeClass;
import org.testng.Assert;

import java.util.Set;

import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.EventGenerationState;

import com.kineticfire.patterns.reactor.GenericHandler;

public class LockHandlerGroup_Test {


	private GenericHandler handler1;
	private GenericHandler handler2;
	private GenericHandler handler3;


	@BeforeClass
	public void setup( ) {
		handler1 = new GenericHandler( );
		handler2 = new GenericHandler( );
		handler3 = new GenericHandler( );
	}


	@Test
	public void testConstructor( ) {
		LockHandlerGroup g = new LockHandlerGroup( );
		Assert.assertTrue( g.isEmpty( ) );
		Assert.assertEquals( g.getHandlers( ).size( ), 0 );
		Assert.assertEquals( g.getPendingHandlers( ).size( ), 0 );
		Assert.assertFalse( g.isLocked( ) );
		Assert.assertFalse( g.isPending( ) );
		Assert.assertFalse( g.isOpen( ) );
		Assert.assertTrue( g.getEventNone( ) );
		Assert.assertFalse( g.getEventFired( ) );
		Assert.assertFalse( g.getEventHold( ) );
		Assert.assertFalse( g.getEventDone( ) );
	}


        @Test
        public void testAddMember( ) {
                LockHandlerGroup g = new LockHandlerGroup( );
		g.addMember( handler1 );
		g.addMember( handler2 );
		g.addMember( handler3 );
		Assert.assertFalse( g.isEmpty( ) );
		Assert.assertTrue( g.isMember( handler1 ) );
		Assert.assertTrue( g.isMember( handler2 ) );
		Assert.assertTrue( g.isMember( handler3 ) );
		Set<Handler> s = g.getHandlers( );
		Assert.assertTrue( s.contains( handler1 ) );
		Assert.assertTrue( s.contains( handler2 ) );
		Assert.assertTrue( s.contains( handler3 ) );
		
        }


        @Test
        public void testRemoveMember( ) {
                LockHandlerGroup g = new LockHandlerGroup( );
                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertTrue( g.isMember( handler1 ) );
                Assert.assertTrue( g.isMember( handler2 ) );
                Assert.assertTrue( g.isMember( handler3 ) );
		g.removeMember( handler1 );
		g.removeMember( handler2 );
		g.removeMember( handler3 );
                Assert.assertTrue( g.isEmpty( ) );
		Set<Handler> s = g.getHandlers( );
                Assert.assertFalse( s.contains( handler1 ) );
                Assert.assertFalse( s.contains( handler2 ) );
                Assert.assertFalse( s.contains( handler3 ) );

        }


        @Test
        public void testGetPendingHandlers( ) {
                LockHandlerGroup g = new LockHandlerGroup( );
                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertTrue( g.isMember( handler1 ) );
                Assert.assertTrue( g.isMember( handler2 ) );
                Assert.assertTrue( g.isMember( handler3 ) );

                Set<Handler> sA = g.getPendingHandlers( );
                Assert.assertTrue( sA.isEmpty( ) );

		g.setPending( handler2 );
                Set<Handler> sB = g.getPendingHandlers( );
                Assert.assertEquals( sB.size( ), 1 );
                Assert.assertTrue( sB.contains( handler2 ) );

		g.setPending( handler1 );
                Set<Handler> sC = g.getPendingHandlers( );
                Assert.assertEquals( sC.size( ), 2 );
                Assert.assertTrue( sC.contains( handler2 ) );
                Assert.assertTrue( sC.contains( handler1 ) );
	}


        @Test
        public void testGetHandlers( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Set<Handler> sA = g.getHandlers( );
                Assert.assertTrue( sA.isEmpty( ) );

                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertTrue( g.isMember( handler1 ) );
                Assert.assertTrue( g.isMember( handler2 ) );
                Assert.assertTrue( g.isMember( handler3 ) );

                Set<Handler> sB = g.getHandlers( );
                Assert.assertEquals( sB.size( ), 3 );
                Assert.assertTrue( sB.contains( handler1 ) );
                Assert.assertTrue( sB.contains( handler2 ) );
                Assert.assertTrue( sB.contains( handler3 ) );
        }


        @Test
        public void testIsMember( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertFalse( g.isMember( handler1 ) );

                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertTrue( g.isMember( handler1 ) );
                Assert.assertTrue( g.isMember( handler2 ) );
                Assert.assertTrue( g.isMember( handler3 ) );
        }


        @Test
        public void testIsEmpty( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.isEmpty( ) );

                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertFalse( g.isEmpty( ) );
        }


        @Test
        public void testIsLocked( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertFalse( g.isLocked( ) );

                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertFalse( g.isLocked( ) );

		g.setLocked( handler1 );
		Assert.assertFalse( g.isLocked( ) );

		g.setPending( handler2 );
		Assert.assertFalse( g.isLocked( ) );

		g.setLocked( handler2 );
		g.setLocked( handler3 );
		Assert.assertTrue( g.isLocked( ) );
        }


        @Test
        public void testIsPending( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertFalse( g.isPending( ) );

                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertFalse( g.isPending( ) );

                g.setPending( handler1 );
                Assert.assertTrue( g.isPending( ) );

                g.setLocked( handler2 );
                Assert.assertTrue( g.isPending( ) );

                g.setPending( handler2 );
                g.setPending( handler3 );
                Assert.assertTrue( g.isPending( ) );
        }


        @Test
        public void testIsOpen( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertFalse( g.isOpen( ) );

                g.addMember( handler1 );
                g.addMember( handler2 );
                g.addMember( handler3 );
                Assert.assertTrue( g.isOpen( ) );

                g.setPending( handler1 );
                Assert.assertFalse( g.isOpen( ) );

                g.setLocked( handler2 );
                Assert.assertFalse( g.isOpen( ) );

                g.setLocked( handler2 );
                g.setLocked( handler3 );
                Assert.assertFalse( g.isOpen( ) );
        }


        @Test
        public void testSetLocked( ) {
                LockHandlerGroup g = new LockHandlerGroup( );
                
		g.addMember( handler1 );
		Assert.assertFalse( g.isLocked( ) );
		Assert.assertFalse( g.isPending( ) );
		Assert.assertTrue( g.isOpen( ) );

		g.setLocked( handler1 );
                Assert.assertTrue( g.isLocked( ) );
		Assert.assertFalse( g.isPending( ) );
		Assert.assertFalse( g.isOpen( ) );
        }


        @Test
        public void testSetPending( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                g.addMember( handler1 );
                Assert.assertFalse( g.isLocked( ) );
                Assert.assertFalse( g.isPending( ) );
                Assert.assertTrue( g.isOpen( ) );

                g.setPending( handler1 );
                Assert.assertFalse( g.isLocked( ) );
                Assert.assertTrue( g.isPending( ) );
                Assert.assertFalse( g.isOpen( ) );
        }


        @Test
        public void testSetOpen( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                g.addMember( handler1 );
		g.setLocked( handler1 );
                Assert.assertTrue( g.isLocked( ) );
                Assert.assertFalse( g.isPending( ) );
                Assert.assertFalse( g.isOpen( ) );

                g.setOpen( handler1 );
                Assert.assertFalse( g.isLocked( ) );
                Assert.assertFalse( g.isPending( ) );
                Assert.assertTrue( g.isOpen( ) );
        }


        @Test
        public void testGetEventNone( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                g.setEventDone( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertTrue( g.getEventDone( ) );

                g.setEventNone( );
                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );
        }


        @Test
        public void testSetEventNone( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                g.setEventDone( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertTrue( g.getEventDone( ) );

                g.setEventNone( );
                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );
        }


        @Test
        public void testGetEventFired( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );

                g.setEventFired( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertTrue( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );
        }


        @Test
        public void testSetEventFired( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );

                g.setEventFired( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertTrue( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );
        }


        @Test
        public void testGetEventHold( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );

                g.setEventHold( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertTrue( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );
        }


        @Test
        public void testSetEventHold( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );

                g.setEventHold( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertTrue( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );
        }


        @Test
        public void testGetEventDone( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );

                g.setEventDone( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertTrue( g.getEventDone( ) );
        }


        @Test
        public void testSetEventDone( ) {
                LockHandlerGroup g = new LockHandlerGroup( );

                Assert.assertTrue( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertFalse( g.getEventDone( ) );

                g.setEventDone( );
                Assert.assertFalse( g.getEventNone( ) );
                Assert.assertFalse( g.getEventFired( ) );
                Assert.assertFalse( g.getEventHold( ) );
                Assert.assertTrue( g.getEventDone( ) );
        }


}
