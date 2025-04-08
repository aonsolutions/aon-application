package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class AgreementIntegrity implements Serializable {
	
	public static class AgreementExtra implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private Integer id;
		private Payment agreementPayment;
		private String startDate;
		private String endDate;
		private String issueDate;

		
		public Integer getId() {
			return id;
		}

		public AgreementExtra setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Payment getAgreementPayment() {
			return agreementPayment;
		}
		
		public AgreementExtra setAgreementPayment(Payment agreementPayment) {
			this.agreementPayment = agreementPayment;
			return this;
		}
		
		public String getStartDate() {
			return startDate;
		}

		public AgreementExtra setStartDate(String startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public String getEndDate() {
			return endDate;
		}

		public AgreementExtra setEndDate(String endDate) {
			this.endDate = endDate;
			return this;
		}
		
		public String getIssueDate() {
			return issueDate;
		}

		public AgreementExtra setIssueDate(String issueDate) {
			this.issueDate = issueDate;
			return this;
		}
	}

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;

	private List<Payment> agreementPayments;
	private List<Payment> otherDomainAgreementPayments;
	private List<Payment> noPaymentConceptAgreementPayments;
	private List<Payment> otherDomainPaymentConcepts;
	private List<Payment> paymentConceptsNoCode;
	private List<Payment> codeInExpression;
	
	private List<String> variableLikeCodes;
	
	private List<Payment> otherDomainPaymentConceptContracts;
	private List<Payment> paymentConceptsNoCodeContracts;
	private List<Payment> paymentConceptsNoRef;
	
	private List<AgreementExtra> agreementExtras;
	
	public AgreementIntegrity() {
		super();
	}
	
	public Integer getId() {
		return id;
	}

	public AgreementIntegrity setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AgreementIntegrity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public List<Payment> getAgreementPayments() {
		return agreementPayments;
	}

	public AgreementIntegrity setAgreementPayments(List<Payment> agreementPayments) {
		this.agreementPayments = agreementPayments;
		return this;
	}

	public List<Payment> getOtherDomainAgreementPayments() {
		return otherDomainAgreementPayments;
	}

	public AgreementIntegrity setOtherDomainAgreementPayments(List<Payment> otherDomainAgreementPayments) {
		this.otherDomainAgreementPayments = otherDomainAgreementPayments;
		return this;
	}

	public List<Payment> getNoPaymentConceptAgreementPayments() {
		return noPaymentConceptAgreementPayments;
	}

	public AgreementIntegrity setNoPaymentConceptAgreementPayments(List<Payment> noPaymentConceptAgreementPayments) {
		this.noPaymentConceptAgreementPayments = noPaymentConceptAgreementPayments;
		return this;
	}

	public List<Payment> getOtherDomainPaymentConcepts() {
		return otherDomainPaymentConcepts;
	}

	public AgreementIntegrity setOtherDomainPaymentConcepts(List<Payment> otherDomainPaymentConcepts) {
		this.otherDomainPaymentConcepts = otherDomainPaymentConcepts;
		return this;
	}

	public List<Payment> getPaymentConceptsNoCode() {
		return paymentConceptsNoCode;
	}

	public AgreementIntegrity setPaymentConceptsNoCode(List<Payment> paymentConceptsNoCode) {
		this.paymentConceptsNoCode = paymentConceptsNoCode;
		return this;
	}

	public List<Payment> getCodeInExpression() {
		return codeInExpression;
	}

	public AgreementIntegrity setCodeInExpression(List<Payment> codeInExpression) {
		this.codeInExpression = codeInExpression;
		return this;
	}

	public List<String> getVariableLikeCodes() {
		return variableLikeCodes;
	}

	public AgreementIntegrity setVariableLikeCodes(List<String> variableLikeCodes) {
		this.variableLikeCodes = variableLikeCodes;
		return this;
	}

	public List<Payment> getOtherDomainPaymentConceptContracts() {
		return otherDomainPaymentConceptContracts;
	}

	public AgreementIntegrity setOtherDomainPaymentConceptContracts(List<Payment> otherDomainPaymentConceptContracts) {
		this.otherDomainPaymentConceptContracts = otherDomainPaymentConceptContracts;
		return this;
	}

	public List<Payment> getPaymentConceptsNoCodeContracts() {
		return paymentConceptsNoCodeContracts;
	}

	public AgreementIntegrity setPaymentConceptsNoCodeContracts(List<Payment> paymentConceptsNoCodeContracts) {
		this.paymentConceptsNoCodeContracts = paymentConceptsNoCodeContracts;
		return this;
	}

	public List<Payment> getPaymentConceptsNoRef() {
		return paymentConceptsNoRef;
	}

	public AgreementIntegrity setPaymentConceptsNoRef(List<Payment> paymentConceptsNoRef) {
		this.paymentConceptsNoRef = paymentConceptsNoRef;
		return this;
	}

	public List<AgreementExtra> getAgreementExtras() {
		return agreementExtras;
	}

	public AgreementIntegrity setAgreementExtras(List<AgreementExtra> agreementExtras) {
		this.agreementExtras = agreementExtras;
		return this;
	}
	
}
