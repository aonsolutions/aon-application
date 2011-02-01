package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.enumeration.DeductionType;


public interface IDeduction {

	public DeductionType getType();
	
	public String getDescription();
	
	public String getExpression();
	
	public double getAmount();
	
}
