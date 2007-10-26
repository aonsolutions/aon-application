package com.code.aon.common.dao.hibernate;


/**
 * A provider of the name of the SessionFactory.
 */
public interface ISessionFactoryNameProvider {

    /**
     * Retrieve the sessionFactory named instance.
     * 
     * @return The SessionFactory. 
     */
	String getName();
	
}
