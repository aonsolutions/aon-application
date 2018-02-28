package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;

public class ContractSalaryCalculator<T extends ISalary> extends GenericContractSalaryCalculator<T, ISalaryCalculatorContext> {

	public static class Listener extends GenericContractSalaryCalculator.Listener{
		
	}

	public ContractSalaryCalculator() {
		super();
	}

	public ContractSalaryCalculator(ISalaryBuilder<T> salaryBuilder) {
		super(salaryBuilder);
	}


}
