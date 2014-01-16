package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;

public class ListMonthListBox extends MonthListBox {

	private List<Date> months = null;

	public ListMonthListBox() {
		super();
	}


	public void setMonths(List<Date> months) {
		this.months = months;
	}
	
	@Override
	public void setSelected(Date t, boolean selected) {
		super.setSelected(t, selected);
	}

	// ------------------------------------------------------------------------
	@Override
	protected Date getMonth(int index) {
		return months == null ? null : months.get(index);
	}
	
	@Override
	protected int getIndex(Date month) {
		return months == null ? -1 : months.indexOf(month);
	}
	
	@Override
	public void setVisibleRange(int start, int length) {
		int size = months == null ? 0: months.size();
		start = Math.min(start, size);
		length = Math.min(length, size - start);
		super.setVisibleRange(Math.max(start,0), Math.max(length,0));
	}

	@Override
	protected List<Date> getMonths(int start, int length) {
		if ( months == null )
			return Collections.emptyList();
		else
			return months.subList(start, start + length);
	}
	
	
	

}
