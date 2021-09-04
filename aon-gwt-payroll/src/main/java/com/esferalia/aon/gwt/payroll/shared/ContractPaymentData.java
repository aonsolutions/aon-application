package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class ContractPaymentData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ContractPayment> contractPayments;
	private List<ContractDeduction> contractDeductions;
	
	public ContractPaymentData() {
		super();
	}

	public List<ContractPayment> getContractPayments() {
		return contractPayments;
	}

	public void setContractPayments(List<ContractPayment> contractPayments) {
		this.contractPayments = contractPayments;
	}

	public List<ContractDeduction> getContractDeductions() {
		return contractDeductions;
	}

	public void setContractDeductions(List<ContractDeduction> contractDeductions) {
		this.contractDeductions = contractDeductions;
	}
	
}
