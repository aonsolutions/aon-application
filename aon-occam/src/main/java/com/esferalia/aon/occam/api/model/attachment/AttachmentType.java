package com.esferalia.aon.occam.api.model.attachment;

import java.util.Locale;
import java.util.ResourceBundle;

public enum AttachmentType {

	THUMBNAIL,
    IMAGE,
	DOCUMENT,
	ECOMMERCE_PRODUCT;
	
	/**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_attachment_type_";

   
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale
     *            Required Locale.
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
    	String BASE_NAME = "com.code.aon.common.i18n.enum";
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	public byte value() {
		return (byte) this.ordinal();
	}

}
