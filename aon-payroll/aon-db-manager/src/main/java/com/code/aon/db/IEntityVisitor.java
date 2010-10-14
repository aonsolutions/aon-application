package com.code.aon.db;

import java.io.Serializable;

import org.dom4j.Element;

public interface IEntityVisitor {

	void startDocument() throws EntityProcessException;
	
	void endDocument() throws EntityProcessException;
	
	void visit( Element element, Class<? extends Serializable> entity ) throws EntityProcessException;
	
}
