package com.code.aon.ui.accounting.check;

import com.code.aon.common.AonException;

public class AonCheckException extends AonException {

	private static final long serialVersionUID = -3307890710641195387L;

	public AonCheckException() {
		super();
	}

	public AonCheckException(String message) {
		super(message);
	}

	public AonCheckException(Throwable cause) {
		super(cause);
	}

	public AonCheckException(String message, Throwable cause) {
		super(message, cause);
	}

}
