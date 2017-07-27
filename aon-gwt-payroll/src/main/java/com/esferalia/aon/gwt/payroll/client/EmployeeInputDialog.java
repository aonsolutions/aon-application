package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeInputDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeInputDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Label filterLabel;
	
	@UiField
	Label nameLabel;
	
	@UiField
	TextBox valueTextBox;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;

	public EmployeeInputDialog() {
		setCaption("Nuevo valor...");
		
		setWidget(binder.createAndBindUi(this));
		
		valueTextBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent key) {
				if(key.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					hide();
					onAccept();	
				}
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
	}

	protected abstract void onAccept();

	public void setHeaderLabel(String headerText) {
		filterLabel.setText(headerText);
	}

	public void setNameLabel(String label) {
		nameLabel.setText(label);
	}

	public void setValue(String value) {
		valueTextBox.setText(value);
	}

	public String getValue() {
		return valueTextBox.getValue();
	}

	public void setVisibleFilterLabel(boolean bool) {
		filterLabel.setVisible(bool);
	}

	public void setFocusOnNameTextBox(boolean focus) {
		valueTextBox.setFocus(focus);
	}

}
