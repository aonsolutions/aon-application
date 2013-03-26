package com.esferalia.aon.salary;

public class SalaryBuilderEvent {
	
	private Object source;
	private String message;

	public SalaryBuilderEvent(Object source, String message) {
		this.source = source;
		this.message = message;
	}
	
	public Object getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}
	
}
