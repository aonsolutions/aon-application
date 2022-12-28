package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

public class ContractConceptCalc extends Payment implements Serializable {
	
	public enum ContractConceptCalcType {
		PAYMENT,
		DEDUCTION,
		COST,
		BONUS,
		EMBARGO
	}

	private static final long serialVersionUID = 1L;
	
	private ContractConceptCalcType contractConceptCalcType;
	private String codeType;
	private boolean hasChange;
	
	// The key provider that provides the unique ID of a contact.
    public static final ProvidesKey<ContractConceptCalc> KEY_PROVIDER = item -> item == null ? null : item.getId();
	
	public ContractConceptCalc() {
		super();
	}

	public ContractConceptCalc(Payment payment) {
		this.id = payment.getId();
		this.description = payment.getDescription();
		this.expression = payment.getExpression();
		this.irpfExpression = payment.getIrpfExpression();
		this.quoteExpression = payment.getQuoteExpression();
		this.type = payment.getType();
		this.salaryType = payment.getSalaryType();
		this.name = payment.getName();
		this.month = payment.getMonth();
		this.setStartDate(payment.getStartDate());
		this.setEndDate(payment.getEndDate());
	}

	public ContractConceptCalcType getContractConceptCalcType() {
		return contractConceptCalcType;
	}

	public ContractConceptCalc setContractConceptCalcType(ContractConceptCalcType contractConceptCalcType) {
		this.contractConceptCalcType = contractConceptCalcType;
		return this;
	}

	public boolean getHasChange() {
		return hasChange;
	}

	public ContractConceptCalc setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		return this;
	}

	public ContractConceptCalc setCodeType(String codeType) {
		this.codeType = codeType;
		return this;
	}
	
	public String getCodeType() {
		return this.codeType;
	}
	
}
