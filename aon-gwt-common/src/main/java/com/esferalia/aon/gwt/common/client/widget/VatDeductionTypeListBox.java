package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.google.gwt.user.client.ui.ListBox;

public class VatDeductionTypeListBox extends ListBox {
	
	public VatDeductionTypeListBox() {
		setWidth("120px");
		addItem("","------");
		for (VatDeductionType d : VatDeductionType.values()) {
			addItem(d.getName());	
		}
	}

	public void setValue(VatDeductionType type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public VatDeductionType getValue() {
		return getSelectedIndex()==0?null:VatDeductionType.values()[getSelectedIndex()-1];
	}
	
}

