/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.AGREEMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SYSTEM;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionException;

/**
 * @author rtrepiana
 *
 */
public class SQLFunctionsTestCase extends
		AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testSystemFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
						put(MONTH_DAYS.getName(), "666.00");
					}
				});
		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		// First of month of contract, 99% will be partial
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		

		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));
	}

	@Test
	public void testAgreementFunction() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[0], 				
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "69.00");
			}
		});
		ContractRecord contract = newContract(aonContext, 
		getToday(), 
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "666.00");
			}
		},
		category);
		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		// First of month of contract, 99% will be partial
		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), 69.00, result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));
		
		Date startDate = getToday();
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, category, startDate, endDate, 			
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "96.00");
			}
		});
		ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), 96.00, result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));

	
		startDate = getToday();
		endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract.getDomain(), category, startDate, endDate, 			
			new HashMap<String, String>() {
			{
				put(MONTH_DAYS.getName(), "99.00");
			}
		});
		ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		ctx.getExpressionContext().eval(String.format("%s('%s')", SYSTEM, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", SYSTEM, MONTH_DAYS), (double) getMax(end, DAY_OF_MONTH), result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s('%s')", AGREEMENT, MONTH_DAYS), 99.00, result.getValue()));
		ctx.getExpressionContext().eval(String.format("%s", MONTH_DAYS), getToday(), end, Double.class)
		.stream()
		.forEach(result-> Assert.assertEquals(String.format("%s", MONTH_DAYS), 666.00, result.getValue()));
	}

	
	// ------------------------------------------------------------------------
	
}
