package com.esferalia.aon.gwt.template.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Error implements IsSerializable{

	Boolean error;
	String textError;
	public Boolean getError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public String getTextError() {
		return textError;
	}
	public void setTextError(String textError) {
		this.textError = textError;
	}
	
	
	
}
