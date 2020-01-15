package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARENTEED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;

import junit.framework.Assert;

public class SQLDelayTestCase extends AbstractSQLTestCase {

	
	private static class Sucessfull extends RuntimeException {
		
	}
	
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
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(100.00, delay.getTotalPayment());
		Assert.assertEquals(100.00, delay.getCommonBase());
		Assert.assertEquals(100.00, delay.getRawCommonBase());
		Assert.assertEquals(100.00, delay.getProfessionalBase());
		Assert.assertEquals(100.00, delay.getIrpfBase());
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
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(150.00, delay.getTotalPayment());
		Assert.assertEquals(150.00, delay.getCommonBase());
		Assert.assertEquals(150.00, delay.getRawCommonBase());
		Assert.assertEquals(150.00, delay.getProfessionalBase());
		Assert.assertEquals(150.00, delay.getIrpfBase());
		

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
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		int monthDays = AonDateUtils.getMax(changeDate, Calendar.DAY_OF_MONTH);
		Assert.assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getIrpfBase(), DELTA);
		Assert.assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getTotalPayment(), DELTA);
		Assert.assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getCommonBase(), DELTA);
		

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
						this.start = "01/01";
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
							this.expression = "TRACE('SALARIO:%f\r\n',(SALARIO * DIAS_TRABAJADOS/DIAS_MES)); 0.00";
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
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getTotalPayment(), DELTA);
		Assert.assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getCommonBase(), DELTA );
		Assert.assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getIrpfBase(), DELTA);
		

	}

	@Test
	public void testMaternityITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, MATERNITY.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("0.00 * %s",  MATERNITY_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = 
				add(add(getFirstDayOfYear(getToday()), MONTH, 6), DAY_OF_MONTH,9);
		addIT(aonContext, contract, LeaveType.PREGNANCY_RISK, startITDate, null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 11 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		PaymentConceptRecord bono = addConcept(aonContext, "BONO", PaymentType.CRA_0005);
		addPayment(aonContext, contract, contract.getStartDate(), null, bono, "BONO BENEFICIOS", "3000.00", "_P", "_P", PaymentType.CRA_0005, (byte) Month.DECEMBER.getValue());
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfYear(getToday()),
				add(startDate, DAY_OF_MONTH, -1),
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		
		SalaryBuilder salaryBuilder = new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.println( startDate + " [" +payment.getName() + "] " + payment.getDescription() + ": " + amount +", " + quote);
			}
		};
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>> compositeSalaryBuilder = 
				new CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>>(jooqSalaryBuilder, salaryBuilder);
		
		delayCalculator.setSalaryBuilder(compositeSalaryBuilder);
		Salary salary = delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		org.junit.Assert.assertEquals(0.00  , salary.getIrpfBase(), DELTA);
		org.junit.Assert.assertEquals(0.00  , salary.getTotalPayment(), DELTA);
		org.junit.Assert.assertEquals( 3000.00 /12 * 11  , salary.getCommonBase(), DELTA);

//		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
//				connection, startDate, endDate, endDate, contract);
//		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
//		
//		for (SalaryPayment p : salary.getSalaryPayments()) {
//			System.out.println( p.getDescription() + ": " + p.getAmount() );
//		}
//		
//		org.junit.Assert.assertEquals(3000.00/12 * 6.3 , salary.getTotalPayment(), DELTA);
		
	}

	@Test
	public void testMaternityITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, MATERNITY.getName());
		addPayment(aonContext, contract, prestIT, 
				String.format("0.00 * %s",  MATERNITY_DAYS),
				String.format("DIAS_COTIZADOS * COEFICIENTE_MATERNIDAD * BASE_REGULADORA")
				);
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = 
				add(add(getFirstDayOfYear(getToday()), MONTH, 6), DAY_OF_MONTH,9);
		addIT(aonContext, contract, LeaveType.MATERNITY, startITDate, null, null);
		addData(aonContext, contract, startITDate, null, ContextVariable.MATERNITY_FACTOR.getName(), "0.5");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 11 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		PaymentConceptRecord bono = addConcept(aonContext, "BONO", PaymentType.CRA_0005);
		addPayment(aonContext, contract, contract.getStartDate(), null, bono, "BONO BENEFICIOS", "3000.00", "_P", "_P", PaymentType.CRA_0005, (byte) Month.DECEMBER.getValue());
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
//				getFirstDayOfMonth(add(startITDate, Calendar.MONTH, 2)),
//				getLastDayOfMonth(add(startITDate, Calendar.MONTH, 2)),
				getFirstDayOfYear(getToday()),
				add(startDate, DAY_OF_MONTH, -1),
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		
		SalaryBuilder salaryBuilder = new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.println( startDate + " [" +payment.getName() + "] " + payment.getDescription() + ": " + amount +", " + quote);
			}
		};
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>> compositeSalaryBuilder = 
				new CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>>(jooqSalaryBuilder, salaryBuilder);
		
		delayCalculator.setSalaryBuilder(compositeSalaryBuilder);
		Salary salary = delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		org.junit.Assert.assertEquals( 3000.00 /12 * 11  , salary.getCommonBase(), DELTA);
		org.junit.Assert.assertEquals(0.00  , salary.getIrpfBase(), DELTA);
		org.junit.Assert.assertEquals(0.00  , salary.getTotalPayment(), DELTA);

//		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
//				connection, startDate, endDate, endDate, contract);
//		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
//		
//		for (SalaryPayment p : salary.getSalaryPayments()) {
//			System.out.println( p.getDescription() + ": " + p.getAmount() );
//		}
//		
//		org.junit.Assert.assertEquals(250.00 * 6.3 + 125.00 * 5.7  + 1750 * 0.5, salary.getTotalPayment(), DELTA);
//		
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
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startITDate = 
				add(add(getFirstDayOfMonth(getToday()), MONTH, 1), DAY_OF_MONTH,5);
		Date endITDate = 
				add(startITDate, DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		//addPayment(aonContext, contract, "TRACE('DIAS_TRABAJADOS: %f\r\n', DIAS_TRABAJADOS); 0.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		
		SalaryBuilder salaryBuilder = new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				//System.out.println( startDate + " [" +payment.getName() + "] " + payment.getDescription() + ": " + amount +", " + quote);
			}
		};
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>> compositeSalaryBuilder = 
				new CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>>(jooqSalaryBuilder, salaryBuilder);
		
		
		delayCalculator.setSalaryBuilder(compositeSalaryBuilder);
		Salary salary = delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		int monthDays = AonDateUtils.getMax(startITDate, Calendar.DAY_OF_MONTH);
		Assert.assertEquals(90.00 + ( 10/30.00*(monthDays-11) ) + ( 10/30.00 * 8 * 0.60 ) , salary.getIrpfBase(), DELTA);
		Assert.assertEquals(90.00 + ( 10/30.00*(monthDays-11) ) + ( 10/30.00 * 8 * 0.60 ) , salary.getTotalPayment(), DELTA);

			
		
		
		try {
			AON.getSalaryData(aonContext, props -> 
					props.getContractProperty().eq(contract.getId())
					.and(props.getIsDelayProperty().eq(true)))
			.forEach( delay -> 
				{
					Double cgcBase = delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.summingDouble(expression-> Double.parseDouble(expression)));
					Assert.assertEquals(100.00, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					Assert.assertEquals(13, cgcBases.size());
					
					Date startCreta = getFirstDayOfMonth(getToday());
					Date endCreta = getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(0).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(0).getEndDate());
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(1).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH,-1), cgcBases.get(1).getEndDate());
					Assert.assertEquals(startITDate, cgcBases.get(2).getStartDate());
					Assert.assertEquals(endITDate, cgcBases.get(3).getEndDate());
					Assert.assertEquals(add(endITDate,DAY_OF_MONTH,1), cgcBases.get(4).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(4).getEndDate());
					
					
					
					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			
			return;
		} 
		
		Assert.fail("No delay!!!!!!!!!!!!!!!!!");

	}

	@Test
	public void testCommonDiseaseITII() throws ExpressionException, SQLException,
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
		
//		addPayment(aonContext, contract, 
//				"TRACE('BASE_REGULADORA * QUOTE_DAYS= %f\r\n', (1 * DIAS_COTIZADOS)); 0.00",
//				String.format("0.00",  QUOTE_DAYS)
//				);
		//@formatter:on

		Date startITDate = 
				add(add(getFirstDayOfMonth(getToday()), MONTH, 1), DAY_OF_MONTH,6);
		Date endITDate = 
				add(startITDate, DAY_OF_MONTH,40);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		//addPayment(aonContext, contract, "TRACE('DIAS_TRABAJADOS: %f\r\n', DIAS_TRABAJADOS); 0.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<ISalary> delayCalculator = new SmartContractSalaryCalculator<ISalary>();
		
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		
		delayCalculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		try {
			AON.getSalaryData(aonContext, props -> 
					props.getContractProperty().eq(contract.getId())
					.and(props.getIsDelayProperty().eq(true)))
			.forEach( delay -> 
				{
					Double cgcBase = delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.summingDouble(expression-> Double.parseDouble(expression)));
					Assert.assertEquals(100.00, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					// 10, 1-3, 4-15, 16-20, 21 
					Assert.assertEquals(14, cgcBases.size());
					
					Date startCreta = getFirstDayOfMonth(getToday());
					Date endCreta = getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(0).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(0).getEndDate());
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(1).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH,-1), cgcBases.get(1).getEndDate());
					// 06,07,08 09-23, 24-
					Assert.assertEquals(startITDate, cgcBases.get(2).getStartDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,2), cgcBases.get(2).getEndDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,3), cgcBases.get(3).getStartDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,14), cgcBases.get(3).getEndDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,15), cgcBases.get(4).getStartDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,19), cgcBases.get(4).getEndDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,20), cgcBases.get(5).getStartDate());
					Assert.assertEquals(getLastDayOfMonth(startITDate), cgcBases.get(5).getEndDate());
					
					
					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			return;
		} 
		
		Assert.fail("No delay!!!!!!!!!!!!!!!!!");

	}
	
	@Test
	public void testCommonDiseaseITIV() throws ExpressionException, SQLException,
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
				}, 
				null);
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
		
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		Date startITDate = 
				add(endDate, DAY_OF_MONTH,-5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		SalaryBuilder delayBuilder = new SalaryBuilder();
		
		delayCalculator.setSalaryBuilder(delayBuilder);
		Salary delay = delayCalculator.calculate(delayCtx);
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		double adjust = + ((monthDays-30) * 10.00/30.00 );
		Assert.assertEquals(10.00 + adjust,delay.getIrpfBase(), DELTA);
		Assert.assertEquals(10.00 + adjust,delay.getTotalPayment(), DELTA);
		Assert.assertEquals(10.00,delay.getCommonBase());
	}

	@Test
	public void testCommonDiseaseITV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1),
				new String[] {
				"250.00" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				null);
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
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		Date startITDate = 
				add(endDate, DAY_OF_MONTH,-5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		SalaryBuilder delayBuilder = new SalaryBuilder();
		
		delayCalculator.setSalaryBuilder(delayBuilder);
		Salary delay = delayCalculator.calculate(delayCtx);
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		double adjust = + ((monthDays-30) * 10.00/30.00 );
		Assert.assertEquals(10.00 + adjust ,delay.getIrpfBase(), DELTA);
		Assert.assertEquals(10.00 + adjust,delay.getTotalPayment(), DELTA);
		Assert.assertEquals(10.00,delay.getCommonBase(), DELTA);
	}

	@Test
	public void testCommonDiseaseITVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				null);
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

		PaymentConceptRecord guarenteed = addConcept(aonContext, GUARENTEED);
		addPayment(aonContext, contract, guarenteed, 
				"250.00",
				"0.00"
				);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		Date startITDate = 
				add(endDate, DAY_OF_MONTH,-5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		SalaryBuilder delayBuilder = new SalaryBuilder();
		
		delayCalculator.setSalaryBuilder(delayBuilder);
		Salary delay = delayCalculator.calculate(delayCtx);
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		double adjust = + ((monthDays-30) * 10.00/30.00 );
		Assert.assertEquals(10.00 + adjust ,delay.getIrpfBase(), DELTA);
		Assert.assertEquals(10.00 + adjust,delay.getTotalPayment(), DELTA);
		Assert.assertEquals(10.00,delay.getCommonBase(), DELTA);
	}

	@Test
	public void testCommonDiseaseITVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				null);
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

		PaymentConceptRecord guarenteed = addConcept(aonContext, GUARENTEED, PaymentType.CRA_0055);
		addPayment(aonContext, contract, guarenteed, 
				"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/250.00/**/: HIDE()",
				"TRACE('DIAS_ENFERMEDAD_COMUN:%d\r\n', DIAS_ENFERMEDAD_COMUN);0.00"
				, PaymentType.CRA_0055);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		Date startITDate = 
				add(endDate, DAY_OF_MONTH,-5);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		SalaryBuilder delayBuilder = new SalaryBuilder();
		
		delayCalculator.setSalaryBuilder(delayBuilder);
		Salary delay = delayCalculator.calculate(delayCtx);
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		double adjust = + ((monthDays-30) * 10.00/30.00 );
		Assert.assertEquals(10.00 + adjust ,delay.getIrpfBase(), DELTA);
		Assert.assertEquals(10.00 + adjust ,delay.getTotalPayment(), DELTA);
		Assert.assertEquals(10.00,delay.getCommonBase(), DELTA);
	}


	@Test
	public void testCommonDiseaseITAndGtzdo() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"S_0 * DIAS_TRABAJADOS / DIAS_MES" ,
				"S_1 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "S_0", "250.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "S_1", "1500.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		
		PaymentConceptRecord gtzdo = addConcept(aonContext, GUARENTEED);
		addPayment(aonContext, contract, gtzdo, 
				"GTZDO(P_0 + P_1);",
				"0.00"
				);
//		addPayment(aonContext, contract, 
//				"TRACE('BASE_REGULADORA * QUOTE_DAYS= %f\r\n', (1 * DIAS_COTIZADOS)); 0.00",
//				String.format("0.00",  QUOTE_DAYS)
//				);
		//@formatter:on

		Date startITDate = 
				add(add(getFirstDayOfMonth(getToday()), MONTH, 1), DAY_OF_MONTH,6);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		AON.getSalaries(aonContext, props -> 
					props.getContractProperty().eq(contract.getId()))
		.forEach(s -> {
			
			Assert.assertEquals(1750.00, s.getTotalPayment() );
			
			System.out.println("CGC_BASE :" + s.getCommonContingenciesBase() );
			System.out.println("IRPF_BASE :" + s.getIrpfBase() );
			System.out.println("TOTAL_PAYMENT :" + s.getTotalPayment() );
		});
		;
		
		setData(aonContext, contract, "S_0", "250.00 + 66.00");
		setData(aonContext, contract, "S_1", "1500.00 + 166.00");
		//addPayment(aonContext, contract, "TRACE('DIAS_TRABAJADOS: %f\r\n', DIAS_TRABAJADOS); 0.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<ISalary> delayCalculator = new SmartContractSalaryCalculator<ISalary>();
		
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		
		delayCalculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		
		try {
			AON.getSalaries(aonContext, props -> 
				props.getContractProperty().eq(contract.getId())
				.and(props.getIsDelayProperty().eq(true)))
				.forEach( delay -> {
					System.out.println("DELAY CGC_BASE :" + delay.getCommonContingenciesBase() );
					System.out.println("DELAY IRPF_BASE :" + delay.getIrpfBase() );
					System.out.println("DELAY TOTAL_PAYMENT :" + delay.getTotalPayment() );
					
					Assert.assertEquals((166.00+66.00) * 10.00, delay.getCommonContingenciesBase() );
					Assert.assertEquals((166.00+66.00) * 10.00, delay.getIrpfBase() );
					Assert.assertEquals((166.00+66.00) * 10.00, delay.getTotalPayment() );

					
				}
			);
			
			AON.getSalaryData(aonContext, props -> 
					props.getContractProperty().eq(contract.getId())
					.and(props.getIsDelayProperty().eq(true)))
			.forEach( delay -> 
				{
					
					System.out.println(delay.getTotalPayment());
					
					System.out.println(delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.joining(",") ));
					
					Double cgcBase = delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.summingDouble(expression-> Double.parseDouble(expression)));
					Assert.assertEquals((166.00+66.00)*10, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					// 10, 1-3, 4-15, 16-20, 21 
					Assert.assertEquals(14, cgcBases.size());
					
					Date startCreta = getFirstDayOfMonth(getToday());
					Date endCreta = getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(0).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(0).getEndDate());
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(1).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH,-1), cgcBases.get(1).getEndDate());
					// 06,07,08 09-23, 24-
					Assert.assertEquals(startITDate, cgcBases.get(2).getStartDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,2), cgcBases.get(2).getEndDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,3), cgcBases.get(3).getStartDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,14), cgcBases.get(3).getEndDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,15), cgcBases.get(4).getStartDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,19), cgcBases.get(4).getEndDate());
					Assert.assertEquals(add(startITDate,DAY_OF_MONTH,20), cgcBases.get(5).getStartDate());
					Assert.assertEquals(getLastDayOfMonth(startITDate), cgcBases.get(5).getEndDate());
					
					
					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			return;
		} 
		
		Assert.fail("No delay!!!!!!!!!!!!!!!!!");

	}

	@Test
	public void testCommonDiseaseITAndGtzdoConstant() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"S_0 * DIAS_TRABAJADOS / DIAS_MES" ,
				"S_1 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "S_0", "250.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "S_1", "1500.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_16_20",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		
		PaymentConceptRecord gtzdo = addConcept(aonContext, GUARENTEED);
		addPayment(aonContext, contract, gtzdo, 
				"99.99",
				"0.00"
				);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);


		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		startDate = add(startDate, MONTH,1);
		endDate = getLastDayOfMonth(startDate);
		Date startITDate = add(startDate, DAY_OF_MONTH,6);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);


		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = new SmartContractSalaryCalculator<ISalary>();
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + " = " + amount +", " + quote + "(" + startDate +"..." + endDate + "");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		addData(aonContext, contract, startDate, null, "S_0", "250.00 + 66.00");
		addData(aonContext, contract, startDate, null, "S_1", "1500.00 + 166.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<ISalary> delayCalculator = new SmartContractSalaryCalculator<ISalary>();
		
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				if ( startDate.compareTo(startITDate) >= 0 ) {
					//System.out.println(description + " = " + amount +", " + quote + "(" + startDate +"..." + endDate + "");
					Assert.assertEquals(0.00, amount);
					Assert.assertEquals(0.00, quote);
					Assert.assertEquals(0.00, tax);
					
				}
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		delayCalculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		
		try {
			AON.getSalaries(aonContext, props -> 
				props.getContractProperty().eq(contract.getId())
				.and(props.getIsDelayProperty().eq(true)))
				.forEach( delay -> {
					System.out.println("DELAY CGC_BASE :" + delay.getCommonContingenciesBase() );
					System.out.println("DELAY IRPF_BASE :" + delay.getIrpfBase() );
					System.out.println("DELAY TOTAL_PAYMENT :" + delay.getTotalPayment() );
					
					Assert.assertEquals((166.00+66.00) * 6 / 30, delay.getCommonContingenciesBase() );
					Assert.assertEquals((166.00+66.00) * 6 / 30, delay.getIrpfBase() );
					Assert.assertEquals((166.00+66.00) * 6 / 30, delay.getTotalPayment() );
					
				}
			);
			
			AON.getSalaryData(aonContext, props -> 
					props.getContractProperty().eq(contract.getId())
					.and(props.getIsDelayProperty().eq(true)))
			.forEach( delay -> 
				{
					
					System.out.println(delay.getTotalPayment());
					
					System.out.println(delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.joining(",") ));
					
					Double cgcBase = delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.summingDouble(expression-> Double.parseDouble(expression)));
					Assert.assertEquals((166.00+66.00) * 6 / 30, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					Assert.assertEquals(5, cgcBases.size());
					
					Date startCreta = add(getFirstDayOfMonth(getToday()), MONTH,1);
					Date endCreta = add(startITDate, DAY_OF_MONTH,-1); //getLastDayOfMonth(startCreta);
					
					Assert.assertEquals(startCreta, cgcBases.get(0).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(0).getEndDate());
					Assert.assertEquals((166.00+66.00) * 6 / 30, Double.parseDouble(cgcBases.get(0).getExpression()) );
					
					startCreta = startITDate;
					endCreta = add(startCreta, DAY_OF_MONTH,2);
					Assert.assertEquals(startCreta, cgcBases.get(1).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(1).getEndDate());
					Assert.assertEquals(0.00, Double.parseDouble(cgcBases.get(1).getExpression()) );

					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = add(startCreta, DAY_OF_MONTH,11);
					Assert.assertEquals(startCreta, cgcBases.get(2).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(2).getEndDate());
					Assert.assertEquals(0.00, Double.parseDouble(cgcBases.get(2).getExpression()) );
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = add(startCreta, DAY_OF_MONTH,4);
					Assert.assertEquals(startCreta, cgcBases.get(3).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(3).getEndDate());
					Assert.assertEquals(0.00, Double.parseDouble(cgcBases.get(3).getExpression()) );

					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					Assert.assertEquals(startCreta, cgcBases.get(4).getStartDate());
					Assert.assertEquals(endCreta, cgcBases.get(4).getEndDate());
					Assert.assertEquals(0.00, Double.parseDouble(cgcBases.get(4).getExpression()) );

					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			return;
		} 
		
		Assert.fail("No delay!!!!!!!!!!!!!!!!!");

	}

	@Test
	public void testNoDelaysAndRoundI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");
		PaymentConceptRecord conceptA = addConcept(aonContext, "A");

		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "P + A";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "P + A";
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
							this.concept = conceptA.getId();
							this.expression = "P * 0.04";
						}
					}
				},
				new HashMap<String,String>(){
					{
						put("SALARIO", "2318.09");
					}
				});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {
				}, category);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			calculator.setSalaryBuilder(new RoundSalaryBuilder<Salary>( jooqSalaryBuilder, d -> Math.round(d*1000.00)/1000.00));
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		AON.getSalaries(aonContext, props -> props.getContractProperty().eq(contract.getId()).and(props.getIsSalaryProperty().eq(true)))
		.forEach( salary  -> {
			Assert.assertEquals(2318.09 + 2318.09 *0.04, salary.getTotalPayment(), 0.001);
			Assert.assertEquals(2318.09 + 2318.09 *0.04, salary.getIrpfBase(), 0.001);
			Assert.assertEquals(2318.09 + 2318.09 *0.04 + (2318.09 + 2318.09 *0.04)/6 , salary.getCommonContingenciesBase(), 0.001);
		});
		;
		
		//addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		delayCalculator.setSalaryBuilder(new RoundSalaryBuilder<Salary>( jooqSalaryBuilder, d -> Math.round(d*1000.00)/1000.00));
		delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		
		AON.getSalaries(aonContext, props -> props.getContractProperty().eq(contract.getId()).and(props.getIsDelayProperty().eq(true)))
		.forEach( delay  -> {
			Assert.assertEquals(0.00, delay.getTotalPayment());
			Assert.assertEquals(0.00, delay.getTotalLiquid());
			Assert.assertEquals(0.00, delay.getIrpfBase());
			Assert.assertEquals(0.00, delay.getCommonContingenciesBase());
		});
		;
		
	}
	
	
	@Test
	public void testDelaysAfterSettle() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				startDate,
				Collections.emptyMap(),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		addPayment(aonContext, contract, contract.getStartDate(), null, "INDEMNIZACION", "66.00", "_P", "_P", PaymentType.CRA_0054, SalaryType.SETTLE);

		ISQLContractSalaryCalculatorContext ctx = 
				getSmartSQLContractSettleContext(connection, contract.getStartDate(), add(startDate, DAY_OF_MONTH,-1), contract);
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		new SmartContractSalaryCalculator<ISalary>( jooqSalaryBuilder ).calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(100.00, delay.getTotalPayment());
		Assert.assertEquals(100.00, delay.getCommonBase());
		Assert.assertEquals(100.00, delay.getRawCommonBase());
		Assert.assertEquals(100.00, delay.getProfessionalBase());
		Assert.assertEquals(100.00, delay.getIrpfBase());
	}
	
	
	@Test
	public void testDelaysOnCalculateAgreementI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"A_CUENTA_CONVENIO(8.00 * DIAS_TRABAJADOS / DIAS_MES)",
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(20.00, delay.getTotalPayment());
		Assert.assertEquals(20.00, delay.getCommonBase());
		Assert.assertEquals(20.00, delay.getRawCommonBase());
		Assert.assertEquals(20.00, delay.getProfessionalBase());
		Assert.assertEquals(20.00, delay.getIrpfBase());
	}
	
	
	@Test
	public void testDelaysOnCalculateAgreementII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"A_CUENTA_CONVENIO(10.00 * DIAS_TRABAJADOS / DIAS_MES)",
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(0.00, delay.getTotalPayment());
		Assert.assertEquals(0.00, delay.getCommonBase());
		Assert.assertEquals(0.00, delay.getRawCommonBase());
		Assert.assertEquals(0.00, delay.getProfessionalBase());
		Assert.assertEquals(0.00, delay.getIrpfBase());
	}
	
	@Test
	@Ignore("Not yet implemented")
	public void testDelaysOnCalculateAgreementIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"A_CUENTA_CONVENIO(15.00 * DIAS_TRABAJADOS / DIAS_MES)",
				}, 
				new String[] {
				}, null);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
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
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(0.00, delay.getTotalPayment());
		Assert.assertEquals(0.00, delay.getCommonBase());
		Assert.assertEquals(0.00, delay.getRawCommonBase());
		Assert.assertEquals(0.00, delay.getProfessionalBase());
		Assert.assertEquals(0.00, delay.getIrpfBase());
	}

	@Test
	public void testDelaysAtDirectPay() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord directPay =addConcept(aonContext, "PAGO_DIRECTO");
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				}, 
				new String[] {
				}, null);
		//@formatter:on
		
		addData(aonContext, 
				contract, 
				contract.getStartDate(), 
				null, 
				"IMPORTE", 
				"1000.00");
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				null, 
				directPay, 
				"PAGO DIRECTO", 
				"IMPORTE * DIAS_TRABAJADOS/DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 10 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		setData(aonContext, 
				contract, 
				"IMPORTE", 
				"1010.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		Assert.assertEquals(100.00, delay.getTotalPayment());
		Assert.assertEquals(100.00, delay.getCommonBase());
		Assert.assertEquals(100.00, delay.getRawCommonBase());
		Assert.assertEquals(100.00, delay.getProfessionalBase());
		Assert.assertEquals(100.00, delay.getIrpfBase());
	}

	@Test
	public void testDelaysWithBonusPeriodI() throws ExpressionException, SQLException,
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
		
		Date bonusStartDate = add(startDate, Calendar.DAY_OF_MONTH, 10);
		BonusConceptRecord bonusConcept = addBonusConcept(aonContext, BonusType.SOCIAL_SECURITY, "66.66");
		addBonus(aonContext, contract, bonusStartDate, bonusConcept);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {
					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(bonusStartDate, datas.get(1).getStartDate());
				});
		;
		
		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
//		
//		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
//					+ " (" + payment.getExpression() + ")");
//		}
//		
		Assert.assertEquals(10.00, delay.getCommonBase());
		Assert.assertEquals(10.00, delay.getRawCommonBase());
		Assert.assertEquals(10.00, delay.getTotalPayment());
		Assert.assertEquals(10.00, delay.getProfessionalBase());
		Assert.assertEquals(10.00, delay.getIrpfBase());
	}

	protected ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			Date endDate, ContractRecord contract) throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SmartSQLContractSettleCalculatorContext(connection, contractStart,
				endDate, endDate, criteria);
		ctx.next();
		return ctx;
	}


}
