package com.esferalia.aon.payroll.calculator;

import java.util.Date;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.PaymentTypeVisitor;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ITimedObject;

public abstract class TaxCalculator {

	double irpfBase = 0 ;
	double renumeration = 0;
	double totalPayment = 0;

	

	public double getIrpfBase() {
		return irpfBase;
	}


	public double getRenumeration() {
		return renumeration;
	}


	public double getTotalPayment() {
		return totalPayment;
	}
		
	
	public abstract double tax(IContractPayment payment, Date start, Date end, 
			double amount ) throws AonException;

	private static class DefaultTaxCalculator extends TaxCalculator {
		
		private ExpressionContext context;
		
		public DefaultTaxCalculator(ExpressionContext context) {
			this.context = context;
		}
		
		private double getTax(IContractPayment payment, Date start, Date end, double amount ) 
		throws AonException 
		{
			String irpfExpr = payment.getIrpfExpression();
			if ( irpfExpr == null ) {
				return 0;
			}
			String name = payment.getName();
			if ( name != null && irpfExpr.equals(name)) {
				return amount;
			}
			
			List<ITimedObject<Double>> taxes;
			taxes = context.eval(irpfExpr, start, end, Double.class);
			double total = 0.00;
			for (ITimedObject<Double> quote : taxes) {
				total += quote.getValue();
			}
			return total;
		}

		@Override
		public double tax(IContractPayment contractPayment, Date start, Date end,
				final double amount) throws AonException {
			
			SalaryType salaryType = contractPayment.getSalaryType();
			if ( salaryType != SalaryType.SALARY ) {
				return 0.00;
			}
			
			DefaultTaxCalculator.this.renumeration += amount;
			
			final double  tax  = getTax(contractPayment, start, end, amount);
			
			
			PaymentType paymentType = contractPayment.getType();
			paymentType.accept(new PaymentTypeVisitor() {
				
				@Override
				public void visitStructuralHours(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitSpecialBonos(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitSocialSecurityBenefits(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitSalarySupplement(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitSalaryInKind(PaymentType paymentType) {
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitOtherNonWage(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitNonStructuralHours(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitMovingCompensation(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitCompensationExpense(PaymentType paymentType) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
				
				@Override
				public void visitBaseSalary(PaymentType type) {
					DefaultTaxCalculator.this.totalPayment += amount;
					DefaultTaxCalculator.this.irpfBase += tax;
				}
			});
//			Month month = contractPayment.getMonth();
//			
//			if ( contractPayment.getSalaryType() != SalaryType.SALARY
//					|| (  month != null && month != issueMonth ) ) {
//				continue;
//			}
//				
			
			final double  payment = amount;
			return payment;
		}
	}
	
	public static TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
		return new DefaultTaxCalculator(ctx.getExpressionContext());
	}

	protected static Month getMonth(Date date ) {
		if ( date == null )
			return null;
		int monthValue = CommonUtil.getMonth(date);
		return Month.getMonthByValue(monthValue);
	}
	

}
