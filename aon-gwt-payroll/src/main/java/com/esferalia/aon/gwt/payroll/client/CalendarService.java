package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;

public interface CalendarService {

	CalendarDraft getCalendar(int workplaceId, Integer pattern, Integer calendar)
			throws IllegalArgumentException;

	Map<Integer, String> getHolidayDescription() throws IllegalArgumentException;

	void saveHolidayList(int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map)
			throws IllegalArgumentException;
	
	void deletePropertyHoliday(Integer id, Date date) throws IllegalArgumentException;
	
}
