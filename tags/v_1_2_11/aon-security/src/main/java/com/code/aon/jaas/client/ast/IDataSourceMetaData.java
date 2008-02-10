package com.code.aon.jaas.client.ast;

import java.util.Properties;

/**
 * Interfaz que define las propiedades de la fuente de datos. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 20-jul-2006
 * @since 1.0
 *
 */

public interface IDataSourceMetaData extends INode {

	static final String URL = "url";
	static final String DRIVER_CLASS = "driverClassName";
	static final String USER = "username";
	static final String PASSWORD = "password";

	/**
	 * The DataSource URL.
	 * 
	 * @return
	 */
	String getConnectionURL();

	/**
	 * The DataSource driver class.
	 * 
	 * @return
	 */
	String getDriverClass();

	/**
	 * The DataSource user.
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * The DataSource password.
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * The DataSource properties.
	 * 
	 * @return
	 */
	Properties getProperties();
}
