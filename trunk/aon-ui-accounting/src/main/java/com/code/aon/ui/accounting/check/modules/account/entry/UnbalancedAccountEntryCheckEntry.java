package com.code.aon.ui.accounting.check.modules.account.entry;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;

public class UnbalancedAccountEntryCheckEntry extends CheckEntryAdapter {

	@Override
	public boolean isFixAvailable() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return false;
	}

	@Override
	public String getMessage() {
		AccountEntry entry = (AccountEntry) getTo();
		return super.getMessage() + " ( Id: " + entry.getId() + " )";
	}

	@Override
	public String getFixActionLabel() {
		return null;
	}

}
