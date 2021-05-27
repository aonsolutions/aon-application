package com.code.aon.common.dao.hibernate;

/**
 * Represents a replication strategy.
 *
 * @author Consulting & Development. Aimar Tellitu - 23/04/2009
 */
public enum ReplicationMode {

	/**
	 * When a row already exists, choose the latest version.
	 */
	 LATEST_VERSION,
	 
	/**
	 * Ignore replicated entities when a row already exists.
	 */	 
	 IGNORE,

	/**
	 * Overwrite existing rows when a row already exists.
	 */
	 OVERWRITE,
	 
	/**
	 * Throw an exception when a row already exists.
	 */	 
	 EXCEPTION;
	
}
