package com.esferalia.aon.gwt.template.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Error implements IsSerializable{

	Boolean error;
	Vector<String> textError;
	Vector<String> textWarning;
	
	public Boolean getError() {
		return error;
	}
	public Error setError(Boolean error) {
		this.error = error;
		return this;
	}
	
	public Vector<String> getTextError() {
		return textError;
	}
	public Error setTextError(Vector<String> textError) {
		this.textError = textError;
		return this;
	}
	
	public Error setTextError(String textError) {
		Vector<String> vector = new Vector<>();
		vector.add(textError);
		this.textError = vector;
		return this;
	}
	public Vector<String> getTextWarning() {
		return textWarning;
	}
	public Error setTextWarning(Vector<String> textWarning) {
		this.textWarning = textWarning;
		return this;
	}
}
