package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.StreetType;
import com.google.gwt.user.client.ui.ListBox;

public class StreetTypeListBox extends ListBox {

	public StreetTypeListBox() {
		setWidth("120px");
		for (StreetType p : StreetType.values()) {
			this.addItem( p.getDescription());	
		}
	}
	
}
