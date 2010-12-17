package com.esferalia.aon.ui.calendar.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class CalendarHolidayControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		LinesController controller = (LinesController) getController();
		Integer year = ((CalendarController)controller.getMasterController()).getYear();
		Calendar startCal = new GregorianCalendar();
		Calendar endCal = new GregorianCalendar();
		startCal.set(year, Calendar.JANUARY, 1);
		endCal.set(year, Calendar.DECEMBER, 31);
		try {
			controller.clearCriteria();
			controller.getCriteria().addBetweenExpression(this.getController().getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_DATE), startCal.getTime(), endCal.getTime());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
}
