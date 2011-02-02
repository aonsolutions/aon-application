package com.code.aon.ui.accounting.check;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class EmptyAccountCheckEntry extends CheckEntryAdapter {

	private boolean fixed = false;
	private String fixLabel = "Borrar Apunte";

	@Override
	public void fix() throws AccountingCheckException {
		try {
			AccountEntry entry = (AccountEntry) getTo();
			IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
			bean.remove(entry);
			fixed = true;
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(),e);
		}
	}

	@Override
	public boolean isFixAvailable() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return fixed;
	}

	@Override
	public String getMessage() {
		AccountEntry entry = (AccountEntry) getTo();
		return super.getMessage() + "(" + entry.getId() + ")";
	}

	@Override
	public String getFixActionLabel() {
		return fixLabel;
	}

}
