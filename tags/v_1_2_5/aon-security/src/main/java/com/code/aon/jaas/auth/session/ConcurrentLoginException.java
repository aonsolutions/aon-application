/**
 * 
 */
package com.code.aon.jaas.auth.session;

/**
 * if an attempt is made to login and the user has already exceeded
 * their maxmimum allowed sessions.
 */
public class ConcurrentLoginException extends AuthenticationLoginException {

	public ConcurrentLoginException(String msg, Object arg) {
		super(msg, arg);
	}

}
