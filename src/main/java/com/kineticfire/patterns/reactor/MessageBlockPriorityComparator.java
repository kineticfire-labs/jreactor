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


import java.util.Comparator;
import com.kineticfire.patterns.reactor.MessageBlock;

/**
 * Provides an object, implementing the Comparator interface, to compare two
 * message blocks.
 * <p>
 * This comparator imposes orderings that are inconsistent with equals.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */


public class MessageBlockPriorityComparator implements Comparator<MessageBlock> {

    /**
     * Creates a MessageBlockPriorityComparator.
     */
    public MessageBlockPriorityComparator( ) {
        // empty
    }

    
    /**
     * Compares two message blocks based on their priority values. Returns a
     * result greater then zero if mb1 is greater than mb2, less than zero is
     * mb2 is greater than mb1, or zero if the two are equal.
     * 
     * @param mb1
     *            first message block to compare
     * @param mb2
     *            second message block to compare
     * @return greater then zero if mb1 is greater than mb2, less than zero is
     *         mb2 is greater than mb1, or zero if the two are equal.
     */
    public int compare( MessageBlock mb1, MessageBlock mb2 ) {
        
        int result;
        
        if ( mb1.getPriority( ) == mb2.getPriority( ) ) {
            result = 0;
        } else if ( mb1.getPriority( ) < mb2.getPriority( ) ) {
            result = -1;
        } else {
            result = 1;
        }        
        
        return( result );
    }


    /**
     * Determines if the provided Object is equivalent to this object.
     * 
     * @param obj
     *            object to compare equivalency with the current object
     * @return true if the provided object is equivalent to the current object
     *         and false otherwise
     */
    public boolean equals( Object obj ) {
        return( this.equals( obj ) );
    }


    /**
      * Returns a hash code per Object.hashCode() specification.
      *
      * @return a hash code per Object.hashCode() specification
      */
    public int hashCode( ) {
        return( System.identityHashCode( this ) );
    }

}
