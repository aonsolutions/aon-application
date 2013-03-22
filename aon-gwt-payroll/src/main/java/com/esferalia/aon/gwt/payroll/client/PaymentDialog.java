package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class PaymentDialog extends CustomDialog {

	
	interface Binder extends UiBinder<Widget, PaymentDialog> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	Payment payment;
	
	public PaymentDialog() {
		setCaption("Percepci\u00f3n");
		setWidget(binder.createAndBindUi(this));
	}

	public void setAvailablePaymens(
			List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePaymens) {
		payment.setAvailablePaymens(availablePaymens);
	}

	public Short getMonth() {
		return payment.getMonth();
	}

	public void setMonth(Short month) {
		payment.setMonth(month);
	}

	public String getPaymentExpression() {
		return payment.getPaymentExpression();
	}

	public void setPaymentExpression(String expression) {
		payment.setPaymentExpression(expression);
	}

	public com.esferalia.aon.gwt.payroll.shared.Payment.Type getType() {
		return payment.getType();
	}
	
	public void setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type type) {
		payment.setType(type);
	}

	public com.esferalia.aon.gwt.payroll.shared.Salary.Type getReceiptType() {
		return payment.getReceiptType();
	}
	
	public void setReceiptType(com.esferalia.aon.gwt.payroll.shared.Salary.Type type) {
		payment.setReceiptType(type);
	}

	public String getDescription() {
		return payment.getDescription();
	}

	public void setDescription(String description) {
		payment.setDescription(description);
	}

	public String getIrpfExpression() {
		return payment.getIrpfExpression();
	}

	public void setIrpfExpression(String expression) {
		payment.setIrpfExpression(expression);
	}

	public String getQuoteExpression() {
		return payment.getQuoteExpression();
	}

	public void setQuoteExpression(String expression) {
		payment.setQuoteExpression(expression);
	}
	
	public com.esferalia.aon.gwt.payroll.shared.Payment getConcept() {
		return payment.getConcept();
	}

	public void setConcept(com.esferalia.aon.gwt.payroll.shared.Payment concept){
		payment.setConcept(concept);
	}
	
	public void setContextProvider(IContextProvider contextProvider) {
		payment.setContextProvider(contextProvider);
	}
	
	public void setNumberFormat(NumberFormat numberFormat) {
		payment.setNumberFormat(numberFormat);
	}
	
	
}
