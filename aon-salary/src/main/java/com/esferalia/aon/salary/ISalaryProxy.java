package com.esferalia.aon.salary;

import java.util.Date;

import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;


public interface ISalaryProxy {

	ISalary getSalary() throws SalaryException;
	ISalaryCalculatorContext getSalaryCalculatorContext() throws SalaryException;
	ISalaryCalculatorContext getSalaryCalculatorContext(Date startDate, Date endDate, Date issueDate) throws SalaryException;
}
