package net.aonsolutions.aon.gwt.warehouse.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class Utils {
	
	public static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	public static final DateTimeFormat hourFormat = DateTimeFormat.getFormat("HH:mm");

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
	
	public static Date format(String format, String date) {
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(format);
		return dateFormat.parse(date);
	}
	
	public static String formatDateTime(Date date) {
		return dateTimeFormat.format(date);
	}
	
	public static String formatDate(Date date) {
		 return dateFormat.format(date);
	}
	
	public static String formatTime(Date date) {
		return hourFormat.format(date);
	}
	
}
