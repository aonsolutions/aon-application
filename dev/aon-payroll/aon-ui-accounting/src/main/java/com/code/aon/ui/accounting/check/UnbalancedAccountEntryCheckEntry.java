package com.code.aon.ui.accounting.check;

import com.code.aon.accounting.AccountEntry;

public class UnbalancedAccountEntryCheckEntry extends CheckEntryAdapter {

	@Override
	public void fix() throws AccountingCheckException {
		//TODO Como mucho ir al apunte.
	}

	@Override
	public boolean isFixAvailable() {
		return false;
	}

	@Override
	public boolean isFixed() {
		return false;
	}

	@Override
	public String getMessage() {
		AccountEntry entry = (AccountEntry) getTo();
		return super.getMessage() + "(" + entry.getId() + ")";
	}

	@Override
	public String getFixActionLabel() {
		return null;
	}

}
