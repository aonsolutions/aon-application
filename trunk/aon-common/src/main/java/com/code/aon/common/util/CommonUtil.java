package com.code.aon.common.util;

/**
 * CommonUtil includes some common methods.
 */
public class CommonUtil {

	/**
	 * Rounds a decimal value to the required precision.
	 * 
	 * @param value
	 *            the value to round
	 * 
	 * @param precision
	 *            the precision of the decimal part
	 * @return the value rounded
	 */
	public static double round(double value, int precision) {
	    return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
	  }

}
