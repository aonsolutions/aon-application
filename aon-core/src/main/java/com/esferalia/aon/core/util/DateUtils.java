package com.esferalia.aon.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;

public class DateUtils {

	public static Month getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return Month.getMonthByValue( c.get(Calendar.MONTH) );
	}
	
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	public static Date add(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, days);
		return c.getTime();
	}
	
	public static int getMonthDays(Month month, int year) {
		GregorianCalendar c = new GregorianCalendar();
		c.set(Calendar.DAY_OF_MONTH,1);
		c.set(Calendar.MONTH,month.getValue());
		c.set(Calendar.YEAR,year);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int validateRange(Date start, Date stop, Date startEdge,Date stopEdge) {
		if (start == null) {
			return 1; // start es requerido
		}
		if (stop != null && stop.before(start)) {
			return 2; // stop es menor que start
		}
        if (startEdge != null && start.before(startEdge)) {
        	return 3; // start fuera de rango por abajo.
        }
        if (stopEdge != null && start.after(stopEdge)) {
        	return 3; // start fuera de rango por arriba.
        }
        if (startEdge != null && stop.before(startEdge)) {
        	return 4; // stop fuera de rango por abajo.
        }
        if (stopEdge != null && stop.after(stopEdge)) {
        	return 4; // stop fuera de rango por arriba.
        }
		return 0;
	}

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

}
