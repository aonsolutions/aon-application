package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public abstract class PeriodDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, PeriodDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	
	@UiField DateBox startDateBox;
	@UiField DateBox endDateBox;
	
	@UiField Button acceptButton;
	@UiField Button cancelButton;
	
	@UiField Label startDatePatternLabel;
	@UiField Label endDatePatternLabel;
	
	public PeriodDialog() {
		setCaption("Periodo...");
		
		setWidget(binder.createAndBindUi(this));

		startDateBox.setFormat(new DateBox.DefaultFormat( ) );
		endDateBox.setFormat(new DateBox.DefaultFormat( ) );
		
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
		
		startDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				acceptButton.setEnabled(event.getValue() != null);
			}
		});
	}
	
	protected abstract void onAccept();
	
	
	public void setStartDate(Date date) {
		startDateBox.setValue(date);
	}

	public Date getStartDate() {
		return startDateBox.getValue();
	}
	
	public void setEndDate(Date date) {
		endDateBox.setValue(date);
	}

	public Date getEndDate() {
		return endDateBox.getValue();
	}
	
	
	public void setDateTimeFormat(DateTimeFormat dateTimeFormat) {
		endDateBox.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
		startDateBox.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
		startDatePatternLabel.setText(dateTimeFormat.getPattern());
		endDatePatternLabel.setText(dateTimeFormat.getPattern());
	}
	
	
	
}
