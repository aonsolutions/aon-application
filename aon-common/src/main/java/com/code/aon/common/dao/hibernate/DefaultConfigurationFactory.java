package com.code.aon.common.dao.hibernate;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.code.aon.common.domain.DomainEntityListener;

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

    /**
     * Update the Configuration after calling configure.
     * 
     * @param configuration the configuration
     */
    protected void updateConfiguration( Configuration configuration ) {    	
    }
    
	public Configuration getConfiguration( String sessionFactoryName ) {
		synchronized (SINGLETON) {
			String configurationResource = System.getProperty(HibernateUtil.HIBERNATE_CONFIGURATION_FILE_PROPERTY);
			AnnotationConfiguration configuration = new AnnotationConfiguration();
			completeConfiguration(configuration);
			if (configurationResource != null) {
				configuration.configure(configurationResource);
			} else {
				configuration.configure();
			}
	        configuration.setListener("pre-insert", new DomainEntityListener());
			Object blobListener = new BlobEntityListener();
	        configuration.setListener("post-commit-insert", blobListener);
	        configuration.setListener("post-commit-update", blobListener);
	        configuration.setListener("post-commit-delete", blobListener);
	        updateConfiguration(configuration);
			return configuration;
		}
	}

}
