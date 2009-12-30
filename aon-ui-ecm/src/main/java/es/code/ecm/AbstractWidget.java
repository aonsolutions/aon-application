/**
 * 
 */
package es.code.ecm;

import es.code.ecm.event.WidgetListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/07/2007
 *
 */
public abstract class AbstractWidget implements Widget {

	/** A description of any WidgetListeners which have been registered. */
	WidgetSupport support;

	@Override
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

	@Override
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
