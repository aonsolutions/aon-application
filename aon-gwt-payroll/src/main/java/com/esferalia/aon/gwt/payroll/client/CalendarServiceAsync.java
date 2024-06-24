package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CalendarServiceAsync {

	void getEmployeeCalendar(String domain, int contract, AsyncCallback<EmployeeCalendarData> callback); 
	
	void setEmployeeCalendar(String domain, int contract, EmployeeCalendarUpdate updateInfo, AsyncCallback<EmployeeCalendarUpdate> callback);

	void getCalendar(String domain, int workplaceId, Integer pattern, Integer calendar, AsyncCallback<CalendarDraft> callback)
			throws IllegalArgumentException;

	void getHolidayDescription(String domain, AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException;
	
	void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType dayTypes[], Integer year, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
	void deletePropertyHoliday(String domain, Integer id, Date date, AsyncCallback<Void> callback) 
			throws IllegalArgumentException;
	
	void updateHolidayCalendar(String currentDomainName, int workplaceId, Integer holidayId, CalendarDraft.DayType dayTypes[],
			AsyncCallback<Void> callback) throws IllegalArgumentException;
	
}
