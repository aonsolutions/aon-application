package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.ui.ListBox;

public class Country2ListBox extends ListBox {

	public Country2ListBox() {
		setWidth("40px");
		this.addItem( "-", "" );
		for (Country p : Country.values()) {
			this.addItem( p.getIso2(), p.getIso2() );	
		}
	}

	public Country getValue() {
		return Country.safeValueOf(getValue(getSelectedIndex()));
	}

	public void setValue(Country country) {
		setSelectedIndex(country==null?0:country.ordinal()+1);		
	}
	
}
