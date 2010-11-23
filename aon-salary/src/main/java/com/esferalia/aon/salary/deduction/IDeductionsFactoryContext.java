package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;

public interface IDeductionsFactoryContext {

	ISalaryProxy getSalaryProxy();
	
	ISalary getCurrentSalary();

}

