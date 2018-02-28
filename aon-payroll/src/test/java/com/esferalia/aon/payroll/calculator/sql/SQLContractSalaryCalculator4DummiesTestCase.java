/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator4Dummies;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */
public class SQLContractSalaryCalculator4DummiesTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.001;


	@Test
	public void test4FixConstantFirstMonthI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, -10),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"1000.00"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		Assert.assertEquals(1000.00 * 11 / 30, salary.getTotalPayment());

	}

	@Test
	public void test4FixConstantPaymentFirstMonthI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, -10),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"BRUTO(1000.00)"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator4Dummies<Salary> calculator = 
				new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")" );
		}

		Assert.assertEquals(1000.00 * 11 / 30, salary.getTotalPayment());

	}

	@Test
	public void test4FixConstantPaymentFirstMonthII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, -10),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				},
				
				new String[] { 
						"BRUTO(1000.00) * DIAS_TRABAJADOS /DIAS_MES"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator4Dummies<Salary> calculator = 
				new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")" );
		}

		Assert.assertEquals(1000.00 * 11 / 30, salary.getTotalPayment());

	}

	@Test
	public void test4FixConstantPaymentITI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"BRUTO(1000.00)"
						},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,9), null, 0.00);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		Assert.assertEquals(1000.00 * 9 / 30, salary.getTotalPayment());

	}

	@Test
	public void test4FixConstantLiquidITI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"NETO(1000.00)"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,9), 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,19), 
				0.00);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(1000.00 * (get(endDate, Calendar.DAY_OF_MONTH) -11) / 30.00 , salary.getTotalLiquid(), DELTA);
		
		Set<com.esferalia.aon.payroll.SalaryPayment> payments = salary.getSalaryPayments();
		Assert.assertEquals(2, payments.size());
		
		

	}

	@Test
	public void test4FixConstantLiquidFirstMonth()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, -10),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"NETO(1000.00)"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator4Dummies<Salary> calculator = 
				new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);		

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")" );
		}

		Assert.assertEquals(1000.00 * 11 / 30, salary.getTotalLiquid(), DELTA);

	}

	@Test
	public void test4FixConstantITI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"1000.00"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,9), null, 0.00);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		ISalary salary = calculator.calculate(ctx);

		Assert.assertEquals(1000.00 * 9 / 30, salary.getTotalPayment());

	}


	@Test
	public void test4FixConstantITII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"1000.00"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,9), 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,19), 
				0.00);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(1000.00 * (get(endDate, Calendar.DAY_OF_MONTH) -11) / 30 , salary.getTotalPayment(), DELTA);
		
		Set<com.esferalia.aon.payroll.SalaryPayment> payments = salary.getSalaryPayments();
		Assert.assertEquals(2, payments.size());
		
		

	}


	@Test
	public void test4FixConstantITIII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30");
					}
				},
				
				new String[] { 
						"1000.00"},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100",
				},
				null
		);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,9), 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,14), 
				0.00);

		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,19), 
				add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH,23), 
				0.00);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator4Dummies<Salary>();
		calculator.setSalaryBuilder(new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(1000.00 * (get(endDate, Calendar.DAY_OF_MONTH) -11) / 30 , salary.getTotalPayment(), DELTA);
		
		Set<com.esferalia.aon.payroll.SalaryPayment> payments = salary.getSalaryPayments();
		Assert.assertEquals(3, payments.size());
		
		
		// 10-11-12-13-14-15 , 20-21-22-23-24
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")" );
		}
		

	}
}
