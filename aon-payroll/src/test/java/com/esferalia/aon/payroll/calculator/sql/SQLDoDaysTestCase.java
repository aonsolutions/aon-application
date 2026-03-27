/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * @author rtrepiana
 *
 */
public class SQLDoDaysTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;


	@Test
	public void testDoDays() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"66.66 * JORNADAS_REALES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 14);
		
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "15");
		
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		assertEquals(66.66 * 15 , salary.getTotalPayment(), 0.00);
		assertEquals(66.66 * 15 , salary.getCommonBase(), 0.00);
		
		
		
	}

	@Test
	public void testDoDaysITI() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"66.66 * JORNADAS_REALES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		PaymentConceptRecord directIT = addConcept(aonContext, DIRECT_PAY.getName(),PaymentType.CRA_0000);
		addPayment(aonContext, contract, directIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_1_3 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		addPayment(aonContext, contract, directIT, 
				String.format("BASE_REGULADORA * 0.00 * %s_4_15 * (isdef %s ? %s : 1.00)",  COMMON_DISEASE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR),
				String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)",  QUOTE_DAYS, LEAVE_FACTOR, LEAVE_FACTOR)
				);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 14);
		
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "15");
		
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(doDaysStartDate, Calendar.DAY_OF_MONTH,1), doDaysEndDate, /*66.66*/null);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		assertEquals(66.66 , salary.getTotalPayment(), 0.00);
		assertEquals(66.66 * 15 , salary.getCommonBase(), 0.00);
	}

	@Test
	public void testDoDaysITBaseCgcMin() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"11.11 * JORNADAS_REALES",
				"FRACCIONAR(33.33)",
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "COTIZACION_MENSUAL", "FALSO()");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "BASE_CGC_MIN_MES", "1260.00 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "BASE_CGC_MIN_DIA", "100.00* JORNADAS_REALES");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "BASE_CGC_MIN", "( COTIZACION_MENSUAL ) ? BASE_CGC_MIN_MES : BASE_CGC_MIN_DIA");
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 14);
		
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "15");
		
//		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, add(doDaysStartDate, Calendar.DAY_OF_MONTH,1), doDaysEndDate, /*66.66*/null);
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			System.out.println(payment.getDescription() +" : " + amount +", " + quote + "[" + startDate + ".." + endDate +"]" );
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		}).calculate(ctx);
		
		//salary.getSalaryDatas().forEach( d -> System.out.println(d.getName() +" " + d.getStartDate() ));
		
		assertEquals(11.11 * 15 + 33.33, salary.getTotalPayment(), 0.00);
		assertEquals(100.00 * 15 , salary.getCommonBase(), 0.00);
	}

	@Test
	public void testDoDaysITBaseCgcMinConstant() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"11.11 * JORNADAS_REALES",
				"33.33",
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * 0.00/100.00" 
				},
				null);
		//@formatter:on
		
		addSystemData(aonContext, contract.getStartDate(), contract.getEndDate(), Collections.singletonMap("BASE_CGC_MIN", " 3.00  * JORNADAS_REALES"));
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 14);
		
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "15");
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			System.out.println(payment.getDescription() +" : " + amount +", " + quote + "[" + startDate + ".." + endDate +"]" );
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		}).calculate(ctx);
		
		//salary.getSalaryDatas().forEach( d -> System.out.println(d.getName() +" " + d.getStartDate() ));
		
		assertEquals(11.11 * 15 + 33.33, salary.getTotalPayment(), 0.00);
		assertEquals(11.11 * 15 + 33.33 , salary.getCommonBase(), DELTA);
	}

	@Test
	public void testDoDaysITBaseCgcMinConstantI() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"11.11 * JORNADAS_REALES",
				"33.33",
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * 0.00/100.00" 
				},
				null);
		//@formatter:on
		
		addSystemData(aonContext, contract.getStartDate(), contract.getEndDate(), Collections.singletonMap("BASE_CGC_MIN", " 3.00  * JORNADAS_REALES"));
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");

		doDaysStartDate = add(doDaysEndDate, Calendar.DATE, 1);
		doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");
		
		doDaysStartDate = add(doDaysEndDate, Calendar.DATE, 1);
		doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			System.out.println(payment.getDescription() +" : " + amount +", " + quote + "[" + startDate + ".." + endDate +"]" );
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		}).calculate(ctx);
		
		assertEquals(11.11 * 15 + 33.33, salary.getTotalPayment(), DELTA);
		assertEquals(11.11 * 15 + 33.33 , salary.getCommonBase(), DELTA);
	}


	@Test
	public void testDoDaysITBaseCgcMinI() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"11.11 * JORNADAS_REALES",
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * 0.00/100.00" 
				},
				null);
		//@formatter:on
		
		addSystemData(aonContext, contract.getStartDate(), contract.getEndDate(), Collections.singletonMap("BASE_CGC_MIN", " 100.00  * JORNADAS_REALES"));
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");

		doDaysStartDate = add(doDaysEndDate, Calendar.DATE, 1);
		doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");
		
		doDaysStartDate = add(doDaysEndDate, Calendar.DATE, 1);
		doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			System.out.println(payment.getDescription() +" : " + amount +", " + quote + "[" + startDate + ".." + endDate +"]" );
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		}).calculate(ctx);
		
		assertEquals(11.11 * 15, salary.getTotalPayment(), DELTA);
		assertEquals(100.00 * 15 , salary.getCommonBase(), DELTA);
	}

	@Test
	public void testDoDaysITBaseCgcMinConstantII() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				Collections.emptyMap(),
				new String[] {
				"11.11 * SUM(JORNADAS_REALES)",
				"33.33"
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * 0.00/100.00" 
				},
				null);
		//@formatter:on
		
		addSystemData(aonContext, contract.getStartDate(), contract.getEndDate(), Collections.singletonMap("BASE_CGC_MIN", " 100.00  * SUM(JORNADAS_REALES)"));
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		Date doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");

		doDaysStartDate = add(doDaysEndDate, Calendar.DATE, 1);
		doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");
		
		doDaysStartDate = add(doDaysEndDate, Calendar.DATE, 1);
		doDaysEndDate = add(doDaysStartDate, Calendar.DATE, 4);
		addData(aonContext, contract, doDaysStartDate, doDaysEndDate, ContextVariable.DO_DAYS, "5");
		
		;
		
		addPayment(aonContext, 
			contract, 
			contract.getStartDate(), 
			contract.getEndDate(), 
			addConcept(aonContext, "FIX_BASE_CGC_MIN"), 
			"COTIZACIÓN MÍNIMA POR CONTINGENCIAS COMUNES :-)", 
			"/*default*/(/*user*/0.00/**/)", 
			"/*fixBaseCgcMin*/BASE_CGP_BRUTA=BASE_CGP=MAX(_B,_A);_P", 
			"/*fixBaseCgcMin*/_A=BASE_CGP;_B=BASE_CGP_BRUTA;MAX(_P,(BASE_CGC - BASE_CGC_BRUTA))", 
			PaymentType.CRA_0001 
			);

		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder() {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment,
		            Map<String, ITimedVariable<?>> context) {
			System.out.println(payment.getDescription() +" : " + amount +", " + quote + "[" + startDate + ".." + endDate +"]" );
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		}).calculate(ctx);
		
		assertEquals(11.11 * 15 + 33.33, salary.getTotalPayment(), DELTA);
		assertEquals(100.00 * 15 , salary.getCommonBase(), DELTA);
	}

	@Test
	public void testDoDaysTimeUnits() throws ExpressionException,
			SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Date contractStartDate = getFirstDayOfMonth(getToday());
		Date contractEndDate = add(contractStartDate, Calendar.DAY_OF_MONTH, 20);
		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				contractEndDate,
				Collections.emptyMap(),
				new String[] {
				"66.66 * JORNADAS_REALES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		Date doDaysStartDate = add(startDate, Calendar.DATE, 9);
		
		addData(aonContext, contract, doDaysStartDate, null, ContextVariable.DO_DAYS, "15");
		
		
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(
				connection, 
				startDate,
				contractEndDate,
				endDate,
				endDate,
				contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		assertEquals(15 , salary.getTimeUnits(), 0.00);
		
		
		
	}

}