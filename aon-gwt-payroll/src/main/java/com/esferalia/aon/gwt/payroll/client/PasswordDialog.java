package com.esferalia.aon.gwt.payroll.client;


import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class PasswordDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, PasswordDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	

	@UiField Button acceptButton;
	@UiField Button cancelButton;
	
	@UiField Label messageLabel;
	@UiField PasswordTextBox passwordTextBox;
		
	public PasswordDialog(String caption, String message) {
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
	
	
	public String getPasswordValue() {
		return passwordTextBox.getText();
	}
	
	public void setPasswordValue(String value){
		passwordTextBox.setText(value);
	}
	
	public void onAccept() {
		
	}
	
	
}
