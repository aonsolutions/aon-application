package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.DeductionType;


public interface IDeduction extends ISalaryItem<DeductionType>{
	
	public String getExpression();
	
	
}
