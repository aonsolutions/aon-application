/**
 * 
 */
package es.code.cdr.ui.controller;

import es.code.ecm.Widget;
import es.code.ecm.WidgetSupport;
import es.code.ecm.event.WidgetListener;
import es.code.ecm.nodes.ECMNode;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/07/2007
 *
 */
public class InfoTabbedPane implements Widget {

	private static final long serialVersionUID = -2535022508224906010L;

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#addWidgetListener(es.code.cdr.ui.controller.event.WidgetListener)
	 */
	public void addWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support == null ) {
					support = new WidgetSupport(this);
				}
				support.addWidgetListener( l );
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#removeWidgetListener(es.code.cdr.ui.controller.event.WidgetListener)
	 */
	public void removeWidgetListener(WidgetListener l) {
		if ( l != null ) {
			synchronized (this) {
				if ( support != null ) {
					support.removeWidgetListener( l );
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#getSelected()
	 */
	public ECMNode getSelectedNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.code.cdr.ui.controller.Widget#perform(es.code.cdr.ui.controller.Widget)
	 */
	public void perform(Widget dependentWidget) {
		// TODO Auto-generated method stub
		System.out.println( "InfoTabbedPane perform method: " + dependentWidget.getClass() + " Clicked: " + dependentWidget.getSelectedNode() );
	}

}
