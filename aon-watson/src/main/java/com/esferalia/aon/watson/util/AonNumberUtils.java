package com.esferalia.aon.watson.util;



public class AonNumberUtils {

	public static Integer toInteger(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

	public static Double toDouble(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Double.parseDouble(value);
		}
		return null;
	}

	public static String toString(Integer value) {
		if (value == null) return null;
		return value.toString();
	}
	public static String toString(Double value) {
		if (value == null) return null;
		return value.toString();
	}

}
