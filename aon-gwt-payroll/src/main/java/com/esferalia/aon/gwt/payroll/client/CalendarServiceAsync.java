package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CalendarServiceAsync {

	void getCalendar(int workplaceId, String pattern, AsyncCallback<List<HolidayDraft>> callback)
			throws IllegalArgumentException;

	void getHolidayDescription(AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;
	
	void saveHolidayList(int workplaceId, String holidayDescription,
			String holidayListBox, Map<Date, String> map, AsyncCallback<Void> callback)
			throws IllegalArgumentException;


}
