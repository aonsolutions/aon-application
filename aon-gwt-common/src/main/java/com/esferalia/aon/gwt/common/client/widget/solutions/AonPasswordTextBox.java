package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class AonPasswordTextBox extends PasswordTextBox {

	public AonPasswordTextBox() {
		super();
		setStyleName(AON.CSS.aonInputText());
	}
	
	public void decorateAsError() {
		addStyleName(AON.CSS.aonInputError());
	}
	public void decorateAsValid() {
		removeStyleName(AON.CSS.aonInputError());
	}
	
}
