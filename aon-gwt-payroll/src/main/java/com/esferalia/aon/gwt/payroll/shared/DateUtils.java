package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class DateUtils {
	
	public static Date getFirstDayOfMonth ( ) {
		return getFirstDayOfMonth(new Date());
	}

	public static Date getLastDayOfMonth ( ) {
		return getLastDayOfMonth(new Date());
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
	
}
