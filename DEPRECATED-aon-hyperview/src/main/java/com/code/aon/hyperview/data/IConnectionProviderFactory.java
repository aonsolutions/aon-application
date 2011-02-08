package com.code.aon.hyperview.data;

import java.util.Properties;

public interface IConnectionProviderFactory {
	/**
	 * jdbc driver class
	 */
	public static final String HYPERVIEW_CONNECTION_DRIVER_CLASS = "aon.hyperview.connection.driver_class";

	/**
	 * jdbc URL
	 */
	public static final String HYPERVIEW_CONNECTION_URL = "aon.hyperview.connection.url";

	/**
	 * database user
	 */
	public static final String HYPERVIEW_CONNECTION_USERNAME = "aon.hyperview.connection.username";

	/**
	 * database user password
	 */
	public static final String HYPERVIEW_CONNECTION_PASSWORD = "aon.hyperview.connection.password";

	/**
	 * datasource JNDI name
	 */
	public static final String HYPERVIEW_CONNECTION_DATASOURCE = "aon.hyperview.connection.datasource";

	IConnectionProvider getConnectionProvider(Properties props)
			throws ConnectionProviderException;

	boolean accept(Properties props);
}
