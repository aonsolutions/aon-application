package com.code.aon.ui.manager.controller;

import javax.naming.Name;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Role;
import com.code.aon.ui.form.BasicController;

public class RoleController extends BasicController implements IManagerConstants {
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public RoleController() {
		this.ldapDAO = new LdapDAO(Role.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);
	}

	public void setApplication( String application ) {
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
		this.ldapDAO.setBaseDN(rolesDN);
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return this.ldapManagerBean;
	}
	
}
