package com.esferalia.aon.gwt.common.shared;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class DateUtils {

	public static int getYear() {
		return getYear(new Date());
	}

	public static int getYear(Date date) {
		return date.getYear() + 1900;
	}
	
	public static int getMonth(Date date) {
		return date.getMonth();
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


	public static Date setDayOfMonth(Date date, int day) {
		date.setDate(day);
		return date;
	}
	
	public static Date addDays2Date(Date date, int days) {
		date.setDate(date.getDate() + days);
		return date;
	}

	public static Date deleteDays2Date(Date date, int days) {
		date.setDate(date.getDate() - days);
		return date;
	}

	public static Date addYears2Date(Date date, int years) {
		CalendarUtil.addMonthsToDate(date, years * 12);
		return date;
	}

	public static Date addMonths2Date(Date date, int months) {
		CalendarUtil.addMonthsToDate(date, months);
		return date;
	}

	public static Date getFirstDayOfYear() {
		return getFirstDayOfYear(new Date());		
	}

	public static Date getFirstDayOfMonth() {
		return getFirstDayOfMonth(new Date());
	}

	public static Date getLastDayOfMonth() {
		return getLastDayOfMonth(new Date());
	}

	public static int getYears(Date a, Date b) {
		return (a.getYear() - b.getYear());
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

	public static boolean isBeforeOrEquals(Date a, Date b) {
		if (a == null)
			return false;
		if (b == null)
			return true;

		return a.before(b) || a.equals(b);
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
		return new Date(date.getYear(), date.getMonth(), date.getDate() - 1);
	}

	public static Date getNextDay(Date date) {
		return new Date(date.getYear(), date.getMonth(), date.getDate() + 1);
	}

	public static Date getFirstDayOfWorkWeek(Date date) {
		Date firstDayOfWeek = copyDateOnly(date);
		int dayOfWeek = firstDayOfWeek.getDay();
		// Remember : 0 for Sunday and 6 for Saturday
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}
		CalendarUtil.addDaysToDate(firstDayOfWeek, 1 - dayOfWeek);
		return firstDayOfWeek;
	}

	public static Date getLastDayOfWorkWeek(Date date) {
		Date lastDayOfWeek = copyDateOnly(date);
		int dayOfWeek = lastDayOfWeek.getDay();
		// Remember : 0 for Sunday and 6 for Saturday
		if (dayOfWeek == 0) {
			dayOfWeek = 7;
		}
		CalendarUtil.addDaysToDate(lastDayOfWeek, 7 - dayOfWeek);
		return lastDayOfWeek;
	}

	public static Date getFirstDayOfYear(Date date) {
		return new Date(date.getYear(), 0, 1);
	}
	public static Date getFirstDayOfYear(int year) {
		return new Date(year, 0, 1);
	}
	public static Date getLastDayOfYear(int year) {
		return new Date(year, 11, 31);
	}
	public static Date getLastDayOfYear(Date date) {
		return new Date(date.getYear(), 11, 31);
	}

	public static Date getFirstDayOfMonth(Date date) {
		return new Date(date.getYear(), date.getMonth(), 1);
	}

	public static Date getLastDayOfMonth(Date date) {
		return new Date(date.getYear(), date.getMonth() + 1, 0);
	}

	public static Date copyDateOnly(Date date) {
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}

	public static boolean isFirstDayOfYear(Date date) {

		return date != null && date.getMonth() == 0 && date.getDate() == 1;
	}

	public static boolean isLastDayOfYear(Date date) {
		return date != null && date.getMonth() == 11 && date.getDate() == 31;
	}

	public static int getDaysBetween(Date start, Date finish) {
		// Convert the dates to the same time
		long aTime = Date.UTC(start.getYear(), start.getMonth(), start.getDate(), 0, 0, 0);
		long bTime = Date.UTC(finish.getYear(), finish.getMonth(), finish.getDate(), 0, 0, 0);

		return (int) ((bTime - aTime) / (24 * 60 * 60 * 1000));
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

	public static int compare(Date d0, Date d1) {
		if (d0 == d1)
			return 0;
		if (d0 == null)
			return 1;
		if (d1 == null)
			return -1;

		return ((d0.getYear() - d1.getYear()) * 372)
				+ ((d0.getMonth() - d1.getMonth()) * 31) + // max = 11*31
				(d0.getDate() - d1.getDate()); // max = 30

	}

	public static Date toUTC(Date date) {
		return new Date(Date.UTC(date.getYear(), date.getMonth(),
				date.getDate(), 0, 0, 0));
	}

	/**
	 * Resets the date to have no time modifiers. Note that the hour might not
	 * be zero if the time hits a DST transition date.
	 *
	 * @param date
	 *            the date
	 */
	@SuppressWarnings("deprecation") // GWT requires Date
	public static void resetTime(Date date) {
		long msec = date.getTime();
		msec = (msec / 1000) * 1000;
		date.setTime(msec);
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
	}

}
