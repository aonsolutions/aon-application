package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSettleTestCase;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLSettleDraftTestCase extends
		SQLSettleTestCase {
	
	
	SalaryDraft draft ;
	
	@Before
	public void newDraft() {
		draft = new SalaryDraft();
	}
	
	@After
	public void nullDraft() {
		draft = null;
	}

	@Override
	protected void setData(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			Map<String, String> datas) {
		for ( Map.Entry<String, String> entry: datas.entrySet()) {
			StringVariable variable = new StringVariable();
			variable.setName(entry.getKey());
			variable.setExpression(entry.getValue());
			variable.setStartDate(startDate);
			variable.setEndDate(endDate);
			draft.addDraftVariable(variable);
		}
	}
	
	@Override
	protected void addPayment(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
			String description, String expression, String irpfExpression, String quoteExpression, PaymentType type) {
		com.esferalia.aon.gwt.payroll.shared.Payment payment = new com.esferalia.aon.gwt.payroll.shared.Payment();
		payment.setScope(Scope.SALARY);
		payment.setStartDate(startDate);
		payment.setEndDate(endDate);
		payment.setDescription(description);
		payment.setExpression(expression);
		payment.setIrpfExpression(irpfExpression);
		payment.setQuoteExpression(quoteExpression);
		payment.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0000);
		payment.setSalaryType(Salary.Type.SETTLE);
		
		draft.addDraftPayment(payment);
	}
	
	@Override
	protected  ISQLContractSalaryCalculatorContext getSQLContractSettleContext(Connection connection,
			Date contractStart, ContractRecord contract) throws SQLException,
			ExpressionException {

		Employee employee = new Employee();
		employee.setId(contract.getId());

		draft.setEmployee(employee);
		draft.setStartDate(contractStart);
		draft.setEndDate(getToday());
		draft.setIssueDate(getToday());
		draft.setType(Salary.Type.SETTLE);
		
		return EmployeesServiceHelper.getSettleCalculatorContextImpl(connection, draft, null);
	}
	
	@Override
	protected  ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection,
			Date contractStart, ContractRecord contract) throws SQLException,
			ExpressionException {

		Employee employee = new Employee();
		employee.setId(contract.getId());

		draft.setEmployee(employee);
		draft.setStartDate(contractStart);
		draft.setEndDate(getToday());
		draft.setIssueDate(getToday());
		draft.setType(Salary.Type.SETTLE);
		
		return EmployeesServiceHelper.getSettleCalculatorContextImpl(connection, draft, null);
	}
	

}
