package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class InvoiceSourceBox extends ListBox {

	public InvoiceSourceBox() {
		this("------", false);
	}	

	public InvoiceSourceBox(String firstItemLabel) {
		this(firstItemLabel, false);
	}
	
	public InvoiceSourceBox(String firstItemLabel, boolean forCanarias) {
		setWidth("120px");
		addItem(AonStringUtils.defaultIfBlank(firstItemLabel),"");
		for (InvoiceSource d : InvoiceSource.values()) {
			String desc = d.getDescription(); 
			addItem(desc);
		}
	}

	public void setValue(InvoiceSource type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public InvoiceSource getValue() {
		return getSelectedIndex()==0?null:InvoiceSource.values()[getSelectedIndex()-1];
	}
	
}

