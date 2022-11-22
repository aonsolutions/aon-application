package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.ui.ListBox;

public class AdministrationListBox extends ListBox {

	
	public AdministrationListBox() {
		this(false, false);
	}

	public AdministrationListBox(boolean onlyForal) {
		this(onlyForal, false);
	}

	public AdministrationListBox( boolean onlyForal, boolean hideNavarra) {
		setWidth("130px");
		this.addItem( Administration.ALAVA.getDescription());
		this.addItem( Administration.BIZKAIA.getDescription());
		this.addItem( Administration.GIPUZKOA.getDescription());
		if (!hideNavarra) {
			this.addItem( Administration.NAVARRA.getDescription());
		}
		if (!onlyForal)
			this.addItem( Administration.COMMON_TERRITORY.getDescription());
	}

	public Administration getValue() {
		return Administration.values()[ getSelectedIndex()];
	}
	
	
}
