package com.esferalia.aon.core;

public interface IRegistry<D extends IDocument> {
	
	D getDocument();
	void setDocument(D document);
	
	String getFullName();
	void setFullName(String Name);

}
