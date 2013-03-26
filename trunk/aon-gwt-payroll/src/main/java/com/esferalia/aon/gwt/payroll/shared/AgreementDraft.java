package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AgreementDraft extends Agreement {

	private Date startDate;
	private Date endDate;
	
	private List<Payment> payments;
	private List<Payment> draftPayments;
	
	public AgreementDraft() {
		payments = new LinkedList<Payment>();
		draftPayments = new LinkedList<Payment>();
	}

	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public List<Payment> getPayments() {
		return payments;
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
	}
	
	
	
}
