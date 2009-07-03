package com.code.aon.ui.registry.controller;

import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the supplier maintenance.
 */
public class RegistryController extends BasicController {

	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

}