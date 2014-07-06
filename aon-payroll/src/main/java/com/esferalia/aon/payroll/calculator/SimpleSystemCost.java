package com.esferalia.aon.payroll.calculator;

public class SimpleSystemCost extends SimpleContractDeduction implements ISystemCost{

	int domain;
	
	public SimpleSystemCost() {
	}

	public SimpleSystemCost(IContractDeduction contractDeduction) {
		super(contractDeduction);
	}
	
	public int getDomain() {
		return domain;
	}
	
	public void setDomain(int domain) {
		this.domain = domain;
	}
	
	
	
}
