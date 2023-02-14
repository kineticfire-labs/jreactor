package com.kineticfire.patterns.reactor;

/**
 * Copyright 2018 KineticFire.
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



import java.io.Serializable;


import com.kineticfire.patterns.reactor.MessageBlock;
import com.kineticfire.patterns.reactor.MessageBlockBuilder;



/**
  *
  * An ObjectBasedMessageBlockBuilder provides a mechansim to build object-based MessageBlocks.
  * <p>
  * The purpose of MessageBlockBuilder is to provide convenience, reduce errors, and make code more readable (and thus maintainable) when building MessageBlocks.  Although the MessageBlock class provides functionality for creating and chaining MessageBlock objects, building large chains results in a lot of repeated code that may be difficult to read.
  * 
  * @author Kris Hall
  * @version 07-07-2018
  */
public class ObjectBasedMessageBlockBuilder extends MessageBlockBuilder implements Serializable {

    //*********************************************
    //~ Instance/static variables

    
    /*
     * Determines if a de-serialized file is compatible with this class.
     * <p>
     * Maintainers must change this value if and only if the new version of this class is not compatible with old versions. See Sun docs for <a href=http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html> details.</a>
     * <p>
     * Not necessary to include in first version of the class, but included here as a reminder of its importance.
    */
    private static final long serialVersionUID = 20090000L;
    
   
    
    //*********************************************
    //~ Constructors


   /**
     * Constructs an empty ObjectBasedMessageBlockBuilder.
     * 
     */
    public ObjectBasedMessageBlockBuilder( ) {
        super( );
    }


    //*********************************************
    //~ Methods



    /**
      * Creates an empty MessageBlock.  The new MessageBlock is appended to the chain if one exists.
      * 
      */
    public void createMessageBlock( ) {
        try {
            currentMb = currentMb.createAppendMessageBlock( );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createMessageBlock( );
            firstMb = currentMb;
        }
    }



    /**
      * Creates an object-based MessageBlock with message set to 'message'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param message
      *    the message to set for the MessageBlock
      */
    public void createMessageBlock( Object message ) {
        try {
            currentMb = currentMb.createAppendObjectBasedMessageBlock( message );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createObjectBasedMessageBlock( message );
            firstMb = currentMb;
        }
    }
       

    /**
      * Creates an object-based MessageBlock with message set to 'message' and priority set to 'priority'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param message
      *    the message to set for the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      */
    public void createMessageBlock( Object message, int priority ) {
        try {
            currentMb = currentMb.createAppendObjectBasedMessageBlock( message, priority );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createObjectBasedMessageBlock( message, priority );
            firstMb = currentMb;
        }
    }


}
