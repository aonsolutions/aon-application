package com.code.aon.common.event;

import com.code.aon.common.ManagerBeanException;

/**
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean. You can register a IManagerBeanListener with a source
 * bean so as to be notified of any bound operations.
 * 
 * @author 	Consulting & Development.
 * @see		com.code.aon.common.event.IManagerBeanListener
 *
 */

public class ManagerBeanListenerAdapter implements IManagerBeanListener {

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.event.IManagerBeanListener#beanInserted(com.code.aon.common.event.ManagerBeanEvent)
	 */
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException{
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.event.IManagerBeanListener#beanUpdated(com.code.aon.common.event.ManagerBeanEvent)
	 */
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.event.IManagerBeanListener#beanRemoved(com.code.aon.common.event.ManagerBeanEvent)
	 */
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException{
	}

}
