package com.esferalia.aon.ui.calendar.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.faces.event.ActionEvent;

import com.code.aon.common.enumeration.Month;
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
		Month month = ((CalendarPeriod)getTo()).getMonth();
		if(month==null){
			((CalendarPeriod)((LinesController)AonUtil.getRegisteredBean("calendarPeriod")).getTo()).setStartDay(null);
			((CalendarPeriod)((LinesController)AonUtil.getRegisteredBean("calendarPeriod")).getTo()).setEndDay(null);
		} else {
			Calendar cal = new GregorianCalendar(getYear(), month.getValue(), 1);
			cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			((CalendarCollections)AonUtil.getRegisteredBean("calendarCollections")).setMonthMaxDays(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			((CalendarPeriod)((LinesController)AonUtil.getRegisteredBean("calendarPeriod")).getTo()).setEndDay(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
	}	
}
