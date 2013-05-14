package com.code.aon.ui.resources.bean;

import java.util.Properties;

import com.code.aon.ui.util.DataSourceUtil;

public class LoginBean {
	
	private Properties dbProperties;
	
	private ResourceResolver resolver;
	
	public LoginBean() {
		this.resolver = new ResourceResolver();
	}

	public void init( String server, String context ) {
		Properties properties = DataSourceUtil.getDBProperties(server, context);
		if ( (properties != null) && (!properties.isEmpty()) ) {
			this.dbProperties = properties;
		}
	}

	public Properties getDbProperties() {
		return dbProperties;
	}

	public ResourceResolver getResolver() {
		return resolver;
	}		

}
