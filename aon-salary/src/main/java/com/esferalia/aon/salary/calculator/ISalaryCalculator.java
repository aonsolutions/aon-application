package com.esferalia.aon.salary.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;

public interface ISalaryCalculator{

	void initialize(ISalaryCalculatorContext ctx) throws SalaryException;
	ISalary calculate(ISalaryCalculatorContext ctx) throws SalaryException;
	boolean accept(ISalaryCalculatorContext ctx);

}
