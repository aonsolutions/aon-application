package com.code.aon.faces.component.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

public class AbbreviateConverter implements Converter {

	private Integer maxWidth;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        String result = null;
        
        // If the incoming value is still a string, play nice
        // and return the value unmodified
        if (value instanceof String) {
        	result = (String) value;
        } else {
        	result = value.toString();
        }
        
    	if ( getMaxWidth() > 0 ) {
    		result = StringUtils.abbreviate( result, getMaxWidth() );
    	}
    	
        return result;
	}

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}
	
}
