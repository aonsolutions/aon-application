package com.code.aon.ui.audit.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class StartupConnectionProvider implements ConnectionProvider {
	
	public static final String AON_CONNECTION_PROVIDER = Environment.CONNECTION_PROVIDER + ".aon";
	
	private ConnectionProvider _connectionProvider;
	
	private boolean delegate;
	
	@Override
	public void configure(Properties props) throws HibernateException {
		String value = props.getProperty(AON_CONNECTION_PROVIDER);
		props.remove(AON_CONNECTION_PROVIDER);
		props.setProperty(Environment.CONNECTION_PROVIDER, value);
		this._connectionProvider = ConnectionProviderFactory.newConnectionProvider(props);
	}

	@Override
	public Connection getConnection() throws SQLException {
		if ( this.delegate ) {
			return this._connectionProvider.getConnection();
		} else {
			try {
				ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
				return ci.getMetadataConnection();
			} catch (AonConnectionException e) {
				throw new SQLException(e.getMessage(),e);
			} finally {
				this.delegate = true;
			}
		}
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		this._connectionProvider.closeConnection(conn);
	}

	@Override
	public void close() throws HibernateException {
		this._connectionProvider.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return this._connectionProvider.supportsAggressiveRelease();
	}
	
}
