package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum TaskPriority.
 */
public enum DelayTime implements IResourceable {
	
	/** FIVE_MINUTES. */
	FIVE_MINUTES(5),

	/** FIFTEEN_MINUTES. */
	FIFTEEN_MINUTES(15),

	/** THIRTY_MINUTES. */
	THIRTY_MINUTES(30),

	/** FORTY_FIVE_MINUTES. */
	FORTY_FIVE_MINUTES(45),

	/** ONE_HOUR. */
	ONE_HOUR(60),

	/** TWO_HOURS. */
	TWO_HOURS(120),

	/** THREE_HOURS. */
	THREE_HOURS(180),

	/** FOUR_HOURS. */
	FOUR_HOURS(240),

	/** EIGHT_HOURS. */
	EIGHT_HOURS(480),

	/** TWELVE_HOURS. */
	TWELVE_HOURS(720),

	/** ONE_DAY. */
	ONE_DAY(1440),
	
	/** TWO_DAYS. */
	TWO_DAYS(2880),
	
	/** THREE_DAYS. */
	THREE_DAYS(4320),
	
	/** FOUR_DAYS. */
	FOUR_DAYS(5760),
	
	/** ONE_WEEK. */
	ONE_WEEK(10080),
	
	/** TWO_WEEKS. */
	TWO_WEEKS(20160);
    
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.groupware.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_delay_time_";
    
    /** The value. */
    private int value;
    
    /**
     * The Constructor.
     * 
     * @param value the value
     */
    DelayTime(int value) {
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
}