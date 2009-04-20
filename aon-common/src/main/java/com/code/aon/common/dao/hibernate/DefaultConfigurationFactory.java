package com.code.aon.common.dao.hibernate;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DefaultConfigurationFactory implements IConfigurationFactory {

    private static final IConfigurationFactory SINGLETON = new DefaultConfigurationFactory();
    
    /**
     * Instantiates a new default configuration factory.
     */
    protected DefaultConfigurationFactory() {
    }
    
    /**
     * Gets the single instance of DefaultConfigurationFactory.
     * 
     * @return single instance of DefaultConfigurationFactory
     */
    public static IConfigurationFactory getInstance() {
    	return SINGLETON;
    }
    
    /**
     * Complete configuration.
     * 
     * @param configuration the configuration
     */
    protected void completeConfiguration( Configuration configuration ) {    	
    }
    
	public Configuration getConfiguration( String sessionFactoryName ) {
		String configurationResource = System.getProperty(HibernateUtil.HIBERNATE_CONFIGURATION_FILE_PROPERTY);
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		completeConfiguration(configuration);
		if (configurationResource != null) {
			configuration.configure(configurationResource);
		} else {
			configuration.configure();
		}
		return configuration;
	}

}
