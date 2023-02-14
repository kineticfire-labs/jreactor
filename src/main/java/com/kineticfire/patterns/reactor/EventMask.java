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
 * The EventMask class defines the set of valid event types for the JReactor.
 * The class provides readable event names for the numeric event values and
 * offers convenience methods for examining and comparing events.
 * <p>
 * The operation-set bits within EventMask related to SelectableChannel are
 * equal to those defined by java.nio.channels.SelectionKey.  That is:
 * <p>
 * EventMask.CREAD = SelectionKey.OP_READ
 * <p>
 * EventMask.CWRITE = SelectionKey.OP_WRITE
 * <p>
 * EventMask.CONNECT = SelectionKey.OP_CONNECT
 * <p>
 * EventMask.ACCEPT = SelectionKey.OP_ACCEPT
 * 
 * @author Kris Hall
 * @version 07-01-09
 */

public abstract class EventMask {

    
    //*********************************************
    //~ Static variables
    
    public static final int NOOP       =      0;
    public static final int CREAD      =      1;
    //public static final int QWRITE     =      2;
    public static final int CWRITE     =      4;
    public static final int CONNECT    =      8;
    public static final int ACCEPT     =     16;
    public static final int QREAD      =     32;
    public static final int TIMER      =     64;
    public static final int LOCK       =    128;
    public static final int SIGNAL     =    256;
    public static final int ERROR       =    512;
    public static final int BLOCKING   =   1024;

    //public static final int QUEUEOPS   = EventMask.QREAD | EventMask.QWRITE;
    public static final int CHANNELOPS = EventMask.CREAD | EventMask.CWRITE | EventMask.ACCEPT | EventMask.CONNECT;    
    public static final int VALIDOPS   = EventMask.QREAD | EventMask.CREAD | EventMask.CWRITE | EventMask.ACCEPT | EventMask.CONNECT | EventMask.TIMER | EventMask.LOCK | EventMask.SIGNAL | EventMask.ERROR | EventMask.BLOCKING;  // removed QWRITE

    
    
    //*********************************************
    //~ Methods
    
    /**
     * Determines if ops contains an EventMask.QREAD.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.QREAD and false otherwise
     * 
     */
    public static boolean isQRead( int ops ) {        
        return( contains( EventMask.QREAD, ops ) );        
    }    


    
    /*
     * Determines if ops contains an EventMask.QWRITE.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.QWRITE and false otherwise
     * 
     *
    public static boolean isQWrite( int ops ) {
        return( contains( EventMask.QWRITE, ops ) );
    }
    */

    
    /*
     * Determines if ops containss EventMask.{QREAD | QWRITE}.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.{QREAD | QWRITE} and false
     *         otherwise
     * 
     *
    public static boolean isQueue( int ops ) {
        return( ( ops & EventMask.QUEUEOPS ) > 0 );
    }
    */
    
    
    /**
     * Determines if ops contains an EventMask.CREAD.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.CREAD and false otherwise
     * 
     */
    public static boolean isCRead( int ops ) {
        return( contains( EventMask.CREAD, ops ) );
    }    


    /**
     * Determines if ops contains an EventMask.CWRITE.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.CWRITE and false otherwise
     * 
     */
    public static boolean isCWrite( int ops ) {
        return( contains( EventMask.CWRITE, ops ) );
    }

    /**
     * Determines if ops contains an EventMask.CONNECT.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.CONNECT and false otherwise
     * 
     */
    public static boolean isConnect( int ops ) {
        return( contains( EventMask.CONNECT, ops ) );
    }


    /**
     * Determines if ops contains an EventMask.ACCEPT.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.ACCEPT and false otherwise
     * 
     */
    public static boolean isAccept( int ops ) {
        return( contains( EventMask.ACCEPT, ops ) );
    }
    
    /**
     * Determines if ops contains EventMask.{ACCEPT | CONNECT | CREAD | CWRITE}.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.{ACCEPT | CONNECT | CREAD |
     *         CWRITE} and false otherwise
     * 
     */
    public static boolean isChannel( int ops ) { 
        return( ( ops & EventMask.CHANNELOPS ) > 0 );
    }


    /**
     * Determines if ops contains an EventMask.TIMER.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.TIMER and false otherwise
     * 
     */
    public static boolean isTimer( int ops ) {
        return( contains( EventMask.TIMER, ops ) );
    }


    /**
     * Determines if ops contains an EventMask.LOCK.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.LOCK and false otherwise
     * 
     */
    public static boolean isLock( int ops ) {
        return( contains( EventMask.LOCK, ops ) );
    }


    /**
     * Determines if ops contains an EventMask.SIGNAL.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.SIGNAL and false otherwise
     * 
     */
    public static boolean isSignal( int ops ) {
        return( contains( EventMask.SIGNAL, ops ) );
    }

    
    /**
     * Determines if ops contains an EventMask.ERROR.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.ERROR and false otherwise
     * 
     */
    public static boolean isError( int ops ) {
        return( contains( EventMask.ERROR, ops ) );
    }

    
    /**
     * Determines if ops contains an EventMask.BLOCKING.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains an EventMask.BLOCKING and false otherwise
     * 
     */
    public static boolean isBlocking( int ops ) {
        return( contains( EventMask.BLOCKING, ops ) );
    }

    

    /**
     * Determines if ops is exactly an EventMask.NOOP.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.NOOP and false otherwise
     * 
     */
    public static boolean xIsNoop( int ops ) {
        return( equals( EventMask.NOOP, ops ) );
    }

    
    /**
     * Determines if ops is exactly an EventMask.QREAD.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.QREAD and false otherwise
     * 
     */
    public static boolean xIsQRead( int ops ) {
        return( equals( EventMask.QREAD, ops ) );
    }    


    /*
     * Determines if ops is exactly an EventMask.QWRITE.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.QWRITE and false otherwise
     * 
     *
    public static boolean xIsQWrite( int ops ) {
        return( equals( EventMask.QWRITE, ops ) );
    }
    */
    
    
    /*
     * Determines if ops contains only a combination of queue events
     * consisting of EventMask.{QREAD | QWRITE}.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.{QREAD | QWRITE} and false
     *         otherwise
     * 
     *
    public static boolean xIsQueue( int ops ) {
        boolean qops = ( EventMask.QUEUEOPS & ops ) > 0;
        boolean nonqops = ( ~EventMask.QUEUEOPS & ops ) > 0;
        return( qops && !nonqops );
    }
    */
    
    
    /**
     * Determines if ops is exactly an EventMask.CREAD.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.CREAD and false otherwise
     * 
     */
    public static boolean xIsCRead( int ops ) {
        return( equals( EventMask.CREAD, ops ) );
    }    


    /**
     * Determines if ops is exactly an EventMask.CWRITE.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.CWRITE and false otherwise
     * 
     */
    public static boolean xIsCWrite( int ops ) {
        return( equals( EventMask.CWRITE, ops ) );
    }

    /**
     * Determines if ops is exactly an EventMask.CONNECT.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.CONNECT and false otherwise
     * 
     */
    public static boolean xIsConnect( int ops ) {
        return( equals( EventMask.CONNECT, ops ) );
    }


    /**
     * Determines if ops is exactly an EventMask.ACCEPT.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.ACCEPT and false otherwise
     * 
     */
    public static boolean xIsAccept( int ops ) {
        return( equals( EventMask.ACCEPT, ops ) );
    }
    
    /**
     * Determines if ops contains only a combination of channel events
     * consisting of EventMask.{ACCEPT | CONNECT | CREAD | CWRITE}.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.{ACCEPT | CONNECT | CREAD |
     *         CWRITE} and false otherwise
     * 
     */
    public static boolean xIsChannel( int ops ) {
        boolean cops = ( EventMask.CHANNELOPS & ops ) > 0;
        boolean noncops = ( ~EventMask.CHANNELOPS & ops ) > 0;
        return( cops && !noncops );
    }


    /**
     * Determines if ops is exactly an EventMask.TIMER.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.TIMER and false otherwise
     * 
     */
    public static boolean xIsTimer( int ops ) {
        return( equals( EventMask.TIMER, ops ) );
    }


    /**
     * Determines if ops is exactly an EventMask.LOCK.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.LOCK and false otherwise
     * 
     */
    public static boolean xIsLock( int ops ) {
        return( equals( EventMask.LOCK, ops ) );
    }


    /**
     * Determines if ops is exactly an EventMask.SIGNAL.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.SIGNAL and false otherwise
     * 
     */
    public static boolean xIsSignal( int ops ) {
        return( equals( EventMask.SIGNAL, ops ) );
    }

    
    /**
     * Determines if ops is exactly an EventMask.ERROR.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.ERROR and false otherwise
     * 
     */
    public static boolean xIsError( int ops ) {
        return( equals( EventMask.ERROR, ops ) );
    }

    
    /**
     * Determines if ops is exactly an EventMask.BLOCKING.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops is exactly an EventMask.BLOCKING and false otherwise
     * 
     */
    public static boolean xIsBlocking( int ops ) {
        return( equals( EventMask.BLOCKING, ops ) );
    }
    
    
    /**
     * Computes the bitwise bitwise AND of two operands.
     * 
     * @param opsA
     *            the first interest operations to evaluate
     * @param opsB
     *            the second interest operations to evaluate
     * @return the bitwise bitwise AND of the two interest operations
     * 
     */
    public static int and( int opsA, int opsB ) {
        return( opsA & opsB );
    }
    
    
    /**
     * Computes the bitwise bitwise OR of two operands.
     * 
     * @param opsA
     *            the first interest operations to evaluate
     * @param opsB
     *            the second interest operations to evaluate
     * @return the bitwise bitwise OR of the two interest operations
     * 
     */
    public static int or( int opsA, int opsB ) {
        return( opsA | opsB );
    }
    
    
    /**
     * Computes the bitwise bitwise XOR of two operands.
     * 
     * @param opsA
     *            the first interest operations to evaluate
     * @param opsB
     *            the second interest operations to evaluate
     * @return the bitwise bitwise XOR of the two interest operations
     * 
     */
    public static int xor( int opsA, int opsB ) {
        return( opsA ^ opsB );
    }
    
    
    /**
     * Determines if the interest operations in opsA are contained in the
     * interest operations of opsB. The method can be read as "are the interest
     * ops in 'a' contained in 'b'?"
     * 
     * @param opsA
     *            the first interest operations to evaluate
     * @param opsB
     *            the second interest operations to evaluate
     * @return true if the interest operations in opsA are contained in the
     *         interest operations of opsB or false otherwise
     * 
     */
    public static boolean contains( int opsA, int opsB ) {
        boolean contains = false;
        if ( ( opsA & opsB ) == opsA ) {
            contains = true;
        }
        return( contains );
    }
    
    
    /**
     * Determines if the interest operations in opsA are exactly identical to
     * those in opsB.
     * 
     * @param opsA
     *            the first interest operations to evaluate
     * @param opsB
     *            the second interest operations to evaluate
     * @return true if the interest operations in opsA are exactly identical to
     *         those in opsB and false otherwise
     * 
     */
    public static boolean equals( int opsA, int opsB ) {
        boolean equals = false;
        
        if ( opsA == opsB ) {
            equals = true;
        }
        return( equals );
    }
    
    
    /**
     * Determines if the value of ops is valid.
     * 
     * @param ops
     *            interest operations to evaluate
     * @return true if ops contains a valid value and false otherwise
     */
    public static boolean isValid( int ops ) {        
        return( contains( ops, VALIDOPS ) );
    }
        
}