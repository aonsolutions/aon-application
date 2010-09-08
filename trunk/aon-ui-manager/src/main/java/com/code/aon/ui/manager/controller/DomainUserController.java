package com.code.aon.ui.manager.controller;

import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.form.BasicController;

public class DomainUserController extends BasicController implements IAonObjectClasses {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserController.class);
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public DomainUserController() {
		this.ldapDAO = new LdapDAO(DomainUser.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);			
	}

	public void setDomain( String domain ) {
		Name dasDN = NameResolver.getUsersDN(domain);
		this.ldapDAO.setBaseDN(dasDN);
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return this.ldapManagerBean;
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
