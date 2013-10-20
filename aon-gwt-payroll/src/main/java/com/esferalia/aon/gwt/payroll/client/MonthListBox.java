package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

public class MonthListBox extends ListBox {

	private static int VISIBLE_MONTHS = 25;

	private Date lastMonth;
	private Date firstMonth;
	private DateTimeFormat dateTimeFormat;
	
	public MonthListBox() {
		
		// Standard base time known as "the epoch", namely January 1, 1970,
		// 00:00:00 GMT.
		firstMonth = new Date(0);
		
		
		addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
			}
		});
		addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
			}
		});
		addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
			}
		});
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
		int selected = getItemCount();
		addItem(date);
		DateUtils.addMonths2Date(date, 1);
		while ( DateUtils.isAfterOrEquals(lastMonth, date) && ( getItemCount() <= VISIBLE_MONTHS ) ){
			addItem(date);
			DateUtils.addMonths2Date(date, 1);
		}
		
		setSelectedIndex(selected);
	}
	
	
	private void addItem(Date date) {
		addItem(dateTimeFormat.format(date));
	}
	
	
}
