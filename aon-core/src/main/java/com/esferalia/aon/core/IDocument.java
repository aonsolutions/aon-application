package com.esferalia.aon.core;

import java.io.Serializable;

public interface IDocument extends Serializable{

	String getValue();
	void setValue(String value);
	
	boolean validate();
	
}
