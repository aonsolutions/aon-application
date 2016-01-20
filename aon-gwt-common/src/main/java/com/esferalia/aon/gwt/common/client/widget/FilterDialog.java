package com.esferalia.aon.gwt.common.client.widget;

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
	
	@UiField Label filterLabel;
	@UiField Label nameLabel;
	@UiField Label dateLabel;
	
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
	
	public void setFilterLabel(String text) {
		filterLabel.setText(text);
	}
	
	public void setNameLabel(String text) {
		nameLabel.setText(text);
	}
	
	public void setDateLabel(String text) {
		dateLabel.setText(text);
	}
	
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
	
	public void setVisibleDateLabel(boolean bool) {
		dateLabel.setVisible(bool);
	}
	
	public void setVisibleDateBox(boolean bool) {
		fromDateBox.setVisible(false);
	}
	
	public void setVisibleDatePatternLabel(boolean bool) {
		fromDatePatternLabel.setVisible(bool);
	}
	
	public void setFocusOnNameTextBox(boolean focus) {
		nameTextBox.setFocus(focus);
	}
	
	public void setFocusOnDateBox(boolean focus) {
		fromDateBox.setFocus(focus);
	}
}
