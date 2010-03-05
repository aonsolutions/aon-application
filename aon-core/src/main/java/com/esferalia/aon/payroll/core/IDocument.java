package com.esferalia.aon.payroll.core;

public interface IDocument {

	String getValue();
	void setValue(String value);
	
	boolean validate();
	
}
