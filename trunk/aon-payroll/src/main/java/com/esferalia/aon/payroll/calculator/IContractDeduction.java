package com.esferalia.aon.payroll.calculator;

import java.util.Date;

import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.expression.IExpression;

public interface IContractDeduction extends IDeduction, IExpression {
	
	public Integer getId();
	public Date getStartDate();
	public Date getEndDate();
}
