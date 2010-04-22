package com.code.aon.hyperview.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ConnectionProviderFactoryManager {

	private static List<IConnectionProviderFactory> factories;

	static {
		register( new JNDIConnectionProviderFactory() );
		register( new JDBCConnectionProviderFactory() );
		register( new HibernateConnectionProviderFactory() );
	}
	
	private ConnectionProviderFactoryManager() {
				
	}

	public static IConnectionProviderFactory getConnectionProviderFactory(Properties props) throws ConnectionProviderException {
		for (IConnectionProviderFactory factory: factories ) {
			if (factory.accept(props)) {
				return factory;
			}
		}
		throw new ConnectionProviderException("No suitable ConnectionProvider!");
	}
	
	public static void register( IConnectionProviderFactory factory ) {
		if (factories == null) {
			factories = new LinkedList<IConnectionProviderFactory>();
		}
		factories.add( factory );
	}
}
