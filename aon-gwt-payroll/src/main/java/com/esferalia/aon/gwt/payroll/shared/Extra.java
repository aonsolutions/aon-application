package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasId;

public class Extra implements Serializable, HasId<Integer>, HasDomain<Integer> {

	private Integer id;

	private Integer domain;
	
	private Integer paymentId;
	
	private String startDate;
	private String endDate;
	private String issueDate;
	
	private String paymentDescription;
	private String agreementDescription;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	
	public String getPaymentDescription() {
		return paymentDescription;
	}
	
	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}
	
	public String getAgreementDescription() {
		return agreementDescription;
	}
	
	public void setAgreementDescription(String agreementDescription) {
		this.agreementDescription = agreementDescription;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Extra && this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return id != null ? id : super.hashCode();
	}
	
}
