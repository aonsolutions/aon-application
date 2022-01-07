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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarPercentDialog extends CustomDialog {

	// ------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, EmployeeCalendarPercentDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------ UiFields
	
	@UiField
	ListBox typeDrop;
	
	@UiField
	TextBox percentBox;
	
	@UiField
	HTMLPanel errorMessage;
	
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
	
	protected EmployeeCalendarPercentDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		setWidget(binder.createAndBindUi(this));
		this.hideClose();
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		acceptButton.setEnabled(false);
		
		typeDrop.clear();
		typeDrop.addItem("Huelga");
		typeDrop.addItem("Ausencia Injustificada");
		
		if(!selectedDates.isEmpty()) {
			selectedDates.sort(null);
			startDateDB.setValue(selectedDates.get(0));
			endDateDB.setValue(selectedDates.get(selectedDates.size() - 1));
		}
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
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
	
	public Integer getTypeDrop(){
		return typeDrop.getSelectedIndex();
	}
	
	public double getPercentValue() {
		return Math.round(this.percent * 100.0) / 100.0;
	}
	
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
	
	// ------------------------------------ Show dialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

}
