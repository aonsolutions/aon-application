package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.google.gwt.user.client.ui.ListBox;

public class InvoiceTransactionListBox extends ListBox {
	
	public InvoiceTransactionListBox() {
		setWidth("120px");
		addItem("","------");
		for (InvoiceTransactionType d : InvoiceTransactionType.values()) {
			addItem(d.getDescription());	
		}
	}

	public void setValue(InvoiceTransactionType transaction) {
		setSelectedIndex(transaction==null?0:transaction.ordinal()+1);
	}

	public InvoiceTransactionType getValue() {
		return getSelectedIndex()==0?null:InvoiceTransactionType.values()[getSelectedIndex()-1];
	}
	
}

