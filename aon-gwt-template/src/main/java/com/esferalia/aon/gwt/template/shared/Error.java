package com.esferalia.aon.gwt.template.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Error implements IsSerializable{

	Boolean error;
	Vector<String> textError;
	public Boolean getError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public Vector<String> getTextError() {
		return textError;
	}
	public void setTextError(Vector<String> textError) {
		this.textError = textError;
	}
	
	
	
}
