package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

public class ValidationMessage2018 implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static enum MessageType {
		ERROR
		,WARNING
		,INFO
	}
	

	int page;
	String message;
	Mod2002018Key key;
	String expression;
	MessageType type;

	public ValidationMessage2018() {
	}

	public ValidationMessage2018(int page, Mod2002018Key key, String message,
			String expression, MessageType type) {
		this.message = message;
		this.page = page;
		this.key = key;
		this.expression = expression;
		this.type = type;
	}
	public ValidationMessage2018(int page, Mod2002018Key key, String message,
			String expression) {
		this(page,key,message,expression, MessageType.ERROR);
	}
	public ValidationMessage2018(int page, Mod2002018Key key, String message) {
		this(page,key,message,null, MessageType.ERROR);
	}

	public ValidationMessage2018(int page, String message) {
		this(page,null,message,null, MessageType.ERROR);
	}
	public ValidationMessage2018(MessageType type,int page, String message) {
		this(page,null,message,null, type);
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

	public Mod2002018Key getKey() {
		return key;
	}

	public void setKey(Mod2002018Key key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	public MessageType getMessageType() {
		return type;
	}
	public void setMessageType(MessageType type) {
		this.type = type;
	}
}
