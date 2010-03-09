package com.code.aon.ui.supplier.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>SupplierStatus</code> class.
 */
public class SupplierStatusConverter extends EnumLocaleConverter {

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
        return SupplierStatus.class;
    }
}