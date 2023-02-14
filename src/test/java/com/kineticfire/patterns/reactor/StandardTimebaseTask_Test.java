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

import java.util.Set;

import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.StandardTimebaseTask;
import com.kineticfire.patterns.reactor.TimerSelector;
import com.kineticfire.patterns.reactor.MockTimerSelector_StandardTimebaseTask;


public class StandardTimebaseTask_Test {

        @Test
        public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                StandardTimebaseTask timer = new StandardTimebaseTask( );


                Field handleF = StandardTimebaseTask.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Handle handle1 = (Handle)handleF.get( timer );
                Assert.assertNull( handle1 );
        }


	@Test
	public void testConstructorSelectorHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		MockTimerSelector_StandardTimebaseTask selector = new MockTimerSelector_StandardTimebaseTask( );
		Handle handle = new Handle( );
		StandardTimebaseTask timer = new StandardTimebaseTask( selector, handle );


                Field handleF = StandardTimebaseTask.class.getDeclaredField( "handle" );
                handleF.setAccessible( true );
                Handle handle1 = (Handle)handleF.get( timer );
                Assert.assertSame( handle, handle1 );

                Field selectorF = StandardTimebaseTask.class.getDeclaredField( "timerSelector" );
                selectorF.setAccessible( true );
                TimerSelector selector1 = (TimerSelector)selectorF.get( timer );
                Assert.assertSame( selector, selector1 );
	}


        @Test
        public void testRun( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockTimerSelector_StandardTimebaseTask selector = new MockTimerSelector_StandardTimebaseTask( );
                Handle handle = new Handle( );
                StandardTimebaseTask timer = new StandardTimebaseTask( selector, handle );

                Assert.assertNull( selector.getTimerExpiredHandle( ) );

		timer.run( );

                Assert.assertSame( handle, selector.getTimerExpiredHandle( ) );
        }


        @Test
        public void testHandle( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
                MockTimerSelector_StandardTimebaseTask selector = new MockTimerSelector_StandardTimebaseTask( );
                Handle handle = new Handle( );
                StandardTimebaseTask timer = new StandardTimebaseTask( selector, handle );

                Assert.assertSame( handle, timer.handle( ) );
        }

}
