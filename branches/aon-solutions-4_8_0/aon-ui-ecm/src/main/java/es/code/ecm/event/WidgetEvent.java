/**
 * 
 */
package es.code.ecm.event;

import java.util.EventObject;

/**
 * An gets delivered whenever a widget action is performed.A WidgetEvent object is sent as an 
 * argument to the WidgetListener methods.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public class WidgetEvent extends EventObject {

	public WidgetEvent(Object source) {
		super(source);
	}

}
