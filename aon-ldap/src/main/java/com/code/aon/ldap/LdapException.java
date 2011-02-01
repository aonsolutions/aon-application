package com.code.aon.ldap;

public class LdapException extends Exception {

	private static final long serialVersionUID = 4327969907077949366L;

	public LdapException() {
		super();
	}

	public LdapException( String message ) {
		super( message );
	}

	public LdapException(String message, Throwable cause) {
		super(message, cause);
	}

	public LdapException(Throwable cause) {
		super(cause);
	}
	
}
