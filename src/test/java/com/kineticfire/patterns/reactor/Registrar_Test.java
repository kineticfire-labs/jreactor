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

import java.util.List;

import com.kineticfire.patterns.reactor.Registrar;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.SpecificSelector;
import com.kineticfire.patterns.reactor.QueueSelector;
import com.kineticfire.patterns.reactor.GenericHandler;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.EventMask;


public class Registrar_Test {

	private QueueSelector selector;

	private Handle handle11;
	private Handle handle12;
	private Handle handle13;
	private Handle handle21;
	private Handle handle22;
	private Handle handle23;
	private Handle handle31;
	private Handle handle32;
	private Handle handle33;

	private GenericHandler handler1;
	private GenericHandler handler2;
	private GenericHandler handler3;

	private int ops;


	@BeforeClass
	public void setup( ) {
		selector = new QueueSelector( );

		handle11 = new Handle( );	
		handle12 = new Handle( );	
		handle13 = new Handle( );	
		handle21 = new Handle( );	
		handle22 = new Handle( );	
		handle23 = new Handle( );	
		handle31 = new Handle( );	
		handle32 = new Handle( );	
		handle33 = new Handle( );	

		handler1 = new GenericHandler( );	
		handler2 = new GenericHandler( );	
		handler3 = new GenericHandler( );	

		ops = EventMask.QREAD;
	}


	@Test
	public void testConstructor( ) {
		Registrar r = new Registrar( );
	}


	@Test
	public void testAddSingle( ) {
		Registrar r = new Registrar( );
		r.add( handle11, handler1, selector, ops );
		Assert.assertTrue( r.contains( handle11 ) );
		Assert.assertTrue( r.contains( handler1 ) );
		Assert.assertSame( r.getHandler( handle11 ), handler1 );
		Assert.assertSame( r.getSelector( handle11 ), selector );
		Assert.assertEquals( r.getInterestOps( handle11 ), ops );
	}


        @Test
        public void testAddHandlerUnique( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle31, handler3, selector, ops );

                Assert.assertTrue( r.contains( handle11 ) );
                Assert.assertTrue( r.contains( handler1 ) );
                Assert.assertSame( r.getHandler( handle11 ), handler1 );
                Assert.assertSame( r.getSelector( handle11 ), selector );
                Assert.assertEquals( r.getInterestOps( handle11 ), ops );

                Assert.assertTrue( r.contains( handle21 ) );
                Assert.assertTrue( r.contains( handler2 ) );
                Assert.assertSame( r.getHandler( handle21 ), handler2 );
                Assert.assertSame( r.getSelector( handle21 ), selector );
                Assert.assertEquals( r.getInterestOps( handle21 ), ops );

                Assert.assertTrue( r.contains( handle31 ) );
                Assert.assertTrue( r.contains( handler3 ) );
                Assert.assertSame( r.getHandler( handle31 ), handler3 );
                Assert.assertSame( r.getSelector( handle31 ), selector );
                Assert.assertEquals( r.getInterestOps( handle31 ), ops );
        }


        @Test
        public void testAddHandlerNonunique( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle12, handler1, selector, ops );
                r.add( handle13, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );
                r.add( handle31, handler3, selector, ops );
                r.add( handle32, handler3, selector, ops );
                r.add( handle33, handler3, selector, ops );


		// Handle 1

                Assert.assertTrue( r.contains( handle11 ) );
                Assert.assertTrue( r.contains( handler1 ) );
                Assert.assertSame( r.getHandler( handle11 ), handler1 );
                Assert.assertSame( r.getSelector( handle11 ), selector );
                Assert.assertEquals( r.getInterestOps( handle11 ), ops );

                Assert.assertTrue( r.contains( handle12 ) );
                Assert.assertTrue( r.contains( handler1 ) );
                Assert.assertSame( r.getHandler( handle12 ), handler1 );
                Assert.assertSame( r.getSelector( handle12 ), selector );
                Assert.assertEquals( r.getInterestOps( handle11 ), ops );

                Assert.assertTrue( r.contains( handle13 ) );
                Assert.assertTrue( r.contains( handler1 ) );
                Assert.assertSame( r.getHandler( handle13 ), handler1 );
                Assert.assertSame( r.getSelector( handle13 ), selector );
                Assert.assertEquals( r.getInterestOps( handle11 ), ops );


		// Handle 2

                Assert.assertTrue( r.contains( handle21 ) );
                Assert.assertTrue( r.contains( handler2 ) );
                Assert.assertSame( r.getHandler( handle21 ), handler2 );
                Assert.assertSame( r.getSelector( handle21 ), selector );
                Assert.assertEquals( r.getInterestOps( handle21 ), ops );

                Assert.assertTrue( r.contains( handle22 ) );
                Assert.assertTrue( r.contains( handler2 ) );
                Assert.assertSame( r.getHandler( handle22 ), handler2 );
                Assert.assertSame( r.getSelector( handle22 ), selector );
                Assert.assertEquals( r.getInterestOps( handle22 ), ops );


                Assert.assertTrue( r.contains( handle23 ) );
                Assert.assertTrue( r.contains( handler2 ) );
                Assert.assertSame( r.getHandler( handle23 ), handler2 );
                Assert.assertSame( r.getSelector( handle23 ), selector );
                Assert.assertEquals( r.getInterestOps( handle23 ), ops );


		// Handle 3

                Assert.assertTrue( r.contains( handle31 ) );
                Assert.assertTrue( r.contains( handler3 ) );
                Assert.assertSame( r.getHandler( handle31 ), handler3 );
                Assert.assertSame( r.getSelector( handle31 ), selector );
                Assert.assertEquals( r.getInterestOps( handle31 ), ops );

                Assert.assertTrue( r.contains( handle32 ) );
                Assert.assertTrue( r.contains( handler3 ) );
                Assert.assertSame( r.getHandler( handle32 ), handler3 );
                Assert.assertSame( r.getSelector( handle32 ), selector );
		Assert.assertEquals( r.getInterestOps( handle32 ), ops );

                Assert.assertTrue( r.contains( handle33 ) );
                Assert.assertTrue( r.contains( handler3 ) );
                Assert.assertSame( r.getHandler( handle33 ), handler3 );
                Assert.assertSame( r.getSelector( handle33 ), selector );
                Assert.assertEquals( r.getInterestOps( handle33 ), ops );
        }


        @Test
        public void testRemoveHandle( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );


                // Handle 1

		boolean ans11 = r.remove( handle11 );
		Assert.assertTrue( ans11 );	
		Assert.assertFalse( r.contains( handle11 ) );
		Assert.assertFalse( r.contains( handler1 ) );

		Assert.assertTrue( r.contains( handle21 ) );	
		Assert.assertTrue( r.contains( handle22 ) );	
		Assert.assertTrue( r.contains( handle23 ) );	
		Assert.assertTrue( r.contains( handler2 ) );	


		// Handle 2

		boolean ans21 = r.remove( handle21 );
		Assert.assertFalse( ans21 );	
		Assert.assertFalse( r.contains( handle21 ) );

		Assert.assertTrue( r.contains( handle22 ) );	
		Assert.assertTrue( r.contains( handle23 ) );	
		Assert.assertTrue( r.contains( handler2 ) );	


		boolean ans22 = r.remove( handle22 );
		Assert.assertFalse( ans22 );	
		Assert.assertFalse( r.contains( handle22 ) );

		Assert.assertTrue( r.contains( handle23 ) );	
		Assert.assertTrue( r.contains( handler2 ) );	


		boolean ans23 = r.remove( handle23 );
		Assert.assertTrue( ans23 );	
		Assert.assertFalse( r.contains( handle23 ) );
		Assert.assertFalse( r.contains( handler2 ) );


		// Handle 3

		boolean ans31 = r.remove( handle31 );
		Assert.assertTrue( ans31 );	
		Assert.assertFalse( r.contains( handle31 ) );

	}


        @Test
        public void testRemoveHandler( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );


                // Handler 1

                r.remove( handler1 );
                Assert.assertFalse( r.contains( handle11 ) );
                Assert.assertFalse( r.contains( handler1 ) );

                // Handler 2

                r.remove( handler2 );
                Assert.assertFalse( r.contains( handle21 ) );
                Assert.assertFalse( r.contains( handle22 ) );
                Assert.assertFalse( r.contains( handle23 ) );
                Assert.assertFalse( r.contains( handler2 ) );

        }


        @Test
        public void testContainsHandle( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );

                Assert.assertTrue( r.contains( handle11 ) );
                Assert.assertTrue( r.contains( handle21 ) );
                Assert.assertTrue( r.contains( handle22 ) );
                Assert.assertTrue( r.contains( handle23 ) );
                Assert.assertFalse( r.contains( handle31 ) );
	}


        @Test
        public void testContainsHandler( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );

                Assert.assertTrue( r.contains( handler1 ) );
                Assert.assertTrue( r.contains( handler2 ) );
                Assert.assertFalse( r.contains( handler3 ) );
        }


        @Test
        public void testGetSelector( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );

                Assert.assertSame( r.getSelector( handle11 ), selector );
                Assert.assertSame( r.getSelector( handle21 ), selector );
                Assert.assertSame( r.getSelector( handle22 ), selector );
                Assert.assertSame( r.getSelector( handle23 ), selector );
                Assert.assertNull( r.getSelector( handle31 ) );
        }


        @Test
        public void testGetHandles( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                //r.add( handle21, handler2, selector, ops );
                //r.add( handle22, handler2, selector, ops );
                //r.add( handle23, handler2, selector, ops );

		List<Handle> l1 = r.getHandles( handler1 );
                Assert.assertEquals( l1.size( ), 1 );
                //Assert.assertSame( l1.get( 0 ), handle11 );
		//Assert.assertTrue( r.contains( handler1 ) );
		//Assert.assertTrue( r.contains( handle11 ) );

		//List<Handle> l2 = r.getHandles( handler2 );
                //Assert.assertEquals( l2.size( ), 3 );
                //Assert.assertSame( l2.get( 0 ), handle21 );
                //Assert.assertSame( l2.get( 1 ), handle22 );
                //Assert.assertSame( l2.get( 2 ), handle23 );
		//Assert.assertTrue( r.contains( handler2 ) );
		//Assert.assertTrue( r.contains( handle21 ) );
		//Assert.assertTrue( r.contains( handle22 ) );
		//Assert.assertTrue( r.contains( handle23 ) );

		//List<Handle> l3 = r.getHandles( handler3 );
                //Assert.assertEquals( l3.size( ), 0 );
		//Assert.assertFalse( r.contains( handler3 ) );
        }


        @Test
        public void testGetHandler( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );

		Assert.assertSame( r.getHandler( handle11 ), handler1 );
		Assert.assertTrue( r.contains( handle11 ) );
		Assert.assertTrue( r.contains( handler1 ) );


		Assert.assertSame( r.getHandler( handle21 ), handler2 );
		Assert.assertSame( r.getHandler( handle22 ), handler2 );
		Assert.assertSame( r.getHandler( handle23 ), handler2 );
		Assert.assertTrue( r.contains( handle21 ) );
		Assert.assertTrue( r.contains( handle22 ) );
		Assert.assertTrue( r.contains( handle23 ) );
		Assert.assertTrue( r.contains( handler2 ) );

		Assert.assertNull( r.getHandler( handle31 ) );
        }


        @Test
        public void testNumHandles( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, ops );
                r.add( handle23, handler2, selector, ops );

                Assert.assertEquals( r.numHandles( handler1 ), 1 );
                Assert.assertEquals( r.numHandles( handler2 ), 3 );
                Assert.assertEquals( r.numHandles( handler3 ), 0 );
        }


        @Test
        public void testGetInterestOps( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, EventMask.TIMER );
                r.add( handle23, handler2, selector, ops );

                Assert.assertEquals( r.getInterestOps( handle11 ), ops );
                Assert.assertEquals( r.getInterestOps( handle21 ), ops );
                Assert.assertEquals( r.getInterestOps( handle22 ), EventMask.TIMER );
                Assert.assertEquals( r.getInterestOps( handle23 ), ops );
                Assert.assertEquals( r.getInterestOps( handle31 ), -1 );
        }


        @Test
        public void testSetInterestOps( ) {
                Registrar r = new Registrar( );
                r.add( handle11, handler1, selector, ops );
                r.add( handle21, handler2, selector, ops );
                r.add( handle22, handler2, selector, EventMask.TIMER );
                r.add( handle23, handler2, selector, ops );

                Assert.assertEquals( r.getInterestOps( handle11 ), ops );
                Assert.assertEquals( r.getInterestOps( handle21 ), ops );
                Assert.assertEquals( r.getInterestOps( handle22 ), EventMask.TIMER );
                Assert.assertEquals( r.getInterestOps( handle23 ), ops );

		r.setInterestOps( handle11, EventMask.LOCK );
		r.setInterestOps( handle21, EventMask.SIGNAL );
		r.setInterestOps( handle22, EventMask.ACCEPT );
		r.setInterestOps( handle23, EventMask.BLOCKING );

                Assert.assertEquals( r.getInterestOps( handle11 ), EventMask.LOCK );
                Assert.assertEquals( r.getInterestOps( handle21 ), EventMask.SIGNAL );
                Assert.assertEquals( r.getInterestOps( handle22 ), EventMask.ACCEPT );
                Assert.assertEquals( r.getInterestOps( handle23 ), EventMask.BLOCKING );
        }
}
