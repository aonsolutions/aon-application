package com.code.aon.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderAdapter;

public class SAXEntityReader extends XMLReaderAdapter {

	private IEntityVisitor visitor;
	
	private Stack<Element> elements;
	
	private Class<? extends Serializable> entity;
	
	private Map<String,Class<? extends Serializable>> entityMap;
	
	private String lastName;
	
	private Class<? extends Serializable> lastEntity;
	
	public SAXEntityReader( IEntityVisitor visitor, Class<? extends Serializable> entity ) throws SAXException {
		this.visitor = visitor;
		setEntity(entity);
	}

	public SAXEntityReader( IEntityVisitor visitor, List<Class<? extends Serializable>> entities ) throws SAXException {
		this.visitor = visitor;
		setEntities(entities);
	}
	
	private void setEntity(Class<? extends Serializable> entity) {
		this.entity = entity;
	}
	
	private void setEntities( List<Class<? extends Serializable>> entities ) {
		this.entityMap = new HashMap<String, Class<? extends Serializable>>();
		for( Class<? extends Serializable> entity : entities ) {
			this.entityMap.put( ClassUtils.getShortClassName(entity), entity);
		}
	}

	@Override
	public void startDocument() throws SAXException {
		elements = new Stack<Element>();
		try {
			this.visitor.startDocument();
		} catch (EntityProcessException e) {
			throw new SAXException( e.getMessage(), e );
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		try {
			this.visitor.endDocument();
		} catch (EntityProcessException e) {
			throw new SAXException( e.getMessage(), e );			
		}
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
	
	private Class<? extends Serializable> resolveEntity( String localName ) {
		Class<? extends Serializable> resolved = null; 
		if ( entity != null ) {
			resolved = entity;
		} else if ( StringUtils.equals(lastName, localName) ) {
			resolved = lastEntity;
		} else {
			resolved = this.entityMap.get(localName);
			lastName = localName;
			lastEntity = resolved;
		}
		return resolved;
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		Element element = elements.pop();
		if ( elements.size() > 1 ) {
			Element parent = elements.peek();
			parent.add( element );
		} else if ( elements.size() == 1 ) {
			try {
				visitor.visit(element, resolveEntity(name));
			} catch (EntityProcessException e) {
				throw new SAXException( e.getMessage(), e );			
			}						
		}
	}
	
}
