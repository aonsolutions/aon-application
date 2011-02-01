package com.esferalia.aon.ui.calendar.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.CalendarPeriod;

public class CalendarPeriodController extends LinesController {
	
	Integer year;
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	/*
	 * ACTION LISTENERS
	 */
	public void onMonthChanged(ActionEvent event){
		CalendarPeriod period = (CalendarPeriod)getTo();
		Calendar cal = new GregorianCalendar(getYear(), period.getMonth().getValue(), 1);
		cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		((CalendarCollections)AonUtil.getRegisteredBean(ICalendarConstants.COLLECTIONS_CONTROLLER_NAME)).setMonthMaxDays(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		period.setEndDay(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	}	
}
