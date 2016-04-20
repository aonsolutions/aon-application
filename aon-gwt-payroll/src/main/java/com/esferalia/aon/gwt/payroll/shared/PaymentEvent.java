package com.esferalia.aon.gwt.payroll.shared;

public class PaymentEvent extends Event implements HasPayment {
	Payment payment;

	@Override
	public Payment getPayment() {
		return payment;
	}

	public PaymentEvent setPayment(Payment payment) {
		this.payment = payment;
		return this;
	}

}