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



/**
  * Provides a Handle that serves as a unique token that represents the relationship between a registered event source and its event handler.
  * <p>
  * The Handle may contain an optional attachment, which is an Object.  The attachment is not thread-safe.
  * 
  * @author Kris Hall
  * @version 07-01-09
  *
  */
public class Handle {


    private Object attachment;
    


    /**
      * Creates a Handle object.
      * 
      */
    public void Handle( ) {
        attachment = null;
    }


    /**
      * Attaches the Object 'attachment' to this Handle.
      *
      * @param attachment
      *    Object to attach to this Handle
      */
    public void attach( Object attachment ) {
        this.attachment = attachment;
    }


    /**
      * Returns the attachment.
      * <p>
      * 'Null' is returned if no attachment is contained by this Handle.
      *
      * @return the attachment of this Handle
      */
    public Object attachment( ) {
        return( attachment );
    }


    /**
      * Clears the attachment.
      * <p>
      * Subsequent calls to 'getAttachment()' will return 'null'.
      *
      */
    public void clearAttachment( ) {
        attachment = null;
    }
    
}
