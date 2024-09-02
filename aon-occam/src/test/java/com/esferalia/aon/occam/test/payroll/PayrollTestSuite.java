package com.esferalia.aon.occam.test.payroll;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.payroll.employee.EmployeeTest;

@Suite
@SelectClasses({
	EmployeeTest.class,
})
public class PayrollTestSuite {
	
}
