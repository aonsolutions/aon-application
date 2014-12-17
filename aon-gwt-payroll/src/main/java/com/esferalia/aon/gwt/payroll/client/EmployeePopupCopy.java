package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class EmployeePopupCopy extends CustomDialog {
	
	interface Listener {
		void onAcceptButtonClickButton (ClickEvent event);
	}

	private static EmployeePopupCopyUiBinder uiBinder = GWT
			.create(EmployeePopupCopyUiBinder.class);

	@UiField
	Label nameEmployee;
	@UiField
	DateBox startDate;
	@UiField
	DateBox endDate;
	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;
	@UiField
	CheckBox especifico;

	interface EmployeePopupCopyUiBinder extends
			UiBinder<Widget, EmployeePopupCopy> {
	}
	
	private List<Listener> listeners;
	
	private String fullName;

	public EmployeePopupCopy() {
		setCaption("Introduce nuevos datos...");

		setWidget(uiBinder.createAndBindUi(this));
		
		this.nameEmployee.setStyleName(AON.AON_BLACK + " " + AON.AON_BOLD);
		
		this.startDate.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		this.startDate.getTextBox().setReadOnly(true);
		this.endDate.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		this.endDate.getTextBox().setReadOnly(true);
		
		this.listeners = new ArrayList<Listener>();
		
		setAnimationEnabled(true);
		setGlassEnabled(true);
		
	}

	// -------------------------------------------------------UiHandlers
	
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		for(Listener listener : listeners)
			listener.onAcceptButtonClickButton(event);
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}
	
	// -----------------------------------------------------------------
	
	public void  showPopUpPanel() {
		center();		
	}
	
	public void setFullName(String fullName) {
		this.nameEmployee.setText(fullName);
	}
	
	public DateBox getStartDateWidget() {
		return startDate;
	}
	
	public DateBox getEndDateWidget() {
		return endDate;
	}
	
	public Button getAcceptButton() {
		return acceptButton;
	}
	
	public boolean getEspecificoValue() {
		return especifico.getValue();
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	//--------------------------------------------------------------------
	
	@Override
	public void center() {
		startDate.setValue(null);
		endDate.setValue(null);
		super.center();
	}
	
	@Override
	public void show() {
		startDate.setValue(null);
		endDate.setValue(null);
		super.show();
	}
}
