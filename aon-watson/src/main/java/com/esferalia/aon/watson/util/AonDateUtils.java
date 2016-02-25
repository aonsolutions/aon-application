package com.esferalia.aon.watson.util;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;

import java.util.Calendar;
import java.util.Date;
import java.util.function.LongFunction;

public class AonDateUtils {

	public static Date max(Date a, Date b) {
		return compare(a, b) > 0 ? a : b;
	}

	public static Date min(Date a, Date b) {
		return compare(a, b) < 0 ? a : b;
	}

	public static int compare(Date a, Date b) {
		if (a == null) {
			return b == null ? 0 : 1;
		}
		return b == null ? -1 : a.compareTo(b);
	}
	
	public static <T extends Date> T getFirstDateOfWeek(T date,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
			calendar.set(Calendar.DAY_OF_YEAR, --day);
		return longFunction.apply(calendar.getTimeInMillis());
	}
	
	public static Date getFirstDayOfWeek(Date date) {
		return getFirstDateOfWeek(date, Date::new);
	}
	
	public static Date getFirstDayOfWeek(java.sql.Date date) {
		return getFirstDateOfWeek(date, Date::new);
	}
	
	public static <T extends Date> T getLastDateOfWeek(T date,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
			calendar.set(Calendar.DAY_OF_YEAR, ++day);
		return longFunction.apply(calendar.getTimeInMillis());
	}
	
	public static Date getLastDayOfWeek(Date date) {
		return getLastDateOfWeek(date, Date::new);
	}
	
	public static Date getLastDayOfWeek(java.sql.Date date) {
		return getLastDateOfWeek(date, Date::new);
	}
	
	public static <T extends Date> T getFirstDayOfYear(T date,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(DAY_OF_YEAR, 1);
		return longFunction.apply(calendar.getTimeInMillis());
	}

	public static Date getFirstDayOfYear(Date date) {
		return getFirstDayOfYear(date, Date::new);
	}

	public static java.sql.Date getFirstDayOfYear(java.sql.Date date) {
		return getFirstDayOfYear(date, java.sql.Date::new);
	}

	public static <T extends Date> T getLastDayOfYear(T date,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(DAY_OF_YEAR, calendar.getActualMaximum(DAY_OF_YEAR));
		return longFunction.apply(calendar.getTimeInMillis());
	}

	public static Date getLastDayOfYear(Date date) {
		return getLastDayOfYear(date, Date::new);
	}

	public static java.sql.Date getLastDayOfYear(java.sql.Date date) {
		return getLastDayOfYear(date, java.sql.Date::new);
	}

	public static <T extends Date> T getFirstDayOfMonth(T date,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(DAY_OF_MONTH, 1);
		return longFunction.apply(calendar.getTimeInMillis());
	}

	public static Date getFirstDayOfMonth(Date date) {
		return getFirstDayOfMonth(date, Date::new);
	}

	public static java.sql.Date getFirstDayOfMonth(java.sql.Date date) {
		return getFirstDayOfMonth(date, java.sql.Date::new);
	}

	public static <T extends Date> T getLastDayOfMonth(T date,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
		return longFunction.apply(calendar.getTimeInMillis());
	}

	public static Date getLastDayOfMonth(Date date) {
		return getLastDayOfMonth(date, Date::new);
	}

	public static java.sql.Date getLastDayOfMonth(java.sql.Date date) {
		return getLastDayOfMonth(date, java.sql.Date::new);
	}

	public static <T extends Date> int get(T date, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(field);
	}

	public static Date addDays(Date date, int value) {
		return add(date, DAY_OF_MONTH, value);
	}

	public static <T extends Date> T add(T date, int field, int value,
			LongFunction<T> longFunction) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, value);
		return longFunction.apply(calendar.getTimeInMillis());
	}

	public static Date add(Date date, int field, int value) {
		return add(date, field, value, Date::new);
	}

	public static java.sql.Date add(java.sql.Date date, int field, int value) {
		return add(date, field, value, java.sql.Date::new);
	}

	public static <T extends Date> int getMax(T date, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(field);
	}

}
