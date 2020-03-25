package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_RAW;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_RAW;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.DIRECT_PAY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERES;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASES;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_BASE_FORCE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.ERE_FORCE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NON_STRUCTURAL_OVERTIME_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.STRUCTURAL_OVERTIME_BASE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.esferalia.aon.salary.expression.ExpressionContext.DeferredExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionVariable;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.expression.TimedResult;
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

	public Double qu0te(IContractPayment payment, Date start, Date end,
			double amount) throws AonException {

		double quote = 0;
		for (ITimedResult<Double> result : quote(payment, start, end, amount))
			quote += result.getValue();

		return quote;

	}

	public abstract Double getEreBase() throws AonException;

	public abstract Double getMaternityBase() throws AonException;

	public abstract Double getDirectPayBase() throws AonException;

	public abstract List<ITimedResult<Double>> quote(IContractPayment payment,
			Date start, Date end, double amount) throws AonException;
	
	
	private static class ZeroTimedResult implements ITimedResult<Double>{
		
		private ITimedResult<?> result;
		
		public ZeroTimedResult(ITimedResult<?> result) {
			this.result = result;
		}
		
		@Override
		public Double getValue() {
			return  0.00;
		}

		@Override
		public Period getPeriod() {
			return result.getPeriod();
		}

		@Override
		public Double getValue(Period period) {
			return 0.00;
		}

		@Override
		public Map<String, ITimedVariable<?>> getContext() {
			return result.getContext();
		}
		
	}
	
	private static class BaseVariable implements ITimedVariable<Double> {

		private Double value;
		private Period period;
		
		public BaseVariable(Double value, Period period ) {
			this.value = value;
			this.period = period;
		}
		
		public BaseVariable(Double value, Date start, Date end ) {
			this ( value, new Period(start, end) );
		}


		@Override
		public Period getPeriod() {
			return period;
		}
		
		@Override
		public Double getValue(Period period) {
			return value / getValueDays() * getPeriodDays(period);
		}
		
		private long getValueDays() {
			return getPeriodDays(period);
		}

		private long getPeriodDays(Period p) {
			return AonDateUtils.getDaysBetweenDates(p.getStart(), p.getEnd())+1;
		}

	}

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
		public List<ITimedResult<Double>> quote(IContractPayment payment,
				Date start, Date end, double amount) throws AonException {
			return Collections.emptyList(); // No cotiza...
		}

		@Override
		public Double getEreBase() throws AonException {
			return null;
		}

		@Override
		public Double getMaternityBase() throws AonException {
			return null;
		}
		
		@Override
		public Double getDirectPayBase() throws AonException {
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
				// cgcBase = getLimitedCgcBase(rawCgcBase, context, salaryStart,
				// salaryEnd);
				cgcBase = getSum(CGC_BASE, context, salaryStart, salaryEnd);
			}
			return cgcBase;
		}

		@Override
		public Double getCgpBase() throws AonException {
			if (cgpBase == null) {
				Double rawCgpBase = rawCgcBase + structuralBase
						+ nonStructuralBase;
				// cgpBase = getLimitedCgpBase(rawCgpBase, context, salaryStart,
				// salaryEnd);
				cgpBase = getSum(CGP_BASE, context, salaryStart, salaryEnd);
			}
			return cgpBase;
		}

		@Override
		public Double getEreBase() throws AonException {
			Double ereBase = 0.0;
			for ( ContextVariable v : ERES ) 
				ereBase += (bases.containsKey(v.getName()) ? bases.get(v.getName()): 0.00);
			return ereBase;
		}

		@Override
		public Double getMaternityBase() throws AonException {
			return bases.containsKey(MATERNITY.getName())
					? bases.get(MATERNITY.getName()) : 0.00;
		}
		
		@Override
		public Double getDirectPayBase() throws AonException {
			return bases.containsKey(DIRECT_PAY.getName())
					? bases.get(DIRECT_PAY.getName()) : 0.00;
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

		protected List<ITimedResult<Double>> quote(IContractPayment payment,
				Date start, Date end) throws UndefinedVariablesException,
						ExpressionException {
			String quoteExpr = payment.getQuoteExpression();
			if (quoteExpr == null) {
				return Collections.emptyList();
			}
			return context.eval(quoteExpr, start, end, Double.class);
		}

		@Override
		public List<ITimedResult<Double>> quote(IContractPayment payment,
				Date start, Date end, double amount) throws AonException {

			List<ITimedResult<Double>> quoteResults = quote(payment, start, end);

			List<ITimedResult<Double>> quotesImpl = new ArrayList<ITimedResult<Double>>(
					quoteResults.size());

			for (ITimedResult<Double> quoteResult : quoteResults) {
				double quote = quoteResult.getValue(quoteResult.getPeriod());
				

				quotesImpl.add(new TimedResult<Double>(quote,
						quoteResult.getPeriod(), quoteResult.getContext()));
				
				List<ITimedResult<Double>> limitsResults = quoteImpl(payment,
						quoteResult.getPeriod().getStart(),
						quoteResult.getPeriod().getEnd(),
						quoteResult.getValue(quoteResult.getPeriod()));

				// TODO: ???
				for ( ITimedResult<Double> limitResult: limitsResults ){
					try {
						limitResult.getContext();
					} catch (ExpressionExceptionWrapper e) {
					}
					quotesImpl.add(new ZeroTimedResult(limitResult));
				}
			}

			return quotesImpl;
		}

		// --------------------------------------------------------------------
		protected List<ITimedResult<Double>> quoteImpl(IContractPayment payment, Date start,
				Date end, final double quote) {
			this.cgcBase = null;
			this.cgpBase = null;
			
			
			List<ITimedResult<Double>> quotesImpl = new ArrayList<ITimedResult<Double>>();

			if (quote == 0 &&
				payment.getType() == PaymentType.CRA_0055 &&
				context.containsVariable("BASE_" + PREST_IT, start,end))
				return quotesImpl;
			
			String name = payment.getName();

			if (!StringUtils.isBlank(name)) {
				bases.put(name, quote +bases.getOrDefault(name, 0.00));
				add(String.format("BASE_%s", name), quote, context, start, end);

				if (AonStringUtils.equals(MATERNITY.getName(), name)
						|| matchAny(ContextVariable.ERES, payment.getName()) 
						|| AonStringUtils.equals(DIRECT_PAY.getName(), name)) {

					if (context.containsVariable(CGC_BASE.getName(), start,
							end))
						//@formatter:off
						quotesImpl.addAll(limit(
								CGC_BASE, 
								CGC_BASE_RAW,
								CGC_BASE_MIN, 
								CGC_BASE_MAX,
								context, 
								start, 
								end, 
								ERE_BASES,
								MATERNITY_BASE,
								DIRECT_BASE
								));
						//@formatter:on
					if (context.containsVariable(CGP_BASE.getName(), start,
							end))
						quotesImpl.addAll(limit(
								CGP_BASE, 
								CGP_BASE_RAW,
								CGP_BASE_MIN, 
								CGP_BASE_MAX,
								context, start, end, 
								ERE_BASES, 
								MATERNITY_BASE,
								DIRECT_BASE));

					return quotesImpl;
				}
				
				
			}
			
			if (AonStringUtils.equals(PREST_IT, name)) {
				
				GeneralQuote.this.rawCgcBase += quote;

				set(CGC_BASE_RAW.getName(), quote, context, start, end);
				set(CGP_BASE_RAW.getName(), quote, context, start, end);
				
				double cgcBaseMin  = 0;
				try {
					cgcBaseMin = getLimit(
					CGC_BASE_MIN,
					context, 
					start, 
					end)
					.stream()
					.collect(Collectors.summingDouble(r->r.getValue()));
					
				} catch (ExpressionException e) {
				}
				
				double cgcBase = Math.max(quote, cgcBaseMin);
				

				set(CGC_BASE.getName(), cgcBase, context, start, end);
				
				double cgpBaseMin  = 0;
				try {
					cgpBaseMin = getLimit(
					CGP_BASE_MIN,
					context, 
					start, 
					end)
					.stream()
					.collect(Collectors.summingDouble(r->r.getValue()));
					
				} catch (ExpressionException e) {
				}

				double cgpBase = Math.max(quote, cgpBaseMin);

				set(CGP_BASE.getName(), cgpBase, context, start, end);
				
				add(String.format("BASE_%s", name), cgcBase, context, start, end);
				quotesImpl.add(new TimedResult<Double>(cgcBase, new Period(start,end), Collections.emptyMap()));
				return quotesImpl;
			}
			
			PaymentType paymentType = payment.getType();
			if (paymentType == null)
				paymentType = PaymentType.CRA_0001;

			paymentType.accept(new PaymentTypeVisitor() {

				@Override
				public void visitOther(PaymentType type) {
					add(CGC_BASE_RAW.getName(), quote, context, start, end);
					
					quotesImpl.addAll(limit(
							CGC_BASE, 
							CGC_BASE_RAW,
							CGC_BASE_MIN, 
							CGC_BASE_MAX,
							context, start, end, 
							ERE_BASES, 
							MATERNITY_BASE,
							DIRECT_BASE));
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
					quotesImpl.addAll(limit(
							CGC_BASE, 
							CGC_BASE_RAW,
							CGC_BASE_MIN, 
							CGC_BASE_MAX,
							context, start, end, 
							ERE_BASES, 
							MATERNITY_BASE,
							DIRECT_BASE));
					GeneralQuote.this.rawCgcBase += quote;
				}

			});

			add(CGP_BASE_RAW.getName(), quote, context, start, end);
			quotesImpl.addAll(limit(
					CGP_BASE, 
					CGP_BASE_RAW,
					CGP_BASE_MIN, 
					CGP_BASE_MAX, 
					context,
					start, 
					end, 
					ERE_BASES, 
					MATERNITY_BASE, 
					DIRECT_BASE));

			SalaryType salaryType = payment.getSalaryType();
			salaryType.accept(new SalaryTypeVisitor<Object>() {

				@Override
				public Object visitSalary(SalaryType salaryType) {
					if ( payment.getType() == PaymentType.CRA_0004 
						&& payment.getMonth() != null 	
						)
						GeneralQuote.this.proExtBase += quote;
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

			return quotesImpl;
		}



		protected List<ITimedResult<Double>> limit(ContextVariable limit, ContextVariable raw,
				ContextVariable min, ContextVariable max,
				ExpressionContext ctx, Date start, Date end, ContextVariable othersArr [], ContextVariable... othersArgs) {
			List<ContextVariable> others = new ArrayList<ContextVariable>();
			others.addAll(Arrays.asList(othersArr));
			others.addAll(Arrays.asList(othersArgs));
			
			return QuoteCalculator.limit(limit, raw, min,
					max, ctx, start, end, others.toArray(ContextVariable[]::new));
		}

		protected List<ITimedResult<Double>> limit(ContextVariable limit, ContextVariable raw,
				ContextVariable min, ContextVariable max,
				ExpressionContext ctx, Date start, Date end, ContextVariable... others) {
			return QuoteCalculator.limit(limit, raw, min,
					max, ctx, start, end, others);
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

		protected List<ITimedResult<Double>> limit(String limitName, String rawName, 
				String minExpression, String maxExpression,
				ExpressionContext ctx, Date start, Date end, String... others) {

			List<ITimedVariable<Double>> raws = ctx.getVariables(rawName, start,
					end);

			for (ITimedVariable<Double> raw : raws) {
				Period rawPeriod = raw.getPeriod();
				Double rawValue = raw.getValue(raw.getPeriod());
				ctx.putVariable(limitName,
						new TimedObject<Double>(rawValue, rawPeriod));
			}
			
			return Collections.emptyList();
		}
		
		@Override
		protected List<ITimedResult<Double>> limit(ContextVariable limit, ContextVariable raw, ContextVariable min,
				ContextVariable max, ExpressionContext ctx, Date start, Date end, ContextVariable... others) {

			List<ITimedVariable<Double>> rawVars = ctx.getVariables(raw.getName(), start,
					end);

			for (ITimedVariable<Double> rawVar : rawVars) {
				Period rawPeriod = rawVar.getPeriod();
				Double rawValue = rawVar.getValue(rawVar.getPeriod());
				ctx.putVariable(limit.getName(),
						new TimedObject<Double>(rawValue, rawPeriod));
			}
			
			return Collections.emptyList();
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
		public Double getDirectPayBase() throws AonException {
			double directPayBase = 0.00;
			for (GeneralQuote calculator : calculators)
				directPayBase += calculator.getDirectPayBase();
			return directPayBase;
		}
		
		@Override
		public List<ITimedResult<Double>> quote(IContractPayment payment,
				Date start, Date end, double amount) throws AonException {
			List<ITimedResult<Double>> quotes = new ArrayList<ITimedResult<Double>>();

			for (GeneralQuote calculator : calculators) {
				Period intersect = CompositeGeneralQuote.intersect(calculator,
						start, end);
				if (intersect != null) {
					quotes.addAll(calculator.quote(payment,
							intersect.getStart(), intersect.getEnd(), amount));
				}
			}

			return quotes;
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

		List<ITimedVariable<Double>> vars = ctx.getVariables(contextVariable,
				start, end);
		for (ITimedVariable<Double> var : vars) {
			try {
				total += var.getValue(var.getPeriod());
			} catch (Exception e) {
			}
		}

		return total;
	}

	private static List<ITimedResult<Double>> getLimit(
			ContextVariable ctxVar,
			ExpressionContext expressionContext, 
			Date start, 
			Date end)
	throws ExpressionException {

		List<ITimedResult<Double>> limits = new ArrayList<ITimedResult<Double>>(); 
		
		
		List<ITimedVariable<Double>> vars = expressionContext.getVariables(ctxVar, start, end);
		if ( vars.isEmpty() )
			throw new UndefinedContextVariablesException(ctxVar);
		
		for ( ITimedVariable<Double> var: vars ) {
			try {
				
				Period period = var.getPeriod();
				ITimedVariable<?> variable = 
				expressionContext.getVariable(ctxVar, 
						period.getStart(), 
						period.getEnd());
				if (variable instanceof IExpressionVariable<?>)
					throw new DeferredExpressionException(((IExpressionVariable) variable).getExpression(), period );
				
				
				Number value = var.getValue(var.getPeriod());
				
				limits.add(
						new TimedResult<Double>(
						value.doubleValue(), 
						var.getPeriod(), 
						Collections.emptyMap()));
				
			} catch ( ExpressionExceptionWrapper e){
				try {
					throw e.getExpressionException();
				} catch ( DeferredExpressionException de){
					limits.addAll(expressionContext.eval(de.getExpression().getExpression(), var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class));
				} catch ( Throwable t ){
					limits.addAll(expressionContext.eval(ctxVar.getName(), var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class));
				}
			} catch ( DeferredExpressionException de){
				limits.addAll(expressionContext.eval(de.getExpression().getExpression(), var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class));
			} catch ( Throwable t ){
				limits.addAll(expressionContext.eval(ctxVar.getName(), var.getPeriod().getStart(), var.getPeriod().getEnd(), Double.class));
			}
		}
		
		return limits;
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
			ctx.putVariable(name, new BaseVariable(
					oldValue + (valueDay * days), period));
		}

		List<Period> nullPeriods = Period.sub(new Period(start, end),
				valuePeriods);
		for (Period period : nullPeriods) {
			long days = AonDateUtils.getDaysBetweenDates(period.getStart(),
					period.getEnd()) + 1;
			ctx.putVariable(name,
					new BaseVariable(valueDay * days, period));
		}

	}
	
	private static void set(String name, Double value, ExpressionContext ctx,
			Date start, Date end) {

		List<ITimedVariable<Double>> vars = ctx.getVariables(name, start, end);

		List<Period> valuePeriods = new ArrayList<Period>();

		long valueDays = AonDateUtils.getDaysBetweenDates(start, end) + 1;

		Double valueDay = value / valueDays;

		for (ITimedVariable<Double> var : vars) {
			Period period = var.getPeriod();
			valuePeriods.add(period);
			long days = AonDateUtils.getDaysBetweenDates(period.getStart(),
					period.getEnd()) + 1;
			ctx.putVariable(name, new BaseVariable(
					(valueDay * days), period));
		}

		List<Period> nullPeriods = Period.sub(new Period(start, end),
				valuePeriods);
		for (Period period : nullPeriods) {
			long days = AonDateUtils.getDaysBetweenDates(period.getStart(),
					period.getEnd()) + 1;
			ctx.putVariable(name,
					new BaseVariable(valueDay * days, period));
		}
	}

	private static List<ITimedResult<Double>> limit(
			ContextVariable limitVar, 
			ContextVariable rawVar,
			ContextVariable minVar, 
			ContextVariable maxVar, 
			ExpressionContext ctx,
			Date start, 
			Date end, 
			ContextVariable... otherVars) {

		List<ITimedResult<Double>> results = new ArrayList<ITimedResult<Double>>();
		
		
		List<ITimedVariable<Double>> raws = ctx.getVariables(rawVar, start,
				end);

		for (ITimedVariable<Double> raw : raws) {
			Period rawPeriod = raw.getPeriod();
			Double rawValue = raw.getValue(raw.getPeriod());

			Double othersValue = 0.00;
			for (ContextVariable other : otherVars)
				othersValue += sum(other, rawPeriod, ctx);

			try {
				List<ITimedResult<Double>> minValues = getLimit(
						minVar,
						ctx, 
						rawPeriod.getStart(), 
						rawPeriod.getEnd());

				double minValue = 0.00;
				for ( ITimedResult<Double> minResult: minValues ) {
					results.add(minResult);
					minValue += minResult.getValue();
				}
				
				minValue -= othersValue;

				if (rawValue <= minValue) {
					ctx.putVariable(limitVar,
							new BaseVariable(minValue, rawPeriod));
					continue;
				}
				
				
			} catch (UndefinedVariablesException e) {
				results.add(new TimedResult<Double>(0.00, new Period(start, end),null){
					@Override
					public Map<String, ITimedVariable<?>> getContext() {
						throw new ExpressionExceptionWrapper(e);
					}
				});
			} catch (Exception e) {
				
			}

			try {
				List<ITimedResult<Double>> maxValues = getLimit(maxVar,
						ctx, rawPeriod.getStart(), rawPeriod.getEnd());

				double maxValue = 0.00;
				for ( ITimedResult<Double> maxResult: maxValues ) {
					results.add(maxResult);
					maxValue += maxResult.getValue();
				}

				maxValue -= othersValue;

				ctx.putVariable(limitVar, new BaseVariable(
						Math.min(rawValue, maxValue), rawPeriod));
				continue;
			} catch (UndefinedVariablesException e) {
				results.add(new TimedResult<Double>(0.00, new Period(start, end),null){
					@Override
					public Map<String, ITimedVariable<?>> getContext() {
						throw new ExpressionExceptionWrapper(e);
					}
				});
			} catch (Exception e) {
			}
			ctx.putVariable(limitVar,
					new BaseVariable(rawValue, rawPeriod));
		}
		
		return results;

	}

	private static Double sum(ContextVariable ctxVar, Period p, ExpressionContext ctx) {
		Double sum = 0.00;

		List<ITimedVariable<Double>> vars = getVariables(ctxVar, p, ctx);

		for (ITimedVariable<Double> var : vars) {
			Period intersect = var.getPeriod().intersect(p);
			sum += var.getValue(var.getPeriod()) * days(intersect)
					/ days(var.getPeriod());
		}

		return sum;
	}

	private static long days(Period p) {
		return CommonUtil.getDaysBetweenDates(p.getStart(), p.getEnd()) + 1;
	}

	
	private static <T> List<ITimedVariable<T>> getVariables(Object name,
			Period p, ExpressionContext ctx) {
		List<ITimedVariable<Object>> variables = ctx.getVariables(name);
		if (variables == null)
			return Collections.emptyList();

		List<ITimedVariable<T>> ret = new ArrayList<ITimedVariable<T>>();

		for (ITimedVariable<?> var : variables) {
			if (var.getPeriod().intersects(p))
				ret.add((ITimedVariable<T>) var);
		}

		return ret;

	}

	private static HashMap<String, ITimedVariable<?>> getUndefinedContext(UndefinedVariablesException e ) {
		
		HashMap<String, ITimedVariable<?>> context = new HashMap<String, ITimedVariable<?>>();
		
		for( String name : e.getVariableNames() )
			context.put(name, new ITimedVariable<Void>() {
				private UndefinedVariablesException e = new UndefinedVariablesException(name);
				
				@Override
				public Period getPeriod() {
					throw new ExpressionExceptionWrapper(e);
				}
				
				@Override
				public Void getValue(Period period) {
					throw new ExpressionExceptionWrapper(e);
				}
			});
		
		return context;
	}
	
	protected static boolean matchAny(ContextVariable vars [] , String name ) {
		if ( AonStringUtils.isBlank(name))
			return false;
		return Arrays.stream(vars).anyMatch(v->AonStringUtils.equals(v.getName(), name ));
	}

}
