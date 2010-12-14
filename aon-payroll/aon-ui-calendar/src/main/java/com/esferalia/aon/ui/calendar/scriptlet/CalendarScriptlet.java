package com.esferalia.aon.ui.calendar.scriptlet;

import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import java.util.*;

public class CalendarScriptlet extends JRDefaultScriptlet {
	
	
	public Integer dayOfWeek(String month, String year)
			throws JRScriptletException {
		Calendar cal = new GregorianCalendar();
		cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
		int num = cal.get(Calendar.DAY_OF_WEEK);
		return num;
	}

	public Integer monthDays(String month, String year)
			throws JRScriptletException {
		Calendar cal = new GregorianCalendar();
		cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		int num = cal.get(Calendar.DAY_OF_MONTH);
		return num;
	}
}