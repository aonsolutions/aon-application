package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.FRIDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SATURDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.SUNDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.THURSDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TUESDAY_HOURS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WEDNESDAY_HOURS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.lang.String.format;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;

import static org.junit.jupiter.api.Assertions.*;

public class SQLReductionTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.000001;

	@Test
	public void testReductionI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				Collections.emptyMap());
		
		addData(aonContext, 
				contract, 
				getFirstDayOfMonth(getToday()),
				null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.FULL_TIME.getName(), "false");
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
					}
				});
		
		addPayment(aonContext, contract, getFirstDayOfYear(getToday()), "666.00 * DIAS_TRABAJADOS / DIAS_MES");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		int lastDayOfMonth = get(getLastDayOfMonth(getToday()), Calendar.DATE);
		assertEquals(lastDayOfMonth * 0.50, workDays.get(0).getValue(), "DIAS TRABAJADOS");
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		assertEquals(666.00 * 0.50, salary.getTotalPayment(), "TOTAL PAYMENT");


	}

	@Test
	public void testReductionII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				Collections.emptyMap());
		
		Date reductionStart = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 11);
		
		addData(aonContext, 
				contract, 
				reductionStart,
				null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "'100'");
						put(ContextVariable.QUOTE_GROUP.getName(), "'01'");
						put(ContextVariable.FULL_TIME.getName(), "FALSO()");
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
					}
				});
		
		addPayment(aonContext, contract, getFirstDayOfYear(getToday()), "666.00 * DIAS_TRABAJADOS / DIAS_MES");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		final int lastDayOfMonth = get(getLastDayOfMonth(getToday()), Calendar.DATE);
		assertEquals(11.0, workDays.get(0).getValue(), "DIAS TRABAJADOS");
		assertEquals((lastDayOfMonth -11) * 0.50, workDays.get(1).getValue(), "DIAS TRABAJADOS");
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<Salary>( jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		// Cret@
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(s -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = s.getContextData()
							.get(CGC_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(666.00 * 11 / lastDayOfMonth, Double.parseDouble(datas.get(0).getExpression()));
					assertEquals(666.00 * 0.50 * (lastDayOfMonth -11) / lastDayOfMonth, Double.parseDouble(datas.get(1).getExpression()));

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(666.00 * 11 / lastDayOfMonth, Double.parseDouble(datas.get(0).getExpression()));
					assertEquals(666.00 * 0.50 * (lastDayOfMonth -11) / lastDayOfMonth, Double.parseDouble(datas.get(1).getExpression()));

				});
		;
		
		
	}

	@Test
	public void testReductionIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(
				aonContext,
				getFirstDayOfYear(getToday()), 
				Collections.emptyMap());
		
		Date reductionStart = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 11);
		
		addData(aonContext, 
				contract, 
				reductionStart,
				null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "'100'");
						put(ContextVariable.QUOTE_GROUP.getName(), "'01'");
						put(ContextVariable.FULL_TIME.getName(), "FALSO()");

						put(MONDAY_HOURS.getName(), format("%d", 4));
						put(TUESDAY_HOURS.getName(), format("%d", 4));
						put(WEDNESDAY_HOURS.getName(), format("%d", 4));
						put(THURSDAY_HOURS.getName(), format("%d", 4));
						put(FRIDAY_HOURS.getName(), format("%d", 4));
						put(SATURDAY_HOURS.getName(), format("%d", -1));
						put(SUNDAY_HOURS.getName(), format("%d", -1));
					}
				});
		
		addPayment(aonContext, contract, getFirstDayOfYear(getToday()), "666.00 * DIAS_TRABAJADOS / DIAS_MES");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		List<ITimedResult<Double>> workDays = ctx.getExpressionContext().eval(
				"DIAS_TRABAJADOS", startDate, endDate, Double.class);
		
		final int lastDayOfMonth = get(getLastDayOfMonth(getToday()), Calendar.DATE);
		assertEquals(11.0, workDays.get(0).getValue(), "DIAS TRABAJADOS");
		assertEquals((lastDayOfMonth -11) * 0.50, workDays.get(1).getValue(), "DIAS TRABAJADOS");
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		new ContractSalaryCalculator<Salary>( jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		// Cret@
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(s -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = s.getContextData()
							.get(CGC_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(666.00 * 11 / lastDayOfMonth, Double.parseDouble(datas.get(0).getExpression()));
					assertEquals(666.00 * 0.50 * (lastDayOfMonth -11) / lastDayOfMonth, Double.parseDouble(datas.get(1).getExpression()));

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(666.00 * 11 / lastDayOfMonth, Double.parseDouble(datas.get(0).getExpression()));
					assertEquals(666.00 * 0.50 * (lastDayOfMonth -11) / lastDayOfMonth, Double.parseDouble(datas.get(1).getExpression()));

				});
		;
		
		
	}
}
