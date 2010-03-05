package com.esferalia.aon.payroll.core;

public interface IRegistry {
	
	IDocument getDocument();
	void setDocument(IDocument document);
	
	String getFullName();
	void setFullName(String Name);

}
