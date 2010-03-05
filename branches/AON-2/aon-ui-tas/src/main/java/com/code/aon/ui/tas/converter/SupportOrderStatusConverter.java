package com.code.aon.ui.tas.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.tas.enumeration.SupportOrderStatus;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * A converter that extends from EnumLocaleConverter for support order status
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class SupportOrderStatusConverter extends EnumLocaleConverter {

    /** 
     * Returns the class of this converter
     * 
     * @return class related
     * @see com.code.aon.ui.converter.EnumLocaleConverter#getEnumClass(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return SupportOrderStatus.class;
    }
	
	
}
