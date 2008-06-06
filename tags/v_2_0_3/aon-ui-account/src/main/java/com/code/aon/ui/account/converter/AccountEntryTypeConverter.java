package com.code.aon.ui.account.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>AccountEntryType</code> class.
 */
public class AccountEntryTypeConverter extends EnumLocaleConverter {

	/**
     * Gets the enum class.
     * 
     * @param c the Component
     * @param ctx the Context
     * 
     * @return the enum class
     */
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return AccountEntryType.class;
    }
}
