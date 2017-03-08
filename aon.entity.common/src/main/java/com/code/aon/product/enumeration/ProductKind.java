package com.code.aon.product.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ProductKind implements IResourceable {
	
	SALE_PURCHASE,
    PURCHASE,
	SALE;

    private static final String MSG_KEY_PREFIX = "aon_enum_product_kind_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}
