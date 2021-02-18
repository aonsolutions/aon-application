package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeCalendarAgrarianDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarAgrarianDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
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
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private Double percent = 1.00;
	private Date contractStartDate;
	private Date contractEndDate;

	public EmployeeCalendarAgrarianDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
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
	
	// -------------------------------------------------------------------------------
	// -------------------------------- AUX METHODS ----------------------------------
	// -------------------------------------------------------------------------------
	
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
	

}
