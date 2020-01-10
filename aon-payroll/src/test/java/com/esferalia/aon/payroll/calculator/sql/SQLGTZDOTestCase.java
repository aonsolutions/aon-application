package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.util.Calendar.DATE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLGTZDOTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.001;

	@Test
	public void testGtzdoOrderI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoOrderII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_2+P_3+P_4)" ,
						"125.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = AonDateUtils.getMax(getToday(), DATE);
		//@formatter:off
		Assert.assertEquals(
				1750.00 + ( 125.00 * workedDays / monthDays ), 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoOrderIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,10);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 1);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);

	}

	@Test
	public void testGtzdoOrderIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,10);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 2);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);

	}

	@Test
	public void testGtzdoOrderV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,10);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 3);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		

	}

	@Test
	public void testGtzdoOrderVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,10);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 15);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);

	}

	@Test
	public void testGtzdoOrderVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 20);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);

	}
	
	@Test
	public void testGtzdoOrderVIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 200);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		System.out.println("------------------------------------------------------------" );
		
		startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoOrderIX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1+P_2+P_3+P_4+P_5+P_6+P_7+P_8+P_9+P_10)" ,
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 200);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		

		startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoOrderX() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1+P_2+P_3+P_4+P_5+P_6+P_7+P_8+P_9+P_10)" ,
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format("30 * 0.75 * %s",  OCCUPATIONAL_DISEASE_DAYS), String.format("1000.00/31 * %s",  OCCUPATIONAL_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 200);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.OCCUPATIONAL_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		System.out.println("------------------------------------------------------------" );
		
		startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoOrderXI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1+P_2+P_3+P_4+P_5+P_6+P_7+P_8+P_9+P_10)" ,
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format("1000.00/%s * 1.00 * %s", MONTH_DAYS, MATERNITY_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 200);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.MATERNITY, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		System.out.println("------------------------------------------------------------" );
		

		startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		

	}

	@Test
	public void testGtzdoOrderXII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1+P_2+P_3+P_4+P_5+P_6+P_7+P_8+P_9+P_10)" ,
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						"100.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format("1000.00/%s * 1.00 * %s", MONTH_DAYS, MATERNITY_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		
		

	}

	
	@Test
	public void testGtzdoOrderXIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()), Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		Date endIt = add(startIt, Calendar.DATE, 2);
		System.out.println("endIt :" + endIt );
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		
		startIt = add(endIt, Calendar.DATE,5);
		System.out.println("startIt :" + startIt );
		endIt = add(startIt, Calendar.DATE, 1);
		System.out.println("endIt :" + endIt );

//		addIT(aonContext, 
//				contract, 
//				LeaveType.COMMON_DISEASE, 
//				startIt,
//				endIt, 
//				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		
	}

	@Test
	public void testGtzdoOrderXV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1,1,3)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()),Calendar.DATE, 10);
		Date endIt = add(startIt, Calendar.DATE, 14);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double workedDays = monthDays - 15;
		double itDays = monthDays - workedDays;

		//@formatter:off
		Assert.assertEquals(
				(1000.00 * workedDays/ monthDays)
				+ (1000.00 * 3/ monthDays)
				+ (1000.00/monthDays * 0.60 * 12), 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoOrderXVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1,21,500)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add(getFirstDayOfMonth(getToday()),Calendar.DATE, 10);
		Date endIt = add(startIt, Calendar.DATE, 5);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double workedDays = monthDays - 6;
		double itDays = monthDays - workedDays;

		//@formatter:off
		Assert.assertEquals(
				(1000.00 * workedDays/ monthDays)
				+ (1000.00/monthDays * 0.60 * 3), 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4All() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1 + P_2)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				category);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
				+ (1500.00 + 250.00 ) * itDays / monthDays, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4Partial() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1 + P_2,1,3)" ,
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
						}, category);
		//@formatter:on
		
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() + ", " + p.getQuote()) ;

		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		double br = ((1750.00 + 1750.00 * 0.10) + (1750.00 + 1750.00 * 0.10)/12 + (1750.00 + 1750.00 * 0.10)/12 )/ monthDays ;
		
		System.out.println("BR = " + br );
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
				+ (1500.00 + 250.00 ) * Math.min(3,itDays) / monthDays
				+ (br * 0.60 * Math.min(12,Math.max(0, itDays-3)))
				+ (br * 0.60 * Math.min(5,Math.max(0, itDays-15)))
				+ (br * 0.75 * Math.min(monthDays-20,Math.max(0, itDays-20)))
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4PartialII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						
					}
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00*DIAS_TRABAJADOS/DIAS_MES" ,
						"GTZDO(P_1 + P_2,4,30)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * 0.00/100" }, category);
		//@formatter:on
		
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				( 1750.00 + 1750.00*0.10 )/30.00
				);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		for(com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getName() +  " = " + payment.getAmount() + " (" + payment.getExpression() +")");
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
				+ (1500.00 + 250.00 ) * Math.max(0,Math.min(30,itDays)-3) / monthDays
				+ (1750.00 + 1750.00*0.10)/30.00 * 0.75 * Math.max(itDays-30,0) 
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo4PartialIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1,1,3)" ,
						"GTZDO(P_1 + P_2,4)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
					+ (1500.00 ) * Math.min(3,itDays) / monthDays
				+ (1500.00 + 250.00 ) * Math.max(0,itDays-3) / monthDays
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	
	@Test
	public void testGtzdo4PartialIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"GTZDO(P_1,1,3)" ,
						"GTZDO(P_1 + P_2,4,10)" ,
						"GTZDO(P_1 * 0.9,11)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = monthDays - workedDays;
		
		//@formatter:off
		Assert.assertEquals(
				(( 1500.00 + 250.00 ) * 1.10) * workedDays / monthDays 
					+ (1500.00 ) * Math.min(3,itDays) / monthDays
				+ (1500.00 + 250.00 ) * Math.min(7,Math.max(0,itDays-3)) / monthDays
				+ (1500.00 * 0.9) * Math.max(0,itDays-10) / monthDays
				, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdo30() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = getFirstDayOfMonth(getToday());
		Date endItI = AonDateUtils.add(startItI, Calendar.DATE, 10);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoMultipleI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = getFirstDayOfMonth(getToday());
		Date endItI = AonDateUtils.add(startItI, Calendar.DATE, 10);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);
		
		Date startItII = AonDateUtils.add(endItI, Calendar.DATE, 5);
		Date endItII = AonDateUtils.add(startItII, Calendar.DATE, 10);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItII,
				endItII, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}
	// ------------------------------------------------------------------------

	@Test
	public void testGtzdoMultipleII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"3000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = getFirstDayOfMonth(getToday());
		Date endItI = AonDateUtils.add(startItI, Calendar.DATE, 10);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);
		
		Date startItII = AonDateUtils.add(endItI, Calendar.DATE, 5);
		Date endItII = AonDateUtils.add(startItII, Calendar.DATE, 1);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItII,
				endItII, 
				null);


		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				3000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoMultipleIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = getFirstDayOfMonth(getToday());
		Date endItI = AonDateUtils.add(startItI, Calendar.DATE, 10);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);
		
		Date startItII = AonDateUtils.add(endItI, Calendar.DATE, 5);
		Date endItII = AonDateUtils.add(startItII, Calendar.DATE, 5);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItII,
				endItII, 
				null);

		Date startItIII = AonDateUtils.add(endItII, Calendar.DATE, 3);
		Date endItIII = AonDateUtils.add(startItIII, Calendar.DATE, 3);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItIII,
				endItIII, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoMultipleIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/GTZDO(P_0,1)/**/: REMOVE()" ,
						}
				, new String[] {}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);

		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = AonDateUtils.add(getToday(), Calendar.MONTH, -1);
		Date endItI = AonDateUtils.add(getFirstDayOfMonth(getToday()), Calendar.DATE, 10);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);
		
		Date startItII = AonDateUtils.add(endItI, Calendar.DATE, 5);
		Date endItII = null;
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItII,
				endItII, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		//ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoNoIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.expression = "0.00";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1 + P_2";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, });
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
						
				}, 
				category);
		//@formatter:on

		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		//ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdosI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"GTZDO(P_1,1,3)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"GTZDO(P_1,4)" ,
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 9);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				null, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdosII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"GTZDO(P_1,1,3)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"GTZDO(P_1,4)" ,
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 9);
		Date endItI = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 13);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdosIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("PAGAS", "12");
					put("SMI", "655.20");
					put("SALARIO_ANUAL", "SMI*14");
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"/*user*/SALARIO_ANUAL/**/ / PAGAS * DIAS_TRABAJADOS / DIAS_MES",
						"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/GTZDO(P_0,1,3)/**/:REMOVE()" ,
						"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/GTZDO(P_0,4)/**/:REMOVE()" ,
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 9);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				null, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println("" +  get(startDate, Calendar.MONTH) + " "+  p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				764.4, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		

		startDate = add(startDate,Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		System.out.println("");
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println("" +  get(startDate, Calendar.MONTH) + " "+  p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				764.4, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		

		startDate = add(startDate,Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		System.out.println("");
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println("" +  get(startDate, Calendar.MONTH) + " "+  p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				764.4, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
	}


	@Test
	public void testGtzdosMultipleI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"GTZDO(P_1,1,3)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"GTZDO(P_1,4)" ,
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format("1000.00/30 * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS), String.format("1000.00/30 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("1000.00/30 * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS), String.format("1000.00/30 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("1000.00/30 * 0.75 * %s_21",  COMMON_DISEASE_DAYS), String.format("1000.00/30 * %s_21",  COMMON_DISEASE_DAYS));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startItI = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 3);
		Date endItI = add(getFirstDayOfMonth(getToday()), Calendar.DATE, 13);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);

		Date startItII = add(endItI, Calendar.DATE, 9);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItII,
				null, 
				null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() + "," + p.getQuote() );
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoEverythingI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(TODO)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}


	@Test
	public void testGtzdoExtras() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		
		addSystemData(aonContext, AonDateUtils.getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PAGA_EXTRA_HELP", "'   2000.00 * DIAS_TRABAJADOS / DIAS_MES'  ");
			}
		});

		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "INPUT(\"/*user*/ SALARIO_BASE + GARANTIZADO/**/\",PAGA_EXTRA_HELP)";
						this.quoteExpression = "SALARIO_BASE/12";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "INPUT(\"/*user*/ SALARIO_BASE + GARANTIZADO/**/\",PAGA_EXTRA_HELP)";
						this.quoteExpression = "SALARIO_BASE/12";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				category);
		addPrestIts(aonContext, contract);

		//@formatter:on
		PaymentConceptRecord gtzdo = addConcept(aonContext, "GARANTIZADO");
		addPayment(aonContext, contract, gtzdo, "GTZDO(SALARIO_BASE)", "0.00");
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "/*user*/SALARIO_MENSUAL/**/ * DIAS_TRABAJADOS / DIAS_MES");
		addData(aonContext, category, contract.getStartDate(), contract.getEndDate(), new HashMap<String,String>(){
			{
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		// Month without ITs 
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		Assert.assertEquals(1000.00 * ( 1.00 + 1.00/12 + 1.00/12 ), salary.getCommonBase() , DELTA);
		
		// Extra without ITs
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, get(getToday(), Calendar.YEAR), getLastDayOfYear(getToday()));
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				// TODO Auto-generated method stub
				System.out.println(payment.getExpression() +" = " + amount );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			};
		}).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);

		Date startIt = add(getToday(), Calendar.MONTH, 1);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		
		// Month full IT
		startDate = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 3));
		endDate = getLastDayOfMonth(startDate);
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println("-->" + payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		Assert.assertEquals(1000.00 * ( 1.00 + 1.00/12 + 1.00/12 ), salary.getCommonBase() , DELTA);

		// Month partial IT
		startDate = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 1));
		endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, endDate, "GARANTIZADO", "0.00");
		
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder(){
			@Override
			public void addZeroPayment(Double quote, Double tax, java.util.Date startDate, java.util.Date endDate,
					IPayment payment, Map<String, ITimedVariable<?>> context) {
				super.addPayment(0.00, quote, tax, "EXTRA", startDate, endDate, payment, context);
			}
		}).calculate(ctx);
		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments())
			System.out.println("-->" + payment.getName() + " = " + payment.getAmount() + ", " + payment.getQuote()
					+ " (" + payment.getExpression() + ")");
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		Assert.assertEquals(1000.00 * ( 1.00 + 1.00/12 + 1.00/12 ), salary.getCommonBase() , DELTA);
		
		agreement = getAgreement(aonContext, category.getAgreementLevel());
		extra = getExtra(aonContext, agreement.getId(), "15/12");

		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, get(getToday(),Calendar.YEAR), getLastDayOfYear(getToday()));
		ExpressionContext expressionContext = ctx.getExpressionContext();
		// TODO: Fix this
		expressionContext.setVariable("GARANTIZADO", 0.00, ctx.getStartDate(), ctx.getEndDate());
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				// TODO Auto-generated method stub
				System.out.println(payment.getExpression() + "[" + startDate + "..."+ endDate +"]  = " + amount );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			};
		}).calculate(ctx);
		
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		
	
	}



	@Test
	public void testGtzdoExtrasII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "GARANTIZADO + SALARIO_BASE";
//						this.quoteExpression = "SALARIO_BASE/12";
						this.quoteExpression = "PRORRATEAR()";
						this.month = Month.DECEMBER;
						this.start = "01/07";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "GARANTIZADO + SALARIO_BASE ";
						this.quoteExpression = "PRORRATEAR()";
						this.month = Month.JULY;
						this.start = "01/01";
						this.end = "30/06";
						this.issue = "15/07";
					}
				}, 
				});

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				category);
		//@formatter:on
		
		addPrestIts(aonContext, contract);

		PaymentConceptRecord gtzdo = addConcept(aonContext, "GARANTIZADO");
		addPayment(aonContext, contract, gtzdo, "GTZDO(SALARIO_BASE)", "0.00");
		PaymentConceptRecord sbase = addConcept(aonContext, "SALARIO_BASE");
		addPayment(aonContext, contract, sbase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		// Month without ITs 
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		Assert.assertEquals(1000.00 * ( 1.00 + 1.00/12 + 1.00/12 ), salary.getCommonBase() , DELTA);
		
		// Extra without ITs
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, get(getToday(), Calendar.YEAR), getLastDayOfYear(getToday()));
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);

		extra = getExtra(aonContext, agreement.getId(), "15/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, get(getToday(), Calendar.YEAR), getLastDayOfYear(getToday()));
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);

		Date startIt = add(getToday(), Calendar.MONTH, 1);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		
		// Month full IT
		startDate = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 3));
		endDate = getLastDayOfMonth(startDate);
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		Assert.assertEquals(1000.00 * ( 1.00 + 1.00/12 + 1.00/12 ), salary.getCommonBase() , DELTA);

		// Month partial IT
		startDate = getFirstDayOfMonth(add(getToday(), Calendar.MONTH, 1));
		endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract, startDate, endDate, "GARANTIZADO", "0.00");
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder(){
			@Override
			public void addZeroPayment(Double quote, Double tax, java.util.Date startDate, java.util.Date endDate,
					IPayment payment, Map<String, ITimedVariable<?>> context) {
				super.addPayment(0.00, quote, tax, "EXTRA", startDate, endDate, payment, context);
			}
		}).calculate(ctx);
		for ( SalaryPayment p:  salary.getSalaryPayments() ) {
			System.out.println(p.getName() + " , "+ p.getExpression() +" = " + p.getAmount() );
			
		}
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		Assert.assertEquals(1000.00 * ( 1.00 + 1.00/12 + 1.00/12 ), salary.getCommonBase() , DELTA);
		
		extra = getExtra(aonContext, agreement.getId(), "15/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, get(getToday(), Calendar.YEAR), getLastDayOfYear(getToday()));
		ExpressionContext expressionContext = ctx.getExpressionContext();
		// TODO: Fix this
		expressionContext.setVariable("GARANTIZADO", 0.00, ctx.getStartDate(), ctx.getEndDate());
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);

		extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, get(getToday(), Calendar.YEAR), getLastDayOfYear(getToday()));
		expressionContext = ctx.getExpressionContext();
		// TODO: Fix this
		expressionContext.setVariable("GARANTIZADO", 0.00, ctx.getStartDate(), ctx.getEndDate());
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				// TODO Auto-generated method stub
				System.out.println(payment.getExpression() +" = " + amount );
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			};
		}).calculate(ctx);
		Assert.assertEquals(1000.00, salary.getTotalPayment() , DELTA);
		
	
	}

	@Test
	public void testGtzdoNotAllI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"GTZDO(P_1)" ,
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getFirstDayOfMonth(getToday());
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		double br = 1000.00 / 30.00;
		double prestIt = br * 0.75 * get(endDate, Calendar.DAY_OF_MONTH );
		//@formatter:off
		Assert.assertEquals(
				Math.max(prestIt, 500.00) , 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoAllI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"GTZDO(P_1 + P_2)" ,
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getFirstDayOfMonth(getToday());
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		double br = 1000.00 / 30.00;
		double prestIt = br * 0.75 * 30.00;
		//@formatter:off
		Assert.assertEquals(
				1000.00 , 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoConstantI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"GTZDO(800.00)" ,
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getFirstDayOfMonth(getToday());
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		double br = 1000.00 / 30.00;
		double prestIt = br * 0.75 * 30.00;
		//@formatter:off
		Assert.assertEquals(
				800.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoConstantII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = 
				newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"GTZDO(800.00)" ,
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add( getFirstDayOfMonth(getToday()), DATE, 10);
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		double br = 1000.00 / 30.00;
		double prestIt = br * 0.75 * 30.00;
		//@formatter:off
		Assert.assertEquals(
				800.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoConstantIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = 
				newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); 				// Monthly quote
					put("IMPORTE_MENSUAL", "800.00");
				}
				}
				, new String[] { 
						"GTZDO( IMPORTE_MENSUAL )" ,
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add( getFirstDayOfMonth(getToday()), DATE, 10);
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		//@formatter:off
		Assert.assertEquals(
				800.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoConstantIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = 
				newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); 				// Monthly quote
					put("IMPORTE_MENSUAL", "1000.00");
				}
				}
				, new String[] { 
						"GTZDO( IMPORTE_MENSUAL )" ,
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = add( getFirstDayOfMonth(getToday()), DATE, 10);
		Date endIt = AonDateUtils.add(startIt, Calendar.DATE, 4);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	@Ignore("Not implemented")
	public void testGtzdoArguments() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_2,P_3,P_4)" ,
						"125.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getToday();
		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
		
		double workedDays = get(startIt, DATE) -1 ;
		double monthDays = AonDateUtils.getMax(getToday(), DATE);
		double itDays = AonDateUtils.getMax(getToday(), DATE);
		//@formatter:off
		Assert.assertEquals(
				1750.00 + ( 125.00 * workedDays / monthDays ), 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}
	
	@Test
	public void testGtzdoOneDay() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"GTZDO(P_1)" ,
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
//						"BASE_CGC * 0.10", 
//						"BASE_CGP * 0.05",
//						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date itDay = getToday();
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				itDay,
				itDay, 
				null);
		

		Date startDate = getFirstDayOfMonth(itDay);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
				
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdoByPeriod() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		// @formatter:off
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap()
				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"GTZDO(P_0)" ,
						}
				, new String[] {}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIT = add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, -2);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIT,
				add(startIT, Calendar.DAY_OF_MONTH, 100), 
				null);
		
		Date startDate = startIT;
		Date endDate = add(startDate, Calendar.DAY_OF_MONTH, 2);
		
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startIT, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
				
		int monthDays = get(getLastDayOfMonth(startIT), Calendar.DAY_OF_MONTH);
		//@formatter:off
		Assert.assertEquals(
				(1000.00 / monthDays ) * 3, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testAutoGtzdoI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		addSystemData(aonContext, 
					getFirstDayOfYear(getToday()), 
					null,
					new HashMap<String,String>() {
					{
						put("BASE_CGC_MIN","756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					}
					}
		);
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		PaymentConceptRecord conceptGarantizado = addConcept(aonContext, "GARANTIZADO");
		addPayment(aonContext, contract, conceptGarantizado, "P_0 + P_1", "_P",  PaymentType.CRA_0055);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getFirstDayOfMonth(getToday());
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
	}

	@Test
	public void testAutoGtzdoII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		addSystemData(aonContext, 
					getFirstDayOfYear(getToday()), 
					null,
					new HashMap<String,String>() {
					{
						put("BASE_CGC_MIN","756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					}
					}
		);
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		PaymentConceptRecord conceptGarantizado = addConcept(aonContext, "GARANTIZADO");
		addPayment(aonContext, contract, conceptGarantizado, "1000.00", "_P",  PaymentType.CRA_0055);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getFirstDayOfMonth(getToday());
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		
		//@formatter:off
		Assert.assertEquals(
				1000.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
	}

	@Test
	public void testAutoGtzdoIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		addSystemData(aonContext, 
					getFirstDayOfYear(getToday()), 
					null,
					new HashMap<String,String>() {
					{
						put("BASE_CGC_MIN","756.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
					}
					}
		);
		

		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put("DIAS_MES", "30"); // Monthly quote
				}
				}
				, new String[] { 
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS / DIAS_MES",
//						"TRACE('BASE_REGULADORA=%f\r\n',BASE_REGULADORA)",
//						"TRACE('DIAS_IT=%d\r\n',DIAS_ENFERMEDAD_COMUN_21)"
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		PaymentConceptRecord conceptGarantizado = addConcept(aonContext, "GARANTIZADO");
		addPayment(aonContext, contract, conceptGarantizado, "100.00", "_P",  PaymentType.CRA_0055);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date startIt = getFirstDayOfMonth(getToday());
		//Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Date startDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
		//int brDays = Math.min(get(getLastDayOfMonth(startIt), Calendar.DATE), 30); // BR : Monthly
		int itDays = get(getLastDayOfMonth(startDate), Calendar.DATE); 
		//@formatter:off
		Assert.assertEquals(
				(1000.00 / 30.00 * 0.75 * itDays ) + 100.00 , //850.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
	}

	@Test
	public void testGtzdosPeriodsI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);

		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				AonDateUtils.getFirstDayOfYear(getToday()),
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"GTZDO(TODO,1,3)" ,
						"GTZDO(TODO,4,20)"
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date start = add( contract.getStartDate(), Calendar.MONTH, 3 );
		Date startIt = add(start, Calendar.DATE, 15);
		Date endIt = null;
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				endIt, 
				null);


		Date startDate = getFirstDayOfMonth(startIt);
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
//		for ( SalaryPayment p: salary.getSalaryPayments())
//			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00,
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		startDate = add(startDate, Calendar.MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00 / 30.00 * 1.00 * 5 
				+ 1000.00 / 30.00 * 0.75 * 26, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGtzdosPeriodsII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		
		Date firstDayOfYear = AonDateUtils.getFirstDayOfYear(getToday());
		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.YEAR, -1);
		
		// @formatter:off
		ContractRecord contract = newContract(aonContext,  
				contractStartDate,
				new HashMap<String,String>(){
				{
					put(MONTH_DAYS.getName(), "30");
				}
				}

				, new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"GTZDO(TODO,21,141)"
						}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPrestIts(aonContext, contract);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		Date endItI = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 4);
		Date startItI = add( endItI, Calendar.DAY_OF_MONTH, -33 );
		
		ContractLeaveRecord it = 
				addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItI,
				endItI, 
				null);

		Date endItII = null;
		Date startItII = add( endItI, Calendar.DAY_OF_MONTH, 4 );

		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startItII,
				endItII, 
				null,
				it.getId());

		Date startDate = getFirstDayOfMonth(startItII);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount());
				
		//@formatter:off
		Assert.assertEquals(
				1000.00,
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
		
	}
	// ----------------------------------------------------------------------------------


	private static void addPrestIts(AONContext aonContext, ContractRecord contract) {
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
	}

	// ----------------------------------------------------------------------------------

}
