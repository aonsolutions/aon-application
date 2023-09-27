package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarInactivityDialog extends CustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, EmployeeCalendarInactivityDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields
	
	@UiField
	ListBox typeInactivity;
	
	@UiField
	DateBoxEx startDateDB;
	
	@UiField
	DateBoxEx endDateDB;
	
	@UiField
	HTMLPanel percentPanel;
	
	@UiField
	HTMLPanel endERTEPanel;
	
	@UiField
	CheckBox endERTECB;
	
	@UiField
	HTMLPanel errorMessage;
	
	@UiField
	TextBox percentBox;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
	// ------------------------------------ Variables
	
	private Double percent = 1.00;
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ------------------------------------ Constructor

	protected EmployeeCalendarInactivityDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		setWidget(binder.createAndBindUi(this));
		this.hideClose();
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		percentPanel.getElement().getStyle().setDisplay(Display.NONE);
		endERTEPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		// Set default value
		endERTECB.setValue(false);
		
		typeInactivity.clear();
		typeInactivity.addItem("Excedencia");
		typeInactivity.addItem("Fijo/Discontinuo");
		typeInactivity.addItem( "Permiso no Retribuido", "PERMISO_NO_RETRIBUIDO");
		typeInactivity.addItem("Suspensi\u00f3n de Empleo y Sueldo", "SUSPENSION_EMPLEO_SUELDO");
		typeInactivity.addItem("ERE");
		typeInactivity.addItem("ERE Fuerza mayor");
		typeInactivity.addItem("ERE Fuerza mayor (Exoneraci\u00F3n de cuotas)");
		
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
		
		percentBox.addBlurHandler(e -> {
			try {
				percent = Double.parseDouble(percentBox.getValue());
				errorMessage.getElement().getStyle().setDisplay(Display.NONE);
				acceptButton.setEnabled(true);
				
				if(percent == 0.00)
					percent = 1.00;
				else {
					percent = percent / 100;
				}
			
			} catch (NumberFormatException ex) {
				errorMessage.getElement().getStyle().clearDisplay();
				acceptButton.setEnabled(false);
				percentBox.setValue("");
				percent = 1.00;
			}
		});
		
		typeInactivity.addChangeHandler(e -> {
			Integer selectedIdx = getTypeIdxInactivity();
			
			if(selectedIdx <= 3) {
				percentPanel.getElement().getStyle().setDisplay(Display.NONE);
				errorMessage.getElement().getStyle().setDisplay(Display.NONE);
				percentBox.setValue("");
				acceptButton.setEnabled(true);
			} else {
				percentPanel.getElement().getStyle().clearDisplay();
				acceptButton.setEnabled(false);
			}
			
			if(selectedIdx == 6 || selectedIdx == 7) {
				endERTEPanel.getElement().getStyle().clearDisplay();
			} else
				endERTEPanel.getElement().getStyle().setDisplay(Display.NONE);
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
	
	public Integer getTypeIdxInactivity(){
		return typeInactivity.getSelectedIndex();
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
	
	public String getTypeInactivity() {
		return typeInactivity.getSelectedValue();
	}
	
	public Boolean isEndERTE() {
		return endERTECB.getValue();
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
