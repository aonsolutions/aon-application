package com.code.aon.tas.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration to identify diferent support order status
 * 
 * @author Consulting & Development. Inigo Gayarre - 30-ago-2006
 * @since 1.0
 * @version 1.0
 *  
 */
public enum SupportOrderStatus implements IResourceable {
	
	/**
	 * Pênding
	 */
	PENDING,
	
    /**
     * Activo.
     */
	ACTIVE,

    /**
     * Finished.
     */
    FINISHED,

    /**
     * Unknown.
     */
    UNKNOWN;

    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.tas.i18n.messages";

    /**
     * Message key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_supportorderstatus_";


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