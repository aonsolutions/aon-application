package com.code.aon.ui.accounting.controller.book;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;

public class AccountingBookException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
