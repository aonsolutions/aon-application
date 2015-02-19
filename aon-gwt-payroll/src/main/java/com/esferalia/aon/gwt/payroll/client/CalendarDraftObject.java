package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CalendarDraftObject {

	private static final String[] months = { "Enero", "Febrero", "Marzo",
			"Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };

	private EmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private Calendar calendar;
	private HolidayDraft holidayDraft;

	public CalendarDraftObject(Integer workplaceId,
			EmployeesServiceAsync employeesService) {

		this.workplaceId = workplaceId;
		this.employeesService = employeesService;
	}

	public void load(final AsyncCallback<CalendarDraftObject> cb) {
		if (calendar != null)
			cb.onSuccess(CalendarDraftObject.this);
		else
			getHolidayCalendar(cb);
	}

	private void getHolidayCalendar(final AsyncCallback<CalendarDraftObject> cb) {
		employeesService.getStatalHolidays(workplaceId,
				new AsyncCallback<HolidayDraft>() {

					@Override
					public void onFailure(Throwable caught) {
						cb.onFailure(caught);

					}

					@Override
					public void onSuccess(HolidayDraft draft) {
						CalendarDraftObject.this.holidayDraft = draft;
						CalendarDraftObject.this.initCalendarObject();
						cb.onSuccess(CalendarDraftObject.this);
					}
				});
	}

	private void initCalendarObject() {

		calendar = new Calendar(4);

		if (holidayDraft.getStatalHolidays().size() > 0)
			calendar.setStatalHolidays(holidayDraft.getStatalHolidays());

		if (holidayDraft.getAutonomicHolidays().size() > 0)
			calendar.setAutonomiHolidays(holidayDraft.getAutonomicHolidays());

		if (holidayDraft.getLocalHolidays().size() > 0)
			calendar.setLocalHolidays(holidayDraft.getLocalHolidays());

		calendar.setFirstDate(DateUtils.getFirstDayOfYear(new Date()));
		calendar.setLastDate(DateUtils.getLastDayOfYear(new Date()));
	}

	public String getMonth(int month) {
		return (month >= 0 && month < 12) ? months[month] : "";
	}

	public Calendar getCalendar() {
		return (this.calendar != null) ? calendar : new Calendar(4);
	}
	
	public String getStatalTitle() {
		return holidayDraft.getStatalTitle();
	}
	
	public Map<Date, String> getStatalHolidays() {
		return holidayDraft.getStatalHolidays();
	}
	
	public String getAutonomiTitle() {
		return holidayDraft.getAutonomiTitle();
	}
	
	public Map<Date, String> getAutonomiHolidays() {
		return holidayDraft.getAutonomicHolidays();
	}
	
	public String getLocalTitle() {
		return holidayDraft.getLocalTitle();
	}
	
	public Map<Date, String> getLocalHolidays() {
		return holidayDraft.getLocalHolidays();
	}

}
