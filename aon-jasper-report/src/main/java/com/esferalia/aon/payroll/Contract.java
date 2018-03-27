package com.esferalia.aon.payroll;

import java.util.Date;

import com.code.aon.company.WorkPlace;

public class Contract {
	
	private Salary salary;
	
	public Contract(Salary salary) {
		this.salary = salary;
	}
	
	public WorkPlace getWorkPlace() {
		return salary.getWorkPlace();
	}
	
	public Date getSeniorityDate() {
		return salary.getSeniorityDate();
	}
	
	
}
