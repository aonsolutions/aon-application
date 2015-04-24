package com.esferalia.aon.gwt.payroll.shared;


public class SalaryPreview extends Salary {
	
	private Employee employee;
	
	
	public SalaryPreview() {
	}
	
	
	public Employee getEmployee() {
		return employee;
	}
	
	public <T extends SalaryPreview> T  setEmployee(Employee employee) {
		this.employee = employee;
		return (T) this;
	}

}
