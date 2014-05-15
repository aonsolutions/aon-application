package com.esferalia.aon.payroll.calculator;

public class SimpleSystemPayment extends SimpleContractPayment implements ISystemPayment{

	int domain;
	
	public SimpleSystemPayment() {
	}

	public SimpleSystemPayment(IContractPayment contractPayment) {
		super(contractPayment);
	}
	
	public int getDomain() {
		return domain;
	}
	
	public void setDomain(int domain) {
		this.domain = domain;
	}
	
	
	
}
