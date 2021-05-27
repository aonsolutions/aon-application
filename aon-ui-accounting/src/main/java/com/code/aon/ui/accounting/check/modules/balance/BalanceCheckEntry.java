package com.code.aon.ui.accounting.check.modules.balance;

import com.code.aon.AonVersion;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;


@Deprecated
public class BalanceCheckEntry extends CheckEntryAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
