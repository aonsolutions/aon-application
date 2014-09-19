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

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;
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

	static enum DateField {
		DAY, WEEK, MONTH, YEAR;
	}

	static class EventMetaData {

		private String name;
		private String label;
		private String description;

		private Cell<Event> editCell;
		private Cell<Event> displayCell;
		
		private DateField [] dateFields ;

		public EventMetaData(String name, DateField... dateFields) {
			this(name, name, dateFields);
		}

		public EventMetaData(String name, String description, DateField... dateFields) {
			this(name, name, description, new EventTextCell(),
					new EventInputTextCell(), dateFields);
		}

		public EventMetaData(String name, String label, String description, DateField... dateFields) {
			this(name, label, description, new EventTextCell(),
					new EventInputTextCell(), dateFields);
		}

		protected EventMetaData(String name, String label, String description,
				Cell<Event> displayCell, Cell<Event> editCell, DateField... dateFields) {
			this.name = name;
			this.label = label;
			this.description = description;
			this.displayCell = displayCell;
			this.editCell = editCell;
			this.dateFields = dateFields;
		}

		public String getName() {
			return name;
		}

		public String getLabel() {
			return label;
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

		public boolean accept(DateField dateField) {
			
			for (DateField field : dateFields)
				if ( field == dateField )
					return true;
			
			return false;
		}
	}

	static class EnumEventMetaData extends EventMetaData {

		public EnumEventMetaData(String name, String label, String description,
				String nullOption, String  options [], DateField ...dateFields) {
			this(name, label, description, Arrays.asList(options), nullOption, dateFields);
		}

		public EnumEventMetaData(String name, String label, String description,
				List<String> options, String nullOption , DateField ...dateFields) {
			super(name, label, description, new EventTextCell(),
					new EventSelectionCell(options, nullOption),dateFields);
		}

	}

	static class DecimalEventMetaData extends EventMetaData {

		private static final NumberConstants NUMBER_CONSTANTS = LocaleInfo
				.getCurrentLocale().getNumberConstants();

		public DecimalEventMetaData(String name, DateField... dateFields) {
			this(name, null, dateFields);
		}

		public DecimalEventMetaData(String name, String description, DateField... dateFields) {
			this(name, name, description, dateFields);
		}

		public DecimalEventMetaData(String name, String label,
				String description, DateField... dateFields) {
			super(name, label, description, new EventNumberCell(
					NUMBER_CONSTANTS), new EventInputNumberCell(
					NUMBER_CONSTANTS), dateFields);
		}

	}

	static class BooleanEventMetaData extends EventMetaData {

		private static final NumberConstants NUMBER_CONSTANTS = LocaleInfo
				.getCurrentLocale().getNumberConstants();

		public BooleanEventMetaData(String name, DateField... dateFields) {
			this(name, null, dateFields);
		}

		public BooleanEventMetaData(String name, String description, DateField... dateFields) {
			this(name, name, description, dateFields);
		}

		public BooleanEventMetaData(String name, String label,
				String description, DateField... dateFields) {
			super(name, label, description, new EventBooleanCell(),
					new EventInputCheckCell(), dateFields);
		}

	}

	static class ConstantEventMetaData extends EventMetaData {

		public ConstantEventMetaData(String name, DateField... dateFields) {
			this(name, null, dateFields);
		}

		public ConstantEventMetaData(String name, String description, DateField... dateFields) {
			this(name, name, description, dateFields);
		}

		public ConstantEventMetaData(String name, String label,
				String description, DateField... dateFields) {
			super(name, label, description, new EventTextCell(),
					new EventTextCell(), dateFields);
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

		private static final int DEFAULT_SIZE = 6;

		private static final SafeStyles DEFAULT_STYLES = SafeStylesUtils
				.forWhiteSpace(WhiteSpace.NOWRAP);

		private static final Templates templates = GWT.create(Templates.class);

		private int size;
		private SafeStyles styles;

		public EventTextCell() {
			this(DEFAULT_STYLES, DEFAULT_SIZE);
		}

		public EventTextCell(SafeStyles styles, int size) {
			this.styles = styles;
		}

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			if (event == null) {
				return;
			}

			render(context, event.getValue(), sb);

		}

		@Override
		public Set<String> getConsumedEvents() {
			return Collections.emptySet();
		}

		protected void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}

			String display = value.substring(0, Math.max(size, value.length()));

			// If the value comes from the user, we escape it to avoid XSS
			// attacks.
			SafeHtml safeValue = SafeHtmlUtils.fromString(display);

			SafeHtml rendered = templates.cell(styles, safeValue);
			sb.append(rendered);
		}

	}

	static class EventNumberCell extends EventTextCell {

		private NumberFormat format;
		private NumberConstants numberConstants;

		public EventNumberCell(NumberConstants numberConstants) {
			this.numberConstants = numberConstants;
			this.format = NumberFormat.getFormat(numberConstants
					.decimalPattern());
		}

		@Override
		protected void render(Context context, String value, SafeHtmlBuilder sb) {
			try {
				super.render(context, format.format(Double.parseDouble(value)),
						sb);
			} catch (NumberFormatException e) {
				super.render(context, numberConstants.notANumber(), sb);
			}
		}
	}

	static class EventBooleanCell extends AbstractCell<Event> {

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<div class=\"aon-check\" />")
			SafeHtml checked();
		}

		private static final Templates templates = GWT.create(Templates.class);

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			if (event == null) {
				return;
			}

			render(context, event.getValue(), sb);

		}

		protected void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value == null || !Boolean.valueOf(value)) {
				return;
			}
			sb.append(templates.checked());
		}

	}

	/**
	 * 
	 */
	static class EventInputTextCell extends AbstractCell<Event> {

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<input class=\"aon-inputText\" style=\"{0}\" type=\"text\" size=\"{1}\" />")
			SafeHtml empty(SafeStyles styles, int size);

			@SafeHtmlTemplates.Template("<input class=\"aon-inputText\" style=\"{0} \"type=\"text\" size=\"{1}\" value=\"{2}\" />")
			SafeHtml input(SafeStyles styles, int size, String value);

		}

		private static final int DEFAULT_SIZE = 5;

		private static final SafeStyles DEFAULT_STYLES = SafeStylesUtils
				.forWhiteSpace(WhiteSpace.NOWRAP);

		private static final Templates templates = GWT.create(Templates.class);

		private int size;
		private SafeStyles styles;

		public EventInputTextCell() {
			this(DEFAULT_STYLES, DEFAULT_SIZE);
		}

		public EventInputTextCell(String... consumedEvents) {
			this(DEFAULT_STYLES, DEFAULT_SIZE, consumedEvents);
		}

		public EventInputTextCell(SafeStyles styles, int size) {
			this(styles, size, BrowserEvents.CHANGE, BrowserEvents.DBLCLICK);
		}

		public EventInputTextCell(SafeStyles styles, int size,
				String... consumedEvents) {
			super(consumedEvents);
			this.styles = styles;
			this.size = size;
		}

		@Override
		public boolean isEditing(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value) {
			return true;
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				Event value, NativeEvent event, ValueUpdater<Event> valueUpdater) {

			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			String type = event.getType();

			if (BrowserEvents.CHANGE.equals(type)) {
				InputElement input = getInputElement(parent);
				value.setValue(input.getValue());
				valueUpdater.update(value);
			} else if (BrowserEvents.DBLCLICK.equals(type)) {
				InputDialog inputDialog = new InputDialog("", "");
				inputDialog.center();
				inputDialog.show();
			}
		}

		@Override
		public boolean resetFocus(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value) {
			getInputElement(parent).focus();
			return true;
		}

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			render(context, event != null ? event.getValue() : null, sb);

		}

		private void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value == null) {
				sb.append(templates.empty(styles, size));
			} else {
				sb.append(templates.input(styles, size, value));
			}

		}

		protected InputElement getInputElement(Element parent) {
			return parent.getFirstChild().cast();
		}

	}

	static class EventInputNumberCell extends EventInputTextCell {

		private NumberConstants numberConstants;

		public EventInputNumberCell(NumberConstants numberConstants) {
			super(BrowserEvents.CHANGE, BrowserEvents.KEYPRESS);
			this.numberConstants = numberConstants;

		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				Event value, NativeEvent event, ValueUpdater<Event> valueUpdater) {

			if (BrowserEvents.KEYPRESS.equals(event.getType())) {
				int keyCode = event.getKeyCode();
				if (keyCode == KeyCodes.KEY_BACKSPACE
						|| keyCode == KeyCodes.KEY_DELETE
						|| keyCode == KeyCodes.KEY_LEFT
						|| keyCode == KeyCodes.KEY_RIGHT
						|| keyCode == KeyCodes.KEY_TAB) {
					super.onBrowserEvent(context, parent, value, event,
							valueUpdater);
					return;
				}

				int charCode = event.getCharCode();
				if (isDigit(charCode) || isDecimalSep(charCode)
						|| isMinus(charCode) || isPlus(charCode)) {
					super.onBrowserEvent(context, parent, value, event,
							valueUpdater);
					return;
				}

				event.preventDefault();

			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);

		}

		private boolean isDigit(int charCode) {
			return (charCode >= 48 && charCode <= 57);
		}

		private boolean isPlus(int charCode) {
			return isAt(charCode, numberConstants.plusSign());
		}

		private boolean isMinus(int charCode) {
			return isAt(charCode, numberConstants.minusSign());
		}

		private boolean isDecimalSep(int charCode) {
			return charCode == Character.codePointAt(".", 0);
		}

		private static boolean isAt(int charCode, String str) {
			for (int i = 0; i < str.length(); i++)
				if (charCode == Character.codePointAt(str, i))
					return true;
			return false;
		}
	}

	/**
	 * 
	 */
	static class EventInputCheckCell extends AbstractCell<Event> {

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<input style=\"{0} \"type=\"checkbox\" checked />")
			SafeHtml checked(SafeStyles styles);

			@SafeHtmlTemplates.Template("<input style=\"{0} \"type=\"checkbox\" />")
			SafeHtml unchecked(SafeStyles styles);

		}

		private static final SafeStyles DEFAULT_STYLES = SafeStylesUtils
				.forWhiteSpace(WhiteSpace.NOWRAP);

		private static final Templates templates = GWT.create(Templates.class);

		private SafeStyles styles;

		public EventInputCheckCell() {
			this(DEFAULT_STYLES);
		}

		public EventInputCheckCell(String... consumedEvents) {
			this(DEFAULT_STYLES, consumedEvents);
		}

		public EventInputCheckCell(SafeStyles styles) {
			this(styles, BrowserEvents.CHANGE);
		}

		public EventInputCheckCell(SafeStyles styles, String... consumedEvents) {
			super(consumedEvents);
			this.styles = styles;
		}

		@Override
		public boolean isEditing(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value) {
			return true;
		}

		@Override
		public void onBrowserEvent(Context context, Element parent,
				Event value, NativeEvent event, ValueUpdater<Event> valueUpdater) {

			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			String type = event.getType();

			if (BrowserEvents.CHANGE.equals(type)) {
				InputElement input = getInputElement(parent);
				value.setValue(Boolean.toString(input.isChecked()));
				valueUpdater.update(value);
			}
		}

		@Override
		public boolean resetFocus(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, Event value) {
			getInputElement(parent).focus();
			return true;
		}

		@Override
		public void render(Context context, Event event, SafeHtmlBuilder sb) {
			render(context, event != null ? event.getValue() : null, sb);

		}

		private void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value != null && Boolean.valueOf(value))
				sb.append(templates.checked(styles));
			else
				sb.append(templates.unchecked(styles));

		}

		protected InputElement getInputElement(Element parent) {
			return parent.getFirstChild().cast();
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
			getSelectElement(parent).focus();
			return true;
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
	private Integer workplaceId;
	private Integer agreementId;

	private Events events;
	private DraftEvents draftEvents;

	private List<Listener> listeners;

	private EmployeesServiceAsync employeesServiceAsync;
	private Map<String, EventMetaData> eventsMetaDataMap;

	private Map<String, EventMetaData> userEventsMetaDataMap;

	public EventsDraftObject(Integer workplaceId, Integer agreeementId,
			EmployeesServiceAsync employeesServiceAsync,
			EventMetaData... eventsMetaData) {
		this.events = new Events();
		this.draftEvents = new DraftEvents();
		this.workplaceId = workplaceId;
		this.agreementId = agreeementId;
		this.employeesServiceAsync = employeesServiceAsync;

		this.userEventsMetaDataMap = new HashMap<String, EventMetaData>();
		for (EventMetaData eventMetaData : eventsMetaData)
			this.userEventsMetaDataMap.put(eventMetaData.name, eventMetaData);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
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

	public void getEvents(final int offset, final int limit,
			final GetCallback getCallback) {
		getEventsImpl(offset, limit, getCallback);
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

	public String getEventLabel(String name) {
		return eventsMetaDataMap.get(name).getLabel();
	}

	public String getEventDescriptin(String name) {
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

	public boolean eventAccept(String name, DateField dateField) {
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
	private void fillEventsMetaData(final Callback cb) {
		if (eventsMetaDataMap != null)
			return;

		employeesServiceAsync.getEventsVariables(workplaceId, agreementId,
				startDate, endDate, new AsyncCallback<Map<String, String>>() {

					@Override
					public void onSuccess(Map<String, String> result) {
						eventsMetaDataMap = new HashMap<String, EventMetaData>();
						eventsMetaDataMap.putAll(userEventsMetaDataMap);
						for (String var : result.keySet())
							eventsMetaDataMap.put(
									var,
									new DecimalEventMetaData(var, result
											.get(var)));
						cb.onSucces();
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						eventsMetaDataMap = new HashMap<String, EventMetaData>();
						eventsMetaDataMap.putAll(userEventsMetaDataMap);

						cb.onFailure(caught);

					}
				});

	}

	private Events getEvents(String name) {
		Events oneEvents = new Events();
		oneEvents.setWorkplaceId(workplaceId);

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

	private void fireOnEventAdded(Event event) {
		for (Listener listener : listeners)
			listener.onEventAdded(event);
	}

	private void getEventsImpl(int offset, int limit,
			final GetCallback getCallback) {

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

}
