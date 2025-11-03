package com.code.aon.jaas.auth.session;

public class UserNotFoundLoginException extends AuthenticationLoginException {

	
	private static final long serialVersionUID = 771025754032612332L;

	public UserNotFoundLoginException(String msg, Object arg) {
		super(msg, arg);
	}

}
