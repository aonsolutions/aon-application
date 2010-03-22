package com.code.aon.ui.db.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.DefaultConfigurationFactory;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.db.hibernate.ReplicateConfigurationPatcher;

public class ReplicateConfigurationFactory extends DefaultConfigurationFactory {

	private static final String TEST_HIBERNATE_PROPERTIES_FILE = "/test.properties";

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicateConfigurationFactory.class.getName());
	
	private IConfigurationFactory defaultFactory;

	private Configuration configuration;
	
	private List<ClassMetadata> entities;

	public ReplicateConfigurationFactory( IConfigurationFactory defaultFactory ) {
		super();
		this.defaultFactory = defaultFactory;		
	}

	public List<ClassMetadata> getEntities() {
		return entities;
	}

	public void setEntities(List<ClassMetadata> entities) {
		this.entities = entities;
	}

	private void addTestProperties(Configuration configuration) {
		Properties properties = new Properties();
		try {
			InputStream in = ReplicateConfigurationFactory.class.getResourceAsStream(TEST_HIBERNATE_PROPERTIES_FILE);
			if ( in != null ) {
				properties.load( in );
				configuration.setProperties(properties);
				in.close();
			}			
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}
	
	@Override
	protected void completeConfiguration(Configuration configuration) {
		ReplicateConfigurationPatcher rcp = new ReplicateConfigurationPatcher(entities);
		rcp.completeConfiguration(configuration);
		addTestProperties(configuration);
	}

	@Override
	public Configuration getConfiguration(String sessionFactoryName) {
		if ( ReplicateSessionFactoryNameProvider.SESSION_FACTORY_NAME.equals(sessionFactoryName) ) {
			if ( configuration == null ) {
				configuration = super.getConfiguration(sessionFactoryName);
			}
			return configuration;
		}
		return this.defaultFactory.getConfiguration(sessionFactoryName);
	}

}
