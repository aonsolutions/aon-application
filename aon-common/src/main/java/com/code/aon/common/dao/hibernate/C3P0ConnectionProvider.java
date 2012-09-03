package com.code.aon.common.dao.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.DatasourceConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.jaas.auth.AuthPrincipal;

/**
 * A strategy for obtaining JDBC connections.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27/03/2008
 * 
 */

public class C3P0ConnectionProvider extends org.hibernate.connection.C3P0ConnectionProvider {

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

		AuthPrincipal principal = getAuthPrincipal();
		if ( principal != null ) {
			Properties connectionProperties = ConnectionProvider.getDBProperties(principal);
			LOGGER.info( "Connection properties: {}", connectionProperties );
			props.putAll( connectionProperties );
			super.configure(props);						
		} else {
			dataSourceProvider = new DatasourceConnectionProvider();
			dataSourceProvider.configure(props);			
		}
	}
	
	protected AuthPrincipal getAuthPrincipal() {
		return BasicPrincipal.getAuthPrincipal();
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

}
