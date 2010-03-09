package com.esferalia.aon.core.util;

import java.util.Calendar;
import java.util.Date;

import com.code.aon.common.enumeration.Month;

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

}
