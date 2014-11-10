package com.esferalia.aon.shared.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AonMathUtils {
	/**
	 * Redondea un valor decimal a la precision requerida
	 * 
	 * @param value
	 *            el valor a redondear
	 * 
	 * @param precision
	 *            la precision de la parte decimal
	 * @return double el valor redondeado
	 */
	public static double round(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Redondea un valor decimal a 2 digitos en la parte decimal
	 * 
	 * @param value
	 *            el valor a redondear
	 * 
	 * @return double el valor redondeado
	 */
	public static double round(double value) {
		return round(value, 2);
	}

	/**
	 * Redondea a la baja un valor decimal a la precision requerida
	 * 
	 * @param value
	 *            el valor a truncar
	 * 
	 * @param precision
	 *            la precision de la parte decimal
	 * @return double el valor truncado
	 */
	public static double floor(double value, int precision) {
		return Math.floor(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	/**
	 * Redondea a la baja un valor decimal a 2 digitos en la parte decimal
	 * 
	 * @param value
	 *            el valor a truncar
	 * 
	 * @return double el valor tuncado
	 */
	public static double floor(double value) {
		return floor(value, 2);
	}

	/**
	 * Redondea al alta un valor decimal a la precision requerida
	 * 
	 * @param value
	 *            el valor a truncar
	 * 
	 * @param precision
	 *            la precision de la parte decimal
	 * @return double el valor truncado
	 */
	public static double ceil(double value, int precision) {
		return Math.ceil(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	/**
	 * Redondea al alta un valor decimal a 2 digitos en la parte decimal
	 * 
	 * @param value
	 *            el valor a truncar
	 * 
	 * @return double el valor tuncado
	 */
	public static double ceil(double value) {
		return ceil(value, 2);
	}
	
	/**
	 * Devuleve el valor absoluto redondeado a 2 dígitos.
	 * 
	 * @param value
	 *            el valor a truncar
	 * @return double el valor absoluto redondeado a 2 dígitos.
	 */
	public static double absRounded(double value) {
		return round( Math.abs(value) );
	}

}
