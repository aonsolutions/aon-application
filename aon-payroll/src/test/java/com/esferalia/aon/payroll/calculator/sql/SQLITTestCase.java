package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SALARY_START;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_LIQUID;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.lang.String.format;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLITTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;


	@Test
	public void testCommonDiseaseITWithGTZDO() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);

		for(com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments()) {
			System.out.println(payment.getName() +  " = " + payment.getAmount() + " (" + payment.getExpression() +")");
		}
		
		Assert.assertEquals(4, salary.getSalaryPayments().size());

	}



	// ------------------------------------------------------------------------

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, ContractRecord contract) throws ExpressionException, SQLException{
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		return getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);
	}

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws ExpressionException, SQLException{
		ISQLContractSalaryCalculatorContext ctx =  new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);
		ctx.next();
		return ctx;
	}


}
