package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.ListBox;

public class PeriodListBox extends ListBox {

	boolean monthPeriods;
	
	public PeriodListBox() {
		this(true);
	}
	public PeriodListBox(boolean monthPeriods) {
		this.monthPeriods = monthPeriods;
		setWidth("90px");
		this.addItem(" --- ", "");
		for (Period p : Period.values()) {
			boolean insert = true;
			if (p == Period.YEAR ) insert = false;
			if (insert && !monthPeriods && p.isMonthPeriod()) insert = false;
			if (insert) this.addItem( p.getDescription(), Integer.toString( p.ordinal() ) );	
		}
	}

	public void setValue(Period period) {
		if (period == null) {
			setSelectedIndex(0);
		} else {
			setSelectedIndex(period.ordinal() - (monthPeriods?0:12) + 1);
		}
	}
	
	public Period getValue() {
		if (getSelectedIndex() == 0) return null;
		return Period.values()[ AonNumberUtils.toInteger( getSelectedValue() ) ];
	}
}
