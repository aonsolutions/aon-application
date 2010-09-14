package com.code.aon.ui.manager.controller;

import javax.naming.Name;

import com.code.aon.ldap.NameResolver;

public class DomainApplicationUserController extends LdapBasicController {
	
	@Override
	public void updateBaseDN(Name parent) {
		Name baseDN = NameResolver.getName( NameResolver.ou(NameResolver.USERS), parent );
		getLdapDAO().setBaseDN(baseDN);
	}
	
}
