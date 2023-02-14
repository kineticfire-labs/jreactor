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

import java.util.Set;
import java.util.HashSet;

import java.lang.reflect.Field;

import com.kineticfire.patterns.reactor.BlockingTask;
import com.kineticfire.patterns.reactor.BlockingTaskSet;
import com.kineticfire.patterns.reactor.Handle;
//import com.kineticfire.patterns.reactor.GenericRunnable_BlockingTask;
import com.kineticfire.patterns.reactor.MockBlockingSelector_BlockingTaskSet;



public class BlockingTaskSet_Test {


	@Test
	public void testConstructorNoArg( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		BlockingTaskSet taskSet = new BlockingTaskSet( );

                Field blockingSelectorF = BlockingTaskSet.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                Assert.assertNull( blockingSelectorF.get( taskSet ) );

                Field handleF = BlockingTaskSet.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertNull( handleF.get( taskSet ) );

                Field runnableSetF = BlockingTaskSet.class.getDeclaredField( "runnableSet" );
                runnableSetF.setAccessible( true );
                Assert.assertNull( runnableSetF.get( taskSet ) );
	}


        @Test
        public void testConstructorArgs( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockBlockingSelector_BlockingTaskSet blockingSelector = new MockBlockingSelector_BlockingTaskSet( );
		Handle handle = new Handle( );
		Set<Runnable> runnableSet = new HashSet<Runnable>( );
                BlockingTaskSet taskSet = new BlockingTaskSet( blockingSelector, handle, runnableSet );

                Field blockingSelectorF = BlockingTaskSet.class.getDeclaredField( "blockingSelector" );
                blockingSelectorF.setAccessible( true );
                Assert.assertSame( blockingSelectorF.get( taskSet ), blockingSelector );

                Field handleF = BlockingTaskSet.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Assert.assertSame( handleF.get( taskSet ), handle );

                Field runnableSetF = BlockingTaskSet.class.getDeclaredField( "runnableSet" );
                runnableSetF.setAccessible( true );
                Assert.assertSame( runnableSetF.get( taskSet ), runnableSet );
        }

        @Test
        public void testCompleted0Runnable( ) {
                MockBlockingSelector_BlockingTaskSet blockingSelector = new MockBlockingSelector_BlockingTaskSet( );
                Handle handle = new Handle( );
                Set<Runnable> runnableSet = new HashSet<Runnable>( );
                BlockingTaskSet taskSet = new BlockingTaskSet( blockingSelector, handle, runnableSet );

		Assert.assertNull( blockingSelector.getTriggerHandle( ) );
		Runnable_BlockingTaskSet runnable = new Runnable_BlockingTaskSet( );
		taskSet.completed( runnable );
		Assert.assertSame( blockingSelector.getTriggerHandle( ), handle );
	}


        @Test
        public void testCompleted1Runnable( ) {
                MockBlockingSelector_BlockingTaskSet blockingSelector = new MockBlockingSelector_BlockingTaskSet( );
                Handle handle = new Handle( );
                Set<Runnable> runnableSet = new HashSet<Runnable>( );
                Runnable_BlockingTaskSet runnable1 = new Runnable_BlockingTaskSet( );
		runnableSet.add( runnable1 );
                BlockingTaskSet taskSet = new BlockingTaskSet( blockingSelector, handle, runnableSet );

                Assert.assertNull( blockingSelector.getTriggerHandle( ) );
                taskSet.completed( runnable1 );
                Assert.assertSame( blockingSelector.getTriggerHandle( ), handle );
        }


        @Test
        public void testCompleted3Runnable( ) {
                MockBlockingSelector_BlockingTaskSet blockingSelector = new MockBlockingSelector_BlockingTaskSet( );
                Handle handle = new Handle( );
                Set<Runnable> runnableSet = new HashSet<Runnable>( );
                Runnable_BlockingTaskSet runnable1 = new Runnable_BlockingTaskSet( );
                Runnable_BlockingTaskSet runnable2 = new Runnable_BlockingTaskSet( );
                Runnable_BlockingTaskSet runnable3 = new Runnable_BlockingTaskSet( );
                runnableSet.add( runnable1 );
                runnableSet.add( runnable2 );
                runnableSet.add( runnable3 );
                BlockingTaskSet taskSet = new BlockingTaskSet( blockingSelector, handle, runnableSet );

                Assert.assertNull( blockingSelector.getTriggerHandle( ) );
                taskSet.completed( runnable1 );
                Assert.assertNull( blockingSelector.getTriggerHandle( ) );
                taskSet.completed( runnable2 );
                Assert.assertNull( blockingSelector.getTriggerHandle( ) );
                taskSet.completed( runnable3 );
                Assert.assertSame( blockingSelector.getTriggerHandle( ), handle );
        }



}
