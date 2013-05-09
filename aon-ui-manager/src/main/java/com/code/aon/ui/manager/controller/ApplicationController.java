package com.code.aon.ui.manager.controller;

import java.util.List;

import com.code.aon.common.ManagerBeanException;
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

	protected String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, APPLICATION_INVALID_NAME, name);
	}

	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, APPLICATION_DUPLICATED_NAME, name);
	}

}