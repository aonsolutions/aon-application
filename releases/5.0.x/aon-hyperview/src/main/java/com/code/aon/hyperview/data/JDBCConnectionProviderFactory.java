package com.code.aon.hyperview.data;

import java.util.Properties;

public class JDBCConnectionProviderFactory implements
		IConnectionProviderFactory {

	public IConnectionProvider getConnectionProvider(Properties props){
		return new JDBCConnectionProvider( props );
	}

	public boolean accept(Properties props) {
		return (props != null && props.containsKey( IConnectionProviderFactory.HYPERVIEW_CONNECTION_DRIVER_CLASS ));
	}

}
