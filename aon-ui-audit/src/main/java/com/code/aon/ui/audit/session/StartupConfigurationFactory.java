package com.code.aon.ui.audit.session;

import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.code.aon.common.dao.hibernate.DefaultConfigurationFactory;

public class StartupConfigurationFactory extends DefaultConfigurationFactory {

	private String newConnectionProvider;
	
	public StartupConfigurationFactory(String newConnectionProvider) {
		this.newConnectionProvider = newConnectionProvider;
	}

	@Override
    protected void updateConfiguration( Configuration configuration ) {
		Properties properties = configuration.getProperties();
		String value = properties.getProperty(Environment.CONNECTION_PROVIDER);
		properties.setProperty(StartupConnectionProvider.AON_CONNECTION_PROVIDER, value);
		properties.setProperty(Environment.CONNECTION_PROVIDER, newConnectionProvider);
    }
	
}
