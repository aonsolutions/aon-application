package com.code.aon.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;

/**
 * Clase CommonUtil para incluir métodos útiles comunes a los proyectos Aon-ui y
 * Aon-no-ui.
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
	 * @return double el valor redondeado
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
	 * @return double el valor redondeado
	 */
	public static double round(double value) {
		return round(value, 2);
	}

	/**
	 * Devuelve el último dia del año en función de año pasado por parámetro.
	 * 
	 * @param year
	 *            El año del que se desea el último dia.
	 * 
	 * @return Un java.util.Date con el último dia de ese año.
	 */
	public static Date getYearLastDay(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * Devuelve el último dia del año en función de la fecha pasada por
	 * parámetro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el último dia del año.
	 * 
	 * @return Un java.util.Date con el último dia de ese año.
	 */
	public static Date getYearLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, 11);
		return calendar.getTime();
	}

	/**
	 * Devuelve el primer dia del año en función de año pasado por parámetro.
	 * 
	 * @param year
	 *            El año del que se desea el primer dia.
	 * 
	 * @return Un java.util.Date con el primer dia de ese año.
	 */
	public static Date getYearFirstDay(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * Devuelve el primer dia del año en función de año pasado por parámetro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del año.
	 * 
	 * @return Un java.util.Date con el primer dia de ese año.
	 */
	public static Date getYearFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		return calendar.getTime();
	}

	/**
	 * Devuelve el número de dias que hay entre las fechas pasadas por
	 * parámetro.
	 * 
	 * @param from
	 *            Fecha inicial.
	 * @param to
	 *            Fecha final.
	 * @return Dias entre las fechas.
	 */
	public static long getDaysBetweenDates(Date from, Date to) {
		if (from == null) {
			throw new IllegalArgumentException("Date 'from' value can not be null.");
		}
		if (to == null) {
			throw new IllegalArgumentException("Date 'to' value can not be null.");
		}
		if (to.before(from)) {
			throw new IllegalArgumentException("Date 'to' can not be earlier than date 'from'.");
		}
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(from);
		c2.setTime(to);
		double r = (double) (c2.getTimeInMillis() - c1.getTimeInMillis())
				/ (double) (24 * 3600 * 1000);
		r = CommonUtil.round(r, 0);
		return (long) r;
	}

	/**
	 * Devuelve el año de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber el año.
	 * @return El año.
	 */
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	/**
	 * Devuelve el mes (0-11) de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber el mes.
	 * @return El mes.
	 */
	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

	/**
	 * Devuelve el dia de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber el dia.
	 * @return El dia.
	 */
	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Devuelve el número de dias del mes en curso indicado en la fecha.
	 * 
	 * @param date
	 *            Fecha de la que se desea saber el número de dias del mes.
	 * @return int El número de dias del mes.
	 */
	public static int daysInMonth(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = DateUtils.addMonths(c1.getTime(), 1);
		return (int) getDaysBetweenDates(c1.getTime(), nextMonth);
	}

	/**
	 * Si el año indicado por parámetro es bisiesto.
	 * 
	 * @param year
	 *            Año.
	 * @return boolean TRUE si es bisiesto.
	 */
	public static boolean isLeapYear(int year) {
		GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
		return c.isLeapYear(year);
	}

	/**
	 * Devuelve una fecha con los parámetros indicados.
	 * 
	 * @param year
	 *            El año de la fecha.
	 * @param month
	 *            El mes de la fecha. (0-11).
	 * @param day
	 *            El dia de la fecha.
	 * @return Date La fecha construída.
	 */
	public static Date getDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		return c.getTime();

	}

}
