package com.esferalia.aon.gwt.common.shared;

import static com.esferalia.aon.gwt.common.shared.DateUtils.addDays2Date;
import static com.esferalia.aon.gwt.common.shared.DateUtils.copyDateOnly;

import java.time.DayOfWeek;
import java.util.Date;

import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;
import com.google.gwt.user.datepicker.client.MonthSelector;

public class CustomDatePicker extends DatePicker implements HasKeyDownHandlers {
	
	static class MonthAndYearSelector extends MonthSelector {

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
			String name = DateTimeFormat.getFormat("d-MMMM-yy").format(date)
					.split("-")[1];
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
	
	
	
	public CustomDatePicker() {
		super(new MonthAndYearSelector() , new DefaultCalendarView(),
				new CalendarModel());
		MonthAndYearSelector monthSelector = (MonthAndYearSelector) this
				.getMonthSelector();
		monthSelector.setPicker(this);
		monthSelector.setModel(this.getModel());
	}

	public void refreshComponents() {
		super.refreshAll();

	}
	
	public boolean isDatePicker(Date date) {
		
		return DateUtils.isBeforeOrEquals(date, getLastDate()) 
				&& DateUtils.isAfterOrEquals(date, getFirstDate());
		
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addHandler(handler, KeyDownEvent.getType());
	}
	
	// ------------------------------------------------------------------------

	public void addStyleToDay(String styleName, int dayOfWeek) {
    	Date date = copyDateOnly(getFirstDate());
    	
    	while( date.compareTo(getLastDate())<=0  ) {
    		if ( date.getDay() == dayOfWeek)
    			super.addStyleToDates(styleName, date);
    		addDays2Date(date, 1);
    	}
    }

    public void addStyleToDays(String styleName, int ...daysOfWeek) {
    	for (int dayOfWeek : daysOfWeek)
			addStyleToDay(styleName, dayOfWeek);
    }

    public void removeStyleFromDay(String styleName, int dayOfWeek) {
    	Date date = copyDateOnly(getFirstDate());
    	
    	while( date.compareTo(getLastDate())<=0  ) {
    		if ( date.getDay() == dayOfWeek)
    			super.removeStyleFromDates(styleName, date);
    		addDays2Date(date, 1);
    	}
    }

    public void removeStyleFromDays(String styleName, int ...daysOfWeek) {
    	for (int dayOfWeek : daysOfWeek)
			removeStyleFromDay(styleName, dayOfWeek);
    }
}