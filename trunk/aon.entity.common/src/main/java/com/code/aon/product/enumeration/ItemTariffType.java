package com.code.aon.product.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ItemTariffType implements IResourceable {
	
	FIXED,
	PURCHASE_PRICE,
	PRICE,
	SALES_PRICE;
   
    private static final String BASE_NAME = "com.code.aon.product.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_item_tariff_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}
