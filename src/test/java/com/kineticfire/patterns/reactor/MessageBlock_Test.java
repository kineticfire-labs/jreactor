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

import com.kineticfire.patterns.reactor.MessageBlock;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;


public class MessageBlock_Test {

	@Test
	public void testConstructorNoArg( ) {
		MessageBlock mb = new MessageBlock( );
		Assert.assertNull( mb.getMessage( ) );
		Assert.assertEquals( mb.getPriority( ) , 100000 );
		Assert.assertNull( mb.getPrev( ) );
		Assert.assertNull( mb.getNext( ) );
		Assert.assertTrue( mb.getIsData( ) );
	}


	@Test
	public void testConstructorMessage( ) {
		Integer i = new Integer( 3 );
		MessageBlock mb = new MessageBlock( i );
		Assert.assertSame( mb.getMessage( ), i );
		Assert.assertEquals( mb.getPriority( ) , 100000 );
		Assert.assertNull( mb.getPrev( ) );
		Assert.assertNull( mb.getNext( ) );
		Assert.assertTrue( mb.getIsData( ) );
	}


	@Test
	public void testConstructorMessagePriority( ) {
		Integer i = new Integer( 3 );
		MessageBlock mb = new MessageBlock( i, 222 );
		Assert.assertSame( mb.getMessage( ), i );
		Assert.assertEquals( mb.getPriority( ) , 222 );
		Assert.assertNull( mb.getPrev( ) );
		Assert.assertNull( mb.getNext( ) );
		Assert.assertTrue( mb.getIsData( ) );
	}


	@Test
	public void testConstructorMessageMb( ) {
		// testing mb2
		Integer i = new Integer( 3 );
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( i, mb1 );
		Assert.assertSame( mb2.getMessage( ), i );
		Assert.assertEquals( mb2.getPriority( ) , 100000 );
		Assert.assertNull( mb1.getPrev( ) );
		Assert.assertSame( mb1.getNext( ), mb2 );
		Assert.assertSame( mb2.getPrev( ), mb1 );
		Assert.assertNull( mb2.getNext( ) );
		Assert.assertTrue( mb1.getIsData( ) );
		Assert.assertTrue( mb2.getIsData( ) );
	}


	@Test
	public void testConstructorMessagePriorityMb( ) {
		// testing mb2
		Integer i = new Integer( 3 );
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( i, 333, mb1 );
		Assert.assertSame( mb2.getMessage( ), i );
		Assert.assertEquals( mb2.getPriority( ) , 333 );
		Assert.assertNull( mb1.getPrev( ) );
		Assert.assertSame( mb1.getNext( ), mb2 );
		Assert.assertSame( mb2.getPrev( ), mb1 );
		Assert.assertNull( mb2.getNext( ) );
		Assert.assertTrue( mb1.getIsData( ) );
		Assert.assertTrue( mb2.getIsData( ) );
	}


	@Test
	public void testResetEmpty( ) {
		MessageBlock mb1 = new MessageBlock( );
		mb1.reset( );
		Assert.assertNull( mb1.getMessage( ) );
		Assert.assertEquals( mb1.getPriority( ) , 100000 );
		Assert.assertNull( mb1.getPrev( ) );
		Assert.assertNull( mb1.getNext( ) );
	}


	@Test
	public void testResetMessagePriority( ) {
		MessageBlock mb2 = new MessageBlock( new Integer( 3 ), 222 );
		mb2.reset( );
		Assert.assertNull( mb2.getMessage( ) );
		Assert.assertEquals( mb2.getPriority( ) , 100000 );
		Assert.assertNull( mb2.getPrev( ) );
		Assert.assertNull( mb2.getNext( ) );
	}


	@Test
	public void testResetFirstChain( ) {
		// given    : mb3 -> mb4
		// action   : mb3.reset( )
		// expected : mb3
		//            mb4
		MessageBlock mb3 = new MessageBlock( new Integer( 3 ), 222 );
		MessageBlock mb4 = new MessageBlock( );
		mb3.setNext( mb4 );
		mb3.reset( );
		Assert.assertNull( mb3.getMessage( ) );
		Assert.assertEquals( mb3.getPriority( ) , 100000 );
		Assert.assertNull( mb3.getPrev( ) );
		Assert.assertNull( mb3.getNext( ) );
		Assert.assertNull( mb4.getPrev( ) );
		Assert.assertNull( mb4.getNext( ) );
	}


	@Test
	public void testResetMiddleChain( ) {
		// given    : mb7 -> mb8 -> mb9
		// action   : mb8.reset( )
		// expected : mb7 -> mb9
		//            mb8
		MessageBlock mb7 = new MessageBlock( );
		MessageBlock mb8 = new MessageBlock( new Integer( 3 ), 222 );
		MessageBlock mb9 = new MessageBlock( );
		mb7.setNext( mb8 );
		mb8.setNext( mb9 );
		mb8.reset( );
		Assert.assertNull( mb8.getMessage( ) );
		Assert.assertEquals( mb8.getPriority( ) , 100000 );
		Assert.assertNull( mb8.getPrev( ) );
		Assert.assertNull( mb8.getNext( ) );
		Assert.assertNull( mb7.getPrev( ) );
		Assert.assertSame( mb7.getNext( ), mb9 );
		Assert.assertSame( mb9.getPrev( ), mb7 );
		Assert.assertNull( mb9.getNext( ) );
	}

	
	@Test
	public void testResetLastChain( ) {
		// given    : mb3 -> mb4
		// action   : mb4.reset( )
		// expected : mb3
		//            mb4
		MessageBlock mb3 = new MessageBlock( );
		MessageBlock mb4 = new MessageBlock( new Integer( 3 ), 222 );
		mb3.setNext( mb4 );
		mb4.reset( );
		Assert.assertNull( mb4.getMessage( ) );
		Assert.assertEquals( mb4.getPriority( ) , 100000 );
		Assert.assertNull( mb3.getPrev( ) );
		Assert.assertNull( mb3.getNext( ) );
		Assert.assertNull( mb4.getPrev( ) );
		Assert.assertNull( mb4.getNext( ) );
	}


	@Test
	public void testSetPrevSingleFirstChain( ) {
		// given    : mb2
		//          : mb1
		// action   : mb2.setPrev( mb1 )
		// expected : mb1 -> mb2
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( );
		mb2.setPrev( mb1 );
		Assert.assertNull( mb1.getPrev( ) );
		Assert.assertSame( mb1.getNext( ), mb2 );
		Assert.assertSame( mb2.getPrev( ), mb1 );
		Assert.assertNull( mb2.getNext( ) );
	}


	@Test
	public void testSetPrevSingleMiddleChain( ) {
		// given    : mb0 -> mb1 -> mb3
		//          : mb2
		// action   : mb3.setPrev( mb2 )
		// expected : mb0 -> mb1
		//	    : mb2 -> mb3
		MessageBlock mb0 = new MessageBlock( );
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( );
		MessageBlock mb3 = new MessageBlock( );
		mb3.setPrev( mb1 );
		mb1.setPrev( mb0 );
		mb3.setPrev( mb2 );
		Assert.assertNull( mb0.getPrev( ) );
		Assert.assertSame( mb0.getNext( ), mb1 );
		Assert.assertSame( mb1.getPrev( ), mb0 );
		Assert.assertNull( mb1.getNext( ) );
		Assert.assertNull( mb2.getPrev( ) );
		Assert.assertSame( mb2.getNext( ), mb3 );
		Assert.assertSame( mb3.getPrev( ), mb2 );
		Assert.assertNull( mb3.getNext( ) );
	}


        @Test
        public void testSetPrevChainFirstChain( ) {
                // given    : mb1 -> mb2
                //          : mb3 -> mb4
                // action   : mb1.setPrev( mb3 )
                // expected : mb3 -> mb1 -> mb2
                //          : mb4
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb2.setPrev( mb1 );
                mb4.setPrev( mb3 );
                mb1.setPrev( mb3 );
                Assert.assertNull( mb4.getPrev( ) );
                Assert.assertNull( mb4.getNext( ) );
                Assert.assertNull( mb3.getPrev( ) );
                Assert.assertSame( mb3.getNext( ), mb1 );
                Assert.assertSame( mb1.getPrev( ), mb3 );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testSetPrevChainMiddleChain( ) {
                // given    : mb1 -> mb2
		//          : mb3 -> mb4
                // action   : mb2.setPrev( mb3 )
                // expected : mb3 -> mb2
	        //          : mb4
		//	    : mb1
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb2.setPrev( mb1 );
                mb4.setPrev( mb3 );
                mb2.setPrev( mb3 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb4.getPrev( ) );
                Assert.assertNull( mb4.getNext( ) );
                Assert.assertNull( mb3.getPrev( ) );
                Assert.assertSame( mb3.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb3 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testInsertPrevSingleFirstChain( ) {
                // given    : mb2
                // action   : mb2.insertPrev( mb1 )
                // expected : mb1 -> mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.insertPrev( mb1 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testInsertPrevSingleMiddleChain( ) {
                // given    : mb0 -> mb1 -> mb3
                // action   : mb3.insertPrev( mb2 )
                // expected : mb0 -> mb1 -> mb2 -> mb3
                MessageBlock mb0 = new MessageBlock( );
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb3.insertPrev( mb1 );
                mb1.insertPrev( mb0 );
                mb3.insertPrev( mb2 );
                Assert.assertNull( mb0.getPrev( ) );
                Assert.assertSame( mb0.getNext( ), mb1 );
                Assert.assertSame( mb1.getPrev( ), mb0 );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertSame( mb2.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb2 );
                Assert.assertNull( mb3.getNext( ) );
        }


        @Test
        public void testInsertPrevChainFirstChain( ) {
                // given    : mb1 -> mb2
		//          : mb3 -> mb4
                // action   : mb1.insertPrev( mb3 )
                // expected : mb3 -> mb4 -> mb1 -> mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb2.insertPrev( mb1 );
                mb4.insertPrev( mb3 );
                mb1.insertPrev( mb3 );
                Assert.assertNull( mb3.getPrev( ) );
                Assert.assertSame( mb3.getNext( ), mb4 );
                Assert.assertSame( mb4.getPrev( ), mb3 );
                Assert.assertSame( mb4.getNext( ), mb1 );
                Assert.assertSame( mb1.getPrev( ), mb4 );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testInsertPrevChainMiddleChain( ) {
                // given    : mb1 -> mb2
		//          : mb3 -> mb4
                // action   : mb2.insertPrev( mb3 )
                // expected : mb1 -> mb3 -> mb4 -> mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb2.insertPrev( mb1 );
                mb4.insertPrev( mb3 );
                mb2.insertPrev( mb3 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb1 );
                Assert.assertSame( mb3.getNext( ), mb4 );
                Assert.assertSame( mb4.getPrev( ), mb3 );
                Assert.assertSame( mb4.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb4 );
                Assert.assertNull( mb2.getNext( ) );
        }

	
        @Test
        public void testGetPrevHasPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb2.getPrev( )
                // expected : mb1
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
		mb2.setPrev( mb1 );
		Assert.assertSame( mb2.getPrev( ), mb1 );
	}


        @Test
        public void testGetPrevNoPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb1.getPrev( )
                // expected : null 
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
		mb2.setPrev( mb1 );
		Assert.assertNull( mb1.getPrev( ) );
	}


        @Test
        public void testGetPrevNum0HasPrev( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb3.getPrev( 0  )
                // expected : mb3
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb3.setPrev( mb2 );
                mb2.setPrev( mb1 );
                Assert.assertSame( mb3.getPrev( 0 ), mb3 );
        }


        @Test
        public void testGetPrevNumHasPrev( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb3.getPrev( 1 )
                // expected : mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb3.setPrev( mb2 );
                mb2.setPrev( mb1 );
                Assert.assertSame( mb3.getPrev( 1 ), mb2 );
        }


        @Test
        public void testGetPrevNumNoPrev( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb1.getPrev( 1 )
                // expected : null
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb3.setPrev( mb2 );
                mb2.setPrev( mb1 );
                Assert.assertNull( mb1.getPrev( 1 ) );
        }


        @Test
        public void testHasPrevHasPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb2.HasPrev( )
                // expected : true
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.setPrev( mb1 );
		Assert.assertTrue( mb2.hasPrev( ) );
	}


        @Test
        public void testHasPrevNoPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb1.HasPrev( )
                // expected : false
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.setPrev( mb1 );
		Assert.assertFalse( mb1.hasPrev( ) );
	}


        @Test
        public void testNumPrev0( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb1.numPrev( )
                // expected : 0
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb3.setPrev( mb2 );
                mb2.setPrev( mb1 );
                Assert.assertEquals( mb1.numPrev( ), 0 );
        }


        @Test
        public void testNumPrev2( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb3.numPrev( )
                // expected : 2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb3.setPrev( mb2 );
                mb2.setPrev( mb1 );
                Assert.assertEquals( mb3.numPrev( ), 2 );
        }


        @Test
        public void testSetNextSingleLastChain( ) {
                // given    : mb2
                //          : mb1
                // action   : mb1.setNext( mb2 )
                // expected : mb1 -> mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testSetNextSingleMiddleChain( ) {
                // given    : mb0 -> mb2 -> mb3
                //          : mb1
                // action   : mb0.setNext( mb1 )
                // expected : mb0 -> mb1
                //          : mb2 -> mb3
                MessageBlock mb0 = new MessageBlock( );
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb0.setNext( mb2 );
                mb2.setNext( mb3 );
                mb0.setNext( mb1 );
                Assert.assertNull( mb0.getPrev( ) );
                Assert.assertSame( mb0.getNext( ), mb1 );
                Assert.assertSame( mb1.getPrev( ), mb0 );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertSame( mb2.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb2 );
                Assert.assertNull( mb3.getNext( ) );
        }

        @Test
        public void testSetNextChainLastChain( ) {
                // given    : mb1 -> mb2
                //          : mb3 -> mb4
                // action   : mb2.setNext( mb4 )
                // expected : mb1 -> mb2 -> mb4
                //          : mb3
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb3.setNext( mb4 );
                mb2.setNext( mb4 );
                Assert.assertNull( mb3.getPrev( ) );
                Assert.assertNull( mb3.getNext( ) );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertSame( mb2.getNext( ), mb4 );
                Assert.assertSame( mb4.getPrev( ), mb2 );
                Assert.assertNull( mb4.getNext( ) );
        }


        @Test
        public void testSetNextChainMiddleChain( ) {
                // given    : mb1 -> mb2
                //          : mb3 -> mb4
                // action   : mb1.setNext( mb4 )
                // expected : mb1 -> mb4
                //          : mb2
                //          : mb3
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb3.setNext( mb4 );
                mb1.setNext( mb4 );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
                Assert.assertNull( mb3.getPrev( ) );
                Assert.assertNull( mb3.getNext( ) );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb4 );
                Assert.assertSame( mb4.getPrev( ), mb1 );
                Assert.assertNull( mb4.getNext( ) );
        }


        @Test
        public void testInsertNextSingleLastChain( ) {
                // given    : mb2
                // action   : mb1.insertNext( mb2 )
                // expected : mb1 -> mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.insertNext( mb2 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testInsertNextSingleMiddleChain( ) {
                // given    : mb0 -> mb1 -> mb3
                // action   : mb1.insertNext( mb2 )
                // expected : mb0 -> mb1 -> mb2 -> mb3
                MessageBlock mb0 = new MessageBlock( );
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb0.insertNext( mb1 );
                mb1.insertNext( mb3 );
                mb1.insertNext( mb2 );
                Assert.assertNull( mb0.getPrev( ) );
                Assert.assertSame( mb0.getNext( ), mb1 );
                Assert.assertSame( mb1.getPrev( ), mb0 );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertSame( mb2.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb2 );
                Assert.assertNull( mb3.getNext( ) );
        }


        @Test
        public void testInsertNextChainLastChain( ) {
                // given    : mb1 -> mb2
                //          : mb3 -> mb4
                // action   : mb2.insertNext( mb3 )
                // expected : mb1 -> mb2 -> mb3 -> mb4
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb1.insertNext( mb2 );
                mb3.insertNext( mb4 );
                mb2.insertNext( mb3 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertSame( mb2.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb2 );
                Assert.assertSame( mb3.getNext( ), mb4 );
                Assert.assertSame( mb4.getPrev( ), mb3 );
                Assert.assertNull( mb4.getNext( ) );
        }


        @Test
        public void testInsertNextChainMiddleChain( ) {
                // given    : mb1 -> mb2
                //          : mb3 -> mb4
                // action   : mb1.insertNext( mb3 )
                // expected : mb1 -> mb3 -> mb4 -> mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                MessageBlock mb4 = new MessageBlock( );
                mb1.insertNext( mb2 );
                mb3.insertNext( mb4 );
                mb1.insertNext( mb3 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb1 );
                Assert.assertSame( mb3.getNext( ), mb4 );
                Assert.assertSame( mb4.getPrev( ), mb3 );
                Assert.assertSame( mb4.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb4 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testGetNextHasNext( ) {
                // given    : mb1 -> mb2
                // action   : mb1.getNext( )
                // expected : mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                Assert.assertSame( mb1.getNext( ), mb2 );
        }


        @Test
        public void testGetNextNoPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb2.getNext( )
                // expected : null
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                Assert.assertNull( mb2.getNext( ) );
        }

        @Test
        public void testGetNextNum0HasNext( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb1.getNext( 0  )
                // expected : mb1
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertSame( mb1.getNext( 0 ), mb1 );
        }


        @Test
        public void testGetNextNumHasNext( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb1.getNext( 1 )
                // expected : mb2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertSame( mb1.getNext( 1 ), mb2 );
        }


        @Test
        public void testGetNextNumNoNext( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb3.getNext( 1 )
                // expected : null
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertNull( mb3.getNext( 1 ) );
        }


        @Test
        public void testHasNextHasNext( ) {
                // given    : mb1 -> mb2
                // action   : mb1.HasNext( )
                // expected : true
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                Assert.assertTrue( mb1.hasNext( ) );
        }


        @Test
        public void testHasNextNoNext( ) {
                // given    : mb1 -> mb2
                // action   : mb2.HasNext( )
                // expected : false
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                Assert.assertFalse( mb2.hasNext( ) );
        }


        @Test
        public void testNumNext0( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb3.numNext( )
                // expected : 0
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertEquals( mb3.numNext( ), 0 );
        }


        @Test
        public void testNumNext2( ) {
                // given    : mb1 -> mb2 -> mb3
                // action   : mb1.numNext( )
                // expected : 2
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertEquals( mb1.numNext( ), 2 );
        }


        @Test
        public void testGetFirst( ) {
                // given    : mb1 -> mb2 -> mb3
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertSame( mb1.getFirst( ), mb1 );
                Assert.assertSame( mb2.getFirst( ), mb1 );
                Assert.assertSame( mb3.getFirst( ), mb1 );
        }


        @Test
        public void testGetLast( ) {
                // given    : mb1 -> mb2 -> mb3
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertSame( mb1.getLast( ), mb3 );
                Assert.assertSame( mb2.getLast( ), mb3 );
                Assert.assertSame( mb3.getLast( ), mb3 );
        }


        @Test
        public void testNumTotal1( ) {
                // given    : mb1
                MessageBlock mb1 = new MessageBlock( );
                Assert.assertEquals( mb1.numTotal( ), 1 );
        }


        @Test
        public void testNumTotal( ) {
                // given    : mb1 -> mb2 -> mb3
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertEquals( mb1.numTotal( ), 3 );
                Assert.assertEquals( mb2.numTotal( ), 3 );
                Assert.assertEquals( mb3.numTotal( ), 3 );
        }


        @Test
        public void testRemoveSingle( ) {
                // given    : mb1
		MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
		ans = mb1.remove( );
		Assert.assertSame( ans, mb1 );
		Assert.assertNull( mb1.getPrev( ) );
		Assert.assertNull( mb1.getNext( ) );
	}


        @Test
        public void testRemoveFirstChain( ) {
                // given    : mb1 -> mb2 -> mb3
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
		mb1.setNext( mb2 );
		mb2.setNext( mb3 );
                ans = mb1.remove( );
                Assert.assertSame( ans, mb1 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertSame( mb2.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb2 );
                Assert.assertNull( mb3.getNext( ) );
        }


        @Test
        public void testRemoveMiddleChain( ) {
                // given    : mb1 -> mb2 -> mb3
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                ans = mb2.remove( );
                Assert.assertSame( ans, mb2 );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb3 );
                Assert.assertSame( mb3.getPrev( ), mb1 );
                Assert.assertNull( mb3.getNext( ) );
        }


        @Test
        public void testRemoveLastChain( ) {
                // given    : mb1 -> mb2 -> mb3
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                ans = mb3.remove( );
                Assert.assertSame( ans, mb3 );
                Assert.assertNull( mb3.getPrev( ) );
                Assert.assertNull( mb3.getNext( ) );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testGetPrevRemovalHasPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb2.getPrevRemoval( )
                // expected : mb1
		MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.setPrev( mb1 );
		ans = mb2.getPrevRemoval( );
                Assert.assertSame( ans, mb1 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testGetPrevRemovalNoPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb1.getPrevRemoval( )
                // expected : null
		MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.setPrev( mb1 );
		ans = mb1.getPrevRemoval( );
                Assert.assertNull( ans );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
        }


       @Test
        public void testGetNextRemovalHasPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb1.getNextRemoval( )
                // expected : mb2
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                ans = mb1.getNextRemoval( );
                Assert.assertSame( ans, mb2 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testGetNextRemovalNoPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb2.getPrevRemoval( )
                // expected : null
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                ans = mb2.getNextRemoval( );
                Assert.assertNull( ans );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testRemoveAllPrevHasPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb2.removeAllPrev( )
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.setPrev( mb1 );
                ans = mb2.removeAllPrev( );
                Assert.assertSame( ans, mb2 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testRemoveAllPrevNoPrev( ) {
                // given    : mb1 -> mb2
                // action   : mb1.removeAllPrev( )
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb2.setPrev( mb1 );
                ans = mb1.removeAllPrev( );
                Assert.assertSame( ans, mb1 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testRemoveAllNextHasNext( ) {
                // given    : mb1 -> mb2 
                // action   : mb1.removeAllNext( )
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                ans = mb1.removeAllNext( );
                Assert.assertSame( ans, mb1 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertNull( mb2.getPrev( ) );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testRemoveAllNextNoNext( ) {
                // given    : mb1 -> mb2
                // action   : mb1.removeAllNext( )
                MessageBlock ans;
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb1.setNext( mb2 );
                ans = mb2.removeAllNext( );
                Assert.assertSame( ans, mb2 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertSame( mb1.getNext( ), mb2 );
                Assert.assertSame( mb2.getPrev( ), mb1 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testGetPriority( ) {
		MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 200 );
		Assert.assertEquals( mb1.getPriority( ), 200 );
	}


        @Test
        public void testSetPriority( ) {
		MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 200 );
		Assert.assertEquals( mb1.getPriority( ), 200 );
		mb1.setPriority( 555 );
		Assert.assertEquals( mb1.getPriority( ), 555 );
	}


        @Test
        public void testResetPriority( ) {
		MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 200 );
		Assert.assertEquals( mb1.getPriority( ), 200 );
		mb1.resetPriority( );
		Assert.assertEquals( mb1.getPriority( ), 100000 );
	}


        @Test
        public void testGetIsData( ) {
		MessageBlock mb1 = new MessageBlock( );
		mb1.setIsData( );
		Assert.assertTrue( mb1.getIsData( ) );
	}


        @Test
        public void testSetIsData( ) {
		MessageBlock mb1 = new MessageBlock( );
		mb1.setIsData( );
		Assert.assertTrue( mb1.getIsData( ) );
	}


        @Test
        public void testGetIsControl( ) {
                MessageBlock mb1 = new MessageBlock( );
                mb1.setIsControl( );
                Assert.assertTrue( mb1.getIsControl( ) );
        }


        @Test
        public void testSetIsControl( ) {
                MessageBlock mb1 = new MessageBlock( );
                mb1.setIsControl( );
                Assert.assertTrue( mb1.getIsControl( ) );
        }


        @Test
        public void testSetMessage( ) {
		MessageBlock mb1 = new MessageBlock( );
		Integer i = new Integer( 1 );
		mb1.setMessage( i );
		Assert.assertSame( mb1.getMessage( ), i );
	}


        @Test
        public void testClearMessage( ) {
		MessageBlock mb1 = new MessageBlock( new Integer( 1 ) );
		mb1.clearMessage( );
		Assert.assertNull( mb1.getMessage( ) );
	}


        @Test
        public void testHasMessage( ) {
		MessageBlock mb1 = new MessageBlock( new Integer( 1 ) );
		Assert.assertNotNull( mb1.getMessage( ) );
	}


        @Test
        public void testGetMessage( ) {
		Integer i = new Integer( 1 );
		MessageBlock mb1 = new MessageBlock( i );
		Assert.assertSame( mb1.getMessage( ), i );
	}


        @Test
        public void testGetMessageBoolean( ) {
		Boolean i = new Boolean( true );
		MessageBlock mb1 = new MessageBlock( i );
		Assert.assertTrue( mb1.getMessageBoolean( ).booleanValue( ) );
	}


        @Test
        public void testGetMessageByte( ) {
		byte b1 = 5;
		byte b2 = 5;
		Byte i = new Byte( b1 );
		Byte j = new Byte( b2 );
		MessageBlock mb1 = new MessageBlock( i );
		Assert.assertEquals( mb1.getMessageByte( ).compareTo( j ), 0 );
	}


        @Test
        public void testGetMessageInteger( ) {
                Integer i = new Integer( 3 );
                MessageBlock mb1 = new MessageBlock( i );
                Assert.assertEquals( mb1.getMessageInteger( ).intValue( ), 3 );
        }


        @Test
        public void testGetMessageFloat( ) {
                Float i = new Float( 3.0 );
                MessageBlock mb1 = new MessageBlock( i );
                Assert.assertEquals( mb1.getMessageFloat( ).floatValue( ), (float)3.0 );
        }


        @Test
        public void testGetMessageDouble( ) {
                Double i = new Double( 3.0 );
                MessageBlock mb1 = new MessageBlock( i );
                Assert.assertEquals( mb1.getMessageDouble( ).doubleValue( ), (double)3.0 );
        }


        @Test
        public void testGetMessageString( ) {
		String i = new String( "a" );
		String j = new String( "a" );
                MessageBlock mb1 = new MessageBlock( i );
                Assert.assertEquals( mb1.getMessageString( ).compareTo( j ), 0 );
        }


        @Test
        public void testToStringMessageStringSingleNoMessage( ) {
                // given    : mb0
                MessageBlock mb0 = new MessageBlock( );
                String ans = new String( "mb[0]=-null-" );
                Assert.assertEquals( mb0.toStringMessageString( ).compareTo( ans ), 0 );
        }


        @Test
        public void testToStringMessageStringSingleHasMessage( ) {
                // given    : mb0
                MessageBlock mb0 = new MessageBlock( new String( "a" ) );
		String ans = new String( "mb[0]=a" );
                Assert.assertEquals( mb0.toStringMessageString( ).compareTo( ans ), 0 );
	}


        @Test
        public void testToStringMessageStringChain( ) {
                // given    : mb0 -> mb1 -> mb2 -> mb3
                MessageBlock mb0 = new MessageBlock( new String( "a" ) );
                MessageBlock mb1 = new MessageBlock( new String( "b" ) );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( new String( "d" ) );
		String ans0 = new String( "mb[0]=a ---> mb[1]=b ---> mb[2]=-null- ---> mb[3]=d" );
		String ans1 = new String( "mb[-1]=a <--- mb[0]=b ---> mb[1]=-null- ---> mb[2]=d" );
		String ans2 = new String( "mb[-2]=a <--- mb[-1]=b <--- mb[0]=-null- ---> mb[1]=d" );
		String ans3 = new String( "mb[-3]=a <--- mb[-2]=b <--- mb[-1]=-null- <--- mb[0]=d" );
                mb0.setNext( mb1 );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertEquals( mb0.toStringMessageString( ).compareTo( ans0 ), 0 );
                Assert.assertEquals( mb1.toStringMessageString( ).compareTo( ans1 ), 0 );
                Assert.assertEquals( mb2.toStringMessageString( ).compareTo( ans2 ), 0 );
                Assert.assertEquals( mb3.toStringMessageString( ).compareTo( ans3 ), 0 );
	}


        @Test
        public void testToStringSingleNoMessage( ) {
                // given    : mb0
                MessageBlock mb0 = new MessageBlock( );
                String ans = new String( "mb[0]=-null-" );
                Assert.assertEquals( mb0.toString( ).compareTo( ans ), 0 );
        }


        @Test
        public void testToStringSingleHasMessage( ) {
                // given    : mb0
                MessageBlock mb0 = new MessageBlock( new String( "a" ) );
                String ans = new String( "mb[0]=-message-" );
                Assert.assertEquals( mb0.toString( ).compareTo( ans ), 0 );
        }


        @Test
        public void testToStringChain( ) {
                // given    : mb0 -> mb1 -> mb2 -> mb3
                MessageBlock mb0 = new MessageBlock( new String( "a" ) );
                MessageBlock mb1 = new MessageBlock( new String( "b" ) );
                MessageBlock mb2 = new MessageBlock( );
                MessageBlock mb3 = new MessageBlock( new String( "d" ) );
                String ans0 = new String( "mb[0]=-message- ---> mb[1]=-message- ---> mb[2]=-null- ---> mb[3]=-message-" );
                String ans1 = new String( "mb[-1]=-message- <--- mb[0]=-message- ---> mb[1]=-null- ---> mb[2]=-message-" );
                String ans2 = new String( "mb[-2]=-message- <--- mb[-1]=-message- <--- mb[0]=-null- ---> mb[1]=-message-" );
                String ans3 = new String( "mb[-3]=-message- <--- mb[-2]=-message- <--- mb[-1]=-null- <--- mb[0]=-message-" );
                mb0.setNext( mb1 );
                mb1.setNext( mb2 );
                mb2.setNext( mb3 );
                Assert.assertEquals( mb0.toString( ).compareTo( ans0 ), 0 );
                Assert.assertEquals( mb1.toString( ).compareTo( ans1 ), 0 );
                Assert.assertEquals( mb2.toString( ).compareTo( ans2 ), 0 );
                Assert.assertEquals( mb3.toString( ).compareTo( ans3 ), 0 );
        }


	@Test
	public void testSetPrevMb( ) {
		// given     : mb0 -> mb2
		//           : mb1
		// expected  : mb0 -> mb1 -> mb2
		MessageBlock mb0 = new MessageBlock( );
		MessageBlock mb1 = new MessageBlock( );
		MessageBlock mb2 = new MessageBlock( );
		mb2.setPrev( mb0 );
		mb2.setPrevMb( mb1 );
		Assert.assertNull( mb0.getPrev( ) );
		Assert.assertSame( mb0.getNext( ), mb2 );
		Assert.assertNull( mb1.getPrev( ) );
		Assert.assertNull( mb1.getNext( ) );
		Assert.assertSame( mb2.getPrev( ), mb1 );
		Assert.assertNull( mb2.getNext( ) );
	}


	@Test
	public void testGetPrevMb( ) {
		// given     : mb0 -> mb1
		MessageBlock mb0 = new MessageBlock( );
		MessageBlock mb1 = new MessageBlock( );
		mb1.setPrev( mb0 );
		Assert.assertNull( mb0.getPrevMb( ) );
		Assert.assertSame( mb1.getPrevMb( ), mb0  );
	}


        @Test
        public void testSetNextMb( ) {
                // given     : mb0 -> mb2
                //           : mb1
                // expected  : mb0 -> mb1 -> mb2
                MessageBlock mb0 = new MessageBlock( );
                MessageBlock mb1 = new MessageBlock( );
                MessageBlock mb2 = new MessageBlock( );
                mb0.setNext( mb2 );
                mb0.setNextMb( mb1 );
                Assert.assertNull( mb0.getPrev( ) );
                Assert.assertSame( mb0.getNext( ), mb1 );
                Assert.assertNull( mb1.getPrev( ) );
                Assert.assertNull( mb1.getNext( ) );
                Assert.assertSame( mb2.getPrev( ), mb0 );
                Assert.assertNull( mb2.getNext( ) );
        }


        @Test
        public void testGetNextMb( ) {
                // given     : mb0 -> mb1
                MessageBlock mb0 = new MessageBlock( );
                MessageBlock mb1 = new MessageBlock( );
                mb0.setNext( mb1 );
                Assert.assertNull( mb1.getNextMb( ) );
                Assert.assertSame( mb0.getNextMb( ), mb1  );
        }


	@Test
	public void testSerializationSingle( ) {
                MessageBlock mb0 = new MessageBlock( new Integer( 3 ) );
		ByteArrayOutputStream bos = new ByteArrayOutputStream( );

		try {
			ObjectOutput out = new ObjectOutputStream( bos );
			out.writeObject( mb0 );
			out.close( );
		} catch ( IOException e ) { }

		byte[] buf = bos.toByteArray( );

		MessageBlock result = null;

		try {
			ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( buf ) );
			result = (MessageBlock)in.readObject( );
			in.close( );
		} catch ( IOException e ) {

		} catch ( ClassNotFoundException e ) { }

		if ( result != null ) {
			int res0 = result.getMessageInteger( );
                        Assert.assertEquals( 3, res0 );
		} else {
			Assert.assertFalse( true );
		}

		//System.out.println( "single = " + buf.length );
	}


        @Test
        public void testSerializationChain( ) {
                MessageBlock mb0 = new MessageBlock( new Integer( 3 ) );
                MessageBlock mb1 = new MessageBlock( new Integer( 5 ), mb0 );
                MessageBlock mb2 = new MessageBlock( new Integer( 9 ), mb1 );
                ByteArrayOutputStream bos = new ByteArrayOutputStream( );

                try {
                        ObjectOutput out = new ObjectOutputStream( bos );
                        out.writeObject( mb0 );
                        out.close( );
                } catch ( IOException e ) { }

                byte[] buf = bos.toByteArray( );

                MessageBlock result = null;

                try {
                        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( buf ) );
                        result = (MessageBlock)in.readObject( );
                        in.close( );
                } catch ( IOException e ) {

                } catch ( ClassNotFoundException e ) { }

                if ( result != null ) {
			int res0 = result.getMessageInteger( );
                        Assert.assertEquals( 3, res0 );
			result = result.getNext( );
			int res1 = result.getMessageInteger( );
                        Assert.assertEquals( 5, res1 );
			result = result.getNext( );
			int res2 = result.getMessageInteger( );
                        Assert.assertEquals( 9, res2 );
                } else {
                        Assert.assertFalse( true );
                }

		//System.out.println( "single = " + buf.length );
        }
}
