package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class EmployeeContractInfo implements Serializable{
	
	private EmployeeInfo employeeInfo;
	private ContractInfo contractInfo;
	
	public EmployeeContractInfo(){
		super();
	}
	
	// ------------- GETTERS / SETTERS -------------

	public EmployeeInfo getEmployeeInfo() {
		return employeeInfo;
	}

	public void setEmployeeInfo(EmployeeInfo employeeInfo) {
		this.employeeInfo = employeeInfo;
	}

	public ContractInfo getContractInfo() {
		return contractInfo;
	}

	public void setContractInfo(ContractInfo contractInfo) {
		this.contractInfo = contractInfo;
	}

}