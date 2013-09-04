package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;


public interface IPaymentHandler {
	
	// EXPRESSION EDITION
	public String getExpression();
	
	public String getQuoteExpression();
	
	public String getIrpfExpression();
	
	public void setExpression(String expression);
	
	public void setQuoteExpression(String expression);
	
	public void setIrpfExpression(String expression);

	public TaxationType getTaxation();
	
	public QuoteType getQuote();
	
	// PERIOD FOR RESULT CALCULATION
	public Month getMonth();
	
	public Integer getYear();
	
	

}
