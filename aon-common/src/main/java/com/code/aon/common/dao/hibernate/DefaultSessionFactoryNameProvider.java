package com.code.aon.common.dao.hibernate;



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
        return HibernateUtil.DEFAULT_SESSION_FACTORY_NAME;
	}

}
