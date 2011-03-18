package com.esferalia.aon.payroll.calculator;

import java.util.Date;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.code.aon.common.AonException;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.SSRegimeTypeVisitor;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;

public abstract class QuoteCalculator {
	
	
	
	protected double itBase = 0 ;
	protected double cgcBase = 0 ;
	protected double proExtBase = 0;
	protected double structuralBase = 0;
	protected double nonStructuralBase = 0;

	
	public double getItBase() {
		return itBase;
	}
	

	public double getCgcBase() {
		return cgcBase;
	}

	public double getCgpBase() {
		return cgcBase + structuralBase  +nonStructuralBase ;
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
		
		private static QuoteCalculator SINGLETON = new NonQuote();
		
		public static QuoteCalculator getInstance() {
			return SINGLETON;
		}
	}
	
	
	public static class GeneralQuote extends QuoteCalculator {
		
		private ExpressionContext context;
		
		public GeneralQuote(ExpressionContext context) {
			this.context = context;
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
					GeneralQuote.this.cgcBase += quote;
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
					GeneralQuote.this.cgcBase += quote;
				}
				
				@Override
				public void visitSocialSecurityBenefits(PaymentType paymentType) {
					GeneralQuote.this.cgcBase += quote;
				}
				
				@Override
				public void visitSalarySupplement(PaymentType paymentType) {
					GeneralQuote.this.cgcBase += quote;
				}
				
				@Override
				public void visitSalaryInKind(PaymentType paymentType) {
					GeneralQuote.this.cgcBase += quote;
				}
				
				@Override
				public void visitOtherNonWage(PaymentType paymentType) {
					GeneralQuote.this.cgcBase += quote;
				}
				
				
				@Override
				public void visitMovingCompensation(PaymentType paymentType) {
					GeneralQuote.this.cgcBase += quote;
				}
				
				@Override
				public void visitCompensationExpense(PaymentType paymentType) {
					GeneralQuote.this.cgcBase += quote;
				}
				
			});
			
			SalaryType salaryType = payment.getSalaryType();
		}
		
	}
	
	public static QuoteCalculator getQuoteCalculator(IContractSalaryCalculatorContext ctx) {
		
		SSRegimeType ssRegimeType = ctx.getSSRegime();
		
		final ExpressionContext expressionContext = 
			ctx.getExpressionContext();
		
		return ssRegimeType.accept(new SSRegimeTypeVisitor<QuoteCalculator>() {

			@Override
			public QuoteCalculator visitGeneralRegime(SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}

			@Override
			public QuoteCalculator visitAgriculturalRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}

			@Override
			public QuoteCalculator visitDomesticEmployeesRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}

			@Override
			public QuoteCalculator visitSelfEmployedRegime(
					SSRegimeType ssRegimeType) {
				return NonQuote.getInstance();
			}

			@Override
			public QuoteCalculator visitCoalMiningRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}

			@Override
			public QuoteCalculator visitSeaWorkersRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}

			@Override
			public QuoteCalculator visitStudentInsuranceRegime(
					SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}

			@Override
			public QuoteCalculator visitArtistRegime(SSRegimeType ssRegimeType) {
				return new GeneralQuote(expressionContext);
			}
		});
	}
	
	


}
