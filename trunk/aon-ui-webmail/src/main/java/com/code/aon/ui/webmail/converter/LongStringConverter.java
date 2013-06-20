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
public class LongStringConverter implements Converter {


    public Object getAsObject(FacesContext context, UIComponent component,
                              String newValue) {

      return newValue;
    }


    public String getAsString(FacesContext context, UIComponent component,
                              Object value) throws ConverterException {
    	try{
		    if(((String)value).length() > 120)
	        return breakLines((String)value, true);
	        else
	        return ((String)value);
    	}catch (Exception e) {
    		return "";
		}
    }


    private String breakLines(String longString, boolean useSpace){
    	String delim = null;
    	if(useSpace)
    		delim = "\\S{120}|\\s";
    	else
    		delim = ".{120}";

        Pattern p = Pattern.compile(delim);
        Matcher m = p.matcher(longString);

		StringBuffer sb = new StringBuffer();
		while (m.find()){
			if(!m.group().equals(" "))
			m.appendReplacement(sb,m.group()+"<br/>");
		}
		m.appendTail(sb);
        return sb.toString();


    }
}