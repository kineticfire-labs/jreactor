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


import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

/**
 * This class implements an Event for the JReactor. The Event indicates the
 * readiness of a registered event source for some ready event and carries
 * information about that event. The generation of an Event is only an
 * indication, not a guarantee, that an event source is ready for some
 * operation.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class Event {
    
    private Handle handle;
    private int readyOps;
    private MessageBlock info; 

    
       //*********************************************
       //~ Constructors


       /**
     * Constructs an Event with arbitrary new handle, readyOps of 0, and 'null'
     * info.
     * 
     */
       public Event( ) {
           handle = new Handle( );
           readyOps = 0;
           info = null;
       }
       

       /**
     * Constructs a Event with arbitrary new handle, readyOps of 'readyOps', and
     * 'null' info.
     * 
     * @param readyOps
     *            the interest operations for which the event source is ready
     */
       public Event( int readyOps ) {
           handle = new Handle( );
           this.readyOps = readyOps;
           info = null;
       }
       
       
       /**
     * Constructs an Event with 'handle' handle, readyOps of 'readyOps', and
     * 'null' info.
     * 
     * @param handle
     *            the handle associated to the event source that generated the
     *            event
     * @param readyOps
     *            the interest operations for which the event source is ready
     */
       public Event( Handle handle, int readyOps ) {
           this.handle = handle;
           this.readyOps = readyOps;
           info = null;
       }
       
       
       /**
     * Constructs an Event with 'handle' handle, readyOps of 'readyOps', and
     * 'info' info.
     * 
     * @param handle
     *            the handle associated to the event source that generated the
     *            event
     * @param readyOps
     *            the interest operations for which the event source is ready
     * @param info
     *            additional information about this event, potentially null
     */
       public Event( Handle handle, int readyOps, MessageBlock info ) {
           this.handle = handle;
           this.readyOps = readyOps;
           this.info = info;
       }
       
       
       //*********************************************
       //~ METHODS
       
       
       /**
     * Returns the handle associated with the event source that generated this
     * event.
     * 
     * @return the handle associated with the event source that generated this
     *         event
     * 
     */
       public Handle getHandle( ) {
           return( handle );
       }
       
       
       /**
     * Returns the ready operations for this event or 0 if the ready operations
     * have not been set.
     * 
     * @return the ready operations for this event or 0 if the ready operations
     *         have not been set
     */
       public int getReadyOps( ) {
           return( readyOps );
       }
       
       
       /**
     * Returns additional information as a MessageBlock for this event or null
     * if additional information has not been supplied.
     * 
     * @return additional information as a MessageBlock for this event or null
     *         if additional information has not been supplied
     */
       public MessageBlock getInfo( ) {
           return( info );
       }

}
