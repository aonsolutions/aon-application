package com.code.aon.employee;

import com.code.aon.employee.enumeration.DeductionType;

public interface IDeduction {

	public DeductionType getType();
	
	public String getDescription();
	
	public String getFunction();
	
	public double getAmount();
	
}
