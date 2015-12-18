package com.esferalia.aon.gwt.payroll.shared;

public class HttpException extends Exception {
	
	private int status ;
	
	public HttpException(int status ) {
		this.status = status;
	}

	public HttpException(int status,  String message) {
		super(message);
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}




}
