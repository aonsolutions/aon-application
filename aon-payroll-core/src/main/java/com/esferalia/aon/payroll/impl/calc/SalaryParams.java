package com.esferalia.aon.payroll.impl.calc;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.core.IEmployee;
import com.esferalia.aon.payroll.core.enumeration.SalaryType;

public class SalaryParams {
	private IEmployee employee;
	private Month month;
	private Integer year;
	private SalaryType type;
	private Date dueDate;
	

	public IEmployee getEmployee() {
		return employee;
	}
	public void setEmployee(IEmployee employee) {
		this.employee = employee;
	}
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public SalaryType getType() {
		return type;
	}
	public void setType(SalaryType type) {
		this.type = type;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
