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
import java.util.Map;

import com.kineticfire.patterns.reactor.LockGroup;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.handler.Handler;

import com.kineticfire.patterns.reactor.GenericHandler_LockGroup;



public class LockGroup_Test {

	@Test
	public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		LockGroup g = new LockGroup( );

                Field valuesF = LockGroup.class.getDeclaredField( "values" );
                valuesF.setAccessible( true );
                Map<Handler, Handle> values = (Map<Handler, Handle>)valuesF.get( g );
                Assert.assertEquals( values.size( ), 0 );
	}


        @Test
        public void testAdd( ) {
		LockGroup g = new LockGroup( );

		GenericHandler_LockGroup handler1 = new GenericHandler_LockGroup( );
		GenericHandler_LockGroup handler2 = new GenericHandler_LockGroup( );
		Handle handle1 = new Handle( );
		Handle handle2 = new Handle( );

		Assert.assertFalse( g.contains( handler1 ) );
		Assert.assertFalse( g.contains( handle1 ) );
		g.add( handler1, handle1 );
		Assert.assertTrue( g.contains( handler1 ) );
		Assert.assertTrue( g.contains( handle1 ) );
		Assert.assertSame( g.getHandle( handler1 ), handle1 );

		g.add( handler2, handle2 );
		Assert.assertTrue( g.contains( handler2 ) );
		Assert.assertTrue( g.contains( handle2 ) );
		Assert.assertSame( g.getHandle( handler2 ), handle2 );
        }


        @Test
        public void testRemove( ) {
                LockGroup g = new LockGroup( );

		GenericHandler_LockGroup handler1 = new GenericHandler_LockGroup( );
		Handle handle1 = new Handle( );

		g.remove( handler1 );


                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertTrue( g.contains( handle1 ) );

		g.remove( handler1 );
                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handle1 ) );
        }


        @Test
        public void testContainsHandler( ) {
                LockGroup g = new LockGroup( );

		GenericHandler_LockGroup handler1 = new GenericHandler_LockGroup( );
		GenericHandler_LockGroup handler2 = new GenericHandler_LockGroup( );
		Handle handle1 = new Handle( );
		Handle handle2 = new Handle( );

                Assert.assertFalse( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );

                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handler1 ) );
                Assert.assertFalse( g.contains( handler2 ) );

                g.add( handler2, handle2 );
                Assert.assertTrue( g.contains( handler2 ) );
                Assert.assertTrue( g.contains( handler2 ) );
        }


        @Test
        public void testContainsHandle( ) {
                LockGroup g = new LockGroup( );

		GenericHandler_LockGroup handler1 = new GenericHandler_LockGroup( );
		GenericHandler_LockGroup handler2 = new GenericHandler_LockGroup( );
		Handle handle1 = new Handle( );
		Handle handle2 = new Handle( );

                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );

                g.add( handler1, handle1 );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );

                g.add( handler2, handle2 );
                Assert.assertTrue( g.contains( handle2 ) );
                Assert.assertTrue( g.contains( handle2 ) );
        }


        @Test
        public void testGetHandle( ) {
                LockGroup g = new LockGroup( );

		GenericHandler_LockGroup handler1 = new GenericHandler_LockGroup( );
		Handle handle1 = new Handle( );

                Assert.assertNull( g.getHandle( handler1 ) );

                g.add( handler1, handle1 );
                Assert.assertSame( g.getHandle( handler1 ), handle1 );
        }

}
