package com.code.ui.gbp.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.ui.converter.EnumLocaleConverter;
import com.code.gbp.enumeration.OfferStatus;

/**
 * Converter used by the <code>ProductStatus</code> class.
 */
public class OfferStatusConverter extends EnumLocaleConverter {

	/**
     * Gets the enum class.
     * 
     * @param c the Component
     * @param ctx the Context
     * 
     * @return the enum class
     */
	@SuppressWarnings("unchecked")
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return OfferStatus.class;
    }
}