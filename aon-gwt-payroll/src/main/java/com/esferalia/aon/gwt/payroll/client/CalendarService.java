package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;

public interface CalendarService {

	List<HolidayDraft> getCalendar(int workplaceId, String pattern)
			throws IllegalArgumentException;

	List<String> getHolidayDescription() throws IllegalArgumentException;

	void saveHolidayList(int workplaceId, String holidayDescription,
			String holidayListBox, Map<Date, String> map)
			throws IllegalArgumentException;
}
