package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomTextBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private TextBox textBox;
	
	public AonCustomTextBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		createTitle(title);
		createInput(title);
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput(String title) {
		textBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		textBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		textBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		textBox = new TextBox();
		textBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		textBox.getElement().setPropertyString("placeholder", "Introduce el valor");
//		textBox.getElement().setPropertyString("placeholder", AonStringUtils.isBlank(title) ? "Escriba aqui" : title);
		
		textBoxPanel.add(textBox);
		add(textBoxPanel);
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

	public void addButton(Widget button) {
		textBoxPanel.add(button);
	}

	public void addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
		textBox.addValueChangeHandler(valueChangeHandler);
	}

	public void addError() {
		addStyleName(AON.CSS.aonCustomError());
	}

	public void removeError() {
		removeStyleName(AON.CSS.aonCustomError());
	}

	public void addWarning() {
		addStyleName(AON.CSS.aonCustomWarning());
	}

	public void removeWarning() {
		removeStyleName(AON.CSS.aonCustomWarning());
	}
	
	public void setMaxWidth(String maxWidth) {
		getElement().getStyle().setProperty("max-width", maxWidth);
		textBox.getElement().getStyle().setProperty("max-width", maxWidth);
	}
	
	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		this.textBox.ensureDebugId(baseID + "Input");
	}

}
