package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * The Enum Month.
 */
public enum Month implements IResourceable {

	/** JANUARY. */
	JANUARY(0),

	/** FEBRUARY. */
	FEBRUARY(1),

	/** MARCH. */
	MARCH(2),

	/** APRIL. */
	APRIL(3),

	/** MAY. */
	MAY(4),

	/** JUNE. */
	JUNE(5),

	/** JULY. */
	JULY(6),

	/** AUGUST. */
	AUGUST(7),

	/** SEPTEMBER. */
	SEPTEMBER(8),

	/** OCTOBER. */
	OCTOBER(9),

	/** NOVEMBER. */
	NOVEMBER(10),

	/** DECEMBER. */
	DECEMBER(11);
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_month_";
    
    /** The value. */
    private int value;
    
    /**
     * The Constructor.
     * 
     * @param value the value
     */
    Month(int value) {
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
    
    /**
     * Gets the value.
     * 
     * @return the value
     */
    public int getValue(){
    	return this.value;
    }
    
    /**
     * Gets the month by value.
     * 
     * @param value the value
     * 
     * @return the month by value
     */
    public static Month getMonthByValue(int value){
    	for( Month month : Month.values() ) {
    		if ( month.value == value ) {
    			return month;
    		}
    	}
    	return null;
    }
}