package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.expression.ExpressionContext;

public interface IPaymentsFactoryContext {

	ISalaryProxy getSalaryProxy();
	ISalary getCurrentSalary();
	ExpressionContext getExpressionContext();
}
