package com.esferalia.aon.gwt.employee.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class SalaryReceipt extends ResizeComposite {

	interface Binder extends UiBinder<Widget, SalaryReceipt> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField HTML receipt;
	

	public SalaryReceipt() {
		initWidget(binder.createAndBindUi(this));
	}
	
	public void setSalaryReceipt(String html) {
		receipt.setHTML(html);
	}
	

}
