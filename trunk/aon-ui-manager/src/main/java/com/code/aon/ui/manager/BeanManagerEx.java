package com.code.aon.ui.manager;

import static com.code.aon.ui.manager.controller.IManagerConstants.DB_MANAGER_CONTROLLER_NAME;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.code.aon.common.IBeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.manager.controller.DBManagerController;
import com.code.aon.ui.util.AonUtil;

public class BeanManagerEx implements IBeanManager {

    private static final BeanManagerEx SINGLETON = new BeanManagerEx();
	
	/**
	 * Map of registered beans in the application.
	 */
	private Map<Class<?>,ManagerBeanWrapper> beans = new HashMap<Class<?>,ManagerBeanWrapper>();
	
    
    /**
     * Instantiates a new default configuration factory.
     */
    private BeanManagerEx() {
    }
    
    /**
     * Gets the single instance of DefaultConfigurationFactory.
     * 
     * @return single instance of DefaultConfigurationFactory
     */
    public static BeanManagerEx getInstance() {
    	return SINGLETON;
    }
	
	public SessionFactory getSessionFactory() {
		DBManagerController dbManager = (DBManagerController) AonUtil.getRegisteredBean(DB_MANAGER_CONTROLLER_NAME);
		return dbManager.getSessionFactory();
	}
    
	public void update( SessionFactory sessionFactory ) {
		for( ManagerBeanWrapper wrapper : beans.values() ) {
			wrapper.setSessionFactory(sessionFactory);
		}
	}    
    
	@Override
	public IManagerBean getManagerBean(Class<? extends ITransferObject> pojoClass) throws ManagerBeanException {
		ManagerBeanWrapper wrapper = beans.get(pojoClass);
		if ( wrapper == null ) {
			wrapper = new ManagerBeanWrapper(pojoClass, getSessionFactory());
			beans.put(pojoClass, wrapper);
		}
		return wrapper.getManagerBean();
	}

}