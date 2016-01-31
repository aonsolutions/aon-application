package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.user.client.ui.ListBox;

public class MonthPeriodListBox extends ListBox {

	
	public MonthPeriodListBox() {
		setWidth("40px");
		this.addItem(" --- ", "");
		for (Period p : Period.values()) {
			if (p.isMonthPeriod()) {
				this.addItem( p.getName(), Integer.toString( p.ordinal() ) );
			}
		}
	}

	public void setValue(Period period) {
		if (period == null) {
			setSelectedIndex(0);
		} else {
			setSelectedIndex(period.ordinal() + 1);
		}
	}
	
	
}
