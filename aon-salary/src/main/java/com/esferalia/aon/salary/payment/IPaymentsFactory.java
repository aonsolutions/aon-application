package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.SalaryException;

public interface IPaymentsFactory {

	boolean accept(IPaymentsFactoryContext ctx);
	Payments getPayments( IPaymentsFactoryContext ctx ) throws SalaryException;
	
}
