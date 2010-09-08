package com.code.aon.ui.manager.controller;

import java.util.List;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.manager.Application;
import com.code.aon.ui.form.BasicController;

public class ApplicationController extends BasicController {

	private BasicManagerBean ldapManagerBean;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			LdapDAO dao = new LdapDAO(Application.class);
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
	@SuppressWarnings("unchecked")
	public List<Application> getApplications() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
}