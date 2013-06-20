package com.code.aon.file.format.core;

import com.code.aon.file.format.Format;
import com.code.aon.file.format.FormatFactoryManager;


/**
 * Defines a component of the disk
 * 
 * @author Consulting & Development. Iñigo GAyarre - 31/01/2007
 * @since 1.0
 *
 */
public class Component implements Filler {

	/**
	 * The name
	 */
	private String name;
	/**
	 * The bean name
	 */
	private String bean;
	/**
	 * The property of the bean
	 */
	private String property;
	/**
	 * The format type
	 */
	private String formatName;
	/**
	 * The pattern
	 */
	private String pattern;
	/**
	 * The value
	 */
	private String value;

	/**
	 * Formats the value with this formatName and the pattern applied
	 * 
	 * @param value the object to format
	 * @return the object formatted
	 * @see com.code.aon.file.format.core.Filler#format(java.lang.Object)
	 */
	public Object format(Object value) {
		Format format = FormatFactoryManager.createFormat(formatName);
		format.applyPattern(pattern);
		String formatStr = format.format(value);

		return formatStr;
	}

	/**
	 * Assigns the name
	 * 
	 * @param name the name of the component
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the name of the component
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assign the bean name of the component
	 * 
	 * @param bean the bean name
	 */
	public void setBean(String bean) {
		this.bean = bean;
	}

	/**
	 * Returns the bean name
	 * 
	 * @return the bean name 
	 */
	public String getBean() {
		return bean;
	}

	/**
	 * Return the property to be called in the bean
	 * 
	 * @return the property of the bean
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Assigns the property to be called in the bean
	 * 
	 * @param property the property of the bean
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Assigns the format name
	 * 
	 * @param formatName the format name
	 */
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	/**
	 * Assigns the pattern
	 * 
	 * @param pattern the pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Assigns the value
	 * 
	 * @param value the value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the value 
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
