package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.ISalaryProxy;

public class PaymentsFactoryContext implements IPaymentsFactoryContext{
	
	private ISalaryProxy salaryProxy;

	@Override
	public ISalaryProxy getSalaryProxy() {
		return salaryProxy;
	}

	public void setSalaryProxy(ISalaryProxy salaryProxy) {
		this.salaryProxy = salaryProxy;
	}
	

}
