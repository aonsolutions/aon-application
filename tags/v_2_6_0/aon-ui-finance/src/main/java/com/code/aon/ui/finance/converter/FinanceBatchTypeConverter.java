package com.code.aon.ui.finance.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>FinanceBatchType</code> class.
 */
public class FinanceBatchTypeConverter extends EnumLocaleConverter {

	/**
     * Gets the enum class.
     * 
     * @param c the Component
     * @param ctx the Context
     * 
     * @return the enum class
     */
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return FinanceBatchType.class;
    }
}
