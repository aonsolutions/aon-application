package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.ISalaryProxy;

public class DeductionsFactoryContext implements IDeductionsFactoryContext{
	
	private ISalaryProxy salaryProxy;

	@Override
	public ISalaryProxy getSalaryProxy() {
		return salaryProxy;
	}

	public void setSalaryProxy(ISalaryProxy salaryProxy) {
		this.salaryProxy = salaryProxy;
	}

	
	

}
