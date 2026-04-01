package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
	
	@BeforeEach
	public void newDraft() {
		draft = new SalaryDraft();
	}
	
	@AfterEach
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
	protected void addSettlePayment(AONContext aonContext, ContractRecord contract, Date startDate, Date endDate,
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
	protected void addSettleEmbargo(AONContext aonContext, ContractRecord contract, String description,
			String expression) {
		com.esferalia.aon.gwt.payroll.shared.Deduction embargo = new com.esferalia.aon.gwt.payroll.shared.Deduction();
		embargo.setScope(Scope.SALARY);
		embargo.setDescription(description);
		embargo.setExpression(expression);
		embargo.setSalaryType(Salary.Type.SETTLE);
		embargo.setStartDate(contract.getStartDate());
		embargo.setEndDate(contract.getEndDate());
		//embargo.setId(Integer.MIN_VALUE);
		
		draft.addDraftEmbargo(embargo);
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
	
	protected ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			Date endDate, ContractRecord contract) throws SQLException, ExpressionException {
		Employee employee = new Employee();
		employee.setId(contract.getId());

		draft.setEmployee(employee);
		draft.setStartDate(contractStart);
		draft.setEndDate(endDate);
		draft.setIssueDate(getToday());
		draft.setType(Salary.Type.SETTLE);
		
		return EmployeesServiceHelper.getSettleCalculatorContextImpl(connection, draft, null);
	}

}
