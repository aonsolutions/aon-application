package com.code.aon.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommonUtil {

	public static double round(double value) {
		return round(value, 2);
	}
	
	public static double round(double value, int precision) {
		if ( Double.isNaN(value) )
			return 0.00;
		if ( Double.isInfinite(value) )
			return 0.00;
		
		return new BigDecimal(value).setScale(precision, RoundingMode.HALF_UP).doubleValue();
	}
}
