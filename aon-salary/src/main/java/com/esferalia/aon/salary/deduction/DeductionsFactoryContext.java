package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;

public class DeductionsFactoryContext implements IDeductionsFactoryContext{
	
	private ISalaryProxy salaryProxy;
	private ISalary  currentSalary;

	@Override
	public ISalaryProxy getSalaryProxy() {
		return salaryProxy;
	}

	public void setSalaryProxy(ISalaryProxy salaryProxy) {
		this.salaryProxy = salaryProxy;
	}

	@Override
	public ISalary getCurrentSalary() {
		return currentSalary;
	}

	public void setCurrentSalary(ISalary currentSalary) {
		this.currentSalary = currentSalary;
	}

}
