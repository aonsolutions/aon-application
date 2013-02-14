package com.code.aon.common.util;

import static com.code.aon.common.util.BeanServerUtil.MAIN_DEPLOYER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.management.MBeanServer;

import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;

public class ConnectionProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class.getName());

	/**
	 * Tell the server method to get the Datasource Metadata.
	 */
	public static final String CONNECTION_METHOD_NAME = "getConnectionProperties";

    /**
     * Gets the dB properties.
     * 
     * @param domain the domain
     * @param application the context
     * @return the dB properties
     */
    public static Properties getDBProperties( String domain, String application ) {
    	MBeanServer server = BeanServerUtil.getMBeanServer();
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
     * @throws AonException 
     */
    public static Connection getConnection( Properties properties ) throws AonException {
    	Connection connection = null;
    	try {
    		String driver = (String) properties.get(Environment.DRIVER);
			Class.forName( driver );
			String url = properties.getProperty(Environment.URL);		
			String user = properties.getProperty(Environment.USER);
			String password = properties.getProperty(Environment.PASS);			
			connection = DriverManager.getConnection(url, user, password);
    	} catch ( Throwable th ) {
    		String m = "Error creating connection: " + properties;
    		LOGGER.error( m );
			throw new AonException(m,th); 
    	}
    	return connection;
    }
	
}
