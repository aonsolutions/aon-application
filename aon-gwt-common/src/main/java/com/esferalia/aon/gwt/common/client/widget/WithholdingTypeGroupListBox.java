package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.widget.solutions.AonListBox;
import com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup;
import com.esferalia.aon.watson.util.AonStringUtils;

public class WithholdingTypeGroupListBox extends AonListBox {
	
	public WithholdingTypeGroupListBox() {
		this("------");
	}
	public WithholdingTypeGroupListBox(String firstItemLabel) {
		setWidth("100px");
		addItem(firstItemLabel,"");
		for (WithholdingTypeGroup d : WithholdingTypeGroup.values()) {
			addItem(d.getDescription(), d.toString());
		}
	}

	public void setValue(WithholdingTypeGroup group) {
		if (group != null ) {
			boolean found = false;
			for (int i = 0; i < getItemCount(); i++) {
				if ( !found && AonStringUtils.equals( getValue(i), group.toString())) {
					found = true;
					setSelectedIndex(i);
				}
			}
		} else {
			setSelectedIndex(0);
		}
	}

	public WithholdingTypeGroup getValue() {
		return AonStringUtils.isBlank(getSelectedValue())
			? null
			: WithholdingTypeGroup.valueOf( getSelectedValue() );
	}
	
}

