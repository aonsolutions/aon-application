package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.calendar.Holiday;

public interface ICalendar {

	List<Calendar> getCalendar(CloseableAONContext ctx, Integer domainId, Integer workplace);
	
	List<Calendar> getCalendars(CloseableAONContext ctx, Integer domainId);

	List<Holiday> getHolidays(CloseableAONContext ctx, Integer domainId);

	void deleteHolidayDetail(CloseableAONContext ctx, Integer id);

	Calendar saveCalendar(CloseableAONContext ctx, Integer domainId, Calendar calendar);

	void setPayrollWorkplaceCalendar(CloseableAONContext ctx, Integer domainId, Integer workplaceId, Integer calendarId);
	
	
}
	