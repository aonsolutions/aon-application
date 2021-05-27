package com.esferalia.aon.salary.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;

public interface ISalaryCalculator<T extends ISalary, C extends ISalaryCalculatorContext>{
	

	boolean accept(C ctx);
	void setSalaryBuilder(ISalaryBuilder<T> salaryBuilder);
	void initialize(C ctx) throws SalaryException;
	T calculate(C ctx) throws SalaryException;

}
