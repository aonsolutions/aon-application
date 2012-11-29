package com.code.aon.ui.accounting.controller.book;


public abstract class AbsAccountingBookRunner implements IAccountingBookRunner {

	private AccountingBookContext ctx;
	
	@Override
	public void setAccountingContext(AccountingBookContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public AccountingBookContext getAccountingContext() {
		return ctx;
	}


}
