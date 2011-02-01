/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.jaas.client.ast;

/**
 * Domain Access Policy.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 10-mar-2005
 * @since 1.0
 *
 */
public interface IAccessPolicy extends INode {

	static final String[] ACCESS_POLICY = {"Nominal", "Concurrent"};

	/**
	 * Maximum number of users defined for the domain.
	 *  
	 * @return Returns the maxDefinedUsers.
	 */
	int getMaxDefinedUsers();

	/**
	 * Maximun number of users allowed to access application in each domain. If the access
	 * policy is NOMINAL, this number is equals to Maximum Defined Users. 
	 *  
	 * @return Returns the maxAllowedUsers.
	 */
	int getMaxAllowedUsers();

	/**
	 * Maximum number of sessions that a user has for the domain.
	 *  
	 * @return Returns the maxSessions4User.
	 */
	int getMaxSessions4User();

	/**
	 * Return true if the maximum sessions for user has exceeded has to throw an exception,
	 * false otherwise.
	 * 
	 * @return
	 */
	boolean isExceptionThrowableIfMaximumExceeded();
}
