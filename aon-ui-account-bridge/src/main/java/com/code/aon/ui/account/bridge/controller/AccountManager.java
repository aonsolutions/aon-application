package com.code.aon.ui.account.bridge.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.account.controller.AccountController;

public class AccountManager extends AccountController {

	private String selectedTab;
	private String backAction;
	
	public String backAction() {
		return backAction==null?"accountManager_list":backAction;
		
	}
	
	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setBackAction(null);
	}
}
