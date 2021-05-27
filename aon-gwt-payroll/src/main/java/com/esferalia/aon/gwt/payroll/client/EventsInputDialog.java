package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EventsInputDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, EventsInputDialog> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	ListBox employeeList;
	
	@UiField
	ListBox variablesList;
	
	@UiField
	Label varLabel;
	
	@UiField
	TextBox valueTextBox;
	
	@UiField
	HTMLPanel errorMessage;
	
	@UiField
	DateBoxEx startDate;
	
	@UiField
	DateBoxEx endDate;
	
	@UiField
	Button cancelButton;
	
	@UiField
	Button acceptButton;
	
//	private Date contractStartDate;
//	private Date contractEndDate;
	private Double value = null;
	
	public EventsInputDialog(String caption, ArrayList<String> variables, ArrayList<EventEmployee> employeeList) {
		setCaption(caption);
		
		setWidget(binder.createAndBindUi(this));
		
		//Init ListBox
		for(String variable : variables) {
			variablesList.addItem(variable, variable);
		}
		
		for(EventEmployee eventEmployee : employeeList)
			this.employeeList.addItem(eventEmployee.getFullName());
		
		varLabel.setText(variablesList.getSelectedItemText() + " : ");
		
		errorMessage.getElement().getStyle().setDisplay(Display.NONE);
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(null != startDate.getValue())
					onAccept();
				hide();
			}
		});
		
		valueTextBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				try {
					if(null != valueTextBox.getValue() && !StringUtils.isEmpty(valueTextBox.getValue()))
						value = Double.parseDouble(valueTextBox.getValue());
					else
						value = null;
					errorMessage.getElement().getStyle().setDisplay(Display.NONE);
					acceptButton.setEnabled(true);
				} catch (NumberFormatException e) {
					errorMessage.getElement().getStyle().clearDisplay();
					acceptButton.setEnabled(false);
					valueTextBox.setValue("");
				}
			}
		});
		
		variablesList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String variable = variablesList.getSelectedValue();
				varLabel.setText(variable + " : ");
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
		this.valueTextBox.ensureDebugId("value_box");
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------ ABSTRACT METHODS -------------------------------
	// -------------------------------------------------------------------------------

	protected abstract void onAccept();
	
	// -------------------------------------------------------------------------------
	// --------------------------------- UI HANDLERS ---------------------------------
	// -------------------------------------------------------------------------------

	@UiHandler("startDate")
	public void onStartDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date) {
//			if(date.before(contractStartDate))
//				startDate.setValue(contractStartDate);
//			else {
				Date firstDayOfMoth = DateUtils.getFirstDayOfMonth(date);
				DateUtils.resetTime(firstDayOfMoth);
				startDate.setValue(firstDayOfMoth);
//			}
		}
	}
	
	@UiHandler("endDate")
	public void onEndDateDBChange(ValueChangeEvent<Date> event) {
		Date date = event.getValue();
		if(null != date) {
//			if(null != contractEndDate) {
//				if(date.after(contractEndDate))
//					endDate.setValue(contractEndDate);
//			} else {
				Date lastDayOfMoth = DateUtils.getLastDayOfMonth(date);
				DateUtils.resetTime(lastDayOfMoth);
				endDate.setValue(lastDayOfMoth);
//			}
		}
	}
	
	// -------------------------------------------------------------------------------
	// --------------------------------- AUX METHODS ---------------------------------
	// -------------------------------------------------------------------------------
	
	public String getValue() {
		return null == this.value ? null : this.value.toString();
	}
	
	public Date getStartDate() {
		return this.startDate.getValue();
	}
	
	public Date getEndDate() {
		return this.endDate.getValue();
	}
	
	public String getVariableName() {
		return this.variablesList.getSelectedValue();
	}
	
	public String getEmployeeSelected() {
		return this.employeeList.getSelectedValue();
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

}
