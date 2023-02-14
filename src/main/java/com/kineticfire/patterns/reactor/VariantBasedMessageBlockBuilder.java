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
import com.kineticfire.patterns.reactor.MessageBlockBuilder;



/**
  *
  * A VariantBasedMessageBlockBuilder provides a mechansim to build variant-based MessageBlocks.
  * <p>
  * The purpose of VariantBasedMessageBlockBuilder is to provide convenience, reduce errors, and make code more readable (and thus maintainable) when building MessageBlocks.  Although the MessageBlock class provides functionality for creating and chaining MessageBlock objects, building large chains results in a lot of repeated code that may be difficult to read.
  * 
  * @author Kris Hall
  * @version 07-07-2018
  */
public class VariantBasedMessageBlockBuilder extends MessageBlockBuilder implements Serializable {

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
     * Constructs an empty VariantBasedMessageBlockBuilder.
     * 
     */
    public VariantBasedMessageBlockBuilder( ) {
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
      * Creates a variant-based MessageBlock with type set to 'type'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param type
      *    the type to set for the MessageBlock
      */
    public void createMessageBlock( String type ) {
        try {
            currentMb = currentMb.createAppendVariantBasedMessageBlock( type );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createVariantBasedMessageBlock( type );
            firstMb = currentMb;
        }
    }


    /**
      * Creates a variant-based MessageBlock with type set to 'type' and one variant with key 'key' associated to message 'message'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param key
      *    the key to add for the variant
      * @param message
      *    the message to add for the variant, associated with the key
      */
    public void createMessageBlock( String type, String key, Object message ) {
        try {
            currentMb = currentMb.createAppendVariantBasedMessageBlock( type, key, message );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createVariantBasedMessageBlock( type, key, message );
            firstMb = currentMb;
        }
    }


    /**
      * Creates a variant-based MessageBlock with type set to 'type' and variants added from 'newVariantMap'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the MessageBlock
      */
    public void createMessageBlock( String type, Map<String,Object> newVariantMap ) {
        try {
            currentMb = currentMb.createAppendVariantBasedMessageBlock( type, newVariantMap );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createVariantBasedMessageBlock( type, newVariantMap );
            firstMb = currentMb;
        }
    }


    /**
      * Creates a variant-based MessageBlock with type set to 'type' and priority 'priority'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      */
    public void createMessageBlock( String type, int priority ) {
        try {
            currentMb = currentMb.createAppendVariantBasedMessageBlock( type, priority );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createVariantBasedMessageBlock( type, priority );
            firstMb = currentMb;
        }
    }


    /**
      * Creates a variant-based MessageBlock with type set to 'type' and one variant with key 'key' associated to message 'message'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param key
      *    the key to add for the variant
      * @param message
      *    the message to add for the variant, associated with the key
      * @param priority
      *    the priority to set for the MessageBlock
      */
    public void createMessageBlock( String type, String key, Object message, int priority ) {
        try {
            currentMb = currentMb.createAppendVariantBasedMessageBlock( type, key, message, priority );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createVariantBasedMessageBlock( type, key, message, priority );
            firstMb = currentMb;
        }
    }


    /**
      * Creates a variant-based MessageBlock with type set to 'type' and variants added from 'newVariantMap'.  The new MessageBlock is appended to the chain if one exists.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      */
    public void createMessageBlock( String type, Map<String,Object> newVariantMap, int priority ) {
        try {
            currentMb = currentMb.createAppendVariantBasedMessageBlock( type, newVariantMap, priority );
        } catch ( NullPointerException e ) {
            currentMb = MessageBlock.createVariantBasedMessageBlock( type, newVariantMap, priority );
            firstMb = currentMb;
        }
    }


   /**
     * Adds the variant message to be stored in the current MessageBlock to 'message' for key 'key'.
     * <p>
     * If a variant with key 'key' already exists, then that variant is over-written.
     * <p>
     * A NullPointerException is thrown if the VariantBasedMessageBlockBuilder is not currently building a MessageBlock.
     *
     * @param key
     *    the key to associate with this variant
     * @param message
     *    the message to be stored for this variant
     */
    public void addVariantMessage( String key, Object message ) {
        currentMb.addVariantMessage( key, message );
    }


   /**
     * Adds the variants in 'newVariantMap' to be stored in the current MessageBlock.
     * <p>
     * If a variant with a key already exists, then that variant is over-written.
     * <p>
     * A NullPointerException is thrown if the VariantBasedMessageBlockBuilder is not currently building a MessageBlock.
     *
     * @param newVariantMap
     *    the variants to add
     */
    public void addVariantMessage( Map<String,Object> newVariantMap ) {
        currentMb.addVariantMessage( newVariantMap );
    }

}
