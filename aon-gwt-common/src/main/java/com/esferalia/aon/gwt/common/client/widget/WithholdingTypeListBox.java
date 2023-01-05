package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.widget.solutions.AonListBox;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup;
import com.esferalia.aon.watson.util.AonStringUtils;

public class WithholdingTypeListBox extends AonListBox {
	
	public WithholdingTypeListBox() {
		this("------");
	}
	public WithholdingTypeListBox(String firstItemLabel) {
		setWidth("100px");
		addItem(firstItemLabel," --- ");
		WithholdingTypeGroup wtg = null;
		for (WithholdingType d : WithholdingType.ORDERED_VALUES) {
			if (wtg != d.getGroup()) {
				wtg = d.getGroup();
				addGroup(" " + AonStringUtils.BULLET + " " +  wtg.getDescription());	
			}
			addItem("   " + AonStringUtils.HYPHEN + " " + d.getDescription(), d.toString());
		}
	}

	public void setValue(WithholdingType type) {
		if (type != null ) {
			boolean found = false;
			for (int i = 0; i < getItemCount(); i++) {
				if ( !found && AonStringUtils.equals( getValue(i), type.toString())) {
					found = true;
					setSelectedIndex(i);
				}
			}
		} else {
			setSelectedIndex(0);
		}
	}

	public WithholdingType getValue() {
		return AonStringUtils.isBlank(getSelectedValue())
			? null
			: WithholdingType.valueOf( getSelectedValue() );
	}
	
}

