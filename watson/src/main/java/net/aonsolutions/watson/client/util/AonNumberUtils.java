package net.aonsolutions.watson.client.util;

public class AonNumberUtils {

	private AonNumberUtils() {

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
