package com.code.aon.common.dao.hibernate;

import java.security.Principal;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Default implementation of the ISessionFactoryNameProvider.
 */
public class DefaultSessionFactoryNameProvider implements ISessionFactoryNameProvider {

	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultSessionFactoryNameProvider.class);
	
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
        InitialContext ic;
        try {
            ic = new InitialContext();
            Subject subject = (Subject)ic.lookup(C3P0ConnectionProvider.SECURITY_SUBJECT);
            if (subject != null && subject.getPrincipals() != null) {
                Principal principal = subject.getPrincipals().iterator().next();
                String name = principal.getName();
                int index = name.indexOf('@');
                return (index > -1)? name.substring(index + 1, name.length()): name;
            }
        } catch (NamingException e) {
            LOGGER.debug(e.getMessage());
        } 
        return HibernateUtil.DEFAULT_SESSION_FACTORY_NAME;
	}

}
