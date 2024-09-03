package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DROP_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.GUARENTEED;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

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
		
		assertEquals(100.00, delay.getTotalPayment());
		assertEquals(100.00, delay.getCommonBase());
		assertEquals(100.00, delay.getRawCommonBase());
		assertEquals(100.00, delay.getProfessionalBase());
		assertEquals(100.00, delay.getIrpfBase());
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
		
		assertEquals(150.00, delay.getTotalPayment());
		assertEquals(150.00, delay.getCommonBase());
		assertEquals(150.00, delay.getRawCommonBase());
		assertEquals(150.00, delay.getProfessionalBase());
		assertEquals(150.00, delay.getIrpfBase());
		

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
		assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getIrpfBase(), DELTA);
		assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getTotalPayment(), DELTA);
		assertEquals(140.00 + ( 10.00 * (monthDays - 15 ) / monthDays  ), delay.getCommonBase(), DELTA);
		

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
			RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder, round(2));
			calculator.setSalaryBuilder(roundSalaryBuilder);
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
				criteria) {
			@Override
			protected <T extends ISalary> ISalaryBuilder<T> getSalaryBuilder(ISalaryBuilder<T> salaryBuilder) {
				return new RoundSalaryBuilder<T>(salaryBuilder, round(2));
			}
		};
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getTotalPayment(), DELTA);
		assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getCommonBase(), DELTA );
		assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getIrpfBase(), DELTA);
		

	}

	@Test
	public void testDelaysExtrasII() throws ExpressionException, SQLException,
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
				new Extra() {
					{
						this.expression = "P";
						this.month = Month.MARCH;
						this.start = "01/01 -1";
						this.end = "31/12 -1";
						this.issue = "31/03";
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
//					new Payment() {
//						{
//							this.concept = conceptP.getId();
//							this.expression = "TRACE('SALARIO:%f\r\n',(SALARIO * DIAS_TRABAJADOS/DIAS_MES)); 0.00";
//						}
//					}
				},
				new HashMap<String,String>(){
					{
						put("DIAS_MES", "30.00");
						put("SALARIO", "1000.00");
					}
				});


		//@formatter:off
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfYear(getToday()),
				new String[] {}, 
				new String[] {}, 
				category);
		//@formatter:on
		
		

		Date startDate = getFirstDayOfMonth(contract.getStartDate());
		Date endDate = getLastDayOfMonth(startDate);
		startDate = add(startDate,MONTH, 2);
		endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		
		setData(aonContext
			, category
			, "SALARIO"
			, "1012.00"
		);
		
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
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		assertEquals(12 + 3, delay.getTotalPayment(), DELTA);
		assertEquals(12 + 3, delay.getCommonBase(), DELTA );
		assertEquals(12 + 3, delay.getIrpfBase(), DELTA);
		

	}

	@Test
	public void testDelaysExtrasZero() throws ExpressionException, SQLException,
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
			RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder, round(2));
			calculator.setSalaryBuilder(roundSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
				
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				contract.getStartDate(), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria) {
		    @Override
		    protected <T extends ISalary> ISalaryBuilder<T> getSalaryBuilder(
		            ISalaryBuilder<T> salaryBuilder) {
		        return new RoundSalaryBuilder<T>(salaryBuilder, round(2));
		    }
		};
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new RoundSalaryBuilder<Salary>(new SalaryBuilder(), round(2)));
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		assertEquals(0.00, delay.getTotalPayment(), DELTA);
		assertEquals(0.00, delay.getCommonBase(), DELTA );
		assertEquals(0.00, delay.getIrpfBase(), DELTA);
		

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
		
		assertEquals(0.00  , salary.getIrpfBase(), DELTA);
		assertEquals(0.00  , salary.getTotalPayment(), DELTA);
		assertEquals( 3000.00 /12 * 11  , salary.getCommonBase(), DELTA);

//		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
//				connection, startDate, endDate, endDate, contract);
//		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
//		
//		for (SalaryPayment p : salary.getSalaryPayments()) {
//			System.out.println( p.getDescription() + ": " + p.getAmount() );
//		}
//		
//		assertEquals(3000.00/12 * 6.3 , salary.getTotalPayment(), DELTA);
		
	}

	@Test
	public void testERTEI() throws ExpressionException, SQLException,
	SalaryException {
		testERTE(ContextVariable.ERE, ContextVariable.ERE_DAYS, ContextVariable.ERE_FACTOR);
	}

	@Test
	public void testERTEForceI() throws ExpressionException, SQLException,
	SalaryException {
		testERTE(ContextVariable.ERE_FORCE, ContextVariable.ERE_DAYS_FORCE, ContextVariable.ERE_FACTOR_FORCE);
	}

	@Test
	public void testERTEForceOffI() throws ExpressionException, SQLException,
	SalaryException {
		testERTE(ContextVariable.ERE_FORCE_OFF, ContextVariable.ERE_DAYS_FORCE_OFF, ContextVariable.ERE_FACTOR_FORCE_OFF);
	}

	@Test
	public void testDropDaysI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);
		cleanSystemData(aonContext);

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
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		
		addData(aonContext, contract, firstDayOfMonth, firstDayOfMonth, ContextVariable.DROP_FACTOR, "1.0");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), MONTH_DAYS, "30.00");
//		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), CGC_BASE_MIN, "35.00");
		
		addSystemData(aonContext, contract.getStartDate(), null, new  HashMap<String, String>(){
			{
				put(CGC_BASE_MIN.getName(), "35.00 * DIAS_NOMINA");
			}
		});
		
		//@formatter:off
		addPayment(aonContext, 
				contract, 
				String.format("/*read_only*/%s * 0.00/**/",  DROP_DAYS),
				String.format("%s",  CGC_BASE_MIN)
				);
		
		//@formatter:on

		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<>();
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate,
				endDate,
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
		
		delayCalculator.setSalaryBuilder(salaryBuilder);
		Salary salary = delayCalculator.calculate(delayCtx);
		
		assertEquals(0.00  , salary.getIrpfBase(), DELTA);
		assertEquals(0.00  , salary.getTotalPayment(), DELTA);
		assertEquals( 0.00 , salary.getCommonBase(), DELTA);

	}


	private void testERTE(ContextVariable ere, ContextVariable ereDays, ContextVariable ereFactor) throws ExpressionException, SQLException,
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
		PaymentConceptRecord ereConcept = addConcept(aonContext, ere.getName());
		addPayment(aonContext, contract, ereConcept, "0.00" , String.format("%s * BASE_REGULADORA",ereDays.getName()));
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Date startEREDate = 
				add(add(startDate, MONTH, 2), DAY_OF_MONTH,9);
		
		addData(aonContext, contract, startEREDate, null,
				new HashMap<String, String>() {
					{
						put(ereFactor.getName(), "1.0");
					}
				});


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
		addPayment(aonContext, contract, "100.00" );
		
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
		
		// 2021-07-10
		//
		assertEquals(100.00 * 11  , salary.getCommonBase(), DELTA);
		assertEquals(100.00 * 2 + 100.00 * 9 / 30.00 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * 2 + 100.00 * 9 / 30.00 , salary.getTotalPayment(), DELTA);

	}

	@Test
	public void testERTEII() throws ExpressionException, SQLException,
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
		PaymentConceptRecord ere = addConcept(aonContext, ContextVariable.ERE.getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",ContextVariable.ERE_DAYS.getName()));
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startEREDate = 
				add(add(getFirstDayOfYear(getToday()), MONTH, 6), DAY_OF_MONTH,9);
		addData(aonContext, contract, startEREDate, null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.ERE_FACTOR.getName(), "1.0");
					}
				});

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
		
		assertEquals(0.00  , salary.getIrpfBase(), DELTA);
		assertEquals(0.00  , salary.getTotalPayment(), DELTA);
		assertEquals( 3000.00 /12 * 11  , salary.getCommonBase(), DELTA);

	}
	
	@Test
	public void testERTEIII() throws ExpressionException, SQLException,
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
		PaymentConceptRecord ere = addConcept(aonContext, ContextVariable.ERE.getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",ContextVariable.ERE_DAYS.getName()));
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Date startEREDate = 
				add(add(startDate, MONTH, 1), DAY_OF_MONTH,9);
		
		addData(aonContext, contract, startEREDate, null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.ERE_FACTOR.getName(), "0.5");
					}
				});


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
		addPayment(aonContext, contract, "100.00 * DIAS_TRABAJADOS / DIAS_MES" );
		
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
		
		// 2021-07-10
		//
		assertEquals(100.00 * 11 , salary.getCommonBase(), DELTA);
		assertEquals(100.00 * 1 + 100.00 * 9 / 30.00 + 50.00 * 21/30 + 50.00 * 9, salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * 1 + 100.00 * 9 / 30.00 + 50.00 * 21/30 + 50.00 * 9 , salary.getTotalPayment(), DELTA);

	}
	

	@Test
	public void testERTEIV() throws ExpressionException, SQLException,
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
		PaymentConceptRecord ere = addConcept(aonContext, ContextVariable.ERE.getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",ContextVariable.ERE_DAYS.getName()));
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date startDate = add(firstDayOfYear, MONTH, 8 ); //getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Date startEREDate = 
				add(add(startDate, MONTH, 1), DAY_OF_MONTH,9);
		
		Date startEREDateII = add(startEREDate, MONTH, 3 );
		
		addData(aonContext, contract, startEREDate, 
				add(startEREDateII, Calendar.DAY_OF_MONTH, -1),
				new HashMap<String, String>() {
					{
						put(ContextVariable.ERE_FACTOR.getName(), "1.0");
					}
				});

		addData(aonContext, contract, startEREDateII, null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.ERE_FACTOR.getName(), "0.5");
					}
				});

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
		addPayment(aonContext, contract, "100.00 * DIAS_TRABAJADOS / DIAS_MES" );
		
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
		
		// 2021-07-10
		//
		assertEquals(100.00 * 11 , salary.getCommonBase(), DELTA);

		// Remember adjust monthly last ERTE ( partial )  period 
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		int erteDays_50 = monthDays - 9;
		
		assertEquals(100.00 * 1 + 100.00 * 9 / 30.00 + 50 * erteDays_50 / 30.00 + 50.00 * 6, salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * 1 + 100.00 * 9 / 30.00 + 50 * erteDays_50 / 30.00 + 50.00 * 6 , salary.getTotalPayment(), DELTA);

	}
	

	@Test
	@Disabled
	public void testERTEV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>(){
					{
						put("DIAS_MES", "30.00");
						put("PLUS_MENSUAL", "250.00");
						put("SALARIO_MENSUAL", "1500.00");
					}
				},
				new String[] {
				"SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES",
				"PLUS_MENSUAL * DIAS_TRABAJADOS / DIAS_MES" ,
				"1200.00/12.00"
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
		PaymentConceptRecord ere = addConcept(aonContext, ContextVariable.ERE_FORCE.getName());
		addPayment(aonContext, contract, ere, "0.00" , String.format("%s * BASE_REGULADORA",ContextVariable.ERE_DAYS_FORCE.getName()));
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on

		Date startEREDate = 
				add(add(getFirstDayOfYear(getToday()), MONTH, 6), DAY_OF_MONTH,9);
		addData(aonContext, contract, startEREDate, null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.ERE_FACTOR_FORCE.getName(), "0.5");
					}
				});

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 12 ; i++ ) {
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
		
		setData(aonContext, contract, "PLUS_MENSUAL", "275.00");
		setData(aonContext, contract, "SALARIO_MENSUAL", "1550.00");
		
		
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
		
		assertEquals( 75*12  , salary.getCommonBase(), DELTA);
		assertEquals(75.00*6 + 75.00/2*5 + 75.00/30*9 + 75.00/2/30*21 , salary.getIrpfBase(), DELTA);
		assertEquals(75.00*6 + 75.00/2*5 + 75.00/30*9 + 75.00/2/30*21 , salary.getTotalPayment(), DELTA);

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

		Date startDate = getFirstDayOfYear(getToday());
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
		
		assertEquals( 3000.00 /12 * 11  , salary.getCommonBase(), DELTA);
		assertEquals(0.00  , salary.getIrpfBase(), DELTA);
		assertEquals(0.00  , salary.getTotalPayment(), DELTA);

//		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
//				connection, startDate, endDate, endDate, contract);
//		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
//		
//		for (SalaryPayment p : salary.getSalaryPayments()) {
//			System.out.println( p.getDescription() + ": " + p.getAmount() );
//		}
//		
//		assertEquals(250.00 * 6.3 + 125.00 * 5.7  + 1750 * 0.5, salary.getTotalPayment(), DELTA);
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
		assertEquals(90.00 + ( 10/30.00*(monthDays-11) ) + ( 10/30.00 * 8 * 0.60 ) , salary.getIrpfBase(), DELTA);
		assertEquals(90.00 + ( 10/30.00*(monthDays-11) ) + ( 10/30.00 * 8 * 0.60 ) , salary.getTotalPayment(), DELTA);

			
		
		
		try {
			AON.getSalaryData(aonContext, props -> 
					props.getContractProperty().eq(contract.getId())
					.and(props.getIsDelayProperty().eq(true)))
			.forEach( delay -> 
				{
					Double cgcBase = delay.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.summingDouble(expression-> Double.parseDouble(expression)));
					assertEquals(100.00, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					assertEquals(13, cgcBases.size());
					
					Date startCreta = getFirstDayOfMonth(getToday());
					Date endCreta = getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(0).getStartDate());
					assertEquals(endCreta, cgcBases.get(0).getEndDate());
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(1).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH,-1), cgcBases.get(1).getEndDate());
					assertEquals(startITDate, cgcBases.get(2).getStartDate());
					assertEquals(endITDate, cgcBases.get(3).getEndDate());
					assertEquals(add(endITDate,DAY_OF_MONTH,1), cgcBases.get(4).getStartDate());
					assertEquals(endCreta, cgcBases.get(4).getEndDate());
					
					
					
					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			
			return;
		} 
		
		fail("No delay!!!!!!!!!!!!!!!!!");

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
					assertEquals(100.00, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					// 10, 1-3, 4-15, 16-20, 21 
					assertEquals(14, cgcBases.size());
					
					Date startCreta = getFirstDayOfMonth(getToday());
					Date endCreta = getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(0).getStartDate());
					assertEquals(endCreta, cgcBases.get(0).getEndDate());
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(1).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH,-1), cgcBases.get(1).getEndDate());
					// 06,07,08 09-23, 24-
					assertEquals(startITDate, cgcBases.get(2).getStartDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,2), cgcBases.get(2).getEndDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,3), cgcBases.get(3).getStartDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,14), cgcBases.get(3).getEndDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,15), cgcBases.get(4).getStartDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,19), cgcBases.get(4).getEndDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,20), cgcBases.get(5).getStartDate());
					assertEquals(getLastDayOfMonth(startITDate), cgcBases.get(5).getEndDate());
					
					
					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			return;
		} 
		
		fail("No delay!!!!!!!!!!!!!!!!!");

	}
	
	@Test
	public void testCommonDiseaseITIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()), Calendar.YEAR,-1),
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

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, -1));
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
		
		AON.getSalaryData(aonContext, p -> p.getContractProperty().eq(contract.getId()))
		.flatMap(s ->s.getContextData().entrySet().stream()).forEach( e -> { 
			System.out.println(e.getKey() + " = " + e.getValue().stream().map( v -> v.getExpression() ).collect(Collectors.joining(","))); 
			})
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
		SalaryBuilder delayBuilder = new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println("**" + payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
				+ " (" + payment.getExpression() + ")" + startDate + ".." + endDate);
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		
		delayCalculator.setSalaryBuilder(delayBuilder);
		Salary delay = delayCalculator.calculate(delayCtx);

		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		double adjust = + ((monthDays-30) * 10.00/30.00 );
		assertEquals(10.00,delay.getCommonBase());
		assertEquals(10.00 + adjust,delay.getTotalPayment(), DELTA);
		assertEquals(10.00 + adjust,delay.getIrpfBase(), DELTA);
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

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, -1));
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
		assertEquals(10.00 + adjust ,delay.getIrpfBase(), DELTA);
		assertEquals(10.00 + adjust,delay.getTotalPayment(), DELTA);
		assertEquals(10.00,delay.getCommonBase(), DELTA);
	}

	@Test
	public void testCommonDiseaseITVI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()), Calendar.YEAR,-1),
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

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, -1));

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
		assertEquals(10.00 + adjust ,delay.getIrpfBase(), DELTA);
		assertEquals(10.00 + adjust,delay.getTotalPayment(), DELTA);
		assertEquals(10.00,delay.getCommonBase(), DELTA);
	}

	@Test
	public void testCommonDiseaseITVII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				add(getFirstDayOfYear(getToday()), Calendar.YEAR,-1),
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

		Date startDate = getFirstDayOfMonth(add(getToday(), MONTH, -1));
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
		assertEquals(10.00 + adjust ,delay.getIrpfBase(), DELTA);
		assertEquals(10.00 + adjust ,delay.getTotalPayment(), DELTA);
		assertEquals(10.00,delay.getCommonBase(), DELTA);
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
			
			assertEquals(1750.00, s.getTotalPayment() );
			
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
					
					assertEquals((166.00+66.00) * 10.00, delay.getCommonContingenciesBase() );
					assertEquals((166.00+66.00) * 10.00, delay.getIrpfBase() );
					assertEquals((166.00+66.00) * 10.00, delay.getTotalPayment() );

					
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
					assertEquals((166.00+66.00)*10, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					// 10, 1-3, 4-15, 16-20, 21 
					assertEquals(14, cgcBases.size());
					
					Date startCreta = getFirstDayOfMonth(getToday());
					Date endCreta = getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(0).getStartDate());
					assertEquals(endCreta, cgcBases.get(0).getEndDate());
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(1).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH,-1), cgcBases.get(1).getEndDate());
					// 06,07,08 09-23, 24-
					assertEquals(startITDate, cgcBases.get(2).getStartDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,2), cgcBases.get(2).getEndDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,3), cgcBases.get(3).getStartDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,14), cgcBases.get(3).getEndDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,15), cgcBases.get(4).getStartDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,19), cgcBases.get(4).getEndDate());
					assertEquals(add(startITDate,DAY_OF_MONTH,20), cgcBases.get(5).getStartDate());
					assertEquals(getLastDayOfMonth(startITDate), cgcBases.get(5).getEndDate());
					
					
					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			return;
		} 
		
		fail("No delay!!!!!!!!!!!!!!!!!");

	}

	@Test
	public void testCommonDiseaseITAndGtzdos() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSalaries(aonContext);

		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptGtzdo = addConcept(aonContext, GUARENTEED, PaymentType.CRA_0055);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					/*
					new Extra() {
						{
							this.start = "01/07";
							this.end = "31/12";
							this.issue = "21/12";
							this.month = Month.DECEMBER;
							this.expression = "SALARIO_BASE";
							this.concept = conceptPagaExtra.getId();
						}
					}, 
					new Extra() {
						{
							this.start = "01/01";
							this.end = "30/06";
							this.issue = "30/06";
							this.month = Month.JUNE;
							this.expression = "SALARIO_BASE";
							this.concept = conceptPagaExtra.getId();
						}
					}*/
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptGtzdo.getId();
								this.expression = "isdef DIAS_ENFERMEDAD_COMUN_1_3 ? DIAS_ENFERMEDAD_COMUN_1_3 * /*user*/BASE_REGULADORA*0.60/**/: HIDE()";
							}
						},
						new Payment() {
							{
								this.concept = conceptGtzdo.getId();
								this.expression = "isdef DIAS_ENFERMEDAD_COMUN_4_15 ? DIAS_ENFERMEDAD_COMUN_4_15 * /*user*/BASE_REGULADORA*0.15/**/: HIDE()";
							}
						},
						new Payment() {
							{
								this.concept = conceptGtzdo.getId();
								this.expression = "isdef DIAS_ENFERMEDAD_COMUN_16_20 ? DIAS_ENFERMEDAD_COMUN_16_20 * /*user*/BASE_REGULADORA*0.15/**/: HIDE()";
							}
						},
						new Payment() {
							{
								this.concept = conceptGtzdo.getId();
								this.expression = "isdef DIAS_ENFERMEDAD_COMUN_21 ? DIAS_ENFERMEDAD_COMUN_21 * /*user*/BASE_REGULADORA*0.25/**/: HIDE()";
							}
						},
				});
		

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				}, 
				new String[] {
				}, 
				category);
		//@formatter:on
		
		contract.setSeniorityDate(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -28));
		contract.update();
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "TC2", "\"100\"");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "GRUPO_COTIZACION", "\"03\"");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1900.00");
		
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
		
		//@formatter:on

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date startDate = add(firstDayOfYear, MONTH, 8 ); //getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Date startITDate = add(startDate, DAY_OF_MONTH,8);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<>();
		
		SalaryBuilder salaryBuilder = new SalaryBuilder();
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<>(connection);
		CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>> compositeSalaryBuilder = 
				new CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>>(salaryBuilder, jooqSalaryBuilder);
		
		calculator.setSalaryBuilder(compositeSalaryBuilder);
		Salary salary = calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1930.00");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		
		SmartContractSalaryCalculator<Salary> delayCalculator = 
				new SmartContractSalaryCalculator<Salary>(new SalaryBuilder() {
					@Override
					public void addPayment(Double amount, Double quote, Double tax, String description,
							java.util.Date startDate, java.util.Date endDate, IPayment payment,
							Map<String, ITimedVariable<?>> context) {
						super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
						if ( amount ==  0 )
							return;
						
						System.out.printf("\t[%1$td-%2$td] %3$s : %4$f \r\n", startDate, endDate, description, amount);

						int dayOfMonth = get(startDate, Calendar.DAY_OF_MONTH);
						if ( dayOfMonth == 1 )
							assertEquals( 8.00, (double) amount, DELTA);
						else if ( dayOfMonth == 9 )
							assertEquals( ( 3 * 0.60 ), (double) amount,  DELTA);
						else if ( dayOfMonth == 12 )
							assertEquals( ( 12 * 0.15  + 12 * 0.60 ), (double) amount,  DELTA);
						else if ( dayOfMonth == 22 )
							assertEquals( ( 5 * 0.15 + 5 * 0.60 ), (double) amount,  DELTA);
						else if ( dayOfMonth == 29 )
							assertEquals( ( 2 * 0.25 + 2 * 0.75 ), (double) amount,  DELTA);
							
					}
				});
		
		Salary delay = delayCalculator.calculate(delayCtx);
		delay.getSalaryPayments().forEach(p -> System.out.println( p.getDescription() + " = " + p.getAmount() +", " + p.getQuote()));
		
		assertEquals(30.00, delay.getCommonBase(), DELTA);
		assertEquals(
		 8 							// 01 - 07 
		 + 3 * 0.60 				// 08 - 10
		 + 12 * 0.15  + 12 * 0.60	// 11 - 22 
		 + 5 * 0.15 + 5 * 0.60 		// 23 - 27 
		 + 2 * 0.25 + 2 * 0.75		// 28 - 31 
		, delay.getTotalPayment(), DELTA);
		
		
	}

	@Test
	@Disabled
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
					assertEquals(0.00, amount);
					assertEquals(0.00, quote);
					assertEquals(0.00, tax);
					
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
					
					assertEquals((166.00+66.00) * 6 / 30, delay.getCommonContingenciesBase() );
					assertEquals((166.00+66.00) * 6 / 30, delay.getIrpfBase() );
					assertEquals((166.00+66.00) * 6 / 30, delay.getTotalPayment() );
					
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
					assertEquals((166.00+66.00) * 6 / 30, cgcBase, DELTA);
					
					List<ContextData> cgcBases = delay.getContextData().get(ContextVariable.CGC_BASE.getName());
					assertEquals(5, cgcBases.size());
					
					Date startCreta = add(getFirstDayOfMonth(getToday()), MONTH,1);
					Date endCreta = add(startITDate, DAY_OF_MONTH,-1); //getLastDayOfMonth(startCreta);
					
					assertEquals(startCreta, cgcBases.get(0).getStartDate());
					assertEquals(endCreta, cgcBases.get(0).getEndDate());
					assertEquals((166.00+66.00) * 6 / 30, Double.parseDouble(cgcBases.get(0).getExpression()) );
					
					startCreta = startITDate;
					endCreta = add(startCreta, DAY_OF_MONTH,2);
					assertEquals(startCreta, cgcBases.get(1).getStartDate());
					assertEquals(endCreta, cgcBases.get(1).getEndDate());
					assertEquals(0.00, Double.parseDouble(cgcBases.get(1).getExpression()) );

					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = add(startCreta, DAY_OF_MONTH,11);
					assertEquals(startCreta, cgcBases.get(2).getStartDate());
					assertEquals(endCreta, cgcBases.get(2).getEndDate());
					assertEquals(0.00, Double.parseDouble(cgcBases.get(2).getExpression()) );
					
					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = add(startCreta, DAY_OF_MONTH,4);
					assertEquals(startCreta, cgcBases.get(3).getStartDate());
					assertEquals(endCreta, cgcBases.get(3).getEndDate());
					assertEquals(0.00, Double.parseDouble(cgcBases.get(3).getExpression()) );

					startCreta = add(endCreta, DAY_OF_MONTH, 1);
					endCreta = getLastDayOfMonth(startCreta);
					assertEquals(startCreta, cgcBases.get(4).getStartDate());
					assertEquals(endCreta, cgcBases.get(4).getEndDate());
					assertEquals(0.00, Double.parseDouble(cgcBases.get(4).getExpression()) );

					throw new Sucessfull();
				}
			);
		} catch ( Sucessfull sucessfull ) {
			return;
		} 
		
		fail("No delay!!!!!!!!!!!!!!!!!");

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
			calculator.setSalaryBuilder(new RoundSalaryBuilder<Salary>( jooqSalaryBuilder, d -> d.setScale(2, RoundingMode.HALF_UP)));
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		AON.getSalaries(aonContext, props -> props.getContractProperty().eq(contract.getId()).and(props.getIsSalaryProperty().eq(true)))
		.forEach( salary  -> {
			assertEquals(2318.09 + 2318.09 *0.04, salary.getTotalPayment(), 0.01);
			assertEquals(2318.09 + 2318.09 *0.04, salary.getIrpfBase(), 0.01);
			assertEquals(2318.09 + 2318.09 *0.04 + (2318.09 + 2318.09 *0.04)/6 , salary.getCommonContingenciesBase(), 0.01);
		});
		;
		
		//addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria) {
		    @Override
		    protected <T extends ISalary> ISalaryBuilder<T> getSalaryBuilder(
		            ISalaryBuilder<T> salaryBuilder) {
		        return new RoundSalaryBuilder<T>(salaryBuilder, round(2));
		    }
		    
		};
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		delayCalculator.setSalaryBuilder(new RoundSalaryBuilder<Salary>( jooqSalaryBuilder, d -> d.setScale(2, RoundingMode.HALF_UP)));
		delayCalculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		
		AON.getSalaries(aonContext, props -> props.getContractProperty().eq(contract.getId()).and(props.getIsDelayProperty().eq(true)))
		.forEach( delay  -> {
			assertEquals(0.00, delay.getTotalPayment());
			assertEquals(0.00, delay.getTotalLiquid());
			assertEquals(0.00, delay.getIrpfBase());
			assertEquals(0.00, delay.getCommonContingenciesBase());
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
		
		assertEquals(100.00, delay.getTotalPayment());
		assertEquals(100.00, delay.getCommonBase());
		assertEquals(100.00, delay.getRawCommonBase());
		assertEquals(100.00, delay.getProfessionalBase());
		assertEquals(100.00, delay.getIrpfBase());
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
		
		assertEquals(20.00, delay.getTotalPayment());
		assertEquals(20.00, delay.getCommonBase());
		assertEquals(20.00, delay.getRawCommonBase());
		assertEquals(20.00, delay.getProfessionalBase());
		assertEquals(20.00, delay.getIrpfBase());
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
		
		assertEquals(0.00, delay.getTotalPayment());
		assertEquals(0.00, delay.getCommonBase());
		assertEquals(0.00, delay.getRawCommonBase());
		assertEquals(0.00, delay.getProfessionalBase());
		assertEquals(0.00, delay.getIrpfBase());
	}
	
	@Test
	@Disabled("Not yet implemented")
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
		
		assertEquals(0.00, delay.getTotalPayment());
		assertEquals(0.00, delay.getCommonBase());
		assertEquals(0.00, delay.getRawCommonBase());
		assertEquals(0.00, delay.getProfessionalBase());
		assertEquals(0.00, delay.getIrpfBase());
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
					+ " (" + payment.getExpression() + ")" + payment.getQuote() );
		}
		
		assertEquals(100.00, delay.getTotalPayment());
		assertEquals(100.00, delay.getCommonBase());
		assertEquals(100.00, delay.getRawCommonBase());
		assertEquals(100.00, delay.getProfessionalBase());
		assertEquals(100.00, delay.getIrpfBase());
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
					assertEquals(2, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(bonusStartDate, datas.get(1).getStartDate());
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
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		assertEquals(10.00, delay.getCommonBase());
		assertEquals(10.00, delay.getRawCommonBase());
		assertEquals(10.00, delay.getTotalPayment());
		assertEquals(10.00, delay.getProfessionalBase());
		assertEquals(10.00, delay.getIrpfBase());
	}
	
	@Test
	public void testDelaysWithConstantsAndPeriodsI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String[] {
				}, 
				null);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Date endPeriod = add(startDate, Calendar.DAY_OF_MONTH, 8);
		addData(aonContext, contract, contract.getStartDate(), endPeriod, ContextVariable.PARTIAL_FACTOR, "1.0");
		
		Date startPeriod = add(endPeriod, Calendar.DAY_OF_MONTH, 1);
		addData(aonContext, contract, startPeriod, null, ContextVariable.PARTIAL_FACTOR, "0.5");

		for ( int i = 0 ; i < 1 ; i++ ) {
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
		
		addPayment(aonContext, contract, "10.00");
		
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
					+ " (" + payment.getExpression() + ")" );
		}
		
		assertEquals(10.00, delay.getTotalPayment());
		assertEquals(10.00, delay.getCommonBase());
		assertEquals(10.00, delay.getRawCommonBase());
		assertEquals(10.00, delay.getProfessionalBase());
		assertEquals(10.00, delay.getIrpfBase());
	}
	
	@Test
	public void testERTEWithConstant() throws ExpressionException, SQLException,
	SalaryException {
		testERTEWithConstant(ContextVariable.ERE_FORCE_OFF, ContextVariable.ERE_DAYS_FORCE_OFF, ContextVariable.ERE_FACTOR_FORCE_OFF);
	}
	
	private void testERTEWithConstant(ContextVariable ere, ContextVariable ereDays, ContextVariable ereFactor) throws ExpressionException, SQLException,
	SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00"
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
		PaymentConceptRecord ereConcept = addConcept(aonContext, ere.getName());
		addPayment(aonContext, contract, ereConcept, "0.00" , String.format("%s * BASE_REGULADORA",ereDays.getName()));
		
		addPayment(aonContext, contract, 
				"TRACE('BASE_REGULADORA = %f\r\n', (BASE_REGULADORA )); 0.00",
				String.format("0.00",  QUOTE_DAYS)
				);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startEREDate = 
				add(add(startDate, MONTH, 2), DAY_OF_MONTH,9);
		
		addData(aonContext, contract, startEREDate, null,
				new HashMap<String, String>() {
					{
						put(ereFactor.getName(), "1.0");
					}
				});
		
		
		startDate = getFirstDayOfMonth(startEREDate);
		endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 1 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					// TODO Auto-generated method stub
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					System.out.println(description + ": " + amount + "," + quote + " (" + startDate +".." + endDate +")");
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		addPayment(aonContext, contract, "100.00" );
		
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
		
		// 2021-07-10
		//
		assertEquals(100.00   , salary.getCommonBase(), DELTA);
		assertEquals(100.00 * 9 / 30.00 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * 9 / 30.00 , salary.getTotalPayment(), DELTA);

	}


	@Test
	public void testITWithConstant() throws ExpressionException, SQLException,
	SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"250.00" ,
				"SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1500.00");
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startITDate = startDate;
				//add(add(startDate, MONTH, 2), DAY_OF_MONTH,9);
		Date endITDate = 
				add(startITDate, DAY_OF_MONTH,1);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);
		
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 1 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					// TODO Auto-generated method stub
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					System.out.println(description + ": " + amount + "," + quote + " (" + startDate +".." + endDate +")");
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		setData(aonContext, contract, "SALARIO_MENSUAL", "1600.00");
		
		
		
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
		
		assertEquals(100.00   , salary.getCommonBase(), DELTA);
		// Remember adjust monthly last IT period 
		int monthDays = AonDateUtils.getMax(endITDate, Calendar.DAY_OF_MONTH);
		int workedDays = monthDays - 2;
		assertEquals(100.00 * workedDays / 30.00 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * workedDays / 30.00 , salary.getTotalPayment(), DELTA);

	}

	@Test
	public void testITWithCRA0002Constant() throws ExpressionException, SQLException,
	SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1500.00");
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS EXTRAORDINARIAS", 
				"250.00", 
				"_P", 
				"_P", 
				PaymentType.CRA_0002, 
				SalaryType.SALARY);
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startITDate = startDate;
				//add(add(startDate, MONTH, 2), DAY_OF_MONTH,9);
		Date endITDate = 
				add(startITDate, DAY_OF_MONTH,1);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);
		
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 1 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					// TODO Auto-generated method stub
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					System.out.println(description + ": " + amount + "," + quote + " (" + startDate +".." + endDate +")");
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		setData(aonContext, contract, "SALARIO_MENSUAL", "1600.00");
		
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
		
		assertEquals(100.00   , salary.getCommonBase(), DELTA);
		// Remember adjust monthly last IT period 
		int monthDays = AonDateUtils.getMax(endITDate, Calendar.DAY_OF_MONTH);
		int workedDays = monthDays - 2;
		assertEquals(100.00 * workedDays / 30.00 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * workedDays / 30.00 , salary.getTotalPayment(), DELTA);

	}


	@Test
	public void testITWithCRA0055Constant() throws ExpressionException, SQLException,
	SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1500.00");
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"MEJORAS PREST.SS.ENFERMEDAD COMÚN", 
				"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/33.00/**/: HIDE()", 
				"_P", 
				"_P", 
				PaymentType.CRA_0055, 
				SalaryType.SALARY);
		
		//@formatter:off
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startITDate = //startDate;
				add(startDate, DAY_OF_MONTH,2);
		Date endITDate = 
				add(startITDate, DAY_OF_MONTH,1);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);
		
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 1 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					// TODO Auto-generated method stub
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					System.out.println(description + ": " + amount + "," + quote + " (" + startDate +".." + endDate +")");
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		setData(aonContext, contract, "SALARIO_MENSUAL", "1600.00");
		
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
		
		// 2021-07-10
		//
		assertEquals(100.00   , salary.getCommonBase(), DELTA);
		// Remember adjust monthly last IT period 
		int monthDays = AonDateUtils.getMax(endITDate, Calendar.DAY_OF_MONTH);
		int activeDays = monthDays - 2;
		assertEquals(100.00 * activeDays / 30.00 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * activeDays / 30.00 , salary.getTotalPayment(), DELTA);

	}

	@Test
	public void testITWithCRA0055ConstantII() throws ExpressionException, SQLException,
	SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1500.00");
		
		PaymentConceptRecord garantizado = addConcept(aonContext, GUARENTEED);
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				garantizado,
				"MEJORAS PREST.SS.ENFERMEDAD COMÚN", 
				"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/108.00/**/: HIDE()", 
				"_P", 
				"_P", 
				PaymentType.CRA_0055);
		
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
				String.format("BASE_REGULADORA * 0.60 * %s_16_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startITDate = add(startDate, DAY_OF_MONTH, 24);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);
		
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 1 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					// TODO Auto-generated method stub
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					System.out.println(description + ": " + amount + "," + quote + " (" + startDate +".." + endDate +")");
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		setData(aonContext, contract, "SALARIO_MENSUAL", "1600.00");
		
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
		
		assertEquals(100.00   , salary.getCommonBase(), DELTA);
		// Not monthly adjust for payment ... so 
		int monthDays = AonDateUtils.getMax(startITDate, Calendar.DAY_OF_MONTH);
		int itDays_4_15 = ( monthDays - 27 );
		assertEquals(100.00 * 24 / 30.00  + 100.00 * itDays_4_15 / 30.00 * 0.60 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * 24 / 30.00  + 100.00 * itDays_4_15 / 30.00 * 0.60, salary.getTotalPayment(), DELTA);

	}

	@Test
	public void testITWithCRA0055ConstantIII() throws ExpressionException, SQLException,
	SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {
				"SALARIO_MENSUAL * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
					"BASE_CGC * 4.70/100", 
					"BASE_CGP * 1.55/100",
					"BASE_CGP * 0.10/100",
					"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "DIAS_MES", "30.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "SALARIO_MENSUAL", "1500.00");
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "CONSTANTE", "108.00");
		
		PaymentConceptRecord garantizado = addConcept(aonContext, GUARENTEED);
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				garantizado,
				"MEJORAS PREST.SS.ENFERMEDAD COMÚN", 
				"isdef DIAS_ENFERMEDAD_COMUN ?/*user*/CONSTANTE/**/: HIDE()", 
				"_P", 
				"_P", 
				PaymentType.CRA_0055);
		
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
				String.format("BASE_REGULADORA * 0.60 * %s_16_21",  COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startITDate = add(startDate, DAY_OF_MONTH, 24);
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);
		
		startDate = getFirstDayOfMonth(startITDate);
		endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 1 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
						java.util.Date startDate, java.util.Date endDate, IPayment payment,
						Map<String, ITimedVariable<?>> context) {
					// TODO Auto-generated method stub
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
					System.out.println(description + ": " + amount + "," + quote + " (" + startDate +".." + endDate +")");
				}
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		setData(aonContext, contract, "SALARIO_MENSUAL", "1600.00");
		
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
		
		// 2021-07-10
		//
		assertEquals(100.00   , salary.getCommonBase(), DELTA);
		// Not monthly adjust for payment ... so 
		int monthDays = AonDateUtils.getMax(startITDate, Calendar.DAY_OF_MONTH);
		int itDays_4_15 = ( monthDays - 27 );
		assertEquals(100.00 * 24 / 30.00  + 100.00 * itDays_4_15 / 30.00 * 0.60 , salary.getIrpfBase(), DELTA);
		assertEquals(100.00 * 24 / 30.00  + 100.00 * itDays_4_15 / 30.00 * 0.60, salary.getTotalPayment(), DELTA);

	}
	
	
	@Test
	public void testDelaysOverrideI() throws ExpressionException, SQLException,
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

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
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
		
		
		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
//		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
//				.getSalaryPayments()) {
//			System.out.println( payment.getId() + "-. " + payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
//					+ " (" + payment.getExpression() + ")");
//		}

		assertEquals(17, delay.getSalaryPayments().size());
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}
	
	
	@Test
	public void testDelaysOverrideII() throws ExpressionException, SQLException,
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

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
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
		
		
		addData(aonContext, contract, firstDayOfMonth, endDate, "ATRASO", "60.66");
		
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
		assertEquals(10, delay.getSalaryPayments().size());
		
		double expected = 60.66 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysOverrideWithout() throws ExpressionException, SQLException,
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

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
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
		
		assertEquals(20, delay.getSalaryPayments().size());
		
		double expected = 0.00 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysCRA0005I() throws ExpressionException, SQLException,
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

		Date startDate = getFirstDayOfYear(getToday());
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
				String.format("12000.00", WORKED_DAYS , MONTH_DAYS ), 
				"_P", 
				"_P/6", 
				PaymentType.CRA_0005, 
				Month.DECEMBER );
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				firstDayOfJuly, 
				lastDayOfDecember, 
				lastDayOfDecember, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getQuote() + ")");
		}
		
		assertEquals(0.00, delay.getTotalPayment());
		assertEquals(10000.00, delay.getCommonBase());
		assertEquals(10000.00, delay.getRawCommonBase());
		assertEquals(10000.00, delay.getProfessionalBase());
		assertEquals(0.00, delay.getIrpfBase());
	}

	@Test
	public void testDelaysCRA0005II() throws ExpressionException, SQLException,
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
				"BASE_IRPF * 10 / 100.00"
				}, null);
		//@formatter:on

		Date startDate = getFirstDayOfYear(getToday());
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
				String.format("12000.00", WORKED_DAYS , MONTH_DAYS ), 
				"_P", 
				"PRORRATEAR(JULIO,DICIEMBRE)", 
				PaymentType.CRA_0005, 
				Month.DECEMBER );
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				firstDayOfJuly, 
				lastDayOfDecember, 
				lastDayOfDecember, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		Salary delay = delayCalculator.calculate(delayCtx);
		
//		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
//					+ " (" + payment.getQuote() + "," + payment.getIrpf()+  ")");
//		}
		
		for (com.esferalia.aon.payroll.SalaryDeduction deduction : delay
				.getSalaryDeductions()) {
			System.out.println(deduction.getName() + " [ " + deduction.getDescription() + "] :" + deduction.getAmount() );
		}

//		for (com.esferalia.aon.payroll.SalaryData data : delay
//				.getSalaryDatas()) {
//			System.out.println(data.getName() + " = '" + data.getExpression() +"'");
//		}

		assertEquals(0.00, delay.getTotalPayment());
		assertEquals(10000.00, delay.getCommonBase());
		assertEquals(10000.00, delay.getRawCommonBase());
		assertEquals(10000.00, delay.getProfessionalBase());
		assertEquals(0.00, delay.getIrpfBase());
	}

	@Test
	public void testDelaysOverrideITI() throws ExpressionException, SQLException,
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
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);
		
		
		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		Date overrideITStartDate = add(overrideStartDate, DAY_OF_MONTH, 10);  
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 20);  
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		
		addData(aonContext, contract, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE, 
				ContextVariable.CRA_0012.getName());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
			}
			
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%s: %d -. [%s]: %s, %f\r\n", payment.getType().name(), ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
				
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(19, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(3, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), cgcBases.get(0).getEndDate());
		
		assertEquals(overrideITStartDate, cgcBases.get(1).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(1).getEndDate());
		assertEquals(0, Double.parseDouble(cgcBases.get(1).getExpression()), 0.00);

		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(2).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(2).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
		
		assertEquals(PaymentType.CRA_0012.name(), delay.getSalaryData(ContextVariable.DELAY_CAUSE.getName()));

		delay.getSalaryPayments().stream().filter( p -> p.getAmount() > 0.00 ).forEach( p -> assertEquals(PaymentType.CRA_0012, p.getType()) );
	}

	@Test
	public void testDelaysOverrideITII() throws ExpressionException, SQLException,
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
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);

		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		Date overrideITStartDate = overrideStartDate; 
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 25);  
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(18, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(2, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(0).getEndDate());
		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(1).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(1).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
		
		assertEquals(PaymentType.CRA_0008.name(), delay.getSalaryData(ContextVariable.DELAY_CAUSE.getName()));

		delay.getSalaryPayments().stream().filter( p -> p.getAmount() > 0.00 ).forEach( p -> assertEquals(PaymentType.CRA_0008, p.getType()) );
	}

	@Test
	public void testDelaysOverrideITIII() throws ExpressionException, SQLException,
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
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);

		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		Date overrideITStartDate = overrideStartDate; 
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 25);  
		addIT(aonContext, contract, LeaveType.MATERNITY, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(18, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate, MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(2, cgcBases.size());
		
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(0).getEndDate());

		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(1).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(1).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysOverrideITIV() throws ExpressionException, SQLException,
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
		addData(aonContext, contract, contract.getStartDate(), null, "DIAS_MES", "30.00");
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);

		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		Date overrideITStartDate = add(overrideStartDate, DAY_OF_MONTH, 10);  
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 20);  
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(19, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(3, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), cgcBases.get(0).getEndDate());
		
		assertEquals(overrideITStartDate, cgcBases.get(1).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(1).getEndDate());
		assertEquals(0, Double.parseDouble(cgcBases.get(1).getExpression()), 0.00);

		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(2).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(2).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysOverrideITVI() throws ExpressionException, SQLException,
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
		addData(aonContext, contract, contract.getStartDate(), null, "DIAS_MES", "30.00");
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);

		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		Date overrideITStartDate = overrideStartDate; 
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 25);  
		addIT(aonContext, contract, LeaveType.MATERNITY, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(18, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(2, cgcBases.size());
		
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(0).getEndDate());
		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(1).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(1).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysOverrideITVII() throws ExpressionException, SQLException,
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
		addData(aonContext, contract, contract.getStartDate(), null, "DIAS_MES", "30.00");
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);

		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		Date overrideITStartDate = overrideStartDate; 
		Date overrideITEndDate = getLastDayOfMonth(overrideITStartDate);  
		addIT(aonContext, contract, LeaveType.MATERNITY, overrideITStartDate, overrideITEndDate, null);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "0.00");
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(17, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(1, cgcBases.size());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysOverrideITVIII() throws ExpressionException, SQLException,
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
		
		addPrestIts(aonContext, contract);
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), new HashMap<String, String>(){
			{
				put(ContextVariable.TC2.getName(), "\"200\"");
				put(ContextVariable.MONDAY_HOURS.getName(), "2.00");
				put(ContextVariable.TUESDAY_HOURS.getName(), "2.00");
				put(ContextVariable.WEDNESDAY_HOURS.getName(), "2.00");
				put(ContextVariable.THURSDAY_HOURS.getName(), "2.00");
				put(ContextVariable.FRIDAY_HOURS.getName(), "2.00");
			}
		});

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);

		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "6.66");
		Date overrideITStartDate = add(overrideStartDate, DAY_OF_MONTH, 10);  
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 20);  
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "12.22");
		
		

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
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, amount );
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(19, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		List<SalaryData> partialFactors =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.PARTIAL_FACTOR.getName()))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(10, partialFactors.size());
		
		partialFactors.stream().allMatch(d -> Double.parseDouble(d.getExpression()) == 0.25 ); 
		
		assertEquals(3, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), cgcBases.get(0).getEndDate());
		
		assertEquals(overrideITStartDate, cgcBases.get(1).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(1).getEndDate());
		assertEquals(0, Double.parseDouble(cgcBases.get(1).getExpression()), 0.00);

		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(2).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(2).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 * 0.25 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(expected, delay.getIrpfBase(), DELTA);
	}

	@Test
	public void testDelaysOverrideQuoteITI() throws ExpressionException, SQLException,
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
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);
		
		
		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "0.00");
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "6.66");
		Date overrideITStartDate = add(overrideStartDate, DAY_OF_MONTH, 10);  
		Date overrideITEndDate = add(overrideStartDate, DAY_OF_MONTH, 20);  
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "0.00");
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "ATRASO", "0.00");
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "12.22");
		
		

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
		
		addData(aonContext, contract, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE, 
				ContextVariable.CRA_0012.getName());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
			}
			
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%s: %d -. [%s]: %s, %f, %f, %s\r\n", payment.getType().name(), ((IContractPayment) payment ).getId(), payment.getName(), description, amount, quote , startDate.toLocaleString() );
				
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(19, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(3, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), cgcBases.get(0).getEndDate());
		
		assertEquals(overrideITStartDate, cgcBases.get(1).getStartDate());
		assertEquals(overrideITEndDate, cgcBases.get(1).getEndDate());
		assertEquals(0.0, Double.parseDouble(cgcBases.get(1).getExpression()), 0.00);

		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(2).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(2).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(10.00 * 7 , delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(10.00 * 7 , delay.getIrpfBase(), DELTA);
		
		assertEquals(PaymentType.CRA_0012.name(), delay.getSalaryData(ContextVariable.DELAY_CAUSE.getName()));

		delay.getSalaryPayments().stream().filter( p -> p.getAmount() > 0.00 ).forEach( p -> assertEquals(PaymentType.CRA_0012, p.getType()) );
	}

	@Test
	public void testDelaysOverrideQuoteITII() throws ExpressionException, SQLException,
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
		
		setData(aonContext, contract, ContextVariable.MONTH_DAYS.getName(), "30.00");
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);
		
		
		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		Date overrideITStartDate = add(overrideStartDate, DAY_OF_MONTH, 10);  
		addData(aonContext, contract, overrideStartDate, add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), "COTIZACION_ATRASO", "3.33");

		Date overrideITEndDate = add(overrideITStartDate, DAY_OF_MONTH, 9);  
		addData(aonContext, contract, overrideITStartDate, overrideITEndDate, "ATRASO", "0.00");
		addData(aonContext, contract, overrideITStartDate, overrideITEndDate, "COTIZACION_ATRASO", "0.00");
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		addData(aonContext, contract, add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), getLastDayOfMonth(overrideITEndDate), "COTIZACION_ATRASO", "3.33");
		
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "12.22");
		
		

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
		
		addData(aonContext, contract, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE, 
				ContextVariable.CRA_0012.getName());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
			}
			
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				if ( amount > 0.00 || quote > 0.00 )
					System.out.printf( "%s: %d -. [%s]: %s, %f, %f, %s\r\n", payment.getType().name(), ((IContractPayment) payment ).getId(), payment.getName(), description, amount, quote , startDate.toLocaleString() );
				
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(23, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.filter(d -> AonNumberUtils.todouble(d.getExpression()) > 0.00 )
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(2, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), cgcBases.get(0).getEndDate());
		
		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(1).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(1).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		
		int workDays = get( getLastDayOfMonth(overrideITStartDate), DAY_OF_MONTH) - 10;
		
		assertEquals(10.00 * 9 + ( 10.00 / 30 *  workDays ) , delay.getTotalPayment(), DELTA);
		assertEquals(10.00 * 9 + ( 10.00 / 30 *  workDays ) , delay.getIrpfBase(), DELTA);
		
		assertEquals(PaymentType.CRA_0012.name(), delay.getSalaryData(ContextVariable.DELAY_CAUSE.getName()));

		delay.getSalaryPayments().stream().filter( p -> p.getAmount() > 0.00 ).forEach( p -> assertEquals(PaymentType.CRA_0012, p.getType()) );
	}

	@Test
	public void testDelaysOverrideQuoteITIII() throws ExpressionException, SQLException,
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
		
		setData(aonContext, contract, ContextVariable.MONTH_DAYS.getName(), "30.00");
		
		addPrestIts(aonContext, contract);

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);
		
		
		Date overrideStartDate = add(firstDayOfMonth, MONTH, 1);
		Date overrideITStartDate = add(overrideStartDate, DAY_OF_MONTH, 10);  
		addData(aonContext, contract, overrideStartDate, add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), "COTIZACION_ATRASO", "3.33");

		Date overrideITEndDate = add(overrideITStartDate, DAY_OF_MONTH, 9);  
		addData(aonContext, contract, overrideITStartDate, add(overrideITStartDate, DAY_OF_MONTH,2), "ATRASO", "1.00");
		addData(aonContext, contract, overrideITStartDate, add(overrideITStartDate, DAY_OF_MONTH,2), "COTIZACION_ATRASO", "3.00");
		addData(aonContext, contract, add(overrideITStartDate, DAY_OF_MONTH,3), overrideITEndDate, "ATRASO", "2.00");
		addData(aonContext, contract, add(overrideITStartDate, DAY_OF_MONTH,3), overrideITEndDate, "COTIZACION_ATRASO", "6.00");
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, overrideITStartDate, overrideITEndDate, null);
		
		addData(aonContext, contract, add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), getLastDayOfMonth(overrideITEndDate), "COTIZACION_ATRASO", "3.33");
		
		
		
		overrideStartDate = add(firstDayOfMonth, MONTH, 3);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "3.33");
		overrideStartDate = add(firstDayOfMonth, MONTH, 6);
		addData(aonContext, contract, overrideStartDate, getLastDayOfMonth(overrideStartDate), "COTIZACION_ATRASO", "12.22");
		
		

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
		
		addData(aonContext, contract, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE, 
				ContextVariable.CRA_0012.getName());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		SQLContractDelayCalculatorContext delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
			}
			
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%s: %d -. [%s]: %s, %f, %f, %s\r\n", payment.getType().name(), ((IContractPayment) payment ).getId(), payment.getName(), description, amount, quote , startDate.toLocaleString() );
				
			}
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		assertEquals(24, delay.getSalaryPayments().size());

		List<SalaryData> cgcBases =
		delay.getSalaryDatas().stream()
		.filter(d -> d.getName().equals(ContextVariable.CGC_BASE.getName()))
		.filter(d -> get(d.getStartDate(),MONTH) == get(overrideITStartDate,MONTH))
		.filter(d -> AonNumberUtils.todouble(d.getExpression()) > 0.00 )
		.sorted((d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
		.collect(Collectors.toList());
		
		assertEquals(4, cgcBases.size());
		assertEquals(getFirstDayOfMonth(overrideITStartDate), cgcBases.get(0).getStartDate());
		assertEquals(add(overrideITStartDate, Calendar.DAY_OF_MONTH,-1), cgcBases.get(0).getEndDate());
		
		assertEquals(add(overrideITEndDate, Calendar.DAY_OF_MONTH,1), cgcBases.get(3).getStartDate());
		assertEquals(getLastDayOfMonth(overrideITStartDate), cgcBases.get(3).getEndDate());
		
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 10.00 * 7.00 + 9.00 + 6.66 + 3.33 + 12.22 ;
		
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		
		
		int workDays = get( getLastDayOfMonth(overrideITStartDate), DAY_OF_MONTH) - 10;

		assertEquals(10.00 * 9.00 + 3.00 + ( 10.00 / 30.00 *  workDays ) , delay.getTotalPayment(), DELTA);
		assertEquals(10.00 * 9.00 + 3.00 + ( 10.00 / 30.00 *  workDays ) , delay.getIrpfBase(), DELTA);
		
		assertEquals(PaymentType.CRA_0012.name(), delay.getSalaryData(ContextVariable.DELAY_CAUSE.getName()));

		delay.getSalaryPayments().stream().filter( p -> p.getAmount() > 0.00 ).forEach( p -> assertEquals(PaymentType.CRA_0012, p.getType()) );
	}

	@Test
	public void testDelaysBonusI() throws ExpressionException, SQLException,
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
						"BASE_CGC * 10.00 / 100.00 ",
				}, null);
		//@formatter:on
		
		addSystemData(aonContext, contract.getStartDate(), 	null, new HashMap<String,String>(){
			{
				put("BASE_CGP_MIN",
						"756.6000 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
				put("BASE_CGP_MAX",
						"3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
				put("BASE_CGC_MIN",
						"(1056.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
				put("BASE_CGC_MAX",
						"(3606.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
				
			}
		});
		
		addData(aonContext, contract, contract.getStartDate(), null,
				new HashMap<String, String>() {
					{
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addBonus(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH,25), "10.00", "BONIFICACIÓN");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		

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
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		assertEquals(0.00, delay.getTotalPayment());
		assertEquals(0.00, delay.getCommonBase());
		assertEquals(0.00, delay.getRawCommonBase());
		assertEquals(0.00, delay.getProfessionalBase());
		assertEquals(0.00, delay.getIrpfBase());
		
		for (com.esferalia.aon.payroll.SalaryDeduction deduction : delay
				.getSalaryDeductions()) {
			System.out.println(deduction.getName() + " [ " + deduction.getDescription() + "] :" + deduction.getAmount()
					+ " (" + deduction.getExpression() + ")");
		}

		assertEquals(0.00, delay.getTotalDeduction());

		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
		}
		
		assertEquals(10.00, delay.getTotalPayment());
		assertEquals(10.00, delay.getCommonBase());
		assertEquals(10.00, delay.getRawCommonBase());
		assertEquals(10.00, delay.getProfessionalBase());
		assertEquals(10.00, delay.getIrpfBase());
		
		for (com.esferalia.aon.payroll.SalaryDeduction deduction : delay
				.getSalaryDeductions()) {
			System.out.println(deduction.getName() + " [ " + deduction.getDescription() + "] :" + deduction.getAmount()
					+ " (" + deduction.getExpression() + ")");
		}

		assertEquals(10.00 * 10.00 / 100.00, delay.getTotalDeduction(), DELTA);
	}


	@Test
	public void testDelaysWithDelays() throws ExpressionException, SQLException,
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
		
		setData(aonContext, contract, "DIAS_MES", "30.00");

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
			//System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
			//		+ " (" + payment.getExpression() + ")");
		}

		
		assertEquals(100.00, delay.getTotalPayment(), 0.00);
		assertEquals(100.00, delay.getCommonBase(), 0.00);
		assertEquals(100.00, delay.getRawCommonBase(), 0.00);
		assertEquals(100.00, delay.getProfessionalBase(), 0.00);
		assertEquals(100.00, delay.getIrpfBase(), 0.00);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		calculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();

		addPayment(aonContext, contract, "5.00 * DIAS_TRABAJADOS / DIAS_MES");
		
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			    System.out.println( "[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ " + description + "] :"
				    + amount+ " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(50.00, delay.getTotalPayment(), 0.00);
		assertEquals(50.00, delay.getCommonBase(), 0.00);
		assertEquals(50.00, delay.getRawCommonBase(), 0.00);
		assertEquals(50.00, delay.getProfessionalBase(), 0.00);
		assertEquals(50.00, delay.getIrpfBase(), 0.00);
	}

	@Test
	public void testZeroDelaysWithDelays() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord salarioBAse = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "15/07";
					}
				}, 
				},
				new Payment[] {
					new Payment() {
						{
						    	this.concept = salarioBAse.getId();
							this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						}
					},
					new Payment() {
						{
						    	this.concept = salarioBAse.getId();
							this.expression = "PLUS * DIAS_TRABAJADOS / DIAS_MES";
						}
					}
				},
				new HashMap<String,String>(){
					{
						put("SALARIO", "1500.00");
						put("PLUS", "250.00");
					}
				});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
					
				}, 
				new String[] {
				}, category);
		//@formatter:on
		
		setData(aonContext, contract, "TC2", "\"100\"");
		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "GRUPO_COTIZACION", "\"05\"");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 16 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			    @Override
			    public void addPayment(Double amount, Double quote, Double tax, String description,
			            java.util.Date startDate, java.util.Date endDate, IPayment payment,
			            Map<String, ITimedVariable<?>> context) {
			        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
//				System.out.println(payment.getName() + " [ " + description + "] :" + amount + ", " + quote + " (" + payment.getExpression() + ")" );
			    }
			    
			    @Override
			    public void setCgcBase(Double cgcBase) {
			        super.setCgcBase(cgcBase);
//			        System.out.println("[ CGC BASE ] :" + cgcBase );
			    }
			    
			    @Override
			    public void setCgpBase(Double cgpBase) {
			        super.setCgpBase(cgpBase);
//			        System.out.println("[ CGC BASE ] :" + cgpBase );
			    }
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		//addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		addData(aonContext, contract, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), "SALARIO", "1510.00");
		

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
		
//		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
//					+ " (" + payment.getExpression() + ")" + " " + payment.getQuote() );
//		}

		
		double expected = 160.00 + 1.67 * 16.00;
//		System.out.println("EXPECTED :" + expected);
		
		assertEquals(160.00 + 1.67 * 16.00, delay.getTotalPayment(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00 , delay.getCommonBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getRawCommonBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getProfessionalBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getIrpfBase(), 0.05);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		calculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			assertEquals(0.00, amount, 0.00);
//			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
//				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
		
		// Now One month 
		
		Date delayMonth = getFirstDayOfMonth(getToday());
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(delayMonth), 
			getLastDayOfMonth(delayMonth), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			assertEquals(0.00, amount, 0.00);
//			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
//				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
		
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(add(delayMonth, MONTH, 2)), 
			getLastDayOfMonth(add(delayMonth, MONTH, 7)), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			assertEquals(0.00, amount, 0.00);
			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
	}

	@Test
	public void testZeroDelaysWithDelaysDaily() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord salarioBAse = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "15/07";
					}
				}, 
				},
				new Payment[] {
					new Payment() {
						{
						    	this.concept = salarioBAse.getId();
							this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						}
					},
					new Payment() {
						{
						    	this.concept = salarioBAse.getId();
							this.expression = "PLUS * DIAS_TRABAJADOS / DIAS_MES";
						}
					}
				},
				new HashMap<String,String>(){
					{
						put("SALARIO", "1500.00");
						put("PLUS", "250.00");
					}
				});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
					
				}, 
				new String[] {
				}, category);
		//@formatter:on
		
		setData(aonContext, contract, "TC2", "\"100\"");
		setData(aonContext, contract, "DIAS_MES", "DIAS_NATURALES_MES");
		setData(aonContext, contract, "GRUPO_COTIZACION", "\"09\"");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 16 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			    @Override
			    public void addPayment(Double amount, Double quote, Double tax, String description,
			            java.util.Date startDate, java.util.Date endDate, IPayment payment,
			            Map<String, ITimedVariable<?>> context) {
			        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.println(payment.getName() + " [ " + description + "] :" + amount + ", " + quote + " (" + payment.getExpression() + ")" );
			    }
			    
			    @Override
			    public void setCgcBase(Double cgcBase) {
			        super.setCgcBase(cgcBase);
			        System.out.println("[ CGC BASE ] :" + cgcBase );
			    }
			    
			    @Override
			    public void setCgpBase(Double cgpBase) {
			        super.setCgpBase(cgpBase);
			        System.out.println("[ CGC BASE ] :" + cgpBase );
			    }
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		//addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		addData(aonContext, contract, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), "SALARIO", "1510.00");
		

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
					+ " (" + payment.getExpression() + ")" + " " + payment.getQuote() );
		}

		
		double expected = 160.00 + 1.67 * 16.00;
		System.out.println("EXPECTED :" + expected);
		
		assertEquals(160.00 + 1.67 * 16.00, delay.getTotalPayment(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00 , delay.getCommonBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getRawCommonBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getProfessionalBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getIrpfBase(), 0.05);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		calculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			//assertEquals(0.00, amount, 0.00);
			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
	}

	
	@Test
	public void testZeroDelaysWithDelaysDelays() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord salarioBAse = addConcept(aonContext, "SALARIO_BASE", PaymentType.CRA_0001);
		
		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				new Extra() {
					{
						this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, 
				new Extra() {
					{
						this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "15/07";
					}
				}, 
				},
				new Payment[] {
					new Payment() {
						{
						    	this.concept = salarioBAse.getId();
							this.expression = "SALARIO * DIAS_TRABAJADOS / DIAS_MES";
						}
					},
					new Payment() {
						{
						    	this.concept = salarioBAse.getId();
							this.expression = "PLUS * DIAS_TRABAJADOS / DIAS_MES";
						}
					}
				},
				new HashMap<String,String>(){
					{
						put("SALARIO", "1500.00");
						put("PLUS", "250.00");
					}
				});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
					
				}, 
				new String[] {
				}, category);
		//@formatter:on
		
		setData(aonContext, contract, "TC2", "\"100\"");
		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "GRUPO_COTIZACION", "\"05\"");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		for ( int i = 0 ; i < 16 ; i++ ) {
			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
					connection, startDate, endDate, endDate, contract);
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			    @Override
			    public void addPayment(Double amount, Double quote, Double tax, String description,
			            java.util.Date startDate, java.util.Date endDate, IPayment payment,
			            Map<String, ITimedVariable<?>> context) {
			        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
//				System.out.println(payment.getName() + " [ " + description + "] :" + amount + ", " + quote + " (" + payment.getExpression() + ")" );
			    }
			    
			    @Override
			    public void setCgcBase(Double cgcBase) {
			        super.setCgcBase(cgcBase);
//			        System.out.println("[ CGC BASE ] :" + cgcBase );
			    }
			    
			    @Override
			    public void setCgpBase(Double cgpBase) {
			        super.setCgpBase(cgpBase);
//			        System.out.println("[ CGC BASE ] :" + cgpBase );
			    }
			};
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
			startDate = add(endDate, DAY_OF_MONTH, 1);
			endDate = getLastDayOfMonth(startDate);
		}
		
		//addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");
		addData(aonContext, contract, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), "SALARIO", "1510.00");
		

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
		
//		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
//				.getSalaryPayments()) {
//			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
//					+ " (" + payment.getExpression() + ")" + " " + payment.getQuote() );
//		}

		
		double expected = 160.00 + 1.67 * 16.00;
//		System.out.println("EXPECTED :" + expected);
		
		assertEquals(160.00 + 1.67 * 16.00, delay.getTotalPayment(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00 , delay.getCommonBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getRawCommonBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getProfessionalBase(), 0.05);
		assertEquals(160.00 + 1.67 * 16.00, delay.getIrpfBase(), 0.05);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		calculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		addData(aonContext, contract, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), "SALARIO", "1520.00");

		criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
				getFirstDayOfMonth(getToday()), 
				add(startDate, DAY_OF_MONTH, -1), 
				endDate, 
				criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder());
		delay = delayCalculator.calculate(delayCtx);
		
		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
					+ " (" + payment.getExpression() + ")" + " " + payment.getQuote() );
		}

		
		expected = 160.00 + 1.66 * 16.00;
//		System.out.println("EXPECTED :" + expected);
		
		assertEquals(160.00 + 1.66 * 16.00, delay.getTotalPayment(), 0.05);
		assertEquals(160.00 + 1.66 * 16.00 , delay.getCommonBase(), 0.05);
		assertEquals(160.00 + 1.66 * 16.00, delay.getRawCommonBase(), 0.05);
		assertEquals(160.00 + 1.66 * 16.00, delay.getProfessionalBase(), 0.05);
		assertEquals(160.00 + 1.66 * 16.00, delay.getIrpfBase(), 0.05);
		
		calculator = new SmartContractSalaryCalculator<ISalary>();
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		calculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();

		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(getToday()), 
			add(startDate, DAY_OF_MONTH, -1), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			assertEquals(0.00, amount, 0.00);
//			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
//				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
		
		// Now One month 
		
		Date delayMonth = getFirstDayOfMonth(getToday());
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(delayMonth), 
			getLastDayOfMonth(delayMonth), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			assertEquals(0.00, amount, 0.00);
//			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
//				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
		
		delayCtx = new SQLContractDelayCalculatorContext(connection, 
			getFirstDayOfMonth(add(delayMonth, MONTH, 2)), 
			getLastDayOfMonth(add(delayMonth, MONTH, 7)), 
			endDate, 
			criteria);
		delayCtx.next();
		delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			assertEquals(0.00, amount, 0.00);
			System.out.println("[" + startDate + "..." + endDate + "] :" + payment.getName() + " [ "
				+ description + "] :" + amount + " (" + payment.getExpression() + ")");
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		});
		delay = delayCalculator.calculate(delayCtx);

		assertEquals(0.00, delay.getTotalPayment(), 0.00);
		assertEquals(0.00, delay.getCommonBase(), 0.00);
		assertEquals(0.00, delay.getRawCommonBase(), 0.00);
		assertEquals(0.00, delay.getProfessionalBase(), 0.00);
		assertEquals(0.00, delay.getIrpfBase(), 0.00);
	}

	@Test
	public void testDelaysPPEI() throws ExpressionException, SQLException,
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
				"4.6 / 100.00 * BASE_CGC"
				}, null);
		//@formatter:on

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
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
		
		addCost(aonContext, contract, contract.getStartDate(), null,
				"/*read-only*/ isdef BASE_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','BASE_PPE * 23.6 / 100.00 '); BASE_PPE : HIDE() /**/",
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"PPE_E");
		
		PaymentConceptRecord ppeConcept = addConcept(aonContext, "PPE", PaymentType.CRA_0000);
		addPayment(
				aonContext, 
				contract, 
				firstDayOfMonth, 
				null, 
				ppeConcept, 
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/) ; 0.00", 
				null, 
				"__PPE", 
				(PaymentType) null, 
				(Byte) null);
		addData(aonContext, contract, firstDayOfMonth, null, "APORTACION_EMPRESA_PPE","100.00");

		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		addData(aonContext, contract, 
				firstDayOfMonth, 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE.getName(), 
				ContextVariable.CRA_0033.getName());
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		
		SQLContractSalaryCalculatorContext delayCtx = new SQLContractPPECalculatorContext(connection, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), endDate, criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f, %s\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, quote, startDate  );
			}
			
			@Override
			public void addBonus(Double amount, String description, java.util.Date startDate, java.util.Date endDate,
					IBonus bonus, Map<String, ITimedVariable<?>> context) {
				super.addBonus(amount, description, startDate, endDate, bonus, context);
				System.out.printf( "[%s]: %s, %f, %s\r\n", bonus.getName(), description, amount, startDate  );
			}
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
				//System.out.println(name + " : " + data.getValue(data.getPeriod() ));
			}
			
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.DELAY_CAUSE.getName()))
				.forEach(v -> assertEquals(v.getExpression(), "CRA_0033"));
		
//		for (com.esferalia.aon.payroll.SalaryPayment payment : delay
//				.getSalaryPayments()) {
//			System.out.println( payment.getId() + "-. " + payment.getName() + " [ " + payment.getDescription() + "] :" + payment.getAmount()
//					+ " (" + payment.getExpression() + ")" + payment.getType());
//		}

		assertEquals(10, delay.getSalaryPayments().size());
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 100.00 * 10 ;
		
		assertEquals(0.00, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(0.00, delay.getIrpfBase(), DELTA);
		assertEquals(-(4.6 / 100.00 * expected ), delay.getTotalLiquid(), DELTA);
		assertEquals(expected, delay.getTotalEnterprise(), DELTA);
		
		
		delayCtx = new SQLContractPPECalculatorContext(connection, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), endDate, criteria);
		delayCtx.next();

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(delayCtx);
		jooqSalaryBuilder.execute();
		
		endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Date delaysStartDate = getFirstDayOfMonth(getToday());
		Date delaysEndDate = add(startDate, DAY_OF_MONTH, -1);
		Double ppeDelays = 
				ctx.getExpressionContext().eval(
				String.format(
				"SELF.getPPEDelaysLiquid(FECHA(%d,%d,%d), FECHA(%d,%d,%d))"
				,get(delaysStartDate, Calendar.YEAR)
				,get(delaysStartDate, Calendar.MONTH)
				,get(delaysStartDate, Calendar.DATE)

				,get(delaysEndDate, Calendar.YEAR)
				,get(delaysEndDate, Calendar.MONTH)
				,get(delaysEndDate, Calendar.DATE)
				), 
				startDate, endDate, Double.class)
				.stream().collect(Collectors.summingDouble( ITimedResult::getValue));
		assertEquals(4.6 / 100 * expected, ppeDelays, DELTA);
		
		ppeDelays = 
				ctx.getExpressionContext().eval(
				String.format(
				"ATRASOS_PPE(FECHA(%d,%d,%d), FECHA(%d,%d,%d))"
				,get(delaysStartDate, Calendar.YEAR)
				,get(delaysStartDate, Calendar.MONTH)
				,get(delaysStartDate, Calendar.DATE)

				,get(delaysEndDate, Calendar.YEAR)
				,get(delaysEndDate, Calendar.MONTH)
				,get(delaysEndDate, Calendar.DATE)
				), 
				startDate, endDate, Double.class)
				.stream().collect(Collectors.summingDouble( ITimedResult::getValue));
		assertEquals(4.6 / 100 * expected, ppeDelays, DELTA);

		ppeDelays = 
				ctx.getExpressionContext().eval(
				String.format(
				"ATRASOS_PPE('%d/%d/%d', '%d/%d/%d')"
				,get(delaysStartDate, Calendar.DATE)
				,get(delaysStartDate, Calendar.MONTH)
				,get(delaysStartDate, Calendar.YEAR)

				,get(delaysEndDate, Calendar.DATE)
				,get(delaysEndDate, Calendar.MONTH)
				,get(delaysEndDate, Calendar.YEAR)
				), 
				startDate, endDate, Double.class)
				.stream().collect(Collectors.summingDouble( ITimedResult::getValue));
		assertEquals(4.6 / 100 * expected, ppeDelays, DELTA);

		ppeDelays = 
				ctx.getExpressionContext().eval(
				String.format(
				"ATRASOS_PPE()"
				,get(delaysStartDate, Calendar.DATE)
				,get(delaysStartDate, Calendar.MONTH)
				,get(delaysStartDate, Calendar.YEAR)

				,get(delaysEndDate, Calendar.DATE)
				,get(delaysEndDate, Calendar.MONTH)
				,get(delaysEndDate, Calendar.YEAR)
				), 
				startDate, endDate, Double.class)
				.stream().collect(Collectors.summingDouble( ITimedResult::getValue));
		assertEquals(4.6 / 100 * expected, ppeDelays, DELTA);
	}

	@Test
	public void testDelaysPPEII() throws ExpressionException, SQLException,
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
				"4.6 / 100.00 * BASE_CGC"
				}, null);
		//@formatter:on
		setData(aonContext, contract, "COEFICIENTE_PARCIALIDAD","0.50");

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
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
		
		addCost(aonContext, contract, contract.getStartDate(), null,
				"/*read-only*/ isdef BASE_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','BASE_PPE * 23.6 / 100.00 '); BASE_PPE : HIDE() /**/",
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"PPE_E");
		
		PaymentConceptRecord ppeConcept = addConcept(aonContext, "PPE", PaymentType.CRA_0000);
		addPayment(
				aonContext, 
				contract, 
				firstDayOfMonth, 
				null, 
				ppeConcept, 
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/) ; 0.00", 
				null, 
				"__PPE", 
				(PaymentType) null, 
				(Byte) null);
		

		setData(aonContext, contract, "HORAS_NOMINA", "MAX(1,FLOOR(MIN(HORAS_TRABAJADAS, 100.00)))");

		addData(aonContext, contract, firstDayOfMonth, null, "APORTACION_EMPRESA_PPE","100.00");

		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		addData(aonContext, contract, 
				firstDayOfMonth, 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE.getName(), 
				ContextVariable.CRA_0033.getName());
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		
		SQLContractSalaryCalculatorContext delayCtx = new SQLContractPPECalculatorContext(connection, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), endDate, criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f, %s\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, quote, startDate  );
			}
			
			@Override
			public void addBonus(Double amount, String description, java.util.Date startDate, java.util.Date endDate,
					IBonus bonus, Map<String, ITimedVariable<?>> context) {
				super.addBonus(amount, description, startDate, endDate, bonus, context);
				System.out.printf( "[%s]: %s, %f, %s\r\n", bonus.getName(), description, amount, startDate  );
			}
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
				//System.out.println(name + " : " + data.getValue(data.getPeriod() ));
			}
			
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.DELAY_CAUSE.getName()))
				.forEach(v -> assertEquals(v.getExpression(), "CRA_0033"));
		
		List<ContextData> salaryHours = AON.getSalaryData( new AONContext(connection) , 
		p -> p.getContractProperty().eq(contract.getId()))
		//.sorted( (s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
		.flatMap( s -> s.getContextData().get(ContextVariable.SALARY_HOURS.getName()).stream())
		.sorted( (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate() ))
		.toList();
		
		
		List<SalaryData> delayHours = delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.SALARY_HOURS.getName())).sorted((v1,v2) -> v1.getStartDate().compareTo(v2.getStartDate())).toList();
		for (int i = 0; i < salaryHours.size(); i++) {
			Double delayHour = Double.parseDouble(delayHours.get(i).getExpression());
			Double salaryHour = Double.parseDouble(salaryHours.get(i).getExpression());
			assertEquals(salaryHour, delayHour, DELTA );
			
		}	
		
		assertEquals(10, delay.getSalaryPayments().size());
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 100.00 * 10 ;
		
		assertEquals(0.00, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(0.00, delay.getIrpfBase(), DELTA);
		assertEquals(-(4.6 / 100.00 * expected ), delay.getTotalLiquid(), DELTA);
		assertEquals(expected, delay.getTotalEnterprise(), DELTA);
		
		
	}

	@Test
	public void testDelaysPPEIII() throws ExpressionException, SQLException,
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
				"4.6 / 100.00 * BASE_CGC"
				}, null);
		//@formatter:on
		setData(aonContext, contract, "COEFICIENTE_PARCIALIDAD","0.50");

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
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
		
		addCost(aonContext, contract, contract.getStartDate(), null,
				"/*read-only*/ isdef BASE_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','BASE_PPE * 23.6 / 100.00 '); BASE_PPE : HIDE() /**/",
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"PPE_E");
		
		PaymentConceptRecord ppeConcept = addConcept(aonContext, "PPE", PaymentType.CRA_0000);
		addPayment(
				aonContext, 
				contract, 
				firstDayOfMonth, 
				null, 
				ppeConcept, 
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/) ; 0.00", 
				null, 
				"__PPE", 
				(PaymentType) null, 
				(Byte) null);
		

		setData(aonContext, contract, "HORAS_NOMINA", "MAX(1,FLOOR(MIN(HORAS_TRABAJADAS, 100.00)))");

		addData(aonContext, contract, firstDayOfMonth, null, "APORTACION_EMPRESA_PPE","100.00");

		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		addData(aonContext, contract, 
				firstDayOfMonth, 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE.getName(), 
				ContextVariable.CRA_0033.getName());
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		
		SQLContractSalaryCalculatorContext delayCtx = new SQLContractPPECalculatorContext(connection, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), endDate, criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f, %s\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, quote, startDate  );
			}
			
			@Override
			public void addBonus(Double amount, String description, java.util.Date startDate, java.util.Date endDate,
					IBonus bonus, Map<String, ITimedVariable<?>> context) {
				super.addBonus(amount, description, startDate, endDate, bonus, context);
				System.out.printf( "[%s]: %s, %f, %s\r\n", bonus.getName(), description, amount, startDate  );
			}
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
				//System.out.println(name + " : " + data.getValue(data.getPeriod() ));
			}
			
		});
		Salary delay = delayCalculator.calculate(delayCtx);
		
		delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.DELAY_CAUSE.getName()))
				.forEach(v -> assertEquals(v.getExpression(), "CRA_0033"));
		
		List<ContextData> salaryHours = AON.getSalaryData( new AONContext(connection) , 
		p -> p.getContractProperty().eq(contract.getId()))
		//.sorted( (s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
		.flatMap( s -> s.getContextData().get(ContextVariable.SALARY_HOURS.getName()).stream())
		.sorted( (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate() ))
		.toList();
		
		
		List<SalaryData> delayHours = delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.SALARY_HOURS.getName())).sorted((v1,v2) -> v1.getStartDate().compareTo(v2.getStartDate())).toList();
		for (int i = 0; i < salaryHours.size(); i++) {
			Double delayHour = Double.parseDouble(delayHours.get(i).getExpression());
			Double salaryHour = Double.parseDouble(salaryHours.get(i).getExpression());
			assertEquals(salaryHour, delayHour, DELTA );
			
		}	
		
		assertEquals(10, delay.getSalaryPayments().size());
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		double expected = 100.00 * 10 ;
		
		assertEquals(0.00, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), DELTA);
		assertEquals(expected, delay.getRawCommonBase(), DELTA);
		assertEquals(expected, delay.getProfessionalBase(), DELTA);
		assertEquals(0.00, delay.getIrpfBase(), DELTA);
		assertEquals(-(4.6 / 100.00 * expected ), delay.getTotalLiquid(), DELTA);
		assertEquals(expected, delay.getTotalEnterprise(), DELTA);
		
		
	}

	
	@Test
	public void testDelaysPPEIV() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, Collections.singletonMap("DIAS_MES", "[ \"01\":30, \"02\":30, \"03\":30, \"04\":30, \"05\":30, \"06\":30, \"07\":30, \"08\": DIAS_NATURALES_MES, \"09\": DIAS_NATURALES_MES, \"10\": DIAS_NATURALES_MES, \"11\": DIAS_NATURALES_MES][GRUPO_COTIZACION]"));
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				"4.6 / 100.00 * BASE_CGC"
				}, null);
		//@formatter:on
		setData(aonContext, contract, "GRUPO_COTIZACION","\"08\"");
		setData(aonContext, contract, "COEFICIENTE_PARCIALIDAD","0.50");

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());

		Date startDate = firstDayOfMonth;
		Date endDate = getLastDayOfMonth(startDate);
		
		Date itStartDate = add(add(startDate, Calendar.MONTH, 2), Calendar.DAY_OF_MONTH, 10 ); 
		Date itEndDate = add(itStartDate, Calendar.MONTH, 2); 
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, itStartDate, itEndDate, null);
		
		
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
		
		addCost(aonContext, contract, contract.getStartDate(), null,
				"/*read-only*/ isdef BASE_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','BASE_PPE * 23.6 / 100.00 '); BASE_PPE : HIDE() /**/",
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"PPE_E");
		
		PaymentConceptRecord ppeConcept = addConcept(aonContext, "PPE", PaymentType.CRA_0000);
		addPayment(
				aonContext, 
				contract, 
				firstDayOfMonth, 
				null, 
				ppeConcept, 
				"APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO", 
				"TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/ * DIAS_TRABAJADOS / DIAS_MES) ; 0.00", 
				null, 
				"__PPE", 
				(PaymentType) null, 
				(Byte) null);
		

		setData(aonContext, contract, "HORAS_NOMINA", "MAX(1,FLOOR(MIN(HORAS_TRABAJADAS, 100.00)))");

		addData(aonContext, contract, firstDayOfMonth, null, "APORTACION_EMPRESA_PPE","100.00");

		addPayment(aonContext, contract, "10.00 * DIAS_TRABAJADOS / DIAS_MES");

		addData(aonContext, contract, 
				firstDayOfMonth, 
				add(startDate, DAY_OF_MONTH, -1), 
				ContextVariable.DELAY_CAUSE.getName(), 
				ContextVariable.CRA_0033.getName());
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		
		SQLContractSalaryCalculatorContext delayCtx = new SQLContractPPECalculatorContext(connection, getFirstDayOfMonth(getToday()), add(startDate, DAY_OF_MONTH, -1), endDate, criteria);
		delayCtx.next();
		SmartContractSalaryCalculator<Salary> delayCalculator = new SmartContractSalaryCalculator<Salary>();
		delayCalculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				System.out.printf( "%d -. [%s]: %s, %f, %s\r\n", ((IContractPayment) payment ).getId(), payment.getName(), description, quote, startDate  );
			}
			
			@Override
			public void addBonus(Double amount, String description, java.util.Date startDate, java.util.Date endDate,
					IBonus bonus, Map<String, ITimedVariable<?>> context) {
				super.addBonus(amount, description, startDate, endDate, bonus, context);
				System.out.printf( "[%s]: %s, %f, %s\r\n", bonus.getName(), description, amount, startDate  );
			}
			
			@Override
			public void addData(String name, ITimedVariable<?> data) {
				// TODO Auto-generated method stub
				super.addData(name, data);
				//System.out.println(name + " : " + data.getValue(data.getPeriod() ));
			}
			
		});

		Salary delay = delayCalculator.calculate(delayCtx);
		
//		delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.DELAY_CAUSE.getName()))
//				.forEach(v -> assertEquals(v.getExpression(), "CRA_0033"));
//		
//		List<ContextData> salaryHours = AON.getSalaryData( new AONContext(connection) , 
//		p -> p.getContractProperty().eq(contract.getId()))
//		//.sorted( (s1,s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
//		.flatMap( s -> s.getContextData().get(ContextVariable.SALARY_HOURS.getName()).stream())
//		.sorted( (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate() ))
//		.toList();
//		
//		
//		List<SalaryData> delayHours = delay.getSalaryDatas().stream().filter(d -> d.getName().equals(ContextVariable.SALARY_HOURS.getName())).sorted((v1,v2) -> v1.getStartDate().compareTo(v2.getStartDate())).toList();
//		for (int i = 0; i < salaryHours.size(); i++) {
//			Double delayHour = Double.parseDouble(delayHours.get(i).getExpression());
//			Double salaryHour = Double.parseDouble(salaryHours.get(i).getExpression());
//			assertEquals(salaryHour, delayHour, DELTA );
//			
//		}	
		
		assertEquals(9, delay.getSalaryPayments().size());
		
		long distinct = delay.getSalaryPayments().stream().map(p -> p.getId()).distinct().count();
		assertEquals(1, distinct);
		
		int itStartDay =  get(itStartDate, Calendar.DAY_OF_MONTH );
		int itStartDays =  get( getLastDayOfMonth(itStartDate), Calendar.DAY_OF_MONTH);

		int itEndDay =  get(itEndDate, Calendar.DAY_OF_MONTH );
		int itEndDays =  get( getLastDayOfMonth(itEndDate), Calendar.DAY_OF_MONTH);

		double expected = 100.00 * 7 * 0.5  
				+ 100.00 * 0.5 * itStartDay / itStartDays 
				+ 100.00 * 0.5 * ( itEndDays - itEndDay - 1 ) / itEndDays 
				;
		
		assertEquals(0.00, delay.getTotalPayment(), DELTA);
		assertEquals(expected, delay.getCommonBase(), 0.5);
		assertEquals(expected, delay.getRawCommonBase(), 0.5);
		assertEquals(expected, delay.getProfessionalBase(), 0.5);
		assertEquals(0.00, delay.getIrpfBase(), DELTA);
		assertEquals(-(4.6 / 100.00 * expected ), delay.getTotalLiquid(), DELTA);
		assertEquals(expected, delay.getTotalEnterprise(), 0.5);
		
		
	}

	// ------------------------------------------

	protected ISQLContractSalaryCalculatorContext getSmartSQLContractSettleContext(Connection connection, Date contractStart,
			Date endDate, ContractRecord contract) throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext ctx = new SmartSQLContractSettleCalculatorContext(connection, contractStart,
				endDate, endDate, criteria);
		ctx.next();
		return ctx;
	}

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
		addPayment(aonContext, contract, prestIT 
				,String.format("/*read-only*/%s * 0.00/**/",  MATERNITY_DAYS)
				,String.format("%s * (isdef %s ? %s : 1.00) * BASE_REGULADORA",  QUOTE_DAYS, MATERNITY_FACTOR, MATERNITY_FACTOR)
				);
	}

}
