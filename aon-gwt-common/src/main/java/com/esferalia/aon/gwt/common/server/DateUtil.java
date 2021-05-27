package com.esferalia.aon.gwt.common.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.esferalia.aon.watson.util.AonMathUtils;

/**
 * @author ecastellano
 * @Deprecated use com.esferalia.aon.watson.server.AonDateUtils 
 */
@Deprecated
public class DateUtil {
	
	public static int getYear() {
		return getYear(new Date());
	}

	public static Date getYearLastDay(int year) {
		return getDate(year, 11, 31);
	}

	public static Date getYearLastDay(Date date) {
		return getDate(getYear(date), 11, 31);
	}

	public static Date getYearFirstDay(int year) {
		return getDate(year, 0, 1);
	}

	public static Date getYearFirstDay(Date date) {
		return getDate(getYear(date), 0, 1);
	}

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
	 * Devuelve el numero de dias que hay entre las fechas pasadas por
	 * parametro.
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
		r = AonMathUtils.round(r, 0);
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
	 * Devuelve el numero de dias del mes en curso indicado en la fecha.
	 * 
	 * @param date
	 *            Fecha de la que se desea saber el numero de dias del mes.
	 * @return int El numero de dias del mes.
	 */
	public static int daysInMonth(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = DateUtil.add(c1.getTime(), Calendar.MONTH, 1);
		return (int) getDaysBetweenDates(c1.getTime(), nextMonth);
	}

	public static boolean isLeapYear(int year) {
		GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
		return c.isLeapYear(year);
	}

	public static Date getDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}
	
	public static Date getMonthFirstDay(Date date) {
		return getDate(getYear(date),getMonth(date),1 );
	}

	public static Date getMonthLastDay(Date date) {
		return getDate(getYear(date),getMonth(date), daysInMonth(date) );
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
	
    public static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
	
}
