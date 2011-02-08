package com.code.aon.hyperview.data;

import java.util.Properties;

public class JNDIConnectionProviderFactory implements
		IConnectionProviderFactory {

	public IConnectionProvider getConnectionProvider(Properties props) {
		return new JNDIConnectionProvider(props);
	}

	public boolean accept(Properties props) {
		return (props != null && props.containsKey( IConnectionProviderFactory.HYPERVIEW_CONNECTION_DATASOURCE ));
	}

}
