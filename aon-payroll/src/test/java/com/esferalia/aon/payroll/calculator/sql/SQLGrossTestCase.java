package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;


public class SQLGrossTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testGross() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
//		Date startIt = getToday();
//		Date endIt = AonDateUtils.add(getToday(), Calendar.DATE, 100);
//		
//		addIT(aonContext, 
//				contract, 
//				LeaveType.COMMON_DISEASE, 
//				startIt,
//				endIt, 
//				null);
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		//@formatter:off
		assertEquals(
				3333.00, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		
		
	}

	@Test
	public void testGrossI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				getToday(),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		int monthDays = AonDateUtils.getMax(getToday(), DATE) ;
		int workDays = AonDateUtils.getMax(getToday(), DATE) - get(getToday(), DATE) +1;
		
		//@formatter:off
		assertEquals(
				3333.00 * workDays / monthDays, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		System.out.println("BRUTO [3333.00]: " + (3333.00 * workDays / monthDays));
		
		
	}

	@Test
	public void testGrossII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		Date startIt = getToday();
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		int monthDays = AonDateUtils.getMax(getToday(), DATE) ;
		int workDays = get(getToday(), DATE)-1;
		
		//@formatter:off
		assertEquals(
				3333.00 * workDays / monthDays, 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		System.out.println("BRUTO [3333.00]: " + (3333.00 * workDays / monthDays));
		
		
	}

	@Test
	public void testGrossIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

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
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						"TRACE('DIAS_TRABAJADOS = %f\r\n',DIAS_TRABAJADOS);0.00"
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		Date startIt = getToday();
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				startIt,
				null, 
				null);
		
		PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format("500 * 0.90 * %s_4_15",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("500 * 0.90 * %s_16_20",  COMMON_DISEASE_DAYS));
		addPayment(aonContext, contract, prestIT, String.format("500 * 0.90 * %s_21",  COMMON_DISEASE_DAYS));

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, criteria);
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
//		for(com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
//			System.out.println(payment.getName() +  " = " + payment.getAmount() + " (" + payment.getExpression() +")");

		int monthDays = AonDateUtils.getMax(getToday(), DATE) ;
		int workDays = get(getToday(), DATE)-1;
		
		//@formatter:off
		assertEquals(
				(3333.00 * workDays / monthDays)
				+(500 * 0.90) * Math.max( monthDays - workDays -3, 0), 
				salary.getTotalPayment() 
				, DELTA);
		//@formatter:on
		System.out.println("BRUTO [3333.00]: " + (3333.00 * workDays / monthDays) + ", " + salary.getTotalPayment()  );
		
		
	}
	
	@Test
	public void testGrossExtrasProrratedI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord pagaExtraConcept = addConcept(aonContext, "PAGA_EXTRA");
		PaymentConceptRecord salarioBaseConcept = addConcept(aonContext, "SALARIO_BASE");
		// @formatter:off
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				
				new Extra[] { new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "SALARIO_BASE";
						this.expression = "0.00";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.concept = pagaExtraConcept.getId();
						this.expression = "SALARIO_BASE"; 
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, },
				
				new Payment [] {
						new Payment() {
						{
							this.concept = salarioBaseConcept.getId();
							this.expression = "1500.00 * DIAS_TRABAJADOS / DIAS_MES";
						}
					}
				}
		
			);

		
		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
				}, new String[] { 
						"BRUTO(3333.00 * DIAS_TRABAJADOS / DIAS_MES)" ,
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						"BASE_IRPF * PORCENTAJE_IRPF/100" }, category);
		//@formatter:on
		
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(),pagaExtraConcept , "PAGA EXTRARODINARIA NAVIDAD", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004);
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(),pagaExtraConcept , "PAGA EXTRARODINARIA VERANO", "SALARIO_BASE", "_P", "_P", PaymentType.CRA_0004);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		for ( SalaryPayment p: salary.getSalaryPayments() ) {
			System.out.println(p.getName() + ":" + p.getAmount() );
		}
		
		//@formatter:off
		assertEquals(
				3333.00 , 
				salary.getTotalPayment() 
				, DELTA);
		
		
		
		//@formatter:on
		
		
	}
	
	@Test
	public void testExtrasAtSalaryI() throws ExpressionException,
			SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "1000.00 * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "BRUTO(1100.00 * DIAS_TRABAJADOS/DIAS_MES)";
							}
						},
						new Payment() {
							{
								this.month = Month.MARCH;
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE";
							}
						},
						new Payment() {
							{
								this.month = Month.JULY;
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							}
						},
						new Payment() {
							{
								this.month = Month.DECEMBER;
								this.concept = conceptPagaExtra.getId();
								this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							}
						}
				});
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.JUNE);
		calendar.set(DAY_OF_MONTH, 1);
		Date contractDate = new Date(calendar.getTimeInMillis());

		ContractRecord contract = newContract(
				aonContext
				,new String[] {} 
				,new String[] {} 
				,category);
		//@formatter:off
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));

		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		assertEquals(1100.00, salary.getTotalPayment(),ContextVariable.TOTAL_PAYMENT.getName());
		assertEquals(1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA,ContextVariable.CGC_BASE.getName());
		
		// 
		// MARCH
		//
		startDate = add(startDate, Calendar.MONTH, 2);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		assertEquals(1100.00 + 1000.00 * 3 / 12.00, salary.getTotalPayment(),ContextVariable.TOTAL_PAYMENT.getName());
		assertEquals(1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA,ContextVariable.CGC_BASE.getName());
		
		// 
		// JULY
		//
		startDate = add(startDate, Calendar.MONTH, 4);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		assertEquals(1100.00 + 1100.00 * 7 / 12.00, salary.getTotalPayment(),ContextVariable.TOTAL_PAYMENT.getName());
		assertEquals(1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA,ContextVariable.CGC_BASE.getName());

		// 
		// DECEMBER
		//
		startDate = add(startDate, Calendar.MONTH, 5);
		endDate = getLastDayOfMonth(startDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder())
		.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for ( SalaryPayment payment : salary.getSalaryPayments() ) 
			System.out.println(payment.getDescription() + " = " + payment.getAmount() +", " + payment.getQuote());
		
		assertEquals(1100.00 + 1100.00, salary.getTotalPayment(),ContextVariable.TOTAL_PAYMENT.getName());
		assertEquals(1100.00 + ( 1100.00/6.00 ) + 1000.00/12.00, salary.getCommonBase(), DELTA,ContextVariable.CGC_BASE.getName());
	}
	
	// ------------------------------------------------------------------------


}
