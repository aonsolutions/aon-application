package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DelayTime implements IResourceable {
	
	FIVE_MINUTES(5),
	FIFTEEN_MINUTES(15),
	THIRTY_MINUTES(30),
	FORTY_FIVE_MINUTES(45),
	ONE_HOUR(60),
	TWO_HOURS(120),
	THREE_HOURS(180),
	FOUR_HOURS(240),
	EIGHT_HOURS(480),
	TWELVE_HOURS(720),
	ONE_DAY(1440),
	TWO_DAYS(2880),
	THREE_DAYS(4320),
	FOUR_DAYS(5760),
	ONE_WEEK(10080),
	TWO_WEEKS(20160);

	private static final String BASE_NAME = "com.code.aon.groupware.i18n.messages";
	private static final String MSG_KEY_PREFIX = "aon_enum_delay_time_";
    
    private int value;
    
    private DelayTime(int value) {
		this.value = value;
	}

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    public int getValue(){
    	return this.value;
    }
}