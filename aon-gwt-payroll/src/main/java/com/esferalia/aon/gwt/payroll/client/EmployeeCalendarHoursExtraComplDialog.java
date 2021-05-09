package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarHoursExtraComplDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarHoursExtraComplDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	HTMLPanel complementaryBlock;
	
	@UiField
	TextBox complementaryHoursOpt;
	
	@UiField
	Label errorMessage;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private EmployeeCalendarDraftObject employeeCalendarDraftObject;
	private List<Date> selectedDates;
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	public EmployeeCalendarHoursExtraComplDialog(List<Date> selectedDates, Date contractStartDate, Date contractEndDate, EmployeeCalendarDraftObject employeeCalendarDraftObject) {
		if(employeeCalendarDraftObject.isFullTimeJourney()) {
			setCaption("HORAS EXTRAS");
		} else {
			setCaption("HORAS COMPLEMENTARIAS");
		}
		
		setWidget(binder.createAndBindUi(this));
		
		if(employeeCalendarDraftObject.isFullTimeJourney()) {
			errorMessage.setText("D\u00EDas en blanco equivale a no horas extras.");
		} else {
			errorMessage.setText("D\u00EDas en blanco equivale a no horas complementarias.");
		}
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.selectedDates = selectedDates;
		this.employeeCalendarDraftObject = employeeCalendarDraftObject;
		
		setDefaultDates();
		initDefaultValuesTB();
		
		startDateDB.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
			}
		});
		
		endDateDB.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				endDateDB.getDatePicker().getElement().setAttribute("style", "visibility: visible; overflow: visible; position: absolute; left: 0px; z-index: 108; ");
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
				if(null != startDateDB.getValue()) {
					accept();
					onAccept();
				}
				hide();
			}
		});	
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
		this.employeeCalendarDraftObject.addDayHourExtraCompl(startDateDB.getValue(), endDateDB.getValue(), getComplementaryHours());
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------- ABSTRACT METHOD ---------------------------------
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
	
	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHODS ----------------------------------
	// -------------------------------------------------------------------------------

	private void initDefaultValuesTB() {
		Double hourComplementary = getDefaultHourComplementary();
		this.complementaryHoursOpt.setValue(null == hourComplementary ? "" : Double.toString(hourComplementary));
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------- GET HOURS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private Double getDefaultHourComplementary() {
		if(null == selectedDates || selectedDates.isEmpty())
			return null;
		
		Double defaultValue = employeeCalendarDraftObject.getHourExtraComplByDate(selectedDates.get(0));
		
		for(Date date : selectedDates) {
			Double iteratorValue =  employeeCalendarDraftObject.getHourExtraComplByDate(date);
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
	
	public Double getComplementaryHours(){
		Double hour = null;
		String hourStr = complementaryHoursOpt.getValue();
		try{
			if(AonStringUtils.isBlank(hourStr))
				return null;
			hour = Double.parseDouble(hourStr);
		}catch (NumberFormatException e) {}
		
		return hour;
	}
	
}
