package com.esferalia.aon.ui.calendar.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.ui.calendar.controller.HolidayController;

public class HolidayControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		HolidayController c = (HolidayController) getController();
		GregorianCalendar cal= new GregorianCalendar();
		c.setYear(cal.get(Calendar.YEAR));	
	}

}
