package com.code.aon.ui.converter;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.enumeration.IResourceable;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 11-ene-2005
 * @since 1.0
 *
 */

public class EnumLocaleConverter implements Converter {
	
	/**
     * Devuelve el tipo de medio correspondiente.
     * 
     * @param name
     * @param locale
     * @return El tipo de medio.
     */
    private Object get( Class<IResourceable> enumType, String name, Locale locale) {
    	IResourceable[] enumConstants = enumType.getEnumConstants();
        for( IResourceable element : enumConstants ) {    	
            if (element.getName(locale).equals(name)) {
                return element;
            }
        }
        return null;
    }
    
    /**
     * Return Enumeration Class
     * 
     * @param ctx
     * @param c
     * @return Class
     */
    @SuppressWarnings("unchecked")
	protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return c.getValueBinding("value").getType(ctx);
    }
	
    @SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext ctx, UIComponent c, String text)
            throws ConverterException {
	    if (text == null ) {
	        return text;
	    }
	    Class enumType = getEnumClass( ctx, c );
	    if ( enumType.isArray() ) {
	    	enumType = enumType.getComponentType();
	    }
	    Object object = get( enumType, text, ctx.getViewRoot().getLocale() );
	    return object;
    }

    public String getAsString(FacesContext ctx, UIComponent c, Object value)
            throws ConverterException {
		if ( value == null) {
		    return null;
		} else if ( value instanceof String ) {
			return (String) value;
		}
		IResourceable enumLocalized = (IResourceable) value;
		return enumLocalized.getName( ctx.getViewRoot().getLocale() );
    }

}
