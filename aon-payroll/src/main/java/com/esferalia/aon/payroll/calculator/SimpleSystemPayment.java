package com.esferalia.aon.payroll.calculator;

import com.code.aon.AonVersion;

public class SimpleSystemPayment extends SimpleContractPayment implements ISystemPayment{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
