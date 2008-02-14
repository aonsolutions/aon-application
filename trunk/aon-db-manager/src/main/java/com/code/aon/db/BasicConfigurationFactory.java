package com.code.aon.db;

import org.hibernate.cfg.Configuration;

import com.code.aon.common.dao.hibernate.IConfigurationFactory;

public class BasicConfigurationFactory implements IConfigurationFactory {

	private Configuration configuration; 
	
	public BasicConfigurationFactory( Configuration  configuration ) {
		this.configuration = configuration;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Configuration getConfiguration( String sessionFactoryName ) {
		return configuration;
	}

}
