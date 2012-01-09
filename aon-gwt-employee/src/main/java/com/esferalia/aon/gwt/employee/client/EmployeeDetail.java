package com.esferalia.aon.gwt.employee.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeDetail extends ResizeComposite {

	interface Binder extends UiBinder<Widget, EmployeeDetail> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField SalaryReceipt salaryReceipt;
	
	
	public EmployeeDetail() {
		initWidget(binder.createAndBindUi(this));
	}
	
	public SalaryReceipt getSalaryReceipt() {
		return salaryReceipt;
	}
	

}
