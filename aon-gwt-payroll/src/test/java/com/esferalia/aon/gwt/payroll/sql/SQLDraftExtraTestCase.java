package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLExtraTestCase;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLDraftExtraTestCase extends SQLExtraTestCase {
	
	@Override
	public ISQLContractSalaryCalculatorContext getExtraSalaryCalculatorContext(
			Connection connection, ContractRecord contract, Date startDate, Date issueDate, Date endDate)
			throws SQLException, ExpressionException {
		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);
		draft.setType(Salary.Type.EXTRA);
		
		
		return EmployeesServiceHelper.getExtraCalculatorContextImpl(connection, draft, null);

	}

}
