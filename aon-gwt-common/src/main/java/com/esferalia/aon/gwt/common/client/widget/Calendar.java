package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonCalendarCSS;
import com.esferalia.aon.gwt.common.client.css.AonCalendarResources;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;
import com.google.gwt.user.datepicker.client.MonthSelector;

public class Calendar extends ResizeComposite implements
		ValueChangeHandler<Date> {

	public interface Listener {

		void onValueChangeEvent(ValueChangeEvent<Date> event);

	}

	private static AonCalendarCSS CALENDAR_CSS = GWT
			.<AonCalendarResources> create(AonCalendarResources.class)
			.calendar();
	private int cols;

	private Map<Date, String> statalHolidays;
	private Map<Date, String> autonomiHolidays;
	private Map<Date, String> localHolidays;

	private Date firstDate;
	private Date lastDate;

	private List<Listener> listeners;

	public Calendar() {
		this(3);
	}

	private FlexTable table;

	public Calendar(int cols) {
		this.cols = cols;
		table = new FlexTable();
		listeners = new ArrayList<Listener>();
		CALENDAR_CSS.ensureInjected();
		table.setStylePrimaryName(CALENDAR_CSS.aonCalendar());
		initWidget(table);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void setStatalHolidays(Map<Date, String> dates) {
		this.statalHolidays = dates;
	}

	public void setAutonomiHolidays(Map<Date, String> autonomiHolidays) {
		this.autonomiHolidays = autonomiHolidays;
	}

	public void setLocalHolidays(Map<Date, String> localHolidays) {
		this.localHolidays = localHolidays;
	}

	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
		if (isAttached())
			initTable();
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
		if (isAttached())
			initTable();
	}

	private void initTable() {
		table.clear();
		int row = 0;
		int col = 0;
		Date date = CalendarUtil.copyDate(firstDate);
		CalendarUtil.setToFirstDayOfMonth(date);

		while (lastDate.after(date)) {
			if (col == 0)
				table.insertRow(row);
			table.insertCell(row, col);

			final CustomDatePicker datePicker = new CustomDatePicker();
			datePicker.getStyleOfDate(date);
			datePicker.setVisibleYearCount(1);
			datePicker.setCurrentMonth(date);
			datePicker.setYearAndMonthDropdownVisible(false);
			datePicker.setYearArrowsVisible(false);
			datePicker.addValueChangeHandler(this);
			if (statalHolidays != null)
				setStatalHoliday(datePicker, datePicker.getFirstDate(),
						datePicker.getLastDate());

			if (autonomiHolidays != null)
				setAutonomiHoliday(datePicker, datePicker.getFirstDate(),
						datePicker.getLastDate());

			if (localHolidays != null)
				setLocalHoliday(datePicker, datePicker.getFirstDate(),
						datePicker.getLastDate());

			table.setWidget(row, col, datePicker);
			CalendarUtil.addMonthsToDate(date, 1);
			row += ++col / cols;
			col = col % cols;
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Date> event) {
		for (Listener listener : listeners)
			listener.onValueChangeEvent(event);
	}

	private void setStatalHoliday(final DatePicker datePicker, Date first,
			Date last) {

		for (Date date : statalHolidays.keySet()) {
			if (DateUtils.isBeforeOrEquals(date, last)
					&& DateUtils.isAfterOrEquals(date, first))
				datePicker.addStyleToDates(CALENDAR_CSS.statalHoliday(), date);
		}
	}

	private void setAutonomiHoliday(final DatePicker datePicker, Date first,
			Date last) {

		for (Date date : autonomiHolidays.keySet()) {
			if (DateUtils.isBeforeOrEquals(date, last)
					&& DateUtils.isAfterOrEquals(date, first))
				datePicker
						.addStyleToDates(CALENDAR_CSS.autonomiHoliday(), date);
		}
	}

	private void setLocalHoliday(final DatePicker datePicker, Date first,
			Date last) {

		for (Date date : localHolidays.keySet()) {
			if (DateUtils.isBeforeOrEquals(date, last)
					&& DateUtils.isAfterOrEquals(date, first))
				datePicker.addStyleToDates(CALENDAR_CSS.localHoliday(), date);
		}
	}

	// -------------------------------------------------------------------------
	@Override
	protected void onAttach() {
		initTable();
		super.onAttach();
	}

	public class CustomDatePicker extends DatePicker {

		public CustomDatePicker() {
			super(new MonthAndYearSelector(), new DefaultCalendarView(),
					new CalendarModel());
			MonthAndYearSelector monthSelector = (MonthAndYearSelector) this
					.getMonthSelector();
			monthSelector.setPicker(this);
			monthSelector.setModel(this.getModel());
		}

		public void refreshComponents() {
			super.refreshAll();
		}

	}

	private class MonthAndYearSelector extends MonthSelector {

		private static final String BASE_NAME = "datePicker";

		private PushButton backwards;
		private PushButton forwards;
		private PushButton backwardsYear;
		private PushButton forwardsYear;
		private Grid grid;
		private int previousYearColumn = 0;
		private int previousMonthColumn = 1;
		private int monthColumn = 2;
		private int nextMonthColumn = 3;
		private int nextYearColumn = 4;
		private CalendarModel model;
		private CustomDatePicker picker;

		public void setModel(CalendarModel model) {
			this.model = model;
		}

		public void setPicker(CustomDatePicker picker) {
			this.picker = picker;
		}

		@Override
		protected void refresh() {
			Date date = getModel().getCurrentMonth();
			String name = DateTimeFormat.getFormat("d-MMMM-yy").format(date).split("-")[1];			
			grid.setText(0, monthColumn, firstCharToUpper(name));
		}
		
		@Override
		protected void setup() {
			
			// Set up grid.
			grid = new Grid(1, 5);
			grid.setWidget(0, previousYearColumn, backwardsYear);
			grid.setWidget(0, previousMonthColumn, backwards);
			grid.setWidget(0, nextMonthColumn, forwards);
			grid.setWidget(0, nextYearColumn, forwardsYear);

			CellFormatter formatter = grid.getCellFormatter();
			formatter.setStyleName(0, monthColumn, BASE_NAME + "Month");
			formatter.setWidth(0, previousYearColumn, "1");
			formatter.setWidth(0, previousMonthColumn, "1");
			formatter.setWidth(0, monthColumn, "100%");
			formatter.setWidth(0, nextMonthColumn, "1");
			formatter.setWidth(0, nextYearColumn, "1");
			grid.setStyleName(BASE_NAME + "MonthSelector");
			initWidget(grid);
		}

		public void addMonths(int numMonths) {
			model.shiftCurrentMonth(numMonths);
			picker.refreshComponents();
		}

		private String firstCharToUpper(String name) {
			String mayus = String.valueOf(name.charAt(0));
			mayus = mayus.toUpperCase();
			name = name.replaceFirst(String.valueOf(name.charAt(0)), mayus);
			return name;
		}
	}

}
