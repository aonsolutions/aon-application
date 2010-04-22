package com.code.aon.common.event;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;

/**
 * This is a utility class that can be used by <code>IManagerBean</code> classes
 * that support CRUD after operations.  
 * You can use an instance of this class as a member field of your bean and delegate 
 * various work to it.
 *
 * @author 	Consulting & Development.
 *
 */
public class ManagerBeanListenerSupport implements IManagerBeanListener {

    // listener list.
	private List<IManagerBeanListener> listeners;

	/**
	 * Construct a ManagerBeanListenerSupport
	 */
	public ManagerBeanListenerSupport() {
		listeners = new ArrayList<IManagerBeanListener>();		
	} 

	/**
	 * Add a IManagerBeanListener to the listener list.
     * The same <code>listener</code> object may be added more than once, and will be called
     * as many times as it is added.
     * 
	 * @param listener
	 */
	public void addListener( IManagerBeanListener listener ) {
		listeners.add( listener );
	}

	/**
     * Remove a IManagerBeanListener from the listener list.
     * This removes a IManagerBeanListener that was registered.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
	 * 
	 * @param listener
	 */
	public void removeListener( IManagerBeanListener listener ) {
		listeners.remove( listener );
	}

	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException{
		for (IManagerBeanListener listener: listeners) {
			listener.beanInserted( evt );
		}
	}

	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException{
		for (IManagerBeanListener listener: listeners) {
			listener.beanUpdated( evt );
		}
	}

	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException{
		for (IManagerBeanListener listener: listeners) {
			listener.beanRemoved( evt );
		}
	}

}
