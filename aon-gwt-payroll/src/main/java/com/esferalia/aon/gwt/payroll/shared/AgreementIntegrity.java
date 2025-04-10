package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class AgreementIntegrity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;

	private List<Payment> agreementPayments;
	private List<String> otherDomainAgreementPayments;
	private List<String> noPaymentConceptAgreementPayments;
	private List<String> otherDomainPaymentConcepts;
	private List<String> paymentConceptsNoCode;
	private List<String> codeInExpression;
	
	private List<String> variableLikeCodes;
	
	private List<String> variableCodeLikeContext;
	
	private List<String> otherDomainPaymentConceptContracts;
	private List<String> paymentConceptsNoCodeContracts;
	private List<String> paymentConceptsNoRef;
	
	private List<String> agreementExtras;
	
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

	public List<String> getOtherDomainAgreementPayments() {
		return otherDomainAgreementPayments;
	}

	public AgreementIntegrity setOtherDomainAgreementPayments(List<String> otherDomainAgreementPayments) {
		this.otherDomainAgreementPayments = otherDomainAgreementPayments;
		return this;
	}

	public List<String> getNoPaymentConceptAgreementPayments() {
		return noPaymentConceptAgreementPayments;
	}

	public AgreementIntegrity setNoPaymentConceptAgreementPayments(List<String> noPaymentConceptAgreementPayments) {
		this.noPaymentConceptAgreementPayments = noPaymentConceptAgreementPayments;
		return this;
	}

	public List<String> getOtherDomainPaymentConcepts() {
		return otherDomainPaymentConcepts;
	}

	public AgreementIntegrity setOtherDomainPaymentConcepts(List<String> otherDomainPaymentConcepts) {
		this.otherDomainPaymentConcepts = otherDomainPaymentConcepts;
		return this;
	}

	public List<String> getPaymentConceptsNoCode() {
		return paymentConceptsNoCode;
	}

	public AgreementIntegrity setPaymentConceptsNoCode(List<String> paymentConceptsNoCode) {
		this.paymentConceptsNoCode = paymentConceptsNoCode;
		return this;
	}

	public List<String> getCodeInExpression() {
		return codeInExpression;
	}

	public AgreementIntegrity setCodeInExpression(List<String> codeInExpression) {
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

	public List<String> getVariableCodeLikeContext() {
		return variableCodeLikeContext;
	}

	public AgreementIntegrity setVariableCodeLikeContext(List<String> variableCodeLikeContext) {
		this.variableCodeLikeContext = variableCodeLikeContext;
		return this;
	}

	public List<String> getOtherDomainPaymentConceptContracts() {
		return otherDomainPaymentConceptContracts;
	}

	public AgreementIntegrity setOtherDomainPaymentConceptContracts(List<String> otherDomainPaymentConceptContracts) {
		this.otherDomainPaymentConceptContracts = otherDomainPaymentConceptContracts;
		return this;
	}

	public List<String> getPaymentConceptsNoCodeContracts() {
		return paymentConceptsNoCodeContracts;
	}

	public AgreementIntegrity setPaymentConceptsNoCodeContracts(List<String> paymentConceptsNoCodeContracts) {
		this.paymentConceptsNoCodeContracts = paymentConceptsNoCodeContracts;
		return this;
	}

	public List<String> getPaymentConceptsNoRef() {
		return paymentConceptsNoRef;
	}

	public AgreementIntegrity setPaymentConceptsNoRef(List<String> paymentConceptsNoRef) {
		this.paymentConceptsNoRef = paymentConceptsNoRef;
		return this;
	}

	public List<String> getAgreementExtras() {
		return agreementExtras;
	}

	public AgreementIntegrity setAgreementExtras(List<String> agreementExtras) {
		this.agreementExtras = agreementExtras;
		return this;
	}
	
}
