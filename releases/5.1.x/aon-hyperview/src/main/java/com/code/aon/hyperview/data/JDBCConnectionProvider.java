package com.code.aon.hyperview.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConnectionProvider implements IConnectionProvider {
	private Properties props;

	public JDBCConnectionProvider(Properties props) {
		this.props = props;
	}

	public Connection getConnection() throws ConnectionProviderException {
		try {
			String driver = props
					.getProperty(IConnectionProviderFactory.HYPERVIEW_CONNECTION_DRIVER_CLASS);
			String url = props
					.getProperty(IConnectionProviderFactory.HYPERVIEW_CONNECTION_URL);
			String user = props
					.getProperty(IConnectionProviderFactory.HYPERVIEW_CONNECTION_USERNAME);
			String passwd = props
					.getProperty(IConnectionProviderFactory.HYPERVIEW_CONNECTION_PASSWORD);
			Class.forName(driver);
			return DriverManager.getConnection(url, user, passwd);
		} catch (Throwable e) {
			throw new ConnectionProviderException(e.getMessage(), e);
		}
	}
}
