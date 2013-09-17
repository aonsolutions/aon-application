package com.code.aon.asset.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * The Enum viewer type.
 */
public enum ViewerType implements IResourceable{

	WEEK,
	MONTH
	;
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_viewer_type_";

    
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
   
    
}
