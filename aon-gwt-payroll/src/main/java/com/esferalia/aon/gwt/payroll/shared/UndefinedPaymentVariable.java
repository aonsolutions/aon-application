package com.esferalia.aon.gwt.payroll.shared;

public class UndefinedPaymentVariable extends UndefinedVariable implements HasPayment {
	private Payment payment;
	
	@Override
	public Payment getPayment() {
		return payment;
	}
	
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	
}