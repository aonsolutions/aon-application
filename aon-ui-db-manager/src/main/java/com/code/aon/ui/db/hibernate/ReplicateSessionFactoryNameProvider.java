package com.code.aon.ui.db.hibernate;

import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;

public class ReplicateSessionFactoryNameProvider implements ISessionFactoryNameProvider {
	
    public static final String SESSION_FACTORY_NAME = "replicate/default";
    
    private static final ISessionFactoryNameProvider SINGLETON = new ReplicateSessionFactoryNameProvider();
    
    private ReplicateSessionFactoryNameProvider() {
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
        return SESSION_FACTORY_NAME;
	}

}
