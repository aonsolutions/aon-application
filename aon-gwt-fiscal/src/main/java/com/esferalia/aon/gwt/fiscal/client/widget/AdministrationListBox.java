package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.FiscalEnum.Administration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class AdministrationListBox extends ListBox {

	
	public AdministrationListBox() {
		setWidth("130px");
		FiscalMessages msgs = (FiscalMessages) GWT.create(FiscalMessages.class);
		
		this.addItem( msgs.administrationName( Administration.ALAVA));
		this.addItem( msgs.administrationName( Administration.BIZKAIA));
		this.addItem( msgs.administrationName( Administration.GIPUZKOA));
		this.addItem( msgs.administrationName( Administration.NAVARRA));
		this.addItem( msgs.administrationName( Administration.COMMON_TERRITORY ));
	}
	
	
}
