package com.code.aon.product.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different type of a plu product.
 * 
 * @author Consulting & Development. 
 * @since 1.0
 * @version 1.0
 *  
 */
public enum PluProductType implements IResourceable {
	
    /**
     * weigthed.
     */
	WEITHED,

    /**
     * unitary.
     */
    UNITARY;
    
    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.product.i18n.messages";

    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_pluproducttype_";


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