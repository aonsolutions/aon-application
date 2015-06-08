package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;

public class ValidationMessage2014 implements Serializable {

	private static final long serialVersionUID = 1L;

	int page;
	String message;
	Mod2002014Key key;
	String expression;

	public ValidationMessage2014() {
	}

	public ValidationMessage2014(int page, Mod2002014Key key, String message,
			String expression) {
		this.message = message;
		this.page = page;
		this.key = key;
		this.expression = expression;
	}

	public ValidationMessage2014(int page, String message) {
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

	public Mod2002014Key getKey() {
		return key;
	}

	public void setKey(Mod2002014Key key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
