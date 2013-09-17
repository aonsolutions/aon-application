package com.code.aon.warehouse.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PriceType implements IResourceable {
	
	COST_PRICE,
	PURCHASE_PRICE,
	AVERAGE_PURCHASE_PRICE;
    
    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_price_type_";
    
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale
     *            Required Locale.
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}
