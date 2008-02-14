package com.code.aon.db;

import java.util.Stack;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderAdapter;

public class SAXEntityReader extends XMLReaderAdapter {

	private String entity;
	
	private IEntityVisitor visitor;
	
	private Stack<Element> elements;
	
	public SAXEntityReader( String entity, IEntityVisitor visitor ) throws SAXException {
		this.entity = entity;
		this.visitor = visitor;
	}

	@Override
	public void startDocument() throws SAXException {
		elements = new Stack<Element>();
		this.visitor.startDocument();
	}
	
	@Override
	public void endDocument() throws SAXException {
		this.visitor.endDocument();
	}

	private Element getElement( String localName, Attributes attributes ) {
		Element element = new DefaultElement( localName );
		for( int i = 0; i < attributes.getLength(); i++ ) {
			Attribute attribute = new DefaultAttribute( attributes.getLocalName(i), attributes.getValue(i) );
			element.add( attribute );
		}
		return element;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		Element element = elements.peek();
		element.addText( new String(ch, start, length) );
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		Element element = getElement(name, attributes);
		elements.add(element);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		Element element = elements.pop();
		if ( entity.equals(name) ) {
			visitor.visit(element);
		}
		if ( elements.size() > 1 ) {
			Element parent = elements.peek();
			parent.add( element );
		}
	}
	
}
