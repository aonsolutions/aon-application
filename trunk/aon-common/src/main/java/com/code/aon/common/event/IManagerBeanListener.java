package com.code.aon.common.event;

import com.code.aon.common.ManagerBeanException;

/**
 * An "ManagerBean" event gets fired whenever an operation is performed  
 * over a bean. You can register a IManagerBeanListener with a source
 * bean so as to be notified of any bound operations.
 * 
 * @author 	Consulting & Development.
 *
 */

public interface IManagerBeanListener {

	/**
     * This method gets called when a bean is inserted.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @throws ManagerBeanException
	 */
	void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException;

	/**
     * This method gets called when a bean is updated.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @throws ManagerBeanException
	 */
	void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException;

	/**
     * This method gets called when a bean is removed.
     * 
     * @param evt A ManagerBeanEvent object describing the event source.
	 * @throws ManagerBeanException
	 */
	void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException;

}
