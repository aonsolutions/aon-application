package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;

public class AccountingReportModuleOptions extends  ModuleOptions<AccountingReportModuleOptions> {

	private static final long serialVersionUID = -1640449101406136572L;
	
	private boolean dontRunOnOpen;

	public boolean isDontRunOnOpen() {
		return dontRunOnOpen;
	}

	public AccountingReportModuleOptions setDontRunOnOpen(boolean dontRunOnOpen) {
		this.dontRunOnOpen = dontRunOnOpen;
		return this;
	}
	
	

}
