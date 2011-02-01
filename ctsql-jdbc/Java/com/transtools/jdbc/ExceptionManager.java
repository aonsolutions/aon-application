package com.transtools.jdbc;

import com.transtools.ctsql.CtsqlException;

import java.sql.SQLException;
import java.text.MessageFormat;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public class ExceptionManager {

//	static private String PROPS_FILE_NAME = "ExceptionManager.properties";

	private static ExceptionManager manager = new ExceptionManager();


//	private Properties props;

	/**
	 *  Constructor for the ExceptionManager object
	 */
	private ExceptionManager() {
//		props = loadProperties();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  type              Description of Parameter
	 *@param  message           Description of Parameter
	 *@exception  SQLException  Description of Exception
	 */
	public void throwException(int type, String message) throws SQLException {
		throwException(type, message, null);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  exception         Description of Parameter
	 *@exception  SQLException  Description of Exception
	 */
	public void throwException(CtsqlException exception) throws SQLException {
		throwException(exception.getType(), exception.getMessage(), exception.getParams());
	}


	/**
	 *  Description of the Method
	 *
	 *@param  type              Description of Parameter
	 *@param  message           Description of Parameter
	 *@param  args              Description of Parameter
	 *@exception  SQLException  Description of Exception
	 */
	public void throwException(int type, String message, Object[] args) throws SQLException {
// New Eva 10-4-2001
		StringBuffer stb = null;
		if(message == null){
			throw new SQLException("Unknown message error <"+type+">");
		}
		if (message != null) {
			stb = new StringBuffer(message);
		}
		String old = "%s";
		int index = message.indexOf(old);
		while (index != -1) {
			stb = stb.replace(index, index + old.length(), (String) args[0]);
			index = message.indexOf(old, index + 1);
		}
		//Tomar tabla inicial de conversión de SQLState de c:\Develop\cosmos\CLODBC\DLL\Odbc_err.hh
		throw new SQLException(buildMessage(stb.toString(), args), null, type);
//		throw new SQLException(buildMessage(message, args),x,type);
// END New Eva 10-4-2001
	}


	/**
	 *  Description of the Method
	 *
	 *@param  rawMessage  Description of Parameter
	 *@param  args        Description of Parameter
	 *@return             Description of the Returned Value
	 */
	private String buildMessage(String rawMessage, Object[] args) {
		if (args == null) {
			return rawMessage;
		}
		else {
			return MessageFormat.format(rawMessage, args);
		}
	}


	/**
	 *  Gets the Manager attribute of the ExceptionManager class
	 *
	 *@return    The Manager value
	 */
	public static ExceptionManager getManager() {
		return manager;
	}

//	public void throwException(int type, Object[] args) throws SQLException {
//		throw new SQLException(buildMessage(type, args));
//	}
//	private String buildMessage(int type, Object[] args) {
//		String rawMessage = props.getProperty(Integer.toString(type));
//		return MessageFormat.format(rawMessage, args);
//	}
//
//	private static Properties loadProperties() {
//		Properties prop = new Properties();
//		try {
//			prop.load(CtsqlChannel.class.getResourceAsStream(PROPS_FILE_NAME));
//		} catch (Throwable t) {}
//		return prop;
//	}
//
//	La base de datos ???? no se pudo abrid a las hh:mm
//
//	-1452=Multibase catalog malformed. Error in {0} server.
//	# This error code has two arguments available:
//	# 0. type: String. Description: Server name;
//	# 1. time: Date: Description: The time when the error occurred.
//	-1452=Catálogo Multibase deteriorado. Error en el servidor {0}.

// ExceptionManager.getManager().throwException(ExceptionManager.SERVER_NOT_FOUND, ...);
}

