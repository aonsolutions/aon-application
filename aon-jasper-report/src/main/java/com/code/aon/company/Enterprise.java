package com.code.aon.company;

import com.esferalia.aon.payroll.Salary;

public class Enterprise {

	private Salary salary;
	
	public Enterprise( Salary salary) {
		this.salary = salary;
	}
	
	public Integer getId() {
		return salary.getEnterpriseId();
	}
	
	
}
