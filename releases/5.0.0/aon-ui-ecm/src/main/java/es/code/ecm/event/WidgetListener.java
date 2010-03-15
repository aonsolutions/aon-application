/**
 * 
 */
package es.code.ecm.event;

import java.util.EventListener;

/**
 * An event gets fired whenever a widget action is performed. 
 * You can register a WidgetListener with a source so as to be notified of any widget action. 
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public interface WidgetListener extends EventListener {

	/**
	 * This method gets called when a widget item is selected. 
	 * 
	 * @param event
	 */
	void selected(WidgetEvent event);

}
