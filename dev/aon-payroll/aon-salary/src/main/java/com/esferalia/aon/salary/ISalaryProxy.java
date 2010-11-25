package com.esferalia.aon.salary;

import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;


public interface ISalaryProxy {

	ISalary getSalary() throws SalaryException;
	SalaryCalculatorContext getSalaryCalculatorContext() throws SalaryException;
}
