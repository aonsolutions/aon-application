package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;

public class AonCustomSuggestBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private SuggestBox suggestBox;
	
	public AonCustomSuggestBox(String title) {
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
		suggestBox = new SuggestBox();
		suggestBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		add(suggestBox);
	}
	
	public SuggestBox getSuggestBox() {
		return this.suggestBox;
	}

	public void setValue(String value) {
		this.suggestBox.setValue(value);
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

}
