package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CalendarExtraHourDialog extends CustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, CalendarExtraHourDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	HTMLPanel extraHourBlock;
	
	@UiField
	TextBox extraHourOpt;
	
	@UiField
	Label errorMessage;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// ------------------------------------ Variables
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	private List<Date> selectedDates;
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ------------------------------------ Constructor
	
	protected CalendarExtraHourDialog(List<Date> selectedDates, Date contractStartDate, Date contractEndDate, EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		setCaption("HORAS EXTRAS");
		setWidget(binder.createAndBindUi(this));
		this.hideClose();
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.selectedDates = selectedDates;
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		setDefaultDates();
		initDefaultValuesTB();
		
		startDateDB.getTextBox().addClickHandler(e -> startDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; "));
		endDateDB.getTextBox().addClickHandler(e -> endDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; "));
		
		cancelButton.addClickHandler(e -> hide());
		acceptButton.addClickHandler(e -> {
			if(null != startDateDB.getValue()) {
				accept();
				onAccept();
			}
			hide();
		});
		
		showDialog();
	}
	
	private void setDefaultDates() {
		if(null != selectedDates && !selectedDates.isEmpty()) {
			startDateDB.setValue(selectedDates.get(0));
			endDateDB.setValue(selectedDates.get(selectedDates.size() - 1));
		} else {
			this.startDateDB.setValue(contractStartDate);
			this.endDateDB.setValue(contractEndDate);
		}
	}

	private void accept() {
		this.employeeCalendarDraftObject.addExtraHour(startDateDB.getValue(), endDateDB.getValue(), getExtraHours());
	}
	
	// ------------------------------------ Abstract method
	
	protected abstract void onAccept();
	
	// ------------------------------------ UiHandlers
	
	@UiHandler("startDateDB")
	public void onStartDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date && date.before(contractStartDate))
			startDateDB.setValue(contractStartDate);
	}
	
	@UiHandler("endDateDB")
	public void onEndDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date && null != contractEndDate && date.after(contractEndDate))
			endDateDB.setValue(contractEndDate);
	}
	
	// ------------------------------------ Auxiliar methods

	private void initDefaultValuesTB() {
		Double hourComplementary = getDefaultExtraHoury();
		this.extraHourOpt.setValue(null == hourComplementary ? "" : Double.toString(hourComplementary));
	}

	// ------------------------------------ Get hours
	
	private Double getDefaultExtraHoury() {
		if(null == selectedDates || selectedDates.isEmpty())
			return null;
		
		Double defaultValue = employeeCalendarDraftObject.getExtraHourByDate(selectedDates.get(0));
		
		for(Date date : selectedDates) {
			Double iteratorValue =  employeeCalendarDraftObject.getExtraHourByDate(date);
			if(defaultValue != iteratorValue)
				return null;
		}
		
		return defaultValue;
	}
	
	public Date getStartDate() {
		return this.startDateDB.getValue();
	}
	
	public Date getEndDate() {
		return this.endDateDB.getValue();
	}
	
	public Double getExtraHours(){
		try {
			String hourStr = extraHourOpt.getValue();
			return Double.parseDouble(hourStr);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	// ------------------------------------ Show dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
}
