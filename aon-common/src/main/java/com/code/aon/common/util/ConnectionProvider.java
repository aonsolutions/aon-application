package com.code.aon.common.util;

import static com.code.aon.common.util.BeanServerUtil.LDAP_SERVICE;
import static com.code.aon.common.util.BeanServerUtil.MAIN_DEPLOYER;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.management.MBeanServer;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class.getName());

	/**
	 * Tell the server method to get the Datasource Metadata.
	 */
	public static final String CONNECTION_METHOD_NAME = "getConnectionProperties";
	
	/**
	 * Tell the server method to get the Datasource Metadata. SAR old version.
	 */
	public static final String CONNECTION_METHOD_OLD_NAME = "getDSMDProperties";
	
	private static Properties convertProperties( Properties properties ) {
		Properties hibernateProperties = new Properties();
		hibernateProperties.put(Environment.USER, properties.get("username"));
		hibernateProperties.put(Environment.PASS, properties.get("password"));
		hibernateProperties.put(Environment.URL, properties.get("url"));
		hibernateProperties.put(Environment.DRIVER, properties.get("driverClassName"));
		return hibernateProperties;
	}	

	private static BasicPrincipal getBasicPrincipal( Principal principal ) {
		String name = principal.getName();
		String domain = StringUtils.substringBetween(name, "@", "/");
		String application = StringUtils.substringAfter(name, "/");
		return new BasicPrincipal(domain, application);
	}

    /**
     * Gets the dB properties.
     * 
     * @param domain the domain
     * @param application the context
     * @return the dB properties
     */
    public static Properties getDBProperties( Principal principal ) {
    	LOGGER.info( "Principal: {}", principal );
    	BasicPrincipal bp = getBasicPrincipal(principal);	
    	MBeanServer server = BeanServerUtil.getMBeanServer();
    	Properties properties = getDBProperties(server, bp.getDomain(), bp.getApplication());
    	if ( properties == null ) {
    		properties = getDBProperties(server, principal);
    	}
    	return properties;
    }
    
    private static Properties getDBProperties( MBeanServer server, Principal principal ) {
		Object[] params = { principal };
		String[] sig = { Principal.class.getName() }; 	
		try {
			Properties properties = (Properties) server.invoke( LDAP_SERVICE, CONNECTION_METHOD_OLD_NAME, params, sig );
			return convertProperties(properties);
		} catch (Throwable e) {
			LOGGER.warn( e.getMessage(), e );
		} 
    	return null;
    }    
    
    private static Properties getDBProperties( MBeanServer server, String domain, String application ) {
		Object[] params = { domain, application };
		String[] sig = { String.class.getName(), String.class.getName() }; 	
		try {
			return (Properties) server.invoke( MAIN_DEPLOYER, CONNECTION_METHOD_NAME, params, sig );
		} catch (Throwable e) {
			LOGGER.warn( e.getMessage(), e );
		} 
    	return null;
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
