package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class InvoiceCommunicationTypeBox extends ListBox {

	public InvoiceCommunicationTypeBox() {
		this("------");
	}	

	public InvoiceCommunicationTypeBox(String firstItemLabel) {
		setWidth("120px");
		addItem(AonStringUtils.defaultIfBlank(firstItemLabel),"");
		for (InvoiceCommunicationType d : InvoiceCommunicationType.values()) {
			String desc = d.name(); 
			addItem(desc);
		}
	}

	public void setValue(InvoiceCommunicationType type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public InvoiceCommunicationType getValue() {
		return getSelectedIndex()==0?null:InvoiceCommunicationType.values()[getSelectedIndex()-1];
	}
	
}

