package com.code.aon.employee.calculator;


import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

public interface IContractPayment extends IPayment, IExpression {
	
	public Month getMonth();
	
	public String getIrpfExpression();
	public String getQuoteExpression();
}
