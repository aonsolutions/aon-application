package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CalendarServiceAsync {

	void getEmployeeCalendar(int contract, AsyncCallback<EmployeeCalendarData> callback); 

	void getCalendar(int workplaceId, Integer pattern, Integer calendar, AsyncCallback<CalendarDraft> callback)
			throws IllegalArgumentException;

	void getHolidayDescription(AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException;
	
	void saveHolidaysAndDays(int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType dayTypes[], AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
	void deletePropertyHoliday(Integer id, Date date, AsyncCallback<Void> callback) 
			throws IllegalArgumentException;
	
}
