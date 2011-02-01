package com.esferalia.aon.salary.expression;

import com.code.aon.common.AonException;

public class ExpressionException extends AonException {

	private static final long serialVersionUID = 2658939739517432990L;
	
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
