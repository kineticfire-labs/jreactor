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



// Core Reactor
import com.kineticfire.patterns.reactor.Handle;
import com.kineticfire.patterns.reactor.MessageBlock;

// Handler & Adapter
import com.kineticfire.patterns.reactor.HandlerAdapter;
import com.kineticfire.patterns.reactor.handler.Handler;

// Event & Event Sources
import com.kineticfire.patterns.reactor.Event;
import com.kineticfire.patterns.reactor.MessageQueue;
import java.nio.channels.SelectableChannel;

// Util
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.Date;

// extra
import com.kineticfire.patterns.reactor.GenericHandler;



public class JReactor_CompositeSelector extends JReactor {

	private Handle reportHandle;
	private Exception reportE;
	private MessageBlock reportCommand;

	JReactor_CompositeSelector( ) {
		reportHandle = null;
		reportE = null;
		reportCommand = null;
	}

   	
   	

   	//*********************************************
   	//~ METHODS
   	
   	
   	

    	public void dispatch( ) {
	}
    
 

	public void shutdown( ) {
	}

	
	
	public boolean isDispatching( ) {
		return( true );
	}
	
	
	public boolean isShutdown( ) {
		return( false );
	}

	
	
	
	
	public boolean isRegistered( Handle handle ) {
		return( true );
	}
	
	
	
	public int interestOps( Handle handle ) {
		return( EventMask.NOOP );
	}
	
	
	public void interestOps( Handle handle, int interestOps, Handle errorHandle ) {
	}

	
	public void interestOps( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle ) {
	}

	
	public void interestOpsOr( Handle handle, int interestOps, Handle errorHandle ) {
	}
	
	
	public void interestOpsOr( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle ) {
	}
	
	
    	public void interestOpsAnd( Handle handle, int interestOps, Handle errorHandle ) {
	}
	
    
    	private void interestOpsAndInternal( Handle handle, int interestOps, HandlerAdapter adapter, Handle errorHandle ) {
    	}
    
    
    	public void interestOpsAnd( Handle lockHandle, Handle handle, int interestOps, Handle errorHandle ) {
	}
    

	public void deregister( Handle handle, Handle errorHandle ) {
	}
	
	
	public void deregister( Handle lockHandle, Handle handle, Handle errorHandle ) {
	}

	
	public Handler handler( Handle handle ) {
		return( new Handler_LockSelector( ) );
	}
	
	
	//	 ------------------ HANDLER ------------------
	
    
	public boolean isRegistered( Handler handler ) {
		return( false );
	}
	
	
	public Set<Handle> handles( Handler handler ) {
		return( new HashSet<Handle>( ) );
	}
	
	
	
	public void deregister( Handler handler, Handle errorHandle ) {
	}

	
	public void deregister( Handle lockHandle, Handler handler, Handle errorHandle ) {
	}

	

   	public Handle registerQueue( MessageQueue queue, Handler handler, int interestOps ) {
		return( new Handle( ) );
	}


    	public boolean isRegisteredQueue( MessageQueue queue ) {
		return( false );
    	}
    
    	public boolean isRegisteredQueue( Handle queueHandle ) {
		return( false );
        }

    
    
	// ------------------ CHANNEL ------------------
       
 
    	public Handle registerChannel( SelectableChannel channel, Handler handler, int interestOps ) {
		return( new Handle( ) );
        }
        

    	public boolean isRegisteredChannel( SelectableChannel channel ) {
		return( false );
        }
        

    	public boolean isRegisteredChannel( Handle channelHandle ) {
		return( false );
        }
    
        
        
	// ------------------ TIMER ------------------

    
    	public Handle registerTimerOnce( Date time, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}


    	public Handle registerTimerOnce( long delay, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}


    	public Handle registerTimerFixedDelay( Date firstTime, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}

    
	public Handle registerTimerFixedDelay( long delay, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}
   
 
    	public Handle registerTimerFixedRate( Date firstTime, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}

    	public Handle registerTimerFixedRate( long delay, long period, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}
    
    
    	public boolean isRegisteredTimer( Handle timerHandle ) {
		return( false );
    	}

    

    	public boolean cancelTimer( Handle timerHandle ) {
		return( false );
    	}
       
 
    	public boolean cancelTimer( Handle lockHandle, Handle timedHandle ) {
		return( false );
    	}
    
    
	// ------------------ LOCK ------------------
        
    	public Handle registerLock( Handler lockHandler, int interestOps ) {    	
		return( new Handle( ) );
        }

        
    	public void addMember( Handle lockHandle, Handler managedHandler, Handle errorHandle ) {
    	}
   
 
    	public void removeMember( Handle lockHandle, Handler managedHandler, Handle errorHandle ) {
        }

    
    	public boolean isRegisteredLock( Handle lockHandle ) {
		return( false );
    	}	
    
    
    	public boolean isMember( Handle lockHandle, Handler managedHandler ) {
		return( false );
    	}
    

    	public boolean isLockedMember( Handle lockHandle, Handler handler ) {
		return( false );
    	}
    
    
    
    	public void lock( Handle lockHandle, Handle errorHandle ) {
        }
    
    
    	public void unlock( Handle lockHandle, Handle errorHandle ) {
        }
    
    
    
    	public boolean isOpen( Handler managedHandler ) {
		return( false );
    	}
    
    
    	public boolean isOpen( Handle lockHandle ) {
		return( false );
    	}
    
    	public boolean isPending( Handler managedHandler ) {
		return( false );
    	}
    
    	public boolean isPending( Handle lockHandle ) {
		return( false );
    	}
    
    
    	public boolean isLocked( Handler managedHandler ) {
		return( false );
    	}
   
 
    	public boolean isLocked( Handle lockHandle ) {
		return( false );
    	}
    
    
    

	// ------------------ SIGNAL ------------------    
    
    
    
    	public Handle registerSignal( String signalName, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}
    
    
    	public boolean isRegisteredSignal( String signalName ) {
		return( false );
    	}

    
    	public boolean isRegisteredSignal( Handle signalHandle ) {
		return( false );
    	}
    
	// ------------------ ERROR ------------------
    
    
    	public Handle registerError( Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}
    
    
    	public Handle newErrorHandle( Handler handler ) {
		return( new Handle( ) );
    	}
	
    
	public Handle newErrorHandle( Handle errorHandle ) {
		return( new Handle( ) );
	}	
	
 
    	public Handle getErrorHandle( Handler handler ) {
		return( new Handle( ) );
    	}
    
    
	public boolean isRegisteredError( Handler handler ) {
		return( false );
	}
	
	
    	public boolean isRegisteredError( Handle errorHandle ) {
		return( false );
    	}
    
 

    
    
	// ------------------ BLOCKING TASK ------------------
    
    
    	public Handle registerBlocking( Runnable runnable, Handler handler, int interestOps ) {
		return( new Handle( ) );
    	}
   
	public Handle registerBlockingGroup( Set<Runnable> runnableSet, Handler handler, int interestOps ) { 
		return( new Handle( ) );
    	}
	
	
    	public boolean isRegisteredBlocking( Handle blockingHandle ) {
		return( false );
    	}
    
    
    
   	
   	// *************************************************************************
   	// ****************** JREACTOR SPECIFIC ****************** 
   	// *************************************************************************   

   		

	public void configureWorkerThreadPool( int workerThreadNum ) {
	}
	
	
	public void configureWorkerThreadPool( int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitsFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitsLast ) {
	}
	
	
	public void configureWorkerThreadPool( int workerThreadNum, int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitsFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitsLast ) {
	}

	
	public void configureCriticalThreadPool( int terminationTimeoutCriticalFirst, TimeUnit terminationTimeoutUnitsCriticalFirst, int terminationTimeoutCriticalLast, TimeUnit terminationTimeoutUnitsCriticalLast ) {
	}
	
	

	public void configureBlockingThreadPool( int blockingThreadNum ) {
	}
	
	
	public void configureBlockingThreadPool( int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitLast ) {
	}
	
	
	public void configureBlockingThreadPool( int blockingThreadNum, int terminationTimeoutFirst, TimeUnit terminationTimeoutUnitFirst, int terminationTimeoutLast, TimeUnit terminationTimeoutUnitLast ) {
	}
	

	public void enableQueueSelector( ) {
	}
	
	
	
	public void enableChannelSelector( ) {
	}
	
	
	public void enableTimedSelector( ) {
	}

	
	public void enableBlockingSelector( ) {
	}
	

	
	public void enableSignalSelector( ) {
	}
	
    
    
   	// *************************************************************************
   	// ****************** ERROR REPORTING ****************** 
   	// *************************************************************************    
	
    	protected void reportError( Handle errorHandle, Exception e ) {
		reportHandle = errorHandle;
		reportE = e;
    	}	
    
    
    	protected void reportError( Handle errorHandle, Exception e, MessageBlock command ) {
		reportHandle = errorHandle;
		reportE = e;
		reportCommand = command;
    	}
    
    
    	protected void reportCriticalError( Exception e ) {
		reportE = e;
    	}


	// added for testing
	public Handle getReportHandle( ) {
		return( reportHandle );
	}

    
	// added for testing
	public Exception getReportE( ) {
		return( reportE );
	}

	// added for testing
	public MessageBlock getReportCommand( ) {
		return( reportCommand );
	}


   	// *************************************************************************
   	// ****************** ADAPTER COORDINATION ****************** 
   	// *************************************************************************

    	protected void resumeSelection( Handler handler, Handle handle, List<MessageBlock> commands ) {
    	}
    
    

   	// *************************************************************************
   	// ****************** DISPATCH ****************** 
   	// *************************************************************************


	public void finalize( ) {
		shutdown( );
	}
	
}
