package com.esferalia.aon.in.payroll.pdf;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;

public interface SalaryPDFBuilder<T extends ISalary> extends ISalaryBuilder<T>{
	public void execute();
}
