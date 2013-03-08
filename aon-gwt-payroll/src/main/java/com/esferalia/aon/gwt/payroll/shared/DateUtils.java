package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class DateUtils {
	
	public static int getYear() {
		return getYear(new Date());
	}

	public static int getYear(Date date) {
		return date.getYear() + 1900;
	}

	public static Date getDate (int month, int year ) {
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		date.setDate(1);
		date.setMonth(month);
		date.setYear(year - 1900);
		return date;
	}

	public static Date getFirstDayOfMonth ( ) {
		return getFirstDayOfMonth(new Date());
	}

	public static Date getLastDayOfMonth ( ) {
		return getLastDayOfMonth(new Date());
	}

	public static boolean isAfterOrEquals(Date a, Date b ) {
		if ( a == null ) 
			return true;
		if ( b == null ) 
			return false; // a != null
		return a.after(b) || a.equals(b); 
	}

	public static Date after(Date a, Date b ) {
		if ( a == null ) 
			return a;
		if ( b == null ) 
			return b;
		if ( a.after(b) ) 
			return a ;
		return b;
	}

	public static Date before(Date a, Date b ) {
		if ( a == null ) 
			return b;
		if ( b == null ) 
			return a;
		if ( a.before(b) ) 
			return a ;
		return b;
	}
	
	
	
	public static Date getFirstDayOfMonth ( Date date ) {
		Date firstDayOfMonth = CalendarUtil.copyDate(date);
		CalendarUtil.setToFirstDayOfMonth(firstDayOfMonth);
		return firstDayOfMonth;
	}
	
	public static Date getLastDayOfMonth ( Date date ) {
		Date firstDayOfNextMonth = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(firstDayOfNextMonth, 1);
		CalendarUtil.setToFirstDayOfMonth(firstDayOfNextMonth);
		
		Date firstDayOfMonth = CalendarUtil.copyDate(date);
		CalendarUtil.setToFirstDayOfMonth(firstDayOfMonth);
		
		int monthDays = CalendarUtil.getDaysBetween(
				firstDayOfMonth, 
				firstDayOfNextMonth);
		
		Date lastDayOfMonth = firstDayOfMonth;
		CalendarUtil.addDaysToDate(lastDayOfMonth, monthDays-1);
		
		return lastDayOfMonth;
	}
	
	
    public static Date resetTime(Date date) {
    	Date onlyDate = CalendarUtil.copyDate(date);
    	onlyDate.setHours(0);
    	onlyDate.setMinutes(0);
    	onlyDate.setSeconds(0);
		return onlyDate;
		
    }

	
	public static boolean equals(Date d1, Date d2) {
		if ( d1 == d2 )
			return true;
		if ( d1 == null )
			return false;
		if ( d2 == null )
			return false;
		
		return CalendarUtil.isSameDate(d1, d2);
	}

	
}
