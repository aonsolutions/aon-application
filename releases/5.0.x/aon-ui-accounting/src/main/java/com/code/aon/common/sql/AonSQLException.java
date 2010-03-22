package com.code.aon.common.sql;

import com.code.aon.common.AonException;

public class AonSQLException extends AonException {

	private static final long serialVersionUID = -3307890710641195387L;

	public AonSQLException() {
		super();
	}

	public AonSQLException(String message) {
		super(message);
	}

	public AonSQLException(Throwable cause) {
		super(cause);
	}

	public AonSQLException(String message, Throwable cause) {
		super(message, cause);
	}

}
