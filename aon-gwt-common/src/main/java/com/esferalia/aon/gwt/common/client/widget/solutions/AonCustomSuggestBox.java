package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class AonCustomSuggestBox extends HTMLPanel {
	
	private static final String EMPTY_STRING = "";
	private HTMLPanel suggestBoxPanel = new HTMLPanel(EMPTY_STRING);
	private SuggestBox suggestBox;
	
	private AonCustomSuggestOracle customOracle;
	
	public AonCustomSuggestBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		createTitle(title);
		createInput();
	}
	
	public AonCustomSuggestBox(String title, AonCustomSuggestOracle customOracle) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		this.customOracle = customOracle;
		
		createTitle(title);
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		suggestBoxPanel.addStyleName(AON.CSS.aonItemFlex());
		suggestBoxPanel.addStyleName(AON.CSS.aonFlexBetween());
		suggestBoxPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		if(null == customOracle) suggestBox = new SuggestBox();
		else suggestBox = new SuggestBox(customOracle);
		
		suggestBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		suggestBoxPanel.add(suggestBox);
		add(suggestBoxPanel);
	}
	
	public SuggestBox getSuggestBox() {
		return this.suggestBox;
	}

	public void setValue(String value) {
		this.suggestBox.setValue(value);
	}

	public void setValue(String value, boolean fireEvent) {
		this.suggestBox.setValue(value, fireEvent);
	}

	public String getValue() {
		return this.suggestBox.getValue();
	}

	public void setFocus(boolean focused) {
		this.suggestBox.setFocus(focused);
	}

	public void setAutoSelectEnabled(boolean autoSelect) {
		this.suggestBox.setAutoSelectEnabled(autoSelect);
	}
	
	public void setPlaceHolder(String placeHolder) {
		this.suggestBox.getElement().setPropertyString("placeholder", placeHolder);
	}

	public void showSuggestionList() {
		this.suggestBox.showSuggestionList();
	}

	public void hideSuggestionList() {
		this.suggestBox.hideSuggestionList();
	}

	public void setEnable(boolean enabled) {
		suggestBox.setEnabled(enabled);
	}
	
	public void addButton(AonTableButton button) {
		suggestBoxPanel.add(button);
	}

	public void setMaxLength(int maxLength) {
		((TextBox) getSuggestBox().getTextBox()).setMaxLength(maxLength);
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
	}
	
	public void setMinWidth(String minWidth) {
		getElement().getStyle().setProperty("min-width", minWidth);
	}
	
	public AonCustomSuggestOracle getOracle() {
	    return customOracle;
	}
	
	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		this.suggestBox.ensureDebugId(baseID + "Input");
	}

}
