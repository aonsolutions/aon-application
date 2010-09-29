package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;

import java.util.List;

import javax.naming.Name;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Application;
import com.code.aon.ui.util.AonUtil;

public class ApplicationController extends LdapBasicController implements IManagerConstants {

	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public Application getApplication() {
		return (Application) getTo();
	}
	
	@SuppressWarnings("unchecked")
	public List<Application> getApplications() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	public void createOrganizationalUnits( String application ) {
		BasicLdap ldap = new BasicLdap();
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
		if (! ldap.exists(rolesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(rolesDN);
		}
		Name profilesDN = NameResolver.getApplicationProfilesDN(application);
		if (! ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(profilesDN);
		}
	}	

	protected String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, APPLICATION_INVALID_NAME, name);
	}

	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, APPLICATION_DUPLICATED_NAME, name);
	}

}