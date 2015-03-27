package com.code.aon.ui.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

public class TrimStringConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if ((value == null) || StringUtils.isBlank(value)) {
			return null;
		}
		return StringUtils.trim(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return StringUtils.trim(value.toString());
	}

}