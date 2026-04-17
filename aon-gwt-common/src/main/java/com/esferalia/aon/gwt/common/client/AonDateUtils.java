package com.esferalia.aon.gwt.common.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class AonDateUtils {

	public static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	public static final DateTimeFormat hourFormat = DateTimeFormat.getFormat("HH:mm");
	public static final DateTimeFormat monthYearFormat = DateTimeFormat.getFormat("MMM/yy");

	public static Integer getCurrentYear() {
		return 1900 + new Date().getYear();
	}
	
	public static Date parse(String format, String date) {
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(format);
		return dateFormat.parse(date);
	}
	
	public static Date parseDateTime(String date) {
		return dateTimeFormat.parse(date);
	}
	
	public static Date parseDate(String date) {
		 return dateFormat.parse(date);
	}
	
	public static Date parseTime(String date) {
		return hourFormat.parse(date);
	}
	
	public static Date parseMonthYear(String date) {
		return monthYearFormat.parse(date);
	}
	
	public static String format(String format, Date date) {
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(format);
		return dateFormat.format(date);
	}
	
	public static String formatDateTime(Date date) {
		return dateTimeFormat.format(date);
	}
	
	public static String formatDate(Date date) {
		 return null == date ? "" : dateFormat.format(date);
	}
	
	public static String formatTime(Date date) {
		return hourFormat.format(date);
	}
	
	public static String formatMonthYear(Date date) {
		String dateFormated = null == date ? "" : monthYearFormat.format(date);
		dateFormated = capitalizeAndRemoveDot(dateFormated);
		return dateFormated;
	}
	
	private static String capitalizeAndRemoveDot(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        if (input.contains(".")) {
            input = input.replace(".", "");
        }

        // Capitalizar la primera letra del mes
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
	
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

	public static Date fromLong(String asLong) {
		return new Date( Long.valueOf(asLong) );
	}

	public static Integer getYear(Date fromDate) {
		if (fromDate != null) {
			return 1900 + fromDate.getYear();
		}
		return null;
	}
}
