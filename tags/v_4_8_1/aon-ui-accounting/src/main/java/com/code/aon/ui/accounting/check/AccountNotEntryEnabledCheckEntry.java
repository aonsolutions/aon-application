package com.code.aon.ui.accounting.check;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class AccountNotEntryEnabledCheckEntry extends CheckEntryAdapter {

	private boolean fixed = false;
	private String fixLabel = "Permitir Apuntes";

	@Override
	public void fix() throws AccountingCheckException {
		try {
			Account account = (Account) getTo();
			IManagerBean bean = BeanManager.getManagerBean(Account.class);
			account.setEntryEnabled(true);
			bean.update(account);
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
		return fixed ;
	}

	@Override
	public String getMessage() {
		Account account = (Account) getTo();
		return super.getMessage() + "(" + account.getId() + " " + account.getDescription() + ")";
	}

	@Override
	public String getFixActionLabel() {
		return fixLabel ;
	}
}
