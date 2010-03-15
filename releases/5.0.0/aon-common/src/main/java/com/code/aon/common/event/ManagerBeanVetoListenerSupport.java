package com.code.aon.common.event;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a utility class that can be used by <code>IManagerBean</code> classes
 * that support CRUD vetoable operations.  
 * You can use an instance of this class as a member field of your bean and delegate 
 * various work to it.
 *
 * @author 	Consulting & Development.
 *
 */

public class ManagerBeanVetoListenerSupport implements IManagerBeanVetoListener {

    // listener list.
	private List<IManagerBeanVetoListener> listeners;
	
	/**
	 * Construct a ManagerBeanVetoListenerSupport
	 */
	public ManagerBeanVetoListenerSupport() {
		listeners = new ArrayList<IManagerBeanVetoListener>();		
	} 
	
	/**
	 * Add a IManagerBeanVetoListener to the listener list.
     * The same <code>listener</code> object may be added more than once, and will be called
     * as many times as it is added.
     * 
	 * @param listener
	 */
	public void addListener( IManagerBeanVetoListener listener ) {
		listeners.add( listener );
	}
	
	/**
     * Remove a IManagerBeanVetoListener from the listener list.
     * This removes a IManagerBeanListener that was registered.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
	 * 
	 * @param listener
	 */
	public void removeListener( IManagerBeanVetoListener listener ) {
		listeners.remove( listener );
	}

	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		for (IManagerBeanVetoListener listener: listeners) {
			listener.vetoableBeanInserted( evt );
		}
	}

	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		for (IManagerBeanVetoListener listener: listeners) {
			listener.vetoableBeanUpdated( evt );
		}
	}

	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		for (IManagerBeanVetoListener listener: listeners) {
			listener.vetoableBeanRemoved( evt );
		}
	}

}
