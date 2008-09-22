package com.code.aon.ui.payroll.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class CheckboxConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent c, String text) {
		System.out.println( "getAsObject " + text);
		if (text == null) {
			return "N";
		}
		
		return ( text.equals( "true" ) )? "S": "N";
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		System.out.println( "getAsString " + value);
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return ( value.equals("S") )? "true": "false";
		} else if (value instanceof Boolean) {
			return ( (Boolean) value )? "S": "N";
		}

		return "false";
	}

}
