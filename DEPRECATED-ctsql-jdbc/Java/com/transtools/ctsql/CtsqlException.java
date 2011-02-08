package com.transtools.ctsql;

import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public class CtsqlException extends Exception {

	private Throwable throwable;
	private int type;
	private String message;
	private Object[] args;
	private int isamError;

	/**
	 *  Description of the Field
	 */
	public final static int WRAPPER = -1;

	/**
	 *  Description of the Field
	 */
	public final static int SERVER_NOT_CONNECTED = 100;
	/**
	 *  Description of the Field
	 */
	public final static int CONNECTION_NOT_OPEN = 101;
	/**
	 *  Description of the Field
	 */
	public final static int STATEMENT_NOT_PREPARED = 200;
	/**
	 *  Description of the Field
	 */
	public final static int STATEMENT_NOT_OPEN = 201;
	/**
	 *  Description of the Field
	 */
	public final static int BAD_ROW_SIZE = 202;
	/**
	 *  Description of the Field
	 */
	public final static int INVALID_COLUMN_TYPE = 203;
	/**
	 *  Description of the Field
	 */
	public final static int ILLEGAL_CONVERSION_TYPE = 204;
	/**
	 *  Description of the Field
	 */
	public final static int RESULSET_TYPE_FOWARDONLY = 205;
	/**
	 *  Description of the Field
	 */
	public final static int RESULSET_CONCUR_READONLY = 206;
	/**
	 *  Description of the Field
	 */
	public final static int ROW_ZERO = 207;
	/*
	 *  Rutinas avanzadas de JDBC 2.0
	 */
	public final static int CANNOT_FIND_TABLE_NAME = 208;
	/**
	 * The communication with the SQL server was interrupted.
	 */
	public final static int COMMUNICATION_INTERRUPTED = -1521;
	/**
	 * Locked by another user
	 */
	public final static int LOCKED_BY_ANOTHER_USER = -1214;

	private static String PROPS_FILE_NAME = "CtsqlException.properties";
	private static Properties props = null;


//	public CtsqlException(int type, String message, Object[] args){
//		this.message = message;
//		this.args = args;
//		this.type = type;
//	}
//
	/**
	 *  Constructor for the CtsqlException object
	 *
	 *@param  type  Description of Parameter
	 */
	public CtsqlException(int type) {
		this.type = type;
	}


	/**
	 *  Constructor for the CtsqlException object
	 *
	 *@param  type  Description of Parameter
	 *@param  args  Description of Parameter
	 */
	public CtsqlException(int type, Object[] args) {
		this.args = args;
		this.type = type;
	}

	/**
	 *  Constructor for the CtsqlException object
	 *
	 *@param  type  Description of Parameter
	 *@param  args  Description of Parameter
	 */
	public CtsqlException(int type, Object[] args, int isamError) {
		this.args = args;
		this.type = type;
		this.isamError = isamError;
	}


	/**
	 *  Constructor for the CtsqlException object
	 *
	 *@param  throwable  Description of Parameter
	 */
	public CtsqlException(Throwable throwable) {
		type = WRAPPER;
		this.throwable = throwable;
	}


	/**
	 *  Gets the InnerThrowable attribute of the CtsqlException object
	 *
	 *@return    The InnerThrowable value
	 */
	public Throwable getInnerThrowable() {
		return throwable;
	}


	/**
	 *  Gets the Message attribute of the CtsqlException object
	 *
	 *@return    The Message value
	 */
	public String getMessage() {
		if (this.type == WRAPPER) {
			return throwable.getMessage();
		}
		String s = formatWithArgs(getMessage(this.type));
		if(s!=null && isamError != 0){
			s += " (iserrno: "+isamError+")"; 
		}
		return s;
	}


	/**
	 *  Gets the Params attribute of the CtsqlException object
	 *
	 *@return    The Params value
	 */
	public Object[] getParams() {
		return args;
	}


	/**
	 *  Gets the Type attribute of the CtsqlException object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return type;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  message  Description of Parameter
	 *@return          Description of the Returned Value
	 */
	private String formatWithArgs(String message) {
		int argCount = 0;
		boolean changed;
		/*
		 *  NEW Paco 21-02-2002. Si no existia el mensaje de error en el fichero
		 *  de propiedades, se daba una leche
		 */
		if (message == null) {
			return null;
		}
		/*
		 *  END NEW Paco 21-02-2002
		 */
		do {
			changed = false;
			for (int i = 0; i < message.length() - 1; i++) {
				if (message.charAt(i) == '%'
						 &&
						message.charAt(i + 1) == 's') {
					String sArg;
					if ((args != null) && (args[argCount] != null)) {
						sArg = args[argCount].toString();
					}
					else {
						sArg = "(null)";
					}
					message = message.substring(0, i) + sArg + message.substring(i + 2);
					argCount++;
					changed = true;
					break;
				}
			}
		} while (changed);
		return message;
	}


	/**
	 *  Gets the Message attribute of the CtsqlException class
	 *
	 *@param  type  Description of Parameter
	 *@return       The Message value
	 */
	public static String getMessage(int type) {
		if (props == null) {
			try {
				props = loadProperties();
			}
			catch (Exception ex) {
				System.out.println("The property file can not be open. ");
				System.out.println("No se ha podido abrir el fichero de propiedades. ");
			}
		}
		return props.getProperty(Integer.toString(type));
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	private static Properties loadProperties() {
		Properties prop = new Properties();
		try {
			prop.load(CtsqlException.class.getResourceAsStream(PROPS_FILE_NAME));
		}
		catch (Throwable t) {
		}
		return prop;
	}
}
