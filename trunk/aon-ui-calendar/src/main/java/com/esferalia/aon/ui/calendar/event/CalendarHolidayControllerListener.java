package com.esferalia.aon.ui.calendar.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class CalendarHolidayControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarHolidayControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		LinesController controller = (LinesController) getController();
		Integer year = ((CalendarController)controller.getMasterController()).getYear();
		Calendar startCal = new GregorianCalendar();
		Calendar endCal = new GregorianCalendar();
		startCal.set(year, Calendar.JANUARY, 1);
		endCal.set(year, Calendar.DECEMBER, 31);
		Integer masterId = ((com.esferalia.aon.calendar.Calendar)controller.getMasterController().getTo()).getId();
		try {
			controller.clearCriteria();
			controller.getCriteria().addEqualExpression(this.getController().getFieldName(IEntityAlias.CALENDAR_HOLIDAY_CALENDAR_ID), masterId);
			controller.getCriteria().addBetweenExpression(this.getController().getFieldName(IEntityAlias.CALENDAR_HOLIDAY_DATE), startCal.getTime(), endCal.getTime());
		} catch (ManagerBeanException e) {
			LOGGER.error("error on CalendarHolidayControllerListener");
		}
	}
	
}
