package com.esferalia.aon.payroll;

import com.esferalia.aon.core.IDocument;

public class Document implements IDocument {

	String value;
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean validate() {
		return true;
	}

}
