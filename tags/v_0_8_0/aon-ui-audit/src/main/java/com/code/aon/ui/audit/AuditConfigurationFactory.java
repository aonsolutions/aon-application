package com.code.aon.ui.audit;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.code.aon.common.dao.hibernate.IConfigurationFactory;

public class AuditConfigurationFactory implements IConfigurationFactory {

	public static final String AUDIT_HIBERNATE_CONFIGURATION_FILE_PROPERTY = "com.code.aon.audit.hibernate.cfg.xml";

	public static final String DEFAULT_AUDIT_HIBERNATE_CONFIGURATION_FILE = "/hibernate.audit.cfg.xml";

	private AnnotationConfiguration configuration;

	private String sessionFactoryName;

	public AuditConfigurationFactory() {
		this.configuration = new AnnotationConfiguration();
		String configurationResource = StringUtils.defaultIfEmpty(System
				.getProperty(AUDIT_HIBERNATE_CONFIGURATION_FILE_PROPERTY),
				DEFAULT_AUDIT_HIBERNATE_CONFIGURATION_FILE);
		this.configuration.configure(configurationResource);
	}

	public String getSessionFactoryName() {
		return sessionFactoryName;
	}

	public void setSessionFactoryName(String sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}

	@Override
	public Configuration getConfiguration(String sessionFactoryName) {
		if (StringUtils.equals(this.sessionFactoryName, sessionFactoryName)) {
			return this.configuration;
		}
		return null;
	}

}
