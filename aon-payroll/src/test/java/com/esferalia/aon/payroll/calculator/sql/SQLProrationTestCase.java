package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLProrationTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.0000001;

	@Test
	public void testTemporaryCRA005I() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,Collections.emptyMap() 
				,new String[] {
						"999.99 * DIAS_TRABAJADOS / DIAS_MES",
						"99.99 * DIAS_TRABAJADOS / DIAS_MES",
						"9.99 * DIAS_TRABAJADOS / DIAS_MES",
				} 
				,new String[] {
						
				} 
				,null);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date firstDayOfJuly = new Date( calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date lastDayOfDecember = new Date( calendar.getTimeInMillis());

		addPayment(
			aonContext, 
			contract, 
			firstDayOfJuly, 
			lastDayOfDecember, 
			"BONO DICIEMBRE", 
			String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ), 
			"_P", 
			"_P/6", 
			PaymentType.CRA_0005, 
			Month.DECEMBER );
		
		Date startDate = getFirstDayOfMonth(firstDayOfJuly);
		Date endDate = getLastDayOfMonth(firstDayOfJuly);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
		startDate = getFirstDayOfMonth(lastDayOfDecember);
		endDate = getLastDayOfMonth(lastDayOfDecember);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99+1200.00,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
	}

	@Test
	public void testTemporaryCRA005II() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,Collections.emptyMap() 
				,new String[] {
						"999.99 * DIAS_TRABAJADOS / DIAS_MES",
						"99.99 * DIAS_TRABAJADOS / DIAS_MES",
						"9.99 * DIAS_TRABAJADOS / DIAS_MES",
				} 
				,new String[] {
						
				} 
				,null);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		Date firstDayOfOctober = new Date( calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date lastDayOfDecember = new Date( calendar.getTimeInMillis());

		addPayment(
			aonContext, 
			contract, 
			firstDayOfOctober, 
			lastDayOfDecember, 
			"BONO DICIEMBRE", 
			String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ), 
			"_P", 
			"_P/3", 
			PaymentType.CRA_0005, 
			Month.DECEMBER );
		
		Date startDate = getFirstDayOfMonth(firstDayOfOctober);
		Date endDate = getLastDayOfMonth(firstDayOfOctober);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/3,salary.getCommonBase(), DELTA);
		
		startDate = getFirstDayOfMonth(lastDayOfDecember);
		endDate = getLastDayOfMonth(lastDayOfDecember);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99+1200.00,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/3,salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testTemporaryCRA005III() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,Collections.emptyMap() 
				,new String[] {
						"999.99 * DIAS_TRABAJADOS / DIAS_MES",
						"99.99 * DIAS_TRABAJADOS / DIAS_MES",
						"9.99 * DIAS_TRABAJADOS / DIAS_MES"				} 
				,new String[] {
						
				} 
				,null);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date firstDayOfJuly = new Date( calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date lastDayOfDecember = new Date( calendar.getTimeInMillis());

		addPayment(
			aonContext, 
			contract, 
			contract.getStartDate(), 
			contract.getEndDate(), 
			"BONO DICIEMBRE", 
			String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ), 
			"_P", 
			"PRORRATEAR(_P,JULIO,DICIEMBRE)", 
			PaymentType.CRA_0005, 
			Month.DECEMBER );
		
		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(contractStart);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99,salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(firstDayOfJuly);
		endDate = getLastDayOfMonth(firstDayOfJuly);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
		startDate = getFirstDayOfMonth(lastDayOfDecember);
		endDate = getLastDayOfMonth(lastDayOfDecember);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99+1200.00,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testTemporaryCRA005IV() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,Collections.emptyMap() 
				,new String[] {
						"999.99 * DIAS_TRABAJADOS / DIAS_MES",
						"99.99 * DIAS_TRABAJADOS / DIAS_MES",
						"9.99 * DIAS_TRABAJADOS / DIAS_MES"				} 
				,new String[] {
						
				} 
				,null);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date firstDayOfJuly = new Date( calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date lastDayOfDecember = new Date( calendar.getTimeInMillis());

		addPayment(
			aonContext, 
			contract, 
			contract.getStartDate(), 
			contract.getEndDate(), 
			"BONO DICIEMBRE", 
			String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ), 
			"_P", 
			"PRORRATEAR(JULIO,DICIEMBRE)", 
			PaymentType.CRA_0005, 
			Month.DECEMBER );
		
		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(contractStart);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99,salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(firstDayOfJuly);
		endDate = getLastDayOfMonth(firstDayOfJuly);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
		startDate = getFirstDayOfMonth(lastDayOfDecember);
		endDate = getLastDayOfMonth(lastDayOfDecember);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99+1200.00,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
	}
	
	@Test
	public void testTemporaryCRA005V() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,Collections.emptyMap() 
				,new String[] {
						"999.99 * DIAS_TRABAJADOS / DIAS_MES",
						"99.99 * DIAS_TRABAJADOS / DIAS_MES",
						"9.99 * DIAS_TRABAJADOS / DIAS_MES"				} 
				,new String[] {
						
				} 
				,null);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date firstDayOfJuly = new Date( calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date lastDayOfDecember = new Date( calendar.getTimeInMillis());

		addPayment(
			aonContext, 
			contract, 
			contract.getStartDate(), 
			contract.getEndDate(), 
			"BONO DICIEMBRE PRORRATEADO", 
			String.format("PRORRATEAR(1200.00 * %s / %s, JULIO,DICIEMBRE)", WORKED_DAYS , MONTH_DAYS ), 
			"_P", 
			"_P", 
			PaymentType.CRA_0005);
		
		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(contractStart);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99,salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(firstDayOfJuly);
		endDate = getLastDayOfMonth(firstDayOfJuly);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
		startDate = getFirstDayOfMonth(lastDayOfDecember);
		endDate = getLastDayOfMonth(lastDayOfDecember);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
	}

	@Test
	@Disabled
	public void testTemporaryCRA004III() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		// @formatter:on
		
		Date contractStart = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(
				aonContext
				,contractStart
				,Collections.emptyMap() 
				,new String[] {
						"999.99 * DIAS_TRABAJADOS / DIAS_MES",
						"99.99 * DIAS_TRABAJADOS / DIAS_MES",
						"9.99 * DIAS_TRABAJADOS / DIAS_MES"				} 
				,new String[] {
						
				} 
				,null);
		//@formatter:off
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date firstDayOfJuly = new Date( calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date lastDayOfDecember = new Date( calendar.getTimeInMillis());

		addPayment(
			aonContext, 
			contract, 
			contract.getStartDate(), 
			contract.getEndDate(), 
			"PAGA DICIEMBRE", 
			String.format("1200.00 * %s / %s", WORKED_DAYS , MONTH_DAYS ), 
			"_P", 
			"PRORRATEAR(_P,JULIO,DICIEMBRE)", 
			PaymentType.CRA_0004, 
			Month.DECEMBER );
		
		Date startDate = getFirstDayOfMonth(contractStart);
		Date endDate = getLastDayOfMonth(contractStart);
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99,salary.getCommonBase(), DELTA);

		startDate = getFirstDayOfMonth(firstDayOfJuly);
		endDate = getLastDayOfMonth(firstDayOfJuly);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
		startDate = getFirstDayOfMonth(lastDayOfDecember);
		endDate = getLastDayOfMonth(lastDayOfDecember);
		
		salary = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))
		;
		
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() +" = " + p.getAmount() ));
		
		assertEquals(999.99+99.99+9.99+1200.00,salary.getTotalPayment(), DELTA);
		assertEquals(999.99+99.99+9.99 + 1200.00/6,salary.getCommonBase(), DELTA);
		
	}
	
	
}
