package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceCountryListBox extends ListBox {

	
	public ProvinceCountryListBox() {
		setWidth("200px");
		CommonMessages msgs = (CommonMessages) GWT.create(CommonMessages.class);
		
		for (Province p : Province.values()) {
			this.addItem( msgs.provinceName( p ));	
		}
		for (Country c : Country.values()) {
			this.addItem( c.getName() );	
		}
	}
	
}
