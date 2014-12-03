package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceListBox extends ListBox {

	private static final CommonMessages MSGS = GWT.create(CommonMessages.class);
	
	public ProvinceListBox() {
		setWidth("120px");
		for (Province p : Province.values()) {
			this.addItem( MSGS.provinceName( p ));	
		}
	}
	
}
