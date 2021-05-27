package com.code.aon.common.dao.hibernate;

import org.hibernate.cfg.Configuration;

/**
 * A factory for creating Hibernate Configuration objects.
 */
public interface IConfigurationFactory {

	/**
	 * Gets the Hibernate Configuration object.
	 * 
	 * @param sessionFactoryName 
	 * @return the configuration
	 */
	Configuration getConfiguration( String sessionFactoryName );
	
}
