package com.code.aon.db;

import org.dom4j.Element;

public interface IEntityVisitor {

	void startDocument();
	
	void endDocument();
	
	void visit( Element element );
	
}
