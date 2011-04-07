package com.esferalia.aon.payroll.calculator;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.CGC_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.CGP_BASE_MAX;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.CGP_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.QUOTE_GROUP;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.MATERNITY;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.SSRegimeTypeVisitor;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;

public abstract class QuoteCalculator {
	
	
	
	protected double itBase = 0 ;
	protected double rawCgcBase = 0 ;
	protected double proExtBase = 0;
	protected double maternityBase = 0 ;
	protected double structuralBase = 0;
	protected double nonStructuralBase = 0;

	
	public double getItBase() {
		return itBase;
	}
	
	public double getCgcBase() {
		return rawCgcBase;
	}

	public double getRawCgcBase() {
		return rawCgcBase;
	}

	public double getCgpBase() {
		return rawCgcBase + structuralBase  +nonStructuralBase ;
	}

	public double getProExtBase() {
		return proExtBase;
	}


	public double getStructuralBase() {
		return structuralBase;
	}


	public double getNonStructuralBase() {
		return nonStructuralBase;
	}
	
	public abstract double  getMaternityBase();

	public abstract void quote(IContractPayment payment, Date start, Date end, double amount ) throws AonException;
	
	
	public static class NonQuote extends QuoteCalculator {
		private NonQuote() {
		}
		@Override
		public void quote(IContractPayment payment, Date start, Date end, double amount) 
			throws AonException 
		{
			return; // No cotiza...
		}
		
		@Override
		public double getMaternityBase() {
			return 0.00;
		}
		
		private static QuoteCalculator SINGLETON = new NonQuote();
		
		public static QuoteCalculator getInstance() {
			return SINGLETON;
		}
	}
	
	
	public static class GeneralQuote extends QuoteCalculator {
		
		private Double cgcBase ;
		private Double cgpBase ;
		private Date salaryStart;
		private Date salaryEnd;
		private ExpressionContext context;
		
		private Map<String, Double> bases ;
		
		public GeneralQuote(ExpressionContext context, Date salaryStart, Date salaryEnd) {
			this.context = context;
			this.salaryStart = salaryStart;
			this.salaryEnd = salaryEnd;
			this.cgcBase = null;
			this.cgpBase = null;
			bases = new HashMap<String, Double>();
		}
		
		public double getCgcBase() {
			if ( cgcBase == null ) {
				cgcBase = getLimitedCgcBase(rawCgcBase, context, salaryStart, salaryEnd);
			}
			return cgcBase;
		}

		public double getCgpBase() {
			if ( cgpBase == null ) {
				Double rawCgpBase = rawCgcBase + structuralBase + nonStructuralBase;
				cgpBase = getLimitedCgpBase(rawCgpBase, context, salaryStart, salaryEnd);
			}
			return cgpBase;
		}
		
		@Override
		public double getMaternityBase() {
			return bases.containsKey(MATERNITY.getName()) ? 
				bases.get(MATERNITY.getName()) : 0.00;
		}
		
		
		private double getQuote(IContractPayment payment, Date start, Date end, double amount ) 
			throws AonException 
		{
			String quoteExpr = payment.getQuoteExpression();
			if ( quoteExpr == null ) {
				return 0;
			}
			String name = payment.getName();
			if ( name != null && quoteExpr.equals(name)) {
				return amount;
			}
			
			List<ITimedObject<Double>> quotes;
			quotes = context.eval(quoteExpr, start, end, Double.class);
			double total = 0.00;
			for (ITimedObject<Double> quote : quotes) {
				total += quote.getValue();
			}
			return total;
		}
		
		
		@Override
		public void quote(IContractPayment payment, Date start, Date end, double amount) 
			throws AonException 
		{
			
			final double quote = getQuote(payment, start, end, amount);
			if ( quote == 0 ) {
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
			
			bases.put(payment.getName(), quote);
		}
		
	}
	
	public static QuoteCalculator getQuoteCalculator(IContractSalaryCalculatorContext ctx) {
		
		SSRegimeType ssRegimeType = ctx.getSSRegime();
		
		final ExpressionContext expressionContext = 
			ctx.getExpressionContext();
		
		final Date startDate = ctx.getStartDate();
		final Date endDate = ctx.getEndDate();
		
		return ssRegimeType.accept(new SSRegimeTypeVisitor<QuoteCalculator>() {

			@Override
			public QuoteCalculator visitGeneralRegime(SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}

			@Override
			public QuoteCalculator visitAgriculturalRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}

			@Override
			public QuoteCalculator visitDomesticEmployeesRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}

			@Override
			public QuoteCalculator visitSelfEmployedRegime(
					SSRegimeType ssRegimeType) {
				return NonQuote.getInstance();
			}

			@Override
			public QuoteCalculator visitCoalMiningRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}

			@Override
			public QuoteCalculator visitSeaWorkersRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}

			@Override
			public QuoteCalculator visitStudentInsuranceRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}

			@Override
			public QuoteCalculator visitArtistRegime(SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext, startDate, endDate);
			}
		});
	}
	
	
	protected double getLimitedCgcBase(double rawCgcBase, ExpressionContext ctx , Date start, Date end) {
		String quoteGroup = ctx.getVariable(QUOTE_GROUP, start, end, String.class);
	
		Map<String, String> minLimits = 
			ctx.getVariable(CGC_BASE_MIN, start, end, Map.class);
		
		Double minLimit = getLimit(minLimits.get(quoteGroup), ctx, start, end);
		
		if ( minLimit != null && rawCgcBase < minLimit ) {
			return minLimit;
		}
	
		Map<String, String> maxLimits = 
			ctx.getVariable(CGC_BASE_MAX, start, end, Map.class);
	
		Double maxLimit = getLimit(maxLimits.get(quoteGroup), ctx, start, end);
		
		if ( maxLimit != null && rawCgcBase > maxLimit ) {
			return maxLimit;
		}
		
		return rawCgcBase;
	}

	protected double getLimitedCgpBase(double rawCgpBase, ExpressionContext ctx , Date start, Date end) 
	{
		String minExpression = ctx.getVariable(CGP_BASE_MIN, start, end, String.class);
		
		Double minLimit = getLimit(minExpression, ctx, start, end);
	
		if ( minLimit != null && rawCgpBase < minLimit ) {
			return minLimit;
		}
		
		String maxExpression = ctx.getVariable(CGP_BASE_MAX, start, end, String.class);
		
		Double maxLimit = getLimit(maxExpression, ctx, start, end);

		if ( maxLimit != null && rawCgpBase > maxLimit ) {
			return maxLimit;
		}
		
		return rawCgpBase;
	}

	private Double getLimit( String  expression, ExpressionContext expressionContext, Date start, Date end  ){
		
		if ( expression == null )
			return null;
		
		List<ITimedObject<Double>> results = null;
		try {
			results = expressionContext.eval(expression, start, end, Double.class );
		} catch (ExpressionException e) {
		}
		
		if ( results == null || results.size() == 0 ) {
			return null;
		}
		
		double total = 0.00;
		
		for (ITimedObject<Double> timedObject : results) {
			total += timedObject.getValue();
		};
		
		return total;
	}
	

}
