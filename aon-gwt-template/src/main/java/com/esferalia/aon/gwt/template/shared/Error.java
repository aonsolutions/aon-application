package com.esferalia.aon.gwt.template.shared;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Error implements IsSerializable{

	Boolean error;
	Integer line;
	LinkedList<String> textError;
	LinkedList<String> textWarning;
	
	public Boolean getError() {
		return error;
	}
	public Error setError(Boolean error) {
		this.error = error;
		return this;
	}
	
	public Integer getLine() {
		return line;
	}
	
	public Error setLine(Integer line) {
		this.line = line;
		return this;
	}
	
	public LinkedList<String> getTextError() {
		if(textError == null) {
			textError = new LinkedList<String>();
		}
		return textError;
	}
	public Error setTextError(LinkedList<String> textError) {
		this.textError = textError;
		return this;
	}
	
	public Error setTextError(String textError) {
		LinkedList<String> LinkedList = new LinkedList<>();
		LinkedList.add(textError);
		this.textError = LinkedList;
		return this;
	}
	public LinkedList<String> getTextWarning() {
		return textWarning;
	}
	public Error setTextWarning(LinkedList<String> textWarning) {
		this.textWarning = textWarning;
		return this;
	}
}
