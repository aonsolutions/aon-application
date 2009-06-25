package com.code.aon.ui.finance.controller;

import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the creditor maintenance.
 */
public class CreditorController extends BasicController {
	
	private String selectedTab;
	
	private boolean showFinanceData = true;
	
	private boolean showAccount;
	
	public boolean isShowFinanceData() {
		return showFinanceData;
	}

	public void setShowFinanceData(boolean showFinanceData) {
		this.showFinanceData = showFinanceData;
	}
        
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowAccount() {
		return showAccount;
	}

	public void setShowAccount(boolean showAccount) {
		this.showAccount = showAccount;
	}
	
}