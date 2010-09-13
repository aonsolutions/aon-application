package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;

import javax.naming.Name;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainUser;

public class DomainUserController extends LdapBasicController {

	@Override
	public void updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getUsersDN(domain);
		getLdapDAO().setBaseDN(baseDN);
	}

	public void createUserWebmailDefaultData( DomainUser user ) {
		BasicLdap ldap = new BasicLdap();
		String domain = NameResolver.getValue(user.getId(), 2);
		Name addressBookDN = NameResolver.getUserAddressBookDN(domain, user.getUid());
		if (! ldap.exists(addressBookDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(addressBookDN);
		}
		Name accountsDN = NameResolver.getUserAccountsDN(domain, user.getUid());
		if (! ldap.exists(accountsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(accountsDN);
		}
		Name signaturesDN = NameResolver.getUserSignaturesDN(domain, user.getUid());
		if (! ldap.exists(signaturesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(signaturesDN);
		}
	}
}
