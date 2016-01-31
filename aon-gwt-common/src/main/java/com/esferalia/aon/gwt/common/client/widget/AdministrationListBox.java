package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class AdministrationListBox extends ListBox {

	
	public AdministrationListBox() {
		setWidth("130px");
		CommonMessages msgs = (CommonMessages) GWT.create(CommonMessages.class);
		
		this.addItem( msgs.administrationName( Administration.ALAVA));
		this.addItem( msgs.administrationName( Administration.BIZKAIA));
		this.addItem( msgs.administrationName( Administration.GIPUZKOA));
		this.addItem( msgs.administrationName( Administration.NAVARRA));
		this.addItem( msgs.administrationName( Administration.COMMON_TERRITORY ));
	}

	public Administration getValue() {
		return Administration.values()[ getSelectedIndex()];
	}
	
	
}
