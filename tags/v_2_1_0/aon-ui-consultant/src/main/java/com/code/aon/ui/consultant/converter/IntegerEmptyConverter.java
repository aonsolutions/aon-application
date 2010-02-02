package com.code.aon.ui.consultant.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * IntegerEmptyConverter used to insert selectOneMenus in search.xhtml pages.
 * 
 */
public class IntegerEmptyConverter implements Converter {
	
	/** EMPRY_STRING. */
	public static final String EMPRY_STRING = "";

	/**
	 * Gets the value as an object.
	 * 
	 * @param value the value
	 * @param component the component
	 * @param context the context
	 * 
	 * @return the value as an object
	 * 
	 * @throws ConverterException the converter exception
	 */
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {
		return (value==null || EMPRY_STRING.equals(value))?null:new Integer(value);
	}

	/**
	 * Gets the value as an string.
	 * 
	 * @param value the value
	 * @param component the component
	 * @param context the context
	 * 
	 * @return the value as an string.
	 * 
	 * @throws ConverterException the converter exception
	 */
	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {
			return (value==null?EMPRY_STRING:value.toString());
	}
}
