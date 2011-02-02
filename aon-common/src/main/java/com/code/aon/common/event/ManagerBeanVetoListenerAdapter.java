package com.code.aon.common.event;

/**
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean. You can register a IManagerBeanVetoListener with a source
 * bean so as to be notified of any bound operations.
 * 
 * @author 	Consulting & Development.
 * @see		com.code.aon.common.event.IManagerBeanVetoListener
 *
 */

public class ManagerBeanVetoListenerAdapter implements IManagerBeanVetoListener {

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.event.IManagerBeanVetoListener#vetoableBeanInserted(com.code.aon.common.event.ManagerBeanEvent)
	 */
	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.event.IManagerBeanVetoListener#vetoableBeanUpdated(com.code.aon.common.event.ManagerBeanEvent)
	 */
	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.event.IManagerBeanVetoListener#vetoableBeanRemoved(com.code.aon.common.event.ManagerBeanEvent)
	 */
	public void vetoableBeanRemoved(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
	}

}
