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

import com.kineticfire.patterns.reactor.MessageBlockPriorityComparator;
import com.kineticfire.patterns.reactor.MessageBlock;

public class MessageBlockPriorityComparator_Test {


        @Test
        public void testCompareLess( ) {
		MessageBlockPriorityComparator c = new MessageBlockPriorityComparator( );
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                MessageBlock mb2 = new MessageBlock( new Integer( 1 ), 2 );

		Assert.assertEquals( c.compare( mb1, mb2 ), -1 );
        }


        @Test
        public void testCompareEqual( ) {
		MessageBlockPriorityComparator c = new MessageBlockPriorityComparator( );
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 1 );
                MessageBlock mb2 = new MessageBlock( new Integer( 1 ), 1 );

		Assert.assertEquals( c.compare( mb1, mb2 ), 0 );
        }


        @Test
        public void testCompareGreat( ) {
                MessageBlockPriorityComparator c = new MessageBlockPriorityComparator( );
                MessageBlock mb1 = new MessageBlock( new Integer( 1 ), 2 );
                MessageBlock mb2 = new MessageBlock( new Integer( 1 ), 1 );

                Assert.assertEquals( c.compare( mb1, mb2 ), 1 );
        }

}
