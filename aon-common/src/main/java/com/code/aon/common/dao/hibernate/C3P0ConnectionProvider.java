package com.code.aon.common.dao.hibernate;

import static com.code.aon.common.util.BeanServerUtil.CONNECTION_METHOD_NAME;
import static com.code.aon.common.util.BeanServerUtil.MAIN_DEPLOYER;

import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.DatasourceConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.BeanServerUtil;

/**
 * A strategy for obtaining JDBC connections.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27/03/2008
 * 
 */

public class C3P0ConnectionProvider extends org.hibernate.connection.C3P0ConnectionProvider {

	/**
	 * Tell the JNDI subject name.
	 */
	public static final String SECURITY_SUBJECT = "java:comp/env/security/subject";

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
	private Properties getConnectionProperties(Properties props) throws NamingException, InstanceNotFoundException, ReflectionException, MBeanException {
		Principal principal = getPrincipal();
    	Object[] params = getParams(principal);
		String[] sig = { String.class.getName(), String.class.getName() };
    	MBeanServer server = BeanServerUtil.getMBeanServer();		
		return (Properties) server.invoke( MAIN_DEPLOYER, CONNECTION_METHOD_NAME, params, sig );
	}

	private Principal getPrincipal() throws NamingException {
		InitialContext ic = new InitialContext();
		Subject subject = (Subject) ic.lookup(SECURITY_SUBJECT);
		return subject.getPrincipals().iterator().next();
	}
	
	private Object[] getParams( Principal principal ) {
		String name = principal.getName();
		String domain = StringUtils.substringBetween(name, "@", "/");
		String context = StringUtils.substringAfter(name, "/");
		return new Object[] { domain, context };
	}

}
