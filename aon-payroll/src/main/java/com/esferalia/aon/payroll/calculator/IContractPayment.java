package com.esferalia.aon.payroll.calculator;


import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

public interface IContractPayment extends IPayment, IExpression {
	
	public Month getMonth();
	
	public Date getStartDate();
	public Date getEndDate();
	
	public String getIrpfExpression();
	public String getQuoteExpression();
	
	public SalaryType getSalaryType();
	

}
