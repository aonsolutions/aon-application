package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CalendarDraftObjectData implements CalendarDraftObject.Listener {
	
	// ----------------------------------------------- CalendarDraftListener

	public interface CalendarDraftListener {

		void onValueChangeEvent(Date date);
		
		void onContextMenu(ContextMenuEvent event, Date date);
		
		void onChangeEvent();	
		
		void onEnterKeyPress(Date date);
		
		void onSuprKeyPress(Date date);
	}
	
	public void addCalendarListener(CalendarDraftListener listener) {
		listeners.add(listener);
	}
	
	public void removeCalendarListener(CalendarDraftListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void onValueChangeEvent(Date date) {
		
		for (CalendarDraftListener listener : listeners)
			listener.onValueChangeEvent(date);
	}
	
	@Override
	public void onContextMenu(ContextMenuEvent event, Date date) {
		for (CalendarDraftListener listener : listeners)
			listener.onContextMenu(event, date);
	}
	
	@Override
	public void onSuprPressEvent(Date date) {
		
		for (CalendarDraftListener listener : listeners)
			listener.onSuprKeyPress(date);
	}

	@Override
	public void onEnterPressEvent(Date date) {
		
		for(CalendarDraftListener listener : listeners)
			listener.onEnterKeyPress(date);
	}
	
	// ----------------------------------------------- CalendarEvents
	
	interface CalendarEvents {
		
		void onInsertHoliday();		
		
		void onUpdateHoliday();
	}
	
	public void addCalendarEvent(CalendarEvents event) {
		calendarEvents.add(event);
	}
	
	public void removeCalendarEvent(CalendarEvents event) {
		calendarEvents.remove(event);
	}

	// ----------------------------------------------- Variables

	private static final String[] months = { "Enero", "Febrero", "Marzo",
			"Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
			"Octubre", "Noviembre", "Diciembre" };

	private CalendarDraftObject calendarDraftObject;

	private Map<Integer, String> listboxHolidayItems;

	private DayType myDayTypesDrafts [];
	private List<MyHolidayDraft> myHolidaysDrafts;
	
	private Map<Date, String> insertsDraft;

	private List<HolidayDraft> generalHolidays;
	
	private List<CalendarDraftListener> listeners;	
	private List<CalendarEvents> calendarEvents;
	
	private Integer colors;

	// ----------------------------------------------- Constructor
	
	public CalendarDraftObjectData(Integer workplaceId) {

		calendarDraftObject = new CalendarDraftObject(workplaceId);
		
		listeners = new ArrayList<CalendarDraftListener>();
		calendarEvents = new ArrayList<CalendarEvents>();
		generalHolidays = new ArrayList<HolidayDraft>();

		calendarDraftObject.addListener(this);

		myHolidaysDrafts = new LinkedList<MyHolidayDraft>();
		
		insertsDraft = new HashMap<Date, String>();
		
		myDayTypesDrafts = new DayType [7];
	}
	
	// ----------------------------------------------- DataBase Methods

	public Map<Integer, String> loadListBoxItems(final AsyncCallback<Map<Integer, String>> cb) {
		
		listboxHolidayItems = new HashMap<Integer, String>();
		
		calendarDraftObject.loadListBoxItems(new AsyncCallback<Map<Integer,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(Map<Integer, String> map) {
				CalendarDraftObjectData.this.listboxHolidayItems.putAll(map);
				cb.onSuccess(listboxHolidayItems);
			}
		});

		return listboxHolidayItems;
	}
	
	public void getHolidayCalendar (Integer pattern, Integer year, final AsyncCallback<CalendarDraftObjectData> cb) {
		getHoliday(pattern, year, cb);
	}

	private void getHoliday(Integer pattern, Integer year,
			final AsyncCallback<CalendarDraftObjectData> cb) {

		calendarDraftObject.getHolidayCalendar(pattern, year,
				new AsyncCallback<CalendarDraftObject>() {

					@Override
					public void onFailure(Throwable caught) {
						cb.onFailure(caught);
					}

					@Override
					public void onSuccess(CalendarDraftObject result) {
						CalendarDraftObjectData.this.colors = 0;
						CalendarDraftObjectData.this.calendarDraftObject = result;
						List<HolidayDraft> list = calendarDraftObject.getHolidays();
//						list.forEach(hliday -> Window.alert(hliday.getId() + " --> " + hliday.getDescription()));
						CalendarDraftObjectData.this.initHolidayList(list);
						cb.onSuccess(CalendarDraftObjectData.this);
					}
				});
	}
	
	public void getHolidayCalendarWithYearChange(Integer pattern, Integer year,
			final AsyncCallback<CalendarDraftObjectData> cb) {

		calendarDraftObject.getHolidayCalendar(pattern, year,
				new AsyncCallback<CalendarDraftObject>() {

					@Override
					public void onFailure(Throwable caught) {
						cb.onFailure(caught);
					}

					@Override
					public void onSuccess(CalendarDraftObject result) {
						CalendarDraftObjectData.this.clearDrafts();
						CalendarDraftObjectData.this.calendarDraftObject = result;
						List<HolidayDraft> list = calendarDraftObject
								.getHolidays();
						DayType daysTypes [] = calendarDraftObject.getDayTypes();
						CalendarDraftObjectData.this.initHolidayList(list);
 						CalendarDraftObjectData.this.initDaysTypes(daysTypes);
						cb.onSuccess(CalendarDraftObjectData.this);
					}
				});
	}
	
	public void saveHolidayDraft(Integer holidayId, Integer year, final AsyncCallback<Void> cb) {
		
		if (myHolidaysDrafts.isEmpty())
			myHolidaysDrafts.add(initMyDrafts());
		
		MyHolidayDraft myDraft = myHolidaysDrafts.get(myHolidaysDrafts.size() - 1);
		
		String description =  myDraft.getDescription();
		Map<Date, String> map = myDraft.getGeneralMap();
		
		Map<Date, String> aux = new HashMap<Date, String>();
		
		for (Date date : map.keySet())
			aux.put(date, map.get(date));
		
		calendarDraftObject.saveHolidaysAndDays(description, holidayId, aux, myDayTypesDrafts, year, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage() + " " + caught.getLocalizedMessage());
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				CalendarDraftObjectData.this.clearDrafts();
				cb.onSuccess(result);
			}
		});
	}
	
	public void updateHolidayCalendar(Integer holidayId, final AsyncCallback<Void> cb) {
		
		calendarDraftObject.updateHolidayCalendar(holidayId, myDayTypesDrafts, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage() + " " + caught.getLocalizedMessage());
				cb.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				CalendarDraftObjectData.this.clearDrafts();
				cb.onSuccess(result);
			}
		});
	}
	
	public void deleteHoliday(Date date) {
		if(insertsDraft.containsKey(date)) 
			deleteFromInsertDraft(date, insertsDraft.get(date));
		
		else if (!myHolidaysDrafts.isEmpty())  {
			Map<Date, String> map = myHolidaysDrafts.get(myHolidaysDrafts.size() -1).getGeneralMap();
			if (map.containsKey(date))
				deleteDateSelected(date, map.get(date));
		}

	}
	
	private void deleteFromInsertDraft(Date date, String description) {
		
		Map<Date, String> aux = myHolidaysDrafts.get(myHolidaysDrafts.size() - 1).getGeneralMap();
		
		insertsDraft.remove(date);
		aux.remove(date);
		
		for(CalendarDraftListener listener : listeners)
			listener.onChangeEvent();
			
	}
	
	private void deleteDateSelected(Date date, String description) {
		
		if(Window.confirm("\u00BFConfirma que desea eliminar el festivo propio '" + description + "' \u003F ")) {
			
			MyHolidayDraft draft = myHolidaysDrafts.get(myHolidaysDrafts.size() - 1);
			Integer id = draft.getId();
			draft.getGeneralMap().remove(date);
						
			calendarDraftObject.deletePropertyHoliday(id, date, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					onFailure(caught);
				}

				@Override
				public void onSuccess(Void result) {
					
					for (CalendarDraftListener listener : listeners)
						listener.onChangeEvent();
				}
			});
		}
	}
	
	// ----------------------------------------------- Auxiliar Methods
	
	public DayType getDayType(int weekDay) {
		return myDayTypesDrafts[weekDay];
	}
	
	public void setDayType(int weekDay, DayType dayType ) {
		myDayTypesDrafts[weekDay] = dayType;
	}
	
	private void initDaysTypes (DayType daysTypes []) {
		myDayTypesDrafts = Arrays.copyOf(daysTypes, daysTypes.length);
	}
	
	public void assignHoliday2Draft(Integer value) {
		if(myHolidaysDrafts.isEmpty() == false) {
			MyHolidayDraft draft = myHolidaysDrafts.get(0);
			draft.setHoliday(value);
		}
	}

	private void initHolidayList(List<HolidayDraft> holidays) {
		this.generalHolidays.clear();

		Iterator<HolidayDraft> iterator = holidays.iterator();
		while (iterator.hasNext()) {
			HolidayDraft draft = iterator.next();
//			Window.alert("initHolidayList --> " + draft.getId() + " != " + getHolidayDescription() + " --> " + (!draft.getId().equals(getHolidayDescription())));
			if (!draft.getId().equals(getHolidayDescription()) /*draft.getDomain() == 0*/)
				this.generalHolidays.add(draft);
			else
				addPropertyCalendar(draft);
		}
	}

	private void addPropertyCalendar(HolidayDraft draft) {			
//		Window.alert("addPropertyCalendar");
		if(conteinsId(draft.getId()) == false) {
			MyHolidayDraft myDraftAux = new MyHolidayDraft();
			myDraftAux.setId(draft.getId());
			myDraftAux.setHoliday(draft.getHoliday());
			myDraftAux.setDescription(draft.getDescription());
			myDraftAux.setMap(draft.getHolidaysMap());
//			Window.alert("addPropertyCalendar myHolidaysDrafts Insert");
			myHolidaysDrafts.add(myDraftAux);
		}
	}

	public void insertHolidays() {
		
		this.colors = 0;

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
		return myHolidaysDrafts.listIterator();
	}

	private ListIterator<HolidayDraft> getHolidaysListIterator() {
		return generalHolidays.listIterator(generalHolidays.size());
	}

	public void setWorkingDay(int dayOfWeek) {
		myDayTypesDrafts[dayOfWeek] = DayType.WORKING_DAY;
		calendarDraftObject.getCalendar().removeStyleFromDay("datePickerDayIsWeekend", dayOfWeek);
	}

	public void setNonWorkingDay(int dayOfWeek) {
		myDayTypesDrafts[dayOfWeek] = DayType.NOT_WORKING_DAY;
		calendarDraftObject.getCalendar().addStyleToDay("datePickerDayIsWeekend", dayOfWeek);
	}

	public void addHoliday(Date date, String description) {
//		Window.alert("addHoliday myHolidaysDrafts isEmpty : " + myHolidaysDrafts.isEmpty());
		if (myHolidaysDrafts.isEmpty())
			myHolidaysDrafts.add(initMyDrafts());
		
		insertsDraft.put(date, description);

		MyHolidayDraft draft = myHolidaysDrafts.get(myHolidaysDrafts.size() - 1);

		calendarDraftObject.getCalendar().addStyle2Date(date, getStyle(colors));
		draft.addHoliday(date, description);
		
		for(CalendarEvents event : calendarEvents)
			event.onInsertHoliday();
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

	private MyHolidayDraft initMyDrafts() {
				
		MyHolidayDraft draft = new MyHolidayDraft();
		draft.setId(-1);
		draft.setDomain(-1);
		draft.setHoliday(-50);
		draft.setDescription("Festivos Propios");

		return draft;

	}

	public Integer getHolidayDescription() {
		return calendarDraftObject.getCalendarHoliday();
//		if ( myHolidaysDrafts.isEmpty() == false)
//			return myHolidaysDrafts.get(0).getHoliday();
////			return myHolidaysDrafts.get(myHolidaysDrafts.size() - 1).getHoliday();
//
//		else if ( generalHolidays.isEmpty() == false)
//			return generalHolidays.get(0).getId();
//
//		else
//			return -50;
	}

	public List<MyHolidayDraft> getMyDrafts() {
		return Collections.unmodifiableList(myHolidaysDrafts);
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

	public boolean isNotWorkDay(int dayOfWeek ){
		return calendarDraftObject.getDayType(dayOfWeek) == DayType.NOT_WORKING_DAY;
	}
	
	public boolean canDeleteMyHoliday(Date date) {
		if (!myHolidaysDrafts.isEmpty())
			return myHolidaysDrafts.get(myHolidaysDrafts.size() - 1).getGeneralMap().containsKey(date);
		
		return false;
	}
	
	public boolean isDefaultHoliday(Date date) {
		List<HolidayDraft> holidaysDraft = getListHolidayDraft();
		boolean isDefaultHoliday = false;
		if (!holidaysDraft.isEmpty())
			for(HolidayDraft holidayDraft : holidaysDraft)
				if(holidayDraft.getHolidaysMap().containsKey(date) && !isDefaultHoliday)
					isDefaultHoliday = true;
		
		return isDefaultHoliday;
	}


	public boolean insertIsEmpy() {
		return insertsDraft.isEmpty() 
				&& Arrays.deepEquals(myDayTypesDrafts, calendarDraftObject.getDayTypes());
	}
	
	private boolean conteinsId(Integer id) {
		
		for(MyHolidayDraft item : myHolidaysDrafts) {
			if (item.getId() == id)
				return true;
		}
		return false;
			
	}
	
	private void clearDrafts() {		
		insertsDraft.clear();
		myHolidaysDrafts.clear();
		generalHolidays.clear();
	}
	
	// ----------------------------------------------- MyHolidayDraft

	class MyHolidayDraft  {

		private Integer id;
		private Integer holiday;
		private Integer domain;
		private String description;
		
		private Map<Date, String> sortHolidays;

		public MyHolidayDraft() {
			
			sortHolidays = new TreeMap<Date, String>(new Comparator<Date>() {

				@Override
				public int compare(Date date1, Date date2) {					
					return date1.compareTo(date2);
				}
			});
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setHoliday(Integer holiday) {
			this.holiday = holiday;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		public void setDomain(Integer domain) {
			this.domain = domain;
		}

		public void addHoliday(Date date, String description) {
			sortHolidays.put(date, description);
		}

		public void setMap(Map<Date, String> general) {
			sortHolidays.putAll(general);
		}

		public Map<Date, String> getGeneralMap() {
			return sortHolidays;
		}

		public Integer getId() {
			return this.id;
		}
		
		public Integer getDomain() {
			return this.domain;
		}

		public Integer getHoliday() {
			return this.holiday;
		}

		public String getDescription() {
			return this.description;
		}
	}

	
}
