package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class InputDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, InputDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField Label messageLabel;
	@UiField TextArea inputTextArea;
	
	@UiField Button acceptButton;
	@UiField Button cancelButton;
	
	
	public InputDialog(String caption, String message) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));

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

		setMessage(message);
	}
	
	
	public void setMessage(String message ){
		messageLabel.setText(message);
	}
	
	
	public String getInputValue() {
		return inputTextArea.getText();
	}
	
	public void setInputValue(String value){
		inputTextArea.setText(value);
	}
	
	public void onAccept() {
		
	}
	
	
}
