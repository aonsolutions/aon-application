package com.code.aon.ui.util;

import static com.code.aon.common.util.BeanServerUtil.CONNECTION_METHOD_NAME;
import static com.code.aon.common.util.BeanServerUtil.MAIN_DEPLOYER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.management.MBeanServer;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.BeanServerUtil;
import com.code.aon.ui.common.controller.DomainResolver;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DataSourceUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceUtil.class.getName());

	/**
	 * Gets the dB properties.
	 * 
	 * @return the dB properties
	 */
	public static Properties getDBProperties() {
    	ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
    	HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
    	return DataSourceUtil.getDBProperties(request);
	}
	
	/**
	 * Gets the DB properties.
	 *
	 * @param request the request
	 * @return the DB properties
	 */
	public static Properties getDBProperties( HttpServletRequest request ) {
    	String domain = DomainResolver.getDomain(request);
    	String application = DomainResolver.getApplication(request.getContextPath());
    	return DataSourceUtil.getDBProperties(domain, application);
	}	
	
    /**
     * Gets the dB properties.
     * 
     * @param domain the domain
     * @param application the context
     * @return the dB properties
     */
    public static Properties getDBProperties( String domain, String application ) {
    	LOGGER.info( "Domain: " + domain + " Application: " + application );
    	MBeanServer server = BeanServerUtil.getMBeanServer();
		Object[] params = { domain, application };
		String[] sig = { String.class.getName(), String.class.getName() }; 	
		try {
			return (Properties) server.invoke( MAIN_DEPLOYER, CONNECTION_METHOD_NAME, params, sig );
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
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
