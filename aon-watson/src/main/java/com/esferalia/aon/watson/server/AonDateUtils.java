package com.esferalia.aon.watson.server;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonDateUtils {
	
    /**
     * Number of milliseconds in a standard second.
     * @since 2.1
     */
    public static final long MILLIS_PER_SECOND = 1000;
    /**
     * Number of milliseconds in a standard minute.
     * @since 2.1
     */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    /**
     * Number of milliseconds in a standard hour.
     * @since 2.1
     */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    /**
     * Number of milliseconds in a standard day.
     * @since 2.1
     */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    
    public static final String ORDER_DATE_FORMAT = "yyyyMMdd"; 
	public static final String SIMPLE_DATE_FORMAT = "dd/MM/yyyy";
	public static final String SIMPLE_DATE_FORMAT2 = "dd-MM-yyyy";
	public static final String SIMPLE_DATE_FORMAT3 = "yyyy/MM/dd";
	public static final String SIMPLE_DATE_FORMAT4 = "yyyy-MM-dd";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_TIME_FORMAT_AUX = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String DATE_TIME_FORMAT_AUX2 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	public static final String DATE_TIME_FORMAT_2 = "dd/MM/yyyy HH:mm:ss";
	public static final String DATE_TIME_FORMAT_3 = "yyyy-MM-dd HH:mm:ss";	
	
	public static final String TIME_FORMAT = "HH:mm";
	
	private static final int MODIFY_ROUND = 1;
	private static final int MODIFY_CEILING = 2;
	private static final int MODIFY_TRUNCATE = 0;
	private static final int SEMI_MONTH = 1001;
	private static final int[][] fields = { { Calendar.MILLISECOND },
			{ Calendar.SECOND }, { Calendar.MINUTE },
			{ Calendar.HOUR_OF_DAY, Calendar.HOUR },
			{ Calendar.DATE, Calendar.DAY_OF_MONTH, Calendar.AM_PM },
			{ Calendar.MONTH, SEMI_MONTH }, { Calendar.YEAR },
			{ Calendar.ERA } };
	
	private AonDateUtils() {
		
	}

	/**
	 * Comprueba si las fecha pasadas por parámetros son el mismo dia. Si
	 * cualquiera de las dos es NULL, devuelve false.
	 * 
	 * @param date1
	 *            Primera fecha a comparar
	 * @param date2
	 *            Segunda fecha a comparar
	 * @return true si son el mismo dia, false en otro caso o alguna (o las dos)
	 *         fechas es NULL.
	 * @see AonDateUtils.isSameDay(Calendar cal1, Calendar cal2)s
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}
	public static boolean isNotSameDay(Date date1, Date date2) {
		return !isSameDay(date1, date2);
	}
	
	public static boolean isToday(Date date) {
		return isSameDay(date, new Date());
	}
	public static boolean isNotToday(Date date) {
		return !isToday(date);
	}
	public static boolean isLessThanToday(Date date) {
		return compare(date, new Date()) < 0 && isNotToday(date);
	}
	public static boolean isMoreThanToday(Date date) {
		return compare(date, new Date()) > 0 && isNotToday(date);
	}
	public static Date todayIfNull( Date date) {
		return date==null?new Date():date;
	}
	/**
	 * Comprueba si las fecha pasadas por parámetros son el mismo dia. Si
	 * cualquiera de las dos es NULL, devuelve false.
	 * 
	 * @param date1
	 *            Primera fecha a comparar
	 * @param date2
	 *            Segunda fecha a comparar
	 * @return true si son el mismo dia, false en otro caso o alguna (o las dos)
	 *         fechas es NULL.
	 * @see AonDateUtils.isSameDay(Calendar cal1, Calendar cal2)s
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2
						.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * @param Convierte
	 *            la fecha pasada por parámetro en un objeto java.sql.Date.
	 * @return El objeto java.sql.Date correspondiente. Si la fecha es NULL,
	 *         devuelve NULL.
	 */
	public static java.sql.Date toSql(Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}
	
	/**
	 * @param Convierte
	 *            la fecha pasada por parámetro en un objeto java.sql.Date.
	 * @return El objeto java.sql.Date correspondiente. Si la fecha es NULL,
	 *         devuelve NULL.
	 */
	public static Timestamp toTimestamp(Date date) {
		return date == null ? null : new Timestamp(date.getTime());
	}

	/**
	 * Devuelve un objecto java.sql.Date para los parámetros indicados.
	 * 
	 * @param year
	 *            El año de la fecha.
	 * @param month
	 *            El mes de la fecha. (0-11).
	 * @param day
	 *            El dia de la fecha.
	 * @return
	 */
	public static java.sql.Date getSqlDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		Date date = c.getTime();
		date = truncate(date, Calendar.DAY_OF_MONTH);
		return new java.sql.Date(date.getTime());
	}

	/**
	 * Devuelve un objecto java.sql.Date para los parámetros indicados.
	 * 
	 * @param year
	 *            El año de la fecha.
	 * @param month
	 *            El mes de la fecha. (0-11).
	 * @param day
	 *            El dia de la fecha.
	 * @return
	 */
	public static Date getDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		Date date = c.getTime();
		return truncate(date, Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Devuelve un objecto java.sql.Date para los parámetros indicados sin hora.
	 * 
	 * @param date
	 *            El año de la fecha.
	 * @return
	 */
	public static Date getDateWithoutTime(Date date) {
		if(date == null) return null;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, getYear(date));
		c.set(Calendar.MONTH, getMonth(date));
		c.set(Calendar.DAY_OF_MONTH, getDay(date));
		return truncate(c.getTime(), Calendar.DAY_OF_MONTH);
	}

	/**
	 * <p>
	 * Truncate this date, leaving the field specified as the most significant
	 * field.
	 * </p>
	 *
	 * <p>
	 * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you
	 * passed with HOUR, it would return 28 Mar 2002 13:00:00.000. If this was
	 * passed with MONTH, it would return 1 Mar 2002 0:00:00.000.
	 * </p>
	 * 
	 * @param date
	 *            the date to work with
	 * @param field
	 *            the field from <code>Calendar</code> or
	 *            <code>SEMI_MONTH</code>
	 * @return the rounded date
	 * @throws IllegalArgumentException
	 *             if the date is <code>null</code>
	 * @throws ArithmeticException
	 *             if the year is over 280 million
	 */
	public static Date truncate(Date date, int field) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar gval = Calendar.getInstance();
		gval.setTime(date);
		modify(gval, field, MODIFY_TRUNCATE);
		return gval.getTime();
	}

	private static void modify(Calendar val, int field, int modType) {
		if (val.get(Calendar.YEAR) > 280000000) {
			throw new ArithmeticException(
					"Calendar value too large for accurate calculations");
		}

		if (field == Calendar.MILLISECOND) {
			return;
		}

		// ----------------- Fix for LANG-59 ---------------------- START
		// ---------------
		// see http://issues.apache.org/jira/browse/LANG-59
		//
		// Manually truncate milliseconds, seconds and minutes, rather than
		// using
		// Calendar methods.

		Date date = val.getTime();
		long time = date.getTime();
		boolean done = false;

		// truncate milliseconds
		int millisecs = val.get(Calendar.MILLISECOND);
		if (MODIFY_TRUNCATE == modType || millisecs < 500) {
			time = time - millisecs;
		}
		if (field == Calendar.SECOND) {
			done = true;
		}

		// truncate seconds
		int seconds = val.get(Calendar.SECOND);
		if (!done && (MODIFY_TRUNCATE == modType || seconds < 30)) {
			time = time - (seconds * 1000L);
		}
		if (field == Calendar.MINUTE) {
			done = true;
		}

		// truncate minutes
		int minutes = val.get(Calendar.MINUTE);
		if (!done && (MODIFY_TRUNCATE == modType || minutes < 30)) {
			time = time - (minutes * 60000L);
		}

		// reset time
		if (date.getTime() != time) {
			date.setTime(time);
			val.setTime(date);
		}
		// ----------------- Fix for LANG-59 ----------------------- END
		// ----------------

		boolean roundUp = false;
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields[i].length; j++) {
				if (fields[i][j] == field) {
					// This is our field... we stop looping
					if (modType == MODIFY_CEILING
							|| (modType == MODIFY_ROUND && roundUp)) {
						if (field == SEMI_MONTH) {
							// This is a special case that's hard to generalize
							// If the date is 1, we round up to 16, otherwise
							// we subtract 15 days and add 1 month
							if (val.get(Calendar.DATE) == 1) {
								val.add(Calendar.DATE, 15);
							} else {
								val.add(Calendar.DATE, -15);
								val.add(Calendar.MONTH, 1);
							}
							// ----------------- Fix for LANG-440
							// ---------------------- START ---------------
						} else if (field == Calendar.AM_PM) {
							// This is a special case
							// If the time is 0, we round up to 12, otherwise
							// we subtract 12 hours and add 1 day
							if (val.get(Calendar.HOUR_OF_DAY) == 0) {
								val.add(Calendar.HOUR_OF_DAY, 12);
							} else {
								val.add(Calendar.HOUR_OF_DAY, -12);
								val.add(Calendar.DATE, 1);
							}
							// ----------------- Fix for LANG-440
							// ---------------------- END ---------------
						} else {
							// We need at add one to this field since the
							// last number causes us to round up
							val.add(fields[i][0], 1);
						}
					}
					return;
				}
			}
			// We have various fields that are not easy roundings
			int offset = 0;
			boolean offsetSet = false;
			// These are special types of fields that require different rounding
			// rules
			switch (field) {
			case SEMI_MONTH:
				if (fields[i][0] == Calendar.DATE) {
					// If we're going to drop the DATE field's value,
					// we want to do this our own way.
					// We need to subtrace 1 since the date has a minimum of 1
					offset = val.get(Calendar.DATE) - 1;
					// If we're above 15 days adjustment, that means we're in
					// the
					// bottom half of the month and should stay accordingly.
					if (offset >= 15) {
						offset -= 15;
					}
					// Record whether we're in the top or bottom half of that
					// range
					roundUp = offset > 7;
					offsetSet = true;
				}
				break;
			case Calendar.AM_PM:
				if (fields[i][0] == Calendar.HOUR_OF_DAY) {
					// If we're going to drop the HOUR field's value,
					// we want to do this our own way.
					offset = val.get(Calendar.HOUR_OF_DAY);
					if (offset >= 12) {
						offset -= 12;
					}
					roundUp = offset >= 6;
					offsetSet = true;
				}
				break;
			}
			if (!offsetSet) {
				int min = val.getActualMinimum(fields[i][0]);
				int max = val.getActualMaximum(fields[i][0]);
				// Calculate the offset from the minimum allowed value
				offset = val.get(fields[i][0]) - min;
				// Set roundUp if this is more than half way between the minimum
				// and maximum
				roundUp = offset > ((max - min) / 2);
			}
			// We need to remove this field
			if (offset != 0) {
				val.set(fields[i][0], val.get(fields[i][0]) - offset);
			}
		}
		throw new IllegalArgumentException(
				"The field " + field + " is not supported");

	}

	/**
	 * Devuelve el ultimo dia del año en funcion de año pasado por parametro.
	 * 
	 * @param year
	 *            El año del que se desea el ultimo dia.
	 * 
	 * @return Un java.util.Date con el ultimo dia de ese año.
	 */
	public static Date getYearLastDay(int year) {
		return getDate(year, 11, 31);
	}

	/**
	 * Devuelve el ultimo dia del año en funcion de la fecha pasada por
	 * parametro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el ultimo dia del año.
	 * 
	 * @return Un java.util.Date con el ultimo dia de ese año.
	 */
	public static Date getYearLastDay(Date date) {
		return getDate(getYear(date), 11, 31);
	}

	/**
	 * Devuelve el primer dia del año en funcion de año pasado por parametro.
	 * 
	 * @param year
	 *            El año del que se desea el primer dia.
	 * 
	 * @return Un java.util.Date con el primer dia de ese año.
	 */
	public static Date getYearFirstDay(int year) {
		return getDate(year, 0, 1);
	}

	/**
	 * Devuelve el primer dia del año en funcion de año pasado por parametro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del año.
	 * 
	 * @return Un java.util.Date con el primer dia de ese año.
	 */
	public static Date getYearFirstDay(Date date) {
		return getDate(getYear(date), 0, 1);
	}

	/**
	 * Devuelve el numero de dias que hay entre las fechas pasadas por
	 * parametro.
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
		cal.add(Calendar.DAY_OF_YEAR,
				(cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) * (-1));
		Date start = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, +6);
		Date end = cal.getTime();
		return new Date[] { start, end };
	}

	public static Date[] getTwoWeekDateRange(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR,
				(cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) * (-1));
		Date start = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, +13);
		Date end = cal.getTime();
		return new Date[] { start, end };
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
	public static long getDaysBetweenDates(Date from, Date to,
			boolean checkDates) {
		if (from == null) {
			throw new IllegalArgumentException(
					"Date 'from' value can not be null.");
		}
		if (to == null) {
			throw new IllegalArgumentException(
					"Date 'to' value can not be null.");
		}
		if (checkDates && to.before(from)) {
			throw new IllegalArgumentException(
					"Date 'to' can not be earlier than date 'from'.");
		}
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(from);
		c2.setTime(to);
		double r = (double) (c2.getTimeInMillis() - c1.getTimeInMillis())
				/ (double) (24 * 3600 * 1000);
		r = AonMathUtils.round(r, 0);
		return (long) r;
	}

	/**
	 * Devuelve el a�o actual.
	 * 
	 * @return El a�o actual.
	 */
	public static int getCurrentYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR);
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
	 * Devuelve el dia de la semana de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber el dia de la semana.
	 * @return El dia de la semana.
	 */
	public static int getDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Devuelve el dia del a�o de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber el dia del a�o.
	 * @return El dia del a�o.
	 */
	public static int getDayOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_YEAR);
	}

	
	/**
	 * Devuelve la hora del dia de la fecha indicada.
	 * 
	 * @param date
	 *            La fecha de la que se desea saber la hora del dia.
	 * @return La hora del dia.
	 */
    public static Integer getHour(Date date){
    	if(date == null) return null;
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	return c.get(Calendar.HOUR_OF_DAY);
    }
    
    public static Integer getMinute(Date date){
    	if(date == null) return null;
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	return c.get(Calendar.MINUTE);
    }
    
    public static Integer getSecond(Date date){
    	if(date == null) return null;
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	return c.get(Calendar.SECOND);
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
		Date nextMonth = AonDateUtils.addMonths(c1.getTime(), 1);
		return (int) getDaysBetweenDates(c1.getTime(), nextMonth);
	}

	/**
	 * Si el año indicado por parametro es bisiesto.
	 * 
	 * @param year
	 *            Año.
	 * @return boolean TRUE si es bisiesto.
	 */
	public static boolean isLeapYear(int year) {
		GregorianCalendar c = (GregorianCalendar) GregorianCalendar
				.getInstance();
		return c.isLeapYear(year);
	}

	/**
	 * Devuelve el primer dia del mes de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del mes.
	 * 
	 * @return Un java.util.Date con el primer dia de ese mes.
	 */
	public static Date getMonthFirstDay(Date date) {
		return getDate(getYear(date), getMonth(date), 1);
	}

	/**
	 * Devuelve el ultimo dia del mes en funcion de la fecha pasada por
	 * parametro.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el ultimo dia del mes.
	 * 
	 * @return Un java.util.Date con el ultimo dia de ese mes.
	 */
	public static Date getMonthLastDay(Date date) {
		return getDate(getYear(date), getMonth(date),
				AonDateUtils.daysInMonth(date));
	}

	/**
	 * Devuelve el primer dia del bimestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del
	 *            bimestre.
	 * 
	 * @return Un java.util.Date con el primer dia de ese bimestre.
	 */
	public static Date getBiMonthFirstDay(Date date) {
		int month = getMonth(date) / 2;
		month = month * 2;
		return getDate(getYear(date), month, 1);
	}

	/**
	 * Devuelve el último dia del bimestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el último dia del
	 *            bimestre.
	 * 
	 * @return Un java.util.Date con el último dia de ese bimestre.
	 */
	public static Date getBiMonthLastDay(Date date) {
		int month = (getMonth(date) / 2);
		month = (month * 2) + 1;
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}

	/**
	 * Devuelve el primer dia del trimestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del
	 *            trimestre.
	 * 
	 * @return Un java.util.Date con el primer dia de ese trimestre.
	 */
	public static Date getQuarterFirstDay(Date date) {
		int month = getMonth(date) / 3;
		month = month * 3;
		return getDate(getYear(date), month, 1);
	}

	/**
	 * Devuelve el último dia del trimestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el último dia del
	 *            trimestre.
	 * 
	 * @return Un java.util.Date con el último dia de ese trimestre.
	 */
	public static Date getQuarterLastDay(Date date) {
		int month = getMonth(date) / 3;
		month = (month * 3) + 2;
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}

	/**
	 * Devuelve el primer dia del cuatrimestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del
	 *            cuatrimestre.
	 * 
	 * @return Un java.util.Date con el primer dia de ese cuatrimestre.
	 */
	public static Date getFourMonthFirstDay(Date date) {
		int month = getMonth(date) / 4;
		month = month * 4;
		return getDate(getYear(date), month, 1);
	}

	/**
	 * Devuelve el último dia del cuatrimestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el último dia del
	 *            cuatrimestre.
	 * 
	 * @return Un java.util.Date con el último dia de ese cuatrimestre.
	 */
	public static Date getFourMonthLastDay(Date date) {
		int month = getMonth(date) / 4;
		month = (month * 4) + 3;
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}

	/**
	 * Devuelve el primer dia del semestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el primer dia del
	 *            semestre.
	 * 
	 * @return Un java.util.Date con el primer dia de ese semestre.
	 */
	public static Date getHalfYearFirstDay(Date date) {
		int month = getMonth(date) / 6;
		month = month * 6;
		return getDate(getYear(date), month, 1);
	}

	/**
	 * Devuelve el último dia del semestre de la fecha pasada.
	 * 
	 * @param date
	 *            La fecha de la que se se desea saber el último dia del
	 *            semestre.
	 * 
	 * @return Un java.util.Date con el último dia de ese semestre.
	 */
	public static Date getHalfYearLastDay(Date date) {
		int month = getMonth(date) / 6;
		month = (month * 6) + 5;
		Date tmp = getDate(getYear(date), month, getDay(date));
		return getMonthLastDay(tmp);
	}
	
	/**
	 * 
	 * @param date
	 * 				La fecha de la se desea saber el primer dia de la semana
	 * @return Un java.util.Date con el primer dia de la semana
	 */

	public static Date getFirstDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.set(Calendar.DAY_OF_YEAR, --day);
			day = calendar.get(Calendar.DAY_OF_YEAR);
		}
		return calendar.getTime();
	}
	
	/**
	 * 
	 * @param date
	 * 				La fecha de la que se desea saber el ultimo dia de la semana
	 * @return Un java.util.Date con ultimo dia de la semana.
	 */
	
	public static Date getLastDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			calendar.set(Calendar.DAY_OF_YEAR, ++day);
		}
		return calendar.getTime();
	}
	
    //-----------------------------------------------------------------------
    /**
     * Sets the years field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setYears(final Date date, final int amount) {
        return set(date, Calendar.YEAR, amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the months field to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new {@code Date} set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setMonths(final Date date, final int amount) {
        return set(date, Calendar.MONTH, amount);
    }

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of years to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addYears(Date date, int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of months to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMonths(Date date, int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of weeks to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addWeeks(Date date, int amount) {
		return add(date, Calendar.WEEK_OF_YEAR, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of days to a date returning a new object. The original date
	 * object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addDays(Date date, int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of hours to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addHours(Date date, int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of minutes to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMinutes(Date date, int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of seconds to a date returning a new object. The original
	 * date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addSeconds(Date date, int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds a number of milliseconds to a date returning a new object. The
	 * original date object is unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date addMilliseconds(Date date, int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	// -----------------------------------------------------------------------
	/**
	 * Adds to a date returning a new object. The original date object is
	 * unchanged.
	 *
	 * @param date
	 *            the date, not null
	 * @param calendarField
	 *            the calendar field to add to
	 * @param amount
	 *            the amount to add, may be negative
	 * @return the new date object with the amount added
	 * @throws IllegalArgumentException
	 *             if the date is null
	 */
	public static Date add(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     * 
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     * The parser parses strictly - it does not allow for dates such as "February 942, 1996". 
     * 
     * @param str  the date to parse, not null
     * @param parsePatterns  the date format patterns to use, see SimpleDateFormat, not null
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException if none of the date patterns were suitable
     * @since 2.5
     */
    public static Date parseDateStrictly(String str, String[] parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, parsePatterns, false);
    }

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     * 
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     * 
     * @param str  the date to parse, not null
     * @param parsePatterns  the date format patterns to use, see SimpleDateFormat, not null
     * @param lenient Specify whether or not date/time parsing is to be lenient.
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException if none of the date patterns were suitable
     * @see java.util.Calender#isLenient()
     */
    private static Date parseDateWithLeniency(String str, String[] parsePatterns,
            boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        
        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(lenient);
        ParsePosition pos = new ParsePosition(0);
        for (int i = 0; i < parsePatterns.length; i++) {

            String pattern = parsePatterns[i];

            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (parsePatterns[i].endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            
            parser.applyPattern(pattern);
            pos.setIndex(0);

            String str2 = str;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (parsePatterns[i].endsWith("ZZ")) {
                int signIdx  = indexOfSignChars(str2, 0);
                while (signIdx >=0) {
                    str2 = reformatTimezone(str2, signIdx);
                    signIdx = indexOfSignChars(str2, ++signIdx);
                }
            }

            Date date = parser.parse(str2, pos);
            if (date != null && pos.getIndex() == str2.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }
    
    /**
     * Index of sign charaters (i.e. '+' or '-').
     * 
     * @param str The string to search
     * @param startPos The start position
     * @return the index of the first sign character or -1 if not found
     */
    private static int indexOfSignChars(String str, int startPos) {
        int idx = AonStringUtils.indexOf(str, '+', startPos);
        if (idx < 0) {
            idx = AonStringUtils.indexOf(str, '-', startPos);
        }
        return idx;
    }

    /**
     * Reformat the timezone in a date string.
     *
     * @param str The input string
     * @param signIdx The index position of the sign characters
     * @return The reformatted string
     */
    private static String reformatTimezone(String str, int signIdx) {
        String str2 = str;
        if (signIdx >= 0 &&
            signIdx + 5 < str.length() &&
            Character.isDigit(str.charAt(signIdx + 1)) &&
            Character.isDigit(str.charAt(signIdx + 2)) &&
            str.charAt(signIdx + 3) == ':' &&
            Character.isDigit(str.charAt(signIdx + 4)) &&
            Character.isDigit(str.charAt(signIdx + 5))) {
            str2 = str.substring(0, signIdx + 3) + str.substring(signIdx + 4);
        }
        return str2;
    }

	public static String format(Date date, String pattern) {
		if (date == null) return null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public static Date parse(String date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return parse(date, format);
	}
	
	public static Date parse(String date, SimpleDateFormat format) {
		try {
			return date == null ? null : format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static LocalDateTime parseLocalDateTime(String date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return parseLocalDateTime(date, formatter);
	}
	
	public static LocalDateTime parseLocalDateTime(String date, DateTimeFormatter formatter) {
		try {
			return date == null ? null :  LocalDateTime.parse(date, formatter);
		} catch (Exception e) {
			return null;
		}
	}
    
	public static String simpleFormat(Date date) {
		return date == null ? null : format(date, SIMPLE_DATE_FORMAT);
	}

	public static LocalDateTime parseLocalDateTime(String date) {
		if(date == null) return null;
		LocalDateTime d = localDateTimeParse(date);
		return d;
	}
	
	public static Date parse(String date) {
		if(date == null) return null;
		Date d = dateTimeParse(date);
		if(d == null) d = simpleParse(date);
		return d;
	}
	
	public static Date simpleParse(String date) {
		if(AonStringUtils.isBlank(date)) return null;
		date = date.replace(" ", "");
		Date d = null;
		if(isSimpleDateFormat(date)) d = parse(date, SIMPLE_DATE_FORMAT);
		if(d == null && isSimpleDateFormat2(date)) d = parse(date, SIMPLE_DATE_FORMAT2);
		if(d == null && isSimpleDateFormat3(date)) d = parse(date, SIMPLE_DATE_FORMAT3);
		if(d == null && isSimpleDateFormat4(date)) d = parse(date, SIMPLE_DATE_FORMAT4);
		return d;
	}
	
	public static boolean isSimpleDateFormat(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("/") && str.indexOf("/") == 2;
	}
	
	public static boolean isSimpleDateFormat2(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("-") && str.indexOf("-") == 2;
	}

	public static boolean isSimpleDateFormat3(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("/") && str.indexOf("/") == 4;
	}
	
	public static boolean isSimpleDateFormat4(String str) {
		return !AonStringUtils.isBlank(str) && str.length() == 10 && str.contains("-") && str.indexOf("-") == 4;
	}

	public static String orderFormat(Date date) {
		return date == null ? null : format(date, ORDER_DATE_FORMAT);
	}
	public static Date orderParse(String date) {
		return date == null ? null : parse(date, ORDER_DATE_FORMAT);	
	}
	
	public static String dateTimeFormat(Date date) {
		return date == null ? null : format(date, DATE_TIME_FORMAT);
	}
	
	public static Date dateTimeParse(String date) {
		if(date == null) return null;
		Date d = parse(date, DATE_TIME_FORMAT);
		if(d == null) d = parse(date, DATE_TIME_FORMAT_AUX);
		if(d == null) d = parse(date, DATE_TIME_FORMAT_2);
		if(d == null) d = parse(date, DATE_TIME_FORMAT_AUX2);
		if(d == null) d = parse(date, DATE_TIME_FORMAT_3);
		return d;
	}
	
	public static LocalDateTime localDateTimeParse(String date) {
		if(date == null) return null;
		LocalDateTime d = parseLocalDateTime(date, DATE_TIME_FORMAT);
		if(d == null) d = parseLocalDateTime(date, DATE_TIME_FORMAT_AUX);
		if(d == null) d = parseLocalDateTime(date, DATE_TIME_FORMAT_2);
		if(d == null) d = parseLocalDateTime(date, DATE_TIME_FORMAT_AUX2);
		if(d == null) d = parseLocalDateTime(date, DATE_TIME_FORMAT_3);
		return d;
	}
	
	
	public static String timeFormat(Date date) {
		return date == null ? null : format(date, TIME_FORMAT);
	}
	public static Date timeParse(String date) {
		return date == null ? null : parse(date, TIME_FORMAT);
	}
	
    /**
     * Sets the day of month field to a date returning a new object.
     * The original date object is unchanged.
     *
     * @param date  the date, not null
     * @param amount the amount to set
     * @return a new Date object set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    public static Date setDays(Date date, int amount) {
        return set(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Sets the specified field to a date returning a new object.  
     * This does not use a lenient calendar.
     * The original date object is unchanged.
     *
     * @param date  the date, not null
     * @param calendarField  the calendar field to set the amount to
     * @param amount the amount to set
     * @return a new Date object set with the specified value
     * @throws IllegalArgumentException if the date is null
     * @since 2.4
     */
    private static Date set(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        // getInstance() returns a new object, so this method is thread safe.
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }   
    
    /**
     * <p>Returns the number of days within the 
     * fragment. All datefields greater than the fragment will be ignored.</p> 
     * 
     * <p>Asking the days of any date will only return the number of days
     * of the current month (resulting in a number between 1 and 31). This 
     * method will retrieve the number of days for any fragment. 
     * For example, if you want to calculate the number of days past this year, 
     * your fragment is Calendar.YEAR. The result will be all days of the 
     * past month(s).</p> 
     * 
     * <p>Valid fragments are: Calendar.YEAR, Calendar.MONTH, both 
     * Calendar.DAY_OF_YEAR and Calendar.DATE, Calendar.HOUR_OF_DAY, 
     * Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND
     * A fragment less than or equal to a DAY field will return 0.</p> 
     *  
     * <p>
     * <ul>
     *  <li>January 28, 2008 with Calendar.MONTH as fragment will return 28
     *   (equivalent to deprecated date.getDay())</li>
     *  <li>February 28, 2008 with Calendar.MONTH as fragment will return 28
     *   (equivalent to deprecated date.getDay())</li>
     *  <li>January 28, 2008 with Calendar.YEAR as fragment will return 28</li>
     *  <li>February 28, 2008 with Calendar.YEAR as fragment will return 59</li>
     *  <li>January 28, 2008 with Calendar.MILLISECOND as fragment will return 0
     *   (a millisecond cannot be split in days)</li>
     * </ul>
     * </p>
     * 
     * @param date the date to work with, not null
     * @param fragment the Calendar field part of date to calculate 
     * @return number of days  within the fragment of date
     * @throws IllegalArgumentException if the date is <code>null</code> or 
     * fragment is not supported
     * @since 2.4
     */
    public static long getFragmentInDays(Date date, int fragment) {
        return getFragment(date, fragment, Calendar.DAY_OF_YEAR);
    }
	
    /**
     * Date-version for fragment-calculation in any unit
     * 
     * @param date the date to work with, not null
     * @param fragment the Calendar field part of date to calculate 
     * @param unit Calendar field defining the unit
     * @return number of units within the fragment of the date
     * @throws IllegalArgumentException if the date is <code>null</code> or 
     * fragment is not supported
     * @since 2.4
     */
    private static long getFragment(Date date, int fragment, int unit) {
        if(date == null) {
            throw  new IllegalArgumentException("The date must not be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getFragment(calendar, fragment, unit);
    }

    /**
     * Calendar-version for fragment-calculation in any unit
     * 
     * @param calendar the calendar to work with, not null
     * @param fragment the Calendar field part of calendar to calculate 
     * @param unit Calendar field defining the unit
     * @return number of units within the fragment of the calendar
     * @throws IllegalArgumentException if the date is <code>null</code> or 
     * fragment is not supported
     * @since 2.4
     */
    private static long getFragment(Calendar calendar, int fragment, int unit) {
        if(calendar == null) {
            throw  new IllegalArgumentException("The date must not be null"); 
        }
        long millisPerUnit = getMillisPerUnit(unit);
        long result = 0;
        
        // Fragments bigger than a day require a breakdown to days
        switch (fragment) {
            case Calendar.YEAR:
                result += (calendar.get(Calendar.DAY_OF_YEAR) * MILLIS_PER_DAY) / millisPerUnit;
                break;
            case Calendar.MONTH:
                result += (calendar.get(Calendar.DAY_OF_MONTH) * MILLIS_PER_DAY) / millisPerUnit;
                break;
        }

        switch (fragment) {
            // Number of days already calculated for these cases
            case Calendar.YEAR:
            case Calendar.MONTH:
            
            // The rest of the valid cases
            case Calendar.DAY_OF_YEAR:
            case Calendar.DATE:
                result += (calendar.get(Calendar.HOUR_OF_DAY) * MILLIS_PER_HOUR) / millisPerUnit;
                //$FALL-THROUGH$
            case Calendar.HOUR_OF_DAY:
                result += (calendar.get(Calendar.MINUTE) * MILLIS_PER_MINUTE) / millisPerUnit;
                //$FALL-THROUGH$
            case Calendar.MINUTE:
                result += (calendar.get(Calendar.SECOND) * MILLIS_PER_SECOND) / millisPerUnit;
                //$FALL-THROUGH$
            case Calendar.SECOND:
                result += (calendar.get(Calendar.MILLISECOND) * 1) / millisPerUnit;
                break;
            case Calendar.MILLISECOND: 
                break;//never useful
            default: 
                throw new IllegalArgumentException("The fragment " + fragment + " is not supported");
        }
        return result;
    }
    
    /**
     * Returns the number of millis of a datefield, if this is a constant value
     * 
     * @param unit A Calendar field which is a valid unit for a fragment
     * @return number of millis
     * @throws IllegalArgumentException if date can't be represented in millisenconds
     * @since 2.4 
     */
    private static long getMillisPerUnit(int unit) {
        long result = Long.MAX_VALUE;
        switch (unit) {
            case Calendar.DAY_OF_YEAR:
            case Calendar.DATE:
                result = MILLIS_PER_DAY;
                break;
            case Calendar.HOUR_OF_DAY:
                result = MILLIS_PER_HOUR;
                break;
            case Calendar.MINUTE:
                result = MILLIS_PER_MINUTE;
                break;
            case Calendar.SECOND:
                result = MILLIS_PER_SECOND;
                break;
            case Calendar.MILLISECOND:
                result = 1;
                break;
            default: throw new IllegalArgumentException("The unit " + unit + " cannot be represented is milleseconds");
        }
        return result;
    }
    
	public static int compare(Date a, Date b) {
		if (a == null) {
			return b == null ? 0 : 1;
		}
		return b == null ? -1 : a.compareTo(b);
	}

	public static boolean isAfter(Date first, Date second) {
		if (first == null) return false;
		if (second == null) return false;
		return first.after(second);
	}
	
	public static boolean isBefore(Date first, Date second) {
		if (first == null) return false;
		if (second == null) return false;
		return first.before(second);
	}

	public static Date today() {
		return Date.from(
			LocalDate
				.now()
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant()
		);
	}
	
	public static Date yesterday() {
		return Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	public static Date tomorrow() {
		return Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
     * Verifica si value est� entre start y end (inclusive).
     * Si value, start o end son null -> false.
     */
    public static boolean isBetween(Date value, Date start, Date end) {
        if (value == null || start == null || end == null) return false;
        return !value.before(start) && !value.after(end);
    }

    /**
     * Verifica si target est� entre start y end (inclusive).
     * Si target es null -> false.
     * Si start o end son null -> se ignora ese l�mite.
     */
    public static boolean isInRange(Date value, Date start, Date end) {
        if (value == null) return false;
        boolean afterStart = (start == null) || !value.before(start);
        boolean beforeEnd  = (end == null)   || !value.after(end);
        return afterStart && beforeEnd;
    }
        
	public static Date previousDay(Date endDate) {
	    if (endDate == null) return null;
	    Date date = new java.util.Date(endDate.getTime());
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDate localDate = instant.atZone(zone).toLocalDate().minusDays(1);
	    return Date.from(localDate.atStartOfDay(zone).toInstant());
	}
	
	public static Date nextDay(Date endDate) {
	    if (endDate == null) return null;
	    Date date = new java.util.Date(endDate.getTime());
	    Instant instant = date.toInstant();
	    ZoneId zone = ZoneId.systemDefault();
	    LocalDate localDate = instant.atZone(zone).toLocalDate().plusDays(1);
	    return Date.from(localDate.atStartOfDay(zone).toInstant());
	}

	public static Date setTimeToZero(Date date) {
		return truncate(date, Calendar.DAY_OF_MONTH);		
	}

	public static Date setTimeToEndOfDay(Date date) {
		return Date.from(
		    date.toInstant()
		        .atZone(ZoneId.systemDefault())
		        .toLocalDate()
		        .atTime(LocalTime.MAX)
		        .atZone(ZoneId.systemDefault())
		        .toInstant()
			);
	}
}
