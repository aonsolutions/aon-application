package com.code.aon.finance.enumeration;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum VatPeriod implements IResourceable {
	
	YEAR(0,11),
	T1(0,2),
	T2(3,5),
	T3(6,8),
	T4(9,11),
	M01(0,0),
	M02(1,1),
	M03(2,2),
	M04(3,3),
	M05(4,4),
	M06(5,5),
	M07(6,6),
	M08(7,7),
	M09(8,8),
	M10(9,9),
	M11(10,10),
	M12(11,11);

	private int startMonth;
	private int dueMonth;
	
	private VatPeriod(int startMonth,int dueMonth) {
		this.startMonth= startMonth;
		this.dueMonth= dueMonth;
	}
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_vat_period_";

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

	public static VatPeriod getMonthlyVatPeriod(int month) {
		if (month==0) return M01;
		else if (month==1) return M02;
		else if (month==2) return M03;
		else if (month==3) return M04;
		else if (month==4) return M05;
		else if (month==5) return M06;
		else if (month==6) return M07;
		else if (month==7) return M08;
		else if (month==8) return M09;
		else if (month==9) return M10;
		else if (month==10) return M11;
		else if (month==11) return M12;
		throw new IllegalArgumentException("Invalid month!");
	}
	public static VatPeriod getQuarterlyVatPeriod(int month) {
		if (month>=0 && month<3) return T1;
		else if (month>=3 && month<6) return T2;
		else if (month>=6 && month<9) return T3;
		else if (month>=9 && month<11) return T4;
		throw new IllegalArgumentException("Invalid month!");
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