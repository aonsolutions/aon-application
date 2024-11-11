package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonCustomCheckBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private CheckBox checkBox;
	
	public AonCustomCheckBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		addStyleName(AON.CSS.aonCustomTextBoxNoBorder());

		
		createTitle(title);
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		checkBox = new CheckBox();
		add(checkBox);
	}
	
	public CheckBox getCheckBox() {
		return this.checkBox;
	}

	public void setValue(boolean value) {
		this.checkBox.setValue(value);
	}
	
	public void setValue(boolean value, boolean fireEvent) {
		this.checkBox.setValue(value, fireEvent);
	}

	public boolean getValue() {
		return this.checkBox.getValue();
	}
	
	public void setEnable(boolean enabled) {
		this.checkBox.setEnabled(enabled);
	}
	
	public void setFocus(boolean focused) {
		this.checkBox.setFocus(focused);
	}
	
	public void addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		checkBox.addValueChangeHandler(handler);
	}
	
	
	

}
