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

import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;


public class Event_Test {

	@Test
	public void testConstructorNoArg( ) {
		Event e = new Event( );
		Assert.assertNotNull( e.getHandle( ) );
		Assert.assertEquals( e.getReadyOps( ), 0 );
		Assert.assertNull( e.getInfo( ) );
	}


	@Test
	public void testConstructorOps( ) {
		Event e = new Event( EventMask.QREAD );
		Assert.assertNotNull( e.getHandle( ) );
		Assert.assertEquals( e.getReadyOps( ), EventMask.QREAD );
		Assert.assertNull( e.getInfo( ) );
	}


	@Test
	public void testConstructorHandleOps( ) {
		Handle h = new Handle( );
		Event e = new Event( h, EventMask.QREAD );
		Assert.assertSame( e.getHandle( ), h );
		Assert.assertEquals( e.getReadyOps( ), EventMask.QREAD );
		Assert.assertNull( e.getInfo( ) );
	}


	@Test
	public void testConstructorHandleOpsMessageBlock( ) {
		Handle h = new Handle( );
		MessageBlock m = new MessageBlock( );
		Event e = new Event( h, EventMask.QREAD, m );
		Assert.assertSame( e.getHandle( ), h );
		Assert.assertEquals( e.getReadyOps( ), EventMask.QREAD );
		Assert.assertSame( e.getInfo( ), m );
	}


        @Test
        public void testGetHandle( ) {
                Handle h = new Handle( );
                Event e = new Event( h, EventMask.QREAD );
                Assert.assertSame( e.getHandle( ), h );
        }


        @Test
        public void testGetReadyOps( ) {
                Event e = new Event( EventMask.QREAD );
                Assert.assertEquals( e.getReadyOps( ), EventMask.QREAD );
        }


        @Test
        public void testGetInfo( ) {
                Handle h = new Handle( );
                MessageBlock m = new MessageBlock( );
                Event e = new Event( h, EventMask.QREAD, m );
                Assert.assertSame( e.getInfo( ), m );
        }

}
