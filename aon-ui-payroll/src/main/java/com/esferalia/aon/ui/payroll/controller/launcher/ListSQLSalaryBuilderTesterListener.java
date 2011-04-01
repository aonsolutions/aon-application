package com.esferalia.aon.ui.payroll.controller.launcher;

import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.payroll.sql.AbstractSQL.ISalary;

public class ListSQLSalaryBuilderTesterListener extends
		ListSalaryBuilderListener {

	
	private SQLSalaryBuilderTester sqlSalaryBuilderTester;
	
	public ListSQLSalaryBuilderTesterListener( SQLSalaryBuilderTester sqlSalaryBuilderTester) {
		super();
		this.sqlSalaryBuilderTester = sqlSalaryBuilderTester;
	}

	public ISalary getSalaryDraft() {
		return sqlSalaryBuilderTester.getSalaryDraft();
	}

	public ISalary getDBSalary() {
		return sqlSalaryBuilderTester.getDBSalary();
	}
	
	
	
}
