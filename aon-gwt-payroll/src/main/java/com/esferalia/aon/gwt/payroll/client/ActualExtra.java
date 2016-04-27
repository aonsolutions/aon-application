package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasId;

public class ActualExtra implements HasId<Integer>{
	
	private Integer id;
	private Date endDate;
	private Date startDate;
	private Date issueDate;
	private String paymentDescription;
	private String agreementDescription;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	
	public String getPaymentDescription() {
		return paymentDescription;
	}
	
	public String getAgreementDescription() {
		return agreementDescription;
	}

	public ActualExtra setId(Integer id) {
		this.id = id;
		return this;
	}

	public ActualExtra setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public ActualExtra setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public ActualExtra setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}

	public ActualExtra setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
		return this;
	}

	public ActualExtra setAgreementDescription(String agreementDescription) {
		this.agreementDescription = agreementDescription;
		return this;
	}
	
	

}