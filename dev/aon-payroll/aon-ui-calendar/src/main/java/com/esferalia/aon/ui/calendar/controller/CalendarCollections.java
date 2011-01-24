package com.esferalia.aon.ui.calendar.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.esferalia.aon.calendar.enumeration.CalendarSource;
import com.esferalia.aon.calendar.enumeration.DayType;
//import com.esferalia.aon.calendar.enumeration.NewCalendarOption_;

public class CalendarCollections {

	private List<SelectItem> dayTypes;
	private Integer monthMaxDays;
	private List<SelectItem> monthDays;
	private List<SelectItem> calendarSources;
	private List<SelectItem> newCalendarOptions;
	
	public Integer getMonthMaxDays() {
		if(monthMaxDays==null){
			monthMaxDays = 31;
		}
		return monthMaxDays;
	}

	public void setMonthMaxDays(Integer monthMaxDays) {
		this.monthMaxDays = monthMaxDays;
	}

	public List<SelectItem> getAllDayTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		dayTypes = new LinkedList<SelectItem>();
		for (DayType day : DayType.values()) {
			String name = day.getName(locale);
			SelectItem item = new SelectItem(day, name);
			dayTypes.add(item);
		}
		return dayTypes;
	}
	public List<SelectItem> getFullDayTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		dayTypes = new LinkedList<SelectItem>();
		for (DayType day : DayType.values()) {
			String name = day.getFullName(locale);
			SelectItem item = new SelectItem(day, name);
			dayTypes.add(item);
		}
		return dayTypes;
	}

	public List<SelectItem> getDayTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		dayTypes = new LinkedList<SelectItem>();
		for (DayType day : DayType.values()) {
			String name = day.getName(locale);
			SelectItem item = new SelectItem(day, name);
			if (item.getLabel().equals("V")||item.getLabel().equals("F")) {
				return dayTypes;
			} else {
				dayTypes.add(item);
			}
		}
		return dayTypes;
	}
	
	public List<SelectItem> getMonthDays(){
		monthDays = new LinkedList<SelectItem>();
		for (Integer i = 1; i <= getMonthMaxDays(); i++) {
			SelectItem item = new SelectItem(i, i.toString());
			monthDays.add(item);
		}
		return monthDays;
	}
	
	public List<SelectItem> getCalendarSources(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		calendarSources = new LinkedList<SelectItem>();
		for (CalendarSource day : CalendarSource.values()) {
			String name = day.getName(locale);
			SelectItem item = new SelectItem(day, name);
			calendarSources.add(item);
		}
		return calendarSources;
	}
	
//	public List<SelectItem> getNewCalendarOptions(){
//		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
//		newCalendarOptions = new LinkedList<SelectItem>();
//		for (NewCalendarOption_ o : NewCalendarOption_.values()) {
//			String name = o.getName(locale);
//			SelectItem item = new SelectItem(o, name);
//			newCalendarOptions.add(item);
//		}
//		return newCalendarOptions;
//	}
	
}
