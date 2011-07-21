package com.esferalia.aon.payroll.calculator.sql;

import java.sql.SQLException;

import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public interface ISQLContractSalaryCalculatorContext 
	extends IContractSalaryCalculatorContext{
	
	public void close() throws SQLException ;

	boolean next() throws SQLException, ExpressionException;
	

}
