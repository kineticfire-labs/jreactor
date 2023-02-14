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

import java.lang.reflect.Field;

import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.HashSet;

import com.kineticfire.patterns.reactor.ChannelSelector;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.handler.Handler;
import com.kineticfire.patterns.reactor.MockCompositeSelector_ChannelSelector;
import com.kineticfire.patterns.reactor.Handler_ChannelSelector;


public class ChannelSelector_Test {

	MockCompositeSelector_ChannelSelector compositeSelector;
	ChannelSelector channelSelector;


	@Test
	public void testRegisterSingle( ) {
		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );



		Handler_ChannelSelector handler1 = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9011 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testRegisterSingle'" );
                }

                Handle handle = channelSelector.register( client, handler1, EventMask.CREAD );

		Assert.assertNotNull( handle );
		Assert.assertTrue( channelSelector.isRegistered( client ) );
		Assert.assertTrue( channelSelector.isRegistered( handle ) );
	}


        @Test
        public void testRegisterMultiUnique( ) {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handler1 = new Handler_ChannelSelector( );
		Handler_ChannelSelector handler2 = new Handler_ChannelSelector( );

                SocketChannel client1 = null;
                SocketChannel client2 = null;
                boolean done1 = false;
                boolean done2 = false;
                try {
                        client1 = SocketChannel.open( );
                        client2 = SocketChannel.open( );
                        client1.configureBlocking( false );
                        client2.configureBlocking( false );
                        done1 = client1.connect( new InetSocketAddress( "127.0.0.1", 9001 ) );
                        done2 = client2.connect( new InetSocketAddress( "127.0.0.1", 9002 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testRegisterMultiUnique'" );
                }

                Handle handle1 = channelSelector.register( client1, handler1, EventMask.CONNECT | EventMask.CREAD );
                Handle handle2 = channelSelector.register( client2, handler2, EventMask.CONNECT | EventMask.CREAD );

                Assert.assertNotNull( handle1 );
                Assert.assertNotNull( handle2 );
                Assert.assertTrue( channelSelector.isRegistered( client1 ) );
                Assert.assertTrue( channelSelector.isRegistered( client2 ) );
                Assert.assertTrue( channelSelector.isRegistered( handle1 ) );
                Assert.assertTrue( channelSelector.isRegistered( handle2 ) );
        }


        @Test
        public void testRegisterMultiNonunique( ) {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handlerA = new Handler_ChannelSelector( );

                SocketChannel client1 = null;
                SocketChannel client2 = null;
                boolean done1 = false;
                boolean done2 = false;
                try {
                        client1 = SocketChannel.open( );
                        client2 = SocketChannel.open( );
                        client1.configureBlocking( false );
                        client2.configureBlocking( false );
                        done1 = client1.connect( new InetSocketAddress( "127.0.0.1", 9003 ) );
                        done2 = client2.connect( new InetSocketAddress( "127.0.0.1", 9004 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testRegisterMultiNonunique'" );
                }

                Handle handle1 = channelSelector.register( client1, handlerA, EventMask.CONNECT | EventMask.CREAD );
                Handle handle2 = channelSelector.register( client2, handlerA, EventMask.CONNECT | EventMask.CREAD );

                Assert.assertNotNull( handle1 );
                Assert.assertNotNull( handle2 );
                Assert.assertTrue( channelSelector.isRegistered( client1 ) );
                Assert.assertTrue( channelSelector.isRegistered( client2 ) );
                Assert.assertTrue( channelSelector.isRegistered( handle1 ) );
                Assert.assertTrue( channelSelector.isRegistered( handle2 ) );
        }


        @Test
        public void testInterestOps( ) throws NoSuchFieldException, IllegalAccessException {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handler = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9005 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testInterestOps'" );
                }

                Handle handle = channelSelector.register( client, handler, EventMask.NOOP );


                compositeSelector.setInterestOps( EventMask.NOOP );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.NOOP );
                channelSelector.interestOps( handle, EventMask.CONNECT );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.CONNECT );
        }


        @Test
        public void testIsRegisteredSelectableChannel( ) {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handler = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9006 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testIsRegisteredSelectableChannel'" );
                }

                Handle handle = channelSelector.register( client, handler, EventMask.CONNECT | EventMask.CREAD );

                Assert.assertTrue( channelSelector.isRegistered( client ) );
        }


        @Test
        public void testIsRegisteredHandle( ) {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handler = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9007 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testIsRegisteredHandle'" );
                }

                Handle handle = channelSelector.register( client, handler, EventMask.CONNECT | EventMask.CREAD );

                Assert.assertNotNull( handle );
                Assert.assertTrue( channelSelector.isRegistered( handle ) );
        }


        @Test
        public void testDeregister( ) {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handler = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9008 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testDeregister'" );
                }

                Handle handle = channelSelector.register( client, handler, EventMask.CONNECT | EventMask.CREAD );

                Assert.assertTrue( channelSelector.isRegistered( handle ) );
                Assert.assertTrue( channelSelector.isRegistered( client ) );

		channelSelector.deregister( handle );
                Assert.assertFalse( channelSelector.isRegistered( handle ) );
                Assert.assertFalse( channelSelector.isRegistered( client ) );
        }


        @Test
        public void testCheckin( ) throws IllegalAccessException, NoSuchFieldException {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


                Handler_ChannelSelector handler = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9009 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testDeregister'" );
                }

                Handle handle = channelSelector.register( client, handler, EventMask.CREAD );

                Field firedF = ChannelSelector.class.getDeclaredField( "fired" );
                firedF.setAccessible( true );
                Set fired = (HashSet)firedF.get( channelSelector );

                compositeSelector.setInterestOps( EventMask.CONNECT );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.CONNECT );
                fired.add( handle );
                Assert.assertTrue( fired.contains( handle ) );

                channelSelector.resumeSelection( handle );

                Assert.assertFalse( fired.contains( handle ) );
        }


        @Test
        public void testResumeSelection( ) throws IllegalAccessException, NoSuchFieldException {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		Handler_ChannelSelector handler = new Handler_ChannelSelector( );

                SocketChannel client = null;
                boolean done = false;
                try {
                        client = SocketChannel.open( );
                        client.configureBlocking( false );
                        done = client.connect( new InetSocketAddress( "127.0.0.1", 9010 ) );
                } catch ( IOException e ) {
                        System.out.println( "IOEXception in 'ChannelSelector_Test:testDeregister'" );
                }

                Handle handle = channelSelector.register( client, handler, EventMask.CREAD );

                Field firedF = ChannelSelector.class.getDeclaredField( "fired" );
                firedF.setAccessible( true );
                Set fired = (HashSet)firedF.get( channelSelector );

		compositeSelector.setInterestOps( EventMask.CONNECT );
                Assert.assertEquals( compositeSelector.getInterestOps( ), EventMask.CONNECT );
		fired.add( handle );
		Assert.assertTrue( fired.contains( handle ) );

		channelSelector.resumeSelection( handle );

		Assert.assertFalse( fired.contains( handle ) );
	}


        @Test
        public void testShutdown( ) throws NoSuchFieldException, IllegalAccessException {

		MockCompositeSelector_ChannelSelector compositeSelector = new MockCompositeSelector_ChannelSelector( );
		ChannelSelector channelSelector = new ChannelSelector( compositeSelector );


		channelSelector.shutdown( );

                Field guardF = ChannelSelector.class.getDeclaredField( "guard" );
                guardF.setAccessible( true );
                Byte guard = (Byte)guardF.get( channelSelector );

                Field doneF = ChannelSelector.class.getDeclaredField( "done" );
                doneF.setAccessible( true );
                Boolean done = (Boolean)doneF.get( channelSelector );

		synchronized( done ) {
			Assert.assertTrue( done );
		}

	}

}
