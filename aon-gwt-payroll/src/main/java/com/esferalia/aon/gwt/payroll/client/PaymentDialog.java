package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0000;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class PaymentDialog extends CustomDialog {
	
	interface Callback {
		void onAccept(PaymentDialog dialog);
	}
	
	interface Binder extends UiBinder<Widget, PaymentDialog> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField 
	Payment payment;
	
	@UiField
	TableRowElement buttonsRow;
	
	private Callback cb;
	
	public PaymentDialog() {
		setCaption("Percepci\u00f3n");
		setWidget(binder.createAndBindUi(this));
		setType(Type.CRA_0001);
	}

	public void setAvailableConcepts(
			List<com.esferalia.aon.gwt.payroll.shared.Payment> availablePaymens) {
		payment.setAvailableConcepts(availablePaymens);
	}

	public Short getMonth() {
		return payment.getMonth();
	}

	public void setMonth(Short month) {
		payment.setMonth(month);
	}
	
	public String getName() {
		return payment.getName();
	}

	public void setName(String name) {
		payment.setName(name);
	}

	public String getPaymentExpression() {
		return payment.getExpression();
	}

	public void setPaymentExpression(String expression) {
		payment.setExpression(expression);
	}

	public com.esferalia.aon.gwt.payroll.shared.Payment.Type getType() {
		return payment.getType();
	}
	
	public void setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type type) {
		payment.setType(type == null ? Type.CRA_0001 : type);
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
	
	public void setEnabledMonthListBox(boolean enabled) {
		payment.setEnabledMonthListBox(enabled);
	}
	
	public void setAvailablePayments(Set<com.esferalia.aon.gwt.payroll.shared.Payment> payments) {
		payment.setAvailablePayments(payments.stream().collect(Collectors.toList()));
	}

	public void setAvailablePayments(List<com.esferalia.aon.gwt.payroll.shared.Payment> payments) {
		payment.setAvailablePayments(payments);
	}
	
	public void setReadOnly(boolean readOnly) {
		payment.setReadOnly(readOnly);
		buttonsRow.getStyle().setDisplay(readOnly ? Display.NONE : Display.TABLE_ROW);
	}
	
	public void setTypeListVisible() {
		payment.typeDeckPanel.showWidget(0);
		payment.taxTypeDeckPanel.showWidget(0);
		payment.quoteTypeDeckPanel.showWidget(0);
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
