package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.StreetType;
import com.google.gwt.user.client.ui.ListBox;

public class StreetTypeListBox extends ListBox {

	public StreetTypeListBox() {
		setWidth("120px");
		this.addItem( "-", "" );
		for (StreetType p : StreetType.values()) {
			this.addItem( p.getDescription(), p.getIneCode());	
		}
	}
	
	public void setValue( StreetType type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}
	
	public StreetType getValue() {
		return getSelectedIndex()==0?null:StreetType.values()[getSelectedIndex()-1];
	}
}

