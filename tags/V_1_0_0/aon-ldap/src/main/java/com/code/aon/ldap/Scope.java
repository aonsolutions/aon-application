package com.code.aon.ldap;

import javax.naming.directory.SearchControls;

public enum Scope {

	OBJECT_SCOPE(SearchControls.OBJECT_SCOPE),
	ONELEVEL_SCOPE(SearchControls.ONELEVEL_SCOPE),
	SUBTREE_SCOPE(SearchControls.SUBTREE_SCOPE);
	
	private int scope;
	
	Scope( int scope ) {
		this.scope = scope;
	}

	public int getScope() {
		return scope;
	}
	
}
