package com.code.aon.ui.accounting.check;

import com.code.aon.account.Account;

public class ParentCheckEntry extends CheckEntryAdapter {


	@Override
	public void fix() throws AccountingCheckException {
		//TODO Crear cuentas de nivel inferior.
	}

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

	@Override
	public String getMessage() {
		Account account = (Account) getTo();
		return super.getMessage() + "(" + account.getId() + " " + account.getDescription() + ")";
	}

}
