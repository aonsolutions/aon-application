package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Events implements Serializable {

	private static final int ALL_EMPLOYE_ID = -1;
	
	public static class Event implements Serializable {

		private String name;
		private String value;
		private Date endDate;
		private Date startDate;

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public Date getStartDate() {
			return startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		@Override
		public String toString() {
			return "{" + "name : " + name + ", value : " + value + ", start : "
					+ startDate + ", end : "
					+ (endDate != null ? endDate : "-") + "}";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Event) {
				Event event = (Event) obj;
				return name.equals(event.name) && value.equals(event.value)
						&& startDate.equals(event.startDate)
						&& endDate.equals(event.endDate);
			}

			return false;

		}

		public boolean isAt(Date date) {
			return (compare(date, startDate) >= 0)
					&& (compare(date, endDate) <= 0);
		}

		public boolean isBetween(Date startDate, Date endDate ) {
			return (compare(this.startDate, endDate ) <= 0)
					&& (compare(this.endDate, startDate) >= 0);
		}

		private boolean before(Event event) {
			return before(startDate, event.startDate);
		}

		private List<Event> diff(Event event) {
			List<Event> diff = new ArrayList<Event>(2);
			if (before(event.startDate, startDate))
				diff.add(clone(event, event.startDate,
						min(event.endDate, add(startDate, -1))));
			if (after(event.endDate, endDate))
				diff.add(clone(event, max(event.startDate, add(endDate, 1)),
						event.endDate));
			return diff;
		}
		private static Date add(Date date, int days) {
			if (date == null) {
				return null;
			}
			Date prev = new Date(date.getTime());
			prev.setDate(date.getDate() + days);
			return prev;
		}


		private static int compare(Date date1, Date date2) {
			if (date1 == date2)
				return 0;
			if (date1 == null)
				return 1;
			if (date2 == null)
				return -1;
			return DateUtils.compare(date1, date2);
		}

		private static boolean after(Date date1, Date date2) {
			return compare(date1, date2) > 0;
		}

		private static Date min(Date date1, Date date2) {
			return compare(date1, date2) <= 0 ? date1 : date2;
		}

		private static boolean before(Date date1, Date date2) {
			return compare(date1, date2) < 0;
		}

		private static Date max(Date date1, Date date2) {
			return compare(date1, date2) >= 0 ? date1 : date2;
		}

		public static Event clone(Event event, Date startdate, Date endDate) {
			Event clone = new Event();
			clone.name = event.name;
			clone.value = event.value;
			clone.startDate = startdate;
			clone.endDate = endDate;
			return clone;
		}
	}


	public static List<Event> diff(List<Event> eventsA, Event eventB) {
		return diff(eventsA, Collections.<Event> singletonList(eventB));
	}

	public static List<Event> diff(List<Event> eventsA, List<Event> eventsB) {
		return diff(eventsA, 0, eventsB);
	}
	
	private Integer workplaceId;

	protected LinkedList<Employee> employeesList;
	protected Map<Integer, Map<String, List<Event>>> eventsMap;

	public Events() {
		employeesList = new LinkedList<Employee>();
		eventsMap = new HashMap<Integer, Map<String, List<Event>>>();
		eventsMap.put(ALL_EMPLOYE_ID, new HashMap<String, List<Event>>());
	}

	public void clear() {
		eventsMap.clear();
		employeesList.clear();
	}

	public int getEmployeeCount() {
		return employeesList.size();
	}

	public void addAll(Events events) {
		for (Employee employee : events.employeesList)
			if (this.employeesList.add(employee))
				this.eventsMap.put(employee.getId(),
						events.eventsMap.get(employee.getId()));

	}
	
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	
	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}
	
	

	public List<Employee> getEmployees() {
		return employeesList;
	}

	public boolean hasEmployee(Employee employee) {
		return eventsMap.containsKey(employee.getId());
	}

	public List<Event> getFinalEvents(int id, String name) {
		Map<String, List<Event>> eventMap = eventsMap.get(id);
		if (eventMap == null || eventMap.isEmpty())
			return Collections.<Event> emptyList();

		List<Event> eventsRawList = eventMap.get(name);
		if (eventsRawList == null || eventsRawList.isEmpty())
			return Collections.<Event> emptyList();

		List<Event> eventsList = new LinkedList<Event>();

		for (Event event : eventsRawList) {
			eventsList.addAll(diff(eventsList, event));
		}

		return eventsList;
	}
	
	public void remove(int employeeId) {
		eventsMap.remove(employeeId);
	}

	public Map<String, List<Event>> getEvents(int employeeId) {
		return eventsMap.get(employeeId);
	}

	public List<Event> getFinalEvents(Employee employee, String name) {
		return getFinalEvents(employee.getId(), name);
	}

	public List<Event> getRawEvents(int id, String name) {
		return eventsMap.get(id).get(name);
	}


	public void addEmployee(Employee employee) {
		employeesList.add(employee);
		eventsMap.put(employee.getId(), new HashMap<String, List<Event>>());
	}

	public void addEvent(Event event) {
		Map<String, List<Event>> eventMap = eventsMap.get(ALL_EMPLOYE_ID);
		List<Event> eventsList = eventMap.get(event.getName());
		if (eventsList == null) {
			eventsList = new LinkedList<Event>();
			eventMap.put(event.getName(), eventsList);
		}
		eventsList.add(0, event);
	}


	public void addEvent(int id, Event event) {
		Map<String, List<Event>> eventMap = eventsMap.get(id);
		List<Event> eventsList = eventMap.get(event.getName());
		if (eventsList == null) {
			eventsList = new LinkedList<Event>();
			eventMap.put(event.getName(), eventsList);
		}
		eventsList.add(0, event);
	}

	public Event getEvent(int id, String name, Date day) {
		Map<String, List<Event>> eventMap = eventsMap.get(id);
		if (eventMap == null || eventMap.isEmpty())
			return null;

		List<Event> eventsList = eventMap.get(name);
		if (eventsList == null || eventsList.isEmpty())
			return null;

		for (Event event : eventsList) {
			if (event.isAt(day)) {
				return event;
			}
		}

		return null;
	}

	public Event getEvent(int id, String name, Date start, Date end) {
		Map<String, List<Event>> eventMap = eventsMap.get(id);
		if (eventMap == null || eventMap.isEmpty())
			return null;

		List<Event> eventsList = eventMap.get(name);
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

	public Set<Integer> getEmployeeIds() {
		return eventsMap.keySet();
	}

	public Set<String> getEventNames(int empployeeId) {
		return eventsMap.get(empployeeId).keySet();
	}


	public void setEvents(int id, Map<String, List<Event>> events) {
		eventsMap.put(id, events);
	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	private static List<Event> diff(List<Event> eventsA, int startA,
			List<Event> eventsB) {
		if (eventsA.size() <= startA)
			return eventsB;

		List<Event> diff = new LinkedList<Event>();
		Event eventA = eventsA.get(startA);
		for (Event eventB : eventsB)
			diff.addAll(eventA.diff(eventB));

		return diff(eventsA, startA + 1, diff);
	}
	

	
}
