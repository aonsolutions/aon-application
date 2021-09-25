package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class ContractPaymentData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ContractConceptCalc> contractConceptCalcs;
	
	public ContractPaymentData() {
		super();
	}

	public List<ContractConceptCalc> getCcontractConceptCalcs() {
		return contractConceptCalcs;
	}

	public void setContractConceptCalcs(List<ContractConceptCalc> contractConceptCalcs) {
		this.contractConceptCalcs = contractConceptCalcs;
	}
	
}
