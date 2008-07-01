package com.code.aon.project.enumeration;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TaskPeriod implements IResourceable {

	NONE(0,0),
	DAILY(Calendar.DATE,1),
	WEEKLY(Calendar.DATE,7),
	BI_WEEKLY(Calendar.DATE,14),
	MONTHLY(Calendar.MONTH,1),
	BI_MONTHLY(Calendar.MONTH,2),
	THREE_MONTHLY(Calendar.MONTH,3),
	FOUR_MONTHLY(Calendar.MONTH,4),
	HALF_YEARLY(Calendar.MONTH,12),
	YEARLY(Calendar.YEAR,1);

	private int value;
	
	private int field;
	
	private TaskPeriod(int field, int value) {
		this.field = field;
		this.value = value;
	}
	
	/** Message file base path. */
	private static final String BASE_NAME = "com.code.aon.project.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_task_period_";
	
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

	public int getValue() {
		return value;
	}

	public int getField() {
		return field;
	}
}