package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.ui.ListBox;

public class CountryListBox extends ListBox {

	public CountryListBox() {
		setWidth("120px");
		this.addItem( "-", "" );
		for (Country p : Country.values()) {
			this.addItem( p.getName(), p.getIso2() );	
		}
	}
	
}
