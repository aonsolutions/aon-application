package com.esferalia.aon.payroll.calculator;

import java.util.function.Predicate;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;

public class OnlyPaymentContractSalaryCalculator<T extends ISalary> extends
		ContractSalaryCalculator<T> {
	
	@Override
	protected Double fillCosts(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		return 0.0;
	}
	
	@Override
	protected Double fillBonus(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		return 0.0;
	}
	
	@Override
	protected Double fillIrpf(IContractSalaryCalculatorContext ctx) 
			throws SalaryException {
		return 0.0;
	}
	
	@Override
	protected Double fillSSDeductions(IContractSalaryCalculatorContext ctx) 
			throws SalaryException {
		return 0.0;
	}
	
	@Override
	protected Double fillOtherDeductions(IContractSalaryCalculatorContext ctx) 
			throws SalaryException {
		return 0.0;
	}
	
	@Override
	protected Double fillAdvances(IContractSalaryCalculatorContext ctx) 
			throws SalaryException {
		return 0.0;
	}
	
	@Override
	protected Double fillEmbargos(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		return 0.0;
	}
	
	
	@Override
	protected void fillSalaryData(IContractSalaryCalculatorContext ctx) {
	}
	
	@Override
	protected void fillEnterpriseData(IContractSalaryCalculatorContext ctx) {
	}
	
	@Override
	protected void fillEmployeeData(IContractSalaryCalculatorContext ctx) {
	}
}
