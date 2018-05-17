package net.aonsolutions.core.dbutils.event;


import java.util.ArrayList;
import java.util.List;


public class DBUtilsEventSupport   {

	private List<DBUtilsListener> listeners;
	
	public DBUtilsEventSupport () {
		super ();
		listeners = new ArrayList<DBUtilsListener>();
	}

	public void addDBUtilsListener ( DBUtilsListener listener ) {
		if ( !listeners.contains ( listener ) ) {
			listeners.add ( listener );
		}
	}
	
	public void removeDBUtilsListener ( DBUtilsListener listener ) {
		if ( listeners.contains ( listener ) ) {
			listeners.remove ( listener );
		}
	}
	
	public void fireDBUtilsEvent ( DBUtilsEvent event ) {
		for (DBUtilsListener listener: listeners) {
			listener.eventHappen( event );
		}		
	}

}