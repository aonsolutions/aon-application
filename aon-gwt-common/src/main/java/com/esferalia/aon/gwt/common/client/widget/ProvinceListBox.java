package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class ProvinceListBox extends ListBox {

	
	public ProvinceListBox() {
		setWidth("120px");
		CommonMessages msgs = (CommonMessages) GWT.create(CommonMessages.class);
		
		for (Province p : Province.values()) {
			this.addItem( msgs.provinceName( p ));	
		}
	}
	
}
