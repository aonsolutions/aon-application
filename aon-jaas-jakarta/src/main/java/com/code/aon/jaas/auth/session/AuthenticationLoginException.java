/**
 * 
 */
package com.code.aon.jaas.auth.session;

import javax.security.auth.login.FailedLoginException;

/**
 * if an attempt is made to login and the user has already exceeded
 * their maxmimum allowed sessions.
 */
public class AuthenticationLoginException extends FailedLoginException {

	private static final long serialVersionUID = 7628276806837564574L;

	private Object arg;

	public AuthenticationLoginException(String msg, Object arg) {
		super(msg);
		this.arg = arg;
	}

	/**
	 * @return the arg
	 */
	public Object getArg() {
		return arg;
	}

	/**
	 * @param arg the arg to set
	 */
	public void setArg(Object arg) {
		this.arg = arg;
	}

}
