package com.esferalia.aon.salary.expression;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;

public class ExpressionException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public ExpressionException() {
		super();
	}
	public ExpressionException(String message) {
		super(message);
	}
	public ExpressionException(Throwable cause) {
		super(cause);
	}
	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

}
