package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDetail extends ResizeComposite {

	interface Binder extends UiBinder<Widget, EmployeeDetail> { }
	private static final Binder binder = GWT.create(Binder.class);

	//@UiField Documents reports;
	
	@UiField SimpleLayoutPanel panel;
	
	;
	
	public EmployeeDetail() {
		initWidget(binder.createAndBindUi(this));
		
	}
	
	/*
	public Documents getSalaryReceipt() {
		return reports;
	}
	*/
	
	public void setWidget(Widget w){
		panel.setWidget(w);
	}
}
