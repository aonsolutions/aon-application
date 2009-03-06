package com.code.aon.desktop.controller;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.desktop.Domain;
import com.code.aon.ui.form.BasicController;

public class DomainController extends BasicController {
	
	private LdapDAO dao;
	
	private BasicManagerBean ldapManagerBean;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			this.dao = new LdapDAO(Domain.class);
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
}