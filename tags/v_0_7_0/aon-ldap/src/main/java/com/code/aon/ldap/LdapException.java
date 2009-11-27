package com.code.aon.ldap;

public class LdapException extends Exception {

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
