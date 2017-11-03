package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLDelayTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.04;
	
	@Test
	public void testDelaysI() throws ExpressionException, SQLException,
			SalaryException {
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

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		ContractSalaryCalculator<Salary> delayCalculator = new ContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(100.00, delay.getTotalPayment());
		

	}

	@Test
	public void testDelaysII() throws ExpressionException, SQLException,
			SalaryException {
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

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		addPayment(aonContext, contract,  add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 5), "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		ContractSalaryCalculator<Salary> delayCalculator = new ContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(150.00, delay.getTotalPayment());
		

	}


	@Test
	public void testDelaysIII() throws ExpressionException, SQLException,
			SalaryException {
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

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Date changeDate = add(getFirstDayOfMonth(getToday()), Calendar.MONTH, 5);
		
		addPayment(aonContext, contract,  add( changeDate, Calendar.DAY_OF_MONTH, 15), "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		ContractSalaryCalculator<Salary> delayCalculator = new ContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		int monthDays = AonDateUtils.getMax(changeDate, Calendar.DAY_OF_MONTH);
		Assert.assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getTotalPayment(), DELTA);
		

	}


	@Test
	public void testDelaysExtrasI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "P";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, 
				},
				new Payment[] {
					new Payment() {
						{
							this.concept = conceptP.getId();
							this.expression = "SALARIO * DIAS_TRABAJADOS/DIAS_MES";
						}
					},
					new Payment() {
						{
							this.concept = conceptP.getId();
							this.expression = "TRACE('%f\r\n',(SALARIO * DIAS_TRABAJADOS/DIAS_MES)); 0.00";
						}
					}
				},
				new HashMap<String,String>(){
					{
						put("SALARIO", "1000.00");
					}
				});


		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				category);
		//@formatter:on
		
		

		Date startDate = getFirstDayOfMonth(contract.getStartDate());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		
		setData(aonContext
			, category
			, "SALARIO"
			, "1015.00"
		);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				contract.getStartDate(), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		ContractSalaryCalculator<Salary> delayCalculator = new ContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getTotalPayment(), DELTA);
		Assert.assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getCommonBase(), DELTA );
		

	}

	@Test
	public void testCommonDiseaseIT() throws ExpressionException, SQLException,
			SalaryException {
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
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 1.00 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				"TRACE('BASE_REGULADORA = %f\r\n', BASE_REGULADORA); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = 
				add(add(getFirstDayOfMonth(getToday()), MONTH, 1), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, "TRACE('DIAS_TRABAJADOS: %f\r\n', DIAS_TRABAJADOS); 0.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		ContractSalaryCalculator<Salary> delayCalculator = new ContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(100.00, delay.getCommonBase());
		//Assert.assertEquals(100.00, delay.getTotalPayment());
		

	}

	

}
