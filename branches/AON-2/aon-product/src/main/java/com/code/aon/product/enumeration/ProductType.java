package com.code.aon.product.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration to identify different product types.
 * 
 * @author Consulting & Development. Joseba Urkiri - 26-sep-2006
 * @since 1.0
 * @version 1.0
 *  
 */
public enum ProductType implements IResourceable {
	
	/**
     * Labour
     */
	LABOUR,
	
    /**
     * raw material
     */
    RAW_MATERIAL,

	/**
     * commercial product
     */
	COMMERCIAL_PRODUCT,

    /**
     * external work
     */
	EXTERNAL_WORK,
	
    /**
     * tool
     */
	TOOL;
   
    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.product.i18n.messages";

    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_product_type_";

   
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
