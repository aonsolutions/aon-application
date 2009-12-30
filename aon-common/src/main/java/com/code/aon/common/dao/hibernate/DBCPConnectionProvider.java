package com.code.aon.common.dao.hibernate;

import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.util.NamingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A strategy for obtaining JDBC connections.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 20/07/2006
 * 
 */

public class DBCPConnectionProvider implements ConnectionProvider {

	
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
	private final static Logger LOGGER = LoggerFactory.getLogger(DBCPConnectionProvider.class);

	/** Data source. */
	private DataSource ds;

	/** User */
	private String user;

	/** Password */
	private String pass;

	/**
	 * Return the {@link DataSource}.
	 * 
	 * @return DataSource.
	 */
	protected DataSource getDataSource() {
		return ds;
	}

	/**
	 * Assign the {@link DataSource}.
	 * 
	 * @param ds
	 */
	protected void setDataSource(DataSource ds) {
		this.ds = ds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.connection.ConnectionProvider#configure(java.util.Properties)
	 */
	public void configure(Properties props) throws HibernateException {
		String jndiName = props.getProperty(Environment.DATASOURCE);
		if (jndiName == null) {
			String msg = "datasource JNDI name was not specified by property " + Environment.DATASOURCE;
			LOGGER.error(msg);
			throw new HibernateException(msg);
		}
		user = props.getProperty(Environment.USER);
		pass = props.getProperty(Environment.PASS);
		try {
			ds = createDataSource(props);
			LOGGER.info("Using datasource: {}", ds.getClass().getName());
		} catch (Exception e) {
			throw new HibernateException("Could not find connection service", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.connection.ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		if (user != null || pass != null) {
			return ds.getConnection(user, pass);
		}
		return ds.getConnection();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.connection.ConnectionProvider#closeConnection(java.sql.Connection)
	 */
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.connection.ConnectionProvider#close()
	 */
	public void close() throws HibernateException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see ConnectionProvider#supportsAggressiveRelease()
	 */
	public boolean supportsAggressiveRelease() {
		return true;
	}

	/**
	 * Create DataSource, there are two ways, if the application has a security
	 * policy then it will create from that policy properties, otherwise it will
	 * use <code>connection.datasource</code> property defined in
	 * <code>hibernate.cfg.xml</code> file.
	 * 
	 * @param props
	 * @return DataSource
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private DataSource createDataSource(Properties props) throws Exception {
		try {
			Properties dsProperties = null;
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
			if ( subject == null )
				throw new NamingException("");

			Principal principal = subject.getPrincipals().iterator().next();
			dsProperties = 
				(Properties) server.invoke( oname, DSMD_METHOD_NAME, 
											new Object[] { principal },
											new String[] { Principal.class.getName() } );
			return BasicDataSourceFactory.createDataSource(dsProperties);
		} catch (NamingException ne) {
			String jndiName = props.getProperty(Environment.DATASOURCE);
			DataSource ds;
			try {
				ds = (DataSource) NamingHelper.getInitialContext(props).lookup(jndiName);
			} catch (NamingException e) {
				throw new HibernateException("Could not find datasource", e);
			}
			if (ds == null) {
				throw new HibernateException("Could not find datasource: " + jndiName);
			}
			return ds;
		}
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
			name = (String) ic.lookup( "java:comp/env/SecurityMBean" );
		} catch (NamingException e) {
			// TODO Auto-generated catch block
		}
		if (name == null)
			name = props.getProperty( CONNECTION_SERVICE );
		return new ObjectName(name);
	}

}
