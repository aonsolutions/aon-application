package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.watson.server.AonDateUtils.addDays;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.DeferredExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionImpl;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.server.AonDateUtils;

public class QuoteCalculatorTestCase {

	@Test
	public void testQuoteI() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		ContractPayment payment = new ContractPayment();
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("66.50 + 1.25 + 1.25 + 0.6969");
		quoteCalculator.quote(payment, startDate, endDate, 100.00);
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(1, cgcBases.size());
	
			assertEquals( startDate, cgcBases.get(0).getPeriod().getStart());
			assertEquals( endDate, cgcBases.get(0).getPeriod().getEnd());
			assertEquals( 69.6969, cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));
		}
		assertEquals(69.6969, quoteCalculator.getRawCgcBase());
		
//		assertEquals(1000.00, quoteCalculator.getCgcBase());
		
		
		
	}

	@Test
	public void testQuoteII() throws AonException {
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		ContractPayment payment = new ContractPayment();
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("33.33");
		quoteCalculator.quote(payment, startDate, addDays(startDate, 14), 100.00);
		
		payment = new ContractPayment();
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("66.66");
		quoteCalculator.quote(payment, addDays(startDate, 15), endDate, 100.00);
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
		List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(2, cgcBases.size());
			
			assertEquals( startDate, cgcBases.get(0).getPeriod().getStart());
			assertEquals( addDays(startDate, 14), cgcBases.get(0).getPeriod().getEnd());
			assertEquals( 33.33, cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));
	
			assertEquals( addDays(startDate, 15), cgcBases.get(1).getPeriod().getStart());
			assertEquals( endDate, cgcBases.get(1).getPeriod().getEnd());
			assertEquals( 66.66, cgcBases.get(1).getValue(cgcBases.get(1).getPeriod()));
		}
		assertEquals(66.66 + 33.33, quoteCalculator.getRawCgcBase());
	}

	@Test
	public void testQuoteIII() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		long days = AonDateUtils.getDaysBetweenDates(startDate, endDate);
		for ( int i = 0; i< days; i++){
			ContractPayment payment = new ContractPayment();
			payment.setType(PaymentType.CRA_0001);
			payment.setSalaryType(SalaryType.SALARY);
			payment.setQuoteExpression(String.format("%d", i));
			Date date = addDays(startDate, i);
			System.out.println("Quote :" + i +", " + date);
			quoteCalculator.quote(payment, date, date, 100.00);
		}
		
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(days, cgcBases.size());
			for ( int i = 0; i< days; i++){
				Date date = addDays(startDate, i);
				assertEquals( date, cgcBases.get(i).getPeriod().getStart());
				assertEquals( date, cgcBases.get(i).getPeriod().getEnd());
				assertEquals( Math.min(Math.max((double)i,1.00), 20.00), cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
				System.out.println(i +"-." + date);
			}
		}
		double total = 0.00;
		for ( int i = 0; i< days; i++)
			total += i;
		
		assertEquals(total, quoteCalculator.getRawCgcBase());

	}

	@Test
	public void testQuoteIV() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		long days = AonDateUtils.getDaysBetweenDates(startDate, endDate);
		for ( int i = 0; i< days; i++){
			ContractPayment payment = new ContractPayment();
			payment.setType(PaymentType.CRA_0001);
			payment.setSalaryType(SalaryType.SALARY);
			payment.setQuoteExpression(String.format("%d", i));
			Date date = addDays(startDate, i);
			quoteCalculator.quote(payment, startDate, endDate, 100.00);
		}
		
		double total = 0.00;
		for ( int i = 0; i< days; i++)
			total += i;
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(1, cgcBases.size());
			assertEquals(startDate, cgcBases.get(0).getPeriod()
					.getStart());
			assertEquals(endDate, cgcBases.get(0).getPeriod().getEnd());
			assertEquals(total,
					cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));
		}
		assertEquals(total, quoteCalculator.getRawCgcBase());
	}
	
	@Test
	public void testQuoteV() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		long days = AonDateUtils.getDaysBetweenDates(startDate, endDate);
		for ( int i = 0; i< days; i++){
			Date date = addDays(startDate, i);

			ContractPayment baseSalary = new ContractPayment();
			baseSalary.setType(PaymentType.CRA_0001);
			baseSalary.setSalaryType(SalaryType.SALARY);
			baseSalary.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(baseSalary, date, date, 100.00);
			
			// Extra 
			ContractPayment extraPay = new ContractPayment();
			extraPay.setType(PaymentType.CRA_0004);
			extraPay.setSalaryType(SalaryType.EXTRA);
			extraPay.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(extraPay,date, date, 100.00);

		}
		
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(days, cgcBases.size());
			for ( int i = 0; i< days; i++){
				Date date = addDays(startDate, i);
				assertEquals( date, cgcBases.get(i).getPeriod().getStart());
				assertEquals( date, cgcBases.get(i).getPeriod().getEnd());
				assertEquals( Math.min(Math.max((double)i * 2.00,1.00),20.00), cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
				System.out.println(i +"-." + date +", " + cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
			}
		}
		double total = 0.00;
		for ( int i = 0; i< days; i++)
			total += 2*i;
		
		assertEquals(total, quoteCalculator.getRawCgcBase());
		assertEquals(total, quoteCalculator.getRawCgpBase());
		assertEquals(total/2, quoteCalculator.getProExtBase());
	}

	@Test
	public void testQuoteVI() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		long days = AonDateUtils.getDaysBetweenDates(startDate, endDate);
		for ( int i = 0; i< days; i++){
			Date date = addDays(startDate, i);

			ContractPayment baseSalary = new ContractPayment();
			baseSalary.setType(PaymentType.CRA_0001);
			baseSalary.setSalaryType(SalaryType.SALARY);
			baseSalary.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(baseSalary, startDate, endDate, 100.00);
		
			ContractPayment extra = new ContractPayment();
			extra.setType(PaymentType.CRA_0004);
			extra.setSalaryType(SalaryType.EXTRA);
			extra.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(extra, startDate, endDate, 100.00);
		}
		
		double total = 0.00;
		for ( int i = 0; i< days; i++)
			total += 2* i;
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(1, cgcBases.size());
			assertEquals(startDate, cgcBases.get(0).getPeriod()
					.getStart());
			assertEquals(endDate, cgcBases.get(0).getPeriod().getEnd());
			assertEquals(30.00*20.00,
					cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));
		}
		assertEquals(total, quoteCalculator.getRawCgcBase());
		assertEquals(total, quoteCalculator.getRawCgpBase());
		assertEquals(total/2, quoteCalculator.getProExtBase());
	}
	
	@Test
	public void testQuoteVII() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		long days = AonDateUtils.getDaysBetweenDates(startDate, endDate);
		for ( int i = 0; i< days; i++){
			Date date = addDays(startDate, i);

			ContractPayment baseSalary = new ContractPayment();
			baseSalary.setType(PaymentType.CRA_0001);
			baseSalary.setSalaryType(SalaryType.SALARY);
			baseSalary.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(baseSalary, date, date, 100.00);
			
			// Extra 
			ContractPayment extraPay = new ContractPayment();
			extraPay.setType(PaymentType.CRA_0004);
			extraPay.setSalaryType(SalaryType.EXTRA);
			extraPay.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(extraPay,date, date, 100.00);

			// Structural Overtime 
			ContractPayment structural = new ContractPayment();
			structural.setType(PaymentType.CRA_0003);
			structural.setSalaryType(SalaryType.SALARY);
			structural.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(structural,date, date, 100.00);

			// Structural Overtime 
			ContractPayment nonStructural = new ContractPayment();
			nonStructural.setType(PaymentType.CRA_0002);
			nonStructural.setSalaryType(SalaryType.SALARY);
			nonStructural.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(nonStructural,date, date, 100.00);
		}
		
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(days, cgcBases.size());
			for ( int i = 0; i< days; i++){
				Date date = addDays(startDate, i);
				assertEquals( date, cgcBases.get(i).getPeriod().getStart());
				assertEquals( date, cgcBases.get(i).getPeriod().getEnd());
				assertEquals( Math.min(Math.max((double)i * 2.00, 1.00), 20.00), cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
				System.out.println(i +"-." + date +", " + cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
			}
		}

		for ( ContextVariable base : new ContextVariable[]{NON_STRUCTURAL_OVERTIME_BASE, STRUCTURAL_OVERTIME_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(days, cgcBases.size());
			for ( int i = 0; i< days; i++){
				Date date = addDays(startDate, i);
				assertEquals( date, cgcBases.get(i).getPeriod().getStart());
				assertEquals( date, cgcBases.get(i).getPeriod().getEnd());
				assertEquals( (double)i * 1.00, cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
				System.out.println(i +"-." + date +", " + cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
			}
		}
		
		for ( ContextVariable base : new ContextVariable[]{CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(days, cgcBases.size());
			for ( int i = 0; i< days; i++){
				Date date = addDays(startDate, i);
				assertEquals( date, cgcBases.get(i).getPeriod().getStart());
				assertEquals( date, cgcBases.get(i).getPeriod().getEnd());
				assertEquals( Math.min(Math.max((double)i * 4.00, 1.00),20.00), cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
				System.out.println(i +"-." + date +", " + cgcBases.get(i).getValue(cgcBases.get(i).getPeriod()));
			}
		}
		
		double total = 0.00;
		for ( int i = 0; i< days; i++)
			total += 4*i;
		
		assertEquals(total/2, quoteCalculator.getRawCgcBase());
		assertEquals(total, quoteCalculator.getRawCgpBase());
		assertEquals(total/4, quoteCalculator.getProExtBase());
		assertEquals(total/4, quoteCalculator.getStructuralBase());
		assertEquals(total/4, quoteCalculator.getNonStructuralBase());
	}

	
	@Test
	public void testQuoteVIII() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		long days = AonDateUtils.getDaysBetweenDates(startDate, endDate);
		for ( int i = 0; i< days; i++){
			Date date = addDays(startDate, i);

			ContractPayment baseSalary = new ContractPayment();
			baseSalary.setType(PaymentType.CRA_0001);
			baseSalary.setSalaryType(SalaryType.SALARY);
			baseSalary.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(baseSalary, startDate, endDate, 100.00);
		
			ContractPayment extra = new ContractPayment();
			extra.setType(PaymentType.CRA_0004);
			extra.setSalaryType(SalaryType.EXTRA);
			extra.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(extra, startDate, endDate, 100.00);

			ContractPayment structural = new ContractPayment();
			structural.setType(PaymentType.CRA_0003);
			structural.setSalaryType(SalaryType.SALARY);
			structural.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(structural, startDate, endDate, 100.00);

			ContractPayment nonStructural = new ContractPayment();
			nonStructural.setType(PaymentType.CRA_0002);
			nonStructural.setSalaryType(SalaryType.SALARY);
			nonStructural.setQuoteExpression(String.format("%d", i));
			quoteCalculator.quote(nonStructural, startDate, endDate, 100.00);
		}
		
		double total = 0.00;
		for ( int i = 0; i< days; i++)
			total += 4* i;
		
		List<ITimedVariable<Double>> cgcBases = ctx.getVariables(CGC_BASE);
		assertEquals(1, cgcBases.size());
		assertEquals(startDate, cgcBases.get(0).getPeriod()
				.getStart());
		assertEquals(endDate, cgcBases.get(0).getPeriod().getEnd());
		assertEquals(30.00 * 20.00,
				cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));

		List<ITimedVariable<Double>> cgpBases = ctx.getVariables(CGP_BASE);
		assertEquals(1, cgpBases.size());
		assertEquals(startDate, cgpBases.get(0).getPeriod()
				.getStart());
		assertEquals(endDate, cgpBases.get(0).getPeriod().getEnd());
		assertEquals(30.00 * 20.00,
				cgpBases.get(0).getValue(cgpBases.get(0).getPeriod()));

		List<ITimedVariable<Double>> structuralBases = ctx.getVariables(STRUCTURAL_OVERTIME_BASE);
		assertEquals(1, structuralBases.size());
		assertEquals(startDate, structuralBases.get(0).getPeriod()
				.getStart());
		assertEquals(endDate, structuralBases.get(0).getPeriod().getEnd());
		assertEquals(total/4,
				structuralBases.get(0).getValue(structuralBases.get(0).getPeriod()));

		List<ITimedVariable<Double>> nonStructuralBases = ctx.getVariables(NON_STRUCTURAL_OVERTIME_BASE);
		assertEquals(1, nonStructuralBases.size());
		assertEquals(startDate, nonStructuralBases.get(0).getPeriod()
				.getStart());
		assertEquals(endDate, nonStructuralBases.get(0).getPeriod().getEnd());
		assertEquals(total/4,
				nonStructuralBases.get(0).getValue(nonStructuralBases.get(0).getPeriod()));

		assertEquals(total/2, quoteCalculator.getRawCgcBase());
		assertEquals(total, quoteCalculator.getRawCgpBase());
		assertEquals(total/4, quoteCalculator.getProExtBase());
		assertEquals(total/4, quoteCalculator.getStructuralBase());
		assertEquals(total/4, quoteCalculator.getNonStructuralBase());
	}
	
	@Test
	public void testQuoteIX() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		ContractPayment payment = new ContractPayment();
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("33.33");
		quoteCalculator.quote(payment, startDate, addDays(startDate, 14), 100.00);
		
		PaymentConcept concept = new PaymentConcept();
		concept.setCode(MATERNITY.getName());
		
		payment = new ContractPayment();
		payment.setPaymentConcept(concept);
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("66.66");
		quoteCalculator.quote(payment, addDays(startDate, 15), endDate, 100.00);
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(1, cgcBases.size());
			
			assertEquals( startDate, cgcBases.get(0).getPeriod().getStart());
			assertEquals( addDays(startDate, 14), cgcBases.get(0).getPeriod().getEnd());
			assertEquals( 33.33, cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));
	
		}

		List<ITimedVariable<Double>> cgcBases = ctx.getVariables(MATERNITY_BASE);
		assertEquals(1, cgcBases.size());
		
		assertEquals( addDays(startDate, 15), cgcBases.get(0).getPeriod().getStart());
		assertEquals( endDate, cgcBases.get(0).getPeriod().getEnd());
		assertEquals( 66.66, cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));

		assertEquals(33.33, quoteCalculator.getRawCgcBase());
		assertEquals(33.33, quoteCalculator.getRawCgpBase());

		assertEquals(66.66, quoteCalculator.getMaternityBase());
	}

	@Test
	public void testQuoteX() throws AonException {
		
		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		ContractPayment payment = new ContractPayment();
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("33.33");
		quoteCalculator.quote(payment, startDate, addDays(startDate, 14), 100.00);
		
		PaymentConcept concept = new PaymentConcept();
		concept.setCode(ERE.getName());
		
		payment = new ContractPayment();
		payment.setPaymentConcept(concept);
		payment.setType(PaymentType.CRA_0001);
		payment.setSalaryType(SalaryType.SALARY);
		payment.setQuoteExpression("66.66");
		quoteCalculator.quote(payment, addDays(startDate, 15), endDate, 100.00);
		
		for ( ContextVariable base : new ContextVariable[]{CGC_BASE, CGP_BASE}) {
			List<ITimedVariable<Double>> cgcBases = ctx.getVariables(base);
			assertEquals(1, cgcBases.size());
			
			assertEquals( startDate, cgcBases.get(0).getPeriod().getStart());
			assertEquals( addDays(startDate, 14), cgcBases.get(0).getPeriod().getEnd());
			assertEquals( 33.33, cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));
	
		}

		List<ITimedVariable<Double>> cgcBases = ctx.getVariables(ERE_BASE);
		assertEquals(1, cgcBases.size());
		
		assertEquals( addDays(startDate, 15), cgcBases.get(0).getPeriod().getStart());
		assertEquals( endDate, cgcBases.get(0).getPeriod().getEnd());
		assertEquals( 66.66, cgcBases.get(0).getValue(cgcBases.get(0).getPeriod()));

		assertEquals(33.33, quoteCalculator.getRawCgcBase());
		assertEquals(33.33, quoteCalculator.getRawCgpBase());

		assertEquals(66.66, quoteCalculator.getEreBase());
	}
	
	@Test
	public void testQuoteXII() throws AonException {

		Date startDate = AonDateUtils.getMonthFirstDay(new Date());
		Date endDate = AonDateUtils.getMonthLastDay(startDate);
		ExpressionContext ctx = getExpressionContext(startDate, endDate);
		
		QuoteCalculator quoteCalculator = new QuoteCalculator.GeneralQuote(ctx, startDate, endDate);
		
		ContractPayment fixedPayment = new ContractPayment();
		fixedPayment.setType(PaymentType.CRA_0001);
		fixedPayment.setSalaryType(SalaryType.SALARY);
		fixedPayment.setQuoteExpression("10.00");
		
		ContractPayment itPayment = new ContractPayment();
		itPayment.setType(PaymentType.CRA_0001);
		itPayment.setSalaryType(SalaryType.SALARY);
		itPayment.setQuoteExpression("200.00");

		ExpressionImpl expression = new ExpressionImpl();
		expression.setName(CGC_BASE_MIN.getName());
		expression.setExpression("30000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
		ctx.addLazyExpression(expression, startDate, endDate);
		
		Double quote = quoteCalculator.qu0te(fixedPayment, startDate, endDate, 10.00);
		quote = quoteCalculator.qu0te(itPayment, startDate, startDate, 200.00);
		
//		assertEquals(CGC_BASE.name(), 1000.00, quote);
		
		List<ITimedResult<Double>> cgcBases = ctx.eval(CGC_BASE.getName(),startDate, endDate, Double.class);
		for ( ITimedResult<Double> cgcBase: cgcBases )
			System.out.println(CGC_BASE.name() + " = " + cgcBase.getValue(cgcBase.getPeriod())+ "[" + cgcBase.getPeriod().getStart() + "..." + cgcBase.getPeriod().getEnd() + "]");

	}
	// ------------------------------------------------------------------------
	
	
	private static final ExpressionContext getExpressionContext(Date startDate, Date endDate) throws ExpressionException{
		
		final  Period period = new Period(startDate, endDate);

		ExpressionContext expressionContext = new ExpressionContext();
		expressionContext.putVariable(ContextVariable.MONTH_DAYS, new ITimedVariable<Double>() {
			@Override
			public Period getPeriod() {
				return period;
			}
			
			@Override
			public Double getValue(Period period) {
				return (double) AonDateUtils.getDay(AonDateUtils.getMonthLastDay(endDate));
			}
		});
		expressionContext.putVariable(ContextVariable.SALARY_DAYS, new ITimedVariable<Double>() {
			@Override
			public Period getPeriod() {
				return period;
			}
			
			@Override
			public Double getValue(Period period) {
				return (double) AonDateUtils.getDaysBetweenDates(period.getStart(), period.getEnd())+1;
			}
		});
		
		DeferredExpressionVariable baseMinExpr = new DeferredExpressionVariable(expressionContext, new IExpression() {
			
			@Override
			public boolean isReadOnly() {
				return true;
			}
			
			@Override
			public ExpressionScope getScope() {
				return ExpressionScope.SYSTEM;
			}
			
			@Override
			public String getName() {
				return ContextVariable.CGC_BASE_MIN.getName();
			}
			
			@Override
			public String getExpression() {
				return "30.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)";
			}
		}, period);
		
		expressionContext.putVariable(ContextVariable.CGC_BASE_MIN.getName(), baseMinExpr );
		expressionContext.putVariable(ContextVariable.CGP_BASE_MIN.getName(), baseMinExpr );

		DeferredExpressionVariable baseMaxExpr = new DeferredExpressionVariable(expressionContext, new IExpression() {
			
			@Override
			public boolean isReadOnly() {
				return true;
			}
			
			@Override
			public ExpressionScope getScope() {
				return ExpressionScope.SYSTEM;
			}
			
			@Override
			public String getName() {
				return ContextVariable.CGC_BASE_MAX.getName();
			}
			
			@Override
			public String getExpression() {
				return "30.00 * 20.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)";
			}
		}, period);
		
		expressionContext.putVariable(ContextVariable.CGC_BASE_MAX.getName(), baseMaxExpr );
		expressionContext.putVariable(ContextVariable.CGP_BASE_MAX.getName(), baseMaxExpr );
		
		return expressionContext;
	}
	
	
}
