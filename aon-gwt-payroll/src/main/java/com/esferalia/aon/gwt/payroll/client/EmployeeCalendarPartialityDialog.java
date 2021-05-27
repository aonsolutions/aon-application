package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarPartialityDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarPartialityDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
		
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
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private Double percent = 1.00;
	private Date contractStartDate;
	private Date contractEndDate;

	public EmployeeCalendarPartialityDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		
		acceptButton.setEnabled(false);
		
		if(!selectedDates.isEmpty()) {
			selectedDates.sort(null);
			startDateDB.setValue(selectedDates.get(0));
			endDateDB.setValue(selectedDates.get(selectedDates.size() - 1));
		}
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		
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
				if(null != startDateDB.getValue())
					onAccept();
				hide();
			}
		});	
		
		percentBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				try {
					percent = Double.parseDouble(percentBox.getValue());
					errorMessage.getElement().getStyle().setDisplay(Display.NONE);
					acceptButton.setEnabled(true);
					
					if(percent == 0.00)
						percent = 1.00;
					else {
						percent = percent / 100;
					}
				
				} catch (NumberFormatException e) {
					errorMessage.getElement().getStyle().clearDisplay();
					acceptButton.setEnabled(false);
					percentBox.setValue("");
					percent = 1.00;
				}
			}
		});
	}
	
	// -------------------------------------------------------------------------------
	// ----------------------------- ABSTRACT METHODS --------------------------------
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

}
