package com.code.aon.ui.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

/**
 * @author Consulting & Development. Aimar Tellitu - 23-dic-2008 
 *
 */
public class EmptyStringConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if ( (value == null) || StringUtils.isEmpty(value) ) {
			return null;
		}
		return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

}