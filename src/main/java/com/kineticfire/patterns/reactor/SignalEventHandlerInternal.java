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



import java.util.concurrent.LinkedBlockingQueue;
import com.kineticfire.patterns.reactor.SignalSelector;
import com.kineticfire.patterns.reactor.Handle;


/**
 * Provides an internal handler for signal events. The handler can be registered
 * with the JVM as a signal handler. When the signal is fired, this handler is
 * executed by the JVM. The internal JVM thread running this handler blocks.
 * This allows the SignalSelector and JReactor to fire an event and dispatch a
 * handler. The JVM thread continues to block until this object's cancel()
 * method is called. Causing the JVM signal handler to block is important
 * because some signals, like "shutdownhook", will cause the JVM to terminate as
 * soon as the JVM's signal handler thread returns.
 * <p>
 * This class is thread safe.
 * 
 * 
 * @author Kris Hall
 * @version 07-01-09
 */



public class SignalEventHandlerInternal extends Thread {

    //*********************************************
    //~ Instance/static variables
    
    SignalSelector signalSelector;
    Handle handle;
    LinkedBlockingQueue<Byte> queue;
    
    
       //*********************************************
       //~ Constructors
    
    /**
     * Creates a new SignalEventHandlerInternal object with references to the
     * signalSelector to coordinate the firing of an event and a reference to
     * the managed handle.
     * <p>
     * PRECONDITION: signalSelector and handle are not null
     * 
     * @param signalSelector reference to the SignalSelector to coordinate firing of a signal event
     * @param handle reference the handle managed by this object
     * 
     */
    public SignalEventHandlerInternal( SignalSelector signalSelector, Handle handle ) {
        this.signalSelector = signalSelector;
        this.handle = handle;
        queue = new LinkedBlockingQueue<Byte>( );          
    }
    
    
    //*********************************************
       //~ METHODS
    
    /**
     * Executed by the JVM's thread in response to a signal event for which this
     * internal handler was registered. Notifies that SignalSelector that an
     * EventMask.SIGNAL event is ready for this handle. If the SignalSelector
     * indicates that the event is valid, this handler blocks the internal JVM
     * thread until cancel() is called or the thread is interrupted.
     * 
     */
    public void run( ) {
        
        if ( signalSelector.signalFired( handle ) ) {
            
            try {
                /*
                 * blocking call to queue, waiting for: data available or
                 * InterruptedException
                 */
                Byte data = queue.take( );
            } catch ( InterruptedException e ) { }
        }
        
    }
    
    
    /**
     * Releases the internal JVM thread blocked on this internal handler after
     * the signal for which it was registered fired. If the internal thread has
     * not yet blocked, then the thread will never block. Calling this method
     * multiple times has no affect.
     */
    public void cancel( ) {
        Byte noop = Byte.parseByte( "0" );
        queue.add( noop );
    }
    
}
