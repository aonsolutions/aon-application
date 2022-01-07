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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarAgrarianDialog extends CustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, EmployeeCalendarAgrarianDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields
	
	@UiField
	ListBox typeListBox;
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// ------------------------------------ Variables
	
	private Double percent = 1.00;
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ------------------------------------ Constructor

	protected EmployeeCalendarAgrarianDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		setWidget(binder.createAndBindUi(this));
		this.hideClose();
		typeListBox.clear();
		typeListBox.addItem("Jornadas Reales", "JORNADAS_REALES");
		typeListBox.addItem("Jornadas Te\u00f3ricas", "JORNADAS_TEORICAS");
		
		if(!selectedDates.isEmpty()) {
			selectedDates.sort(null);
			startDateDB.setValue(selectedDates.get(0));
			endDateDB.setValue(selectedDates.get(selectedDates.size() - 1));
		}
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
		startDateDB.getTextBox().addClickHandler(e -> startDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; "));
		endDateDB.getTextBox().addClickHandler(e -> endDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; "));
		
		cancelButton.addClickHandler(e -> hide());
		acceptButton.addClickHandler(e -> {
			if(null != startDateDB.getValue())
				onAccept();
			hide();
		});
	
		showDialog();
	}
	
	// ------------------------------------ Abstract methods
	
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
	
	public Integer getTypeIdx(){
		return typeListBox.getSelectedIndex();
	}
	
	public double getPercentValue() {
		return Math.round(this.percent * 100.0) / 100.0;
	}
	
	public Date getStartDate() {
		return startDateDB.getValue();
	}
	
	public void setStartDate(Date date){
		startDateDB.setValue(date);
	}
	
	public Date getEndDate() {
		return endDateDB.getValue();
	}
	
	public void setEndDate(Date date){
		endDateDB.setValue(date);
	}
	
	public String getType() {
		return typeListBox.getSelectedValue();
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
