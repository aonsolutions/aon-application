package com.esferalia.aon.salary.calculator;

import java.util.Date;

import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.expression.ExpressionContext;

public interface ISalaryCalculatorContext {
	
	public Date getChargeDate();

	public Date getIssueDate();
	
	public Date getStartDate() ;
	
	public Date getEndDate() ;
	
	public ISalaryProxy getSalaryProxy();

	public ExpressionContext getExpressionContext() ;

}
