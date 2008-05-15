/**
 * 
 */
package es.code.cdr.core;

import java.io.Serializable;

import es.code.cdr.beans.CDRNode;
import es.code.cdr.event.WidgetListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 11/07/2007
 *
 */
public interface Widget extends Serializable {

	/**
	 * Add a WidgetListener to the listener list.
	 *   
	 * @param l
	 */
	void addWidgetListener(WidgetListener l);

	/**
	 * Remove a WidgetListener from the listener list.
	 * 
	 * @param l
	 */
	void removeWidgetListener(WidgetListener l);

	/**
	 * Gets selected widget selected node.
	 *  
	 * @return
	 */
	CDRNode getSelectedNode();

	/**
	 * Performs widget action depending on parent widget.
	 * 
	 * @param dependentWidget
	 */
	void perform(Widget dependentWidget);
}
