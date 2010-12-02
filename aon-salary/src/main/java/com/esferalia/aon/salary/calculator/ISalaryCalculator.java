package com.esferalia.aon.salary.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;

public interface ISalaryCalculator{

	void initialize(SalaryCalculatorContext ctx) throws SalaryException;
	ISalary calculate(SalaryCalculatorContext ctx) throws SalaryException;
	boolean accept(SalaryCalculatorContext ctx);

}
