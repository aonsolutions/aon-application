package com.esferalia.aon.gwt.common.shared;

public class NumberUtils {

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
}
