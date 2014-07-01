package com.esferalia.aon.gwt.common.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AonUtil {
	public static final String EMPTY = "";
			
	public static double round(double value) {
		return AonUtil.round(value,2);
	}
	
	public static boolean equals(double value1,double value2) {
		return AonUtil.round(value1) == AonUtil.round(value2);
	}

	public static double round(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision,
				RoundingMode.HALF_UP).doubleValue();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean contains(String str, char searchChar) {
		if (isEmpty(str)) {
			return false;
		}
		return str.indexOf(searchChar) >= 0;
	}

	public static String trim(String str) {
		return str == null ? null : str.trim();
	}

	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.length() == 0) {
			return EMPTY;
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	public static String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return EMPTY;
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}
}
