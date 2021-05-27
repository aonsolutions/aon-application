//package com.esferalia.aon.gwt.payroll.client;
//
//import static com.google.gwt.user.client.Event.getTypeInt;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//
//import com.esferalia.aon.gwt.common.client.AON;
//import com.esferalia.aon.gwt.common.shared.DateUtils;
//import com.esferalia.aon.gwt.common.shared.StringUtils;
//import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.DateRange;
//import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.MonthDateRange;
//import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.Td;
//import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.WeekDateRange;
//import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.YearDateRange;
//import com.esferalia.aon.gwt.payroll.client.EventsDraftObject.CopyCallback;
//import com.esferalia.aon.gwt.payroll.client.EventsDraftObject.GetCallback;
//import com.esferalia.aon.gwt.payroll.client.EventsDraftObject.SaveCallback;
//import com.esferalia.aon.gwt.payroll.shared.Employee;
//import com.esferalia.aon.gwt.payroll.shared.Events.Event;
//import com.esferalia.aon.gwt.payroll.shared.Period;
//import com.google.gwt.cell.client.Cell;
//import com.google.gwt.cell.client.ValueUpdater;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.core.client.Scheduler;
//import com.google.gwt.core.client.Scheduler.ScheduledCommand;
//import com.google.gwt.dom.client.BrowserEvents;
//import com.google.gwt.dom.client.Element;
//import com.google.gwt.dom.client.Style;
//import com.google.gwt.dom.client.Style.Position;
//import com.google.gwt.dom.client.Style.TextAlign;
//import com.google.gwt.dom.client.Style.Unit;
//import com.google.gwt.dom.client.Style.Visibility;
//import com.google.gwt.dom.client.TableCellElement;
//import com.google.gwt.dom.client.TableRowElement;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ScrollEvent;
//import com.google.gwt.safecss.shared.SafeStyles;
//import com.google.gwt.safecss.shared.SafeStylesBuilder;
//import com.google.gwt.safecss.shared.SafeStylesUtils;
//import com.google.gwt.safehtml.client.SafeHtmlTemplates;
//import com.google.gwt.safehtml.shared.SafeHtml;
//import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
//import com.google.gwt.safehtml.shared.SafeHtmlUtils;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.google.gwt.uibinder.client.UiHandler;
//import com.google.gwt.user.client.DOM;
//import com.google.gwt.user.client.EventListener;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DockLayoutPanel;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
//import com.google.gwt.user.client.ui.InlineLabel;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.ListBox;
//import com.google.gwt.user.client.ui.ResizeComposite;
//import com.google.gwt.user.client.ui.ScrollPanel;
//import com.google.gwt.user.client.ui.UIObject;
//import com.google.gwt.user.client.ui.Widget;
//import com.google.gwt.user.datepicker.client.CalendarUtil;
//import com.google.gwt.view.client.MultiSelectionModel;
//
//public class EventsDraft_Old extends ResizeComposite {
//
//	public static final String YEAR = "YEAR";
//
//	private static final String SELECTED = "selected";
//
//	private static final int COL_OFFSET = 2;
//	private static final int ROW_OFFSET = 2;
//
//	private static final int PAGE_SIZE = 50;
//	private static final int VERTICAL_SPACE = 20;
//
//	interface Template extends SafeHtmlTemplates {
//		@SafeHtmlTemplates.Template("<div>{0}</div>")
//		SafeHtml div(SafeHtml contents);
//
//		@SafeHtmlTemplates.Template("<div style=\"{0};\">{1}</div>")
//		SafeHtml div(SafeStyles styles, SafeHtml contents);
//	}
//
//	interface Binder extends UiBinder<Widget, EventsDraft_Old> {
//	}
//
//	private static final Binder binder = GWT.create(Binder.class);
//	private static final Template template = GWT.create(Template.class);
//
//	private static final DateRange[] DATE_RANGES = { 
//			new WeekDateRange(),
////			new MonthDateRange(), 
////			new YearDateRange() 
//			};
//
//	public static final int WEEK_DATE_RANGE = 0;
//	public static final int MONTH_DATE_RANGE = 1;
//	public static final int YEAR_DATE_RANGE = 2;
//
//	private class EvenstTable extends FlexTable {
//
//		private int sunkEvents;
//
//		public EvenstTable() {
//
//			// Sink events.
//			int eventBitsToAdd = 0;
//			eventBitsToAdd |= getTypeInt(BrowserEvents.FOCUS);
//			eventBitsToAdd |= getTypeInt(BrowserEvents.BLUR);
//			eventBitsToAdd |= getTypeInt(BrowserEvents.CLICK); // For selection.
//			eventBitsToAdd |= getTypeInt(BrowserEvents.KEYUP); // For selection.
//			eventBitsToAdd |= getTypeInt(BrowserEvents.KEYDOWN);// For keyboard
//																// navigation.
//
//			sinkEvents(eventBitsToAdd);
//		}
//
//		@Override
//		public void sinkEvents(int eventBitsToAdd) {
//			sunkEvents |= eventBitsToAdd;
//			super.sinkEvents(eventBitsToAdd);
//		}
//
//		@Override
//		public void onBrowserEvent(com.google.gwt.user.client.Event event) {
//			super.onBrowserEvent(event);
//
//			Element td = getEventTargetCell(event);
//			onBrowserEvent(event, td);
//		}
//
//		void onBrowserEvent(com.google.gwt.user.client.Event event, Element td) {
//
//			if (td == null) {
//				return;
//			}
//
//			TableCellElement targetTableCell = TableCellElement.as(td);
//			TableRowElement targetTableRow = TableRowElement.as(td
//					.getParentElement());
//
//			int row = targetTableRow.getSectionRowIndex();
//			int col = TableCellElement.as(td).getCellIndex();
//
//			onBrowserEvent(event, targetTableCell, row, col);
//
//		}
//
//		void onBrowserEvent(com.google.gwt.user.client.Event event,
//				TableCellElement targetTableCell, int row, int col) {
//
//			String eventType = event.getType();
//			if (BrowserEvents.CLICK.equals(eventType)) {
//				EventsDraft_Old.this.onEventsTableClick(row, col);
//			} else if (BrowserEvents.KEYDOWN.equals(eventType)) {
//			}
//
//			fireEventToCell(event, event.getType(), targetTableCell, row, col);
//
//		}
//
//		/**
//		 * Fire an event to the Cell.
//		 */
//		private void fireEventToCell(com.google.gwt.user.client.Event event,
//				String eventType, TableCellElement td, final int row,
//				final int col) {
//			// Check if the cell consumes the event.
//			com.google.gwt.cell.client.Cell<Event> cell = getEditCell();
//			if (!cellConsumesEventType(cell, eventType)) {
//				return;
//			}
//
//			Date startDate = EventsDraft_Old.this.getStartDate(col);
//			Date endDate = EventsDraft_Old.this.getEndDate(col);
//			String name = EventsDraft_Old.this.getSelectedEvent();
//			Event cellValue = new Event();
//			cellValue.setName(name);
//			cellValue.setEndDate(endDate);
//			cellValue.setStartDate(startDate);
//
//			Event draftValue = EventsDraft_Old.this.getEvent(row, col);
//			cellValue.setValue(draftValue != null ? draftValue.getValue()
//					: null);
//
//			com.google.gwt.dom.client.Element parent = td
//					.getFirstChildElement();
//
//			ValueUpdater<Event> valueUpdater = new ValueUpdater<Event>() {
//				@Override
//				public void update(Event value) {
//					EventsDraft_Old.this.update(row, col, value);
//				}
//			};
//
//			cell.onBrowserEvent(null, parent, cellValue, event, valueUpdater);
//
//		}
//
//		/**
//		 * Check if a cell consumes the specified event type.
//		 * 
//		 * @param cell
//		 *            the cell
//		 * @param eventType
//		 *            the event type to check
//		 * @return true if consumed, false if not
//		 */
//		private boolean cellConsumesEventType(
//				com.google.gwt.cell.client.Cell<?> cell, String eventType) {
//			Set<String> consumedEvents = cell.getConsumedEvents();
//			return consumedEvents != null && consumedEvents.contains(eventType);
//		}
//
//	}
//
//	@UiField
//	DockLayoutPanel mainPanel;
//
//	@UiField(provided = true)
//	EvenstTable eventsTable;
//
//	@UiField
//	ScrollPanel eventsTableScrollPane;
//	private Element headEl;
//	private Element leftColEl;
//	private Element rigthColEl;
//	private Element upperLeftEl;
//	private Element upperRightEl;
//
//	@UiField
//	InlineLabel dateRangeLabel;
//
//	@UiField
//	Label eventLabel;
//	@UiField
//	ListBox eventListBox;
//	@UiField
//	ListBox copyDateRangeListBox;
//
//	@UiField
//	ListBox dateRangeListBox;
//
//	@UiField
//	Button acceptButton;
//	@UiField
//	Button nextDateRangeButton;
//	@UiField
//	Button previousDateRangeButton;
//
//	private boolean waitingForEvents;
//
//	private EventsDraftObject draftObject;
//
//	private Td editingTd;
//	private MultiSelectionModel<Td> selectionModel;
//
//	public EventsDraft_Old() {
//		eventsTable = new EvenstTable();
//		initWidget(binder.createAndBindUi(this));
//		this.waitingForEvents = false;
//		this.selectionModel = new MultiSelectionModel<Td>();
//		hide(eventsTableScrollPane);
//	}
//
//	// ------------------------------------------
//	//
//	// ------------------------------------------
//
//	public void setEventsDraftObject(EventsDraftObject draftObject) {
//		this.draftObject = draftObject;
//
//		fillEventList();
//		fillDateRangeList();
//
//		syncWithDateRange();
//
//	}
//
//	@Override
//	public void onResize() {
//		resizeEventsTable();
//		moveFroozenElements();
//		super.onResize();
//	}
//
//	// -------------------------------------------------------------------------
//	// UI Handlers
//	// -------------------------------------------------------------------------
//
//	@UiHandler("acceptButton")
//	void onAcceptButton(ClickEvent event) {
//		String name = getSelectedEvent();
//		draftObject.save(name, new SaveCallback() {
//
//			@Override
//			public void onSaveSucces() {
//			}
//
//			@Override
//			public void onSaveFailure(Throwable throwable) {
//				Window.alert(throwable.getLocalizedMessage());
//			}
//		});
//	}
//
//	@UiHandler("nextDateRangeButton")
//	void onNextDateRangeButton(ClickEvent event) {
//		DateRange dateRange = getDateRange();
//		Date startDate = dateRange.getNext(draftObject.getStartDate());
//		Date endDate = dateRange.getNext(startDate);
//		CalendarUtil.addDaysToDate(endDate, -1);
//		draftObject.setPeriod(startDate, endDate,
//				new EventsDraftObject.Callback() {
//
//					@Override
//					public void onSucces() {
//						reload();
//					}
//
//					@Override
//					public void onFailure(Throwable throwable) {
//						reload();
//					}
//
//				});
//	}
//
//	@UiHandler("previousDateRangeButton")
//	void onPreviousDateRangeButton(ClickEvent event) {
//		DateRange dateRange = getDateRange();
//
//		Date endDate = draftObject.getStartDate();
//		CalendarUtil.addDaysToDate(endDate, -1);
//		Date startDate = dateRange.getPrevious(draftObject.getStartDate());
//		draftObject.setPeriod(startDate, endDate,
//				new EventsDraftObject.Callback() {
//
//					@Override
//					public void onSucces() {
//						reload();
//					}
//
//					@Override
//					public void onFailure(Throwable throwable) {
//						reload();
//					}
//
//				});
//	}
//
//	@UiHandler("eventsTableScrollPane")
//	void onEventsTableScrollPaneScroll(ScrollEvent event) {
//
//		int max = eventsTableScrollPane.getMaximumVerticalScrollPosition();
//		int pos = eventsTableScrollPane.getVerticalScrollPosition();
//		Element td = eventsTable.getRowFormatter().getElement(1);
//
//		if (pos + 2 * td.getOffsetHeight() > max) {
//
//			int visible = getVisibleEmployeeCount();
//			List<Employee> employees = draftObject.getEmployees();
//			int remain = employees.size() - visible;
//			if (remain >= PAGE_SIZE) {
//				fillEventsTable(employees.subList(visible, visible + PAGE_SIZE));
//			} else {
//				if (remain > 0)
//					fillEventsTable(employees
//							.subList(visible, employees.size()));
//				getMoreEvents();
//			}
//		}
//	}
//
//	@UiHandler("copyDateRangeListBox")
//	void onCopyDateRangeListBoxChanged(ChangeEvent event) {
//		int index = copyDateRangeListBox.getSelectedIndex();
//		String value = copyDateRangeListBox.getValue(index);
//		DateRange dateRange = getDateRange();
//		Date startDate = dateRange.parseSplit(value);
//		Date endDate = dateRange.getNext(startDate);
//		CalendarUtil.addDaysToDate(endDate, -1);
//
//		String name = getSelectedEvent();
//
//		draftObject.copyEvents(name, startDate, endDate, new CopyCallback() {
//
//			@Override
//			public void onCopySucces() {
//				initAndfillEventsTable();
//			}
//
//			@Override
//			public void onCopyFailure(Throwable throwable) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//	}
//
//	@UiHandler("eventListBox")
//	void onEventListBoxChanged(ChangeEvent event) {
//		updateEventLabel();
//
//		if (fillDateRangeList()) {
//			syncWithDateRange();
//		}
//
//		initAndfillEventsTable();
//		fillCopyDateRange();
//	}
//
//	@UiHandler("dateRangeListBox")
//	void onDateRangeListBoxChanged(ChangeEvent event) {
//		syncWithDateRange();
//	}
//
//	@UiHandler("eventsTableScrollPane")
//	void onSalaryTableScroll(ScrollEvent event) {
//		moveFroozenElements();
//	}
//
//	// -------------------------------------------------------------------------
//	//
//	// -------------------------------------------------------------------------
//
//	private void reload() {
//		clearEventsTable();
//		fillDateRangeLabel();
//		initAndfillEventsTable();
//		fillCopyDateRange();
//	}
//
//	private DateRange getDateRange() {
//		int i = dateRangeListBox.getSelectedIndex();
//		String value = dateRangeListBox.getValue(i);		
//		return DATE_RANGES[Integer.parseInt(value)];
//	}
//
//	private void clearEventsTable() {
//		eventsTable.removeAllRows();
//
//	}
//
//	private int getVisibleEmployeeCount() {
//		return eventsTable.getRowCount() - ROW_OFFSET;
//	}
//
//	private void initEventsTable() {
//
//		CellFormatter cellFormatter = eventsTable.getCellFormatter();
//
//		eventsTable.insertRow(0);
//
//		eventsTable.setText(0, 0, "Documento");
//		cellFormatter.addStyleName(0, 0, AON.AON_BOLD);
//		cellFormatter.addStyleName(0, 0, AON.AON_TEXT_CENTER);
//		eventsTable.setText(0, 1, "Empleado");
//		cellFormatter.addStyleName(0, 1, AON.AON_BOLD);
//		cellFormatter.addStyleName(0, 1, AON.AON_TEXT_CENTER);
//		cellFormatter.addStyleName(0, 1, AON.AON_NOWRAP);
//
//		int col = 2;
//
//		DateRange dateRange = getDateRange();
//
//		Date splits[] = dateRange.getSplits(draftObject.getStartDate(),
//				draftObject.getEndDate());
//		for (Date date : splits) {
//			eventsTable.setText(0, col, dateRange.formatSplit(date));
//			cellFormatter.addStyleName(0, col, AON.AON_NOWRAP);
//			cellFormatter.addStyleName(0, col, AON.AON_BOLD);
//			cellFormatter.addStyleName(0, col, AON.AON_TEXT_CENTER);
//			col++;
//		}
//
//		eventsTable.setHTML(0, col++, "&nbsp");
//
//		int cols = col;
//
//		eventsTable.insertRow(1);
//		eventsTable.setHTML(1, 0, "&nbsp;");
//		eventsTable.setHTML(1, 1, "&nbsp;");
//
//		Cell<Event> cell = getEditCell();
//		SafeStyles styles = new SafeStylesBuilder().textAlign(TextAlign.CENTER)
//				.paddingTop(1, Unit.PX).paddingBottom(1, Unit.PX)
//				.paddingLeft(0.5, Unit.EM).paddingRight(0.5, Unit.EM)
//				.toSafeStyles();
//
//		for (int i = 2; i < cols; i++) {
//			SafeHtmlBuilder sb = new SafeHtmlBuilder();
//			cell.render(null, null, sb);
//			// Build the contents.
//			SafeHtml contents = template.div(styles, sb.toSafeHtml());
//			eventsTable.setHTML(1, i, contents);
//		}
//
//		// sink Events
//		int eventBitsToAdd = 0;
//		for (String typeName : cell.getConsumedEvents())
//			eventBitsToAdd |= getTypeInt(typeName);
//		eventsTable.sinkEvents(eventBitsToAdd);
//	}
//
//	private void fillEventsTable(List<Employee> employees) {
//
//		Cell<Event> editCell = getEditCell();
//		Cell<Event> displayCell = getDisplayCell();
//		String eventName = getSelectedEvent();
//
//		CellFormatter cellFormatter = eventsTable.getCellFormatter();
//
//		int row = eventsTable.getRowCount();
//
//		DateRange dateRange = getDateRange();
//
//		Date splits[] = dateRange.getSplits(draftObject.getStartDate(),
//				draftObject.getEndDate());
//
//		for (Employee employee : employees) {
//			
//			eventsTable.setText(row, 0, employee.getDocument());
//			cellFormatter.addStyleName(row, 0, AON.AON_NOWRAP);
//			cellFormatter.addStyleName(row, 0, AON.AON_TEXT_CENTER);
//			String fullName = employee.getFullname();
//			eventsTable
//					.setHTML(row, 1, "&nbsp;&nbsp;" + (StringUtils.isBlank(fullName) ?  "&nbsp;" : fullName ) );
//			cellFormatter.addStyleName(row, 1, AON.AON_NOWRAP);
//
//			int col = 2;
//			// for (Date start : splits ) {
//			for (int i = 0; i < splits.length; i++) {
//
//				Date start = splits[i];
//				Date end = DateUtils
//						.getPrevDay(i + 1 < splits.length ? splits[i + 1]
//								: dateRange.getNext(draftObject.getStartDate()));
//
//				SafeHtmlBuilder sb = new SafeHtmlBuilder();
//
//				Event event = getEvent(employee, eventName, start, end);
//				displayCell.render(null, event, sb);
//				// Build the contents.
//				SafeHtml contents = template.div(sb.toSafeHtml());
//				eventsTable.setHTML(row, col, contents);
//				cellFormatter.addStyleName(row, col, AON.AON_TEXT_CENTER);
//				col++;
//
//			}
//			SafeStyles styles = SafeStylesUtils.forTextAlign(TextAlign.CENTER);
//			SafeHtmlBuilder sb = new SafeHtmlBuilder();
//			editCell.render(null, null, sb);
//			SafeHtml contents = template.div(styles, sb.toSafeHtml());
//			eventsTable.setHTML(row, col++, contents);
//
//			row++;
//		}
//
//		// TODO: Update froozen elements.
//		removeFromParent(headEl);
//		removeFromParent(upperLeftEl);
//		removeFromParent(upperRightEl);
//		removeFromParent(leftColEl);
//		removeFromParent(rigthColEl);
//
//		Element scrollEl = eventsTableScrollPane.getElement();
//
//		headEl = cloneHead(eventsTable, 2, 2);
//		upperLeftEl = cloneUpperLeftEl(eventsTable, 2, 2);
//		upperRightEl = cloneUpperRightEl(eventsTable, 1, 2);
//		leftColEl = cloneLeftColEl(eventsTable, 2, 2);
//		rigthColEl = cloneRightColEl(eventsTable, 1, 2);
//
//		DOM.appendChild(scrollEl, headEl);
//		DOM.appendChild(scrollEl, upperLeftEl);
//		DOM.appendChild(scrollEl, upperRightEl);
//		DOM.appendChild(scrollEl, leftColEl);
//		DOM.appendChild(scrollEl, rigthColEl);
//
//		// Already attached
//		DOM.sinkEvents(headEl, eventsTable.sunkEvents);
//		DOM.setEventListener(headEl, new EventListener() {
//
//			@Override
//			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
//				Element td = getEventTargetCell(event, headEl);
//
//				if (td == null) {
//					return;
//				}
//
//				TableCellElement targetTableCell = TableCellElement.as(td);
//				TableRowElement targetTableRow = TableRowElement.as(td
//						.getParentElement());
//
//				int row = targetTableRow.getSectionRowIndex();
//				int col = TableCellElement.as(td).getCellIndex();
//
//				eventsTable.onBrowserEvent(event, targetTableCell, row, col
//						+ COL_OFFSET);
//			}
//		});
//		DOM.sinkEvents(upperRightEl, eventsTable.sunkEvents);
//		DOM.setEventListener(upperRightEl, new EventListener() {
//
//			@Override
//			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
//				Element td = getEventTargetCell(event, upperRightEl);
//
//				if (td == null) {
//					return;
//				}
//
//				TableCellElement targetTableCell = TableCellElement.as(td);
//				TableRowElement targetTableRow = TableRowElement.as(td
//						.getParentElement());
//
//				int row = targetTableRow.getSectionRowIndex();
//
//				int col = eventsTable.getCellCount(row) - 1;
//
//				eventsTable.onBrowserEvent(event, targetTableCell, row, col);
//			}
//		});
//		DOM.sinkEvents(rigthColEl, eventsTable.sunkEvents);
//		DOM.setEventListener(rigthColEl, new EventListener() {
//
//			@Override
//			public void onBrowserEvent(com.google.gwt.user.client.Event event) {
//				Element td = getEventTargetCell(event, rigthColEl);
//
//				if (td == null) {
//					return;
//				}
//
//				TableCellElement targetTableCell = TableCellElement.as(td);
//				TableRowElement targetTableRow = TableRowElement.as(td
//						.getParentElement());
//
//				int row = targetTableRow.getSectionRowIndex();
//
//				int col = eventsTable.getCellCount(row) - 1;
//
//				eventsTable
//						.onBrowserEvent(event, targetTableCell, row + 2, col);
//			}
//		});
//
//		resizeEventsTable();
//
//		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//			@Override
//			public void execute() {
//				moveFroozenElements();
//				show(eventsTableScrollPane);
//			}
//		});
//
//	}
//
//	private void fillEventList() {
//		eventListBox.clear();
//		for (String name : draftObject.getEventsNames())
//			eventListBox.addItem(draftObject.getEventLabel(name), name);
//		updateEventLabel();
//
//	}
//
//	private void fillDateRangeLabel() {
//		dateRangeLabel.setText(getDateRange().format(
//				draftObject.getStartDate(), draftObject.getEndDate()));
//
//	}
//
//	private boolean fillDateRangeList() {
//
//		int previous = dateRangeListBox.getItemCount() > 0 ? Integer.valueOf(dateRangeListBox
//				.getValue(dateRangeListBox.getSelectedIndex())) : -1 ;
//
//		dateRangeListBox.clear();
//
//		String event = getSelectedEvent();		
//		for (int i = 0; i < DATE_RANGES.length; i++) {
//			DateRange dateRange = DATE_RANGES[i];
//			if (draftObject.eventAccept(event, dateRange.getDateField())) {
//				dateRangeListBox.addItem(dateRange.getDescription(),
//						String.valueOf(i));
//				if ( previous == i ) {
//					dateRangeListBox.setSelectedIndex(dateRangeListBox.getItemCount()-1);
//				}
//			}
//		}
//		
//		int current = dateRangeListBox.getItemCount() > 0 ? Integer.valueOf(dateRangeListBox
//				.getValue(dateRangeListBox.getSelectedIndex())) : -1 ;
//
//		return ( previous != current ) ;
//	}
//
//	private void syncWithDateRange() {
//		syncWithDateRange(new EventsDraftObject.Callback() {
//
//			@Override
//			public void onSucces() {
//				fillDateRangeLabel();
//				initAndfillEventsTable();
//				fillCopyDateRange();
//			}
//
//			@Override
//			public void onFailure(Throwable throwable) {
//				fillDateRangeLabel();
//				initAndfillEventsTable();
//				fillCopyDateRange();
//			}
//
//		});
//	}
//
//	private void syncWithDateRange(EventsDraftObject.Callback cb) {
//
//		DateRange dateRange = getDateRange();
//
//		Date startDate = dateRange.getStart(draftObject.getStartDate());
//		Date endDate = dateRange.getNext(startDate);
//		CalendarUtil.addDaysToDate(endDate, -1);
//
//		draftObject.setPeriod(startDate, endDate, cb);
//	}
//
//	private void updateEventLabel() {
//		eventLabel.setText(draftObject.getEventDescriptin(getSelectedEvent()));
//	}
//
//	private void fillCopyDateRange() {
//
//		copyDateRangeListBox.clear();
//
//		String name = getSelectedEvent();
//		draftObject.getAvailPeriod(name, new AsyncCallback<Period>() {
//
//			@Override
//			public void onSuccess(Period result) {
//
//				if (result == null) {
//					copyDateRangeListBox.addItem("-", "");
//					return;
//				}
//
//				Date first = result.getStart();
//				Date last = result.getEnd();
//				Date date = result.getStart();
//				Date current = draftObject.getStartDate();
//
//				DateRange dateRange = getDateRange();
//
//				if (current.before(first)) {
//					copyDateRangeListBox.addItem("-", "");
//					copyDateRangeListBox.setSelectedIndex(0);
//				}
//
//				while (date.before(current)) {
//
//					// Compare with actual day ( keep out time ).
//					if (CalendarUtil.isSameDate(date, current)) {
//						copyDateRangeListBox.addItem("-", "");
//						copyDateRangeListBox
//								.setSelectedIndex(copyDateRangeListBox
//										.getItemCount() - 1);
//						date = dateRange.getNext(date);
//						continue;
//					}
//
//					Date next = dateRange.getNext(date);
//					Date end = CalendarUtil.copyDate(next);
//					CalendarUtil.addDaysToDate(end, -1);
//
//					String item = dateRange.format(date, end);
//					String value = dateRange.formatSplit(date);
//
//					copyDateRangeListBox.addItem(item, value);
//
//					date = next;
//				}
//
//				if (current.after(last)) {
//					copyDateRangeListBox.addItem("-", "");
//					copyDateRangeListBox.setSelectedIndex(copyDateRangeListBox
//							.getItemCount() - 1);
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});
//
//	}
//
//	private void resizeEventsTable() {
//		int mainTop = mainPanel.getAbsoluteTop();
//		int mainHeight = mainPanel.getOffsetHeight();
//		int scrollTop = eventsTableScrollPane.getAbsoluteTop();
//		int height = mainHeight - (scrollTop - mainTop);
//		eventsTableScrollPane.setHeight((height - VERTICAL_SPACE) + "px");
//
//		int decorationsWidth = eventsTableScrollPane.getOffsetWidth()
//				- eventsTableScrollPane.getElement().getClientWidth();
//
//		if (eventsTable.getRowCount() == 0)
//			return;
//
//		int mainWidth = mainPanel.getOffsetWidth();
//		int tableWidth = eventsTable.getOffsetWidth() + decorationsWidth;
//
//		if (mainWidth < tableWidth) {
//			eventsTableScrollPane.setWidth(mainWidth + "px");
//		} else {
//			eventsTableScrollPane.setWidth(tableWidth + "px");
//		}
//
//	}
//
//	private void moveFroozenElements() {
//		if (headEl == null || upperLeftEl == null || upperRightEl == null
//				|| leftColEl == null || rigthColEl == null)
//			return;
//
//		int top = eventsTableScrollPane.getAbsoluteTop();
//		int left = eventsTableScrollPane.getAbsoluteLeft();
//		int width = eventsTableScrollPane.getElement().getClientWidth();
//		int height = eventsTableScrollPane.getElement().getClientHeight();
//		int scrollTop = eventsTableScrollPane.getVerticalScrollPosition();
//		int scrollLeft = eventsTableScrollPane.getHorizontalScrollPosition();
//
//		// 'upperLeftEl' fixed at upper left corner of 'eventsTableScrollPane'
//		moveEl(upperLeftEl, top, left, width, height);
//
//		// 'upperRighEl' fixed at upper right corner of 'eventsTableScrollPane'
//		moveEl(upperRightEl, top, left + width - upperRightEl.getClientWidth(),
//				width, height);
//
//		// 'headEl' fixed at top of 'eventsTableScrollPane', but also scrolls
//		// horizontally.
//		int headLeft = left + upperLeftEl.getOffsetWidth() - 1 - scrollLeft;
//		moveEl(headEl, top, headLeft, 0, scrollLeft, width - (headLeft - left),
//				height);
//		headEl.getStyle().setWidth(
//				eventsTable.getElement().getClientWidth()
//						- upperLeftEl.getOffsetWidth() + 2, Unit.PX); // +2 ????
//
//		// 'leftColEl' fixed at left of 'eventsTableScrollPane', but also
//		// scrolls vertically.
//		int leftColTop = top + upperLeftEl.getOffsetHeight() - scrollTop
//				- (scrollTop == 0 ? 1 : 0);
//		moveEl(leftColEl, leftColTop, left, scrollTop, 0, width, height
//				- (leftColTop - top));
//
//		// 'leftColEl' fixed at left of 'eventsTableScrollPane', but also
//		// scrolls vertically.
//		int rigthColTop = top + upperLeftEl.getOffsetHeight() - scrollTop
//				- (scrollTop == 0 ? 1 : 0);
//		moveEl(rigthColEl, rigthColTop,
//				left + width - rigthColEl.getClientWidth(), scrollTop, 0,
//				width, height - (rigthColTop - top));
//	}
//
//	private void getMoreEvents() {
//		getMoreEvents(draftObject.getEmployeeCount());
//	}
//
//	private void getMoreEvents(int offset) {
//		if (waitingForEvents)
//			return;
//
//		waitingForEvents = true;
//
//		draftObject.getEvents(offset, PAGE_SIZE, new GetCallback() {
//
//			@Override
//			public void onEventsSucces(List<Employee> employees) {
//				if (employees.size() > 0)
//					fillEventsTable(employees);
//				waitingForEvents = false;
//			}
//
//			@Override
//			public void onEventsFailure(Throwable throwable) {
//				// TODO Auto-generated method stub
//				waitingForEvents = false;
//			}
//		});
//
//	}
//
//	private String getSelectedEvent() {
//		return eventListBox.getValue(eventListBox.getSelectedIndex());
//	}
//
//	private Cell<Event> getEditCell() {
//		return draftObject.getEventEditCell(getSelectedEvent());
//	}
//
//	private Cell<Event> getDisplayCell() {
//		return draftObject.getEventDisplayCell(getSelectedEvent());
//	}
//
//	private Date getDayAt(int col) {
//
//		Date firstDay = draftObject.getStartDate();
//		Date day = CalendarUtil.copyDate(firstDay);
//		CalendarUtil.addDaysToDate(day, col - COL_OFFSET);
//
//		return day;
//
//	}
//
//	private Date getStartDate(int col) {
//		return isLastCol(col) ? CalendarUtil.copyDate(draftObject
//				.getStartDate()) : getDayAt(col);
//	}
//
//	private Date getEndDate(int col) {
//		return isLastCol(col) ? CalendarUtil.copyDate(draftObject.getEndDate())
//				: getDayAt(col);
//	}
//
//	private Employee getEmployeeAt(int row) {
//		return draftObject.getEmployees().get(row - ROW_OFFSET);
//	}
//
//	private Event getEvent(int row, int col) {
//
//		if (isLastCol(col) || isFirstRow(row))
//			return null;
//
//		Date day = getDayAt(col);
//		String name = getSelectedEvent();
//		Employee employee = getEmployeeAt(row);
//
//		return draftObject.getEvent(employee, name, day);
//	}
//
//	private Event getEvent(Employee employee, String name, Date start, Date end) {
//		return draftObject.getEvent(employee, name, start, end);
//	}
//
//	/**
//	 * Convert the cell to edit mode.
//	 */
//	private void editCell(int row, int col) {
//
//		Cell<Event> cell = getEditCell();
//		SafeHtmlBuilder sb = new SafeHtmlBuilder();
//
//		Event event = getEvent(row, col);
//
//		cell.render(null, event, sb);
//
//		SafeHtml contents = SafeHtmlUtils.EMPTY_SAFE_HTML;
//		contents = template.div(SafeStylesUtils.forTextAlign(TextAlign.CENTER),
//				sb.toSafeHtml());
//		eventsTable.setHTML(row, col, contents);
//
//		Element td = eventsTable.getCellFormatter().getElement(row, col);
//		com.google.gwt.dom.client.Element div = td.getFirstChildElement();
//
//		editingTd = new Td(row, col);
//
//		cell.resetFocus(null, div, event);
//	}
//
//	/**
//	 * Convert the cell to non-edit mode.
//	 */
//	private void cancelCell() {
//		cancelCell(editingTd.row, editingTd.col);
//		editingTd = null;
//	}
//
//	private void deSelectAll() {
//		for (Td td : selectionModel.getSelectedSet())
//			eventsTable.getCellFormatter().removeStyleName(td.row, td.col,
//					SELECTED);
//		selectionModel.clear();
//	}
//
//	/**
//	 * Convert the cell to non-edit mode.
//	 */
//	private void cancelCell(int row, int col) {
//
//		Cell<Event> cell = getDisplayCell();
//		SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
//
//		Event event = getEvent(row, col);
//
//		cell.render(null, event, cellBuilder);
//
//		SafeHtml contents = SafeHtmlUtils.EMPTY_SAFE_HTML;
//		contents = template.div(cellBuilder.toSafeHtml());
//		eventsTable.setHTML(row, col, contents);
//
//	}
//
//	private void displayEvent(Event event) {
//		int cols = getColCount() - 1;
//		int rows = eventsTable.getRowCount();
//
//		for (int row = ROW_OFFSET; row < rows; row++)
//			for (int col = COL_OFFSET; col < cols; col++)
//				displayEvent(row, col, event);
//
//	}
//
//	private void displayColEvent(int col, Event event) {
//		int rows = eventsTable.getRowCount();
//		for (int row = ROW_OFFSET; row < rows; row++)
//			displayEvent(row, col, event);
//
//	}
//
//	private void displayRowEvent(int row, Event event) {
//
//		for (int col = COL_OFFSET; col < getColCount() - 1; col++)
//			displayEvent(row, col, event);
//
//	}
//
//	private void displayEvent(int row, int col, Event event) {
//
//		Cell<Event> cell = getDisplayCell();
//		SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
//		cell.render(null, event, cellBuilder);
//		SafeHtml contents = SafeHtmlUtils.EMPTY_SAFE_HTML;
//		contents = template.div(cellBuilder.toSafeHtml());
//		eventsTable.setHTML(row, col, contents);
//
//	}
//
//	private void update(int row, int col, Event newEvent) {
//		Event oldEvent = getEvent(row, col);
//		String newValue = newEvent != null ? newEvent.getValue() : null;
//		String oldValue = oldEvent != null ? oldEvent.getValue() : null;
//		if (EventsDraft_Old.equals(newValue, oldValue))
//			return; // Nothing to change, already has this value.
//
//		if (isFirstRow(row)) {
//
//			draftObject.addDraftEvent(newEvent);
//
//			if (isLastCol(col)) {
//				displayEvent(newEvent);
//			} else {
//				displayColEvent(col, newEvent);
//			}
//		} else {
//			Employee employee = getEmployeeAt(row);
//			draftObject.addDraftEvent(employee, newEvent);
//
//			if (isLastCol(col)) {
//				displayRowEvent(row, newEvent);
//			}
//		}
//	}
//
//	void onEventsTableClick(int row, int col) {
//
//		if (isEditing(row, col))
//			return;
//
//		if (isEditing())
//			cancelCell();
//		else
//			deSelectAll();
//
//		if (!isEventCell(row, col))
//			return;
//
//		editCell(row, col);
//	}
//
//	private boolean isEventCell(int row, int col) {
//
//		return (row >= ROW_OFFSET) && (col >= COL_OFFSET)
//				&& (col < (getColCount() - 1));
//
//	}
//
//	private boolean isEditing() {
//		return editingTd != null;
//	}
//
//	private boolean isEditing(int row, int col) {
//		return editingTd != null && editingTd.row == row
//				&& editingTd.col == col;
//	}
//
//	private int getColCount() {
//		return eventsTable.getCellCount(0);
//	}
//
//	private boolean isFirstRow(int row) {
//		return row == 1;
//	}
//
//	private boolean isLastCol(int col) {
//		return (col == (eventsTable.getCellCount(0) - 1));
//	}
//
//	// --------------------------------------------------------- Private methods
//
//	private void initAndfillEventsTable() {
//		clearEventsTable();
//
//		initEventsTable();
//
//		List<Employee> employees = draftObject.getEmployees();
//
//		if (employees.size() >= PAGE_SIZE) {
//			fillEventsTable(employees.subList(0, PAGE_SIZE));
//		} else {
//			fillEventsTable(employees);
//			getMoreEvents();
//		}
//
//	}
//
//	private static Element getEventTargetCell(
//			com.google.gwt.user.client.Event event, Element tableElem) {
//		Element td = DOM.eventGetTarget(event);
//		for (; td != null; td = DOM.getParent(td)) {
//			// If it's a TD, it might be the one we're looking for.
//			if (DOM.getElementProperty(td, "tagName").equalsIgnoreCase("td")) {
//				// Make sure it's directly a part of this table before returning
//				// it.
//				Element tr = DOM.getParent(td);
//				Element body = DOM.getParent(tr);
//				Element table = DOM.getParent(body);
//				if (table == tableElem) {
//					return td;
//				}
//			}
//			// If we run into this table's body, we're out of options.
//			if (td == tableElem) {
//				return null;
//			}
//		}
//		return null;
//	}
//
//	private static Element cloneTR(Element tr) {
//
//		Element rt = DOM.clone(tr, true);
//
//		com.google.gwt.dom.client.Element td = tr.getFirstChildElement();
//		com.google.gwt.dom.client.Element dt = rt.getFirstChildElement();
//
//		while (td != null) {
//			Style style = dt.getStyle();
//			style.setWidth(td.getClientWidth(), Unit.PX);
//			// remove padding already included at above 'client' width.
//			style.setPaddingLeft(0, Unit.PX);
//			style.setPaddingRight(0, Unit.PX);
//
//			// remove padding already included at above 'client' height.
//			style.setHeight(td.getClientHeight(), Unit.PX);
//			style.setPaddingTop(0, Unit.PX);
//			style.setPaddingBottom(0, Unit.PX);
//
//			td = td.getNextSiblingElement();
//			dt = dt.getNextSiblingElement();
//		}
//
//		return rt;
//
//	}
//
//	private static void removeFromParent(Element el) {
//		if (el != null && el.hasParentElement())
//			el.removeFromParent();
//
//	}
//
//	private static Element cloneUpperLeftEl(FlexTable flexTable, int cols,
//			int rows) {
//
//		Element table = DOM.createTable();
//		Element tbody = DOM.createTBody();
//		DOM.appendChild(table, tbody);
//
//		for (int row = 0; row < rows; row++) {
//			Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
//			// remove all columns except 'cols' at right.
//			for (int i = rt.getChildCount(); i > cols; i--)
//				rt.getChild(i - 1).removeFromParent();
//
//			DOM.appendChild(tbody, rt);
//
//		}
//
//		Style style = table.getStyle();
//		style.setPosition(Position.FIXED);
//		style.setBackgroundColor("white");
//		style.setProperty("width", "auto"); /* override width: 100% */
//
//		table.setClassName(flexTable.getElement().getClassName());
//
//		return table;
//	}
//
//	private static Element cloneUpperRightEl(FlexTable flexTable, int cols,
//			int rows) {
//
//		Element table = DOM.createTable();
//		Element tbody = DOM.createTBody();
//		DOM.appendChild(table, tbody);
//
//		for (int row = 0; row < rows; row++) {
//			Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
//			// remove all columns except 'cols' at left.
//			for (int i = rt.getChildCount() - 1 - cols; i >= 0; i--)
//				rt.getChild(i).removeFromParent();
//
//			DOM.appendChild(tbody, rt);
//
//		}
//
//		Style style = table.getStyle();
//		style.setPosition(Position.FIXED);
//		style.setBackgroundColor("white");
//		style.setProperty("width", "auto"); /* override width: 100% */
//
//		table.setClassName(flexTable.getElement().getClassName());
//
//		return table;
//	}
//
//	private static Element cloneHead(EvenstTable eventsTable, int cols, int rows) {
//
//		Element table = DOM.createTable();
//		Element tbody = DOM.createTBody();
//		DOM.appendChild(table, tbody);
//
//		for (int row = 0; row < rows; row++) {
//			Element rt = cloneTR(eventsTable.getRowFormatter().getElement(row));
//
//			// remove 'cols' at right.
//			for (int i = 0; i < cols; i++)
//				rt.getChild(0).removeFromParent();
//
//			DOM.appendChild(tbody, rt);
//
//		}
//
//		Style style = table.getStyle();
//		style.setPosition(Position.FIXED);
//		style.setBackgroundColor("white");
//		style.setProperty("width", "auto"); /* override width: 100% */
//
//		table.setClassName(eventsTable.getElement().getClassName());
//
//		return table;
//	}
//
//	private static Element cloneLeftColEl(FlexTable flexTable, int cols,
//			int start) {
//
//		Element table = DOM.createTable();
//		Element tbody = DOM.createTBody();
//		DOM.appendChild(table, tbody);
//
//		int rows = flexTable.getRowCount();
//
//		for (int row = start; row < rows; row++) {
//			Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
//			// remove all columns except 'cols' at right.
//			for (int i = rt.getChildCount(); i > cols; i--)
//				rt.getChild(i - 1).removeFromParent();
//
//			DOM.appendChild(tbody, rt);
//		}
//
//		Style style = table.getStyle();
//		style.setPosition(Position.FIXED);
//		style.setBackgroundColor("white");
//		style.setProperty("width", "auto"); /* override width: 100% */
//
//		table.setClassName(flexTable.getElement().getClassName());
//
//		return table;
//	}
//
//	private static Element cloneRightColEl(FlexTable flexTable, int cols,
//			int start) {
//
//		Element table = DOM.createTable();
//		Element tbody = DOM.createTBody();
//		DOM.appendChild(table, tbody);
//
//		int rows = flexTable.getRowCount();
//
//		for (int row = start; row < rows; row++) {
//			Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
//			// remove all columns except 'cols' at left.
//			for (int i = rt.getChildCount() - 1 - cols; i >= 0; i--)
//				rt.getChild(i).removeFromParent();
//
//			DOM.appendChild(tbody, rt);
//		}
//
//		Style style = table.getStyle();
//		style.setPosition(Position.FIXED);
//		style.setBackgroundColor("white");
//		style.setProperty("width", "auto"); /* override width: 100% */
//
//		table.setClassName(flexTable.getElement().getClassName());
//
//		return table;
//	}
//
//	private static void moveEl(Element el, int top, int left, int width,
//			int height) {
//
//		Style style = el.getStyle();
//		style.setTop(top, Unit.PX);
//		style.setLeft(left, Unit.PX);
//
//		setClip(style, 0, width, height, 0);
//	}
//
//	private static void moveEl(Element el, int top, int left, int clipTop,
//			int clipLeft, int clipRight, int clipBottom) {
//
//		Style style = el.getStyle();
//		style.setTop(top, Unit.PX);
//		style.setLeft(left, Unit.PX);
//
//		setClip(style, clipTop, clipRight, clipBottom, clipLeft);
//	}
//
//	private static void setClip(Style style, int top, int right, int bottom,
//			int left) {
//		style.setProperty("clip", "rect(" + top + "px," + right + "px,"
//				+ bottom + "px, " + left + "px)");
//	}
//
//	private static void hide(UIObject uiObject) {
//		uiObject.getElement().getStyle().setVisibility(Visibility.HIDDEN);
//	}
//
//	private static void show(UIObject uiObject) {
//		uiObject.getElement().getStyle().setVisibility(Visibility.VISIBLE);
//	}
//
//	private static boolean equals(Object obj1, Object obj2) {
//		if (obj1 == obj2)
//			return true;
//		if (obj1 == null)
//			return false;
//		if (obj2 == null)
//			return false;
//		return obj1.equals(obj2);
//	}
//
//}
