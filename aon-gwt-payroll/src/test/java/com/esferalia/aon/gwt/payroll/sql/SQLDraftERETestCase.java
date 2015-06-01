package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FACTOR;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLERETestCase;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLDraftERETestCase extends SQLERETestCase {

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			java.sql.Date issueDate, ContractRecord contract)
			throws ExpressionException, SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);

		return EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
	};

	@Test
	public void testEREIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(CGC_BASE_MIN.getName(),
								Integer.toString(Integer.MIN_VALUE));
						put(CGP_BASE_MIN.getName(),
								Integer.toString(Integer.MIN_VALUE));
						put(CGC_BASE_MAX.getName(),
								Integer.toString(Integer.MAX_VALUE));
						put(CGP_BASE_MAX.getName(),
								Integer.toString(Integer.MAX_VALUE));
					}
				},
				new String[] { "( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES", },
				new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC); BASE_CGC * 0.10",
						"TRACE('BASE_CGP = %f\r\n', BASE_CGP); BASE_CGP * 0.05" },
				newAgreement(aonContext, new Extra[] {}, Collections.emptyMap()));



		PaymentConceptRecord ere = addConcept(aonContext, ERE.getName());
		addPayment(aonContext, contract, ere, null,
				"TRACE('ERE = %f \r\n', (DIAS_ERE * BASE_REGULADORA)); DIAS_ERE * BASE_REGULADORA");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		
		Date startEre = getFirstDayOfMonth(getToday());
		int ereDays = (int) Math.max(1,(Math.random() * (getMax(getToday(), DAY_OF_MONTH) - 1)));
		Date endEre = add(startEre, DAY_OF_MONTH, ereDays - 1);
		
		NumberVariable ereVar = new NumberVariable();
		ereVar.setName(ERE_FACTOR.getName());
		ereVar.setStartDate(startEre);
		ereVar.setEndDate(endEre);
		ereVar.setValue(1.00);
		
		draft.addDraftVariable(ereVar);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder() {
				}).calculate(ctx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}

		Assert.assertEquals(
				((1500.00 + 250.00) * 1.10 )*(get(endDate, DAY_OF_MONTH) - ereDays) / get(endDate, DAY_OF_MONTH), 
				salary.getTotalPayment(),
				DELTA);

		Assert.assertEquals((1750.00 * 1.10), salary.getCommonBase(), DELTA);

		Assert.assertEquals(salary.getTotalPayment() * 0.15,
				salary.getSocialSecurityContributions(), DELTA);
	}

}
