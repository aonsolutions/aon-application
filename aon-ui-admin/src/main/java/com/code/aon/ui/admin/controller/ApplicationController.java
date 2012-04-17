package com.code.aon.ui.admin.controller;

import com.code.aon.config.Application;
import com.code.aon.ui.form.BasicController;

public class ApplicationController extends BasicController {

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

}