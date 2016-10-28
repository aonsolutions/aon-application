package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.google.gwt.user.client.ui.ListBox;

public class AccountingRegistryTypeListBox extends ListBox {
	
	public AccountingRegistryTypeListBox() {
		setWidth("120px");
		addItem("","------");
		for (AccountingRegistryType d : AccountingRegistryType.values()) {
			addItem(d.getDescription());	
		}
	}

	public void setValue(AccountingRegistryType type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public AccountingRegistryType getValue() {
		return getSelectedIndex()==0?null:AccountingRegistryType.values()[getSelectedIndex()-1];
	}
	
}

