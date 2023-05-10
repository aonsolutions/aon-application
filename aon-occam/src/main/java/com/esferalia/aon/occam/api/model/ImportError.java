package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

public class ImportError implements Serializable{

	private static final long serialVersionUID = 1L;
	Boolean error;
	Integer line;
	LinkedList<String> textError;
	LinkedList<String> textWarning;
	
	public ImportError() {
		super();
	}
	
	public Boolean getError() {
		return error;
	}
	public ImportError setError(Boolean error) {
		this.error = error;
		return this;
	}
	
	public Integer getLine() {
		return line;
	}
	
	public ImportError setLine(Integer line) {
		this.line = line;
		return this;
	}
	
	public LinkedList<String> getTextError() {
		if(textError == null) {
			textError = new LinkedList<String>();
		}
		return textError;
	}
	public ImportError setTextError(LinkedList<String> textError) {
		this.textError = textError;
		return this;
	}
	
	public ImportError setTextError(String textError) {
		LinkedList<String> LinkedList = new LinkedList<>();
		LinkedList.add(textError);
		this.textError = LinkedList;
		return this;
	}
	public LinkedList<String> getTextWarning() {
		return textWarning;
	}
	public ImportError setTextWarning(LinkedList<String> textWarning) {
		this.textWarning = textWarning;
		return this;
	}
	public ImportError setTextWarning(String textWarning) {
		if(getTextWarning() == null) {
			this.textWarning = new LinkedList<>();
		}
		getTextWarning().add(textWarning);
		return this;
	}
}
