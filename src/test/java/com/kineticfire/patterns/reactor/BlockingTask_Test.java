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


import com.kineticfire.patterns.reactor.BlockingTask;
import com.kineticfire.patterns.reactor.BlockingTaskSet;
import com.kineticfire.patterns.reactor.GenericRunnable_BlockingTask;
import com.kineticfire.patterns.reactor.MockBlockingTaskSet_BlockingTask;



public class BlockingTask_Test {


	@Test
	public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		BlockingTask task = new BlockingTask( );

                Field blockingTaskSetF = BlockingTask.class.getDeclaredField( "blockingTaskSet" );
                blockingTaskSetF.setAccessible( true );
                Assert.assertNull( blockingTaskSetF.get( task ) );

                Field runnableF = BlockingTask.class.getDeclaredField( "runnable" );
                runnableF.setAccessible( true );
                Assert.assertNull( runnableF.get( task ) );
	}


        @Test
        public void testConstructorTaskRunnable( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                BlockingTaskSet taskSet = new BlockingTaskSet( );
		GenericRunnable_BlockingTask runnable = new GenericRunnable_BlockingTask( );
                BlockingTask task = new BlockingTask( taskSet, runnable );

                Field blockingTaskSetF = BlockingTask.class.getDeclaredField( "blockingTaskSet" );
                blockingTaskSetF.setAccessible( true );
                Assert.assertSame( blockingTaskSetF.get( task ), taskSet );

                Field runnableF = BlockingTask.class.getDeclaredField( "runnable" );
                runnableF.setAccessible( true );
                Assert.assertSame( runnableF.get( task ), runnable );
        }

	
	@Test
	public void testRun( ) {
		MockBlockingTaskSet_BlockingTask taskSet = new MockBlockingTaskSet_BlockingTask( );
		GenericRunnable_BlockingTask runnable = new GenericRunnable_BlockingTask( taskSet );
                BlockingTask task = new BlockingTask( taskSet, runnable );

		Assert.assertNull( taskSet.getRunnableCompleted( ) );
		Assert.assertNull( taskSet.getRunnableRan( ) );
		task.run( );
		Assert.assertSame( taskSet.getRunnableCompleted( ), runnable );
		Assert.assertSame( taskSet.getRunnableRan( ), runnable );
			
	}
}
