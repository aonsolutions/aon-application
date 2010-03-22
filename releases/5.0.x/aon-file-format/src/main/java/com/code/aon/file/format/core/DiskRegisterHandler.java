package com.code.aon.file.format.core;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The register handler
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class DiskRegisterHandler extends DefaultHandler {

	/**
	 * The register manager
	 */
	private RegisterManager manager;
	/**
	 * The type
	 */
	private String type;
	/**
	 * The register
	 */
	private Register register = new Register();
	/**
	 * Component of the register
	 */
	private Component component;
	/**
	 * The name property of the component 
	 */
	public static final String NAME = "name";
	/**
	 * The name property of the component 
	 */
	public static final String BEAN = "bean";
	/**
	 * The bean property of the component 
	 */
	public static final String PROPERTY = "property";
	/**
	 * The value property of the component 
	 */
	public static final String VALUE = "value";
	/**
	 * The format property of the component 
	 */
	public static final String FORMAT = "format";
	/**
	 * The pattern property of the component 
	 */
	public static final String PATTERN = "pattern";

	/**
	 * Contructor, assigns the register manager
	 * 
	 * @param manager the register manager
	 */
	public DiskRegisterHandler(RegisterManager manager) {
		this.manager = manager;
	}

	/**
	 * Creates a new component if needed and fills it or updates the registry type
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
														Attributes attributes) {
		if (localName.equals("register")) {
			type = attributes.getValue(NAME);
		}
		else if (localName.equals("field")) {
			component = new Component();
			component.setName(attributes.getValue(NAME));
			component.setBean(attributes.getValue(BEAN));
			component.setProperty(attributes.getValue(PROPERTY));
			component.setValue(attributes.getValue(VALUE));
			component.setFormatName(attributes.getValue(FORMAT));
			component.setPattern(attributes.getValue(PATTERN));
		}
	}

	/**
	 * Adds the registry to the manager or the compnent to the registry
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("register")) {
			manager.put(type, register);
		}
		else if (localName.equals("field")) {
				register.add(component);
		}
	}

}
