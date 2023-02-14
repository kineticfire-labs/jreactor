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


import java.util.TimerTask;
import com.kineticfire.patterns.reactor.TimerSelector;
import com.kineticfire.patterns.reactor.Handle;

/**
 * This class provides a wrapper around the java.util.TimerTask to generate
 * timer events and notify the TimerSelector. The StandardTimeBaseTask is
 * created with a reference to the TimerSelector and a reference to the handle
 * associated with the timer event. Upon expiration of the StandardTimeBaseTask,
 * the TimerSelector is notified with the handle that references the timer
 * event.
 * <p>
 * The class extends TimerTask which may be submitted to a java.util.Timer
 * object. The Timer manages timer tasks.
 * 
 * @author Kris Hall
 * @author version 11-22-09
 */

public class StandardTimebaseTask extends TimerTask {

    // *********************************************
    // ~ Instance/static variables
    
    private TimerSelector timerSelector;
    private Handle handle;

    
    // *********************************************
    // ~ Constructors

    /**
     * Constructs the StandardTimebaseTask with null references to
     * TimerSelector and the managed Handle.
     * 
     */
    public StandardTimebaseTask( ) {
        this.timerSelector = null;
        this.handle = null;
    }

    /**
     * Constructs the StandardTimebaseTask with references to TimerSelector
     * and the Handle the task is managing.
     * 
     * @param timerSelector
     *            a reference to the timerSelector to notify on the expiration
     *            of this timer event
     * @param handle
     *            the handle which registered this timer event
     */
    public StandardTimebaseTask( TimerSelector timerSelector, Handle handle ) {
        this.timerSelector = timerSelector;
        this.handle = handle;
    }

    
    
    // *********************************************
    // ~ METHODS

    /**
     * Method invoked, per the TimerTask class, when this timer event expires.
     * Upon expiration, the TimerSelector is notified of the handle referencing
     * the timer event.
     */
    public void run( ) {
        timerSelector.timerExpired( handle );
    }

    
    /**
     * Returns the handle referencing the timer event.
     * 
     * @return the handle referencing the timer event
     */
    public Handle handle( ) {
        return ( handle );
    }

}