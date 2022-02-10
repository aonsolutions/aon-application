package com.esferalia.aon.gwt.fiscal.shared.invoice;

import java.io.Serializable;

public class ICResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	boolean error;
	String errorMessage;
	
	public ICResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean isError() {
		return error;
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
