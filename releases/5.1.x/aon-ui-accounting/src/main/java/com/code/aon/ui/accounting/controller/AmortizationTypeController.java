package com.code.aon.ui.accounting.controller;

import com.code.aon.ui.form.BasicController;

public class AmortizationTypeController extends BasicController {

	private boolean yearsEnabled;

	public boolean isYearsEnabled() {
		return yearsEnabled;
	}

	public void setYearsEnabled(boolean yearsEnabled) {
		this.yearsEnabled= yearsEnabled;
	}
}
