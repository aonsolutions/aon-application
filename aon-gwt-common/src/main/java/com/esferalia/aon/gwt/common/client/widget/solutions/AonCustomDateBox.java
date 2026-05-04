package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class AonCustomDateBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private AonDateBox dateBox;
	
	public AonCustomDateBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
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
		dateBox = new AonDateBox();
		dateBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		dateBox.setWidth("100%");
		dateBox.getElement().getStyle().setProperty("padding", "0");
		add(dateBox);
	}
	
	public DateBox getDateBox() {
		return this.dateBox;
	}

	public void setValue(Date date) {
		this.dateBox.setValue(date);
	}
	
	public void setValue(Date date, boolean fireEvent) {
		this.dateBox.setValue(date, fireEvent);
	}

	public Date getValue() {
		return this.dateBox.getValue();
	}
	
	public void setEnable(boolean enabled) {
		this.dateBox.setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		this.dateBox.setFocus(focused);
	}
	
	public void addValueChangeHandler(ValueChangeHandler<Date> handler) {
		this.dateBox.addValueChangeHandler(handler);
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

}
