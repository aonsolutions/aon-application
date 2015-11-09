package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class AccountEntryListBox extends ListBox {

	private static final CommonMessages MSGS = GWT.create(CommonMessages.class);
	
	public AccountEntryListBox() {
		setWidth("190px");
		addItem("","------");
		for (AccountEntryType type : AccountEntryType.values()) {
			this.addItem( MSGS.accountEntryType(type));	
		}
	}
	
	public void setValue( AccountEntryType type) {
		setSelectedIndex(type==null?0:type.ordinal());
	}
	
	public AccountEntryType getValue() {
		return getSelectedIndex()==0?null:AccountEntryType.values()[getSelectedIndex()-1];
	}
	
}
