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

import com.kineticfire.patterns.reactor.EventMask;

public class EventMask_Test {

	private final int noOps = 0;
	private final int allOps = Integer.MAX_VALUE;

	@Test
	public void testIsQRead( ) {
		int ops = EventMask.QREAD;
		Assert.assertTrue( EventMask.isQRead( ops ) );
		Assert.assertTrue( EventMask.isQRead( ops | EventMask.LOCK ) );
		Assert.assertFalse( EventMask.isQRead( noOps ) );
		Assert.assertFalse( EventMask.isQRead( EventMask.LOCK ) );
	}


        @Test
        public void testIsQWrite( ) {
                int ops = EventMask.CWRITE;
                Assert.assertTrue( EventMask.isCWrite( ops ) );
                Assert.assertTrue( EventMask.isCWrite( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isCWrite( noOps ) );
                Assert.assertFalse( EventMask.isCWrite( EventMask.LOCK ) );
        }


	/*
        @Test
        public void testIsQueue( ) {
                int ops = EventMask.QREAD | EventMask.QWRITE;
                Assert.assertTrue( EventMask.isQueue( ops ) );
                Assert.assertTrue( EventMask.isQueue( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isQueue( noOps ) );
                Assert.assertFalse( EventMask.isQueue( EventMask.LOCK ) );
        }
	*/


	@Test
	public void testIsCRead( ) {
		int ops = EventMask.CREAD;
		Assert.assertTrue( EventMask.isCRead( ops ) );
		Assert.assertTrue( EventMask.isCRead( ops | EventMask.LOCK ) );
		Assert.assertFalse( EventMask.isCRead( noOps ) );
		Assert.assertFalse( EventMask.isCRead( EventMask.LOCK ) );
	}


        @Test
        public void testIsCWrite( ) {
                int ops = EventMask.CWRITE;
                Assert.assertTrue( EventMask.isCWrite( ops ) );
                Assert.assertTrue( EventMask.isCWrite( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isCWrite( noOps ) );
                Assert.assertFalse( EventMask.isCWrite( EventMask.LOCK ) );
        }


        @Test
        public void testIsConnect( ) {
                int ops = EventMask.CONNECT;
                Assert.assertTrue( EventMask.isConnect( ops ) );
                Assert.assertTrue( EventMask.isConnect( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isConnect( noOps ) );
                Assert.assertFalse( EventMask.isConnect( EventMask.LOCK ) );
        }


        @Test
        public void testIsAccept( ) {
                int ops = EventMask.ACCEPT;
                Assert.assertTrue( EventMask.isAccept( ops ) );
                Assert.assertTrue( EventMask.isAccept( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isAccept(noOps ) );
                Assert.assertFalse( EventMask.isAccept( EventMask.LOCK ) );
        }


        @Test
        public void testIsChannel( ) {
                int ops = EventMask.CREAD | EventMask.CWRITE | EventMask.CONNECT | EventMask.ACCEPT;
                Assert.assertTrue( EventMask.isChannel( ops ) );
                Assert.assertTrue( EventMask.isChannel( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isChannel( noOps ) );
                Assert.assertFalse( EventMask.isChannel( EventMask.LOCK ) );
        }


        @Test
        public void testIsTimer( ) {
                int ops = EventMask.TIMER;
                Assert.assertTrue( EventMask.isTimer( ops ) );
                Assert.assertTrue( EventMask.isTimer( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isTimer( noOps ) );
                Assert.assertFalse( EventMask.isTimer( EventMask.LOCK ) );
        }


        @Test
        public void testIsLock( ) {
                int ops = EventMask.LOCK;
                Assert.assertTrue( EventMask.isLock( ops ) );
                Assert.assertTrue( EventMask.isLock( ops | EventMask.TIMER ) );
                Assert.assertFalse( EventMask.isLock( noOps ) );
                Assert.assertFalse( EventMask.isLock( EventMask.TIMER ) );
        }


        @Test
        public void testIsSignal( ) {
                int ops = EventMask.SIGNAL;
                Assert.assertTrue( EventMask.isSignal( ops ) );
                Assert.assertTrue( EventMask.isSignal( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isSignal( noOps ) );
                Assert.assertFalse( EventMask.isSignal( EventMask.LOCK ) );
        }


        @Test
        public void testIsError( ) {
                int ops = EventMask.ERROR;
                Assert.assertTrue( EventMask.isError( ops ) );
                Assert.assertTrue( EventMask.isError( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isError( noOps ) );
                Assert.assertFalse( EventMask.isError( EventMask.LOCK ) );
        }


        @Test
        public void testIsBlocking( ) {
                int ops = EventMask.BLOCKING;
                Assert.assertTrue( EventMask.isBlocking( ops ) );
                Assert.assertTrue( EventMask.isBlocking( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.isBlocking( noOps ) );
                Assert.assertFalse( EventMask.isBlocking( EventMask.LOCK ) );
        }


        @Test
        public void testXIsNoop( ) {
                int ops = EventMask.NOOP;
                Assert.assertTrue( EventMask.xIsNoop( ops ) );
                Assert.assertFalse( EventMask.xIsNoop( ops + EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsNoop( EventMask.LOCK ) );
        }


        @Test
        public void testXIsQRead( ) {
                int ops = EventMask.QREAD;
                Assert.assertTrue( EventMask.xIsQRead( ops ) );
                Assert.assertFalse( EventMask.xIsQRead( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsQRead( noOps ) );
                Assert.assertFalse( EventMask.xIsQRead( EventMask.LOCK ) );
        }


	/*
        @Test
        public void testXIsQWrite( ) {
                int ops = EventMask.QWRITE;
                Assert.assertTrue( EventMask.xIsQWrite( ops ) );
                Assert.assertFalse( EventMask.xIsQWrite( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsQWrite( noOps ) );
                Assert.assertFalse( EventMask.xIsQWrite( EventMask.LOCK ) );
        }
	*/


	/*
        @Test
        public void testXIsQueue( ) {
                int ops = EventMask.QREAD | EventMask.QWRITE;
                Assert.assertTrue( EventMask.xIsQueue( ops ) );
                Assert.assertTrue( EventMask.xIsQueue( EventMask.QWRITE ) );
                Assert.assertFalse( EventMask.xIsQueue( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsQueue( noOps ) );
                Assert.assertFalse( EventMask.xIsQueue( EventMask.LOCK ) );
        }
	*/


        @Test
        public void testXIsCRead( ) {
                int ops = EventMask.CREAD;
                Assert.assertTrue( EventMask.xIsCRead( ops ) );
                Assert.assertFalse( EventMask.xIsCRead( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsCRead( noOps ) );
                Assert.assertFalse( EventMask.xIsCRead( EventMask.LOCK ) );
        }


        @Test
        public void testXIsCWrite( ) {
                int ops = EventMask.CWRITE;
                Assert.assertTrue( EventMask.xIsCWrite( ops ) );
                Assert.assertFalse( EventMask.xIsCWrite( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsCWrite( noOps ) );
                Assert.assertFalse( EventMask.xIsCWrite( EventMask.LOCK ) );
        }


        @Test
        public void testXIsConnect( ) {
                int ops = EventMask.CONNECT;
                Assert.assertTrue( EventMask.xIsConnect( ops ) );
                Assert.assertFalse( EventMask.xIsConnect( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsConnect( noOps ) );
                Assert.assertFalse( EventMask.xIsConnect( EventMask.LOCK ) );
        }


        @Test
        public void testXIsAccept( ) {
                int ops = EventMask.ACCEPT;
                Assert.assertTrue( EventMask.xIsAccept( ops ) );
                Assert.assertFalse( EventMask.xIsAccept( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsAccept(noOps ) );
                Assert.assertFalse( EventMask.xIsAccept( EventMask.LOCK ) );
        }


        @Test
        public void testXIsChannel( ) {
                int ops = EventMask.CREAD | EventMask.CWRITE | EventMask.CONNECT | EventMask.ACCEPT;
                Assert.assertTrue( EventMask.xIsChannel( ops ) );
                Assert.assertTrue( EventMask.xIsChannel( EventMask.CWRITE ) );
                Assert.assertFalse( EventMask.xIsChannel( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsChannel( noOps ) );
                Assert.assertFalse( EventMask.xIsChannel( EventMask.LOCK ) );
        }


        @Test
        public void testXIsTimer( ) {
                int ops = EventMask.TIMER;
                Assert.assertTrue( EventMask.xIsTimer( ops ) );
                Assert.assertFalse( EventMask.xIsTimer( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsTimer( noOps ) );
                Assert.assertFalse( EventMask.xIsTimer( EventMask.LOCK ) );
        }


        @Test
        public void testXIsLock( ) {
                int ops = EventMask.LOCK;
                Assert.assertTrue( EventMask.xIsLock( ops ) );
                Assert.assertFalse( EventMask.xIsLock( ops | EventMask.TIMER ) );
                Assert.assertFalse( EventMask.xIsLock( noOps ) );
                Assert.assertFalse( EventMask.xIsLock( EventMask.TIMER ) );
        }


        @Test
        public void testXIsSignal( ) {
                int ops = EventMask.SIGNAL;
                Assert.assertTrue( EventMask.xIsSignal( ops ) );
                Assert.assertFalse( EventMask.xIsSignal( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsSignal( noOps ) );
                Assert.assertFalse( EventMask.xIsSignal( EventMask.LOCK ) );
        }


        @Test
        public void testXIsError( ) {
                int ops = EventMask.ERROR;
                Assert.assertTrue( EventMask.xIsError( ops ) );
                Assert.assertFalse( EventMask.xIsError( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsError( noOps ) );
                Assert.assertFalse( EventMask.xIsError( EventMask.LOCK ) );
        }


        @Test
        public void testXIsBlocking( ) {
                int ops = EventMask.BLOCKING;
                Assert.assertTrue( EventMask.xIsBlocking( ops ) );
                Assert.assertFalse( EventMask.xIsBlocking( ops | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.xIsBlocking( noOps ) );
                Assert.assertFalse( EventMask.xIsBlocking( EventMask.LOCK ) );
        }


        @Test
        public void testAnd( ) {
                Assert.assertEquals( EventMask.and( EventMask.NOOP, EventMask.NOOP ), 0 );
                Assert.assertEquals( EventMask.and( EventMask.NOOP, EventMask.TIMER ), 0 );
                Assert.assertEquals( EventMask.and( EventMask.TIMER, EventMask.NOOP ), 0 );

                Assert.assertEquals( EventMask.and( allOps, EventMask.TIMER ), EventMask.TIMER );

                Assert.assertEquals( EventMask.and( EventMask.TIMER, EventMask.TIMER ), EventMask.TIMER );
        }


        @Test
        public void testOr( ) {
                Assert.assertEquals( EventMask.or( EventMask.NOOP, EventMask.NOOP ), 0 );
                Assert.assertEquals( EventMask.or( EventMask.NOOP, EventMask.TIMER ), EventMask.TIMER );
                Assert.assertEquals( EventMask.or( EventMask.TIMER, EventMask.NOOP ), EventMask.TIMER );

                Assert.assertEquals( EventMask.or( EventMask.TIMER, EventMask.TIMER ), EventMask.TIMER );

                Assert.assertEquals( EventMask.or( allOps, EventMask.TIMER ), allOps );
        }


        @Test
        public void testXor( ) {
                Assert.assertEquals( EventMask.xor( EventMask.NOOP, EventMask.NOOP ), 0 );
                Assert.assertEquals( EventMask.xor( EventMask.NOOP, EventMask.TIMER ), EventMask.TIMER );
                Assert.assertEquals( EventMask.xor( EventMask.TIMER, EventMask.NOOP ), EventMask.TIMER );
                Assert.assertEquals( EventMask.xor( EventMask.TIMER, EventMask.TIMER ), 0 );

                Assert.assertEquals( EventMask.xor( EventMask.TIMER, EventMask.LOCK ), EventMask.TIMER | EventMask.LOCK );
        }


        @Test
        public void testContains( ) {
                Assert.assertTrue( EventMask.contains( EventMask.NOOP, EventMask.NOOP ) );
                Assert.assertTrue( EventMask.contains( EventMask.TIMER, EventMask.TIMER ) );
                Assert.assertTrue( EventMask.contains( EventMask.TIMER, EventMask.TIMER | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.contains( EventMask.TIMER, EventMask.LOCK ) );
                Assert.assertFalse( EventMask.contains( EventMask.TIMER | EventMask.LOCK, EventMask.TIMER ) );
        }


        @Test
        public void testEquals( ) {
                Assert.assertTrue( EventMask.equals( EventMask.NOOP, EventMask.NOOP ) );
                Assert.assertTrue( EventMask.equals( EventMask.TIMER, EventMask.TIMER ) );
                Assert.assertFalse( EventMask.equals( EventMask.TIMER, EventMask.TIMER | EventMask.LOCK ) );
                Assert.assertFalse( EventMask.equals( EventMask.TIMER, EventMask.LOCK ) );
                Assert.assertFalse( EventMask.equals( EventMask.TIMER | EventMask.LOCK, EventMask.TIMER ) );
        }


        @Test
        public void testIsValid( ) {
                Assert.assertTrue( EventMask.isValid( EventMask.NOOP ) );
                Assert.assertTrue( EventMask.isValid( EventMask.TIMER ) );
                Assert.assertTrue( EventMask.isValid( EventMask.CHANNELOPS ) );
                Assert.assertTrue( EventMask.isValid( EventMask.VALIDOPS ) );
                Assert.assertFalse( EventMask.isValid( 2048 ) );
        }
}
