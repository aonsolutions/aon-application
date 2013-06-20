package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class EventsDraftObject {

	private static final int ALL_EMPLOYE_ID = -1;

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

	static class EventMetaData {

		private String name;
		private String description;

		private Cell<Event> editCell;
		private Cell<Event> displayCell;

		public EventMetaData(String name) {
			this(name, name);
		}

		public EventMetaData(String name, String description) {
			this(name, description, new EventTextCell(),
					new EventInputTextCell());
		}

		protected EventMetaData(String name, String description,
				Cell<Event> displayCell, Cell<Event> editCell) {
			this.name = name;
			this.description = description;
			this.displayCell = displayCell;
			this.editCell = editCell;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Cell<Event> getEditCell() {
			return editCell;
		}

		public Cell<Event> getDisplayCell() {
			return displayCell;
		}

	}

	static class EnumEventMetaData extends EventMetaData {

		public EnumEventMetaData(String name, String description,
				String nullOption, String... options) {
			this(name, description, Arrays.asList(options), nullOption);
		}

		public EnumEventMetaData(String name, String description,
				List<String> options, String nullOption) {
			super(name, description, new EventTextCell(),
					new EventSelectionCell(options, nullOption));
		}

	}

	/**
	 * A custom {@link Cell} used to render the value of a event {@link Event}
	 * as a string.
	 */

	static class EventTextCell extends AbstractCell<Event> {

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<span style=\"{0}\">{1}</span>")
			SafeHtml cell(SafeStyles styles, SafeHtml value);
		}

		private static Templates templates = GWT.create(Templates.class);

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			if (event == null) {
				return;
			}

			render(context, event.getValue(), sb);

		}

		private void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}

			// If the value comes from the user, we escape it to avoid XSS
			// attacks.
			SafeHtml safeValue = SafeHtmlUtils.fromString(value);

			// Use the template to create the Cell's html.
			SafeStyles styles = SafeStylesUtils
					.forWhiteSpace(WhiteSpace.NOWRAP);
			SafeHtml rendered = templates.cell(styles, safeValue);
			sb.append(rendered);
		}

	}

	/**
	 * 
	 */
	static class EventInputTextCell extends AbstractCell<Event> {

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<input class=\"aon-inputText\" type=\"text\" value=\"{0}\" />")
			SafeHtml input(String value);
		}

		private static Templates templates = GWT.create(Templates.class);

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			render(context, event != null ? event.getValue() : null, sb);

		}

		private void render(Context context, String value, SafeHtmlBuilder sb) {

			if (value != null) {
				sb.append(templates.input(value));
			} else {
				sb.appendHtmlConstant("<input type=\"text\" tabindex=\"-1\"></input>");
			}

		}

	}

	/**
	 * 
	 */
	static class EventSelectionCell extends AbstractCell<Event> {

		interface Templates extends SafeHtmlTemplates {
			@Template("<option value=\"{0}\">{0}</option>")
			SafeHtml deselected(String option);

			@Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
			SafeHtml selected(String option);
		}

		private static Templates template = GWT.create(Templates.class);

		private String nullOption = null;
		private List<String> options;
		private HashMap<String, Integer> indexForOption;

		public EventSelectionCell(List<String> options, String nullOption) {
			super(BrowserEvents.CHANGE);
			this.indexForOption = new HashMap<String, Integer>();
			this.options = new ArrayList<String>(options.size() + 1);
			if (nullOption != null) {
				this.options.add(nullOption);
				this.indexForOption.put(nullOption, 0);
			}
			for (int i = 0; i < options.size(); i++) {
				String option = options.get(i);
				this.options.add(option);
				this.indexForOption.put(option, i + 1);
			}

		}

		@Override
		public boolean resetFocus(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value) {
			if (isEditing(context, parent, value)) {
				getSelectElement(parent).focus();
				return true;
			}
			return false;
		}

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			render(context, event != null ? event.getValue() : null, sb);

		}

		@Override
		public void onBrowserEvent(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value, NativeEvent event,
				ValueUpdater<Event> valueUpdater) {

			super.onBrowserEvent(context, parent, value, event, valueUpdater);

			String type = event.getType();

			if (BrowserEvents.CHANGE.equals(type)) {

				SelectElement select = getSelectElement(parent);

				String option = options.get(select.getSelectedIndex());
				if (option == nullOption)
					option = null;

				value.setValue(option);

				valueUpdater.update(value);
			}
		}

		@Override
		public boolean isEditing(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value) {
			return true;
		}

		// ---------------------------------------------------------------------
		//
		// ---------------------------------------------------------------------

		private SelectElement getSelectElement(Element parent) {
			return parent.getFirstChild().cast();
		}

		private int getSelectedIndex(String value) {
			Integer index = indexForOption.get(value);
			if (index == null) {
				return -1;
			}
			return index.intValue();
		}

		private void render(Context context, String value, SafeHtmlBuilder sb) {

			// TODO: Get the view data.

			int selectedIndex = getSelectedIndex(value != null ? value
					: nullOption);
			sb.appendHtmlConstant("<select tabindex=\"-1\">");
			int index = 0;
			for (String option : indexForOption.keySet()) {
				if (index++ == selectedIndex) {
					sb.append(template.selected(option));
				} else {
					sb.append(template.deselected(option));
				}
			}
			sb.appendHtmlConstant("</select>");

		}

	}

	static class DraftEvents {

		Map<Integer, Map<String, List<Event>>> eventsMap = new HashMap<Integer, Map<String, List<Event>>>();

		public DraftEvents() {
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
				if ( events == null ) {
					employeeEventsMap.put(entry.getKey(), events = new LinkedList<Event>());
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

	}

	private Date endDate;
	private Date startDate;
	private int workplaceId;

	private Events events;
	private DraftEvents draftEvents;

	private List<Listener> listeners;

	private EmployeesServiceAsync employeesServiceAsync;
	private Map<String, EventMetaData> eventsMetaDataMap;

	public EventsDraftObject(int workplaceId,
			EmployeesServiceAsync employeesServiceAsync,
			EventMetaData... eventsMetaData) {
		this.events = new Events();
		this.draftEvents = new DraftEvents();
		this.workplaceId = workplaceId;
		this.employeesServiceAsync = employeesServiceAsync;

		this.eventsMetaDataMap = new HashMap<String, EventMetaData>();
		for (EventMetaData eventMetaData : eventsMetaData)
			this.eventsMetaDataMap.put(eventMetaData.name, eventMetaData);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setStartDate(Date startDate) {
		this.events.clear();
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.events.clear();
		this.endDate = endDate;
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

	public void getEvents(int offset, int limit, final GetCallback getCallback) {
		String names[] = eventsMetaDataMap.keySet().toArray(
				new String[eventsMetaDataMap.size()]);
		employeesServiceAsync.getEvents(workplaceId, startDate, endDate,
				offset, limit, names, new AsyncCallback<Events>() {
					@Override
					public void onFailure(Throwable caught) {
						getCallback.onEventsFailure(caught);
					}

					@Override
					public void onSuccess(Events result) {
						events.addAll(result);
						draftEvents.addAll(result.getEmployees());
						getCallback.onEventsSucces(result.getEmployees());
					}
				});
	}

	public void getAvailPeriod(String name, AsyncCallback<Period> callback) {
		employeesServiceAsync.getAvailPeriod(workplaceId, name, callback);
	}

	public void copyEvents(final String name, final Date start, final Date end,
			final CopyCallback callback) {

		employeesServiceAsync.getEvents(workplaceId, start, end, 0,
				Integer.MAX_VALUE, new String[] { name },
				new AsyncCallback<Events>() {

					@Override
					public void onSuccess(Events result) {
						
						Date currentStart = CalendarUtil.copyDate(startDate);
						Date currentEnd = CalendarUtil.copyDate(endDate);

						int days = CalendarUtil
								.getDaysBetween(start, startDate);

						for (Employee employee : result.getEmployees()) {
							if (!events.hasEmployee(employee))
								events.addEmployee(employee);

							int employeeId = employee.getId();

							List<Event> rawEvents = result.getRawEvents(
									employeeId, name);

							for (Event event : rawEvents) {

								Date eventStart = event.getStartDate();
								CalendarUtil.addDaysToDate(eventStart, days);
								if (currentStart.after(eventStart))
									event.setStartDate(currentStart);

								Date eventEnd = event.getEndDate();
								if (eventEnd != null)
									CalendarUtil.addDaysToDate(eventEnd, days);
								if (eventEnd == null
										|| currentEnd.before(eventEnd))
									event.setEndDate(currentEnd);
							}

							draftEvents.setEvents(employeeId, name, rawEvents);
						}
						callback.onCopySucces();
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCopyFailure(caught);
					}

				});

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

	private Events getEvents(String name) {
		Events oneEvents = new Events();
		oneEvents.setWorkplaceId(workplaceId);

		for (Integer employeId : events.getEmployeeIds()) {
			Map<String, List<Event>> eventsMap = new HashMap<String, List<Event>>();
			eventsMap.put(name, getEvents(employeId, name));
			oneEvents.setEvents(employeId, eventsMap);
		}

		List<Event> all = draftEvents.getFinalEvents(-1, name, startDate,
				endDate);
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

	private void fireOnEventAdded(Event event) {
		for (Listener listener : listeners)
			listener.onEventAdded(event);
	}
}
