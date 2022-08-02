package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0001;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfTestCase;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLDraftIrpfTestCase extends SQLIrpfTestCase {
	
	@Test
	@Override
	public void testExtrasI() throws ExpressionException, SQLException, SalaryException {
	}

	@Test
	@Override
	public void testTotalLiquid() throws ExpressionException, SQLException {
	}
	
	@Override
	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, ContractRecord contract) throws ExpressionException,
			SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		int months  = 12 /*- get(startDate,Calendar.MONTH)*/;

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);

		
		for ( int i = 0; i <  6; i ++ ) {
			com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
			draftPayment.setStartDate(startDate);
			draftPayment.setEndDate(null);
			draftPayment.setExpression("66666.00/(6.00 * " + months + ") * DIAS_TRABAJADOS / DIAS_MES");
			draftPayment.setIrpfExpression("_P");
			draftPayment.setQuoteExpression("_P");
			draftPayment.setSalaryType(Salary.Type.SALARY);
			draftPayment
					.setDescription("RETRIBUCION NO INCLUIDA EN OTROS APARTADOS");
			draftPayment
					.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0001);
	
			draft.addDraftPayment(draftPayment);
		}

		return EmployeesServiceHelper.getSalaryCalculatorContext(connection, draft, null);
		
	}
//	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
//			Connection connection, Date startDate, Date endDate,
//			Date issueDate, Criteria criteria) throws ExpressionException,
//			SQLException {
//
//		SalaryDraft draft = new SalaryDraft();
//
//		
//		for ( int i = 0; i <  6; i ++ ) {
//			com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
//			draftPayment.setStartDate(startDate);
//			draftPayment.setEndDate(endDate);
//			draftPayment.setExpression("66666/6");
//			draftPayment.setIrpfExpression("_P");
//			draftPayment.setQuoteExpression("_P");
//			draftPayment.setSalaryType(Salary.Type.SALARY);
//			draftPayment
//					.setDescription("RETRIBUCION NO INCLUIDA EN OTROS APARTADOS");
//			draftPayment
//					.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0001);
//	
//			draft.addDraftPayment(draftPayment);
//		}
//
//
//		SQLSalaryDraftCalculatorContext draftCtx = new SQLSalaryDraftCalculatorContext(
//				draft, connection, startDate, endDate, issueDate, criteria);
//		
//		draftCtx.next();
//		
//		return draftCtx;
//	}

	@Test
	public void testSettleWithHolidays() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				add(getToday(), Calendar.YEAR, -10),
				getLastDayOfMonth(getToday()),
				Collections.emptyMap(),
				new String[] {
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES"	
				},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}
				, null
				);

		
		Date start = contract.getStartDate();
		Date end = contract.getEndDate();
		Date issue = contract.getEndDate();
		;
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				null, ///contract.getEndDate(),  
				"VACACIONES RETRIBUIDAS NO DISFRUTADAS", 
				"/*read-only*/DIAS_VACACIONES_NO_DISFRUTADOS * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )/**/ ", 
				"_P", 
				"_P", 
				PaymentType.CRA_0006,
				SalaryType.SETTLE);

		Date noHolidaysStart = add(contract.getEndDate(), Calendar.DATE, 1);
		Date noHolidaysEnd = add(contract.getEndDate(), Calendar.DATE, 15);
//		
//		addData(aonContext, contract, noHolidaysStart , noHolidaysEnd, ContextVariable.NO_HOLIDAYS, "15");
		
		SalaryDraft draft = new SalaryDraft();
		
		NumberVariable noHolidaysVariable = new NumberVariable();
		noHolidaysVariable.setName(ContextVariable.NO_HOLIDAYS.getName());
		noHolidaysVariable.setValue(15.00);
		noHolidaysVariable.setScope(Scope.SALARY);
		noHolidaysVariable.setStartDate(noHolidaysStart);
		noHolidaysVariable.setEndDate(noHolidaysEnd);

		draft.addDraftVariable(noHolidaysVariable);

		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		SQLSettleDraftCalculatorContext draftCtx = new SQLSettleDraftCalculatorContext(
				draft, connection, contract.getStartDate(), noHolidaysEnd, contract.getEndDate(), criteria);

		draftCtx.next();

		try {
			com.esferalia.aon.payroll.Salary salary = 
			new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>( new SalaryBuilder()).calculate(draftCtx);
			for ( SalaryPayment p : salary.getSalaryPayments() ) {
				System.out.println(p.getExpression() + ": " + p.getAmount() );
			}
			for ( SalaryDeduction d : salary.getSalaryDeductions() ) {
				System.out.println(d.getExpression() + ": " + d.getAmount() );
			}
			org.junit.Assert.assertEquals(3, salary.getSalaryDeductions().size());
			
		} catch (SalaryException e1) {
		}
		
	}

	@Override
	protected ISQLContractSalaryCalculatorContext getContractSettleCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {

		SalaryDraft draft = new SalaryDraft();

		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();

		draftPayment.setStartDate(startDate);
		draftPayment.setEndDate(endDate);
		draftPayment.setExpression("66666");
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setSalaryType(Salary.Type.SETTLE);
		draftPayment.setDescription("VACACIONES RETRIBUIDAS NO DISFRUTADAS");
		draftPayment
				.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0006);

		draft.addDraftPayment(draftPayment);

		SQLSettleDraftCalculatorContext draftCtx = new SQLSettleDraftCalculatorContext(
				draft, connection, startDate, endDate, issueDate, criteria);

		draftCtx.next();
		return draftCtx;
	}
	
	@Override
	protected void assertIrpf(double expected, double base, double percent, double delta) {
		super.assertIrpf(expected, base + ( 66666.00 / 12 ), percent, delta);
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration) {
		super.assertAnnualRemuneration(expected + 66666.00, annualRemuneration);
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, double delta) {
		super.assertAnnualRemuneration(expected + 66666.00, annualRemuneration, delta);
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration, int months, double delta) {
		super.assertAnnualRemuneration(expected + 66666.00 /*+ (66666.00 / (6.00 * 12 ) * 12)*/, annualRemuneration, delta);
	}

	@Override
	protected void assertDeduccibleExpenses(double expected,
			double deduccibleExpenses) {
		super.assertDeduccibleExpenses(expected + (66666.00 * 0.15), deduccibleExpenses);
	}
	
	@Test
	public void testBRUTO() throws ExpressionException, SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext,
				AonDateUtils.getFirstDayOfYear(getToday()),
				Collections.emptyMap(),
				new String[] {
					"1500.00 * DIAS_TRABAJADOS / DIAS_MES"	
				},
				new String[] { 
						"BASE_CGC * 0.10", "BASE_CGP * 0.05",
						"BASE_ESTR * 0.10", "BASE_NESTR * 0.20",
						"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}
				, null
				);

		
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		

		Date startDate = AonDateUtils.getFirstDayOfMonth(getToday());
		Date endDate = AonDateUtils.getLastDayOfMonth(startDate);
		Date issueDate = AonDateUtils.getLastDayOfMonth(startDate);

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);
		
		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
		draftPayment.setStartDate(startDate);
		draftPayment.setEndDate(endDate);
		draftPayment.setExpression("BRUTO(3000.00 * DIAS_TRABAJADOS / DIAS_MES)");
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setSalaryType(Salary.Type.SALARY);
		draftPayment.setDescription("RETRIBUCION NO INCLUIDA EN OTROS APARTADOS");
		draftPayment.setType(CRA_0001);

		draft.addDraftPayment(draftPayment);
		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx =
		EmployeesServiceHelper.getSalaryCalculatorContext(connection, draft, null);
		draftCtx.setListener(irpf -> {			
//			org.junit.Assert.assertEquals(1500.00*2*12, irpf.getIrpfResult().getAnnualRemuneration(), 0.001 );
			System.out.println("AnnualRemuneration : " + irpf.getIrpfResult().getAnnualRemuneration());
		});

		try {
			com.esferalia.aon.payroll.Salary salary = 
			new SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary>( new SalaryBuilder()).calculate(draftCtx);
			for ( SalaryPayment p : salary.getSalaryPayments() ) {
				System.out.println(p.getExpression() + ": " + p.getAmount() );
			}
			for ( SalaryDeduction d : salary.getSalaryDeductions() ) {
				System.out.println(d.getExpression() + ": " + d.getAmount() );
			}
			org.junit.Assert.assertEquals(1500.00*2, salary.getTotalPayment(), 0.001 );
			
		} catch (SalaryException e1) {
		}
		
	}
	
	@Test
	@Override
	public void testDeductHomeLoan() throws ExpressionException, SQLException, SalaryException {
		
	}

	
}
