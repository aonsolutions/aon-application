package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;

public interface IAccountingBookRunner {
	
	public boolean accept(BookType type);
	public void setAccountingContext(AccountingBookContext ctx);
	public AccountingBookContext getAccountingContext();
	public void run(OutputStream out) throws AccountingBookException;
	
}
