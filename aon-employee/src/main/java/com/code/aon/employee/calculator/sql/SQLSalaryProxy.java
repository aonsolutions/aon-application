package com.code.aon.employee.calculator.sql;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.SalaryCalculatorContext;

public class SQLSalaryProxy implements ISalaryProxy {

	private Integer id;
	
	public SQLSalaryProxy( Integer id ) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	@Override
	public ISalary getSalary() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SalaryCalculatorContext getSalaryCalculatorContext()
			throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

}
