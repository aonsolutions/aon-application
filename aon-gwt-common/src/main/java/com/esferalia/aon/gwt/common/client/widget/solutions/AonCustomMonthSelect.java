package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonCustomMonthSelect extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private AonCustomMonthListBox listBox;
	
	public AonCustomMonthSelect(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		addStyleName(AON.CSS.aonCustomTextBox());

		createTitle(title);
		createInput();
	}

	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(title);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		listBox = new AonCustomMonthListBox();
		add(listBox);
	}
	
	public AonCustomMonthListBox getAonCustomMonthListBox() {
		return this.listBox;
	}

	public void clearItems() {
		this.listBox.clear();
	}

	public void addItem(String item, String value) {
		this.listBox.addItem(item, value);
	}
	
	public void addItem(String item) {
		this.listBox.addItem(item);
	}

	public void setValue(Date date) {
		this.listBox.setSelected(date, true);
	}

	public Date getValue() {
		return this.listBox.getSelected();
	}

	public void addChangeHandler(ChangeHandler changeHandler) {
		listBox.addChangeHandler(changeHandler);
	}

	public void setEnable(boolean enabled) {
		listBox.setEnabled(enabled);
	}
	
	public boolean isEnable() {
		return listBox.isEnabled();
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

}
