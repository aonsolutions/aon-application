package com.code.aon.ui.commercial.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>OfferStatus</code> class
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
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return OfferStatus.class;
    }
}
