package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class PaymentEditor extends ResizeComposite {
	
	interface Binder extends UiBinder<Widget, PaymentEditor> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	com.esferalia.aon.gwt.payroll.client.Payment paymentUI;
	
	private Payment payment;
	private EnterprisesServiceAsync enterprisesService;

	public PaymentEditor() {
		initWidget(binder.createAndBindUi(this));
		initEnterprisesService();
	}
	
	public void setPayment(Payment payment) {
		this.payment = payment;
		dumpPayment(payment);
	}
	
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------ 
	
	private void dumpPayment(Payment payment) {
		paymentUI.setName(payment.getName());
		paymentUI.setType(payment.getType());
		paymentUI.setDescription(payment.getDescription());
		paymentUI.setPaymentExpression(payment.getExpression());
		paymentUI.setIrpfExpression(payment.getIrpfExpression());
		paymentUI.setQuoteExpression(payment.getQuoteExpression());
	}
	
	private void initEnterprisesService() {
		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		EnterprisesServiceAsync enterprisesServiceRaw = GWT
				.create(EnterprisesService.class);
		enterprisesService = new EnterprisesServiceAsyncDecorator(
				enterprisesServiceRaw);
	}
	
	private void initContextProvider() {
		
	}
	
	
	
}
