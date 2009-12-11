package com.code.aon.asset.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;


/**
 * The Enum activity status.
 */
public enum ActivityStatus implements IResourceable, IStringEnum {

	PENDING("0"),
	ACCEPTED("1"),
	REFUSED("2");
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.asset.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_activity_status_";


    private String value;
    ActivityStatus( String value ) {
    	this.value = value;
	}
    
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
   
    @Override
	public String getValue() {
		return value;
	}
    
}
