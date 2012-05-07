package com.code.aon.accounting.enumeration;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum Quarter implements IResourceable {

	T1(0,2),
	T2(3,5),
	T3(6,8),
	T4(9,11);

	private int startMonth;
	private int dueMonth;
	
	private Quarter(int startMonth,int dueMonth) {
		this.startMonth= startMonth;
		this.dueMonth= dueMonth;
	}
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.accounting.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_quarter_";

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
    
	public int getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public int getDueMonth() {
		return dueMonth;
	}
	public void setDueMonth(int dueMonth) {
		this.dueMonth = dueMonth;
	}

	public Date getStartDate(int year) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, getStartMonth());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, year);
		return c.getTime();
	}
	public Date getDueDate(int year) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, getDueMonth() + 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, year);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}
	
}