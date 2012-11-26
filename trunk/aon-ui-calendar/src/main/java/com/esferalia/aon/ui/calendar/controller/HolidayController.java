package com.esferalia.aon.ui.calendar.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class HolidayController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HolidayController.class.getName());
	
	private Integer year;
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public void onChangeYear(ActionEvent event){
		try {
			refreshLines();
		} catch (ManagerBeanException e) {
			String msg = "error on onChangeYear";
			LOGGER.error(msg);
			throw new AbortProcessingException(e);
		}
	}
	
	private void refreshLines() throws ManagerBeanException{
		IController detail = (IController) AonUtil.getRegisteredBean(ICalendarConstants.HOLIDAY_DETAIL_CONTROLLER_NAME);
		detail.initializeModel();
	}

}
