package com.esferalia.aon.calendar.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;


public enum DayType implements IResourceable, IStringEnum {

	WORKING_DAY("0"),
	HOLIDAY("1"),
	NOT_WORKING_DAY("2"),
	VACATION("3");
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.calendar.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_daytype_";
    
    private String value;
    
    DayType( String value ) {
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