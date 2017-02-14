package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.DateField;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeEventsDraftObject extends AbstractEventsDraftObject {
	
	private static final int ALL_EMPLOYE_ID = -1;

	static interface Callback {

		void onSucces();

		void onFailure(Throwable throwable);

	}

	static interface SaveCallback {

		void onSaveSucces();

		void onSaveFailure(Throwable throwable);

	}

	static interface GetCallback {

		void onEventsFailure(Throwable throwable);

		void onEventsSucces(List<Employee> employees);

	}

	static interface CopyCallback {

		void onCopySucces();

		void onCopyFailure(Throwable throwable);

	}

	static interface Listener {
		void onEventAdded(Event event);
	}

	static class EmployeeDraftEvents {

		Map<Integer, Map<String, List<Event>>> eventsMap = new HashMap<Integer, Map<String, List<Event>>>();

		public EmployeeDraftEvents() {
			add(ALL_EMPLOYE_ID);
		}

		public Map<String, List<Event>> add(Integer id) {
			Map<String, List<Event>> employeeEventsMap = eventsMap.get(id);
			if (employeeEventsMap == null) {
				eventsMap.put(id,
						employeeEventsMap = new HashMap<String, List<Event>>());
			}
			return employeeEventsMap;
		}

		public void addAll(List<Employee> employees) {
			for (Employee employee : employees) {
				Map<String, List<Event>> employeeEventsMap = add(employee
						.getId());
				copyAllEmployeeEventsMap(employeeEventsMap);
			}

		}

		public void copyAllEmployeeEventsMap(
				Map<String, List<Event>> employeeEventsMap) {
			Map<String, List<Event>> allEventsMap = eventsMap
					.get(ALL_EMPLOYE_ID);
			for (Entry<String, List<Event>> entry : allEventsMap.entrySet()) {
				List<Event> events = employeeEventsMap.get(entry.getKey());
				if (events == null) {
					employeeEventsMap.put(entry.getKey(),
							events = new LinkedList<Event>());
				}
				events.addAll(0, entry.getValue());
			}
		}

		public void addEvent(Event event) {
			for (Integer id : eventsMap.keySet())
				addEvent(id, event);
		}

		public void addEvent(Integer id, Event event) {
			Map<String, List<Event>> employeeEventMap = eventsMap.get(id);
			List<Event> eventsList = employeeEventMap.get(event.getName());
			if (eventsList == null) {
				eventsList = new LinkedList<Event>();
				employeeEventMap.put(event.getName(), eventsList);
			}
			eventsList.add(0, event); // at the front, first of all others...
		}

		public void setEvents(Integer id, String name, List<Event> events) {
			Map<String, List<Event>> employeeEventMap = eventsMap.get(id);
			if (employeeEventMap == null) {
				eventsMap.put(id,
						employeeEventMap = new HashMap<String, List<Event>>());
			}
			if (events == null || events.isEmpty())
				return;

			List<Event> eventsList = employeeEventMap.get(name);
			if (eventsList == null || eventsList.isEmpty()) {
				employeeEventMap.put(name, events);
			} else {
				eventsList.addAll(0, events);
			}
		}

		public Event getEvent(int id, String name, Date day) {
			Map<String, List<Event>> employeeEventMap = eventsMap.get(id);
			if (employeeEventMap == null || employeeEventMap.isEmpty())
				return null;

			List<Event> eventsList = employeeEventMap.get(name);
			if (eventsList == null || eventsList.isEmpty())
				return null;

			for (Event event : eventsList) {
				if (event.isAt(day)) {
					return event;
				}
			}

			return null;
		}

		public List<Event> getFinalEvents(int id, String name, Date startDate,
				Date endDate) {
			Map<String, List<Event>> eventMap = eventsMap.get(id);
			if (eventMap == null || eventMap.isEmpty())
				return Collections.<Event> emptyList();

			List<Event> eventsRawList = eventMap.get(name);
			if (eventsRawList == null || eventsRawList.isEmpty())
				return Collections.<Event> emptyList();

			List<Event> eventsList = new LinkedList<Event>();

			for (Event event : eventsRawList) {
				if (event.isBetween(startDate, endDate))
					eventsList.addAll(Events.diff(eventsList, event));
			}

			return eventsList;
		}

		public Event getEvent(int id, String name, Date start, Date end) {
			Map<String, List<Event>> employeeEventMap = eventsMap.get(id);
			if (employeeEventMap == null || employeeEventMap.isEmpty())
				return null;

			List<Event> eventsList = employeeEventMap.get(name);
			if (eventsList == null || eventsList.isEmpty())
				return null;

			for (Event event : eventsList) {

				if (event.isAt(start)) {

					if (event.isAt(end))
						return event;

					Event nextEvent = getEvent(id, name,
							DateUtils.getNextDay(event.getEndDate()), end);

					if (nextEvent == null)
						return null;

					if (StringUtils.equals(event.getValue(),
							nextEvent.getValue()))
						return Event.clone(event, event.getStartDate(),
								nextEvent.getEndDate());
					else
						return null;
				}
			}

			return null;
		}
	}

	private Date endDate;
	private Date startDate;
	private Employee employee;

	private Events events;
	private EmployeeDraftEvents draftEvents;

	private List<Listener> listeners;

	private EmployeesServiceAsync employeesServiceAsync;
	
	private Map<String, EventMetaData> eventsMetaDataMap;
	private Map<String, EventMetaData> userEventsMetaDataMap;

	public EmployeeEventsDraftObject(Employee employee,
			EmployeesServiceAsync employeesServiceAsync,
			EventMetaData... eventsMetaData) {
		this.events = new Events();
		this.draftEvents = new EmployeeDraftEvents();
		this.employee = employee;
		this.employeesServiceAsync = employeesServiceAsync;
		
		this.userEventsMetaDataMap = new HashMap<String, EventMetaData>();
		for (EventMetaData eventMetaData : eventsMetaData)			
			this.userEventsMetaDataMap.put(eventMetaData.getName(), eventMetaData);		
	}

	public Date getStartDate() {
		return startDate != null ? startDate : DateUtils.getFirstDayOfMonth(new Date());
	}

	public Date getEndDate() {
		return endDate != null ? endDate : DateUtils.getLastDayOfMonth(new Date());
	}

	public void setPeriod(Date startDate, Date endDate, Callback cb) {
		this.events.clear();
		this.eventsMetaDataMap = null;
		this.startDate = startDate;
		this.endDate = endDate;
		fillEventsMetaData(cb);	
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void save(String event, final SaveCallback callback) {
		final Events dirtyEvents = getEvents(event);
		employeesServiceAsync.saveEvents(dirtyEvents, startDate, endDate,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onSaveFailure(caught);
					}

					@Override
					public void onSuccess(Void result) {
						callback.onSaveSucces();
					}
				});
	}
	
	public void getEmployeeEvents(final int offset, final int limit, final GetCallback getCallback) {
		
	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	public List<Employee> getEmployees() {
		return events.getEmployees();
	}

	public int getEmployeeCount() {
		return events.getEmployees().size();
	}

	public Set<String> getEventsNames() {		
		return eventsMetaDataMap.keySet();
	}

	public Cell<Event> getEventEditCell(String name) {		
		return eventsMetaDataMap.get(name).getEditCell();
	}

	public Cell<Event> getEventDisplayCell(String name) {
		return eventsMetaDataMap.get(name).getDisplayCell();
	}

	public String getEventLabel(String name) {
		return eventsMetaDataMap.get(name).getLabel();
	}

	public String getEventDescription(String name) {
		return eventsMetaDataMap.get(name).getDescription();
	}

	public Event getEvent(Employee employee, String name, Date day) {
		Event event = null;
		// event = draftEvents.getEvent(ALL_EMPLOYE_ID, name, day);
		// if (event != null)
		// return event;
		event = draftEvents.getEvent(employee.getId(), name, day);
		if (event != null)
			return event;
		return events.getEvent(employee.getId(), name, day);
	}

	public Event getEvent(Employee employee, String name, Date start, Date end) {
		Event event = null;
		event = draftEvents.getEvent(employee.getId(), name, start, end);
		if (event != null)
			return event;
		return events.getEvent(employee.getId(), name, start, end);
	}
	
	public boolean employeeEventAccept(String name, DateField dateField) {
		return eventsMetaDataMap.get(name).accept(dateField);
	}

	// -------------------------------------------------------------------------
	// Draft related

	public void addDraftEvent(Event event) {
		draftEvents.addEvent(event);
	}

	public void addDraftEvent(Employee employee, Event event) {
		draftEvents.addEvent(employee.getId(), event);
	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	private void fillEventsMetaData (final Callback cb) {
		
		if (eventsMetaDataMap == null)
			eventsMetaDataMap = new HashMap<String, EventMetaData>(userEventsMetaDataMap);		

			cb.onSucces();
	}
	
	private Events getEvents(String name) {
		Events oneEvents = new Events();
		

		for (Integer employeId : events.getEmployeeIds()) {
			Map<String, List<Event>> eventsMap = new HashMap<String, List<Event>>();
			eventsMap.put(name, getEvents(employeId, name));
			oneEvents.setEvents(employeId, eventsMap);
		}

		List<Event> all = draftEvents.getFinalEvents(ALL_EMPLOYE_ID, name,
				startDate, endDate);
		if (!all.isEmpty()) {
			Map<String, List<Event>> eventsMap = new HashMap<String, List<Event>>();
			eventsMap.put(name, all);
			oneEvents.setEvents(-1, eventsMap);
		}

		return oneEvents;
	}

	private List<Event> getEvents(int id, String name) {
		List<Event> dbList = events.getFinalEvents(id, name);
		List<Event> draftList = draftEvents.getFinalEvents(id, name, startDate,
				endDate);
		List<Event> newList = Events.diff(draftList, dbList);
		newList.addAll(draftList);

		return newList;
	}
	
	private void getEventsImp(int offset, int limit, final GetCallback getCallback) {
		
		String names[] = eventsMetaDataMap.keySet().toArray(new String[eventsMetaDataMap.size()]);
		
	//	employeesServiceAsync.getEvents(workplaceId, startDate, endDate, offset, limit, names, callback);
	}

}
