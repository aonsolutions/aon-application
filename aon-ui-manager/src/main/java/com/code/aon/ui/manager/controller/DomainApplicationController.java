package com.code.aon.ui.manager.controller;

import javax.naming.Name;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainApplication;
import com.code.aon.ui.form.BasicController;

public class DomainApplicationController extends BasicController {
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public DomainApplicationController() {
		this.ldapDAO = new LdapDAO(DomainApplication.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);			
	}

	public void setDomain( String domain ) {
		Name dasDN = NameResolver.getDomainApplicationsDN(domain);
		this.ldapDAO.setBaseDN(dasDN);
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return this.ldapManagerBean;
	}

}
