package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.expression.IExpression;

public interface IContractBonus extends IExpression, IBonus, IHashStartAndEndDate{
	
	public Integer getId();
	
	public String getDescription();
	public String getExpression();
}
