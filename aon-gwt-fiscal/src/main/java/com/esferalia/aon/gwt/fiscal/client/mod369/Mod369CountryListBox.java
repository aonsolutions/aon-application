package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.ui.ListBox;

public class Mod369CountryListBox extends ListBox {

	public Mod369CountryListBox() {
		setWidth("170px");		
		this.addItem( "-", "" );
		for (Country p : Country.values()) {
			if (p.isIntracommunityCountry() || p == Country.ES)
				this.addItem( p.getName(), p.getIso2() );	
		}
	}

	public Country getValue() {
		return Country.safeValueOf(getValue(getSelectedIndex()));
	}

	public void setValue(Country country) {		
		int index = 0;
		if (country != null) {
			for (int i=1; i<getItemCount(); i++) {
				if (getValue(i) == country.getIso2()) {
					index = i;
					break;				
				}	
			}					
		}
		setSelectedIndex(index);		
	}
	
}
