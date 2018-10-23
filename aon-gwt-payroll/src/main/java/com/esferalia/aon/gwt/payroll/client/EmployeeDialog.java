package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDialog extends CustomDialog {
	
	interface Callback {
		void onAccept(EmployeeDialog dialog);
	}
	
	interface Binder extends UiBinder<Widget, EmployeeDialog> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	Employee employee;
	
	private Callback cb;
	
	public EmployeeDialog() {
		setCaption("Trabajador");
		setWidget(binder.createAndBindUi(this));
	}

	public void show(Callback cb) {
		this.cb = cb;
		super.show();
	}
	
	public void setPopupPositionAndShow(PositionCallback positionCallback, Callback callback) {
		this.cb = callback;
		super.setPopupPositionAndShow(positionCallback);
	}
	
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent clickEvent) {
		hide();
	}
	
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent clickEvent) {
		hide();
		cb.onAccept(this);
	}
	
	// ------------------------------------------------------------------------
	//
	// ------------------------------------------------------------------------
	
}
