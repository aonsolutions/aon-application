package com.esferalia.aon.gwt.fiscal.shared.invoice;

import java.io.Serializable;

public class ICResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	boolean error;
	String errorMessage;
	String errorCode;
	
	public ICResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean isError() {
		return error;
	}
	
	public ICResponse setError(boolean error) {
		this.error = error;
		return this;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public ICResponse setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public ICResponse setErrorCode(String errorCode) {
		this.errorCode = errorCode;
		return this;
	}
}
