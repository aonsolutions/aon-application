package com.code.aon.ui.webmail.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.util.regex.*;

/**
 * Custom converter break the long line of string into several lines based on the
 * string length and maximum length of one line
 */
public class MaxLenghtStringConverter implements Converter {


    public Object getAsObject(FacesContext context, UIComponent component,
                              String valueStr) {
	    if(valueStr.length() > 30)
        return valueStr.substring(0, 30);
        else
        return valueStr;
    }


    public String getAsString(FacesContext context, UIComponent component,
                              Object value) throws ConverterException {
    	String valueStr = (String)value; 
	    if(valueStr.length() > 30)
        return valueStr.substring(0, 30);
        else
        return valueStr;
    }
}