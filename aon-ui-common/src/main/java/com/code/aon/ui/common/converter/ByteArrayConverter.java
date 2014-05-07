package com.code.aon.ui.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class ByteArrayConverter implements Converter {
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if ( value != null ) {
			return new String( (byte[]) value );	
		}
		return null;
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if ( value != null ) {
			return value.getBytes();	
		}
		return null;
	}	

}
