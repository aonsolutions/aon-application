package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CalendarDraftObjectData implements CalendarDraftObject.Listener {

	public interface Listener {

		void onValueChangeEvent(ValueChangeEvent<Date> event);

		void onInsertHoliday();

	}

	class MyHolidayDraft  {

		private Integer id;
		private Integer domain;
		private String description;

		private Map<Date, String> holidays;

		public MyHolidayDraft() {

			holidays = new LinkedHashMap<Date, String>();
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setDomain(Integer domain) {
			this.domain = domain;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void addHoliday(Date date, String description) {
			holidays.put(date, description);
		}

		public void setMap(Map<Date, String> general) {
			this.holidays.putAll(general);
		}

		public Map<Date, String> getGeneralMap() {

			Map<Date, String> sortMap = new TreeMap<Date, String>(
					new Comparator<Date>() {

						@Override
						public int compare(Date date1, Date date2) {
							return date1.compareTo(date2);
						}
					});

			sortMap.putAll(holidays);
			return sortMap;
		}

		public Integer getId() {
			return this.id;
		}

		public Integer getDomain() {
			return this.domain;
		}

		public String getDescription() {
			return this.description;
		}
	}

	private static final String[] months = { "Enero", "Febrero", "Marzo",
			"Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };

	private Integer colors;

	private CalendarDraftObject calendarDraftObject;

	private List<MyHolidayDraft> myDrafts;
	private List<MyHolidayDraft> inserts;

	private List<HolidayDraft> generalHolidays;
	private List<Listener> listeners;

	public CalendarDraftObjectData(Integer workplaceId,
			EmployeesServiceAsync employeesService) {

		calendarDraftObject = new CalendarDraftObject(workplaceId,
				employeesService);
		calendarDraftObject.addListener(this);

		myDrafts = new LinkedList<MyHolidayDraft>();
		inserts = new LinkedList<MyHolidayDraft>();
		listeners = new ArrayList<Listener>();

		generalHolidays = new ArrayList<HolidayDraft>();
	}

	public List<String> loadListBoxItems(final AsyncCallback<List<String>> cb) {

		final List<String> listboxHolidayItems = new ArrayList<String>();

		calendarDraftObject.loadListBoxItems(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(List<String> result) {
				listboxHolidayItems.addAll(result);
				cb.onSuccess(listboxHolidayItems);
			}
		});

		return listboxHolidayItems;
	}

	public CalendarDraftObject getHolidayCalendar(String pattern,
			final AsyncCallback<CalendarDraftObjectData> cb) {

		calendarDraftObject.getHolidayCalendar(pattern,
				new AsyncCallback<CalendarDraftObject>() {

					@Override
					public void onFailure(Throwable caught) {
						cb.onFailure(caught);
					}

					@Override
					public void onSuccess(CalendarDraftObject result) {
						CalendarDraftObjectData.this.calendarDraftObject = result;
						List<HolidayDraft> list = calendarDraftObject
								.getHolidays();
						CalendarDraftObjectData.this.initHolidayList(list);
						cb.onSuccess(CalendarDraftObjectData.this);
					}
				});

		return calendarDraftObject;
	}

	public void saveHolidayDraft(String value, final AsyncCallback<Void> cb) {
		
		MyHolidayDraft myDraft = myDrafts.get(myDrafts.size() - 1);
		
		String description =  myDraft.getDescription();
		Map<Date, String> map = myDraft.getGeneralMap();
		
		Map<Date, String> aux = new HashMap<Date, String>();
		
		for (Date date : map.keySet())
			aux.put(date, map.get(date));
		
		calendarDraftObject.insertHolidaysList(description, value, aux, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage() + " " + caught.getLocalizedMessage());
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				inserts.clear();
				myDrafts.clear();
				cb.onSuccess(result);
			}
		});
	}

	private void initHolidayList(List<HolidayDraft> holidays) {
		this.generalHolidays.clear();
		this.colors = 0;

		Iterator<HolidayDraft> iterator = holidays.iterator();
		while (iterator.hasNext()) {
			HolidayDraft draft = iterator.next();
			if (draft.getDomain() == 0)
				this.generalHolidays.add(draft);
			else
				addPropertyCalendar(draft);
		}
	}

	private void addPropertyCalendar(HolidayDraft draft) {
		MyHolidayDraft myDraftAux = new MyHolidayDraft();
		myDraftAux.setId(draft.getId());
		myDraftAux.setDomain(draft.getDomain());
		myDraftAux.setDescription(draft.getDescription());

		myDraftAux.setMap(draft.getHolidaysMap());

		myDrafts.add(myDraftAux);
	}

	public void insertHolidays() {

		ListIterator<HolidayDraft> holidaysIterator = getHolidaysListIterator();
		while (holidaysIterator.hasPrevious()) {
			HolidayDraft draft = holidaysIterator.previous();
			for (Date date : draft.getHolidaysMap().keySet())
				calendarDraftObject.getCalendar().addStyle2Date(date,
						getStyle(colors));

			colors++;
		}

		ListIterator<MyHolidayDraft> propertyIterator = getMyHolidayDraftsIterator();
		while (propertyIterator.hasNext()) {
			MyHolidayDraft draft = propertyIterator.next();
			for (Date date : draft.getGeneralMap().keySet())
				calendarDraftObject.getCalendar().addStyle2Date(date,
						getStyle(colors));
		}
	}

	private ListIterator<MyHolidayDraft> getMyHolidayDraftsIterator() {
		return myDrafts.listIterator();
	}

	private ListIterator<HolidayDraft> getHolidaysListIterator() {
		return generalHolidays.listIterator(generalHolidays.size());
	}

	private static String getStyle(Integer contador) {

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

	public void addHoliday(Date date, String description) {
		if (myDrafts.isEmpty())
			myDrafts.add(initMyDrafts());

		MyHolidayDraft draft = myDrafts.get(myDrafts.size() - 1);

		calendarDraftObject.getCalendar().addStyle2Date(date, getStyle(colors));
		draft.addHoliday(date, description);
		inserts.addAll(myDrafts);

		for (Listener listener : listeners)
			listener.onInsertHoliday();
	}

	private MyHolidayDraft initMyDrafts() {
		MyHolidayDraft draft = new MyHolidayDraft();
		draft.setId(-1);
		draft.setDomain(-1);
		draft.setDescription("Festivos Propios");

		return draft;

	}

	public String getHolidayDescription() {

		for (HolidayDraft draft : generalHolidays) {

			if (draft.getDomain() == 0)
				return draft.getDescription();
		}

		return "-";
	}

	public List<MyHolidayDraft> getMyDrafts() {
		return Collections.unmodifiableList(myDrafts);
	}

	public Calendar getCalendar() {
		return calendarDraftObject.getCalendar();
	}

	public List<HolidayDraft> getListHolidayDraft() {
		return generalHolidays;
	}

	public String getMonth(int month) {
		return (month >= 0 && month < 12) ? months[month] : "";
	}

	public void addCalendarListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeCalendarListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onValueChangeEvent(ValueChangeEvent<Date> event) {
		for (Listener listener : listeners)
			listener.onValueChangeEvent(event);
	}

	public boolean insertIsEmpy() {
		return inserts.isEmpty();
	}
}
