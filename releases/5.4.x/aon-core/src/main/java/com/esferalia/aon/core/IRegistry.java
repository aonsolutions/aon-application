package com.esferalia.aon.core;

import java.io.Serializable;

public interface IRegistry extends Serializable {
	
	IDocument getDocument();
	void setDocument(IDocument document);
	
	String getFullName();
	void setFullName(String Name);

}
