package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceListBox extends ListBox {

	public ProvinceListBox() {
		setWidth("120px");
		for (Province p : Province.values()) {
			this.addItem( p.getName() );	
		}
	}
	
	public void setValue( Province province) {
		setSelectedIndex(province==null?0:province.ordinal());
	}
}
