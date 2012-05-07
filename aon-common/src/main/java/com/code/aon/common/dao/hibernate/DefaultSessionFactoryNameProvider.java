package com.code.aon.common.dao.hibernate;

import java.security.Principal;

import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;


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
       	AuthPrincipal principal = BasicPrincipal.getAuthPrincipal();
       	if (principal != null) {
       		String name = principal.getName();
       		int index = name.indexOf('@');
       		return (index > -1)? name.substring(index + 1, name.length()): name;
       	}
        return HibernateUtil.DEFAULT_SESSION_FACTORY_NAME;
	}

}
