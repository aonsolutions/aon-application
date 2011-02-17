package com.code.aon.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.bean.BeanConfigManager;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class DefaultBeanManager implements IBeanManager {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultBeanManager.class);

    private static final IBeanManager SINGLETON = new DefaultBeanManager();
	
	/**
	 * Map of registered beans in the application.
	 */
	private Map<String,IFinderBean> beans = new HashMap<String,IFinderBean>();
	
    
    /**
     * Instantiates a new default configuration factory.
     */
    private DefaultBeanManager() {
    }
    
    /**
     * Gets the single instance of DefaultConfigurationFactory.
     * 
     * @return single instance of DefaultConfigurationFactory
     */
    public static IBeanManager getInstance() {
    	return SINGLETON;
    }
	
	
	/**
	 * Return the <code>IManagerBean</code> bound to the POJO Class name.
	 *  
	 * @param instance
	 * @param bean
	 */
	private void register(String instance, IFinderBean bean) {
		if (!beans.containsKey(bean)) {
			beans.put(instance, bean);
			LOGGER.debug("Registered bean {}", instance);
		}
	}

	@Override
	public IManagerBean getManagerBean(Class<?> pojoClass) throws ManagerBeanException {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(pojoClass.getName());
        String key = sessionFactoryName + "/" + pojoClass ;
        BasicManagerBean managerBean = (BasicManagerBean) beans.get( key );
		if ( managerBean == null ) {
			managerBean = (BasicManagerBean) BeanConfigManager.getBean( pojoClass, sessionFactoryName );
			if ( managerBean != null ) {
				register( key, managerBean );
			}
		}
		return managerBean;
	}

}
