package com.code.aon.common;

import org.slf4j.Logger;

/**
 * The Class DefaultLogger.
 */
public class DefaultLogger implements ILogger {

	private Logger logger;
	
	/**
	 * Instantiates a new default logger.
	 * 
	 * @param logger the logger
	 */
	public DefaultLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void error(String msg) {
		this.logger.error(msg);
	}

	@Override
	public void info(String msg) {
		this.logger.info(msg);
	}

	@Override
	public void warn(String msg) {
		this.logger.warn(msg);
	}

}
