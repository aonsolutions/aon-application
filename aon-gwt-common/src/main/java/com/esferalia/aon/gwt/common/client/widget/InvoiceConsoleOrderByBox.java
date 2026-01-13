package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams.OrderBy;
import com.google.gwt.user.client.ui.ListBox;

public class InvoiceConsoleOrderByBox extends ListBox {

	public InvoiceConsoleOrderByBox() {
		setWidth("120px");
		for (OrderBy d : OrderBy.values()) {
			String desc = d.getLabel(); 
			addItem(desc);
		}
		setSelectedIndex(0);
	}

	public void setValue(OrderBy type) {
		setSelectedIndex(type==null?0:type.ordinal());
	}

	public OrderBy getValue() {
		return OrderBy.values()[getSelectedIndex()];
	}
	
}

