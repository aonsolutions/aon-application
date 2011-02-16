package com.code.aon.common;


/**
 * This class manages all the beans registered in the application.
 *  
 * @author Consulting & Development. Eugenio Castellano - 12-dic-2005
 * 
 */
public class BeanManager {

	private static IBeanManager manager = DefaultBeanManager.getInstance();
	
	/**
	 * Gets the manager.
	 *
	 * @return the manager
	 */
	public static IBeanManager getManager() {
		return manager;
	}

	/**
	 * Sets the manager.
	 *
	 * @param manager the new manager
	 */
	public static void setManager(IBeanManager manager) {
		BeanManager.manager = manager;
	}

	/**
	 * Return the <code>IManagerBean</code> bound to the POJO Class name.
	 * 
	 * @param pojo
	 * @return The requested <code>IManagerBean</code>.
	 * @throws ManagerBeanException
	 */
	public static IManagerBean getManagerBean(String pojo) throws ManagerBeanException {
		try {
			Class<?> pojoClass = Class.forName( pojo );
			return getManagerBean( pojoClass );
		} catch (ClassNotFoundException e) {
            throw new ManagerBeanException(e.getMessage(), e);
        }
	}

	/**
	 * Return the <code>IManagerBean</code> bound to the POJO Class.
	 * 
	 * @param pojoClass
	 * @return The requested <code>IManagerBean</code>.
	 * @throws ManagerBeanException
	 */
	public static IManagerBean getManagerBean(Class<?> pojoClass) throws ManagerBeanException {
		return manager.getManagerBean(pojoClass);
	}
	
}