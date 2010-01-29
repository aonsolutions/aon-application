package com.code.aon.ui.webmail.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author esferalia Networks. Aimar Tellitu - 25-may-2009 
 *
 */
public class ChromeEditorConverter implements Converter {

    private boolean isChrome( FacesContext context ) {
    	String browser = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
		return StringUtils.contains(browser, "Chrome");
    }
	
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
		String result = value.toString();
		return isChrome(context) ? StringEscapeUtils.escapeXml(result): result;
	}

}