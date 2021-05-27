package com.code.aon.company;

import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.payroll.Salary;

public class WorkPlace {
	
	private Salary salary;
	
	public WorkPlace( Salary salary) {
		this.salary = salary;
	}
	
	public Enterprise getEnterprise() {
		return salary.getEnterprise();
	}

	public RegistryAddress getAddress() {
		return salary.getRegistryAddress();
	}

}
