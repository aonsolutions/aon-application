package com.esferalia.aon.ui.calendar.event;

import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.calendar.enumeration.CalendarSource;
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
		c.setAnualHours(0);
		c.setMonday(DayType.WORKING_DAY);
		c.setMondayHours(8);
		c.setTuesday(DayType.WORKING_DAY);
		c.setTuesdayHours(8);
		c.setWednesday(DayType.WORKING_DAY);
		c.setWednesdayHours(8);
		c.setThursday(DayType.WORKING_DAY);
		c.setThursdayHours(8);
		c.setFriday(DayType.WORKING_DAY);
		c.setFridayHours(8);
		c.setSaturday(DayType.NOT_WORKING_DAY);
		c.setSaturdayHours(0);
		c.setSunday(DayType.NOT_WORKING_DAY);
		c.setSundayHours(0);
		
		CalendarController controller = (CalendarController) getController();
		if (controller.getSource() == CalendarSource.NONE){
			controller.setGeneric(true);
		} else {
			controller.setGeneric(false);
		}
		controller.setOwnCalendar(true);
		controller.setMasterCalendar(new Calendar());
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController controller = (CalendarController) getController();
		if(controller.getSource()==null || controller.getSource()==CalendarSource.NONE){
			try {
				controller.getCriteria().addEqualExpression(controller.getFieldName(ICalendarAlias.CALENDAR_GENERIC), true);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController controller = (CalendarController) getController();
		Calendar calendar = (Calendar) controller.getTo();
		GregorianCalendar cal= new GregorianCalendar();
		controller.setYear(cal.get(java.util.Calendar.YEAR));	
		try {
			controller.loadSource();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
		controller.setMasterCalendar(calendar.getCalendar());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController controller = (CalendarController) getController();
		Calendar c = (Calendar) controller.getTo();
		if(controller.isGeneric()){
			c.setGeneric(true);
			if(controller.getMasterCalendar()!=null && controller.getMasterCalendar().getId()!=null){
				c.setCalendar(controller.getMasterCalendar());
			}
		}
		if(controller.isOwnCalendar()){
			if(controller.getMasterCalendar()!=null && controller.getMasterCalendar().getId()!=null){
				c.setCalendar(controller.getMasterCalendar());
			}
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController controller = (CalendarController) getController();
		Calendar c = (Calendar) controller.getTo();
		if(!controller.isUsingExisting()){
			if(controller.getMasterCalendar()!=null && controller.getMasterCalendar().getId()!=null){
				c.setCalendar(controller.getMasterCalendar());
			}
		}
	}
	
	
	
}
