package net.aonsolutions.watson.client.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AonMathUtils {

	private AonMathUtils() {

	}

	public static double round(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_UP).doubleValue();
	}

	public static double roundDown(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_DOWN).doubleValue();
	}

	public static double round(double value) {
		return round(value, 2);
	}

	public static double floor(double value, int precision) {
		return Math.floor(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	public static double floor(double value) {
		return floor(value, 2);
	}

	public static double ceil(double value, int precision) {
		return Math.ceil(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	public static double ceil(double value) {
		return ceil(value, 2);
	}

	public static double absRounded(double value) {
		return round(Math.abs(value));
	}

	public static boolean isZero(double value) {
		return round(value) == 0.0;
	}

	public static boolean isNotZero(double value) {
		return !isZero(value);
	}

	public static boolean isLessThanZero(double value) {
		return round(value) < 0.0;
	}

	public static boolean isLessThan(double value, double threshold) {
		return round(value) < threshold;
	}

	public static boolean isGreatherThanZero(double value) {
		return round(value) > 0.0;
	}

	public static boolean isGreatherThan(double value, double threshold) {
		return round(value) > threshold;
	}

	public static boolean isNegative(double value) {
		return round(value) < 0.0;
	}

	public static boolean equals(double value1, double value2) {
		return round(value1) == round(value2);
	}

	public static boolean notEquals(double value1, double value2) {
		return !equals(value1, value2);
	}

	public static boolean between(double value, double gte, double lte) {
		return round(value) >= round(gte) && round(value) <= round(lte);
	}

	public static int toInt(Integer value) {
		return value == null ? 0 : value;
	}

	public static double toDouble(Double value) {
		return value == null ? 0 : value;
	}

	public static double sum(double value1, double value2) {
		return round(value1 + value2);
	}

	public static int max(final int a, final int b) {
		return (b > a) ? b : a;
	}

	public static int min(final int a, final int b) {
		return (a > b) ? b : a;
	}

	public static double zeroIfNegative(double value) {
		return isLessThanZero(value) ? 0.0 : value;
	}

	public static double zeroIfPositive(double value) {
		return isGreatherThanZero(value) ? 0.0 : value;
	}
}
