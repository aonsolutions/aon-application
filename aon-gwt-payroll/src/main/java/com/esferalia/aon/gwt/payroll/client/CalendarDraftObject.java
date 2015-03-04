package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

	private static final String[] months = { "Enero", "Febrero", "Marzo",
			"Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };

	private EmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private Calendar calendar;
	
	
	private List<String> listBoxItems;
	private List<HolidayDraft> holidays;
	
	private List<Listener> listeners;

	public CalendarDraftObject(Integer workplaceId,
			EmployeesServiceAsync employeesService) {

		this.listeners = new ArrayList<Listener>();
		this.workplaceId = workplaceId;
		this.employeesService = employeesService;
		
		
	}
	
	public List<String> loadListBoxItems(final AsyncCallback<List<String>> cb) {
		
		if(listBoxItems != null)
			return listBoxItems;
		else
			return loadListBox(cb);

	}
	
	private List<String> loadListBox(final AsyncCallback<List<String>> cb) {
		
		employeesService.getHolidayDescription(new AsyncCallback<List<String>>() {

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

	public void load(final AsyncCallback<CalendarDraftObject> cb) {
		if (calendar != null)
			cb.onSuccess(CalendarDraftObject.this);
		else
			getHolidayCalendar(cb);
	}

	private void getHolidayCalendar(final AsyncCallback<CalendarDraftObject> cb) {
		
		employeesService.getCalendar(workplaceId, new AsyncCallback<List<HolidayDraft>>() {

			@Override
			public void onFailure(Throwable caught) {
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
	
	private void initCalendarObject() {

		calendar = new Calendar(4);
		calendar.addListener(this);

		calendar.setFirstDate(DateUtils.getFirstDayOfYear(new Date()));
		calendar.setLastDate(DateUtils.getLastDayOfYear(new Date()));
		
	}
	
	public void insertHolidays() {
		int contador = 0;
		
		ListIterator<HolidayDraft> iterator = holidays.listIterator(holidays.size());
		
		while (iterator.hasPrevious()) {
			
			HolidayDraft draft = iterator.previous();
			
			for (Date date : draft.getHolidaysMap().keySet()) 
				calendar.addStyle2Date(date, getStyle(contador));		
	
			contador++;
		}
	}
	
	public List<HolidayDraft> getListHolidayDraft() {
		return Collections.unmodifiableList(holidays);
	}
	
	public void addListener (Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener (Listener listener) {
		listeners.remove(listener);
	}

	public String getMonth(int month) {
		return (month >= 0 && month < 12) ? months[month] : "";
	}

	public Calendar getCalendar() {
		return (this.calendar != null) ? calendar : new Calendar(4);
	}
	
	public static String getStyle(Integer contador) {
		
		switch (contador) {
		case 0:
			return AON.AON_CALENDAR_STATAL_HOLIDAY;
		case 1:
			return AON.AON_CALENDAR_AUTONOMI_HOLIDAY;
		case 2:
			return AON.AON_CALENDAR_LOCAL_HOLIDAY;
		case 3:
			return AON.AON_CALENDAR_MyHOLIDAY_1;
			
		default:
			return AON.AON_CALENDAR_MyHOLIDAY_2;
		}
	}

	@Override
	public void onValueChangeEvent(ValueChangeEvent<Date> event) {
		for(Listener listener : listeners)
			listener.onValueChangeEvent(event);
	}
}
