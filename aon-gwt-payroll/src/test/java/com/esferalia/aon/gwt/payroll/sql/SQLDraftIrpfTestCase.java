package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfTestCase;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLDraftIrpfTestCase extends SQLIrpfTestCase {
	
	@Test
	@Override
	public void testTotalLiquid() throws ExpressionException, SQLException {
	}
	
	@Override
	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, ContractRecord contract) throws ExpressionException,
			SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		int months  = 12 - get(startDate,Calendar.MONTH);

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);

		
		for ( int i = 0; i <  6; i ++ ) {
			com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
			draftPayment.setStartDate(startDate);
			draftPayment.setEndDate(endDate);
			draftPayment.setExpression("66666.00/(6.00 * " + months + ")");
			draftPayment.setIrpfExpression("_P");
			draftPayment.setQuoteExpression("_P");
			draftPayment.setSalaryType(Salary.Type.SALARY);
			draftPayment
					.setDescription("RETRIBUCION NO INCLUIDA EN OTROS APARTADOS");
			draftPayment
					.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0001);
	
			draft.addDraftPayment(draftPayment);
		}

		return EmployeesServiceHelper.getSalaryCalculatorContext(connection, draft, null);
		
	}
//	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
//			Connection connection, Date startDate, Date endDate,
//			Date issueDate, Criteria criteria) throws ExpressionException,
//			SQLException {
//
//		SalaryDraft draft = new SalaryDraft();
//
//		
//		for ( int i = 0; i <  6; i ++ ) {
//			com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
//			draftPayment.setStartDate(startDate);
//			draftPayment.setEndDate(endDate);
//			draftPayment.setExpression("66666/6");
//			draftPayment.setIrpfExpression("_P");
//			draftPayment.setQuoteExpression("_P");
//			draftPayment.setSalaryType(Salary.Type.SALARY);
//			draftPayment
//					.setDescription("RETRIBUCION NO INCLUIDA EN OTROS APARTADOS");
//			draftPayment
//					.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0001);
//	
//			draft.addDraftPayment(draftPayment);
//		}
//
//
//		SQLSalaryDraftCalculatorContext draftCtx = new SQLSalaryDraftCalculatorContext(
//				draft, connection, startDate, endDate, issueDate, criteria);
//		
//		draftCtx.next();
//		
//		return draftCtx;
//	}

	@Override
	protected ISQLContractSalaryCalculatorContext getContractSettleCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {

		SalaryDraft draft = new SalaryDraft();

		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();

		draftPayment.setStartDate(startDate);
		draftPayment.setEndDate(endDate);
		draftPayment.setExpression("66666");
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setSalaryType(Salary.Type.SETTLE);
		draftPayment.setDescription("VACACIONES RETRIBUIDAS NO DISFRUTADAS");
		draftPayment
				.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0006);

		draft.addDraftPayment(draftPayment);

		SQLSettleDraftCalculatorContext draftCtx = new SQLSettleDraftCalculatorContext(
				draft, connection, startDate, endDate, issueDate, criteria);

		draftCtx.next();
		return draftCtx;
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration) {
		super.assertAnnualRemuneration(expected + 66666.00, annualRemuneration);
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, double delta) {
		super.assertAnnualRemuneration(expected + 66666.00, annualRemuneration, delta);
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, int months, double delta) {
		super.assertAnnualRemuneration(expected + (66666.00 / (6.00 *12) * months), annualRemuneration, delta);
	}

	@Override
	protected void assertDeduccibleExpenses(double expected,
			double deduccibleExpenses) {
		super.assertDeduccibleExpenses(expected + (66666.00 * 0.15), deduccibleExpenses);
	}
}
