package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.payroll.sql.AbstractSQL.ISalary;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;

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
	
	public List<LogMessage> getTestList() {
		return super.getList();
	}
	
	@Override
	protected void addMessage(SalaryBuilderListenerLevel level, String msg ) {
		if (list.size() > 256 ) {
			list.remove(0);
		}

		ISalary salaryDraft = getSalaryDraft();

		LogMessage testMsg = new LogMessage(level, msg );
		if ( salaryDraft != null ) {
			try {
				testMsg.setContractId(salaryDraft.getContract());
				testMsg.setEmployeeName(salaryDraft.getEmployeeName());
				testMsg.setEnterpriseName(salaryDraft.getEnterpriseName());
				testMsg.setStartDate(salaryDraft.getStartDate());
				testMsg.setEndDate(salaryDraft.getEndDate());
				testMsg.setIssueDate(salaryDraft.getIssueDate());
			} catch (SQLException e) {
				// TODO employee & enterprise name a null ???
			}
		}

		getTestList().add(testMsg);
		saveToLog(testMsg);
	}

	
}
