package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlException;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlExceptionManager {

//	static private String PROPS_FILE_NAME = "CtsqlExceptionManager.properties";

	private static CtsqlExceptionManager manager = new CtsqlExceptionManager();


//	private Properties props;

	/**
	 *  Constructor for the CtsqlExceptionManager object
	 */
	private CtsqlExceptionManager() {
//		props = loadProperties();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  type                Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 */
	public void throwException(int type) throws CtsqlException {
		throw new CtsqlException(type, null);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  type                Description of Parameter
	 *@param  args                Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 */
	public void throwException(int type, Object[] args) throws CtsqlException {
//		String rawMessage = props.getProperty(Integer.toString(type));
		throw new CtsqlException(type, args);
	}

	public void throwException(int type, Object[] args, int isamError) throws CtsqlException {
		throw new CtsqlException(type, args, isamError);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  th                  Description of Parameter
	 *@exception  CtsqlException  Description of Exception
	 */
	public void throwException(Throwable th) throws CtsqlException {
		throw new CtsqlException(th);
	}


	/**
	 *  Gets the Manager attribute of the CtsqlExceptionManager class
	 *
	 *@return    The Manager value
	 */
	public static CtsqlExceptionManager getManager() {
		return manager;
	}

//	private String buildMessage(int type, Object[] args) {
//		String rawMessage = props.getProperty(Integer.toString(type));
//		return MessageFormat.format(rawMessage, args);
//		return  props.getProperty(Integer.toString(type));
//	}

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
