package com.code.aon.ui.accounting.check.modules.account;

import com.code.aon.ui.accounting.check.CheckEntryAdapter;

public class ParentCheckEntry extends CheckEntryAdapter {


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
