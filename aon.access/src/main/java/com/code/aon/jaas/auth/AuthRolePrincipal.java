package com.code.aon.jaas.auth;

import java.io.Serializable;
import java.security.Principal;

public class AuthRolePrincipal implements Principal, Serializable {
	
	private static final long serialVersionUID = -6983785353044312954L;
	
	private String name;
	
	public AuthRolePrincipal(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}