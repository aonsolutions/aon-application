package com.code.aon.common;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.code.aon.common.bean.BeanConfigManager;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * This class manages all the beans registered in the application.
 *  
 * @author Consulting & Development. Eugenio Castellano - 12-dic-2005
 * 
 */
public class BeanManager {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(BeanManager.class.getName());

	/**
	 * Map of registered beans in the application.
	 */
	private static Map<String,IFinderBean> beans = new HashMap<String,IFinderBean>();

	/*
	 * TODO Registrar los IManagerBean no es la opcion correcta. 
	 */
	/**
	 * TODO
	 * @param instance
	 * @param bean
	 */
	public static void register(String instance, IFinderBean bean) {
		if (!beans.containsKey(bean)) {
			beans.put(instance, bean);
			LOGGER.info("Registered bean " + instance + " - " + bean);
		}
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
			Class pojoClass = Class.forName( pojo );
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
	public static IManagerBean getManagerBean(Class pojoClass) throws ManagerBeanException {
        String key = HibernateUtil.getSessionFactoryName() + "/" + pojoClass ;
        BasicManagerBean managerBean = (BasicManagerBean) beans.get( key );
		if ( managerBean == null ) {
			managerBean = (BasicManagerBean) BeanConfigManager.getBean( pojoClass );
			if ( managerBean != null ) {
				register( key, managerBean );
			}
		}
		return managerBean;
	}
	
}
