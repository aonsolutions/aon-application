package com.transtools.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public class CtsqlJdbcDriver implements Driver {

	/**
	 *  Constructor for the CtsqlJdbcDriver object
	 *
	 *@exception  SQLException  Description of Exception
	 */
	public CtsqlJdbcDriver() throws SQLException {
		DriverManager.registerDriver(this);
	}


	/**
	 *  Gets the PropertyInfo attribute of the CtsqlJdbcDriver object
	 *
	 *@param  str    Description of Parameter
	 *@param  props  Description of Parameter
	 *@return        The PropertyInfo value
	 */
	public DriverPropertyInfo getPropertyInfo(String str, Properties props)[] {
		return null;
	}


	/**
	 *  Gets the MajorVersion attribute of the CtsqlJdbcDriver object
	 *
	 *@return    The MajorVersion value
	 */
	public int getMajorVersion() {
		return 1;
	}


	/**
	 *  Gets the MinorVersion attribute of the CtsqlJdbcDriver object
	 *
	 *@return    The MinorVersion value
	 */
	public int getMinorVersion() {
		return 3;
	}


	/**
	 *  El url a suminstrar debe tener la estructura
	 *  jdbc:ctsql://host:port/dbname[; <attribute-name>= <attribute-value>]
	 *
	 *@param  url               Description of Parameter
	 *@param  props             Description of Parameter
	 *@return                   Description of the Returned Value
	 *@exception  SQLException  Description of Exception
	 *@jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public synchronized Connection connect(String url, Properties props) throws SQLException {
		Connection connection = null;

		if (acceptsURL(url)) {
			int beginHost = url.indexOf("//");
			int endHost = url.indexOf(":", beginHost);
			int port;
			String dbName = null;
			if ((beginHost != -1) && (endHost != -1)) {
				String host = url.substring(beginHost + 2, endHost);
				int endPort = url.indexOf("/", endHost);
				int endDBName = url.indexOf(";");
				if ((endPort != -1)||(endDBName != -1)) {
					if((endPort == -1) || (endDBName != -1)&&(endDBName < endPort)){
						// no hay dbname pero hay propiedades.
						endPort = endDBName;
					}else if (endDBName != -1) {
						dbName = url.substring(endPort + 1, endDBName);
					}
					port = Integer.parseInt(url.substring(endHost + 1, endPort));
					if (endDBName != -1) {
						addConnectionProperties(url.substring(endDBName + 1), props);
					}
					else {
						dbName = url.substring(endPort + 1);
					}
				}else{
					port = Integer.parseInt(url.substring(endHost + 1));
				}
				connection = new TTConnection(host, port, dbName, props);
			}else{
				throw new SQLException("Invalid URL.");
			}
		}
		return connection;
	}


	/**
	 *@param  str               Description of Parameter
	 *@return                   Description of the Returned Value
	 *@exception  SQLException  Description of Exception
	 *@jdbc.jcosmos             Necesario de implementar para JCosmos.
	 */
	public boolean acceptsURL(String str) throws SQLException {
		boolean result = false;

		int init = str.indexOf(":");
		if (init != -1) {
			int fin = str.indexOf(":", init + 1);
			if (fin != -1) {
				String subprotocol = str.substring(init + 1, fin);
				if (subprotocol.equals("ctsql")) {
					result = true;
				}
			}
		}
		return result;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public boolean jdbcCompliant() {
		return false;
	}


	/**
	 *  Adds a feature to the ConnectionProperties attribute of the CtsqlJdbcDriver
	 *  object
	 *
	 *@param  pairs  The feature to be added to the ConnectionProperties attribute
	 *@param  props  The feature to be added to the ConnectionProperties attribute
	 */
	private void addConnectionProperties(String pairs, Properties props) {
		int start = 0;

		do {
			int equal = pairs.indexOf("=", start);
			if (equal != -1) {
				int end = pairs.indexOf(";", equal);
				if (end == -1) {
					end = pairs.length();
				}

				String key = pairs.substring(start, equal);
				String value = pairs.substring(equal + 1, end);
				props.setProperty(key, value);
				start = end + 1;
			}
		} while (start < pairs.length());
	}

	static {
		try {
			new CtsqlJdbcDriver();
		}
		catch (SQLException e) {
		}
	}
}
