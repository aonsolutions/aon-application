package com.esferalia.aon.occam.test.payroll;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.payroll.employee.EmployeeTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	EmployeeTestSuite.class,
})
public class PayrollTestSuite {
	
}
