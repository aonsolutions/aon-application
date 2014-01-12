package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class DeductionEditor extends ResizeComposite {
	
	interface Binder extends UiBinder<Widget, DeductionEditor> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	com.esferalia.aon.gwt.payroll.client.Item deductionUI;
	
	private Deduction deduction;
	
	public DeductionEditor() {
		initWidget(binder.createAndBindUi(this));
	}
	
	public void setDeduction(Deduction deduction) {
		this.deduction = deduction;
		dumpDeduction(deduction);
	}
	
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------ 
	
	private void dumpDeduction(Deduction deduction) {
		deductionUI.setName(deduction.getName());
		deductionUI.setType(deduction.getType());
		deductionUI.setDescription(deduction.getDescription());
		deductionUI.setDeductionExpression(deduction.getExpression());
	}
	
	
	
	
}
