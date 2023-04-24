package com.esferalia.aon.watson.util;

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

	public static double roundDown(double value, int precision) {
		return new BigDecimal(Double.toString(value)).setScale(precision, RoundingMode.HALF_DOWN).doubleValue();
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
	 * Devuelve el valor absoluto redondeado a 2 dígitos.
	 * 
	 * @param value
	 *            el valor a truncar
	 * @return double el valor absoluto redondeado a 2 dígitos.
	 */
	public static double absRounded(double value) {
		return round( Math.abs(value) );
	}

	/**
	 * Devuelve verdadero si el valor es igual a cero. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return boolean verdadero si el valor es igual a cero.
	 */
	public static boolean isZero(double value) {
		return round( value ) == 0.0;
	}

	/**
	 * Devuelve verdadero si el valor es menor a cero. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return boolean verdadero si el valor es igual a cero.
	 */
	public static boolean isLessThanZero(double value) {
		return round( value ) < 0.0;
	}

	/**
	 * Devuelve verdadero si el valor es menor que valueToCompare 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @param threshold
	 *            el valor a comparar
	 * @return boolean verdadero si el value es menor threshold.
	 */
	public static boolean isLessThan(double value,double threshold) {
		return round( value ) < threshold;
	}

	/**
	 * Devuelve verdadero si el valor es mayor a cero. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return boolean verdadero si el valor es igual a cero.
	 */
	public static boolean isGreatherThanZero(double value) {
		return round( value ) > 0.0;
	}

	/**
	 * Devuelve verdadero si el valor es mayor que valueToCompare 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @param threshold
	 *            el valor a comparar
	 * @return boolean verdadero si el value es mayor threshold.
	 */
	public static boolean isGreatherThan(double value,double threshold) {
		return round( value ) > threshold;
	}

	/**
	 * Devuelve verdadero si el valor es diferente de cero. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return boolean verdadero si el valor es diferente de cero.
	 */
	public static boolean isNotZero(double value) {
		return !isZero(value);
	}
	
	/**
	 * Devuelve verdadero si el valor es diferente de cero. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return boolean verdadero si el valor es diferente de cero.
	 */
	public static boolean isNegative(double value) {
		return round( value ) < 0.0;
	}

	/**
	 * Devuelve verdadero si son iguales los valores pasados, redondesdos a dos decimales.
	 * @param Primer double a comparar.
	 * @param Segundo double a comparar.
	 * @return
	 */
	public static boolean equals(double value1,double value2) {
		return round(value1) == round(value2);
	}
	public static boolean notEquals(double value1,double value2) {
		return !equals(value1,value2);
	}
	
	/**
	 * Devuelve verdadero si el valor esta comprendido en el rango indicado.
	 * @param valor a evaluar.
	 * @param gte Rango inferior
	 * @param lte Rango superior
	 * @return
	 */
	public static boolean between(double value,double gte,double lte) {
		return round(value) >= round(gte) && 
			   round(value) <= round(lte) ;
	}

	/**
	 * Devuelve el tipo primitivo, y cero si el objeto es null. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return int el tipo primitivo, y cero si el objeto es null..
	 */
	public static int toInt(Integer value) {
		return value == null ?0 :value;
	}

	/**
	 * Devuelve el tipo primitivo, y cero si el objeto es null. 
	 * 
	 * @param value
	 *            el valor a evaluar
	 * @return int el tipo primitivo, y cero si el objeto es null..
	 */
	public static double toDouble(Double value) {
		return value == null ?0 :value;
	}
	
	/**
	 * Suma y redondea dos valores
	 * 
	 * @param value1 primer sumando.
	 * @param value2 segundo sumando.
	 * 
	 * @return double el valor de la suma redondeado.
	 */
	public static double sum(double value1, double value2) {
		return round( value1 + value2);
	}
	
	/**
	 * Devuelve el mayor
	 * 
	 * @param a primer num. 
	 * @param b segundo num..
	 * 
	 * @return El mayor de los dos.
	 */
    public static int max(final int a, final int b) {
        if (b > a) {
            return b;
        }
        return a;
    }

	/**
	 * Devuelve el mayor
	 * 
	 * @param a primer num. 
	 * @param b segundo num..
	 * 
	 * @return El mayor de los dos.
	 */
    public static int min(final int a, final int b) {
        if (a > b) {
            return b;
        }
        return a;
    }
    
    
	/**
	 * Devuelve cero si el valor es menor que zero
	 * 
	 * @param value primer num. 
	 * 
	 * @return Cero si negativo
	 */
    public static double zeroIfNegative(double value) {
    	return isLessThanZero(value)?0.0:value;
    }

    
	/**
	 * Devuelve cero si el valor es mayor que zero
	 * 
	 * @param value primer num. 
	 * 
	 * @return Cero si positivo
	 */
    public static double zeroIfPositive(double value) {
    	return isGreatherThanZero(value)?0.0:value;
    }
}
