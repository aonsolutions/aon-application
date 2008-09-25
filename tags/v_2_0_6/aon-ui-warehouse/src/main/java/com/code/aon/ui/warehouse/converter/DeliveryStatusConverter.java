package com.code.aon.ui.warehouse.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.ui.converter.EnumLocaleConverter;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * A converter for DeliveryStatus 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class DeliveryStatusConverter extends EnumLocaleConverter {

    /* (non-Javadoc)
     * @see com.code.aon.ui.converter.EnumLocaleConverter#getEnumClass(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return DeliveryStatus.class;
    }
	
	
}
