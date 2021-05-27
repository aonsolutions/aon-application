package com.esferalia.aon.gwt.common.client.widget;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.ListBox;

public class PayMethodListBox extends ListBox {
	
	public PayMethodListBox() {
		setWidth("120px");
		PayMethodListBox.this.addItem( "----","-1" );
	}
	
	public void fill(LinkedList<PayMethod> result) {
		for (PayMethod p : result) {
			addItem( p.getName(), AonNumberUtils.toString(p.getId()) );
		}
	}
	
	public void setValue(Integer payMethodId) {
		boolean found = false;
		if (payMethodId != null) {
			for (int i = 0 ;  i < getItemCount(); i++) {
				int a = AonNumberUtils.toInteger( getValue(i) );
				int b = payMethodId;
				if ( a == b) {
					setSelectedIndex(i);
					found = true;
					break;
				}
			}
		}
		if (!found) setSelectedIndex(0);
	}

	public Integer getValue() {
		if (getSelectedIndex() == 0) return null;
		return AonNumberUtils.toInteger( getSelectedValue() );
	}
}
