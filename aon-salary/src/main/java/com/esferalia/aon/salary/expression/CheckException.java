package com.esferalia.aon.salary.expression;

import com.code.aon.AonVersion;

public class CheckException extends ExpressionException {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public CheckException() {
		super();
	}

	public CheckException(String message) {
		super(message);
	}

}
