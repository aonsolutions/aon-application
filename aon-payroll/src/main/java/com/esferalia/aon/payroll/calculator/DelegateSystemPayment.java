package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.payroll.DelegateContractPayment;

public class DelegateSystemPayment extends DelegateContractPayment implements ISystemPayment {
	
	private ISystemPayment systemPayment;
	
	public DelegateSystemPayment(ISystemPayment systemPayment) {
		super(systemPayment);
		this.systemPayment = systemPayment;
	}

	@Override
	public int getDomain() {
		return systemPayment.getDomain();
	}

}
