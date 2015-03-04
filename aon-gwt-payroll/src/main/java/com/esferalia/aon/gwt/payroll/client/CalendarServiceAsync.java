package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CalendarServiceAsync {

	void getCalendar(int workplaceId, AsyncCallback<List<HolidayDraft>> callback)
			throws IllegalArgumentException;
	
	void getHolidayDescription(AsyncCallback<List<String>> callback) throws IllegalArgumentException;

}
