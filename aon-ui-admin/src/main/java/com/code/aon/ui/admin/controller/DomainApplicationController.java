package com.code.aon.ui.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.config.DomainApplication;
import com.code.aon.ui.form.BasicController;

public class DomainApplicationController extends BasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationController.class);
	
	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public DomainApplication getDomainApplication() {
		return (DomainApplication) getTo();
	}
	
}
