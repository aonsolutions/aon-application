package com.esferalia.aon.salary.deduction;

import java.io.Serializable;

import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.DeductionType;


public interface IDeduction extends ISalaryItem<DeductionType>, Serializable {
	
	public String getExpression();
	
	
}
