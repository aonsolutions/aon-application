package com.esferalia.aon.salary.payment;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryProxy;

public interface IPaymentsFactoryContext {

	ISalaryProxy getSalaryProxy();
	ISalary getCurrentSalary();
}
