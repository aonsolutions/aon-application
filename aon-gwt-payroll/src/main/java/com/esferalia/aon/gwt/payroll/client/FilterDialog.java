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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public abstract class FilterDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, FilterDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	
	@UiField TextBox nameTextBox;
	@UiField DateBox fromDateBox;
	
	@UiField Button acceptButton;
	@UiField Button cancelButton;
	
	@UiField Label fromDatePatternLabel;
	
	public FilterDialog() {
		setCaption("Filtros...");
		
		setWidget(binder.createAndBindUi(this));

		fromDateBox.setFormat(new DateBox.DefaultFormat( ) );
		
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
	
	public void setName(String text) {
		nameTextBox.setText(text);
	}
	
	public String getName() {
		return nameTextBox.getValue();
	}
	
	public void setDateFrom(Date date) {
		fromDateBox.setValue(date);
	}

	public Date getDateFrom() {
		return fromDateBox.getValue();
	}
	
	public void setDateTimeFormat(DateTimeFormat dateTimeFormat) {
		fromDateBox.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
		fromDatePatternLabel.setText(dateTimeFormat.getPattern());
	}
	
	
	
}
