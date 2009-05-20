package com.code.aon.common.dao.hibernate;

import java.security.Principal;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;


/**
 * Default implementation of the ISessionFactoryNameProvider.
 */
public class DefaultSessionFactoryNameProvider implements ISessionFactoryNameProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultSessionFactoryNameProvider.class.getName());	
	
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
    
	public String getName() {
        InitialContext ic;
        try {
            ic = new InitialContext();
            Subject subject = (Subject)ic.lookup(DBCPConnectionProvider.SECURITY_SUBJECT);
            if (subject != null && subject.getPrincipals() != null) {
                Principal principal = subject.getPrincipals().iterator().next();
                String name = principal.getName();
                int index = name.indexOf('@');
                return (index > -1)? name.substring(index + 1, name.length()): name;
            }
        } catch (NamingException e) {
            LOGGER.fine(e.getMessage());
        } 
        return HibernateUtil.DEFAULT_SESSION_FACTORY_NAME;
	}

}
