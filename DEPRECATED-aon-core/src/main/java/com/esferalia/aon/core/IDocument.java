package com.esferalia.aon.core;

import java.io.Serializable;

public interface IDocument extends Serializable{

	String getTipo();
	void setTipo(String tipo);

	String getPais();
	void setPais(String pais);

	String getValue();
	void setValue(String value);
	
	boolean validate();
	
}
