package com.code.aon.hyperview.data;

import java.sql.Connection;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

public class JNDIConnectionProvider implements IConnectionProvider {

	private Properties props;

	public JNDIConnectionProvider(Properties props) {
		this.props = props;
	}

	public Connection getConnection() throws ConnectionProviderException {
		try {
			String dsKey = props
					.getProperty(IConnectionProviderFactory.HYPERVIEW_CONNECTION_DATASOURCE);
			DataSource ds = getDataSource(dsKey);
			return ds.getConnection();
		} catch (Throwable e) {
			throw new ConnectionProviderException(e.getMessage(), e);
		}
	}

	private DataSource getDataSource(String dsName) throws NamingException {
		InitialContext context = new InitialContext();
		Object dsRef = context.lookup(dsName);
		DataSource ds = (DataSource) PortableRemoteObject.narrow(dsRef,
				DataSource.class);
		return ds;
	}

}
