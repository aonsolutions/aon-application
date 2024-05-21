package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

public class AonCustomTextBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private TextBox textBox;
	
	public AonCustomTextBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		addStyleName(AON.CSS.aonCustomTextBox());

		createTitle(title);
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		textBox = new TextBox();
		textBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		add(textBox);
	}
	
	public TextBox getTextBox() {
		return this.textBox;
	}

	public void setValue(String value) {
		this.textBox.setValue(value);
	}

	public String getValue() {
		return this.textBox.getValue();
	}
	
	public void setEnable(boolean enabled) {
		this.textBox.setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		this.textBox.setFocus(focused);
	}

}
