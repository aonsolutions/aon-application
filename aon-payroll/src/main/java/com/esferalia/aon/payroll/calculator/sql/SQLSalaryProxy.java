package com.esferalia.aon.payroll.calculator.sql;

import java.util.Date;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
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


}
