package com.esferalia.aon.watson.util;

public class AonNumberUtils {

	public static final Integer INTEGER_ZERO = new Integer(0);
	public static final Integer INTEGER_ONE = new Integer(1);
	
	public static boolean isValid(Double number) {
		return number != null && !number.isNaN() && !number.isInfinite();
	}

	public static boolean isNotValid(Double number) {
		return !isValid(number);
	}

	public static boolean equals(Number n1, Number n2) {
		if (n1 == n2)
			return true;
		if (n1 == null)
			return false;
		if (n2 == null)
			return false;
		return n1.equals(n2);
	}
	public static boolean notEquals(Number n1, Number n2) {
		return !equals(n1, n2);
	}

	public static <T extends Number> int compare(T n1, T n2) {
		if (n1 == n2)
			return 0;
		if (n1 == null)
			return -1;
		if (n2 == null)
			return 1;
		return Double.compare(n1.doubleValue(), n2.doubleValue());
	}

	public static <T extends Number> int compare(T n1, T n2, int decimals) {
		if (n1 == n2)
			return 0;
		if (n1 == null)
			return -1;
		if (n2 == null)
			return 1;
		return Double.compare(Math.round(n1.doubleValue() * 10*decimals), Math.round(n2.doubleValue() * 10 * decimals ));
	}

	public static Byte toByte(String value) {
		if (!AonStringUtils.isBlank(value)) {
			return Byte.parseByte(value);
		}
		return null;
	}

	public static byte toByte(Integer i) {
		if (i != null) {
			return i.byteValue();
		}
		return 0;
	}

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

	public static double todouble(String value) {
		if (!AonStringUtils.isBlank(value)) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				// Nothing. returns 0.
			}
		}
		return 0;		
	}

	public static int toint(String value) {
		if (!AonStringUtils.isBlank(value)) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				// Nothing. returns 0.
			}
		}
		return 0;		
	}

	public static String toString(Integer value) {
		if (value == null) return null;
		return value.toString();
	}
	public static String toString(Double value) {
		if (value == null) return null;
		return value.toString();
	}

	public static boolean between(Number value, Number limit1, Number limit2) {
		if (Double.compare(value.doubleValue(), limit1.doubleValue()) < 0) return false;
		if (Double.compare(value.doubleValue(), limit2.doubleValue()) > 0) return false;
		return true;
	}

	public static double zeroIfNull(Double value) {
		return (value==null?0:value);
	}

}
