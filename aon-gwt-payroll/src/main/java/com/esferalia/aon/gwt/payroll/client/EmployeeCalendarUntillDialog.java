package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarUntillDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarUntillDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String messageStyle();
		String hideElement();
		String dateStyle();
		String paddingButtons();
		String margingButton();
	}
	
	@UiField
	Label message;
	
	@UiField
	Label date;
	
	@UiField
	DateBoxEx endDateBox;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;

	public EmployeeCalendarUntillDialog(String caption) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		endDateBox.getTextBox().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				endDateBox.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
				
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});	
		
	}
	
	public EmployeeCalendarUntillDialog(String caption, String message) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		date.addStyleName(style.dateStyle());
		acceptButton.addStyleName(style.margingButton());
		
		this.message.setVisible(true);
		this.message.setText(message);
		
		this.message.addStyleName(style.messageStyle());
		
		endDateBox.getTextBox().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				endDateBox.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
				
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});	
		
	}

	protected abstract void onAccept();
	
	public Date getSelectedDate() {
		return endDateBox.getValue();
	}
	
	public void setDefaultDate(Date date){
		endDateBox.setValue(date);
	}
	
	public void setDateLabel(String label){
		date.setText(label);
	}

}
