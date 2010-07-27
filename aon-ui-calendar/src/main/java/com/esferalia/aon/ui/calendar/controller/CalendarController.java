package com.esferalia.aon.ui.calendar.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.calendar.enumeration.DayType;

public class CalendarController extends BasicController {

	private List<SelectItem> dayTypes;

	public List<SelectItem> getAllDayTypes() {
		
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			dayTypes = new LinkedList<SelectItem>();
			for (DayType day : DayType.values()) {
				String name = day.getName(locale);
				SelectItem item = new SelectItem(day, name);
				dayTypes.add(item);
			}
		
		return dayTypes;
	}

	public List<SelectItem> getDayTypes() {
		
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
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

}
