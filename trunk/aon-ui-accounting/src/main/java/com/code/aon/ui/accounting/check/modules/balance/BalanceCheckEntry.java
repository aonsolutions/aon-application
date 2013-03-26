package com.code.aon.ui.accounting.check.modules.balance;

import com.code.aon.ui.accounting.check.CheckEntryAdapter;


public class BalanceCheckEntry extends CheckEntryAdapter {

	@Override
	public boolean isFixed() {
		return false;
	}


	@Override
	public boolean isFixAvailable() {
		return false;
	}

	@Override
	public String getFixActionLabel() {
		return null;
	}
}
