/**
 * 
 */
package com.esferalia.aon.payroll.calculator.junit;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractLeaveLoader;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class RegulatoryBaseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testStandardBr() throws Exception{

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		MockSalaryBuilder builder = new MockSalaryBuilder();
		calculator.setSalaryBuilder(builder);

		MockContractSalaryCalculatorContext ctx = new MockContractSalaryCalculatorContext(){
			@Override
			public ISalary getSalary(Date date) {
				Salary salary = new Salary();
				salary.setTimeUnits( 30 );
				salary.setCommonBase(1000d);
				return salary;
			}
		};

		ContractPayment payment = new ContractPayment();
		payment.setExpression("1000");
		payment.setSalaryType(SalaryType.SALARY);
		payment.setStartDate(ctx.getStartDate());
		ctx.addContractPayment(payment);
		
		
		assertEquals("", ctx.br(ctx.getStartDate()), 1000d/30);

		
		
		/*
		ContractLeaveLoader contractLeaveLoader = new ContractLeaveLoader(
				ctx.getStartDate(), ctx.getEndDate());
		contractLeaveLoader.loadContractLeave(0, ctx.getStartDate(),
				ctx.getEndDate(), 0, LeaveType.COMMON_DISEASE, null,
				ctx.getExpressionContext());
		*/
		//calculator.calculate(ctx);

		//assertEquals("", 1000d, (double) builder.getTotalPayment(), 0);
	}

	//@Test
	public void testUndefinedBr() throws Exception{

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		MockSalaryBuilder builder = new MockSalaryBuilder();
		calculator.setSalaryBuilder(builder);

		MockContractSalaryCalculatorContext ctx = new MockContractSalaryCalculatorContext();

		ContractPayment payment = new ContractPayment();
		payment.setExpression("1000");
		payment.setSalaryType(SalaryType.SALARY);
		payment.setStartDate(ctx.getStartDate());
		ctx.addContractPayment(payment);
		
		try {
			ctx.br(ctx.getStartDate());
		} catch ( UndefinedContextVariablesException e ){
			assertEquals("", e.getVariableNames()[0], ContextVariable.REGULATORY_BASE.getName());
		}
		

		
	}

	@Test
	public void testStandardCalcBr() throws Exception{

		ContractSalaryCalculator calculator = new ContractSalaryCalculator();
		MockSalaryBuilder builder = new MockSalaryBuilder();
		calculator.setSalaryBuilder(builder);

		MockContractSalaryCalculatorContext ctx = new MockContractSalaryCalculatorContext(){
			@Override
			public IContractSalaryCalculatorContext getContractSalaryCalculatorContext(
					Date startDate, Date endDate) {
				MockContractSalaryCalculatorContext ctx = new MockContractSalaryCalculatorContext();
				ctx.setStartDate(startDate);
				ctx.setEndDate(endDate);
				
				ContractPayment payment = new ContractPayment();
				payment.setExpression("1000");
				payment.setType(PaymentType.CRA_0001);
				payment.setQuoteExpression("_P");
				payment.setSalaryType(SalaryType.SALARY);
				payment.setStartDate(ctx.getStartDate());
				ctx.addContractPayment(payment);

				return ctx;
			}
		};

		ContractPayment payment = new ContractPayment();
		payment.setExpression("1000");
		payment.setSalaryType(SalaryType.SALARY);
		payment.setStartDate(ctx.getStartDate());
		ctx.addContractPayment(payment);
		
		Date startDate = DateUtils.addDays(ctx.getStartDate(), 10);
		ContractLeaveLoader contractLeaveLoader = new ContractLeaveLoader(
				ctx.getStartDate(), ctx.getEndDate());
		contractLeaveLoader.loadContractLeave(0, startDate,
				ctx.getEndDate(), 0, LeaveType.COMMON_DISEASE, "0.00",
				ctx.getExpressionContext());
		
		assertEquals("", 1000d/10, ctx.br(startDate));

		
		
		//calculator.calculate(ctx);

		//assertEquals("", 1000d, (double) builder.getTotalPayment(), 0);
	}
}
