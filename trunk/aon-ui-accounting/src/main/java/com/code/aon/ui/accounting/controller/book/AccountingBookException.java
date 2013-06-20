package com.code.aon.ui.accounting.controller.book;

import com.code.aon.common.AonException;

public class AccountingBookException extends AonException {

	private static final long serialVersionUID = 1L;

	public AccountingBookException() {
		super();
	}

	public AccountingBookException(String message) {
		super(message);
	}

	public AccountingBookException(Throwable cause) {
		super(cause);
	}

	public AccountingBookException(String message, Throwable cause) {
		super(message, cause);
	}

}
