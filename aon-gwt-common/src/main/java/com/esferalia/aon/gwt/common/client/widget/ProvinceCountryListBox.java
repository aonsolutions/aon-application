package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceCountryListBox extends ListBox {

	
	public ProvinceCountryListBox() {
		setWidth("200px");
		for (Province p : Province.values()) {
			this.addItem( p.getName() );	
		}
		for (Country c : Country.values()) {
			this.addItem( c.getName() );	
		}
	}
	
}
