package com.esferalia.aon.payroll.calculator;

import java.util.Date;

import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.expression.IExpression;

public interface IContractBonus extends IExpression, IBonus {

	public Date getStartDate();
	public Date getEndDate();
	public String getDescription();
	public String getExpression();
}
