package com.code.aon.ui.supplier.controller;

import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the supplier maintenance.
 */
public class SupplierController extends BasicController {

	private String selectedTab;
	
	private boolean showFinanceRegistryBank = true;
	
	private boolean showAccount;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isShowFinanceRegistryBank() {
		return showFinanceRegistryBank;
	}

	public void setShowFinanceRegistryBank(boolean showFinanceRegistryBank) {
		this.showFinanceRegistryBank = showFinanceRegistryBank;
	}

	public boolean isShowAccount() {
		return showAccount;
	}

	public void setShowAccount(boolean showAccount) {
		this.showAccount = showAccount;
	}
	
}