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

	public static Date getDate(int month, int year) {
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		date.setDate(1);
		date.setMonth(month);
		date.setYear(year - 1900);
		return date;
	}

	public static Date addMonths2Date(Date date, int months) {
		CalendarUtil.addMonthsToDate(date, months);
		return date;
	}

	public static Date getFirstDayOfMonth() {
		return getFirstDayOfMonth(new Date());
	}

	public static Date getLastDayOfMonth() {
		return getLastDayOfMonth(new Date());
	}

	public static int getMonths(Date a, Date b) {
		return (a.getYear() - b.getYear()) * 12 + (a.getMonth() - b.getMonth());
	}

	public static boolean isAfterOrEquals(Date a, Date b) {
		if (a == null)
			return true;
		if (b == null)
			return false; // a != null
		return a.after(b) || a.equals(b);
	}

	public static Date after(Date a, Date b) {
		if (a == null)
			return a;
		if (b == null)
			return b;
		if (a.after(b))
			return a;
		return b;
	}

	public static Date before(Date a, Date b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		if (a.before(b))
			return a;
		return b;
	}

	public static Date getPrevDay(Date date) {
		Date nextDay = CalendarUtil.copyDate(date);
		CalendarUtil.addDaysToDate(nextDay, -1);
		return nextDay;
	}

	public static Date getNextDay(Date date) {
		Date nextDay = CalendarUtil.copyDate(date);
		CalendarUtil.addDaysToDate(nextDay, 1);
		return nextDay;
	}

	public static Date getFirstDayOfWorkWeek(Date date) {
		Date firstDayOfWeek = resetTime(date);
		int dayOfWeek = firstDayOfWeek.getDay();
		// Remember : 0 for Sunday and 6 for Saturday
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}
		CalendarUtil.addDaysToDate(firstDayOfWeek, 1 - dayOfWeek);
		return firstDayOfWeek;
	}

	public static Date getLastDayOfWorkWeek(Date date) {
		Date lastDayOfWeek = resetTime(date);
		int dayOfWeek = lastDayOfWeek.getDay();
		// Remember : 0 for Sunday and 6 for Saturday
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}
		CalendarUtil.addDaysToDate(lastDayOfWeek, 7 - dayOfWeek);
		return lastDayOfWeek;
	}

	public static Date getFirstDayOfYear(Date date) {
		Date firstDayOfYear = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(firstDayOfYear, -1 * date.getMonth());
		CalendarUtil.setToFirstDayOfMonth(firstDayOfYear);
		return firstDayOfYear;
	}

	public static Date getFirstDayOfMonth(Date date) {
		Date firstDayOfMonth = CalendarUtil.copyDate(date);
		CalendarUtil.setToFirstDayOfMonth(firstDayOfMonth);
		return firstDayOfMonth;
	}

	public static Date getLastDayOfMonth(Date date) {
		Date firstDayOfNextMonth = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(firstDayOfNextMonth, 1);
		CalendarUtil.setToFirstDayOfMonth(firstDayOfNextMonth);

		Date firstDayOfMonth = CalendarUtil.copyDate(date);
		CalendarUtil.setToFirstDayOfMonth(firstDayOfMonth);

		int monthDays = CalendarUtil.getDaysBetween(firstDayOfMonth,
				firstDayOfNextMonth);

		Date lastDayOfMonth = firstDayOfMonth;
		CalendarUtil.addDaysToDate(lastDayOfMonth, monthDays - 1);

		return lastDayOfMonth;
	}

	public static Date resetTime(Date date) {
		long time = date.getTime();
		long milliseconds = time % (24 * 60 * 60 * 1000);
		return new Date(time - milliseconds);
	}

	public static boolean equals(Date d1, Date d2) {
		if (d1 == d2)
			return true;
		if (d1 == null)
			return false;
		if (d2 == null)
			return false;

		return CalendarUtil.isSameDate(d1, d2);
	}

	public static int compare(Date date0, Date date1) {
		return ((date0.getYear() - date1.getYear()) * 372)
				+ ((date0.getMonth() - date1.getMonth()) * 31) + // max = 11 *
																	// 31
				(date0.getDate() - date1.getDate()); // max = 30

	}

}
