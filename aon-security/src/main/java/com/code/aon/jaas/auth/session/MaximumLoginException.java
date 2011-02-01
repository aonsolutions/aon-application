/**
 * 
 */
package com.code.aon.jaas.auth.session;

/**
 * if an attempt is made to login and the user has already exceeded
 * their maxmimum allowed sessions.
 */
public class MaximumLoginException extends AuthenticationLoginException {

	public MaximumLoginException(String msg, Object arg) {
		super(msg, arg);
	}

}
