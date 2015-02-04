package com.esferalia.aon.gwt.common.client.widget;

import java.util.Date;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DatePicker;

public class Calendar extends ResizeComposite {

	private int cols ;

	private Date firstDate;
	private Date lastDate;

	public Calendar() {
		this(3);
	}
	
	private FlexTable table;

	public Calendar(int cols) {
		this.cols = cols;
		table = new FlexTable();
		table.setStylePrimaryName("gwt-Calendar");
		initWidget(table);
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
			DatePicker datePicker = new DatePicker();
			datePicker.setVisibleYearCount(1);
			datePicker.setCurrentMonth(date);
			table.setWidget(row, col, datePicker);
			CalendarUtil.addMonthsToDate(date, 1);
			row += ++col / cols;
			col = col % cols;
		}
	}

	// -------------------------------------------------------------------------
	@Override
	protected void onAttach() {
		initTable();
		super.onAttach();
	}
}
