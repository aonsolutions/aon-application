package com.esferalia.aon.payroll.calculator;

import com.code.aon.AonVersion;

public class SimpleSystemCost extends SimpleContractDeduction implements ISystemCost{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
