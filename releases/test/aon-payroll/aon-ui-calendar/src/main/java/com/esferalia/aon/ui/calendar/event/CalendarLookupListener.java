package com.esferalia.aon.ui.calendar.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.calendar.controller.ICalendarConstants;

public class CalendarLookupListener extends ControllerAdapter {
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		CalendarController calendar = (CalendarController) FormUtil.getController(ICalendarConstants.CALENDAR_CONTROLLER_NAME);
		try {
			if(!calendar.isUsingExisting()){
				getController().getCriteria().addEqualExpression(calendar.getFieldName(ICalendarAlias.CALENDAR_GENERIC), true);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
}