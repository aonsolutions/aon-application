package com.esferalia.aon.ui.calendar.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class CalendarControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
	
			CalendarController calendarController = (CalendarController)this.getController(); 
			Calendar calendar = (Calendar) calendarController.getTo();
			calendar.setAnualHours(0);

			calendar.setMonday(DayType.WORKING_DAY);
			calendar.setMondayHours(8);
			calendar.setTuesday(DayType.WORKING_DAY);
			calendar.setTuesdayHours(8);
			calendar.setWednesday(DayType.WORKING_DAY);
			calendar.setWednesdayHours(8);
			calendar.setThursday(DayType.WORKING_DAY);
			calendar.setThursdayHours(8);
			calendar.setFriday(DayType.WORKING_DAY);
			calendar.setFridayHours(8);
			calendar.setSaturday(DayType.NOT_WORKING_DAY);
			calendar.setSaturdayHours(0);
			calendar.setSunday(DayType.HOLIDAY);
			calendar.setSundayHours(0);
	}



}
