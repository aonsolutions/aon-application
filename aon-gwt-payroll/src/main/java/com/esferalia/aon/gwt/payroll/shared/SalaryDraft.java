package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

public class SalaryDraft extends Salary {
	
	private Employee employee;
	
	
	public SalaryDraft() {
	}
	
	
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
