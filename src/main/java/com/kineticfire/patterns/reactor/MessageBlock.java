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



import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;



/**
  *
  * A MessageBlock provides a standard and flexible mechanism for passing data and messages between objects.
  * <p>
  * Message passing is a common technique to ensure data consistancy in multi-threaded applications and design patterns, such as the Reactor.  A MessageBlock is the basic unit for queues in the Reactor architecture.  Objects typically promote thread concurrency by placing queues at their boundaries and communicating via MessageBlocks.
  * <p>
  * A MessageBlock serves as a container for arbitrary data.  Data can be placed into a MessageBlock using two approaches:  object-based data and variant-based data.
  * <p>
  * <b>Object-based Data</b>
  * <p>
  * Object-based data MessageBlocks store zero or one chunks of data as an Object.  Object-based data for a MessageBlock most closely follows the Reactor design pattern as described in [Sch94].  In [Sch94], the MessageBlock stores data in a data block, which is roughly equivalent to a buffer.  In the com.kineticfire.patterns.reactor package, a MessageBlock stores data as an Object.  Methods are available to set, query, and retrieve the data stored in a MessageBlock.  The MessageBlock provides convenience methods to retrieve the stored data as common objects such as Integer and String, hiding the mechansisms of casting.  A cast operation is not that costly at a number comparison and then a reference comparison [Sta14].
  * <p>
  * MessageBlocks may be connected together to form chains, as will be discussed later, which can be traversed.  These object-based data MessageBlock chains are efficient for processing messages where the components of the message are always present, ordered (especially when order is important), and the sender and receiever know the order.
  * <p>
  * One or more object-based data MessageBlocks in a chain would typically define a single event, command, message, etc.
  * <p>
  * <b>Variant-based Data</b>
  * <p>
  * Variant-based data defines a String event type and stores zero or more additional data messages in a single MessageBlock using key-value pairs, where the key is a String and the value is an Object.  A key-value pair is refered to as a "variant" [Lem14].  Methods are available to set, query, and retrieve both the event type and the variant data stored in the MessageBlock.  The MessageBlock provides convenience methods to retrieve the stored variant data as common objets such as Integer and String, hiding the mechanisms of casting.  A cast operation is not that costly at a number comparison and then a reference comparison [Sta14].
  * <p>
  * Variant-based data MessageBlocks are a good choice when the components of the message are not always present and/or unordered (or order is not important).  Variant-based data MessageBlocks help avoid the potential pitfalls of object-based data MessageBlocks, where the latter has order dependency [Lem14].
  * <p>
  * A single variant-based data MessageBlock would typically define a single event, command, message, etc.  Variant-based data MessageBlocks may be chained together, but this would typically be used to express order for events where each variant-based data MessageBlock was a self-contained event.
  * <p>
  * <b>Choice of Object vs. Variant-based Data</b>
  * <p>
  * It is strongly recommended that either object-based data or variant-based data MessageBlocks be used in order to produce the most straight-forward and maintainable code.  The two approaches can be mixed, but this is expicitly not advised.  Object and variant-based data MessageBlocks each follow a different paradigm in message passing.
  * <p>
  * <b>Additional Features</b>
  * <p>
  * Each MessageBlock may be marked as either a 'data' message or a 'control' message.  The marker serves as a convenience for handlers processing MessageBlocks.  MessageBlocks default to 'data' messages upon creation.  Implementing components must define 'data' and 'control', if used.
  * <p>
  * MessageBlock is compatible with priority queues.  Each MessageBlock contains a priority value, defaulting to 100,000.  The class com.kineticfire.patterns.reactor.MessagePriorityComparator provides a Comparator for evaluating the priority values of MessageBlocks.
  * <p>
  * MessageBlock implements the Serializable interface, allowing MessageBlocks to be streamed to and reconstituted from files, buffers, network channels, etc.  If serialization is attempted on a MessageBlock that contains non-serializable data, a NotSerializableException will be thrown.
  * <p>
  * This class provides accessor and mutator methods sufficient for maintaining persistant data, specifically for POJOs and Hibernate.  The MessageBlock class also defines a 'serialVersionUID' value to promote backwards-compatibility through different versions of the class.
  * <p>
  * The user must avoid creating circular references with MessageBlock chains.
  * <p>
  * This class is not thread safe.
  * <p>
  * <b>References</b>
  * <p>
  * [Lem14] Richard Lemarchand, "Game Engine Architecture 2nd Ed.", CRC Press, Boca Raton, FL, 2014.
  * <p>
  * [Sch94] Douglas Schmidt, "Reactor: An Object Behavioral Pattern for Concurrent Event Demultiplexing and Event Handler Dispatching, " Proceedings of the First Pattern Languages of Programs conference in Monticello, Illinois, Aug. 1994.
  * <p>
  * [Sta14] Stack Overflow, "What is the cost of casting in Java?  Is it a good idea to avoid it?", http://stackoverflow.com/questions/26335959/what-is-the-cost-of-casting-in-java-is-it-a-good-idea-to-avoid-it, viewed 7 Jul 2018, 13 Oct. 2014.
  * 
  * @author Kris Hall
  * @version 07-01-09
  */
public class MessageBlock implements Serializable {

    //*********************************************
    //~ Instance/static variables
    
    /*
     * @serial
     */
    private String type;
    
    /*
     * @serial
     */
    private Map<String,Object> variantMap;
    
    /*
     * @serial
     */
    private Object message;
    
    /*
     * @serial
     */
    private int priority;
    
    /*
     * @serial
     */
    private MessageBlock prevMb;
    
    /*
     * @serial
     */
    private MessageBlock nextMb;
    
    /*
     * @serial
     */
    private boolean isData;
    
    
    private static final int DEFAULT_PRIORITY = 100000;
    
    
    /*
     * Determines if a de-serialized file is compatible with this class.
     * <p>
     * Maintainers must change this value if and only if the new version of this class is not compatible with old versions. See Sun docs for <a href=http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html> details.</a>
     * <p>
     * Not necessary to include in first version of the class, but included here as a reminder of its importance.
    */
    private static final long serialVersionUID = 20090001L;
    
   
    
    //*********************************************
    //~ Constructors


   /**
     * Constructs an empty MessageBlock.
     * 
     */
    public MessageBlock( ) {
        type = null;
        variantMap = null;
        message = null;
        prevMb = null;
        nextMb = null;
        priority = DEFAULT_PRIORITY;
        isData = true;
    }



    //*********************************************
    //~ Factory Methods


        // static

    /**
      * Factory method to create an empty MessageBlock.
      * <p>
      * This is the equivalent of the calling the constructor 'MessageBlock()'.
      * 
      * @return the created MessageBlock
      */
    public static MessageBlock createMessageBlock( ) {
        MessageBlock mb = new MessageBlock( );
        return( mb );
    }


        // member

    /**
      * Factory method to create an empty MessageBlock and appended to this MessageBlock.
      * 
      * @return the created MessageBlock
      */
    public MessageBlock createAppendMessageBlock( ) {
        MessageBlock mb = new MessageBlock( );

        insertNext( mb );

        return( mb );
    }



    // Factory methods for Object-based data MessageBlocks

        // static


    /**
      * Factory method to create an object-based MessageBlock with message set to 'message'.
      * 
      * @param message
      *    the message to set for the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createObjectBasedMessageBlock( Object message ) {
        MessageBlock mb = new MessageBlock( );
        mb.setMessage( message );
        return( mb );
    }
       

    /**
      * Factory method to create an object-based MessageBlock with message set to 'message' and priority set to 'priority'.
      * 
      * @param message
      *    the message to set for the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createObjectBasedMessageBlock( Object message, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setMessage( message );
        mb.setPriority( priority );
        return( mb );
    }


    /**
      * Factory method to create an object-based MessageBlock with message set to 'message' and appended to 'appendToBlock' MessageBlock.
      * <p> 
      * PRECONDITION: appendToBlock is not null
      * 
      * @param message
      *    the message to set for the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createObjectBasedMessageBlock( Object message, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setMessage( message );

        appendToBlock.setNext( mb );

        return( mb );
    }
    
    
    /**
      * Factory method to create an object-based MessageBlock with message set to 'message', priority set to 'priority', and appended to 'appendToBlock' MessageBlock.
      * <p> 
      * PRECONDITION: appendToBlock is not null
      * 
      * @param message
      *    the message to set for the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createObjectBasedMessageBlock( Object message, int priority, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setMessage( message );
        mb.setPriority( priority );

        appendToBlock.setNext( mb );

        return( mb );
    }


        // member

    /**
      * Factory method to create an object-based MessageBlock with message set to 'message' and appended to this MessageBlock.
      * 
      * @param message
      *    the message to set for the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendObjectBasedMessageBlock( Object message ) {
        MessageBlock mb = new MessageBlock( );
        mb.setMessage( message );

        insertNext( mb );

        return( mb );
    }


    /**
      * Factory method to create an object-based MessageBlock with message set to 'message', priority set to 'priority', and appended to this MessageBlock.
      * 
      * @param message
      *    the message to set for the new MessageBlock
      * @param priority
      *    the priority to set for the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendObjectBasedMessageBlock( Object message, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setMessage( message );
        mb.setPriority( priority );

        insertNext( mb );

        return( mb );
    }



    // Factory methods for Variant-based data MessageBlocks


        // static


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type'.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and one variant with key 'key' associated to message 'message'.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param key
      *    the key to add for the variant
      * @param message
      *    the message to add for the variant, associated with the key
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, String key, Object message ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( key, message );
        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and variants added from 'newVariantMap'.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, Map<String,Object> newVariantMap ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( newVariantMap );
        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and priority 'priority'.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.setPriority( priority );
        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and one variant with key 'key' associated to message 'message'.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param key
      *    the key to add for the variant
      * @param message
      *    the message to add for the variant, associated with the key
      * @param priority
      *    the priority to set for the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, String key, Object message, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( key, message );
        mb.setPriority( priority );
        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and variants added from 'newVariantMap'.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, Map<String,Object> newVariantMap, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( newVariantMap );
        mb.setPriority( priority );
        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and appended to 'appendToBlock' MessageBlock.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );

        appendToBlock.setNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', one variant with key 'key' associated to message 'message', and appended to 'appendToBlock' MessageBlock.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param key
      *    the key to add for the variant
      * @param message
      *    the message to add for the variant, associated with the key
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, String key, Object message, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( key, message );

        appendToBlock.setNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', variants added from 'newVariantMap', and appended to 'appendToBlock' MessageBlock.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, Map<String,Object> newVariantMap, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( newVariantMap );

        appendToBlock.setNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', priority 'priority', and appended to 'appendToBlock' MessageBlock.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, int priority, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.setPriority( priority );

        appendToBlock.setNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', one variant with key 'key' associated to message 'message', and appended to 'appendToBlock' MessageBlock.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param key
      *    the key to add for the variant
      * @param message
      *    the message to add for the variant, associated with the key
      * @param priority
      *    the priority to set for the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, String key, Object message, int priority, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( key, message );
        mb.setPriority( priority );

        appendToBlock.setNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', variants added from 'newVariantMap', priority set to 'priority', and appended to 'appendToBlock' MessageBlock.
      * 
      * @param type
      *    the type to set for the MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the MessageBlock
      * @param priority
      *    the priority to set for the MessageBlock
      * @param appendToBlock
      *    MessageBlock to append to the MessageBlock
      * @return the created MessageBlock
      */
    public static MessageBlock createVariantBasedMessageBlock( String type, Map<String,Object> newVariantMap, int priority, MessageBlock appendToBlock ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( newVariantMap );
        mb.setPriority( priority );

        appendToBlock.setNext( mb );

        return( mb );
    }



        // member

    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type' and appended to this MessageBlock.
      * 
      * @param type
      *    the type to set for the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendVariantBasedMessageBlock( String type ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );

        insertNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', one variant with key 'key' associated to message 'message', and appended to this MessageBlock.
      * 
      * @param type
      *    the type to set for the new MessageBlock
      * @param key
      *    the key to add for the variant for the new MessageBlock
      * @param message
      *    the message to add for the variant for the new MessageBlock, associated with the key
      * @return the created MessageBlock
      */
    public MessageBlock createAppendVariantBasedMessageBlock( String type, String key, Object message ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( key, message );

        insertNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', variants added from 'newVariantMap', and appended to this MessageBlock.
      * 
      * @param type
      *    the type to set for the new MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendVariantBasedMessageBlock( String type, Map<String,Object> newVariantMap ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( newVariantMap );

        insertNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', priority set to 'priority', and appended to this MessageBlock.
      * 
      * @param type
      *    the type to set for the new MessageBlock
      * @param priority
      *    the priority to set for the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendVariantBasedMessageBlock( String type, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.setPriority( priority );

        insertNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', one variant with key 'key' associated to message 'message', priority set to 'priority', and appended to this MessageBlock.
      * 
      * @param type
      *    the type to set for the new MessageBlock
      * @param key
      *    the key to add for the variant for the new MessageBlock
      * @param message
      *    the message to add for the variant for the new MessageBlock, associated with the key
      * @param priority
      *    the priority to set for the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendVariantBasedMessageBlock( String type, String key, Object message, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( key, message );
        mb.setPriority( priority );

        insertNext( mb );

        return( mb );
    }


    /**
      * Factory method to create a variant-based MessageBlock with type set to 'type', variants added from 'newVariantMap', priority set to 'priority', and appended to this MessageBlock.
      * 
      * @param type
      *    the type to set for the new MessageBlock
      * @param newVariantMap
      *    variants as key-value pairs to add to the new MessageBlock
      * @param priority
      *    the priority to set for the new MessageBlock
      * @return the created MessageBlock
      */
    public MessageBlock createAppendVariantBasedMessageBlock( String type, Map<String,Object> newVariantMap, int priority ) {
        MessageBlock mb = new MessageBlock( );
        mb.setType( type );
        mb.addVariantMessage( newVariantMap );
        mb.setPriority( priority );

        insertNext( mb );

        return( mb );
    }



    //*********************************************
    //~ Methods


   /**
     * Resets this MessageBlock.
     * <p>
     * The MessageBlock is set as if just having been initialized with a call to the no-arguement constructor 'MessageBlock()'. The message is 'null', no links to other MessageBlocks, and the priority is set to the default priority.
     * <p>
     * If the MessageBlock is part of the chain, the MessageBlock is removed from the chain.  If the MessageBlock need only be removed from the chain and not reset, then call 'remove()'.
     * 
     */
    public void reset( ) {
        remove( ); // sets prevMb and nextMb to null
        type = null;
        variantMap = null;
        message = null;
        priority = DEFAULT_PRIORITY;
        isData = true;
    }
    
    
   /**
     * Adds the 'newMessageBlock' to preceed this MessageBlock.  Returns a reference to the first MessageBlock in the chain.
     * <p>
     * MessageBlocks proceeding newMessageBlock and those preceeding this MessageBlock are dropped. Returns a reference to the first MessageBlock in the chain now involving both this MessageBlock and 'newMessageBlock'.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * 
     * @param newMessageBlock
     *    MessageBlock to add to preceed this MessageBlock
     * @return the first MessageBlock in the chain
     */
    public MessageBlock setPrev( MessageBlock newMessageBlock ) {
        setBefore( newMessageBlock );
        return( newMessageBlock.getFirst( ) );
    }

    
   /**
     * Inserts the entire 'newMessageBlock' chain before the this MessageBlock, preserving both MessageBlock chains.  Returns a reference to the first MessageBlock in the chain.
     * <p>
     * The entire 'newMessageBlock' chain--from the first MessageBlock to the last MessageBlock, regardless of the location of 'newMessageBlock'--is inserted. Returns a reference to the first MessageBlock in the chain now involving both this MessageBlock and 'newMessageBlock'. 
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * 
     * @param newMessageBlock
     *    MessageBlock chain to insert before the this MessageBlock
     * @return the first MessageBlock in the new chain
     */
    public MessageBlock insertPrev( MessageBlock newMessageBlock ) {
        insertBefore( newMessageBlock );
        return( newMessageBlock.getFirst( ) );
    }
    
    
   /**
     * Gets the previous MessageBlock.
     * 
     * @return a reference to the previous MessageBlock or null if there is no previous MessageBlock
     */
    public MessageBlock getPrev( ) {
        return( prevMb );
    }
    
    
   /**
     * Returns a reference to the MessageBlock that is 'num' MessageBlocks previous or null if there no such MessageBlock.
     * <p>
     * A 'num' of zero or less will return this MessageBlock, 'num' of one will return the previous MessageBlock or null if there is no previous MessageBlock, and so on.
     * 
     * @param num
     *    number of previous MessageBlocks to step
     * @return a reference to the MessageBlock that is 'num' MessageBlocks previous or null if there no such MessageBlock
     */
    public MessageBlock getPrev( int num ) {
        MessageBlock mb = this;
        
        while ( ( mb != null ) &&  ( num > 0 ) ) {
            num--;
            mb = mb.getPrev( );
        }
        
        return( mb );
    }
    
    
   /**
     * Determines if there is a previous MessageBlock.
     * 
     * @return 'true' if there is a previous MessageBlock and 'false' otherwise
     */
    public boolean hasPrev( ) {
        boolean hasPrev = true;

        if ( prevMb == null ) {
                hasPrev = false;
        }

        return( hasPrev );
    }

    
   /**
     * Returns the number of previous MessageBlocks.
     * 
     * @return the number of previous MessageBlocks
     */
    public int numPrev( ) {
        MessageBlock mb = this;
        int numPrev = 0;
        
        while ( mb.hasPrev( ) ) {
            mb = mb.getPrev( );
            numPrev++;
        }
        
        return( numPrev );
    }
    

   /**
     * Adds the 'newMessageBlock' to the end of the this MessageBlock.  Returns a reference to the last MessageBlock in the chain.
     * <p>
     * MessageBlocks preceeding 'newMessageBlock' and those proceeding this MessageBlock are dropped.  Returns a reference to the last MessageBlock in the chain now involving both this MessageBlock and 'newMessageBlock'.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * 
     * @param newMessageBlock
     *    MessageBlock to add to the end of the this MessageBlock
     * @return the last MessageBlock in the new chain
     */    
    public MessageBlock setNext( MessageBlock newMessageBlock ) {
        setAfter( newMessageBlock );
        return( getLast( ) );
    }
    
    
   /**
     * Inserts the entire 'newMessageBlock' chain after this MessageBlock, preserving both MessageBlock chains.  Returns a reference to the last MessageBlock in the new chain.
     * <p>
     * The entire 'newMessageBlock' chain--from the first MessageBlock to the last MessageBlock, regardless of the location of newMessageBlock--is inserted.  Returns a reference to the last MessageBlock in the chain now involving both this MessageBlock and 'newMessageBlock'.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * 
     * @param newMessageBlock
     *    MessageBlock chain to insert after this MessageBlock
     * @return the last MessageBlock in the new chain
     */
    public MessageBlock insertNext( MessageBlock newMessageBlock ) {
        insertAfter( newMessageBlock );
        return( getLast( ) );
    }

    
   /**
     * Returns a reference to the next MessageBlock.
     * 
     * @return reference to the next MessageBlock or null if there is no next MessageBlock
     */
    public MessageBlock getNext( ) {
        return( nextMb );
    }

    
   /**
     * Returns a reference to the MessageBlock that is 'num' MessageBlocks next or null if there no such MessageBlock.
     * <p>
     * A 'num' of zero or less will return this MessageBlock, 'num' of one will return the next MessageBlock or null if there is no next MessageBlock, and so on.
     * 
     * @param num
     *    number of next MessageBlocks to step
     * @return a reference to the MessageBlock that is 'num' MessageBlocks next or nor if there no such MessageBlock
     */
    public MessageBlock getNext( int num ) {
        MessageBlock mb = this;

        while ( ( mb != null ) &&  ( num > 0 ) ) {
            num--;
            mb = mb.getNext( );
        }
        
        return( mb );
    }
    
    
   /**
     * Determines if there is a next MessageBlock.
     * 
     * @return true if there is a next MessageBlock and false otherwise
     */
    public boolean hasNext( ) {
        boolean hasNext = true;

        if ( nextMb == null ) {
            hasNext = false;
        }
            
        return( hasNext );
    }
    
    
   /**
     * Counts the number of next MessageBlocks.
     * 
     * @return the number of next MessageBlocks
     */
    public int numNext( ) {
        MessageBlock mb = this;
        int numNext = 0;
        
        while ( mb.hasNext( ) ) {
            mb = mb.getNext( );
            numNext++;
        }
        
        return( numNext );
    }
    
    
   /**
     * Gets the first MessageBlock in the chain.
     * 
     * @return a reference to the first MessageBlock in the chain
     */
    public MessageBlock getFirst( ) {
        MessageBlock mbFirst = this;
        while ( mbFirst.hasPrev( ) ) {
            mbFirst = mbFirst.getPrev( );
        }
        return( mbFirst );
    }

    
   /**
     * Gets the last MessageBlock in the chain.
     * 
     * @return a reference to the last MessageBlock in the chain
     */
    public MessageBlock getLast( ) {
        MessageBlock mbLast = this;
        while ( mbLast.hasNext( ) ) {
            mbLast = mbLast.getNext( );
        }
        return ( mbLast );
    }
    
    
   /**
     * Counts the total number of MessageBlocks in the chain including the current MessageBlock.
     * 
     * @return the total number of MessageBlocks in the chain including the current MessageBlock
     */
    public int numTotal( ) {
        return( numPrev( ) + numNext( ) + 1 );
    }


   /**
     * Removes the MessageBlock from the chain and returns a reference to the removed MessageBlock.
     * <p>
     * Methods getNextMbRemoval() and getPrevMbRemoval() are available to remove the current MessageBlock and traverse the MessageBlock chain.
     * 
     * @return the MessageBlock removed from the chain
     */
    public MessageBlock remove( ) {
         if ( prevMb != null && nextMb != null ) {
            prevMb.setNextMb( nextMb );
            nextMb.setPrevMb( prevMb );
        } else if ( prevMb == null && nextMb != null ) {
            nextMb.setPrevMb( null );
        } else if ( prevMb != null && nextMb == null ) {
            prevMb.setNextMb( null );
        }

        prevMb = null;
        nextMb = null;
    
        return( this );
    }

    
   /**
     * Removes the current MessageBlock from the chain and returns a reference to the previous MessageBlock.
     * 
     * @return the previous MessageBlock in the chain or null if there is no previous MessageBlock
     */
    public MessageBlock getPrevRemoval( ) {
        MessageBlock tempPrev = prevMb;
        remove( );
        return( tempPrev );
    }
    
    
   /**
     * Removes the current MessageBlock from the chain and returns a reference to the next MessageBlock.
     * 
     * @return the next MessageBlock in the chain or null if there is no next MessageBlock
     */
    public MessageBlock getNextRemoval( ) {
        MessageBlock tempNext = nextMb;
        remove( );
        return( tempNext );
    }
    
    
   /**
     * Removes all previous MessageBlocks and returns a reference to the current MessageBlock.
     * 
     * @return the current MessageBlock
     */
    public MessageBlock removeAllPrev( ) {
        if ( prevMb != null ) {
            prevMb.setNextMb( null );
            prevMb = null;
        }
        return( this );
    }
    
    
   /**
     * Removes all next MessageBlocks and returns a reference to the current messasgeBlock.
     * 
     * @return the current MessageBlock
     */
    public MessageBlock removeAllNext( ) {
        if ( nextMb != null ) {
            nextMb.setPrevMb( null );
            nextMb = null;
        }
        return( this );
    }
    

   /**
     * Gets the priority of this MessageBlock.
     * 
     * @return the priority of this MessageBlock
     */
    public int getPriority( ) {
        return( priority );
    }
    
    
   /**
     * Sets the priority of this MessageBlock.
     * 
     * @param priority
     *            priority to set for this MessageBlock
     */
    public void setPriority( int priority ) {
        this.priority = priority;
    }

    
   /**
     * Resets the priority of this MessageBlock to the default value.
     * 
     */
    public void resetPriority( ) {
        priority = DEFAULT_PRIORITY;
    }
    
    
   /**
     * Queries if this MessageBlock is a data message or control message.
     * 
     * @return true if this MessageBlock is a data message and false otherwise
     */
    public boolean getIsData( ) {
        return( isData );
    }
    
    
   /**
     * Sets this MessageBlock as a data message.
     * 
     */
    public void setIsData( ) {
        isData = true;
    }

    
   /**
     * Queries if this MessageBlock is a data message or control message.
     * 
     * @return true if this MessageBlock is a control message and false
     *         otherwise
     */
    public boolean getIsControl( ) {
        return( !isData );
    }
    
    
   /**
     * Sets this MessageBlock as a control message.
     * 
     */
    public void setIsControl( ) {
        isData = false;
    }

    
    
   /*
     * Sets the newMessageBlock as the previous MessageBlock without updating newMessageBlock's next MessageBlock reference.
     * <p>
     * Users should avoid this method in favor of setPrev(MessageBlock).
     * <p>
     * This method is provided as a mutator method for persistant message objects.
     * <p>
     * PRECONDITION: newMessageBlock is not null
     * 
     * @param newMessageBlock
     *            MessageBlock to set as previous MessageBlock
     */
    public void setPrevMb( MessageBlock newMessageBlock ) {
        prevMb = newMessageBlock;
    }

    
   /*
     * Gets the previous MessageBlock.
     * <p>
     * Users should avoid this method in favor of getPrev(MessageBlock).
     * <p>
     * This method is provided as an accessor method for persistant message objects.
     * 
     * @return a reference to the previous MessageBlock or null if there is no
     *         previous MessageBlock
     */
    public MessageBlock getPrevMb( ) {
        return( prevMb );
    }
    
    
   /*
     * Sets the 'newMessageBlock' as the next MessageBlock without updating 'newMessageBlock' previous MessageBlock reference.
     * <p>
     * Users should avoid using this method in favor of setNext(MessageBlock).
     * <p>
     * This method is provided as a mutator method for persistant message objects.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * 
     * @param newMessageBlock
     *            MessageBlock to set as next MessageBlock
     */
    public void setNextMb( MessageBlock newMessageBlock ) {
        nextMb = newMessageBlock;
    }

    
   /*
     * Returns a reference to the next MessageBlock.
     * <p>
     * Users should avoid using this method in favor of getNext(MessageBlock).
     * <p>
     * This method is provided as an accessor method for persistant message objects.
     * 
     * @return reference to the next MessageBlock or null if there is no next
     *         MessageBlock
     */
    public MessageBlock getNextMb( ) {
        return( nextMb );
    }
    
    
   /**
     * Adds the 'newMessageBlock' to the end of the this MessageBlock.  MessageBlocks preceeding 'newMessageBlock' and those proceeding this MessageBlock are dropped.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * <p>
     * This method will soon be deprecated.  Users should not use this method and instead use 'setNext(MessageBlock)'.
     * 
     * @param newMessageBlock
     *    MessageBlock to add to the end of the this MessageBlock
     */
    private void setAfter( MessageBlock newMessageBlock ) {
        if ( nextMb != null ) {
            nextMb.setPrevMb( null );
        }
        
        if ( newMessageBlock.hasPrev( ) ) {
            newMessageBlock.getPrev( ).setNextMb( null );
        }
        
        nextMb = newMessageBlock;
        newMessageBlock.setPrevMb( this );
    }

    
   /**
     * Adds the 'newMessageBlock' to proceed this MessageBlock.
     * <p>
     * MessageBlocks proceeding 'newMessageBlock' and those preceeding this MessageBlock are dropped.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * <p>
     * This method will soon be deprecated.  Users should not use this method and instead use 'setPrev(MessageBlock)'.
     * 
     * @param newMessageBlock
     *    MessageBlock to add to the end of the this MessageBlock
     */
    private void setBefore( MessageBlock newMessageBlock ) {
        if ( prevMb != null ) {
            prevMb.setNextMb( null );
        }
        
        if ( newMessageBlock.hasNext( ) ) {
            newMessageBlock.getNext( ).setPrevMb( null );
        }
        
        prevMb = newMessageBlock;
        newMessageBlock.setNextMb( this );
    }
    
    
   /**
     * Inserts the entire 'newMessageBlock' chain after the this MessageBlock, preserving the trailing remainder of the this MessageBlock chain.
     * <p>
     * The entire 'newMessageBlock' chain--from the first MessageBlock to the last MessageBlock, regardless of the location of 'newMessageBlock'--is inserted. The insertion occurs after the this MessageBlock; the original chain of the this MessageBlock is added to the end of the 'newMessageBlock' chain.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * <p>
     * This method will soon be deprecated.  Users should not use this method and instead use 'insertNext(MessageBlock)'.
     * 
     * @param newMessageBlock
     *    MessageBlock chain to insert after the this MessageBlock
     */
    private void insertAfter( MessageBlock newMessageBlock ) {
        if ( nextMb == null ) { // this IS the last MessageBlock
            MessageBlock startChain = newMessageBlock.getFirst( );
            nextMb = startChain;
            startChain.setPrevMb( this );
        } else { // this is NOT the last MessageBlock
            MessageBlock temp = nextMb;
            MessageBlock startChain = newMessageBlock.getFirst( );
            nextMb = startChain;
            startChain.setPrevMb( this );
            
            MessageBlock endChain = newMessageBlock.getLast( ); 
            endChain.setNextMb( temp );
            temp.setPrevMb( endChain );
        }
    }

    
   /**
     * Inserts the entire 'newMessageBlock' chain before the this MessageBlock, preserving the preceeding remainder of the this MessageBlock chain.
     * <p>
     * The entire 'newMessageBlock' chain--from the first MessageBlock to the last MessageBlock, regardless of the location of 'newMessageBlock'--is inserted. The insertion occurs before the this MessageBlock; the original chain of the this MessageBlock is added to proceed the 'newMessageBlock' chain.
     * <p>
     * PRECONDITION: 'newMessageBlock' is not null
     * <p>
     * This method will soon be deprecated.  Users should not use this method and instead use 'insertPrev(MessageBlock)'.
     * 
     * @param newMessageBlock
     *    MessageBlock chain to insert before the this MessageBlock
     */
    private void insertBefore( MessageBlock newMessageBlock ) {
        if ( prevMb == null ) { // this IS the first message block
            MessageBlock endChain = newMessageBlock.getLast( );
            prevMb = endChain;
            endChain.setNextMb( this );
        } else { // this is NOT the first message block
            MessageBlock temp = prevMb;
            MessageBlock endChain = newMessageBlock.getLast( );
            prevMb = endChain;
            endChain.setNextMb( this );
            
            MessageBlock startChain = newMessageBlock.getFirst( );
            temp.setNextMb( startChain );
            startChain.setPrevMb( temp );
        }
    }



    // Data fields for Variant-based data MessageBlocks


    /**
      * Sets the type for a variant-based data MessageBlock.
      *
      * @param type
      *    type to set
      */
    public void setType( String type ) {
        this.type = type;
    }


    /**
      * Returns the type for a variant-based data MessageBlock.
      * <p>
      * If not set, then null is returned.
      *
      * @return the type for a variant-based data MessageBlock
      */
    public String getType( ) {
        return( type );
    }


    /*
      * Sets the variantMap to 'variantMap'.
      * <p>
      * This method is provided as a mutator method for persistant message objects.
      * 
      * @param variantMap
      *    variant map to set
      */
    public void setVariantMap( Map<String,Object> variantMap ) {
        this.variantMap = variantMap;
    }


    /*
      * Returns the variantMap.
      * <p>
      * This method is provided as a mutator method for persistant message objects.
      *
      * @return the variantMap
      */
    public Map<String,Object> getVariantMap( ) {
        return( variantMap );
    }


   /**
     * Adds the variant message to be stored in this MessageBlock to 'message' for key 'key'.
     * <p>
     * If a variant with key 'key' already exists, then that variant is over-written.
     *
     * @param key
     *    the key to associate with this variant
     * @param message
     *    the message to be stored for this variant
     */
    public void addVariantMessage( String key, Object message ) {

        try {
            variantMap.put( key, message );
        } catch ( NullPointerException e ) {
            variantMap = new HashMap<String,Object>( );
            variantMap.put( key, message );
        }

    }


   /**
     * Adds the variants in 'newVariantMap' to be stored in this MessageBlock.
     * <p>
     * If a variant with a key already exists, then that variant is over-written.
     *
     * @param newVariantMap
     *    the variants to add
     */
    public void addVariantMessage( Map<String,Object> newVariantMap ) {

        if ( variantMap == null ) {
            variantMap = new HashMap<String,Object>( );
        }

        variantMap.putAll( newVariantMap );
    }
    
    
   /**
     * Clears all variant messages from this MessageBlock.
     * <p>
     * Future calls to getVariantMessage('key') for any value of 'key' will return null.
     * 
     */
    public void clearVariantMessage( ) {
        variantMap = null;
    }
    
    
   /**
     * Clears the variant message from this MessageBlock for key 'key'.
     * <p>
     * Future calls to getVariantMessage('key') will return a null value.
     *
     * @param key
     *    the key for the variant to remove
     */
    public void clearVariantMessage( String key ) {
        try {
            variantMap.remove( key );
        } catch ( NullPointerException e ) {
        }
    }
        
    
   /**
     * Checks if this MessageBlock contains a variant message.
     * 
     * @return true if this MessageBlock contains a variant message and false otherwise
     */
    public boolean hasVariantMessage( ) {
        boolean hasVariantMessage = false;

        if ( variantMap != null ) {
            if ( !variantMap.isEmpty( ) ) {
                hasVariantMessage = true;
            }
        }

        return( hasVariantMessage );
    }
        
    
   /**
     * Checks if this MessageBlock contains a variant message with key 'key'.
     * 
     * @param key
     *    the key for the variant message to return
     * @return true if this MessageBlock contains a variant message with key 'key' and false otherwise
     */
    public boolean hasVariantMessage( String key ) {
        boolean hasVariantMessage = false;

        try {
            hasVariantMessage = variantMap.containsKey( key );
        } catch ( NullPointerException e ) {
            hasVariantMessage = false;
        }

        return( hasVariantMessage );
    }
        
    
   /**
     * Checks if this MessageBlock contains a variant message with key 'key' where the value is not null.
     * 
     * @param key
     *    the key for the variant message to query and return
     * @return true if this MessageBlock contains a variant message with key 'key' that is not null and false otherwise
     */
    public boolean hasVariantMessageNotNull( String key ) {

        boolean hasVariantMessage = false;

        try {

            Object value = variantMap.get( key );

            if ( value != null ) {
                hasVariantMessage = true;
            }

        } catch ( NullPointerException e ) {
            hasVariantMessage = false;
        }

        return( hasVariantMessage );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as an Object.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' or null if there is no message
     */
    public Object getVariantMessage( String key ) {
        Object vm = null;

        try {
            vm = variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        }

        return( vm );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as a Boolean.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a Boolean or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Boolean getVariantMessageBoolean( String key ) {
        Boolean vm = null;

        try {
            vm = (Boolean)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as a Byte.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a Byte or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Byte getVariantMessageByte( String key ) {
        Byte vm = null;

        try {
            vm = (Byte)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as an Integer.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a Integer or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Integer getVariantMessageInteger( String key ) {
        Integer vm = null;

        try {
            vm = (Integer)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as a Float.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a Float or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Float getVariantMessageFloat( String key ) {
        Float vm = null;

        try {
            vm = (Float)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as a Long.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a Long or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Long getVariantMessageLong( String key ) {
        Long vm = null;

        try {
            vm = (Long)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }

    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as a Double.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a Double or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Double getVariantMessageDouble( String key ) {
        Double vm = null;

        try {
            vm = (Double)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock for key 'key' as a String.
     * 
     * @param key
     *    the key for the variant message to return
     * @return the message stored in this MessageBlock variant for key 'key' as a String or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public String getVariantMessageString( String key ) {
        String vm = null;

        try {
            vm = (String)variantMap.get( key );
        } catch ( NullPointerException e ) {
            vm = null;
        } catch ( ClassCastException e ) {
            vm = null;
        }

        return( vm );
    }




    // Data fields for Object-based data MessageBlocks
    

   /**
     * Sets the message to be stored in this MessageBlock.
     * 
     * @param message
     *            the message to be stored in this MessageBlock
     */
    public void setMessage( Object message ) {
        this.message = message;
    }
    
    
   /**
     * Clears the message from this MessageBlock.
     * <p>
     * Future calls to getMessage( ) will return a null value. This method is added for readability; the method is identical to calling setMessage(null).
     * 
     */
    public void clearMessage( ) {
        message = null;
    }
        
    
   /**
     * Checks if this MessageBlock contains message.
     * 
     * @return true if this MessageBlock contains message and false otherwise
     */
    public boolean hasMessage( ) {
        boolean hasMessage = false;

        if ( message != null ) {
            hasMessage = true;
        }

        return( hasMessage );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as an Object.
     * 
     * @return the message stored in this MessageBlock
     */
    public Object getMessage( ) {
        return( message );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as a Boolean.
     * 
     * @return the message stored in this MessageBlock as a Boolean or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Boolean getMessageBoolean( ) {
        Boolean d = null;
        try {
            d = (Boolean)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as a Byte.
     * 
     * @return the message stored in this MessageBlock as a Byte or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Byte getMessageByte( ) {
        Byte d = null;
        try {
            d = (Byte)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as an Integer.
     * 
     * @return the message stored in this MessageBlock as a Integer or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Integer getMessageInteger( ) {
        Integer d = null;
        try {
            d = (Integer)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as a Float.
     * 
     * @return the message stored in this MessageBlock as a Float or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Float getMessageFloat( ) {
        Float d = null;
        try {
            d = (Float)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as a Long.
     * 
     * @return the message stored in this MessageBlock as a Long or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Long getMessageLong( ) {
        Long d;
        try {
            d = (Long)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }

    
   /**
     * Gets the message stored in this MessageBlock as a Double.
     * 
     * @return the message stored in this MessageBlock as a Double or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public Double getMessageDouble( ) {
        Double d = null;
        try {
            d = (Double)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }
    
    
   /**
     * Gets the message stored in this MessageBlock as a String.
     * 
     * @return the message stored in this MessageBlock as a String or null if there is no message (e.g. is null) or a ClassCastException occurred
     */
    public String getMessageString( ) {
        String d = null;
        try {
            d = (String)message;
        } catch ( ClassCastException e ) {
            d = null;
        }
        return( d );
    }

}
