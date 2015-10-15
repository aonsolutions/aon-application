package com.esferalia.aon.salary.bonus;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.expression.ExpressionContext;

public interface IBonusesFactoryContext {

	ISalaryProxy getSalaryProxy();
	
	ISalary getCurrentSalary();

	ExpressionContext getExpressionContext();

}

