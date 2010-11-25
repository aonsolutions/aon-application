package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;

public class PaymentsFactoryContext implements IPaymentsFactoryContext{
	
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
