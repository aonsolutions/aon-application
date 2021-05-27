package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;

public interface CalendarService {

	CalendarDraft getCalendar(String domain, int workplaceId, Integer pattern, Integer calendar)
			throws IllegalArgumentException;

	Map<Integer, String> getHolidayDescription(String domain) throws IllegalArgumentException;

	void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType daysTypes [])
			throws IllegalArgumentException;
	
	void deletePropertyHoliday(String domain, Integer id, Date date) throws IllegalArgumentException;
	
	
	EmployeeCalendarData getEmployeeCalendar(String domain, int contract);
	
	void setEmployeeCalendar(String domain, int contract, EmployeeCalendarUpdate updateInfo);
	
	
}
