package com.code.aon.ui.accounting.check;

import com.code.aon.common.AonException;

public class AccountingCheckException extends AonException {

	private static final long serialVersionUID = -3307890710641195387L;

	public AccountingCheckException() {
		super();
	}

	public AccountingCheckException(String message) {
		super(message);
	}

	public AccountingCheckException(Throwable cause) {
		super(cause);
	}

	public AccountingCheckException(String message, Throwable cause) {
		super(message, cause);
	}

}
