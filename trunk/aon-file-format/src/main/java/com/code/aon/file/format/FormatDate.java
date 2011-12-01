package com.code.aon.file.format;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * FormatDate Factory
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class FormatDate implements Format {

	public static final String ZERO = "0";
	/**
	 * The pattern
	 */
	private Object pattern; // 9(?)
	/**
	 * The size
	 */
	private int size;
	/**
	 * The default value
	 */
	private static final String DEFAULT_VALUE = "0";

	public void applyPattern(Object newPattern) {
		this.applyPattern((String)newPattern);
	}

	/**
	 * Assigns a pattern
	 * 
	 * @param newPattern the string of the pattern
	 */
	public void applyPattern(String newPattern) {
		this.pattern = newPattern.trim();
		size = this.getPrecision((String)this.pattern);
	}

	public String format(Object value) {
		if (value != null && value instanceof Timestamp) {
			value = new Date(((Timestamp)value).getTime());
		}else if (value != null && value instanceof Date) {
			SimpleDateFormat df = new SimpleDateFormat((String)pattern);
			return df.format(value);
		}else if (value != null && value instanceof Calendar) {
			SimpleDateFormat df = new SimpleDateFormat((String)pattern);
			return df.format(((Calendar)value).getTime());
		}else if (value != null && value instanceof GregorianCalendar) {
			SimpleDateFormat df = new SimpleDateFormat((String)pattern);
			return df.format(((Calendar)value).getTime());
		}
		return this.format(DEFAULT_VALUE);
	}

	/**
	 * Formats the String
	 * 
	 * @param value the original string
	 * @return the string formatted
	 */
	public String format(String value) {
		String tmp = new String(value.trim());
		tmp = (tmp.length() > size) ? tmp.substring(0, size) : tmp;

		return this.fillPrecision(tmp.length()) + tmp;
	}

	/**
	 * Return the needed string to fill the correct size
	 * 
	 * @param length current lenght
	 * @return the filler string
	 */
	private String fillPrecision(int length) {
		int total = this.size - length;
		StringBuffer sb = new StringBuffer(total);
		for (int i=0; i<total; i++) {
			sb.append(ZERO);
		}
		return sb.toString();
	}

	/**
	 * Returns the precision
	 * 
	 * @param pattern the pattern
	 * @return the precision
	 */
	public int getPrecision(String pattern) {
		return (pattern.length());
	}

	public Object parse(String value) throws java.text.ParseException {
		return value.trim();
	}

	@SuppressWarnings("deprecation")
	static public void main(String[] args) {
		FormatDate fecha = new FormatDate();
		fecha.applyPattern("ddMMyy");
		System.out.println ("#"+fecha.format(new Date(102, 11, 28))+"#");
	}

}