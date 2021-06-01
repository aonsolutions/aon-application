package com.esferalia.aon.gwt.payroll.util;

import java.util.regex.Pattern;

public class DataToolkit {
	/**
	 * Get safe value
	 * 
	 * @param value - The value.
	 * @return value | 0
	 */
	public static Double safeValue(Double value) {
		return value != null ? value : 0d;
	}

	/**
	 * Get safe value
	 * 
	 * @param value - The value.
	 * @return value | ""
	 */
	public static String safeValue(String value) {
		return value != null ? value : "";
	}

	
	/**
	 * safe parse double
	 * @param value
	 * @return parsed value | defaultValue
	 */
	public static Double safeParseDouble(String value, double defaultValue) {
		Double parsed = defaultValue;
		try{
			parsed = Double.parseDouble(value);
		}
		catch(Exception ignored) {}
		
		return parsed;
	}

	/**
	 * Returns if a string IS a number
	 * @param string -The string to evaluate
	 * @return true | false
	 */
	public static boolean isNumber(String string) {
		Pattern pattern = Pattern.compile("\\d*\\.?\\d*");
		return pattern.matcher(string).matches();
	}
	
}
