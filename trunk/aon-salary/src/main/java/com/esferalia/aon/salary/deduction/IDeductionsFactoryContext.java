package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.expression.ExpressionContext;

public interface IDeductionsFactoryContext {

	ISalaryProxy getSalaryProxy();
	
	ISalary getCurrentSalary();

	ExpressionContext getExpressionContext();

}

