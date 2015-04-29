package com.esferalia.aon.payroll.calculator;


import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.payment.IPayment;

public interface IContractPayment extends IPayment, IExpression, IHashStartAndEndDate {
	
	public Integer getId();
	
	public Month getMonth();
	
	public Integer getConceptId();

	public String getIrpfExpression();
	public String getQuoteExpression();
	
	public SalaryType getSalaryType();
	
	public boolean isDescriptionDecorable();

}
