package com.code.aon.common.dao.hibernate;

import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.DatasourceConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A strategy for obtaining JDBC connections.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27/03/2008
 * 
 */

public class C3P0ConnectionProvider extends org.hibernate.connection.C3P0ConnectionProvider {

	
	private static final String SECURITY_M_BEAN = "java:comp/env/SecurityMBean";

	/**
	 * Tell the object name to use depending on the aplication server.
	 * Catalina:type=Security,name=AonSecurity -- jboss.admin:service=AonSecurity
	 */
	public static final String CONNECTION_SERVICE = "hibernate.connection.service";

	/**
	 * Tell the JNDI subject name.
	 */
	public static final String SECURITY_SUBJECT = "java:comp/env/security/subject";

	/**
	 * Tell the server method to get the Datasource Metadata.
	 */
	public static final String DSMD_METHOD_NAME = "getDSMDProperties";

	/**
	 * Logger initialization
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(C3P0ConnectionProvider.class);
	
	
	/** Data source. */
	private DatasourceConnectionProvider dataSourceProvider;

	@Override
	public void configure(Properties props) throws HibernateException {
		String jndiName = props.getProperty(Environment.DATASOURCE);
		if (jndiName == null) {
			String msg = "datasource JNDI name was not specified by property " + Environment.DATASOURCE;
			LOGGER.error(msg);
			throw new HibernateException(msg);
		}
		try {
			Properties connectionProperties = getConnectionProperties(props);
			LOGGER.info( "Connection properties: {}", connectionProperties );
			props.putAll( connectionProperties );
			super.configure(props);			
		} catch ( NamingException ne ) {
			dataSourceProvider = new DatasourceConnectionProvider();
			dataSourceProvider.configure(props);
		} catch (Exception e) {
			throw new HibernateException("Could not find connection service", e);
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if ( dataSourceProvider != null ) {
			return dataSourceProvider.getConnection();
		}
		return super.getConnection();
	}
	
	@Override
	public void close() {
		if ( dataSourceProvider != null ) {
			dataSourceProvider.close();
		} else {
			super.close();			
		}
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		if ( dataSourceProvider != null ) {
			dataSourceProvider.closeConnection(conn);
		} else {
			super.closeConnection(conn);			
		}
	}

	@Override
	public boolean supportsAggressiveRelease() {
		if ( dataSourceProvider != null ) {
			return dataSourceProvider.supportsAggressiveRelease();
		} else {
			return super.supportsAggressiveRelease();			
		}
	}

	/**
	 * Gets the connection properties.
	 * 
	 * @param props the props
	 * 
	 * @return the connection properties
	 * @throws NamingException 
	 * @throws MalformedObjectNameException 
	 * @throws ReflectionException 
	 * @throws MBeanException 
	 * @throws InstanceNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	private Properties getConnectionProperties(Properties props) throws NamingException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		InitialContext ic = new InitialContext();
		Subject subject = (Subject) ic.lookup(SECURITY_SUBJECT);
		ObjectName oname = findObjectName( ic, props );
		MBeanServer server = null;
		List servers = MBeanServerFactory.findMBeanServer(null);
		if (servers.size() > 0) {
			if (servers.size() > 1) {
//	Iterates over servers list untill AonMainDeployerMBean is found.
//	TODO Isolate application server. 
				Iterator it = servers.iterator();
				while (it.hasNext()) {
					server = (MBeanServer) it.next();
					try {
						ObjectName jbossname = new ObjectName( "jboss.admin:service=AonMainDeployer" );
						server.getObjectInstance( jbossname );
						break;
					} catch(Exception e) {
					}
				}
			} else {
				server = (MBeanServer) servers.get(0);
			}
		}
		if ( subject == null ) {
			throw new NamingException("");
		}

		Principal principal = subject.getPrincipals().iterator().next();
		Properties dsProperties = 
			(Properties) server.invoke( oname, DSMD_METHOD_NAME, 
										new Object[] { principal },
										new String[] { Principal.class.getName() } );
		return convertProperties(dsProperties);
	}
	
	/**
	 * Convert properties.
	 * 
	 * @param properties the properties
	 * 
	 * @return the properties
	 */
	private  Properties convertProperties( Properties properties ) {
		Properties hibernateProperties = new Properties();
		hibernateProperties.put(Environment.USER, properties.get("username"));
		hibernateProperties.put(Environment.PASS, properties.get("password"));
		hibernateProperties.put(Environment.URL, properties.get("url"));
		hibernateProperties.put(Environment.DRIVER, properties.get("driverClassName"));
		return hibernateProperties;
	}

	/**
	 * Find a proper ObjectName.
	 * 
	 * @param ic
	 * @param props
	 * @throws MalformedObjectNameException
	 */
	private ObjectName findObjectName(InitialContext ic, Properties props) throws MalformedObjectNameException {
		String name = null;
		try {
			name = (String) ic.lookup( SECURITY_M_BEAN );
		} catch (NamingException e) {
			LOGGER.debug( "Not found " + SECURITY_M_BEAN, e );
		}
		if (name == null) {
			name = props.getProperty( CONNECTION_SERVICE );
		}
		return new ObjectName(name);
	}

}
