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
import java.util.List;
import java.util.Set;

import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.handler.Handler;

import com.kineticfire.patterns.reactor.MockJReactor_HandlerAdapter;
import com.kineticfire.patterns.reactor.GenericHandler_HandlerAdapter;



public class HandlerAdapter_Test {

	@Test
	public void testConstructorNoArg( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		HandlerAdapter adapter = new HandlerAdapter( );

		Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
		jreactorF.setAccessible( true );
		Assert.assertNull( jreactorF.get( adapter ) );

		Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
		handlerF.setAccessible( true );
		Assert.assertNull( handlerF.get( adapter ) );

		Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
		handleF.setAccessible( true );
		Assert.assertNull( handleF.get( adapter ) );

		Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
		readyOpsF.setAccessible( true );
		Assert.assertEquals( readyOpsF.get( adapter ), 0 );

		Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
		infoF.setAccessible( true );
		Assert.assertNull( infoF.get( adapter ) );

		Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
		commandsF.setAccessible( true );
		List commands = (List)commandsF.get( adapter );
		Assert.assertEquals( commands.size( ), 0 );
	}


        @Test
        public void testConstructorJReactor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );	
                HandlerAdapter adapter = new HandlerAdapter( jreactor );

                Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( adapter ), jreactor );

                Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
                handlerF.setAccessible( true );
                Assert.assertNull( handlerF.get( adapter ) );

                Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertNull( handleF.get( adapter ) );

                Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( adapter ), 0 );

                Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                Assert.assertNull( infoF.get( adapter ) );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 0 );
        }


        @Test
        public void testConstructorJReactorHandler( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
		GenericHandler_HandlerAdapter handler = new GenericHandler_HandlerAdapter( );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler );

                Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( adapter ), jreactor );

                Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
                handlerF.setAccessible( true );
                Assert.assertSame( handlerF.get( adapter ), handler );

                Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertNull( handleF.get( adapter ) );

                Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( adapter ), 0 );

                Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                Assert.assertNull( infoF.get( adapter ) );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 0 );
        }

        @Test
        public void testConstructorJReactorHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
		Handle handle = new Handle( );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handle );

                Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( adapter ), jreactor );

                Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
                handlerF.setAccessible( true );
                Assert.assertNull( handlerF.get( adapter ) );

                Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertSame( handleF.get( adapter ), handle );

                Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( adapter ), 0 );

                Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                Assert.assertNull( infoF.get( adapter ) );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 0 );

		Handle handle2 = new Handle( );
		Assert.assertFalse( adapter.eventHandleNoted( handle2 ) );
		Assert.assertTrue( adapter.eventHandleNoted( handle ) );
        }


        @Test
        public void testConstructorJReactorHandlerHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
		GenericHandler_HandlerAdapter handler = new GenericHandler_HandlerAdapter( );
                Handle handle = new Handle( );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler, handle );

                Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( adapter ), jreactor );

                Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
                handlerF.setAccessible( true );
                Assert.assertSame( handlerF.get( adapter ), handler );

                Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertSame( handleF.get( adapter ), handle );

                Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( adapter ), 0 );

                Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                Assert.assertNull( infoF.get( adapter ) );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 0 );
        }

        @Test
        public void testConstructorJReactorHandlerHandleOps( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
                GenericHandler_HandlerAdapter handler = new GenericHandler_HandlerAdapter( );
                Handle handle = new Handle( );
		int ops = 2;
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler, handle, ops );

                Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( adapter ), jreactor );

                Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
                handlerF.setAccessible( true );
                Assert.assertSame( handlerF.get( adapter ), handler );

                Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertSame( handleF.get( adapter ), handle );

                Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( adapter ), ops );

                Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                Assert.assertNull( infoF.get( adapter ) );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 0 );
        }


        @Test
        public void testConstructorJReactorHandlerHandleOpsInfo( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
                GenericHandler_HandlerAdapter handler = new GenericHandler_HandlerAdapter( );
                Handle handle = new Handle( );
                int ops = 2;
		MessageBlock info = new MessageBlock( new Integer( 3 ) );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler, handle, ops, info );

                Field jreactorF = HandlerAdapter.class.getDeclaredField( "jreactor" );
                jreactorF.setAccessible( true );
                Assert.assertSame( jreactorF.get( adapter ), jreactor );

                Field handlerF = HandlerAdapter.class.getDeclaredField( "handler" );
                handlerF.setAccessible( true );
                Assert.assertSame( handlerF.get( adapter ), handler );

                Field handleF = HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertSame( handleF.get( adapter ), handle );

                Field readyOpsF = HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( adapter ), ops );

                Field infoF = HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                MessageBlock infoR = (MessageBlock)infoF.get( adapter );
                Assert.assertEquals( infoR.getMessageInteger( ), info.getMessageInteger( ) );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );
                Assert.assertEquals( commands.size( ), 0 );
        }


        @Test
        public void testRun( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {

                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
                GenericHandler_HandlerAdapter handler = new GenericHandler_HandlerAdapter( );
                Handle handle = new Handle( );
                int ops = 2;
                MessageBlock info = new MessageBlock( new Integer( 3 ) );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler, handle, ops, info );
		adapter.run( );


                Field handleF = GenericHandler_HandlerAdapter.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
		Handle handleR = (Handle)handleF.get( handler );
                Assert.assertSame( handleR, handle );

                Field readyOpsF = GenericHandler_HandlerAdapter.class.getDeclaredField( "readyOps" );
                readyOpsF.setAccessible( true );
                Assert.assertEquals( readyOpsF.get( handler ), ops );

                Field infoF = GenericHandler_HandlerAdapter.class.getDeclaredField( "info" );
                infoF.setAccessible( true );
                MessageBlock infoR = (MessageBlock)infoF.get( handler );
                Assert.assertEquals( infoR.getMessageInteger( ), info.getMessageInteger( ) );


                Field handlerFj = MockJReactor_HandlerAdapter.class.getDeclaredField( "handler" );
                handlerFj.setAccessible( true );
                Handler handlerRj = (Handler)handlerFj.get( jreactor );
                Assert.assertSame( handlerRj, handler );

                Field handleFj = MockJReactor_HandlerAdapter.class.getDeclaredField( "handle" );
                handleFj.setAccessible( true );
                Handle handleRj = (Handle)handleFj.get( jreactor );
                Assert.assertSame( handleRj, handle );

                Field commandsFj = MockJReactor_HandlerAdapter.class.getDeclaredField( "commands" );
                commandsFj.setAccessible( true );
                List commandsj = (List)commandsFj.get( jreactor );
                Assert.assertEquals( commandsj.size( ), 0 );
        }


        @Test
        public void testResumeSelection( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {

                MockJReactor_HandlerAdapter jreactor = new MockJReactor_HandlerAdapter( );
                GenericHandler_HandlerAdapter handler = new GenericHandler_HandlerAdapter( );
                Handle handle = new Handle( );
                int ops = 2;
                MessageBlock info = new MessageBlock( new Integer( 3 ) );
                HandlerAdapter adapter = new HandlerAdapter( jreactor, handler, handle, ops, info );
                adapter.resumeSelection( );


                Field handlerFj = MockJReactor_HandlerAdapter.class.getDeclaredField( "handler" );
                handlerFj.setAccessible( true );
                Handler handlerRj = (Handler)handlerFj.get( jreactor );
                Assert.assertSame( handlerRj, handler );

                Field handleFj = MockJReactor_HandlerAdapter.class.getDeclaredField( "handle" );
                handleFj.setAccessible( true );
                Handle handleRj = (Handle)handleFj.get( jreactor );
                Assert.assertSame( handleRj, handle );

                Field commandsFj = MockJReactor_HandlerAdapter.class.getDeclaredField( "commands" );
                commandsFj.setAccessible( true );
                List commandsj = (List)commandsFj.get( jreactor );
                Assert.assertEquals( commandsj.size( ), 0 );

        }



	@Test
        public void testCommand( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		HandlerAdapter adapter = new HandlerAdapter( );

                Field commandsF = HandlerAdapter.class.getDeclaredField( "commands" );
                commandsF.setAccessible( true );
                List commands = (List)commandsF.get( adapter );

                Assert.assertEquals( commands.size( ), 0 );
		MessageBlock mb1 = new MessageBlock( );
		adapter.command( mb1 );
                Assert.assertEquals( commands.size( ), 1 );
		MessageBlock mb2 = new MessageBlock( );
		adapter.command( mb2 );
                Assert.assertEquals( commands.size( ), 2 );
	}


        @Test
        public void testEventHandleNoted( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                HandlerAdapter adapter = new HandlerAdapter( );

                Field eventHandleNotedF = HandlerAdapter.class.getDeclaredField( "eventHandleNoted" );
                eventHandleNotedF.setAccessible( true );
                Set<Handle> eventHandleNoted = (Set<Handle>)eventHandleNotedF.get( adapter );

                Assert.assertEquals( eventHandleNoted.size( ), 0 );
		Handle handle1 = new Handle( );
		boolean result1 = adapter.eventHandleNoted( handle1 );
		Assert.assertFalse( result1 );
                Assert.assertEquals( eventHandleNoted.size( ), 1 );

		boolean result2 = adapter.eventHandleNoted( handle1 );
		Assert.assertTrue( result2 );
                Assert.assertEquals( eventHandleNoted.size( ), 1 );

		Handle handle3 = new Handle( );
		boolean result3 = adapter.eventHandleNoted( handle3 );
		Assert.assertFalse( result3 );
                Assert.assertEquals( eventHandleNoted.size( ), 2 );

		boolean result4 = adapter.eventHandleNoted( handle3 );
		Assert.assertTrue( result4 );
                Assert.assertEquals( eventHandleNoted.size( ), 2 );
        }

}
