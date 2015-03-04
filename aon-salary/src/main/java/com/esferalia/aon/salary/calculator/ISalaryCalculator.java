package com.esferalia.aon.salary.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;

public interface ISalaryCalculator<T extends ISalary>{
	

	boolean accept(ISalaryCalculatorContext ctx);
	void setSalaryBuilder(ISalaryBuilder<T> salaryBuilder);
	void initialize(ISalaryCalculatorContext ctx) throws SalaryException;
	T calculate(ISalaryCalculatorContext ctx) throws SalaryException;

}
