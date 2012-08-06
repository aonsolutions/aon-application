package com.code.aon.ui.common.hibernate;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;


/**
 * Default implementation of the ISessionFactoryNameProvider.
 */
public class DefaultSessionFactoryNameProvider implements ISessionFactoryNameProvider {

    private static final ISessionFactoryNameProvider SINGLETON = new DefaultSessionFactoryNameProvider();
    
    private DefaultSessionFactoryNameProvider() {
    }
    
    /**
     * Gets the single instance of DefaultSessionFactoryNameProvider.
     * 
     * @return single instance of DefaultSessionFactoryNameProvider
     */
    public static ISessionFactoryNameProvider getInstance() {
    	return SINGLETON;
    }
    
	public String getName( String pojoClass ) {
       	AuthPrincipal principal = AonUtil.getAuthPrincipal();
       	if (principal != null) {
       		String name = principal.getName();
       		int index = name.indexOf('@');
       		return (index > -1)? name.substring(index + 1, name.length()): name;
       	}
        return HibernateUtil.DEFAULT_SESSION_FACTORY_NAME;
	}

}
