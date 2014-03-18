package com.code.aon.ui.academy.controller;

import com.code.aon.AonVersion;

public class CourseController extends CourseListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	

}