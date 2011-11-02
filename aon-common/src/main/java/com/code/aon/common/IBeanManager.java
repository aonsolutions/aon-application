package com.code.aon.common;

public interface IBeanManager {

	/**
	 * Return the <code>IManagerBean</code> bound to the POJO Class.
	 * 
	 * @param pojoClass
	 * @return The requested <code>IManagerBean</code>.
	 * @throws ManagerBeanException
	 */
	IManagerBean getManagerBean(Class<? extends ITransferObject> pojoClass) throws ManagerBeanException;	
	
}