package com.esferalia.aon.accounting.mining.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AccMiningUtils {

	public static final String EMPTY = "";

	public static String defaultString(String str) {
		return str == null ? EMPTY : str;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;

	}

	public static boolean isNotEmpty(String str) {
		return !AccMiningUtils.isEmpty(str);
	}
	
	public static double round(double value) {
		return round(value, 2);
	}

	public static double round(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_UP).doubleValue();
	}
}
