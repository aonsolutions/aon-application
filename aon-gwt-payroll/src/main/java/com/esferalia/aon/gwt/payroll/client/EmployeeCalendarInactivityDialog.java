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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarInactivityDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarInactivityDialog> {

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
	HorizontalPanel messageBlock;
	
	@UiField
	Label message;
	
	@UiField
	ListBox typeInactivity;
	
	@UiField
	DateBoxEx startDateBox;
	
	@UiField
	DateBoxEx endDateBox;
	
	@UiField
	HorizontalPanel buttons;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;

	public EmployeeCalendarInactivityDialog(String caption) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		messageBlock.addStyleName(style.hideElement());
		
		typeInactivity.clear();
		typeInactivity.addItem("Excedencia");
		typeInactivity.addItem("Fijo/Discontinuo");
		typeInactivity.addItem("Permiso no Retribuido");
		typeInactivity.addItem("Suspensi\u00f3n de Emploeo y Sueldo");
		
		startDateBox.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startDateBox.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
			}
		});
		
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
	
	public EmployeeCalendarInactivityDialog(String caption, String message) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		messageBlock.removeStyleName(style.hideElement());
		//date.addStyleName(style.dateStyle());
		buttons.addStyleName(style.paddingButtons());
		acceptButton.addStyleName(style.margingButton());
		
		this.message.setVisible(true);
		this.message.setText(message);
		
		this.message.addStyleName(style.messageStyle());
		
		typeInactivity.clear();
		typeInactivity.addItem("Excedencia");
		typeInactivity.addItem("Fijo/Discontinuo");
		typeInactivity.addItem("Permiso no Retribuido");
		typeInactivity.addItem("Suspensi\u00f3n de Emploeo y Sueldo");
		
		startDateBox.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startDateBox.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
			}
		});
		
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
	
	public Date getStartDate() {
		return startDateBox.getValue();
	}
	
	public void setStartDate(Date date){
		startDateBox.setValue(date);
	}
	
	public Date getEndDate() {
		return endDateBox.getValue();
	}
	
	public void setEndDate(Date date){
		endDateBox.setValue(date);
	}
	
	public String getTypeInactivity() {
		return typeInactivity.getSelectedItemText();
	}

}
