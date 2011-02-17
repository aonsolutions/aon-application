package com.esferalia.aon.salary.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;

public interface ISalaryCalculator{
	

	boolean accept(ISalaryCalculatorContext ctx);
	void setSalaryBuilder(ISalaryBuilder salaryBuilder);
	void initialize(ISalaryCalculatorContext ctx) throws SalaryException;
	ISalary calculate(ISalaryCalculatorContext ctx) throws SalaryException;

}
