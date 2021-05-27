package com.code.aon.product.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum AttachmentType implements IResourceable {
	
	THUMBNAIL,
    IMAGE,
	DOCUMENT,
	ECOMMERCE_PRODUCT;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_attachment_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}
