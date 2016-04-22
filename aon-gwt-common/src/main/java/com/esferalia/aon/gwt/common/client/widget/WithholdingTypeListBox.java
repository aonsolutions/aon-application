package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.google.gwt.user.client.ui.ListBox;

public class WithholdingTypeListBox extends ListBox {
	
	public WithholdingTypeListBox() {
		setWidth("120px");
		addItem("","------");
		for (WithholdingType d : WithholdingType.values()) {
			addItem(d.getDescription());	
		}
	}

	public void setValue(WithholdingType type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public WithholdingType getValue() {
		return getSelectedIndex()==0?null:WithholdingType.values()[getSelectedIndex()-1];
	}
	
}

