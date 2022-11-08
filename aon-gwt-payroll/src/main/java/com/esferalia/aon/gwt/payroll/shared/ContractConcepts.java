package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Set;

public class ContractConcepts implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Set<ContractConcept> paymentConcepts;
	private Set<ContractConcept> deductionConcepts;
	private Set<ContractConcept> bonusConcepts;
	private Set<ContractConcept> costConcepts;
	
	public ContractConcepts() {
		super();
	}

	public Set<ContractConcept> getPaymentConcepts() {
		return paymentConcepts;
	}

	public ContractConcepts setPaymentConcepts(Set<ContractConcept> paymentConcepts) {
		this.paymentConcepts = paymentConcepts;
		return this;
	}

	public Set<ContractConcept> getDeductionConcepts() {
		return deductionConcepts;
	}

	public ContractConcepts setDeductionConcepts(Set<ContractConcept> deductionConcepts) {
		this.deductionConcepts = deductionConcepts;
		return this;
	}

	public Set<ContractConcept> getBonusConcepts() {
		return bonusConcepts;
	}

	public ContractConcepts setBonusConcepts(Set<ContractConcept> bonusConcepts) {
		this.bonusConcepts = bonusConcepts;
		return this;
	}

	public Set<ContractConcept> getCostConcepts() {
		return costConcepts;
	}

	public void setCostConcepts(Set<ContractConcept> costConcepts) {
		this.costConcepts = costConcepts;
	}
	
}
