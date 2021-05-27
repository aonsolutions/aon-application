package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarDatesDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarDatesDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;

	interface MyStyle extends CssResource {}
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------

	private Date contractStartDate;
	private Date contractEndDate;
	
	public EmployeeCalendarDatesDialog(String caption, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		setWidget(binder.createAndBindUi(this));
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------ ABSTRACT METHODS -------------------------------
	// -------------------------------------------------------------------------------
	
	protected abstract void onAccept();
	
	// -------------------------------------------------------------------------------
	// -------------------------------- UI HANDLERS ----------------------------------
	// -------------------------------------------------------------------------------
	
	@UiHandler("startDateDB")
	public void onStartDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date) {
			if(date.before(contractStartDate))
				startDateDB.setValue(contractStartDate);
		}
	}
	
	@UiHandler("endDateDB")
	public void onEndDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date && null != contractEndDate) {
			if(date.after(contractEndDate))
				endDateDB.setValue(contractEndDate);
		}
	}
	
	@UiHandler("acceptButton")
	public void onAcceptButtonClick(ClickEvent event) {
		if(null != startDateDB.getValue())
			onAccept();
		this.hide();
	}
	
	@UiHandler("cancelButton")
	public void onCancelButtonClick(ClickEvent event) {
		this.hide();
	}
	
	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHDOS ----------------------------------
	// -------------------------------------------------------------------------------
	
	public Date getStartDate() {
		return this.startDateDB.getValue();
	}
	
	public void setStartDate(Date date) {
		this.startDateDB.setValue(date);
	}
	
	public Date getEndDate() {
		return this.endDateDB.getValue();
	}
	
	public void setEndDate(Date date) {
		this.endDateDB.setValue(date);
	}

}
