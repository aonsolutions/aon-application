package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class AonBooleanLabel extends Label {
	
	public AonBooleanLabel(boolean checkValue) {
		this.setStyleName(AON.CSS.aonIconLabel());
		this.addStyleName(checkValue? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
	}

	public AonBooleanLabel(boolean checkValue, String text) {
		this.setText(checkValue,text);
	}

	private void setText(boolean checkValue, String text) {
		this.setText(checkValue?text:"");
	}

	public AonBooleanLabel(boolean checkValue, String text, String title) {
		if ( AonStringUtils.isBlank(text)) {
			this.setStyleName(AON.CSS.aonIconLabel());
			this.addStyleName(checkValue? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		} else {
			setText(checkValue,text);	
		}
		this.setTitle(title);
	}
}
