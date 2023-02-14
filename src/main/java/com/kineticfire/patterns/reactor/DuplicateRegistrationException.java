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


import com.kineticfire.patterns.reactor.InvalidResourceException;

/**
 * Thrown to indicate that registration for an event source failed because that
 * event source is already registered.
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public class DuplicateRegistrationException extends InvalidResourceException {
    

        /*
         * Determines if a de-serialized file is compatible with this class.
         * <p>
         * Maintainers must change this value if and only if the new version of this
         * class is not compatible with old versions. See Sun docs for <a
         * href=http://java.sun.com/products/jdk/1.1/docs/guide
         * /serialization/spec/version.doc.html> details. </a>
         * <p>
         * Not necessary to include in first version of the class, but included here
         * as a reminder of its importance.
        */
        private static final long serialVersionUID = 20120000L;

    
    /**
     * Creates an empty DuplicateRegistrationException.
     * 
     */
    public DuplicateRegistrationException( ) {

    }
    
}
