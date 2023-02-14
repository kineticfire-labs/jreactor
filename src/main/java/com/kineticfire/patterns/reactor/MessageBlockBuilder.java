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



import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;


import com.kineticfire.patterns.reactor.MessageBlock;



/**
  *
  * A MessageBlockBuilder provides a mechansim to build MessageBlocks.
  * <p>
  * The purpose of MessageBlockBuilder is to provide convenience, reduce errors, and make code more readable (and thus maintainable) when building MessageBlocks.  Although the MessageBlock class provides functionality for creating and chaining MessageBlock objects, building large chains results in a lot of repeated code that may be difficult to read.
  * 
  * @author Kris Hall
  * @version 07-07-2018
  */
public abstract class MessageBlockBuilder implements Serializable {

    //*********************************************
    //~ Instance/static variables

    
    /*
     * @serial
     */
    protected MessageBlock firstMb;
    
    /*
     * @serial
     */
    protected MessageBlock currentMb;
    
    
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
     * Constructs an empty MessageBlockBuilder.
     * 
     */
    public MessageBlockBuilder( ) {
        firstMb = null;
        currentMb = null;
    }
    
    
   /**
     * Sets the current MessageBlock as a data message.
     * <p>
     * A NullPointerException is thrown if the MessageBlockBuilder is not currently building a MessageBlock.
     * 
     */
    public void setIsData( ) {
        currentMb.setIsData( );
    }

    
   /**
     * Sets the current MessageBlock as a control message.
     * <p>
     * A NullPointerException is thrown if the MessageBlockBuilder is not currently building a MessageBlock.
     * 
     */
    public void setIsControl( ) {
        currentMb.setIsControl( );
    }


    /**
      * Returns the built MessageBlock and returns the MessageBlockBuilder to the empty, initialized state.
      * <p>
      * The first MessageBlock in the chain, if a chain were created, is returned.  Null is returned if no MessageBlock is currntly being built.
      *
      * @return the built MessageBlock
      */
    public MessageBlock getMessageBlock( ) {
        MessageBlock mb = firstMb;
        firstMb = null;
        currentMb = null;

        return( mb );
    }


    /**
      * Resets the MessageBlockBuilder to its empty, initialized state.
      *
      */
    public void reset( ) {
        firstMb = null;
        currentMb = null;
    }
    
    
   /**
     * Inserts the entire 'newMessageBlock' chain after the current MessageBlock, preserving both MessageBlock chains.
     * <p>
     * The entire 'newMessageBlock' chain--from the first MessageBlock to the last MessageBlock, regardless of the location of newMessageBlock--is inserted.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * 
     * @param newMessageBlock
     *    MessageBlock chain to insert after the current MessageBlock
     */
    public void insertNext( MessageBlock newMessageBlock ) {
        try {
            currentMb = currentMb.insertNext( newMessageBlock );
        } catch ( NullPointerException e ) {
            firstMb = newMessageBlock.getFirst( );
            currentMb = newMessageBlock.getLast( );
        }
    }


   /*
     * Returns the firstMb.
     * <p>
     * Users should avoid this method.
     * <p>
     * This method is provided as an accessor method for persistant message objects.
     * 
     * @return a reference to the firstMb
     */
    public MessageBlock getFirstMb( ) {
        return( firstMb );
    }


   /*
     * Sets the the firstMb.
     * <p>
     * Users should avoid this method.
     * <p>
     * This method is provided as an accessor method for persistant message objects.
     * 
     * @param mb
     *    MessageBlock to set as the firstMb
     */
    public void setFirstMb( MessageBlock mb ) {
        firstMb = mb;
    }


   /*
     * Returns the currentMb.
     * <p>
     * Users should avoid this method.
     * <p>
     * This method is provided as an accessor method for persistant message objects.
     * 
     * @return a reference to the currentMb
     */
    public MessageBlock getCurrentMb( ) {
        return( currentMb );
    }


   /*
     * Sets the the currentMb.
     * <p>
     * Users should avoid this method.
     * <p>
     * This method is provided as an accessor method for persistant message objects.
     * 
     * @param mb
     *    MessageBlock to set as the currentMb
     */
    public void setCurrentMb( MessageBlock mb ) {
        currentMb = mb;
    }


}
