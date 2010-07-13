package com.code.aon.dao.ldap.util;

import java.util.Properties;

import javax.naming.Name;

import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class AonLdapUtil implements IAonObjectClasses, ILdapConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(AonLdapUtil.class.getName());

	public static String getApplicationId( String context ) {
		String application = context;
		if ( application.startsWith("/") ) {
			application = application.substring(1);
		}
		return application;
	}    
	
    public static Properties getDBProperties( String domain, String context ) {
    	Properties properties = new Properties();
    	String application = getApplicationId(context);
    	LOGGER.info( "Domain: " + domain + " Application: " + application );
    	BasicLdap ldap = new BasicLdap();
    	Name domainApplicationDN = NameResolver.getDomainApplicationDN(domain, application);
		Entry domainApplication = ldap.get( domainApplicationDN, DOMAIN_APPLICATION );
		if ( domainApplication != null ) {
			if ( domainApplication.containsKey(DATA_SOURCE_ATTRIBUTE) ) {
				String dataSourceValue = domainApplication.getAsString(DATA_SOURCE_ATTRIBUTE);
				Name dataSourceDN = NameResolver.getName(dataSourceValue);
				Entry dataSource = ldap.get( dataSourceDN, DB_CONNECTION );
				if ( dataSource != null ) {
					String userName = dataSource.getAsString(USER_ID_ATTRIBUTE);
					properties.put(Environment.USER, userName);
					byte[] password = dataSource.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
					properties.put(Environment.PASS, new String(password));
					String url = dataSource.getAsString(LABELED_URI_ATTRIBUTE);
					properties.put(Environment.URL, url);
					String driverClassName = dataSource.getAsString(DRIVER_CLASS_NAME_ATTRIBUTE);
					properties.put(Environment.DRIVER, driverClassName);
				} else {
					LOGGER.error( "DataSource not found: " + dataSourceDN );
				}
			}
		} else {
			LOGGER.error( "Domain Application not found: " + domainApplicationDN );
		}
    	return properties;
    }

}
