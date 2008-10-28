package com.code.aon.common.dao.hibernate;


/**
 * A provider of the name of the SessionFactory.
 */
public interface ISessionFactoryNameProvider {

    /**
     * Retrieve the sessionFactory named instance.
     * 
     * @param pojoClass 
     * @return The SessionFactory. 
     */
	String getName( String pojoClass );
	
}
