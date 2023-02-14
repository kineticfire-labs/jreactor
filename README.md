# JReactor
The JReactor is an open source Java library for simplifying and more quickly building concurrent/asynchronous applications and to best leverage multi-processor/multi-core systems.

The JReactor implements the Reactor architectural pattern, originally defined in [Sch94].  The pattern emphasizes concurrency management along with a common data exchange approach among objects through a message-passing paradigm. The Reactor promotes scalable, re-usable, and robust software. The Reactor pattern lends itself to event-driven applications such as user interfaces, data-driven processing, simulation, gaming, networking, and real-time communications signaling.  The pattern also helps more easily exploit the benefits of multi-processor/multi-core systems. 

The JReactor from KineticFire Labs provides intrinsic concurrency and error management along with a common and thread-safe data exchange mechanism, allowing developers to focus on building business-specific logic to complete projects in less time.  By enabling patterns and providing modules that satisfy a range of use cases in software development and that promote software re-use, there is much less code to write, debug, and maintain which reduces both engineering and maintenence costs.

# Why use the JReactor?
1. **Less complexity and fewer bugs from concurrency.**  Thread-safety is guaranteed and details abstracted from the developer.  This aspect results in less complexity and fewer concurrency-related bugs, allowing the developer to focus more time on implementing business logic.
2. **More re-usable.**  A common data exchange format and message passing scheme help to decouple components and encourage the creation of re-usable modules.
3. **More robust.** Concurrency is intrinsically managed, eliminating a large source of coding errors.  A rigorous and common error control mechanism helps better manage all types of errors.
4. **More scalable.**  Concurrency is safely managed no matter the number of allowed threads:  assign one thread or 100!  Typically, the number of threads should be less than the total number of cores on the system.
5. **Less code to write, debug, and maintain.**  Intrinsic capabilities such as concurrency management, scaling, and event handling provide a semi-complete application, where developers need primarily focus on coding the business logic.

# Table of Contents
1. [Key Concepts](#key-concepts)
2. [Code Examples](#code-examples)
3. [References](#references)

# Key Concepts

## Event Sources
Event sources are objects that fire events known as ready operations.  Examples of event sources and their ready operations include:  a queue fires 'data available', a timer fires 'timer expired', and a network channel fires 'accept ready', 'connection ready', 'read ready', and/or 'write ready'.  The event source doesn't process the event-- that's up to an event handler.

Event sources supported by the JReactor are:
1. **Queue.** A queue.
2. **Channel.**  A network channel that fires events 'read', 'write', 'accept', and 'connect'.
3. **Timer.**  A timed event as either a one-time event or recurring event with a fixed delay or fixed rate.
4. **Lock.**  Indicates a guarantee that a Handler object or a group of Handler objects is not running and will not run until the lock is explicitly released by the developer code.
5. **Signal.**  Only defined signal source is 'shutdownhook', which is that fired when the JVM terminates due to (a) the program exiting normally such as the last non-daemon thread exits or when or (b) a response from a user interrupt such as CTRL-C or a system-wide event like a user logoff or system shutdown to include SIGUP (Unix only), SIGINT, or SIGTERM.
6. **Blocking task or task set.**  A blocking task or set of tasks that may block, such as I/O operation (e.g., disk read/write, database query, etc.), TLS setup, etc.
7. **Error.**  An error occured, which could be an error concerning an event source or a Handler, or a critical error within the JReactor.

## Event Handlers
Event handlers are developer code that implement business logic to process events from event sources.  The tuple of event handler, event source, and interest operations is registered registered with the JReactor; interest operations are those ready operations for which the event handler wants to be notified.  The JReactor invokes the the event handler to service the event source if the event source has at least one ready operation matching the handler's current interest operations.  The JReactor guarantees that at most one instance of the event handler is running at any given time.  If the developer ensures that the event source can only be modified by the event handler (or the event source itself is thread-safe), then both the event source and handler are thread-safe.  All without polling or blocking, and without managing locks, mutexes, etc.

## Handles
Handles serve as unique tokens that represent the relationship between a registered event source and its handler.  A handle is returned when an event source and handler are registered with the JReactor.  The handle is used to modify the registration, such as changing ready events of interest or to deregister the event source.

## MessageBlocks
MessageBlocks provide common data containers and mechanisms to transfer data between components.  MessageBlocks can contain data in two ways: as an opaque Object or a map structure using a String key and Object value.  In either case, data retrieved from a MessageBlock can be cast to the appropriate type.  MessageBlocks can be connected in a chain, which can be traversed in either direction.

# Code Examples

## Instantiate the JReactor
This example shows the basic instantiation of the JReactor, which should suffice for many applications.  Documentation for the JReactor describes several configuration options to optimize usage of the library.

```
import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.JReactor;

// ...

// instantiate an event handler that handles JReactor-level errors or those not addressed by handlers; see documentation on this critical error handler
CriticalErrorHandler criticalErrorHandler = new CriticalErrorHandler( );

// create a JReactor with three worker threads; see documentation on best choosing the number of threads
Reactor reactor = new JReactor( 3, criticalErrorHandler );

// create and configure event sources and handlers, passing the 'reactor' reference to the handlers for them to use the JReactor
// ...

// start the JReactor
reactor.dispatch( );
```

## Create a Handler
A handler is needed to register with an event source.  The following example shows code for setting up a handler.  Note that it is expected the top-level component will have created the JReactor and passed a reference to this handler.

```
import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.Handler;

public class MyExample implements Handler {

    private Reactor reactor;

    public MyExample( Reactor reactor ) {
        this.reactor = reactor;
    }

    // must implement handleEvent(...) method per Handler interface, to be presented later in "Implent the Business Logic in the Handler" example
```

## Register an Event Source and Handler
Register an event source and handler.

Some event sources, such as queues, are explicitly created and their references are accessible outside of the JReactor.  Registering an event source and handler returns a Handle, which serves as a reference to modify the registration such as by changing interest operations or deregistering the source and handler.

```
import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;
import com.kineticfire.patterns.reactor.MessageQueue;
import com.kineticfire.patterns.reactor.LinkedBlockingMessageQueue;

public class MyExample implements Handler {

    private MessageQueue queue;
    private Handle queueHandle;

    // ...

    private void someMethod( ) {
        // create the message queue for incoming messages
        queue = new LinkedBlockingMessageQueue( );

        // Register the message queue event source with the JReactor.  Set the handler as this object, and set the interest operations to data available in the queue
        queueHandle = reactor.registerQueue( queue, this, EventMask.QREAD );
    }

}
```

Other event sources, such as timers, are created by and are directly referenced only by the JReactor.

```
import com.kineticfire.patterns.reactor.Reactor;
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.EventMask;

public class MyExample implements Handler {

    private Handle timerHandle;

    // ...

    private void someMethod( ) {
        // Registers a timer event source with the JReactor.  Set the handler as this object, and set the interest operations to timer expired.
        // This timer recurs at a fixed-rate with the first timer expiration occuring at two seconds (2000 milliseconds) and then at a fixed-rate every three seconds (300 milliseconds)
        // In addition to fixed-rate timers, fixed-delay and one-time timers are also available.
        timerHandle = reactor.registerTimerFixedRate( 2000, 3000, this, EventMask.TIMER );
    }

}
```

## Implement the Business Logic in the Handler
Implement your business logic:  handle ready events from event source with your Handler.

The event handler's handleEvent(...) method performs your business logic.  This method is called by the JReactor when an event source for which the handler is registered has a ready event that matches the registered interest operations.  Note that a handler can service multiple event sources.  Even in this case, the JReactor still ensures that at most one instance of the event handler is running at any given time.

```
import com.kineticfire.patterns.reactor.Handler;
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

public class MyExample implements Handler {

    // ...

    // Handle ready event on event source.  This method is called by the JReactor.
    //    per 'Handler' interface
    public void handleEvent( Handle handle, int readyOps, MessageBlock info ) {

        // Check which handle to which the event pertains, if this handler services multiple event sources
        // Check that you care about the ready operations 'readyOps' for which the event source is ready
        // Check that the event source is indeed ready for the indicated ready operations (such as "data available in queue") as this method is an indication but not a guarantee for ready operations

        // Add your business logic here!

    }

}
```

## Create and Use MessageBlocks for Passing Data
The MessageBlock is an important part of the JReactor, enabling a common mechanism for conveying data between components.  Coupled with a message passing paradigm, a component can send data contained in a MessageBlock to another component in an asynchronous and thread-safe manner.

Data in a MessageBlock can follow two different paradigms, or both simultaneously.  A MessageBlock can store data as a single data item (called an Object MessageBlock), and/or can store multiple data elements using a map-style mechanism (called a variant MessageBlock) that uses key-value pairs.  The latter form is more commensurate with JSON and web communications.

MessageBlocks can be connected in a chain that can be traversed in both directions.  The MessageBlock chain can be manipulated, such as adding and removing MessageBlocks.  A MessageBlock chain is suited for applications where data is provided or processed in a known order.

MessageBlocks are serializable--so long as the data in them is also serializable--and can be used to transport data over a network connection.

MessageBlocks can also define optional parameters:
1. 'type' such as to define an event type
2. a boolean indicator as to if the MessageBlock contains signaling or data
3. 'priority' that can be used to priority order MessageBlocks in queues such as in the PriorityLinkedBlockingMessageQueue

These features and others provide great capability and flexibility for developers.  See the JReactor documentation for a complete set of MessageBlock features.

The examples below provide a glimpse at some basic MessageBlock usage.


```
// define common variables
MessageBlock mb;
String data = "Hello";
Object o;
String s;


/* ******************************************* */
/* creating MessageBlocks without data */

// using 'new'
mb = new MessageBlock( );

// using factory method
mb = MessageBlock.createMessageBlock( );

/* ******************************************* */
/* Variant-based (map-type data) MessageBlocks */

// with event type 'general' and mapping key String 'info' to value Object 'data'
mb = MessageBlock.createVariantBasedMessageBlock( "general", "info", data );


/* ******************************************* */
/* Object-based (single object data) MessageBlocks */

mb = MessageBlock.createObjectBasedMessageBlock( data );


/* ******************************************* */
/* Retrieving data */

// ... from Object-based MessageBlocks //

// as an Object
o = mb.getMessage( );

// as a String by using a helper method to cast data; should check for null pointer and casting errors
s = mb.getMessageString( );

// ... from variant-based MessageBlocks //

// get the value as an Object for key "info"
s = mb.getMessage( "info" );

// get the value as a String for key "info"; helper method casts returned value to String, shuld check for null pointer and casting errors
s = mb.getVariantMessageString( "info" );


/* ******************************************* */
/* Creating and traversing MessageBlock chains */

MessageBlock mb1 = MessageBlock.createObjectBasedMessageBlock( "Hello" );
MessageBlock mb2 = MessageBlock.createObjectBasedMessageBlock( "Hi", mb1 );
MessageBlock mb3 = MessageBlock.createObjectBasedMessageBlock( "Howdy", mb2 );

MessageBlock currentMb = mb1;

while ( currentMb != null ) {
    System.out.println( currentMb.getMessageString( ) );
    currentMb = currentMb.getNext( );
}
```


# References
[Sch94] Douglas Schmidt, "Reactor: An Object Behavioral Pattern for Concurrent Event Demultiplexing and Event Handler Dispatching," Proceedings of the First Pattern Languages of Programs conference in Monticello, Illinois, Aug. 1994.
