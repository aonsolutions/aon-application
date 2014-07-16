package com.esferalia.aon.payroll.calculator;

import com.code.aon.AonVersion;

public class SimpleSystemDeduction extends SimpleContractDeduction implements ISystemDeduction{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
