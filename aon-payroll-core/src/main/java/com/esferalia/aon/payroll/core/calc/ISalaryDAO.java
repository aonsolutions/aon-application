package com.esferalia.aon.payroll.core.calc;

import com.esferalia.aon.payroll.core.ISalary;
import com.esferalia.aon.payroll.impl.calc.SalaryParams;

public interface ISalaryDAO {
	
	ISalary getSalary(SalaryParams params);

}
