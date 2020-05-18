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

public abstract class EmployeeCalendarInactivityDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EmployeeCalendarInactivityDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
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
	
	// -------------------------------------------------------------------------------
	// --------------------------------- MAIN CLASS ----------------------------------
	// -------------------------------------------------------------------------------
	
	private Double percent = 1.00;
	private Date contractStartDate;
	private Date contractEndDate;

	public EmployeeCalendarInactivityDialog(String caption, List<Date> selectedDates, Date contractStartDate, Date contractEndDate) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		percentPanel.getElement().getStyle().setDisplay(Display.NONE);
		endERTEPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		// Set default value
		endERTECB.setValue(false);
		
		typeInactivity.clear();
		typeInactivity.addItem("Excedencia");
		typeInactivity.addItem("Fijo/Discontinuo");
		typeInactivity.addItem("Permiso no Retribuido");
		typeInactivity.addItem("Suspensi\u00f3n de Empleo y Sueldo");
		typeInactivity.addItem("ERE");
		typeInactivity.addItem("ERE Fuerza mayor");
		typeInactivity.addItem("ERE Fuerza mayor (Exoneraci" + String.valueOf("\u00F3") + "n de cuotas)");
		typeInactivity.addItem("ERE Fuerza mayor Parcial (Exoneraci" + String.valueOf("\u00F3") + "n de cuotas)");
		
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
		
		typeInactivity.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
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
		return typeInactivity.getSelectedItemText();
	}
	
	public Boolean isEndERTE() {
		return endERTECB.getValue();
	}

}
