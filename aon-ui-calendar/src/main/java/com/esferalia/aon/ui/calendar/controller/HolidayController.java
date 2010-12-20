package com.esferalia.aon.ui.calendar.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class HolidayController extends BasicController {
	
	private Integer year;
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public void onChangeYear(ActionEvent event){
		refreshLines();
	}
	
	private void refreshLines(){
		IController detail = (IController) AonUtil.getRegisteredBean(ICalendarConstants.HOLIDAY_DETAIL_CONTROLLER_NAME);
		detail.initializeModel();
	}

}
