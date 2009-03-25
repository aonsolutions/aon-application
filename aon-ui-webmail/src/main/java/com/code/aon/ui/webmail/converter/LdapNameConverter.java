package com.code.aon.ui.webmail.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.ldap.NameResolver;

/**
 * Custom converter break the long line of string into several lines based on
 * the string length and maximum length of one line
 */
public class LdapNameConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String newValue) {
		if (newValue == null) {
			return null;
		}
		return NameResolver.getName(newValue);
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

}