package com.code.aon.ui.supplier.controller;

import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the supplier maintenance.
 */
public class SupplierController extends BasicController {

	private String selectedTab;
	
	private boolean showFinanceData = true;
	private boolean showAccount = false;
	private boolean showSegment = true;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isShowFinanceData() {
		return showFinanceData;
	}

	public void setShowFinanceData(boolean showFinanceData) {
		this.showFinanceData = showFinanceData;
	}

	public boolean isShowAccount() {
		return showAccount;
	}

	public void setShowAccount(boolean showAccount) {
		this.showAccount = showAccount;
	}
	
	public boolean isShowSegment() {
		return showSegment;
	}

	public void setShowSegment(boolean showSegment) {
		this.showSegment = showSegment;
	}
}