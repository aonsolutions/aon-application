/**
 * 
 */
package es.code.cdr.core;

import es.code.cdr.core.event.WidgetListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/07/2007
 *
 */
public abstract class AbstractWidget implements Widget {

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

	/**
     * Fire a WidgetEvent to any registered listeners.
	 */
	protected void fireWidgetSelected() {
    	support.fireWidgetSelected();
	}

}
