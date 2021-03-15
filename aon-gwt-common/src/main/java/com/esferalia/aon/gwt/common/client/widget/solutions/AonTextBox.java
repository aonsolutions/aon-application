package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.TextBox;

public class AonTextBox extends TextBox {

	public AonTextBox() {
		super();
		setStyleName(AON.CSS.aonInputText());
	}
}
