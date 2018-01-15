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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

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
		Assert.assertEquals(150.00 + 15.00/12*10 + 15.00/12*10, delay.getIrpfBase(), DELTA);
		

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
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
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
		ContractSalaryCalculator<Salary> delayCalculator = new ContractSalaryCalculator<Salary>();
		
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
					Assert.assertEquals(100.00, cgcBase);
					
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
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
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
		ContractSalaryCalculator<ISalary> delayCalculator = new ContractSalaryCalculator<ISalary>();
		
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
	

}
