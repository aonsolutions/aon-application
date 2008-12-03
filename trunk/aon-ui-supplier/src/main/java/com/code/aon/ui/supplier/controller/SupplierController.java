package com.code.aon.ui.supplier.controller;

import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the supplier maintenance.
 */
public class SupplierController extends BasicController {
	
	private boolean showFinanceRegistryBank;
	
	public boolean isShowFinanceRegistryBank() {
		return showFinanceRegistryBank;
	}

	public void setShowFinanceRegistryBank(boolean showFinanceRegistryBank) {
		this.showFinanceRegistryBank = showFinanceRegistryBank;
	}

}