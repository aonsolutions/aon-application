package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;

@Deprecated
public class ContractSalaryCalculator<T extends ISalary> extends GenericContractSalaryCalculator<T, IContractSalaryCalculatorContext> {

	public static class Listener extends GenericContractSalaryCalculator.Listener{
		
	}

	public ContractSalaryCalculator() {
		super();
	}

	public ContractSalaryCalculator(ISalaryBuilder<T> salaryBuilder) {
		super(salaryBuilder);
	}


}
