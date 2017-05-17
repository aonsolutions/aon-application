package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

@Deprecated //Use ProvinceListBox or ProvinceCountryListBox
public class MunicipalityListBox extends ListBox {

	private static final CommonMessages MSGS = GWT.create(CommonMessages.class);
	
	public MunicipalityListBox() {
		setWidth("120px");
		for (Province p : Province.values()) {
			this.addItem( MSGS.provinceName( p ));	
		}
	}
	
}
