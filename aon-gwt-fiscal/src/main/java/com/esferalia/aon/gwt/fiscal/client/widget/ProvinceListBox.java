package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceListBox extends ListBox {

	
	public ProvinceListBox() {
		setWidth("120px");
		FiscalMessages msgs = (FiscalMessages) GWT.create(FiscalMessages.class);
		
		for (Province p : Province.values()) {
			this.addItem( msgs.provinceName( p ));	
		}
	}
	
}
