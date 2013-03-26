package com.code.aon.common;

/**
 * The Interface ILogger.
 */
public interface ILogger {

	/**
	 * Info.
	 * 
	 * @param msg the msg
	 */
	void info(String msg);
	
	/**
	 * Warning.
	 * 
	 * @param msg the msg
	 */
	void warn(String msg);
	
	/**
	 * Error.
	 * 
	 * @param msg the msg
	 */
	void error(String msg);
	
}
