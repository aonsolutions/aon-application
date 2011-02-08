package com.code.aon.hyperview.data;

import java.util.Properties;

public class HibernateConnectionProviderFactory implements
		IConnectionProviderFactory {

	public IConnectionProvider getConnectionProvider(Properties props){
		return new HibernateConnectionProvider();
	}

	public boolean accept(Properties props) {
		return true;
	}

}
