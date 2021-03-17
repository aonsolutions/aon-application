package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.google.gwt.user.client.ui.ListBox;

public class PayMethodTypeListBox extends ListBox {

	public PayMethodTypeListBox() {
		setWidth("120px");
		for (PayMethodType d : PayMethodType.values()) {
			addItem(d.getDescription());	
		}
	}

	public void setValue(PayMethodType payMethodType) {
		setSelectedIndex((payMethodType == null ? PayMethodType.CASH_BASIS : payMethodType).ordinal());
	}

	public PayMethodType getValue() {
		return PayMethodType.values()[getSelectedIndex()];
	}
	
}

