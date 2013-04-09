package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ListBox;

public class MonthListBox extends ListBox {

	private static int VISIBLE_MONTHS = 25;

	private Date lastMonth;
	private Date firstMonth;
	private DateTimeFormat dateTimeFormat;
	
	public MonthListBox() {
	}

	public void setLastMonth(Date lastMonth) {
		this.lastMonth = lastMonth != null ? DateUtils.getFirstDayOfMonth(lastMonth) : null;
	}

	public void setFirstMonth(Date firstMonth) {
		this.firstMonth = DateUtils.getFirstDayOfMonth(firstMonth);
	}

	public void setDateTimeFormat(DateTimeFormat dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public Date getSelectedMonth() {
		return dateTimeFormat.parse(getItemText(getSelectedIndex()));
	}

	public void setSelectedMonth(Date month) {
		
		month = DateUtils.getFirstDayOfMonth(month);
		
		// Date d = CalendarUtil.addMonthsToDate(date, months)
		Date date = DateUtils.getFirstDayOfMonth(month);
		
		DateUtils.addMonths2Date(date, (-1) * Math.min(DateUtils.getMonths(month, firstMonth), VISIBLE_MONTHS/2));
		while ( date.before(month)) {
			addItem(date);
			DateUtils.addMonths2Date(date, 1);
		}
		addItem(date);
		DateUtils.addMonths2Date(date, 1);
		while ( DateUtils.isAfterOrEquals(lastMonth, date) && ( getItemCount() <= VISIBLE_MONTHS ) ){
			addItem(date);
			DateUtils.addMonths2Date(date, 1);
		}
		

	}
	
	
	private void addItem(Date date) {
		addItem(dateTimeFormat.format(date));
	}
}
