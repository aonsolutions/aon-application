package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;

public class AonCustomListBox extends HTMLPanel implements Focusable {

	private static final String EMPTY_STRING = "";
	private ListBox listBox;
	
	public AonCustomListBox(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());
		if (AonStringUtils.isNotEmpty(title)) {
			createTitle(title);
		}
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

	/**
	 * @deprecated use {@link #setEnabled(boolean)} instead
	 */
	public void setEnable(boolean enabled) {
		listBox.setEnabled(enabled);
	}
	public void setEnabled(boolean enabled) {
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

	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		this.listBox.ensureDebugId(baseID + "Select");
	}

	@Override
	public int getTabIndex() {
		return listBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		listBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		listBox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		listBox.setTabIndex(index);
	}

	public void setSelectedIndex(int i) {
		listBox.setSelectedIndex(i);
	}
	public int getSelectedIndex() {
		return listBox.getSelectedIndex();
	}
}
