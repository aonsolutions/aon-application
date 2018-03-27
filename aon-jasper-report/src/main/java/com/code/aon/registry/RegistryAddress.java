package com.code.aon.registry;

import com.esferalia.aon.payroll.Salary;

public class RegistryAddress {
	
	private Salary salary;
	
	public RegistryAddress(Salary salary) {
		this.salary = salary;
	}

	public String getProvince() {
		return salary.getProvince();
	}
	
	
}
