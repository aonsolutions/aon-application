package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonCustomToogleButton extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel toogleButtonPanel;
	private Button toogleButton;
	
	public AonCustomToogleButton(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		createTitle(title);
		createToogleButton();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(title);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createToogleButton() {
		toogleButtonPanel = new HTMLPanel("");
		toogleButtonPanel.getElement().getStyle().setProperty("padding-left", ".5rem");
		
		toogleButton = new Button();
		setValue(false);
		toogleButton.addClickHandler(e -> setValue(!getValue()));
		toogleButtonPanel.add(toogleButton);
		
		add(toogleButtonPanel);
	}
	
	public void setValue(boolean value) {
		getEnableDisableButton(toogleButton, value);
	}

	public boolean getValue() {
		return isActiveToggleButton(toogleButton);
	}
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

}
