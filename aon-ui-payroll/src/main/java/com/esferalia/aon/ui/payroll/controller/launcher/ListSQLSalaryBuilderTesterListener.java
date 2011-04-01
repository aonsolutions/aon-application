package com.esferalia.aon.ui.payroll.controller.launcher;

import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;

public class ListSQLSalaryBuilderTesterListener extends
		ListSalaryBuilderListener {

	
	private SQLSalaryBuilderTester sqlSalaryBuilderTester;
	
	public ListSQLSalaryBuilderTesterListener( SQLSalaryBuilderTester sqlSalaryBuilderTester) {
		super();
		this.sqlSalaryBuilderTester = sqlSalaryBuilderTester;
	}
	
	
	
}
