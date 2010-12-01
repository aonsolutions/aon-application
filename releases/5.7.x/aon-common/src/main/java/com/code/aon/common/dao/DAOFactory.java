package com.code.aon.common.dao;

import com.code.aon.common.jndi.ServiceLocator;

/**
 * Factory of IDAO instances.
 *  
 * @author Consulting & Development. Aimar Tellitu - 22-jun-2005
 * @version 1.0
 * 
 * @since 1.0
 */
public class DAOFactory {

	/**
	 * Return an instance of <code>DAOFactory</code>
	 * 
	 * @param jndiDAOFactory
	 * @return An instance of <code>DAOFactory</code>
	 * @throws DAOSysException
	 */
	public static Object getDAOFactory(String jndiDAOFactory)
			throws DAOSysException {
		try {
			ServiceLocator sl = ServiceLocator.getInstance();
			String className = sl.getProperty(jndiDAOFactory);
			return Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new DAOSysException(e.getMessage(), e);
		}
	}

}