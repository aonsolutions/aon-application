package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_RAW;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_RAW;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.enumeration.AbstractSSRegimeTypeVisitor;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class QuoteCalculator {

	protected double itBase = 0;
	protected double rawCgcBase = 0;
	protected double proExtBase = 0;
	protected double maternityBase = 0;
	protected double structuralBase = 0;
	protected double nonStructuralBase = 0;

	public Double getItBase() throws AonException {
		return itBase;
	}

	public Double getCgcBase() throws AonException {
		return rawCgcBase;
	}

	public Double getRawCgcBase() {
		return rawCgcBase;
	}

	public Double getRawCgpBase() {
		return rawCgcBase + structuralBase + nonStructuralBase;
	}

	public Double getCgpBase() throws AonException {
		return rawCgcBase + structuralBase + nonStructuralBase;
	}

	public Double getProExtBase() throws AonException {
		return proExtBase;
	}

	public Double getStructuralBase() throws AonException {
		return structuralBase;
	}

	public Double getNonStructuralBase() throws AonException {
		return nonStructuralBase;
	}

	public abstract Double getEreBase() throws AonException;

	public abstract Double getMaternityBase() throws AonException;

	public abstract Double quote(IContractPayment payment, Date start,
			Date end, double amount) throws AonException;

	public static class NonQuote extends QuoteCalculator {
		private NonQuote() {
		}

		@Override
		public Double getItBase() throws AonException {
			return null;
		}

		@Override
		public Double getCgcBase() throws AonException {
			return null;
		}

		@Override
		public Double getRawCgcBase() {
			return null;
		}

		@Override
		public Double getRawCgpBase() {
			return null;
		}

		@Override
		public Double getCgpBase() throws AonException {
			return null;
		}

		@Override
		public Double getProExtBase() throws AonException {
			return null;
		}

		@Override
		public Double getStructuralBase() throws AonException {
			return null;
		}

		@Override
		public Double getNonStructuralBase() throws AonException {
			return null;
		}

		@Override
		public Double quote(IContractPayment payment, Date start, Date end,
				double amount) throws AonException {
			return null; // No cotiza...
		}

		@Override
		public Double getEreBase() throws AonException {
			return null;
		}

		@Override
		public Double getMaternityBase() throws AonException {
			return null;
		}

		private static QuoteCalculator SINGLETON = new NonQuote();

		public static QuoteCalculator getInstance() {
			return SINGLETON;
		}
	}

	public static class GeneralQuote extends QuoteCalculator {

		private Double cgcBase;
		private Double cgpBase;
		private Date salaryStart;
		private Date salaryEnd;
		private ExpressionContext context;

		private Map<String, Double> bases;

		public GeneralQuote(ExpressionContext context, Date salaryStart,
				Date salaryEnd) {
			this.context = context;
			this.salaryStart = salaryStart;
			this.salaryEnd = salaryEnd;
			this.cgcBase = null;
			this.cgpBase = null;
			bases = new HashMap<String, Double>();
		}

		@Override
		public Double getCgcBase() throws AonException {
			if (cgcBase == null) {
//				cgcBase = getLimitedCgcBase(rawCgcBase, context, salaryStart,
//						salaryEnd);
				cgcBase = getSum(CGC_BASE, context, salaryStart, salaryEnd);
			}
			return cgcBase;
		}

		@Override
		public Double getCgpBase() throws AonException {
			if (cgpBase == null) {
				Double rawCgpBase = rawCgcBase + structuralBase
						+ nonStructuralBase;
//				cgpBase = getLimitedCgpBase(rawCgpBase, context, salaryStart,
//						salaryEnd);
				cgpBase = getSum(CGP_BASE, context, salaryStart, salaryEnd);
			}
			return cgpBase;
		}

		@Override
		public Double getEreBase() throws AonException {
			return bases.containsKey(ERE.getName()) ? bases.get(ERE.getName())
					: 0.00;
		}

		@Override
		public Double getMaternityBase() throws AonException {
			return bases.containsKey(MATERNITY.getName()) ? bases.get(MATERNITY
					.getName()) : 0.00;
		}

		protected double getQuote(IContractPayment payment, Date start,
				Date end, double amount) throws AonException {
			String quoteExpr = payment.getQuoteExpression();
			if (quoteExpr == null) {
				return 0;
			}

			// String name = payment.getName();
			// if (name != null && quoteExpr.equals(name)) {
			// return amount;
			// }

			double total = 0.00;
			List<ITimedResult<Double>> quotes = context.eval(quoteExpr, start,
					end, Double.class);
			for (ITimedObject<Double> quote : quotes) {
				total += quote.getValue();
			}

			return total;
		}
		
		
		protected List<ITimedResult<Double>> quote(IContractPayment payment, Date start,
				Date end) throws UndefinedVariablesException, ExpressionException {
			String quoteExpr = payment.getQuoteExpression();
			if (quoteExpr == null) {
				return Collections.emptyList();
			}
			return  context.eval(quoteExpr, start,
					end, Double.class);
		}

		@Override
		public Double quote(IContractPayment payment, Date start, Date end,
				double amount) throws AonException {
			double total = 0.00;
			
			List<ITimedResult<Double>> quotes = quote(payment, start, end);
			
			for (ITimedResult<Double> quote : quotes)
				total += quoteImpl(
						payment, 
						quote.getPeriod().getStart(), 
						quote.getPeriod().getEnd(), 
						quote.getValue(quote.getPeriod())
						);

			return total; 
		}
		
		// --------------------------------------------------------------------
		protected Double quoteImpl(IContractPayment payment, Date start, Date end,
				final double quote) {
			this.cgcBase = null;
			this.cgpBase = null;
			

			String name = payment.getName();
			
			
			if (!StringUtils.isBlank(name)) {
				bases.put(name, quote);
				add(String.format("BASE_%s", name), quote, context, start, end);
				
				if (AonStringUtils.equals(MATERNITY.getName(), name)
						|| AonStringUtils.equals(ERE.getName(), name)){
					
					if ( context.containsVariable(CGC_BASE.getName(), start, end) )
						limit(CGC_BASE.getName(), CGC_BASE_RAW.getName(),
								CGC_BASE_MIN.getName(), CGC_BASE_MAX.getName(),
								context, start, end, MATERNITY_BASE.getName(), ERE_BASE.getName());
					if ( context.containsVariable(CGP_BASE.getName(), start, end) )
						limit(CGP_BASE.getName(),  CGP_BASE_RAW.getName(),
								CGP_BASE_MIN.getName(), CGP_BASE_MAX.getName(),
								context, start, end, MATERNITY_BASE.getName(), ERE_BASE.getName());
						
					return quote;
				}

			}

			PaymentType paymentType = payment.getType();
			if ( paymentType == null )
				paymentType = PaymentType.CRA_0001;

			paymentType.accept(new PaymentTypeVisitor() {

				@Override
				public void visitOther(PaymentType type) {
					add(CGC_BASE_RAW.getName(), quote, context, start, end);
					limit(CGC_BASE.getName(), CGC_BASE_RAW.getName(),
							CGC_BASE_MIN.getName(), CGC_BASE_MAX.getName(),
							context, start, end, MATERNITY_BASE.getName(), ERE_BASE.getName());
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitStructuralHours(PaymentType paymentType) {
					add(STRUCTURAL_OVERTIME_BASE.getName(), quote, context,
							start, end);
					GeneralQuote.this.structuralBase += quote;
				}

				@Override
				public void visitNonStructuralHours(PaymentType paymentType) {
					add(NON_STRUCTURAL_OVERTIME_BASE.getName(), quote, context,
							start, end);
					GeneralQuote.this.nonStructuralBase += quote;
				}

				@Override
				public void visitSalaryInKind(PaymentType paymentType) {
					add(CGC_BASE_RAW.getName(), quote, context, start, end);
					limit(CGC_BASE.getName(), CGC_BASE_RAW.getName(),
							CGC_BASE_MIN.getName(), CGC_BASE_MAX.getName(),
							context, start, end, MATERNITY_BASE.getName(), ERE_BASE.getName());
					GeneralQuote.this.rawCgcBase += quote;
				}

			});

			add(CGP_BASE_RAW.getName(), quote, context, start, end);
			limit(CGP_BASE.getName(),  CGP_BASE_RAW.getName(),
					CGP_BASE_MIN.getName(), CGP_BASE_MAX.getName(),
					context, start, end, MATERNITY_BASE.getName(), ERE_BASE.getName());

			SalaryType salaryType = payment.getSalaryType();
			salaryType.accept(new SalaryTypeVisitor<Object>() {

				@Override
				public Object visitSalary(SalaryType salaryType) {
					return null;
				}

				@Override
				public Object visitExtra(SalaryType salaryType) {
					GeneralQuote.this.proExtBase += quote;
					return null;
				}

				@Override
				public Object visitSettle(SalaryType salaryType) {
					return null;
				}

				@Override
				public Object visitDelay(SalaryType salaryType) {
					return null;
				}

				@Override
				public Object visitNotEnjoyedVacations(SalaryType salaryType) {
					return null;
				}
			});

			return quote;
		}

		protected void limit(String limitName, String rawName,
				String minExpression, String maxExpression, ExpressionContext ctx,
				Date start, Date end, String ...others) {
			QuoteCalculator.limit(limitName, rawName, minExpression, maxExpression, ctx, start, end, others);
		}

		
	}

	public static class UnlimitedQuote extends GeneralQuote {
		public UnlimitedQuote(ExpressionContext context, Date salaryStart,
				Date salaryEnd) {
			super(context, salaryStart, salaryEnd);
		}

		@Override
		public Double getCgcBase() throws AonException {
			return super.getRawCgcBase();
		}

		@Override
		public Double getCgpBase() throws AonException {
			return super.getRawCgcBase() + getStructuralBase()
					+ getNonStructuralBase();
		}

		protected void limit(String limitName, String rawName,
				String minExpression, String maxExpression, ExpressionContext ctx,
				Date start, Date end, String ...others) {

			List<ITimedVariable<Double>> raws = ctx.getVariables(rawName, start,
					end);
			
			for (ITimedVariable<Double> raw : raws) {
				Period rawPeriod = raw.getPeriod();
				Double rawValue = raw.getValue(raw.getPeriod());
				ctx.putVariable(limitName, new TimedObject<Double>(
						rawValue, rawPeriod));
			}
		}
	}

	public static class CompositeGeneralQuote extends QuoteCalculator {

		List<GeneralQuote> calculators;

		public CompositeGeneralQuote(ExpressionContext context,
				Date startSalary, Date endSalary) {

			calculators = new LinkedList<GeneralQuote>();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startSalary);

			Date endQuote;
			Date startQuote;

			do {
				startQuote = calendar.getTime();
				calendar.set(Calendar.DATE,
						calendar.getActualMaximum(Calendar.DATE));
				endQuote = Period.min(endSalary, calendar.getTime());
				calculators
						.add(new GeneralQuote(context, startQuote, endQuote));
				calendar.add(Calendar.DATE, 1);
			} while (Period.compare(endQuote, endSalary) < 0);

		}

		@Override
		public Double getCgcBase() throws AonException {
			double cgcBase = 0.00;
			for (GeneralQuote calculator : calculators)
				cgcBase += calculator.getCgcBase();
			return cgcBase;
		}

		@Override
		public Double getCgpBase() throws AonException {
			double cgpBase = 0.00;
			for (GeneralQuote calculator : calculators)
				cgpBase += calculator.getCgpBase();
			return cgpBase;
		}

		@Override
		public Double getRawCgcBase() {
			double rawCgcBase = 0.00;
			for (GeneralQuote calculator : calculators)
				rawCgcBase += calculator.getRawCgcBase();
			return rawCgcBase;
		}

		@Override
		public Double getRawCgpBase() {
			double rawCgpBase = 0.00;
			for (GeneralQuote calculator : calculators)
				rawCgpBase += calculator.getRawCgpBase();
			return rawCgpBase;
		}

		@Override
		public Double getEreBase() throws AonException {
			double ereBase = 0.00;
			for (GeneralQuote calculator : calculators)
				ereBase += calculator.getEreBase();
			return ereBase;
		}

		@Override
		public Double getMaternityBase() throws AonException {
			double maternityBase = 0.00;
			for (GeneralQuote calculator : calculators)
				maternityBase += calculator.getMaternityBase();
			return maternityBase;
		}

		@Override
		public Double quote(IContractPayment payment, Date start, Date end,
				double amount) throws AonException {
			double quote = 0.00;
			for (GeneralQuote calculator : calculators) {
				Period intersect = CompositeGeneralQuote.intersect(calculator,
						start, end);
				if (intersect != null) {
					quote += calculator.quote(payment, intersect.getStart(),
							intersect.getEnd(), amount);
				}
			}
			return quote;
		}

		private static Period intersect(GeneralQuote calculator, Date start,
				Date end) {
			return new Period(calculator.salaryStart, calculator.salaryEnd)
					.intersect(new Period(start, end));
		}

	}

	public static QuoteCalculator getQuoteCalculator(
			IContractSalaryCalculatorContext ctx) {

		final ExpressionContext expressionContext = ctx.getExpressionContext();

		final Date startDate = ctx.getStartDate();
		final Date endDate = ctx.getEndDate();

		SSRegimeType ssRegimeType = ctx.getSSRegime();

		QuoteCalculator quoteCalculator = ssRegimeType
				.accept(new AbstractSSRegimeTypeVisitor<QuoteCalculator>() {

					@Override
					public QuoteCalculator visitSelfEmployedRegime(
							SSRegimeType ssRegimeType) {
						return NonQuote.getInstance();
					}
				});

		if (quoteCalculator != null) {
			return quoteCalculator;
		}

		SalaryType salaryType = ctx.getSalaryType();

		quoteCalculator = salaryType
				.accept(new SalaryTypeVisitor<QuoteCalculator>() {

					@Override
					public QuoteCalculator visitSalary(SalaryType salaryType) {
						return new GeneralQuote(expressionContext, startDate,
								endDate);
					}

					@Override
					public QuoteCalculator visitExtra(SalaryType salaryType) {
						return new NonQuote();
					}

					@Override
					public QuoteCalculator visitSettle(SalaryType salaryType) {
						return new UnlimitedQuote(expressionContext, startDate,
								endDate);
					}

					@Override
					public QuoteCalculator visitDelay(SalaryType salaryType) {
						return new UnlimitedQuote(expressionContext, startDate,
								endDate);
					}

					@Override
					public QuoteCalculator visitNotEnjoyedVacations(
							SalaryType salaryType) {
						return new GeneralQuote(expressionContext, startDate,
								endDate);
					}

				});
		return quoteCalculator;
	}

	protected double getSum(ContextVariable contextVariable,
			ExpressionContext ctx, Date start, Date end)
			throws ExpressionException {
		Double total = 0.00;
		
		List<ITimedVariable<Double>> vars = ctx.getVariables(contextVariable, start, end);
		for ( ITimedVariable<Double> var : vars  ){
			try {
				total += var.getValue(var.getPeriod());
			} catch ( Exception e ) {
			}
		}
		
		return total;
	}



	private static Double getLimit(String expression,
			ExpressionContext expressionContext, Date start, Date end)
			throws ExpressionException {

		if (expression == null)
			return null;

		List<ITimedResult<Double>> limits = null;
		limits = expressionContext.eval(expression, start, end, Double.class);

		if (limits == null || limits.size() == 0) {
			return null;
		}

		Double limit = 0.00;
		for (ITimedResult<Double> result : limits) {
			limit += result.getValue();
		}

		return limit;
	}

	private static void add(String name, Double value, ExpressionContext ctx,
			Date start, Date end) {

		List<ITimedVariable<Double>> vars = ctx.getVariables(name, start, end);

		List<Period> valuePeriods = new ArrayList<Period>();

		long valueDays = AonDateUtils.getDaysBetweenDates(start, end) + 1;

		Double valueDay = value / valueDays;

		for (ITimedVariable<Double> var : vars) {
			Period period = var.getPeriod();
			valuePeriods.add(period);
			Double oldValue = var.getValue(period);
			long days = AonDateUtils.getDaysBetweenDates(period.getStart(),
					period.getEnd()) + 1;
			ctx.putVariable(name, new TimedObject<Double>(oldValue
					+ (valueDay * days), period));
		}

		List<Period> nullPeriods = Period.sub(new Period(start, end),
				valuePeriods);
		for (Period period : nullPeriods) {
			long days = AonDateUtils.getDaysBetweenDates(period.getStart(),
					period.getEnd()) + 1;
			ctx.putVariable(name, new TimedObject<Double>(valueDay * days,
					period));
		}

	}

	private static void limit(String limitName, String rawName,
			String minExpression, String maxExpression, ExpressionContext ctx,
			Date start, Date end, String ...others) {

		List<ITimedVariable<Double>> raws = ctx.getVariables(rawName, start,
				end);
		
		for (ITimedVariable<Double> raw : raws) {
			Period rawPeriod = raw.getPeriod();
			Double rawValue = raw.getValue(raw.getPeriod());
			
			Double othersValue = 0.00;
			for ( String other: others )
				othersValue += sum(other, rawPeriod, ctx );
			
			try {
				Double minValue = getLimit(minExpression, ctx,
						rawPeriod.getStart(), rawPeriod.getEnd());
				
				minValue -= othersValue;
				
				if ( rawValue <= minValue) {
					ctx.putVariable(limitName, new TimedObject<Double>(
							minValue , rawPeriod));
					return;
				}
			} catch ( Exception e ){
			}
				
			try{
				Double maxValue = getLimit(maxExpression, ctx,
						rawPeriod.getStart(), rawPeriod.getEnd());

				maxValue -= othersValue;
				
				ctx.putVariable(
						limitName,
						new TimedObject<Double>(Math
								.min(rawValue, maxValue), rawPeriod));
				return;
			} catch (Exception e) {
			}
			ctx.putVariable(limitName, new TimedObject<Double>(
					rawValue, rawPeriod));
		}

	}
	
	private static Double sum(String name, Period p, ExpressionContext ctx) {
		Double sum = 0.00;
		
		List<ITimedVariable<Double>> vars = getVariables(name, p, ctx);
		
		for (ITimedVariable<Double> var : vars) {
			Period intersect = var.getPeriod().intersect(p);
			sum += var.getValue(var.getPeriod()) * days(intersect) / days(var.getPeriod());
		}

		return sum;
	}
	
	private static long days(Period p) {
		return CommonUtil.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	private static  <T> List<ITimedVariable<T>> getVariables(String name, Period p, ExpressionContext ctx) {
		List<ITimedVariable<Object>> variables = ctx.getVariables(name);
		if (variables == null)
			return Collections.emptyList();

		List<ITimedVariable<T>> ret = new ArrayList<ITimedVariable<T>>();

		for (ITimedVariable<?> var : variables) {
			if ( var.getPeriod().intersects(p))
				ret.add((ITimedVariable<T>) var);
		}

		return ret;

	}
	

}
