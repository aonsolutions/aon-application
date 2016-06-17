package com.esferalia.aon.gwt.common.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.AonCalendarCSS;
import com.esferalia.aon.gwt.common.client.css.AonCalendarResources;
import com.esferalia.aon.gwt.common.shared.CustomDatePicker;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class Calendar extends ResizeComposite implements
		ValueChangeHandler<Date>, KeyDownHandler {
	
	public interface Listener {

		void onValueChangeEvent(Date date);
		
		void onSuprPressEvent(Date date);
		
		void onEnterPressEvent(Date date);
	}
	
	interface SelectionState {
		
		public void doAction(ValueChangeEvent<Date> event);
	}
	
	class SimpleSelectedState implements SelectionState {

		@Override
		public void doAction(ValueChangeEvent<Date> event) {
			
		}
	}

	private static AonCalendarCSS CALENDAR_CSS = GWT
			.<AonCalendarResources> create(AonCalendarResources.class)
			.calendar();
	private int cols;

	private Date firstDate;
	private Date lastDate;
	
	private Date dateSelected;

	private List<Listener> listeners;
	private List<ValueChangeEvent<Date>> selections;
	
	private SimpleSelectedState simpleSelected;
	
	public Calendar() {
		this(3);
	}

	private FlexTable table;

	public Calendar(int cols) {
		this.cols = cols;
		table = new FlexTable();
		listeners = new ArrayList<Listener>();
		selections = new ArrayList<ValueChangeEvent<Date>>();
		CALENDAR_CSS.ensureInjected();
		table.setStylePrimaryName(CALENDAR_CSS.aonCalendar());
		simpleSelected = new SimpleSelectedState();
		initWidget(table);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
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
			
			CustomDatePicker datePicker = new CustomDatePicker();
			
			datePicker.getStyleOfDate(date);
			datePicker.setVisibleYearCount(1);
			datePicker.setCurrentMonth(date);
			datePicker.setYearAndMonthDropdownVisible(false);
			datePicker.setYearArrowsVisible(false);
			datePicker.addValueChangeHandler(this);			
			datePicker.addKeyDownHandler(this);
			datePicker.sinkEvents(Event.ONKEYDOWN);
			table.setWidget(row, col, datePicker);
			CalendarUtil.addMonthsToDate(date, 1);
			row += ++col / cols;
			col = col % cols;
		}
	}

	public void addStyle2Date(Date date, String style) {
		setStyle(style, date);
	}

	private void setStyle(String style, Date date) {
		Iterator<Widget> iterator = getTableIterator();
		
		while (iterator.hasNext()) {
			Widget widget = iterator.next();
			if (widget instanceof CustomDatePicker) {
				CustomDatePicker datePicker = (CustomDatePicker) widget;
				
				if(datePicker.isDatePicker(date))
					datePicker.addStyleToDates(style, date);
			}
		}
	}

	private Iterator<Widget> getTableIterator() {
		return ((HasWidgets) table).iterator();
	}
	
	private Date getDateSelected() {
		return this.dateSelected;
	}

	@Override
	public void onValueChange(ValueChangeEvent<Date> event) {
				
		this.dateSelected = event.getValue();		
		
		for (Listener listener : listeners)
			listener.onValueChangeEvent(dateSelected);
	}


	@Override
	public void onKeyDown(KeyDownEvent event) {
		
		int keyCode = event.getNativeKeyCode();
		
		if (keyCode == KeyCodes.KEY_DELETE)
			onSuprPress(getDateSelected());
			
		else if (keyCode == KeyCodes.KEY_ENTER)
			onEnterPress(getDateSelected());

	}
	
	public void addStyleToDay(String styleName, int dayOfWeek) {
		Iterator<Widget> iterator = table.iterator();
		while ( iterator.hasNext() )
			((CustomDatePicker) iterator.next()).addStyleToDay(styleName, dayOfWeek);
	}

	public void removeStyleFromDay(String styleName, int dayOfWeek) {
		Iterator<Widget> iterator = table.iterator();
		while ( iterator.hasNext() )
			((CustomDatePicker) iterator.next()).removeStyleFromDay(styleName, dayOfWeek);
	}



	// -------------------------------------------------------------------------

	private final void onSuprPress (Date date) {
		for(Listener listener : listeners)
			listener.onSuprPressEvent(date);
	}
	
	private final void onEnterPress (Date date) {
		for (Listener listener : listeners)
			listener.onEnterPressEvent(date);
	}
	
	// -------------------------------------------------------------------------
	@Override
	protected void onAttach() {
		initTable();
		super.onAttach();
	}
}
