package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class InvoiceCommunicationStatusBox extends ListBox {

	public InvoiceCommunicationStatusBox() {
		this("------", false);
	}	

	public InvoiceCommunicationStatusBox(String firstItemLabel) {
		this(firstItemLabel, false);
	}
	
	public InvoiceCommunicationStatusBox(String firstItemLabel, boolean forCanarias) {
		setWidth("120px");
		addItem(AonStringUtils.defaultIfBlank(firstItemLabel),"");
		for (InvoiceCommunicationStatus d : InvoiceCommunicationStatus.values()) {
			String desc = d.name(); 
			addItem(desc);
		}
	}

	public void setValue(InvoiceCommunicationStatus type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public InvoiceCommunicationStatus getValue() {
		return getSelectedIndex()==0?null:InvoiceCommunicationStatus.values()[getSelectedIndex()-1];
	}
	
}

