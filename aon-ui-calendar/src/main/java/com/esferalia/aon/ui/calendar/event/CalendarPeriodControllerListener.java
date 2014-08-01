package com.esferalia.aon.ui.calendar.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.calendar.controller.CalendarPeriodController;

public class CalendarPeriodControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
			CalendarPeriod calendar = (CalendarPeriod) this.getController().getTo();
			calendar.setMonday(DayType.WORKING_DAY);
			calendar.setTuesday(DayType.WORKING_DAY);
			calendar.setWednesday(DayType.WORKING_DAY);
			calendar.setThursday(DayType.WORKING_DAY);
			calendar.setFriday(DayType.WORKING_DAY);
			calendar.setSaturday(DayType.NOT_WORKING_DAY);
			calendar.setSunday(DayType.NOT_WORKING_DAY);
			calendar.setMondayHours(8.0);
			calendar.setTuesdayHours(8.0);
			calendar.setWednesdayHours(8.0);
			calendar.setThursdayHours(8.0);
			calendar.setFridayHours(8.0);
			calendar.setSaturdayHours(0.0);
			calendar.setSundayHours(0.0);
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		obtainYear();
	}
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		obtainYear();
	}
	
	private void obtainYear()
			throws ControllerListenerException{
		CalendarPeriodController controller = (CalendarPeriodController)getController();
		Integer year = ((CalendarController)controller.getMasterController()).getYear();
		if(year==null){
			String msg = "El año no puede ser nulo";
			throw new ControllerListenerException(msg);
		} else {
			controller.setYear(year);
		}
	}

}
