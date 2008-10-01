package com.code.aon.dbdumper.event;


import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;


/**
 * This class manages the DumpListeners
 * 
 * @author Consulting & Development. Iñigo GAyarre - 13/12/2006
 * @since 1.0
 *
 */
public class DumpEventSupport   {

	/**
	 * A list of object<DumpListener> listening to DumpEvents 
	 */
	private List<DumpListener> listeners;
	
	/**
	 * Constructor 
	 */
	public DumpEventSupport () {
		super ();
		listeners = new ArrayList<DumpListener>();
	}
	
	/**
	 * Add a DumpListener to the list
	 * 
	 * @param listener
	 */
	public void addDumpListener ( DumpListener listener ) {
		if ( !listeners.contains ( listener ) ) {
			listeners.add ( listener );
		}
	}
	
	/**
	 * Removes a DumpListener from the list
	 * 
	 * @param listener
	 */
	public void removeDumpListener ( DumpListener listener ) {
		if ( listeners.contains ( listener ) ) {
			listeners.remove ( listener );
		}
	}
	
	/**
	 * Notify all DumpListener in the list that a event happenend
	 * 
	 * @param event
	 */
	public void fireDumpEvent ( DumpEvent event ) {
		Iterator iter = listeners.iterator ();
		while ( iter.hasNext() ) {
			DumpListener listener = ( DumpListener ) iter.next() ;
			listener.eventHappen( event );
		}		
	}

}