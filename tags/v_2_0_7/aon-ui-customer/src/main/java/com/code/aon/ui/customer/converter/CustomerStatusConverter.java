package com.code.aon.ui.customer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>CustomerStatus</code> class.
 */
public class CustomerStatusConverter extends EnumLocaleConverter {

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
        return CustomerStatus.class;
    }
}
