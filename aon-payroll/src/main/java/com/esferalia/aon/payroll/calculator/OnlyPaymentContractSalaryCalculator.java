package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.salary.SalaryException;

public class OnlyPaymentContractSalaryCalculator extends
		ContractSalaryCalculator {
	
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
	protected Double fillDeductions(IContractSalaryCalculatorContext ctx)
			throws SalaryException {
		return 0.00;
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
