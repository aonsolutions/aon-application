package com.esferalia.aon.payroll.calculator;

public class SimpleSystemDeduction extends SimpleContractDeduction implements ISystemDeduction{

	int domain;
	
	public SimpleSystemDeduction() {
	}

	public SimpleSystemDeduction(IContractDeduction contractDeduction) {
		super(contractDeduction);
	}
	
	public int getDomain() {
		return domain;
	}
	
	public void setDomain(int domain) {
		this.domain = domain;
	}
	
	
	
}
