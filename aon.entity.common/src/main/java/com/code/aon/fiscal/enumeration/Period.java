package com.code.aon.fiscal.enumeration;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.config.enumeration.Administration;

public enum Period implements IResourceable {
	
	M01(0,0,"01"),
	M02(1,1,"02"),
	M03(2,2,"03"),
	M04(3,3,"04"),
	M05(4,4,"05"),
	M06(5,5,"06"),
	M07(6,6,"07"),
	M08(7,7,"08"),
	M09(8,8,"09"),
	M10(9,9,"10"),
	M11(10,10,"11"),
	M12(11,11,"12"),
	T1(0,2,"T1"),	//12
	T2(3,5,"T2"),	//13
	T3(6,8,"T3"),	//14
	T4(9,11,"T4"),	//15
	YEAR(0,11,"An"); //16

	private int startMonth;
	private int dueMonth;
	private String name;
	
	private Period(int startMonth,int dueMonth,String name) {
		this.startMonth= startMonth;
		this.dueMonth= dueMonth;
		this.name = name;
	}

    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_period_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    public String getName() {
    	return name;
    }
    public String getName(Administration admon) {
    	if (admon == Administration.COMMON_TERRITORY) {
    		if (this == T1) return "1T";
    		if (this == T2) return "2T";
    		if (this == T3) return "3T";
    		if (this == T4) return "4T";
    	}
    	return name;
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

	public static Period getMonthlyPeriod(int month) {
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
	public static Period getQuarterlyPeriod(int month) {
		if (month>=0 && month<3) return T1;
		else if (month>=3 && month<6) return T2;
		else if (month>=6 && month<9) return T3;
		else if (month>=9 && month<12) return T4;
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