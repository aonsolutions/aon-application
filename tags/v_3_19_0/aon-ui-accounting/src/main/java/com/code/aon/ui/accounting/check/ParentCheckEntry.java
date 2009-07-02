package com.code.aon.ui.accounting.check;

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

}
