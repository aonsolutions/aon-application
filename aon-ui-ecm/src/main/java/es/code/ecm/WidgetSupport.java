package es.code.ecm;

import java.io.Serializable;

import sun.awt.EventListenerAggregate;

import es.code.ecm.event.WidgetEvent;
import es.code.ecm.event.WidgetListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public class WidgetSupport implements Serializable {

	// Manages the listener list.
	private transient EventListenerAggregate listeners;

	/** 
	 * The object to be provided as the "widget" for any generated events.
	 * @serial
	 */
	private Object widget;

	/**
	 * Constructs a <code>WidgetSupport</code> object.
	 *
	 * @param widgetBean  The bean to be given as the widget for any events.
	 */
	public WidgetSupport(Object widgetBean) {
		if (widgetBean == null) {
			throw new NullPointerException();
		}
		widget = widgetBean;
	}

	/**
	 * Add a WidgetListener to the listener list.
	 * 
	 * @param l
	 */
	public void addWidgetListener(WidgetListener l) {
		if (l == null) {
		    return;
		}

		if (listeners == null) {
			listeners = new EventListenerAggregate( WidgetListener.class );
		}
		listeners.add( l );
	}

	/**
	 * Remove a WidgetListener from the listener list.
	 * 
	 * @param l
	 */
	public void removeWidgetListener(WidgetListener l) {
		if (l == null) {
		    return;
		}

		if (listeners == null) {
			return;
		}
		listeners.remove( l );
	}

	/**
     * Fire a WidgetEvent to any registered listeners.
	 */
	public void fireWidgetSelected() {
		if ( listeners != null ) {
			Object[] list = listeners.getListenersInternal();
			for (int i = 0; i < list.length; i++) {
				WidgetListener target = (WidgetListener)list[i];
				target.selected( new WidgetEvent( this.widget ) );
			}
		}
	}

}
