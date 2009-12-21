package com.code.aon.common;

import com.code.aon.ql.Criteria;

/**
 * This class provides a <code>Criteria</code> object.
 * 
 * @author Consulting & Development. 
 *
 */
public interface ICriteriaProvider {

	/**
	 * Return the <code>Criteria</code> object.
	 * 
	 * @return The <code>Criteria</code> object.
	 * @throws ManagerBeanException
	 */
	Criteria getCriteria() throws ManagerBeanException;
	
}
