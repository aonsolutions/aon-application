package net.aonsolutions.watson.client.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AonNumberUtils {

	private AonNumberUtils() {

	}

	public static double round(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_UP).doubleValue();
	}
	public static double round(double value) {
		return round(value, 2);
	}

	public static boolean equals(Number n1, Number n2) {
		if (n1 == n2)
			return true;
		if (n1 == null)
			return false;
		if (n2 == null)
			return false;
		return Double.compare(n1.doubleValue(), n2.doubleValue()) == 0;
	}

	public static boolean notEquals(Number n1, Number n2) {
		return !equals(n1, n2);
	}

	public static String toString(Integer value) {
		if (value == null)
			return null;
		return value.toString();
	}

	public static String toString(Double value) {
		if (value == null)
			return null;
		return value.toString();
	}

	public static String toString(Number value) {
		if (value == null)
			return null;
		return value.toString();
	}

	public static Integer toInteger(Number number) {
		if (number == null)
			return null;
		return Integer.valueOf(number.intValue());
	}
}
