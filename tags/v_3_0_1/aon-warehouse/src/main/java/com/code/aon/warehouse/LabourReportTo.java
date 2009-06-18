package com.code.aon.warehouse;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;

public class LabourReportTo implements ITransferObject {

	private Employee employee;
	
	private double estimatedHours;
	
	private double realHours;

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public double getEstimatedHours() {
		return estimatedHours;
	}

	public void setEstimatedHours(double estimatedHours) {
		this.estimatedHours = estimatedHours;
	}

	public double getRealHours() {
		return realHours;
	}

	public void setRealHours(double realHours) {
		this.realHours = realHours;
	}
	
	public double getDifference(){
		return this.estimatedHours - this.realHours;
	}
}