package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CalendarDraftObject implements Calendar.Listener {

	public interface Listener {

		void onValueChangeEvent(Date date);
		
		void onSuprPressEvent(Date date);
		
		void onEnterPressEvent(Date date);

	}
	
	static class ValueComparator implements Comparator<Integer> {
		 
	    Map<Integer, String> map;
	 
	    public ValueComparator(Map<Integer, String> base) {
	        this.map = base;
	    }

		@Override
		public int compare(Integer o1, Integer o2) {
			return map.get(o2).compareTo(map.get(o1));
		}
	}

	private EmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private Calendar calendar;
	private Integer year;
	private CalendarDraft calendarDraft;
	private Map<Integer, String> listBoxItems;
	private List<Listener> listeners;

	public CalendarDraftObject(Integer workplaceId,
			EmployeesServiceAsync employeesService) {

		this.listeners = new ArrayList<Listener>();
		this.workplaceId = workplaceId;
		this.employeesService = employeesService;

	}

	public Map<Integer, String> loadListBoxItems(
			final AsyncCallback<Map<Integer, String>> cb) {

		if (listBoxItems != null)
			return listBoxItems;
		else
			return loadListBox(cb);
	}

	private Map<Integer, String> loadListBox(final AsyncCallback<Map<Integer, String>> cb) {
		
		CalendarDraftObject.this.listBoxItems = new HashMap<Integer, String>();
		
		employeesService.getHolidayDescription(new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(Map<Integer, String> map) {				
				CalendarDraftObject.this.listBoxItems.putAll(map);
				cb.onSuccess(map);
			}
		});
		return listBoxItems;
	}
	
	public void getHolidayCalendar(Integer pattern, Integer year,
			final AsyncCallback<CalendarDraftObject> cb) {
		
		this.year = year;
		
		employeesService.getCalendar(workplaceId, pattern, year,
				new AsyncCallback<com.esferalia.aon.gwt.payroll.shared.CalendarDraft>() {

					@Override
					public void onFailure(Throwable caught) {
						CalendarDraftObject.this.calendarDraft = new CalendarDraft();
						cb.onFailure(caught);
					}

					@Override
					public void onSuccess(com.esferalia.aon.gwt.payroll.shared.CalendarDraft calendarDraft) {
						CalendarDraftObject.this.calendarDraft = calendarDraft;
						CalendarDraftObject.this.initCalendarObject();
						cb.onSuccess(CalendarDraftObject.this);
					}
				});
	}

	public void insertHolidaysList(String holidayDescription,
			Integer holidayListBox, Map<Date, String> map,
			final AsyncCallback<Void> cb) {

		employeesService.saveHolidayList(workplaceId, holidayDescription,
				holidayListBox, map, new AsyncCallback<Void>() {

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
	
	public void deletePropertyHoliday(Integer id, Date date, final AsyncCallback<Void> cb) {
		
		employeesService.deletePropertyHoliday(id, date, new AsyncCallback<Void>() {

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
	
	public DayType getDayType(int weekDay ){
		return calendarDraft.getDayType(weekDay);
	}
	
	public List<HolidayDraft> getHolidays() {
		return calendarDraft.getHolidayDrafts();
	}

	private void initCalendarObject() {

		calendar = new Calendar(4);
		calendar.addListener(this);
		
		Date dateAux = DateUtils.getDate(1, year);
		
		calendar.setFirstDate(DateUtils.getFirstDayOfYear(dateAux));
		calendar.setLastDate(DateUtils.getLastDayOfYear(dateAux));
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public Calendar getCalendar() {
		return (this.calendar != null) ? calendar : new Calendar(4);
	}

	@Override
	public void onValueChangeEvent(Date date) {
		
		for (Listener listener : listeners)
			listener.onValueChangeEvent(date);
	}
	
	@Override
	public void onSuprPressEvent(Date date) {
		for (Listener listener : listeners)
			listener.onSuprPressEvent(date);
	}
	
	@Override
	public void onEnterPressEvent(Date date) {
		for(Listener listener : listeners)
			listener.onEnterPressEvent(date);
	}
	
}
