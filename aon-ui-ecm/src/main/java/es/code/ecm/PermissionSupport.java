package es.code.ecm;

import java.io.Serializable;

import sun.awt.EventListenerAggregate;
import es.code.ecm.event.PermissionEvent;
import es.code.ecm.event.PermissionListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 27/11/2008
 *
 */
public class PermissionSupport implements Serializable {

	// Manages the listener list.
	private transient EventListenerAggregate listeners;

	/**
	 * Add a PermissionListener to the listener list.
	 * 
	 * @param l
	 */
	protected void addPermissionListener(PermissionListener l) {
		if (l == null) {
		    return;
		}

		if (listeners == null) {
			listeners = new EventListenerAggregate( PermissionListener.class );
		}
		listeners.add( l );
	}

	/**
	 * Remove a PermissionListener from the listener list.
	 * 
	 * @param l
	 */
	protected void removePermissionListener(PermissionListener l) {
		if (l == null) {
		    return;
		}

		if (listeners == null) {
			return;
		}
		listeners.remove( l );
	}

	/**
     * Fire a PermissionEvent to any registered listeners.
	 */
	protected void firePermissionAdded(PermissionEvent event) {
		if ( listeners != null ) {
			Object[] list = listeners.getListenersInternal();
			for (int i = 0; i < list.length; i++) {
				PermissionListener target = (PermissionListener)list[i];
				if ( event == null )
					event = new PermissionEvent( null, null );
				target.permissionAdded( event );
			}
		}
	}

	/**
     * Fire a PermissionEvent to any registered listeners.
	 */
	protected void firePermissionChanged(PermissionEvent event) {
		if ( listeners != null ) {
			Object[] list = listeners.getListenersInternal();
			for (int i = 0; i < list.length; i++) {
				PermissionListener target = (PermissionListener)list[i];
				if ( event == null )
					event = new PermissionEvent( null, null );
				target.permissionChanged( event );
			}
		}
	}

}
