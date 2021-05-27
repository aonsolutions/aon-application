package com.esferalia.aon.gwt.payroll.client;

import static com.google.gwt.user.client.Event.getTypeInt;

import java.util.Date;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.client.EmployeeEventsDraftObject_COPIA.SaveCallback;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Events.Event;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.MultiSelectionModel;

public class EmployeeEventsDraft_COPIA extends AbstractEventsDraft {

	public static final String YEAR = "YEAR";

	private static final String SELECTED = "selected";

	private static final int COL_OFFSET = 1;
	private static final int ROW_OFFSET = 2;

	private static final int PAGE_SIZE = 50;
	private static final int VERTICAL_SPACE = 20;

	interface Template extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div>{0}</div>")
		SafeHtml div(SafeHtml contents);

		@SafeHtmlTemplates.Template("<div style=\"{0};\">{1}</div>")
		SafeHtml div(SafeStyles styles, SafeHtml contents);

	}

	interface Binder extends UiBinder<Widget, EmployeeEventsDraft_COPIA> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private static final Template template = GWT.create(Template.class);

	private static final DateRange[] DATE_RANGES = { new WeekDateRange(),
			new MonthDateRange(), new YearDateRange() };

	class EvenstTable extends FlexTable {

		private int sunkEvents;

		public EvenstTable() {

			// Sink events.
			int eventBitsToAdd = 0;
			eventBitsToAdd |= getTypeInt(BrowserEvents.FOCUS);
			eventBitsToAdd |= getTypeInt(BrowserEvents.BLUR);
			eventBitsToAdd |= getTypeInt(BrowserEvents.CLICK); // For selection.
			eventBitsToAdd |= getTypeInt(BrowserEvents.KEYUP); // For selection.
			eventBitsToAdd |= getTypeInt(BrowserEvents.KEYDOWN);// For keyboard

			sinkEvents(eventBitsToAdd);
		}

		@Override
		public void sinkEvents(int eventBitsToAdd) {
			sunkEvents |= eventBitsToAdd;
			super.sinkEvents(eventBitsToAdd);
		}

		@Override
		public void onBrowserEvent(com.google.gwt.user.client.Event event) {
			super.onBrowserEvent(event);

			Element td = getEventTargetCell(event);
			onBrowserEvent(event, td);
		}

		void onBrowserEvent(com.google.gwt.user.client.Event event, Element td) {

			if (td == null) {
				return;
			}

			TableCellElement targetTableCell = TableCellElement.as(td);
			TableRowElement targetTableRow = TableRowElement.as(td
					.getParentElement());

			int row = targetTableRow.getSectionRowIndex();
			int col = TableCellElement.as(td).getCellIndex();

			onBrowserEvent(event, targetTableCell, row, col);

		}

		void onBrowserEvent(com.google.gwt.user.client.Event event,
				TableCellElement targetTableCell, int row, int col) {

			String eventType = event.getType();
			if (BrowserEvents.CLICK.equals(eventType)) {
				EmployeeEventsDraft_COPIA.this.onEventsTableClick(row, col);
			} else if (BrowserEvents.KEYDOWN.equals(eventType)) {
			}

			fireEventToCell(event, event.getType(), targetTableCell, row, col);
		}

		/**
		 * Fire an event to the Cell.
		 */
		private void fireEventToCell(com.google.gwt.user.client.Event event,
				String eventType, TableCellElement td, final int row,
				final int col) {
			// Check if the cell consumes the event.
			com.google.gwt.cell.client.Cell<Event> cell = getEditCell();
			if (!cellConsumesEventType(cell, eventType)) {
				return;
			}

			Date startDate = EmployeeEventsDraft_COPIA.this.getStartDate(col);
			Date endDate = EmployeeEventsDraft_COPIA.this.getEndDate(col);
			String name = EmployeeEventsDraft_COPIA.this.getSelectedEvent();
			Event cellValue = new Event();
			// *************
			//Check cabecera
			// *************
			cellValue.setName(name);
			cellValue.setEndDate(endDate);
			cellValue.setStartDate(startDate);

			Event draftValue = EmployeeEventsDraft_COPIA.this.getEvent(row, col);
			cellValue.setValue(draftValue != null ? draftValue.getValue()
					: null);

			com.google.gwt.dom.client.Element parent = td
					.getFirstChildElement();

			ValueUpdater<Event> valueUpdater = new ValueUpdater<Event>() {
				@Override
				public void update(Event value) {
					EmployeeEventsDraft_COPIA.this.update(row, col, value);
				}
			};

			cell.onBrowserEvent(null, parent, cellValue, event, valueUpdater);

		}

		/**
		 * Check if a cell consumes the specified event type.
		 * 
		 * @param cell
		 *            the cell
		 * @param eventType
		 *            the event type to check
		 * @return true if consumed, false if not
		 */
		private boolean cellConsumesEventType(
				com.google.gwt.cell.client.Cell<?> cell, String eventType) {
			Set<String> consumedEvents = cell.getConsumedEvents();
			return consumedEvents != null && consumedEvents.contains(eventType);
		}
	}

	@UiField
	DockLayoutPanel mainPanel;

	@UiField(provided = true)
	EvenstTable eventsTable;

	@UiField
	ScrollPanel eventsTableScrollPane;
	private Element headEl;
	private Element leftColEl;
	private Element rigthColEl;
	private Element upperLeftEl;
	private Element upperRightEl;

	@UiField
	InlineLabel dateRangeLabel;

	@UiField
	ListBox dateRangeListBox;

	@UiField
	Button acceptButton;
	@UiField
	Button nextDateRangeButton;
	@UiField
	Button previousDateRangeButton;

	private boolean waitingForEvents;

	private EmployeeEventsDraftObject_COPIA employeeDraftObject;

	private Td editingTd;
	private MultiSelectionModel<Td> selectionModel;

	public EmployeeEventsDraft_COPIA() {
		eventsTable = new EvenstTable();
		initWidget(binder.createAndBindUi(this));
		this.waitingForEvents = false;
		this.selectionModel = new MultiSelectionModel<Td>();
		hide(eventsTableScrollPane);
	}

	// ------------------------------------------
	//
	// ------------------------------------------

	public void setEventsDraftObject(
			EmployeeEventsDraftObject_COPIA employeeDraftObject) {
		this.employeeDraftObject = employeeDraftObject;
		fillDateRangeList();
		syncWithDateRange();
	}

	@Override
	public void onResize() {
		resizeEventsTable();
		moveFroozenElements();
		super.onResize();
	}

	// -------------------------------------------------------------------------
	// UI Handlers
	// -------------------------------------------------------------------------

	@UiHandler("acceptButton")
	void onAcceptButton(ClickEvent event) {
		String name = getSelectedEvent();

		employeeDraftObject.save(name, new SaveCallback() {

			@Override
			public void onSaveSucces() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSaveFailure(Throwable throwable) {
				// TODO Auto-generated method stub

			}
		});
	}

	@UiHandler("nextDateRangeButton")
	void onNextDateRangeButton(ClickEvent event) {
		DateRange dateRange = getDateRange();
		Date startDate = dateRange.getNext(employeeDraftObject.getStartDate());
		Date endDate = dateRange.getNext(startDate);
		CalendarUtil.addDaysToDate(endDate, -1);

		employeeDraftObject.setPeriod(startDate, endDate,
				new EmployeeEventsDraftObject_COPIA.Callback() {

					@Override
					public void onSucces() {
						reload();
					}

					@Override
					public void onFailure(Throwable throwable) {
						reload();
					}
				});
	}

	@UiHandler("previousDateRangeButton")
	void onPreviousDateRangeButton(ClickEvent event) {
		
//		DateRange dateRange = getDateRange();

//		Date endDate = employeeDraftObject.getStartDate();
//		CalendarUtil.addDaysToDate(endDate, -1);

//		Date startDate = dateRange.getPrevious(employeeDraftObject
//				.getStartDate());
//		
//		Date endDate = dateRange.getPrevious(employeeDraftObject
//				.getEndDate());
		
		DateRange dateRange = getDateRange();
		
		Date endDate = employeeDraftObject.getStartDate();
		CalendarUtil.addDaysToDate(endDate, -1);
		Date startDate = dateRange.getPrevious(endDate);
		
		employeeDraftObject.setPeriod(startDate, endDate,
				new EmployeeEventsDraftObject_COPIA.Callback() {

					@Override
					public void onSucces() {
						reload();
					}

					@Override
					public void onFailure(Throwable throwable) {
						reload();
					}
				});
	}

	@UiHandler("eventsTableScrollPane")
	void onEventsTableScrollPaneScroll(ScrollEvent event) {

		int max = eventsTableScrollPane.getMaximumVerticalScrollPosition();
		int pos = eventsTableScrollPane.getVerticalScrollPosition();
		Element td = eventsTable.getRowFormatter().getElement(1);

		if (pos + 2 * td.getOffsetHeight() > max) {

			int visible = getVisibleEmployeeCount();
			//List<Employee> employees = employeeDraftObject.getEmployees();
			Set<String> events = employeeDraftObject.getEventsNames();
			int remain = events.size() - visible;
			if (remain >= PAGE_SIZE) {
				//fillEventsTable(employees.subList(visible, visible + PAGE_SIZE));				
				
			} else {
				if (remain > 0)
				//	fillEventsTable(employees
				//			.subList(visible, employees.size()));
				getMoreEvents();
			}
		}
	}

	@UiHandler("dateRangeListBox")
	void onDateRangeListBoxChanged(ChangeEvent event) {
		syncWithDateRange();
	}

	@UiHandler("eventsTableScrollPane")
	void onSalaryTableScroll(ScrollEvent event) {
		moveFroozenElements();
	}

	// -------------------------------------------------------------------------
	//
	// -------------------------------------------------------------------------

	private void reload() {
		clearEventsTable();
		fillDateRangeLabel();
		initAndfillEventsTable();
	}

	private DateRange getDateRange() {
		int i = dateRangeListBox.getSelectedIndex();
		String value = dateRangeListBox.getValue(i);
		return DATE_RANGES[Integer.parseInt(value)];
	}

	private void clearEventsTable() {
		eventsTable.removeAllRows();
	}

	private int getVisibleEmployeeCount() {
		return eventsTable.getRowCount() - ROW_OFFSET;
	}

	private void initEventsTable() {
		CellFormatter cellFormatter = eventsTable.getCellFormatter();
		eventsTable.insertRow(0);
		eventsTable.setText(0, 0, "Incidencia");
		cellFormatter.addStyleName(0, 0, AON.AON_BOLD);
		cellFormatter.addStyleName(0, 0, AON.AON_TEXT_CENTER);

		int col = 1;
		DateRange dateRange = getDateRange();
		Date splits[] = dateRange.getSplits(employeeDraftObject.getStartDate(),
				employeeDraftObject.getEndDate());
		for (Date date : splits) {
			eventsTable.setText(0, col, dateRange.formatSplit(date));
			cellFormatter.addStyleName(0, col, AON.AON_NOWRAP);
			cellFormatter.addStyleName(0, col, AON.AON_BOLD);
			cellFormatter.addStyleName(0, col, AON.AON_TEXT_CENTER);
			col++;
		}
		eventsTable.setHTML(0, col++, "&nbsp");
		int cols = col;
		eventsTable.insertRow(1);
		eventsTable.setHTML(1, 0, "&nbsp;");
		eventsTable.setHTML(1, 1, "&nbsp;");		
		Cell<Event> cell = getEditCell();		
		SafeStyles styles = new SafeStylesBuilder().textAlign(TextAlign.CENTER)
				.paddingTop(1, Unit.PX).paddingBottom(1, Unit.PX)
				.paddingLeft(0.5, Unit.EM).paddingRight(0.5, Unit.EM)
				.toSafeStyles();
		
		for (int i = 1; i < cols; i++) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			cell.render(null, null, sb);
			// Build the contents.
			SafeHtml contents = template.div(styles, sb.toSafeHtml());
			eventsTable.setHTML(1, i, contents);
		}
		
		// sink Events
		int eventBitsToAdd = 0;
		for (String typeName : cell.getConsumedEvents())
			eventBitsToAdd |= getTypeInt(typeName);
		eventsTable.sinkEvents(eventBitsToAdd);
	}
	
	private void fillEventTable (Set<String> events) {
		
		Cell<Event> editCell = getEditCell();
		Cell<Event> displayCell = getDisplayCell();
		String eventName = getSelectedEvent();
		CellFormatter cellFormatter = eventsTable.getCellFormatter();
		int row = eventsTable.getRowCount();
		DateRange dateRange = getDateRange();
		Date splits[] = dateRange.getSplits(employeeDraftObject.getStartDate(),
				employeeDraftObject.getEndDate());
		
		for (String event : events) {
			
			eventsTable.setText(row, 0, event);
			cellFormatter.addStyleName(row, 0, AON.AON_NOWRAP);
			cellFormatter.addStyleName(row, 0, AON.AON_TEXT_CENTER);			

			int col = 1;

			for (int i = 0; i < splits.length; i++) {

//				Date start = splits[i];
//				Date end = DateUtils
//						.getPrevDay(i + 1 < splits.length ? splits[i + 1]
//								: dateRange.getNext(employeeDraftObject
//										.getStartDate()));

				SafeHtmlBuilder sb = new SafeHtmlBuilder();

				//Event event = getEvent(employee, eventName, start, end);
				//displayCell.render(null, event, sb);
				// Build the contents.
				SafeHtml contents = template.div(sb.toSafeHtml());
				eventsTable.setHTML(row, col, contents);
				cellFormatter.addStyleName(row, col, AON.AON_TEXT_CENTER);
				col++;
			}
			SafeStyles styles = SafeStylesUtils.forTextAlign(TextAlign.CENTER);
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			editCell.render(null, null, sb);
			SafeHtml contents = template.div(styles, sb.toSafeHtml());
			eventsTable.setHTML(row, col++, contents);

			row++;
		}
		// TODO: Update froozen elements.
		removeFromParent(headEl);
		removeFromParent(upperLeftEl);
		removeFromParent(upperRightEl);
		removeFromParent(leftColEl);
		removeFromParent(rigthColEl);

		Element scrollEl = eventsTableScrollPane.getElement();
		
		headEl = cloneHead(eventsTable, 1, 1);
		upperLeftEl = cloneUpperLeftEl(eventsTable, 1, 1);
		upperRightEl = cloneUpperRightEl(eventsTable, 0, 1);
		leftColEl = cloneLeftColEl(eventsTable, 1, 1);
		rigthColEl = cloneRightColEl(eventsTable, 0, 1);

		DOM.appendChild(scrollEl, headEl);
		DOM.appendChild(scrollEl, upperLeftEl);
		DOM.appendChild(scrollEl, upperRightEl);
		DOM.appendChild(scrollEl, leftColEl);
		DOM.appendChild(scrollEl, rigthColEl);

		// Already attached
		DOM.sinkEvents(headEl, eventsTable.sunkEvents);
		DOM.setEventListener(headEl, new EventListener() {

			@Override
			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
				Element td = getEventTargetCell(event, headEl);

				if (td == null) {
					return;
				}

				TableCellElement targetTableCell = TableCellElement.as(td);
				TableRowElement targetTableRow = TableRowElement.as(td
						.getParentElement());

				int row = targetTableRow.getSectionRowIndex();
				int col = TableCellElement.as(td).getCellIndex();

				eventsTable.onBrowserEvent(event, targetTableCell, row, col
						+ COL_OFFSET);
			}
		});
		DOM.sinkEvents(upperRightEl, eventsTable.sunkEvents);
		DOM.setEventListener(upperRightEl, new EventListener() {

			@Override
			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
				Element td = getEventTargetCell(event, upperRightEl);

				if (td == null) {
					return;
				}

				TableCellElement targetTableCell = TableCellElement.as(td);
				TableRowElement targetTableRow = TableRowElement.as(td
						.getParentElement());

				int row = targetTableRow.getSectionRowIndex();

				int col = eventsTable.getCellCount(row) - 1;

				eventsTable.onBrowserEvent(event, targetTableCell, row, col);
			}
		});
		DOM.sinkEvents(rigthColEl, eventsTable.sunkEvents);
		DOM.setEventListener(rigthColEl, new EventListener() {

			@Override
			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
				Element td = getEventTargetCell(event, rigthColEl);

				if (td == null) {
					return;
				}

				TableCellElement targetTableCell = TableCellElement.as(td);
				TableRowElement targetTableRow = TableRowElement.as(td
						.getParentElement());

				int row = targetTableRow.getSectionRowIndex();

				int col = eventsTable.getCellCount(row) - 1;

				eventsTable
						.onBrowserEvent(event, targetTableCell, row + 2, col);
			}
		});

		resizeEventsTable();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				moveFroozenElements();
				show(eventsTableScrollPane);
			}
		});

	}

	private void fillDateRangeLabel() {
		dateRangeLabel.setText(getDateRange().format(
				employeeDraftObject.getStartDate(),
				employeeDraftObject.getEndDate()));

	}

	private boolean fillDateRangeList() {

		int previous = dateRangeListBox.getItemCount() > 0 ? Integer
				.valueOf(dateRangeListBox.getValue(dateRangeListBox
						.getSelectedIndex())) : -1;
		dateRangeListBox.clear();
		String event = "DIAS_EFECTIVOS";

		try {
			for (int i = 0; i < DATE_RANGES.length; i++) {
				DateRange dateRange = DATE_RANGES[i];			
				if (employeeDraftObject.employeeEventAccept(event,
						dateRange.getDateField())) {
					dateRangeListBox.addItem(dateRange.getDescription(),
							String.valueOf(i));
					if (previous == i) {
						dateRangeListBox.setSelectedIndex(dateRangeListBox
								.getItemCount() - 1);
					}
				}
			}
			
		} catch (Exception ex) {
			Window.alert("Error en fillDateRange: " + ex.getMessage());
		}

		int current = dateRangeListBox.getItemCount() > 0 ? Integer
				.valueOf(dateRangeListBox.getValue(dateRangeListBox
						.getSelectedIndex())) : -1;

		return (previous != current);
	}

	private void syncWithDateRange() {		
		syncWithDateRange(new EmployeeEventsDraftObject_COPIA.Callback() {

			@Override
			public void onSucces() {				
				fillDateRangeLabel();
				initAndfillEventsTable();
			}

			@Override
			public void onFailure(Throwable throwable) {
				fillDateRangeLabel();
				initAndfillEventsTable();

			}
		});
	}

	private void syncWithDateRange(EmployeeEventsDraftObject_COPIA.Callback cb) {		
		DateRange dateRange = getDateRange();
		Date startDate = dateRange.getStart(employeeDraftObject.getStartDate());
		Date endDate = dateRange.getNext(startDate);
		CalendarUtil.addDaysToDate(endDate, -1);
		try {
			employeeDraftObject.setPeriod(startDate, endDate, cb);			
		} catch (Exception ex) {
			Window.alert("Exception: " + ex.getMessage() + " " + ex.getCause());
		}
		
	}

	private void resizeEventsTable() {
		int mainTop = mainPanel.getAbsoluteTop();
		int mainHeight = mainPanel.getOffsetHeight();
		int scrollTop = eventsTableScrollPane.getAbsoluteTop();
		int height = mainHeight - (scrollTop - mainTop);
		eventsTableScrollPane.setHeight((height - VERTICAL_SPACE) + "px");

		int decorationsWidth = eventsTableScrollPane.getOffsetWidth()
				- eventsTableScrollPane.getElement().getClientWidth();

		if (eventsTable.getRowCount() == 0)
			return;

		int mainWidth = mainPanel.getOffsetWidth();
		int tableWidth = eventsTable.getOffsetWidth() + decorationsWidth;

		if (mainWidth < tableWidth) {
			eventsTableScrollPane.setWidth(mainWidth + "px");
		} else {
			eventsTableScrollPane.setWidth(tableWidth + "px");
		}

	}

	private void moveFroozenElements() {
		if (headEl == null || upperLeftEl == null || upperRightEl == null
				|| leftColEl == null || rigthColEl == null)
			return;

		int top = eventsTableScrollPane.getAbsoluteTop();
		int left = eventsTableScrollPane.getAbsoluteLeft();
		int width = eventsTableScrollPane.getElement().getClientWidth();
		int height = eventsTableScrollPane.getElement().getClientHeight();
		int scrollTop = eventsTableScrollPane.getVerticalScrollPosition();
		int scrollLeft = eventsTableScrollPane.getHorizontalScrollPosition();

		// 'upperLeftEl' fixed at upper left corner of 'eventsTableScrollPane'
		moveEl(upperLeftEl, top, left, width, height);

		// 'upperRighEl' fixed at upper right corner of 'eventsTableScrollPane'
		moveEl(upperRightEl, top, left + width - upperRightEl.getClientWidth(),
				width, height);

		// 'headEl' fixed at top of 'eventsTableScrollPane', but also scrolls
		// horizontally.
		int headLeft = left + upperLeftEl.getOffsetWidth() - 1 - scrollLeft;
		moveEl(headEl, top, headLeft, 0, scrollLeft, width - (headLeft - left),
				height);
		headEl.getStyle().setWidth(
				eventsTable.getElement().getClientWidth()
						- upperLeftEl.getOffsetWidth() + 2, Unit.PX); // +2 ????

		// 'leftColEl' fixed at left of 'eventsTableScrollPane', but also
		// scrolls vertically.
		int leftColTop = top + upperLeftEl.getOffsetHeight() - scrollTop
				- (scrollTop == 0 ? 1 : 0);
		moveEl(leftColEl, leftColTop, left, scrollTop, 0, width, height
				- (leftColTop - top));

		// 'leftColEl' fixed at left of 'eventsTableScrollPane', but also
		// scrolls vertically.
		int rigthColTop = top + upperLeftEl.getOffsetHeight() - scrollTop
				- (scrollTop == 0 ? 1 : 0);
		moveEl(rigthColEl, rigthColTop,
				left + width - rigthColEl.getClientWidth(), scrollTop, 0,
				width, height - (rigthColTop - top));
	}

	private void getMoreEvents() {
		getMoreEvents(employeeDraftObject.getEmployeeCount());
	}

	private void getMoreEvents(int offset) {
		if (waitingForEvents)
			return;

		waitingForEvents = true;
	}

	private String getSelectedEvent() {
		//return eventListBox.getValue(eventListBox.getSelectedIndex());
		return "DIAS_EFECTIVOS";
	}

	private Cell<Event> getEditCell() {
		return employeeDraftObject.getEventEditCell(getSelectedEvent());
	}

	private Cell<Event> getDisplayCell() {
		return employeeDraftObject.getEventDisplayCell(getSelectedEvent());
	}

	private Date getDayAt(int col) {

		Date firstDay = employeeDraftObject.getStartDate();
		Date day = CalendarUtil.copyDate(firstDay);
		CalendarUtil.addDaysToDate(day, col - COL_OFFSET);

		return day;

	}

	private Date getStartDate(int col) {
		return isLastCol(col) ? CalendarUtil.copyDate(employeeDraftObject
				.getStartDate()) : getDayAt(col);
	}

	private Date getEndDate(int col) {
		return isLastCol(col) ? CalendarUtil.copyDate(employeeDraftObject
				.getEndDate()) : getDayAt(col);
	}

	private Employee getEmployeeAt(int row) {
		return employeeDraftObject.getEmployees().get(row - ROW_OFFSET);
	}

	private Event getEvent(int row, int col) {

		if (isLastCol(col) || isFirstRow(row))
			return null;

		Date day = getDayAt(col);
		String name = getSelectedEvent();
		Employee employee = getEmployeeAt(row);

		return employeeDraftObject.getEvent(employee, name, day);
	}

	private Event getEvent(Employee employee, String name, Date start, Date end) {
		return employeeDraftObject.getEvent(employee, name, start, end);
	}

	/**
	 * Convert the cell to edit mode.
	 */
	private void editCell(int row, int col) {

		Cell<Event> cell = getEditCell();
		SafeHtmlBuilder sb = new SafeHtmlBuilder();

		Event event = getEvent(row, col);

		cell.render(null, event, sb);

		SafeHtml contents = SafeHtmlUtils.EMPTY_SAFE_HTML;
		contents = template.div(SafeStylesUtils.forTextAlign(TextAlign.CENTER),
				sb.toSafeHtml());
		eventsTable.setHTML(row, col, contents);

		Element td = eventsTable.getCellFormatter().getElement(row, col);
		com.google.gwt.dom.client.Element div = td.getFirstChildElement();

		editingTd = new Td(row, col);

		cell.resetFocus(null, div, event);
	}

	/**
	 * Convert the cell to non-edit mode.
	 */
	private void cancelCell() {
		cancelCell(editingTd.row, editingTd.col);
		editingTd = null;
	}

	private void deSelectAll() {
		for (Td td : selectionModel.getSelectedSet())
			eventsTable.getCellFormatter().removeStyleName(td.row, td.col,
					SELECTED);
		selectionModel.clear();
	}

	/**
	 * Convert the cell to non-edit mode.
	 */
	private void cancelCell(int row, int col) {

		Cell<Event> cell = getDisplayCell();
		SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();

		Event event = getEvent(row, col);

		cell.render(null, event, cellBuilder);

		SafeHtml contents = SafeHtmlUtils.EMPTY_SAFE_HTML;
		contents = template.div(cellBuilder.toSafeHtml());
		eventsTable.setHTML(row, col, contents);

	}

	private void displayEvent(Event event) {
		int cols = getColCount() - 1;
		int rows = eventsTable.getRowCount();

		for (int row = ROW_OFFSET; row < rows; row++)
			for (int col = COL_OFFSET; col < cols; col++)
				displayEvent(row, col, event);

	}

	private void displayColEvent(int col, Event event) {
		int rows = eventsTable.getRowCount();
		for (int row = ROW_OFFSET; row < rows; row++)
			displayEvent(row, col, event);

	}

	private void displayRowEvent(int row, Event event) {

		for (int col = COL_OFFSET; col < getColCount() - 1; col++)
			displayEvent(row, col, event);

	}

	private void displayEvent(int row, int col, Event event) {

		Cell<Event> cell = getDisplayCell();
		SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
		cell.render(null, event, cellBuilder);
		SafeHtml contents = SafeHtmlUtils.EMPTY_SAFE_HTML;
		contents = template.div(cellBuilder.toSafeHtml());
		eventsTable.setHTML(row, col, contents);

	}

	private void update(int row, int col, Event newEvent) {
		Event oldEvent = getEvent(row, col);
		String newValue = newEvent != null ? newEvent.getValue() : null;
		String oldValue = oldEvent != null ? oldEvent.getValue() : null;
		if (EmployeeEventsDraft_COPIA.equals(newValue, oldValue))
			return; // Nothing to change, already has this value.

		if (isFirstRow(row)) {

			employeeDraftObject.addDraftEvent(newEvent);

			if (isLastCol(col)) {
				displayEvent(newEvent);
			} else {
				displayColEvent(col, newEvent);
			}
		} else {
			Employee employee = getEmployeeAt(row);
			employeeDraftObject.addDraftEvent(employee, newEvent);

			if (isLastCol(col)) {
				displayRowEvent(row, newEvent);
			}
		}
	}

	void onEventsTableClick(int row, int col) {

		if (isEditing(row, col))
			return;

		if (isEditing())
			cancelCell();
		else
			deSelectAll();

		if (!isEventCell(row, col))
			return;

		editCell(row, col);
	}

	private boolean isEventCell(int row, int col) {

		return (row >= ROW_OFFSET) && (col >= COL_OFFSET)
				&& (col < (getColCount() - 1));

	}

	private boolean isEditing() {
		return editingTd != null;
	}

	private boolean isEditing(int row, int col) {
		return editingTd != null && editingTd.row == row
				&& editingTd.col == col;
	}

	private int getColCount() {
		return eventsTable.getCellCount(0);
	}

	private boolean isFirstRow(int row) {
		return row == 1;
	}

	private boolean isLastCol(int col) {
		return (col == (eventsTable.getCellCount(0) - 1));
	}

	// --------------------------------------------------------- Private methods

	private void initAndfillEventsTable() {		
		clearEventsTable();
		initEventsTable();
		Set<String> myEvents = employeeDraftObject.getEventsNames();
		fillEventTable(myEvents);
/*		List<Employee> employees = employeeDraftObject.getEmployees();
		
		if (events.size() >= PAGE_SIZE)
			fillEventsTable(events.sub);

		if (employees.size() >= PAGE_SIZE) {
			fillEventsTable(employees.subList(0, PAGE_SIZE));
		} else {
			fillEventsTable(employees);
			getMoreEvents();
		}
*/
	}

	protected static Element cloneHead(EvenstTable eventsTable, int cols,
			int rows) {

		Element table = DOM.createTable();
		Element tbody = DOM.createTBody();
		DOM.appendChild(table, tbody);

		for (int row = 0; row < rows; row++) {
			Element rt = cloneTR(eventsTable.getRowFormatter().getElement(row));

			// remove 'cols' at right.
			for (int i = 0; i < cols; i++)
				rt.getChild(0).removeFromParent();

			DOM.appendChild(tbody, rt);

		}

		Style style = table.getStyle();
		style.setPosition(Position.FIXED);
		style.setBackgroundColor("white");
		style.setProperty("width", "auto"); /* override width: 100% */

		table.setClassName(eventsTable.getElement().getClassName());

		return table;
	}

}
