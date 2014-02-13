package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.enumeration.AbstractSSRegimeTypeVisitor;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.salary.expression.ITimedResult;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public abstract class QuoteCalculator {

	protected double itBase = 0;
	protected double rawCgcBase = 0;
	protected double proExtBase = 0;
	protected double maternityBase = 0;
	protected double structuralBase = 0;
	protected double nonStructuralBase = 0;

	public double getItBase() throws AonException {
		return itBase;
	}

	public double getCgcBase() throws AonException {
		return rawCgcBase;
	}

	public double getRawCgcBase() {
		return rawCgcBase;
	}

	public double getRawCgpBase() {
		return rawCgcBase + structuralBase + nonStructuralBase;
	}

	public double getCgpBase() throws AonException {
		return rawCgcBase + structuralBase + nonStructuralBase;
	}

	public double getProExtBase() throws AonException {
		return proExtBase;
	}

	public double getStructuralBase() throws AonException {
		return structuralBase;
	}

	public double getNonStructuralBase() throws AonException {
		return nonStructuralBase;
	}

	public abstract double getMaternityBase() throws AonException;

	public abstract void quote(IContractPayment payment, Date start, Date end,
			double amount) throws AonException;

	public static class NonQuote extends QuoteCalculator {
		private NonQuote() {
		}

		@Override
		public void quote(IContractPayment payment, Date start, Date end,
				double amount) throws AonException {
			return; // No cotiza...
		}

		@Override
		public double getMaternityBase() throws AonException {
			return 0.00;
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
		public double getCgcBase() throws AonException {
			if (cgcBase == null) {
				cgcBase = getLimitedCgcBase(rawCgcBase, context, salaryStart,
						salaryEnd);
			}
			return cgcBase;
		}

		@Override
		public double getCgpBase() throws AonException {
			if (cgpBase == null) {
				Double rawCgpBase = rawCgcBase + structuralBase
						+ nonStructuralBase;
				cgpBase = getLimitedCgpBase(rawCgpBase, context, salaryStart,
						salaryEnd);
			}
			return cgpBase;
		}

		@Override
		public double getMaternityBase() throws AonException {
			return bases.containsKey(MATERNITY.getName()) ? bases.get(MATERNITY
					.getName()) : 0.00;
		}

		protected double getQuote(IContractPayment payment, Date start,
				Date end, double amount) throws AonException {
			String quoteExpr = payment.getQuoteExpression();
			if (quoteExpr == null) {
				return 0;
			}
			String name = payment.getName();
			if (name != null && quoteExpr.equals(name)) {
				// System.out.println("QUOTE:" + payment.getName() + ", " +
				// payment.getDescription()+ " = " + amount);
				return amount;
			}

			List<ITimedResult<Double>> quotes;
			quotes = context.eval(quoteExpr, start, end, Double.class);
			double total = 0.00;
			for (ITimedObject<Double> quote : quotes) {
				total += quote.getValue();
				// System.out.println("QUOTE:" + payment.getName() + ", " +
				// payment.getDescription()+ " = " + total + " " +
				// payment.getQuoteExpression());
			}

			return total;
		}

		@Override
		public void quote(IContractPayment payment, Date start, Date end,
				double amount) throws AonException {

			final double quote = getQuote(payment, start, end, amount);
			if (quote == 0) {
				return;
			}

			PaymentType paymentType = payment.getType();

			paymentType.accept(new PaymentTypeVisitor() {

				@Override
				public void visitBaseSalary(PaymentType type) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitStructuralHours(PaymentType paymentType) {
					GeneralQuote.this.structuralBase += quote;
				}

				@Override
				public void visitNonStructuralHours(PaymentType paymentType) {
					GeneralQuote.this.nonStructuralBase += quote;
				}

				@Override
				public void visitSpecialBonos(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitSocialSecurityBenefits(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitSalarySupplement(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitSalaryInKind(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitOtherNonWage(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitMovingCompensation(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

				@Override
				public void visitCompensationExpense(PaymentType paymentType) {
					GeneralQuote.this.rawCgcBase += quote;
				}

			});

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
			String name = payment.getName();
			if (!StringUtils.isBlank(name)) {
				bases.put(name, quote);
				context.addVariable(String.format("BASE_%s", name), quote,
						start, end);
				// System.out.printf("%s : %f [[%TF..%TF][%f] %s, %s  \r\n",
				// name, quote, start, end, amount, payment.getDescription(),
				// payment.getQuoteExpression() );
			}
		}

	}

	public static class UnlimitedQuote extends GeneralQuote {
		public UnlimitedQuote(ExpressionContext context, Date salaryStart,
				Date salaryEnd) {
			super(context, salaryStart, salaryEnd);
		}

		@Override
		public double getCgcBase() throws AonException {
			return super.getRawCgcBase();
		}

		@Override
		public double getCgpBase() throws AonException {
			return super.getRawCgcBase() + getStructuralBase()
					+ getNonStructuralBase();
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
		public double getCgcBase() throws AonException {
			double cgcBase = 0.00;
			for (GeneralQuote calculator : calculators)
				cgcBase += calculator.getCgcBase();
			return cgcBase;
		}

		@Override
		public double getCgpBase() throws AonException {
			double cgpBase = 0.00;
			for (GeneralQuote calculator : calculators)
				cgpBase += calculator.getCgpBase();
			return cgpBase;
		}

		@Override
		public double getRawCgcBase() {
			double rawCgcBase = 0.00;
			for (GeneralQuote calculator : calculators)
				rawCgcBase += calculator.getRawCgcBase();
			return rawCgcBase;
		}

		@Override
		public double getRawCgpBase() {
			double rawCgpBase = 0.00;
			for (GeneralQuote calculator : calculators)
				rawCgpBase += calculator.getRawCgpBase();
			return rawCgpBase;
		}

		@Override
		public double getMaternityBase() throws AonException {
			double maternityBase = 0.00;
			for (GeneralQuote calculator : calculators)
				maternityBase += calculator.getMaternityBase();
			return maternityBase;
		}

		@Override
		public void quote(IContractPayment payment, Date start, Date end,
				double amount) throws AonException {
			System.out.println("Quote : " + payment.getId() + ", " + payment.getName() + "[" +start + "..." + end + "]: " + amount +", " + payment.getQuoteExpression() );
			for (GeneralQuote calculator : calculators) {
				Period intersect = CompositeGeneralQuote.intersect(calculator,
						start, end);
				if (intersect != null) {
					calculator.quote(payment, intersect.getStart(),
							intersect.getEnd(), amount);
					System.out.println("\tQuote : " + payment.getName() + "[" +intersect.getStart() + "..." + intersect.getEnd() + "]: " + calculator.getRawCgcBase() );
				}
			}
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

	protected double getLimitedCgcBase(double rawCgcBase,
			ExpressionContext ctx, Date start, Date end)
			throws ExpressionException {
		String quoteGroup = ctx.getVariable(QUOTE_GROUP, start, end,
				String.class);

		Map<String, String> minLimits = ctx.getVariable(CGC_BASE_MIN, start,
				end, Map.class);

		if (minLimits == null) {
			String variableName = CGC_BASE_MIN.getName();
			throw new UndefinedVariablesException(variableName);
		}

		Double minLimit = getLimit(minLimits.get(quoteGroup), ctx, start, end);

		if (minLimit != null && rawCgcBase < minLimit) {
			return minLimit;
		}

		Map<String, String> maxLimits = ctx.getVariable(CGC_BASE_MAX, start,
				end, Map.class);

		if (maxLimits == null) {
			String variableName = CGC_BASE_MIN.getName();
			throw new UndefinedVariablesException(variableName);
		}

		Double maxLimit = getLimit(maxLimits.get(quoteGroup), ctx, start, end);

		if (maxLimit != null && rawCgcBase > maxLimit) {
			return maxLimit;
		}

		return rawCgcBase;
	}

	protected double getLimitedCgpBase(double rawCgpBase,
			ExpressionContext ctx, Date start, Date end)
			throws ExpressionException {
		String minExpression = ctx.getVariable(CGP_BASE_MIN, start, end,
				String.class);

		Double minLimit = getLimit(minExpression, ctx, start, end);

		if (minLimit != null && rawCgpBase < minLimit) {
			return minLimit;
		}

		String maxExpression = ctx.getVariable(CGP_BASE_MAX, start, end,
				String.class);

		Double maxLimit = getLimit(maxExpression, ctx, start, end);

		if (maxLimit != null && rawCgpBase > maxLimit) {
			return maxLimit;
		}

		return rawCgpBase;
	}

	private Double getLimit(String expression,
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
			limit+=result.getValue();
		}


		return limit;
	}

}
