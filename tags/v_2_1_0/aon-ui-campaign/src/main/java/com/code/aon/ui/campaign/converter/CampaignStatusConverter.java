package com.code.aon.ui.campaign.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.campaign.enumeration.CampaignStatus;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>CampaignStatus</code> class.
 */
public class CampaignStatusConverter extends EnumLocaleConverter {

	/**
     * Gets the enum class.
     * 
     * @param c the Component
     * @param ctx the Context
     * 
     * @return the enum class
     */
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return CampaignStatus.class;
    }
}
