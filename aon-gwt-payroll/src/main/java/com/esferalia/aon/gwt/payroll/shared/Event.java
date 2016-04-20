package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Event implements Serializable {
	public static enum Type {
		ERROR, WARNING, INFO, DEBUG
	};

	Event.Type type;
	String message;

	public Event.Type getType() {
		return type;
	}

	public Event setType(Event.Type type) {
		this.type = type;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Event setMessage(String message) {
		this.message = message;
		return this;
	}

}