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

	public static Double toDouble(Number number) {
		if (number == null)
			return null;
		return Double.valueOf(number.doubleValue());
	}

	/**
     * <p>Convert a {@code String} to an {@code int}, returning
     * {@code zero} if the conversion fails.</p>
     *
     * <p>If the string is {@code null}, {@code zero} is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null) = 0
     *   NumberUtils.toInt("")   = 0
     *   NumberUtils.toInt("1")  = 1
     * </pre>
     *
     * @param str  the string to convert, may be null
     * @return the int represented by the string, or {@code zero} if
     *  conversion fails
     * @since 2.1
     */
    public static int toInt(final String str) {
        return toInt(str, 0);
    }

    /**
     * <p>Convert a {@code String} to an {@code int}, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is {@code null}, the default value is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     *
     * @param str  the string to convert, may be null
     * @param defaultValue  the default value
     * @return the int represented by the string, or the default if conversion fails
     * @since 2.1
     */
    public static int toInt(final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Integer toInteger(final String str) {
        if (str == null) return null;
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }
}
