package com.esferalia.aon.gwt.payroll.shared;

public class NumberUtils {

	public static boolean isValid(Double number) {
		return number != null && !number.isNaN() && !number.isInfinite();
	}

	public static boolean isNotValid(Double number) {
		return !isValid(number);
	}
}
