package com.esferalia.aon.ui.calendar.event;

import java.util.GregorianCalendar;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

/**
 * @author eagirrezabal
 *
 */
public class CalendarControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		Calendar c = (Calendar) getController().getTo();
		c.setAnualHours(0.0);
		c.setMonday(DayType.WORKING_DAY);
		c.setMondayHours(8.0);
		c.setTuesday(DayType.WORKING_DAY);
		c.setTuesdayHours(8.0);
		c.setWednesday(DayType.WORKING_DAY);
		c.setWednesdayHours(8.0);
		c.setThursday(DayType.WORKING_DAY);
		c.setThursdayHours(8.0);
		c.setFriday(DayType.WORKING_DAY);
		c.setFridayHours(8.0);
		c.setSaturday(DayType.NOT_WORKING_DAY);
		c.setSaturdayHours(0.0);
		c.setSunday(DayType.NOT_WORKING_DAY);
		c.setSundayHours(0.0);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController controller = (CalendarController) getController();
		GregorianCalendar cal= new GregorianCalendar();
		controller.setYear(cal.get(java.util.Calendar.YEAR));	
	}
	
}
