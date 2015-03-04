package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;

public interface CalendarService {

	List<HolidayDraft> getCalendar(int workplaceId)
			throws IllegalArgumentException;
	
	List<String> getHolidayDescription() throws IllegalArgumentException;
}
