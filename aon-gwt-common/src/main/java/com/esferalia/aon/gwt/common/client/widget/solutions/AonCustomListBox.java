package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;

public class AonCustomListBox extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private ListBox listBox;
	
	public AonCustomListBox(String title) {
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
		listBox = new ListBox();
		listBox.setStyleName(AON.CSS.aonCustomTextBoxInput());
		add(listBox);
	}
	
	public ListBox getListBox() {
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

	public void setValue(String value) {
		setSelectedValueLB(listBox, value);
	}

	public String getValue() {
		return this.listBox.getSelectedValue();
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	public void addChangeHandler(ChangeHandler changeHandler) {
		listBox.addChangeHandler(changeHandler);
	}

}
