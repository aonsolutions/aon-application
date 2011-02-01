package com.code.aon.ui.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DataSourceUtil implements IAonObjectClasses, ILdapConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceUtil.class.getName());

	/**
	 * Gets the application id.
	 * 
	 * @param context the context
	 * @return the application id
	 */
	public static String getApplicationId( String context ) {
		return StringUtils.removeStart(context, "/");
	}
	
	/**
	 * Gets the dB properties.
	 * 
	 * @return the dB properties
	 */
	public static Properties getDBProperties() {
    	DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DomainResolver.CONTROLLER_NAME);
    	String domain = resolver.getDomain();
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	String context = ctx.getExternalContext().getRequestContextPath();
    	return DataSourceUtil.getDBProperties(domain, context);
	}
	
	/**
	 * Gets the DB properties.
	 *
	 * @param request the request
	 * @return the DB properties
	 */
	public static Properties getDBProperties( HttpServletRequest request ) {
    	String domain = DomainResolver.getDomain(request);
    	String context = request.getContextPath();
    	return DataSourceUtil.getDBProperties(domain, context);
	}	
	
    /**
     * Gets the dB properties.
     * 
     * @param domain the domain
     * @param context the context
     * @return the dB properties
     */
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

    /**
     * Gets the connection.
     * 
     * @param properties the properties
     * @return the connection
     */
    public static Connection getConnection( Properties properties ) {
    	Connection connection = null;
    	try {
    		String driver = (String) properties.get(Environment.DRIVER);
			Class.forName( driver );
			String url = properties.getProperty(Environment.URL);		
			String user = properties.getProperty(Environment.USER);
			String password = properties.getProperty(Environment.PASS);			
			connection = DriverManager.getConnection(url, user, password);
    	} catch ( Throwable th ) {
    		LOGGER.error( "Error creating connection: " + properties );
    	}
    	return connection;
    }
    
}
