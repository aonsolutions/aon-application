package com.code.aon.common.event;

import java.util.EventObject;

import com.code.aon.ql.Criteria;

/**
 * A "ManagerBean" event gets delivered whenever an operation is performed  
 * over a bean. A ManagerBeanEvent object is sent as an
 * argument to the IManagerBeanListener and IManagerBeanVetoListener methods.
 * 
 * @author 	Consulting & Development.
 *
 */

public class FinderBeanEvent extends EventObject {

	/**
     * Construct a new <code>ManagerBeanEvent</code>.
	 * 
	 * @param source
	 */
	public FinderBeanEvent(Object source) {
		super(source);
	}

	/**
	 * Return the <code>Criteria</code>
	 * 
	 * @return The <code>Criteria</code>
	 */
	public Criteria getCriteria() {
		return (Criteria) super.getSource();
	}
}
