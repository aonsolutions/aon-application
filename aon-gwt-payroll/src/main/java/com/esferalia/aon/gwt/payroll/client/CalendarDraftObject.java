package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.jooq.Aon_master;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CalendarDraftObject implements Calendar.Listener {

	public interface Listener {

		void onValueChangeEvent(ValueChangeEvent<Date> event);

	}

	private EmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private Calendar calendar;
	
	private List<HolidayDraft> holidays;
	private List<String> listBoxItems;
	private List<Listener> listeners;
	
	public CalendarDraftObject(Integer workplaceId,
			EmployeesServiceAsync employeesService) {
		
		this.listeners = new ArrayList<Listener>();
		this.workplaceId = workplaceId;
		this.employeesService = employeesService;

	}

	public List<String> loadListBoxItems(final AsyncCallback<List<String>> cb) {

		if (listBoxItems != null)
			return listBoxItems;
		else
			return loadListBox(cb);
	}

	private List<String> loadListBox(final AsyncCallback<List<String>> cb) {

		employeesService
				.getHolidayDescription(new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						CalendarDraftObject.this.listBoxItems = new ArrayList<String>();
						cb.onFailure(caught);

					}

					@Override
					public void onSuccess(final List<String> list) {
						CalendarDraftObject.this.listBoxItems = list;
						cb.onSuccess(listBoxItems);
					}
				});

		return listBoxItems;
	}

	public void getHolidayCalendar(String pattern, final AsyncCallback<CalendarDraftObject> cb) {
		employeesService.getCalendar(workplaceId, pattern, 
				new AsyncCallback<List<HolidayDraft>>() {

					@Override
					public void onFailure(Throwable caught) {
						CalendarDraftObject.this.holidays = new ArrayList<HolidayDraft>();
						cb.onFailure(caught);
					}

					@Override
					public void onSuccess(List<HolidayDraft> holidays) {
						CalendarDraftObject.this.holidays = holidays;
						CalendarDraftObject.this.initCalendarObject();
						cb.onSuccess(CalendarDraftObject.this);
					}
				});
	}
	
	public void insertHolidaysList (String holidayDescription, String holidayListBox, Map<Date, String> map, final AsyncCallback<Void> cb) {
		
		employeesService.saveHolidayList(workplaceId, holidayDescription, holidayListBox, map, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				cb.onSuccess(result);
			}
		});
	}
	
	public EmployeesServiceAsync getEmployeesService() {
		return employeesService;
	}
	
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	
	public List<HolidayDraft> getHolidays() {
		return Collections.unmodifiableList(holidays);
	}

	private void initCalendarObject() {

		calendar = new Calendar(4);
		calendar.addListener(this);

		calendar.setFirstDate(DateUtils.getFirstDayOfYear(new Date()));
		calendar.setLastDate(DateUtils.getLastDayOfYear(new Date()));
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public Calendar getCalendar() {
		return (this.calendar != null) ? calendar : new Calendar(4);
	}

	@Override
	public void onValueChangeEvent(ValueChangeEvent<Date> event) {
		for (Listener listener : listeners)
			listener.onValueChangeEvent(event);
	}
}
