package com.esferalia.aon.gwt.fiscal.client.mod390.e2024;


import com.esferalia.aon.occam.api.model.fiscal.ActivityType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.ListBox;

public class ActivityTypeListBox extends ListBox {

	public ActivityTypeListBox() {
		this("---");
	}	
	
	public ActivityTypeListBox(String firstItemLabel) {
		setWidth("60px");
		addItem(AonStringUtils.defaultIfBlank(firstItemLabel),"");
		for (ActivityType d : ActivityType.values()) {
			addItem(d.toString() + " - " + d.getDescription(),d.toString());	
		}
	}

	public void setValue(ActivityType type) {
		setSelectedIndex(type==null?0:type.ordinal()+1);
	}

	public ActivityType getValue() {
		return getSelectedIndex()==0?null:ActivityType.values()[getSelectedIndex()-1];
	}
	
}

