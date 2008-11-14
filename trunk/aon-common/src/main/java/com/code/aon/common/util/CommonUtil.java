package com.code.aon.common.util;

/**
 * Clase CommonUtil para incluir métodos útiles comunes a los proyectos Aon-ui y Aon-no-ui.
 */
public class CommonUtil {

	/**
	 * Redondea un valor decimal a la precisión requerida
	 * 
	 * @param value
	 *            el valor a redondear
	 * 
	 * @param precision
	 *            la precisión de la parte decimal
	 * @return double
	 *            el valor redondeado
	 */
	public static double round(double value, int precision) {
	    return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	/**
	 * Redondea un valor decimal a 2 dígitos en la parte decimal
	 * 
	 * @param value
	 *            el valor a redondear
	 * 
	 * @return double
	 *            el valor redondeado
	 */
	public static double round(double value) {
	    return round(value, 2);
	}

}
