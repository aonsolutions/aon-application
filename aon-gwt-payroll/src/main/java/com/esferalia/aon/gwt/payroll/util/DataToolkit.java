package com.esferalia.aon.gwt.payroll.util;

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
		catch(NumberFormatException ignored) {}
		
		return parsed;
	}

	
}
