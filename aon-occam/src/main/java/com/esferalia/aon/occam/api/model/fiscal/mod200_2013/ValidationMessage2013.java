package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;

public class ValidationMessage2013 implements Serializable {

	private static final long serialVersionUID = 1L;

	int page;
	String message;
	Mod2002013Key key;
	String expression;

	public ValidationMessage2013() {
	}

	public ValidationMessage2013(int page, Mod2002013Key key, String message,
			String expression) {
		this.message = message;
		this.page = page;
		this.key = key;
		this.expression = expression;
	}

	public ValidationMessage2013(int page, String message) {
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

	public Mod2002013Key getKey() {
		return key;
	}

	public void setKey(Mod2002013Key key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
