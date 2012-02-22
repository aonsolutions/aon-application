package com.code.aon.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;

/**
 * Clase CommonUtil para incluir mï¿½todos ï¿½tiles comunes a los proyectos Aon-ui y
 * Aon-no-ui.
 */
public class CommonUtil {

	/**
	 * Redondea un valor decimal a la precisiï¿½n requerida
	 * 
	 * @param value
	 *            el valor a redondear
	 * 
	 * @param precision
	 *            la precisiï¿½n de la parte decimal
	 * @return double el valor redondeado
	 */
	public static double round(double value, int precision) {
		return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	/**
	 * Redondea un valor decimal a 2 dï¿½gitos en la parte decimal
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
	 * Trunca un valor decimal a la precisiï¿½n requerida
	 * 
	 * @param value
	 *            el valor a truncar
	 * 
	 * @param precision
	 *            la precisiï¿½n de la parte decimal
	 * @return double el valor truncado
	 */
	public static double truncate(double value, int precision) {
		return Math.floor(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	/**
	 * Trunca un valor decimal a 2 dï¿½gitos en la parte decimal
	 * 
	 * @param value
	 *            el valor a truncar
	 * 
	 * @return double el valor tuncado
	 */
	public static double truncate(double value) {
		return truncate(value, 2);
	}

	/**
	 * Devuelve el ï¿½ltimo dia del aï¿½o en funciï¿½n de aï¿½o pasado por parï¿½metro.
	 * 
	 * @param year
	 *            El aï¿½o del que se desea el ï¿½ltimo dia.
	 * 
	 * @return Un java.util.Date con el ï¿½ltimo dia de ese aï¿½o.
	 */
	public static Date getYearLastDay(int year) {
		return getDate(year, 11, 31);
	}

	/**
	 * Devuelve el ï¿½ltimo dia del aï¿½o en funciï¿½n de la fecha pasada por
	 * parï¿½metro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el ï¿½ltimo dia del aï¿½o.
	 * 
	 * @return Un java.util.Date con el ï¿½ltimo dia de ese aï¿½o.
	 */
	public static Date getYearLastDay(Date date) {
		return getDate(getYear(date), 11, 31);
	}

	/**
	 * Devuelve el primer dia del aï¿½o en funciï¿½n de aï¿½o pasado por parï¿½metro.
	 * 
	 * @param year
	 *            El aï¿½o del que se desea el primer dia.
	 * 
	 * @return Un java.util.Date con el primer dia de ese aï¿½o.
	 */
	public static Date getYearFirstDay(int year) {
		return getDate(year, 0, 1);
	}

	/**
	 * Devuelve el primer dia del aï¿½o en funciï¿½n de aï¿½o pasado por parï¿½metro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del aï¿½o.
	 * 
	 * @return Un java.util.Date con el primer dia de ese aï¿½o.
	 */
	public static Date getYearFirstDay(Date date) {
		return getDate(getYear(date), 0, 1);
	}

	/**
	 * Devuelve el nï¿½mero de dias que hay entre las fechas pasadas por
	 * parï¿½metro.
	 * 
	 * @param from
	 *            Fecha inicial.
	 * @param to
	 *            Fecha final.
	 * @return Dias entre las fechas.
	 */
	public static long getDaysBetweenDates(Date from, Date to) {
		return getDaysBetweenDates(from, to, true);
	}

	public static Date[] getWeekDateRange(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DAY_OF_YEAR, (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) * (-1));
	    Date start = cal.getTime();
	    cal.add(Calendar.DAY_OF_YEAR, +6);
	    Date end = cal.getTime();
		return new Date[]{start,end};
	}
    
	public static Date[] getTwoWeekDateRange(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DAY_OF_YEAR, (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) * (-1));
	    Date start = cal.getTime();
	    cal.add(Calendar.DAY_OF_YEAR, +13);
	    Date end = cal.getTime();
		return new Date[]{start,end};
	}

	/**
	 * Devuelve el nï¿½mero de dias que hay entre las fechas pasadas por
	 * parï¿½metro.
	 * 
	 * @param from
	 *            Fecha inicial.
	 * @param to
	 *            Fecha final.
	 * @param checkDates
	 *            Chequear que from sea anterior a to.
	 * @return Dias entre las fechas.
	 */
	public static long getDaysBetweenDates(Date from, Date to, boolean checkDates) {
		if (from == null) {
			throw new IllegalArgumentException("Date 'from' value can not be null.");
		}
		if (to == null) {
			throw new IllegalArgumentException("Date 'to' value can not be null.");
		}
		if (checkDates && to.before(from)) {
			throw new IllegalArgumentException("Date 'to' can not be earlier than date 'from'.");
		}
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(from);
		c2.setTime(to);
		double r = (double) (c2.getTimeInMillis() - c1.getTimeInMillis()) / (double) (24 * 3600 * 1000);
		r = CommonUtil.round(r, 0);
		return (long) r;
	}

	/**
	 * Devuelve el aï¿½o de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber el aï¿½o.
	 * @return El aï¿½o.
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
	 * Devuelve el nï¿½mero de dias del mes en curso indicado en la fecha.
	 * 
	 * @param date
	 *            Fecha de la que se desea saber el nï¿½mero de dias del mes.
	 * @return int El nï¿½mero de dias del mes.
	 */
	public static int daysInMonth(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = DateUtils.addMonths(c1.getTime(), 1);
		return (int) getDaysBetweenDates(c1.getTime(), nextMonth);
	}

	/**
	 * Si el aï¿½o indicado por parï¿½metro es bisiesto.
	 * 
	 * @param year
	 *            Aï¿½o.
	 * @return boolean TRUE si es bisiesto.
	 */
	public static boolean isLeapYear(int year) {
		GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
		return c.isLeapYear(year);
	}

	/**
	 * Devuelve una fecha con los parï¿½metros indicados.
	 * 
	 * @param year
	 *            El aï¿½o de la fecha.
	 * @param month
	 *            El mes de la fecha. (0-11).
	 * @param day
	 *            El dia de la fecha.
	 * @return Date La fecha construï¿½da.
	 */
	public static Date getDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		Date date = c.getTime();
		return DateUtils.truncate(date, Calendar.DAY_OF_MONTH); 

	}

	/**
	 * Devuelve el primer dia del mes de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del mes.
	 * 
	 * @return Un java.util.Date con el primer dia de ese aï¿½o.
	 */
	public static Date getMonthFirstDay(Date date) {
		return getDate(getYear(date),getMonth(date),1 );
	}

	/**
	 * Devuelve el ï¿½ltimo dia del mes en funciï¿½n de la fecha pasada por
	 * parï¿½metro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el ï¿½ltimo dia del mes.
	 * 
	 * @return Un java.util.Date con el ï¿½ltimo dia de ese aï¿½o.
	 */
	public static Date getMonthLastDay(Date date) {
		return getDate(getYear(date),getMonth(date), CommonUtil.daysInMonth(date) );
	}
	
	public static Date getBiMonthFirstDay(Date date) {
		int month = getMonth(date) / 2;
		month = month * 2; 
		return getDate(getYear(date),month,1 );
	}
	
	public static Date getBiMonthLastDay(Date date) {
		int month = (getMonth(date) / 2);
		month = (month * 2) + 1;
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}
	
	public static Date getQuarterFirstDay(Date date) {
		int month = getMonth(date) / 3;
		month = month * 3; 
		return getDate(getYear(date),month,1 );
	}
	
	public static Date getQuarterLastDay(Date date) {
		int month = getMonth(date) / 3;
		month = (month * 3) + 2; 
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}

	public static Date getFourMonthFirstDay(Date date) {
		int month = getMonth(date) / 4;
		month = month * 4; 
		return getDate(getYear(date),month,1 );
	}
	
	public static Date getFourMonthLastDay(Date date) {
		int month = getMonth(date) / 4;
		month = (month * 4) + 3; 
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}

	public static Date getHalfYearFirstDay(Date date) {
		int month = getMonth(date) / 6;
		month = month * 6; 
		return getDate(getYear(date),month,1 );
	}
	
	public static Date getHalfYearLastDay(Date date) {
		int month = getMonth(date) / 6;
		month = (month * 6) + 5; 
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}
	
	public static void main(String[] args) {
		Date date = getDate(2012, 7, 23);
		
		SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
		System.out.println( "Fecha ..: " + formatter.format(date));
		System.out.println( "Primer dia del año ..: " + formatter.format(getYearFirstDay(date)));
		System.out.println( "Último dia del año ..: " + formatter.format(getYearLastDay(date)));
		System.out.println();
		System.out.println( "Primer dia del semestre ..: " + formatter.format(getHalfYearFirstDay(date)));
		System.out.println( "Último dia del semestre ..: " + formatter.format(getHalfYearLastDay(date)));
		System.out.println();
		System.out.println( "Primer dia del cuatrimestre ..: " + formatter.format(getFourMonthFirstDay(date)));
		System.out.println( "Último dia del cuatrimestre ..: " + formatter.format(getFourMonthLastDay(date)));
		System.out.println();
		System.out.println( "Primer dia del trimestre ..: " + formatter.format(getQuarterFirstDay(date)));
		System.out.println( "Último dia del trimestre ..: " + formatter.format(getQuarterLastDay(date)));
		System.out.println();
		System.out.println( "Primer dia del bimestre ..: " + formatter.format(getBiMonthFirstDay(date)));
		System.out.println( "Último dia del bimestre ..: " + formatter.format(getBiMonthLastDay(date)));
		System.out.println();
		System.out.println( "Primer dia del mes ..: " + formatter.format(getMonthFirstDay(date)));
		System.out.println( "Último dia del mes ..: " + formatter.format(getMonthLastDay(date)));

		
		getYearLastDay(date);
	}
}
