package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class PaymentEditor extends ResizeComposite {

	interface Binder extends UiBinder<Widget, PaymentEditor> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	com.esferalia.aon.gwt.payroll.client.Payment paymentUI;

	private Payment payment;
	private DomainEnterprisesServiceAsync enterprisesService;

	public PaymentEditor() {
		initWidget(binder.createAndBindUi(this));
		initEnterprisesService();
		initContextProvider();
		paymentUI.showMonth(false);
	}

	public void setPayment(Payment payment) {
		if ( this.payment != null )
			fillPayment(this.payment);
		this.payment = payment;
		dumpPayment(payment);
	}

	// ------------------------------------------------------------- UIHandlers
	@UiHandler("acceptButton")
	public void onAcceptClick(ClickEvent event) {
		if ( this.payment == null )
			return;
		
		fillPayment(this.payment);
		enterprisesService.savePaymentConcept(this.payment,
				new AsyncCallback<Payment>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Payment payment) {
						loadPayment(payment);
					}
				});

	}

	@UiHandler("deleteButton")
	public void onDeleteClick(ClickEvent event) {
		enterprisesService.deletePaymentConcept(payment, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	// ------------------------------------------------------------------------

	private void loadPayment(Payment payment) {
		this.payment.setId(payment.getId());
		this.payment.setName(payment.getName());
		this.payment.setType(payment.getType());
		this.payment.setDescription(payment.getDescription());
		this.payment.setExpression(payment.getExpression());
		this.payment.setIrpfExpression(payment.getIrpfExpression());
		this.payment.setQuoteExpression(payment.getQuoteExpression());
	}

	private void fillPayment(Payment payment) {
		payment.setName(paymentUI.getName());
		payment.setType(paymentUI.getType());
		payment.setDescription(paymentUI.getDescription());
		payment.setExpression(paymentUI.getExpression());
		payment.setIrpfExpression(paymentUI.getIrpfExpression());
		payment.setQuoteExpression(paymentUI.getQuoteExpression());
	}

	private void dumpPayment(Payment payment) {
		paymentUI.setName(payment.getName());
		paymentUI.setType(payment.getType());
		paymentUI.setDescription(payment.getDescription());
		paymentUI.setExpression(payment.getExpression());
		paymentUI.setIrpfExpression(payment.getIrpfExpression());
		paymentUI.setQuoteExpression(payment.getQuoteExpression());
	}

	private void initEnterprisesService() {
		// Create a remote service proxy to talk to the server-side Enterprises
		// service.
		enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	}

	private void initContextProvider() {

		class ContextProvider implements IContextProvider {
			@Override
			public boolean isEditable(String name) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void eval(String expression, List<Variable> vars,
					AsyncCallback<List<Result>> callback) {
				callback.onFailure(new EvalException());
				
			}

			@Override
			public void getContext(AsyncCallback<ContextDescriptor> callback) {
				PaymentEditor.this.enterprisesService.getContext(callback);
			}
		}

		paymentUI.setContextProvider(new ContextProvider());
	}

}
