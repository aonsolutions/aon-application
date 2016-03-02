package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.ui.ListBox;

public class AdministrationListBox extends ListBox {

	
	public AdministrationListBox() {
		setWidth("130px");
		this.addItem( Administration.ALAVA.getDescription());
		this.addItem( Administration.BIZKAIA.getDescription());
		this.addItem( Administration.GIPUZKOA.getDescription());
		this.addItem( Administration.NAVARRA.getDescription());
		this.addItem( Administration.COMMON_TERRITORY.getDescription());
	}

	public Administration getValue() {
		return Administration.values()[ getSelectedIndex()];
	}
	
	
}
