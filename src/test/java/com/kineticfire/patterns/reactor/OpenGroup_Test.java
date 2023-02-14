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

import com.kineticfire.patterns.reactor.OpenGroup;
import com.kineticfire.patterns.reactor.Handle;

import com.kineticfire.patterns.reactor.GenericHandler_OpenGroup;



public class OpenGroup_Test {

	@Test
	public void testConstructor( ) throws NoSuchFieldException, SecurityException, IllegalAccessException, ClassCastException {
		OpenGroup g = new OpenGroup( );

		Field valuesF = OpenGroup.class.getDeclaredField( "values" );
                valuesF.setAccessible( true );
                Set<Handle> values = (Set<Handle>)valuesF.get( g );

		Assert.assertEquals( values.size( ), 0 );
	}


        @Test
        public void testAdd( ) {
		OpenGroup g = new OpenGroup( );

		Handle handle1 = new Handle( );
		Handle handle2 = new Handle( );

		Assert.assertFalse( g.contains( handle1 ) );
		g.add( handle1 );
		Assert.assertTrue( g.contains( handle1 ) );

		Assert.assertFalse( g.contains( handle2 ) );
		g.add( handle2 );
		Assert.assertTrue( g.contains( handle2 ) );
        }


        @Test
        public void testRemove( ) {
                OpenGroup g = new OpenGroup( );

                Handle handle1 = new Handle( );
                Handle handle2 = new Handle( );

                Assert.assertFalse( g.contains( handle1 ) );
                g.add( handle1 );
                Assert.assertTrue( g.contains( handle1 ) );
		g.remove( handle1 );
                Assert.assertFalse( g.contains( handle1 ) );


                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );
                g.add( handle1 );
                g.add( handle2 );
                Assert.assertTrue( g.contains( handle1 ) );
                Assert.assertTrue( g.contains( handle2 ) );
		g.remove( handle2 );
		g.remove( handle1 );
                Assert.assertFalse( g.contains( handle1 ) );
                Assert.assertFalse( g.contains( handle2 ) );
        }


        @Test
        public void testContains( ) {
                OpenGroup g = new OpenGroup( );

                Handle handle1 = new Handle( );
                Handle handle2 = new Handle( );

                Assert.assertFalse( g.contains( handle1 ) );
                g.add( handle1 );
                Assert.assertTrue( g.contains( handle1 ) );

                Assert.assertFalse( g.contains( handle2 ) );
                g.add( handle2 );
                Assert.assertTrue( g.contains( handle2 ) );
        }
}
