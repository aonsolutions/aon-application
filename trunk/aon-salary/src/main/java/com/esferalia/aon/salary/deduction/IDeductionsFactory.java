package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.SalaryException;

public interface IDeductionsFactory {

	boolean accept(IDeductionsFactoryContext ctx);
	Deductions getDeductions( IDeductionsFactoryContext ctx ) throws SalaryException;
	
}
