package com.code.aon.ui.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * 
 * @author 
 * @since 1.0
 *
 */

public class StringToHtmlConverter implements Converter {
	

    @SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext ctx, UIComponent c, String text)
            throws ConverterException {
    	return text;
    }

    public String getAsString(FacesContext ctx, UIComponent c, Object value)
            throws ConverterException {
		if ( value == null) {
		    return null;
		} else if ( value instanceof String ) {
			return stringToHtml((String)value);
		}
		return value.toString();
    }

	private String stringToHtml(String string) {
		String result = string.replace('\r', ' ').replaceAll("\n", "</br>");
		return result;
	}
    
    
    

}
