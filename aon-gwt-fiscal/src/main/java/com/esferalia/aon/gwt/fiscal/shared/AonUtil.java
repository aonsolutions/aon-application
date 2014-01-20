package com.esferalia.aon.gwt.fiscal.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AonUtil {
	public static double round(double value) {
		return AonUtil.round(value,2);
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

}
