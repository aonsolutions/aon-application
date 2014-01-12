package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class DeductionDialog extends CustomDialog {
	
	interface Callback {
		void onAccept(DeductionDialog dialog);
	}
	
	interface Binder extends UiBinder<Widget, DeductionDialog> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	com.esferalia.aon.gwt.payroll.client.Deduction deduction;
	
	private Callback cb;
	
	public DeductionDialog() {
		setCaption("Deducci\u00f3n");
		setWidget(binder.createAndBindUi(this));
	}

	public void setAvailableDeductions(
			List<com.esferalia.aon.gwt.payroll.shared.Deduction> availableDeductions) {
		deduction.setAvailableDeductions(availableDeductions);
	}


	public String getDeductionExpression() {
		return deduction.getDeductionExpression();
	}

	public void setDeductionExpression(String expression) {
		deduction.setDeductionExpression(expression);
	}

	public com.esferalia.aon.gwt.payroll.shared.Deduction.Type getType() {
		return deduction.getType();
	}
	
	public void setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type type) {
		deduction.setType(type);
	}

	public String getDescription() {
		return deduction.getDescription();
	}

	public void setDescription(String description) {
		deduction.setDescription(description);
	}

	public com.esferalia.aon.gwt.payroll.shared.Item<Deduction.Type> getConcept() {
		return deduction.getConcept();
	}

	public void setConcept(com.esferalia.aon.gwt.payroll.shared.Deduction concept){
		deduction.setConcept(concept);
	}
	
	public void setContextProvider(IContextProvider contextProvider) {
		deduction.setContextProvider(contextProvider);
	}
	
	public void setNumberFormat(NumberFormat numberFormat) {
		deduction.setNumberFormat(numberFormat);
	}
	
	public void show(Callback cb) {
		this.cb = cb;
		super.show();
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
