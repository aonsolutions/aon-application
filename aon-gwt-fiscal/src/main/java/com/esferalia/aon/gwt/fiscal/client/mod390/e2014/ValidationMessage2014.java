package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Mod390DetailKey;

public class ValidationMessage2014 implements Serializable {

	private static final long serialVersionUID = 1L;

	int page;
	String message;
	Mod390DetailKey key;
	String expression;

	public ValidationMessage2014() {
	}

	public ValidationMessage2014(int page, Mod390DetailKey key, String message,
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

	public Mod390DetailKey getKey() {
		return key;
	}

	public void setKey(Mod390DetailKey key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
