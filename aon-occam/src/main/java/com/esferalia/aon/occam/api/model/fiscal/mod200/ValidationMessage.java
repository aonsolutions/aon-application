package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;

public class ValidationMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	int page;
	String message;
	Mod200Key key;
	String expression;

	public ValidationMessage() {
	}

	public ValidationMessage(int page, Mod200Key key, String message,
			String expression) {
		this.message = message;
		this.page = page;
		this.key = key;
		this.expression = expression;
	}

	public ValidationMessage(int page, String message) {
		this(page,null,message,null);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Mod200Key getKey() {
		return key;
	}

	public void setKey(Mod200Key key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
