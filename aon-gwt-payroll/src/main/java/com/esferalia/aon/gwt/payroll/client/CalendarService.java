package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;

public interface CalendarService {

	CalendarDraft getCalendar(int workplaceId, Integer pattern, Integer calendar)
			throws IllegalArgumentException;

	Map<Integer, String> getHolidayDescription() throws IllegalArgumentException;

	void saveHolidaysAndDays(int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType daysTypes [])
			throws IllegalArgumentException;
	
	void deletePropertyHoliday(Integer id, Date date) throws IllegalArgumentException;
	
	
	EmployeeCalendarData getEmployeeCalendar(int contract); 
	
	
}
