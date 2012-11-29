package com.code.aon.ui.accounting.controller.book;


public class AccountingBookContext {
	
	private AccountingBookController controller;
	private AccountingBook book;

	public AccountingBookContext(AccountingBook book, AccountingBookController controller) {
		this.controller = controller;
		this.book = book;
	}
	
	public AccountingBookController getAccountingBookController() {
		return controller;
	}
	public AccountingBook getAccountingBook() {
		return book;
	}
	
}
