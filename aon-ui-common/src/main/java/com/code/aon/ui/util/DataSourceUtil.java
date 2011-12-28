package com.code.aon.ui.util;

import static com.code.aon.common.util.BeanServerUtil.CONNECTION_METHOD_NAME;
import static com.code.aon.common.util.BeanServerUtil.MAIN_DEPLOYER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.management.MBeanServer;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.common.util.BeanServerUtil;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DataSourceUtil {

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
    	String application = getApplicationId(context);
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
