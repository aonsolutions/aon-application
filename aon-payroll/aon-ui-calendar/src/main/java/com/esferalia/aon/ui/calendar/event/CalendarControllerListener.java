package com.esferalia.aon.ui.calendar.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.Contract;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class CalendarControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
	
	}
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController c = (CalendarController) getController();
		c.setEnterprise(new Enterprise());
		c.setWorkPlace(new WorkPlace());
		c.setWorkPlaces(null);
		c.setContract(new Contract());
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController c = (CalendarController) getController();
		GregorianCalendar cal= new GregorianCalendar();
		c.setYear(cal.get(Calendar.YEAR));	
		c.setSource(c.getTo().getSource());
		c.setSourceId(c.getTo().getSourceId());
		try {
			c.loadSource();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
		
		
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event){
		CalendarController c = (CalendarController) getController();
		c.initialize();
	}
	

}
