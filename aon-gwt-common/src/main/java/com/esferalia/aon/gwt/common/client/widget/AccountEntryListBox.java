package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.ListBox;

public class AccountEntryListBox extends ListBox {

	@SuppressWarnings("deprecation")
	public AccountEntryListBox() {
		setWidth("160px");
		addItem("","------");
		for (AccountEntryType type : AccountEntryType.values()) {
			if    (type != AccountEntryType.LEASING 
				&& type != AccountEntryType.LEASING_FEE
				&& type != AccountEntryType.INVESTMENT_INVOICE
				&& type != AccountEntryType.STOCK_VARIATION
				) {
				this.addItem( type.getDescription(), AonNumberUtils.toString(type.ordinal()) );
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setValue( AccountEntryType type) {
		if ( type == null || type == AccountEntryType.LEASING || type == AccountEntryType.LEASING_FEE) {
			setSelectedIndex(0);  
		} else {
			String value = AonNumberUtils.toString(type.ordinal());
			for (int i = 0; i < getItemCount(); i++) {
				if (value.equals(getValue(i))) {
					setSelectedIndex(i);		
				}
			}
		}
	}
	
	public AccountEntryType getValue() {
		return (getSelectedIndex()==0)
			?null	
			:AccountEntryType.values()[AonNumberUtils.toint( getValue(getSelectedIndex()))];
	}
	
}
