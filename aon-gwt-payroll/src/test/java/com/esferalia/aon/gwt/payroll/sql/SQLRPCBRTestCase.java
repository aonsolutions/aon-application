package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLBRTestCase;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLRPCBRTestCase extends SQLBRTestCase {
	@Override
	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, ContractRecord contract) throws ExpressionException,
			SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);

		return EmployeesServiceHelper.getSalaryCalculatorContext(connection, draft, null);
//		return super.getContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, contract);

	}
}
