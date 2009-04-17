package com.code.aon.db;

import java.io.Serializable;

import org.dom4j.Element;

public interface IEntityVisitor {

	void setEntity( Class<? extends Serializable> entity );
	
	void startDocument();
	
	void endDocument();
	
	void visit( Element element );
	
}
